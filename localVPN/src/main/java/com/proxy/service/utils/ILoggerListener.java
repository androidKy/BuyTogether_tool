package com.proxy.service.utils;

/**
 * 接口
 * logger 日志监听接口
 *
 * @author 枕套
 * @date 2018/4/28
 */
public interface ILoggerListener {
    /**
     * 普通日志输出
     *
     * @param method 方法名
     * @param object 内容
     */
    void log(String method, Object object);


    /**
     * 警告日志输出
     *
     * @param method     方法名
     * @param warningMsg 内容
     */
    void warn(String method, String warningMsg);

    /**
     * 异常捕获日志输出
     *
     * @param throwable 捕获异常
     * @param method    发生异常的方法名
     */
    void error(Throwable throwable, String method);
}
