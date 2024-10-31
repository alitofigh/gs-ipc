package org.gsstation.novin.core.exception;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public class InvalidProtocolItemException extends GsRuntimeException {
    public static final String INVALID_PROTOCOL_ITEM_MESSAGE = "%s";

    public InvalidProtocolItemException() {
    }

    public InvalidProtocolItemException(String message) {
        super(message);
    }

    public InvalidProtocolItemException(Throwable cause) {
        super(cause);
    }

    public InvalidProtocolItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidProtocolItemException(
            String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidProtocolItemException(String message, String errorCode) {
        super(message, errorCode);
    }

    public InvalidProtocolItemException(
            String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public InvalidProtocolItemException(
            String message, String errorCode, String localMessage) {
        super(message, errorCode, localMessage);
    }

    public InvalidProtocolItemException(
            String message, Throwable cause,
            String errorCode, String localMessage) {
        super(message, cause, errorCode, localMessage);
    }
}
