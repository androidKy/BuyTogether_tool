package com.proxy.service

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Handler
import android.os.ParcelFileDescriptor
import com.proxy.service.core.*
import com.proxy.service.dns.DnsPacket
import com.proxy.service.dns.DnsProxy
import com.proxy.service.header.CommonMethods
import com.proxy.service.header.IPHeader
import com.proxy.service.header.TCPHeader
import com.proxy.service.header.UDPHeader
import com.proxy.service.tcp.TcpProxyServer
import com.safframework.log.L
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Description:
 * Created by Quinin on 2019-07-18.
 **/
class LocalVpnService : VpnService(), Runnable {

    companion object {
        var ProxyUrl: String? = ""
        val mOnStatusChangedListeners = ConcurrentHashMap<onStatusChangedListener, Any>()
        lateinit var mInstance: LocalVpnService
        const val START_VPN_SERVICE_REQUEST_CODE = 2019
        var mWeakReference: WeakReference<Activity>? = null

        fun prepareVPN(activity: Activity): Intent? {
            mWeakReference = WeakReference(activity)
            return prepare(activity)
        }


        fun addOnStatusChangedListener(listener: onStatusChangedListener) {
            if (!mOnStatusChangedListeners.containsKey(listener)) {
                mOnStatusChangedListeners[listener] = 1
            }
        }

        fun removeOnStatusChangedListener(listener: onStatusChangedListener) {
            if (mOnStatusChangedListeners.containsKey(listener)) {
                mOnStatusChangedListeners.remove(listener)
            }
        }
    }

    var ProxyUrl: String? = null
    var IsRunning = false

    private var mId: Int = 0
    private var LOCAL_IP: Int = 0


    private var mVPNThread: Thread? = null
    private var mVPNInterface: ParcelFileDescriptor? = null
    private var mTcpProxyServer: TcpProxyServer? = null
    private var mDnsProxy: DnsProxy? = null
    private var mVPNOutputStream: FileOutputStream? = null

    private var mPacket: ByteArray
    private var mIPHeader: IPHeader
    private var mTCPHeader: TCPHeader
    private var mUDPHeader: UDPHeader
    private var mDNSBuffer: ByteBuffer
    private var mHandler: Handler
    private var mSentBytes: Long = 0
    private var mReceivedBytes: Long = 0

    init {

        mId++
        mHandler = Handler()
        mPacket = ByteArray(20000)
        mIPHeader = IPHeader(mPacket, 0)
        mTCPHeader = TCPHeader(mPacket, 20)
        mUDPHeader = UDPHeader(mPacket, 20)
        mDNSBuffer = (ByteBuffer.wrap(mPacket).position(28) as ByteBuffer).slice()
    }

    override fun onCreate() {
        mInstance = this
        L.i("VPNService created $mId")
        // Start a new session by creating a new thread.
        mVPNThread = Thread(this, "VPNServiceThread")
        mVPNThread!!.start()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        IsRunning = true
        return super.onStartCommand(intent, flags, startId)
    }

    interface onStatusChangedListener {
        fun onStatusChanged(status: String, isRunning: Boolean?)

        fun onLogReceived(logString: String)
    }


    private fun onStatusChanged(status: String, isRunning: Boolean) {
        mHandler.post {
            for ((key) in mOnStatusChangedListeners) {
                key.onStatusChanged(status, isRunning)
            }
        }
    }

    fun writeLog(format: String, vararg args: Any) {
        val logString = String.format(format, *args)
        mHandler.post {
            for ((key) in mOnStatusChangedListeners) {
                key.onLogReceived(logString)
            }
        }
    }

    fun sendUDPPacket(ipHeader: IPHeader, udpHeader: UDPHeader) {
        try {
            CommonMethods.ComputeUDPChecksum(ipHeader, udpHeader)
            this.mVPNOutputStream!!.write(ipHeader.m_Data, ipHeader.m_Offset, ipHeader.getTotalLength())
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    internal fun getAppInstallID(): String {
        val preferences = getSharedPreferences("SmartProxy", Context.MODE_PRIVATE)
        var appInstallID = preferences.getString("AppInstallID", null)
        if (appInstallID == null || appInstallID.isEmpty()) {
            appInstallID = UUID.randomUUID().toString()
            val editor = preferences.edit()
            editor.putString("AppInstallID", appInstallID)
            editor.apply()
        }
        return appInstallID
    }

    internal fun getVersionName(): String {
        try {
            val packageManager = packageManager
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            val packInfo = packageManager.getPackageInfo(packageName, 0)
            return packInfo.versionName
        } catch (e: Exception) {
            return "0.0"
        }

    }

    @Synchronized
    override fun run() = try {
        L.i("VPNService work thread is runing... ${mId.toString()}")

        ProxyConfig.AppInstallID = getAppInstallID()//获取安装ID
        ProxyConfig.AppVersion = getVersionName()//获取版本号
        L.i("AppInstallID: ${ProxyConfig.AppInstallID}")
        writeLog("Android version: ${Build.VERSION.RELEASE}")
        writeLog("App version: ${ProxyConfig.AppVersion}")


        ChinaIpMaskManager.loadFromFile(resources.openRawResource(R.raw.ipmask))//加载中国的IP段，用于IP分流。
        waitUntilPreapred()//检查是否准备完毕。

        writeLog("Load config from file ...")
        try {
            ProxyConfig.Instance.loadFromFile(resources.openRawResource(R.raw.config))
            writeLog("Load done")
        } catch (e: Exception) {
            var errString: String? = e.message
            if (errString == null || errString.isEmpty()) {
                errString = e.toString()
            }
            writeLog("Load failed with error: %s", errString)
        }

        mTcpProxyServer = TcpProxyServer(0)
        mTcpProxyServer!!.start()
        writeLog("LocalTcpServer started.")

        mDnsProxy = DnsProxy()
        mDnsProxy!!.start()
        writeLog("LocalDnsProxy started.")

        while (true) {
            if (IsRunning) {
                //加载配置文件
                writeLog("set shadowsocks/(http proxy)")
                try {
                    ProxyConfig.Instance.m_ProxyList.clear()
                    ProxyConfig.Instance.addProxyToList(ProxyUrl)
                    writeLog("Proxy is: %s", ProxyConfig.Instance.defaultProxy)
                } catch (e: Exception) {
                    var errString: String? = e.message
                    if (errString == null || errString.isEmpty()) {
                        errString = e.toString()
                    }
                    IsRunning = false
                    onStatusChanged(errString, false)
                    continue
                }

                val welcomeInfoString = ProxyConfig.Instance.welcomeInfo
                if (welcomeInfoString != null && welcomeInfoString.isNotEmpty()) {
                    writeLog("%s", ProxyConfig.Instance.welcomeInfo)
                }
                writeLog("Global mode is " + if (ProxyConfig.Instance.globalMode) "on" else "off")

                runVPN()
            } else {
                Thread.sleep(100)
            }
        }
    } catch (e: InterruptedException) {
        L.e(e.message, e)
    } catch (e: Exception) {
        L.e(e.message, e)
        writeLog("Fatal error: %s", e.toString())
    } finally {
        writeLog("App terminated.")
        dispose()
    }

    @Throws(Exception::class)
    private fun runVPN() {
        this.mVPNInterface = establishVPN()
        this.mVPNOutputStream = FileOutputStream(mVPNInterface!!.fileDescriptor)
        val fileInputStream = FileInputStream(mVPNInterface!!.fileDescriptor)
        var size = fileInputStream.read(mPacket)
        while (size != -1 && IsRunning) {
            while (size > 0 && IsRunning) {
                size = fileInputStream.read(mPacket)
                if (mDnsProxy!!.Stopped || mTcpProxyServer!!.Stopped) {
                    fileInputStream.close()
                    throw Exception("LocalServer stopped.")
                }
                onIPPacketReceived(mIPHeader, size)
            }
            Thread.sleep(20)
        }
        fileInputStream.close()
        disconnectVPN()
    }

    @Throws(IOException::class)
    internal fun onIPPacketReceived(ipHeader: IPHeader, size: Int) {
        when (ipHeader.getProtocol()) {
            IPHeader.TCP -> {
                val tcpHeader = mTCPHeader
                tcpHeader.m_Offset = ipHeader.getHeaderLength()
                if (ipHeader.sourceIP == LOCAL_IP) {
                    if (tcpHeader.sourcePort == mTcpProxyServer?.Port) {// 收到本地TCP服务器数据
                        val session = NatSessionManager.getSession(tcpHeader.getDestinationPort().toInt())
                        if (session != null) {
                            ipHeader.sourceIP = ipHeader.getDestinationIP()
                            tcpHeader.sourcePort = session.RemotePort
                            ipHeader.destinationIP = LOCAL_IP

                            CommonMethods.ComputeTCPChecksum(ipHeader, tcpHeader)
                            mVPNOutputStream!!.write(ipHeader.m_Data, ipHeader.m_Offset, size)
                            mReceivedBytes += size.toLong()
                        } else {
                            L.i("NoSession: ipHeader: ${ipHeader.toString()} \n tcpHeader: ${tcpHeader.toString()}")
                        }
                    } else {
                        // 添加端口映射
                        val portKey = tcpHeader.sourcePort
                        var session = NatSessionManager.getSession(portKey.toInt())
                        if (session == null || session?.RemoteIP != ipHeader?.destinationIP || session?.RemotePort != tcpHeader.destinationPort) {
                            session = NatSessionManager.createSession(
                                portKey.toInt(),
                                ipHeader.destinationIP,
                                tcpHeader.destinationPort
                            )
                        }

                        session?.LastNanoTime = System.nanoTime()
                        if (session != null)
                            session.PacketSent++ //注意顺序

                        val tcpDataSize = ipHeader.dataLength - tcpHeader.headerLength
                        if (session?.PacketSent == 2 && tcpDataSize == 0) {
                            return //丢弃tcp握手的第二个ACK报文。因为客户端发数据的时候也会带上ACK，这样可以在服务器Accept之前分析出HOST信息。
                        }

                        //分析数据，找到host
                        if (session?.BytesSent == 0 && tcpDataSize > 10) {
                            val dataOffset = tcpHeader.m_Offset + tcpHeader.headerLength
                            val host = HttpHostHeaderParser.parseHost(tcpHeader.m_Data, dataOffset, tcpDataSize)
                            if (host != null) {
                                session.RemoteHost = host
                            } else {
                                L.i("No host name found: ${session.RemoteHost}")
                            }
                        }

                        // 转发给本地TCP服务器
                        ipHeader.sourceIP = ipHeader.getDestinationIP()
                        ipHeader.destinationIP = LOCAL_IP
                        tcpHeader.destinationPort = mTcpProxyServer!!.Port

                        CommonMethods.ComputeTCPChecksum(ipHeader, tcpHeader)
                        mVPNOutputStream!!.write(ipHeader.m_Data, ipHeader.m_Offset, size)
                        session!!.BytesSent += tcpDataSize//注意顺序
                        mSentBytes += size.toLong()
                    }
                }
            }
            IPHeader.UDP -> {
                // 转发DNS数据包：
                val udpHeader = mUDPHeader
                udpHeader.m_Offset = ipHeader.getHeaderLength()
                if (ipHeader.sourceIP == LOCAL_IP && udpHeader.destinationPort.toInt() == 53) {
                    mDNSBuffer.clear()
                    mDNSBuffer.limit(ipHeader.getDataLength() - 8)
                    val dnsPacket = DnsPacket.FromBytes(mDNSBuffer)
                    if (dnsPacket != null && dnsPacket.Header.QuestionCount > 0) {
                        mDnsProxy!!.onDnsRequestReceived(ipHeader, udpHeader, dnsPacket)
                    }
                }
            }
        }
    }

    private fun waitUntilPreapred() {
        while (VpnService.prepare(this) != null) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    @Throws(Exception::class)
    private fun establishVPN(): ParcelFileDescriptor {
        val builder = Builder()
        builder.setMtu(ProxyConfig.Instance.mtu)
        L.i("setMtu: ${ProxyConfig.Instance.mtu}")

        val ipAddress = ProxyConfig.Instance.getDefaultLocalIP()
        LOCAL_IP = CommonMethods.ipStringToInt(ipAddress.Address)
        L.i("Address: ${ipAddress.Address} , LOCAL_IP: ${CommonMethods.ipIntToString(LOCAL_IP)}")
        builder.addAddress(ipAddress.Address, ipAddress.PrefixLength)
        L.i("addAddress: ${ipAddress.Address} length = ${ipAddress.PrefixLength}")

        for (dns in ProxyConfig.Instance.getDnsList()) {
            builder.addDnsServer(dns.Address)
            L.i("addDnsServer: %s\n", dns.Address)
        }

        if (ProxyConfig.Instance.routeList.size > 0) {
            for (routeAddress in ProxyConfig.Instance.getRouteList()) {
                builder.addRoute(routeAddress.Address, routeAddress.PrefixLength)
                L.i("addRoute: ${routeAddress.Address}, length = ${routeAddress.PrefixLength}")
            }
            builder.addRoute(CommonMethods.ipIntToString(ProxyConfig.FAKE_NETWORK_IP), 16)

            L.i(
                "addRoute for FAKE_NETWORK_IP: ${CommonMethods.ipIntToString(ProxyConfig.FAKE_NETWORK_IP)}"
            )
        } else {
            builder.addRoute("0.0.0.0", 0)
            L.i("addDefaultRoute: 0.0.0.0/0")
        }

        val systemProperties = Class.forName("android.os.SystemProperties")
        val method = systemProperties.getMethod("get", *arrayOf<Class<*>>(String::class.java))
        val servers = ArrayList<String>()
        for (name in arrayOf("net.dns1", "net.dns2", "net.dns3", "net.dns4")) {
            val value = method.invoke(null, name) as String
            if ("" != value && !servers.contains(value)) {
                servers.add(value)
                if (value.replace("\\d".toRegex(), "").length == 3) {//防止IPv6地址导致问题
                    builder.addRoute(value, 32)
                } else {
                    builder.addRoute(value, 128)
                }
                L.i("dnsName: $name value: $value")
            }
        }

        if (AppProxyManager.isLollipopOrAbove) {
            writeLog("Proxy App Info:" + AppProxyManager.Instance.proxyAppInfo)
            if (AppProxyManager.Instance.proxyAppInfo.size == 0) {
                writeLog("Proxy All Apps")
            }
            for (app in AppProxyManager.Instance.proxyAppInfo) {
                //                builder.addAllowedApplication("com.vm.shadowsocks");//需要把自己加入代理，不然会无法进行网络链接
                //                builder.addAllowedApplication("com.bull.vpn");//需要把自己加入代理，不然会无法进行网络链接
                builder.addAllowedApplication("com.net.request")//需要把自己加入代理，不然会无法进行网络链接
                try {
                    builder.addAllowedApplication(app.getPkgName())
                    //                    writeLog("Proxy App: " + app.getAppLabel() + " , PackName:" + app.getPkgName());
                } catch (e: Exception) {
                    e.printStackTrace()
                    //                    writeLog("Proxy App Fail: " + app.getAppLabel());
                }

            }
        } else {
            writeLog("No Pre-App proxy, due to low Android version.")
        }
        val activityClass = mWeakReference?.get()?.javaClass
        L.i("activity class: ${activityClass?.simpleName}")
        val intent = Intent(this, activityClass)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        builder.setConfigureIntent(pendingIntent)

        builder.setSession(ProxyConfig.Instance.sessionName)
        val pfdDescriptor = builder.establish()
        onStatusChanged(ProxyConfig.Instance.sessionName + "已连接", true)
        return pfdDescriptor
    }

    fun disconnectVPN() {
        try {
            if (mVPNInterface != null) {
                mVPNInterface!!.close()
                mVPNInterface = null
            }
        } catch (e: Exception) {
            // ignore
        }

        onStatusChanged(ProxyConfig.Instance.sessionName + "未连接", false)
        this.mVPNOutputStream = null
    }

    @Synchronized
    private fun dispose() {
        // 断开VPN
        disconnectVPN()

        // 停止TcpServer
        if (mTcpProxyServer != null) {
            mTcpProxyServer!!.stop()
            mTcpProxyServer = null
            writeLog("LocalTcpServer stopped.")
        }

        // 停止DNS解析器
        if (mDnsProxy != null) {
            mDnsProxy!!.stop()
            mDnsProxy = null
            writeLog("LocalDnsProxy stopped.")
        }

        stopSelf()
        IsRunning = false
        System.exit(0)
    }

    override fun onDestroy() {
        L.i("VPNService $mId was destroy.")
        if (mVPNThread != null) {
            mVPNThread!!.interrupt()
        }

        mWeakReference?.clear()
    }
}