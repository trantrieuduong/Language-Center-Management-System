package com.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static void register() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
        System.setProperty("sun.awt.exception.handler", GlobalExceptionHandler.class.getName());
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        log.error("Uncaught exception on thread [{}]", thread.getName(), throwable);
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                "Lỗi hệ thống không xác định: " + throwable.getMessage()
                        + "\nVui lòng xem log để biết chi tiết.",
                "Lỗi hệ thống",
                JOptionPane.ERROR_MESSAGE));
    }

    public void handle(Throwable throwable) {
        uncaughtException(Thread.currentThread(), throwable);
    }
}
