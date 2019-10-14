package com.ppdai.das.core.log;

public interface ILogger {
    void logEvent(String type, String name, String message);

    void logTransaction(String type, String name, String message, Callback callback);
}
