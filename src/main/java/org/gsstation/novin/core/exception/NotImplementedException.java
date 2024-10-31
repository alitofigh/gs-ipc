package org.gsstation.novin.core.exception;

/**
 * Created by A_Tofigh at 07/19/2024
 */
public class NotImplementedException extends GsRuntimeException {
    public static final String NO_ROUTER_EXISTS_FOR_FARAZ_HOST_MESSAGE =
            "No router is configured to send transactions directly to";
    public static final String NO_ROUTER_EXISTS_FOR_SIMIA_HOST_MESSAGE =
            "No router is configured to send transactions directly to";

    public NotImplementedException() {
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplementedException(
            String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NotImplementedException(String message, String errorCode) {
        super(message, errorCode);
    }

    public NotImplementedException(
            String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public NotImplementedException(
            String message, String errorCode, String localMessage) {
        super(message, errorCode, localMessage);
    }

    public NotImplementedException(
            String message, Throwable cause,
            String errorCode, String localMessage) {
        super(message, cause, errorCode, localMessage);
    }
}
