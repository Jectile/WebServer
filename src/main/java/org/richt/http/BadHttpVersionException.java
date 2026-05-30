package org.richt.http;

public class BadHttpVersionException extends Exception {
    public BadHttpVersionException(Throwable cause) {
        super(cause);
    }

    public BadHttpVersionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BadHttpVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadHttpVersionException(String message) {
        super(message);
    }

    public BadHttpVersionException() {
    }
}
