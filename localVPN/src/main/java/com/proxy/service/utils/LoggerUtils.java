package com.proxy.service.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 工具类
 * Logger 日志输出
 *
 * @author 枕套
 * @date 2018/4/28
 */
public class LoggerUtils {
    private static LoggerUtils instance;
    private Logger mLogger;
    private String tag = "--LoggerUtils--";

    private static final int JSON_INDENT = 2;

    private static final String TOP_LEFT_CORNER = "┌";
    private static final String BOTTOM_LEFT_CORNER = "└";
    private static final String MIDDLE_CORNER = "├";
    private static final String HORIZONTAL_LINE = "│";
    private static final String DOUBLE_DIVIDER = "───────────────────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";

    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    public LoggerUtils() {
        if (mLogger == null) {
            mLogger = Logger.getLogger(LoggerUtils.class.getSimpleName());
        }
    }

    public synchronized static LoggerUtils getInstance() {
        if (instance == null) {
            instance = new LoggerUtils();
        }
        return instance;
    }

    /**
     * 将时间戳转换为时间
     */
    private String stampToDate(Long timeMillis) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

    public void setDebug(boolean isDebug) {
        if (mLogger != null) {
            if (isDebug) {
                mLogger.setLevel(Level.ALL);
            } else {
                mLogger.setLevel(Level.OFF);
            }
        }
    }

    /**
     * 设置本地缓存，生成本地日志文件
     *
     * @param filePrefix 文件前缀
     * @param level      输出的级别 可为NULL（表示不输出日志文件）
     * @Desc  生成目录在根目录下的Logger文件夹下的对应的包名目录下
     */
    public void setLocalCache(Context context, String filePrefix, @Nullable Level level) {
        try {
            String filePath = Environment.getExternalStorageDirectory().getPath() + "/Logger";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            filePath = filePath + "/" + context.getPackageName();
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            filePath = filePath + "/" + filePrefix + "_log-" + stampToDate(System.currentTimeMillis()) + ".txt";


            FileHandler fileHandler = new FileHandler(filePath, true);
            if (level == null) {
                fileHandler.setLevel(Level.OFF);
            } else {
                fileHandler.setLevel(level);
            }
            fileHandler.setFormatter(new SimpleFormatter());
            mLogger.addHandler(fileHandler);
        } catch (IOException e) {
            error(e, "setLocalCache");
        }
    }

    public LoggerUtils setTag(String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * 普通日志输出
     *
     * @param errorMsg 内容
     */
    public void log(String errorMsg) {
        if (mLogger != null) {
            errorMsg = printMessage(null, toString(errorMsg));
            mLogger.info(errorMsg);
        }
    }

    /**
     * 普通日志输出
     *
     * @param method 方法名
     * @param object 内容
     */
    public void log(String method, Object object) {
        if (mLogger != null) {
            String errorMsg = printMessage(method, toString(object));
            mLogger.info(errorMsg);
        }
    }

    /**
     * 普通日志输出
     *
     * @param method   方法名
     * @param errorMsg 内容
     */
    public void log(String method, String errorMsg) {
        if (mLogger != null) {
            errorMsg = printMessage(method, toString(errorMsg));
            mLogger.info(errorMsg);
        }
    }

    /**
     * 警告日志输出
     *
     * @param method     方法名
     * @param warningMsg 内容
     */
    public void warn(String method, String warningMsg) {
        if (mLogger != null) {
            warningMsg = printMessage(method, toString(warningMsg));
            mLogger.warning(warningMsg);
        }
    }

    /**
     * 异常捕获日志输出
     *
     * @param throwable 捕获异常
     * @param method    发生异常的方法名
     */
    public void error(Throwable throwable, String method) {
        if (mLogger != null) {
            String errorMsg = printMessage(method, getStackTraceString(throwable));
            mLogger.warning(errorMsg);
        }
    }


    private String printMessage(String method, String msg) {
        StringBuffer buffer = new StringBuffer();
        try {
            buffer.append(" " + TOP_BORDER)
                    .append("\n" + MIDDLE_CORNER + "\u3000" + tag);

            if (method != null && !TextUtils.isEmpty(method)) {
                buffer.append("\n" + MIDDLE_CORNER + "\u3000method:" + method);
            }

            if (msg != null && !TextUtils.isEmpty(msg)) {

                buffer.append("\n")
                        .append(MIDDLE_BORDER)
                        .append("\n");

                msg = msg.trim();
                if (msg.startsWith("{") && msg.endsWith("}")) {
                    JSONObject jsonObject = new JSONObject(msg);
                    msg = jsonObject.toString(JSON_INDENT);

                    String[] msgSpilts = msg.split("\n");
                    for (int i = 0; i < msgSpilts.length; i++) {
                        buffer.append(HORIZONTAL_LINE + "\u3000" + msgSpilts[i] + "\n");
                    }
                } else if (msg.startsWith("[") && msg.endsWith("]")) {
                    JSONArray jsonArray = new JSONArray(msg);
                    msg = jsonArray.toString(JSON_INDENT);

                    String[] msgSpilts = msg.split("\n");
                    for (int i = 0; i < msgSpilts.length; i++) {
                        buffer.append(HORIZONTAL_LINE + "\u3000" + msgSpilts[i] + "\n");
                    }
                } else if (msg.contains("\n")) {
                    String[] msgSpilts = msg.split("\n");
                    for (int i = 0; i < msgSpilts.length; i++) {
                        buffer.append(HORIZONTAL_LINE + "\u3000" + msgSpilts[i] + "\n");
                    }
                } else {
                    buffer.append(HORIZONTAL_LINE + "\u3000" + msg + "\n");
                }
            } else {
                buffer.append("\n");
            }
            buffer.append(BOTTOM_BORDER);
        } catch (Exception e) {
            error(e, "printMessage");
        }
        return buffer.toString();
    }

    private String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private String toString(Object object) {
        if (object == null) {
            return "null";
        }
        if (!object.getClass().isArray()) {
            return object.toString();
        }
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        }
        if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        }
        if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        }
        return "Couldn't find a correct type for the object";
    }
}