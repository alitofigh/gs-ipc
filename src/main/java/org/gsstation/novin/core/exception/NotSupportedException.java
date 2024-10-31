package org.gsstation.novin.core.exception;

/**
 * Created by A_Tofigh at 07/16/2024
 */
public class NotSupportedException extends GsRuntimeException {
    public NotSupportedException() {
    }

    public NotSupportedException(String message) {
        super(message);
    }

    public NotSupportedException(Throwable cause) {
        super(cause);
    }

    public NotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportedException(
            String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NotSupportedException(String message, String errorCode) {
        super(message, errorCode);
    }

    public NotSupportedException(
            String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public NotSupportedException(
            String message, String errorCode, String localMessage) {
        super(message, errorCode, localMessage);
    }

    public NotSupportedException(
            String message, Throwable cause,
            String errorCode, String localMessage) {
        super(message, cause, errorCode, localMessage);
    }
}
