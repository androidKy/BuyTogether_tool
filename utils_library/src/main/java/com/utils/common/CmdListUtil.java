package com.utils.common;

import java.io.*;

/**
 * Description:执行一连串CMD命令
 * Created by Quinin on 2019-08-10.
 **/
public class CmdListUtil {
    private final String COMMAND_EXIT = "exit\n";
    private final String COMMAND_LINE_END = "\n";

    private static CmdListUtil mInstance;
    //private Process mProcess;

    private CmdListUtil() {

    }

    public static CmdListUtil getInstance() {
        if (mInstance == null) {
            synchronized (CmdListUtil.class) {
                if (mInstance == null) {
                    mInstance = new CmdListUtil();
                }
            }
        }

        return mInstance;
    }

    //静默安装
   /* public boolean installSlient(String path) {
        String cmd = "pm install -r " + path;
        return execCmd(cmd).contains("Success");
    }

    public boolean uninstallSlient(String packageName) {
        String cmd = "pm uninstall " + packageName;
        return execCmd(cmd).contains("Success");
    }
*/
    public String execCmd(String command) {
        if (ThreadUtils.isMainThread()) {
            //return "cmd executed can't run in mainThread.";
            throw new IllegalStateException("cmd executed can't run in mainThread.");
        }

        DataOutputStream os = null;
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder("su");
            pb.redirectErrorStream(true);
            process = pb.start();
            //process = Runtime.getRuntime().exec("su");

            os = new DataOutputStream(process.getOutputStream());
            os.write(command.getBytes());
            os.writeBytes(COMMAND_LINE_END);
            os.flush();

            os.writeBytes(COMMAND_EXIT);
            os.flush();

            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
                //System.out.println(line);
            }
            //int exitCode = process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            asyncProcessDestroy(process);
        }
//        L.i("Adb执行命令结果：" + result.toString());
        return result.toString();
    }

    public void executeCMD(String cmd) {
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            java.lang.Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 通过Android底层实现进程关闭
     *
     * @param process
     */
    public void killProcess(Process process) {
        int pid = getProcessId(process.toString());
        if (pid != 0) {
            try {
                android.os.Process.killProcess(pid);
            } catch (Exception e) {
                try {
                    process.destroy();
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * 获取当前进程的ID
     *
     * @param str
     * @return
     */
    public int getProcessId(String str) {
        try {
            int i = str.indexOf("=") + 1;
            int j = str.indexOf("]");
            String cStr = str.substring(i, j).trim();
            return Integer.parseInt(cStr);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 关闭进程的所有流
     *
     * @param process
     */
    public void closeAllStream(Process process) {
        try {
            InputStream in = process.getInputStream();
            if (in != null)
                in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            InputStream in = process.getErrorStream();
            if (in != null)
                in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            OutputStream out = process.getOutputStream();
            if (out != null)
                out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁一个进程
     *
     * @param process
     */
    public void processDestroy(Process process) {
        if (process != null) {
            try {
                if (process.exitValue() != 0) {
                    closeAllStream(process);
                    killProcess(process);
                }
            } catch (IllegalThreadStateException e) {
                closeAllStream(process);
                killProcess(process);
            }
        }
    }


    /**
     * 通过线程进行异步销毁
     */
    private void asyncProcessDestroy(final Process process) {
       // L.i("通过线程进行异步销毁-进行CMD命令的进程");
        //processDestroy(process);
      /*  ThreadUtils.executeByCached(new ThreadUtils.Task<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                processDestroy(process);
                return false;
            }

            @Override
            public void onSuccess(Boolean result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });*/
        Thread thread = new Thread("Daemon") {
            @Override
            public void run() {
                processDestroy(process);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
