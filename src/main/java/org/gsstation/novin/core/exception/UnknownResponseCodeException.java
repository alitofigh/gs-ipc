package org.gsstation.novin.core.exception;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public class UnknownResponseCodeException extends GsRuntimeException {
    public static final String UNDEFINED_ACTION_CODE_MESSAGE =
            "The given action code does not identify any defined "
                    + "action codes in the system; invalid action code: %s";

    public UnknownResponseCodeException() {
    }

    public UnknownResponseCodeException(String message) {
        super(message);
    }

    public UnknownResponseCodeException(Throwable cause) {
        super(cause);
    }

    public UnknownResponseCodeException(
            String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownResponseCodeException(
            String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnknownResponseCodeException(
            String message, String errorCode) {
        super(message, errorCode);
    }

    public UnknownResponseCodeException(
            String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public UnknownResponseCodeException(
            String message, String errorCode, String localMessage) {
        super(message, errorCode, localMessage);
    }

    public UnknownResponseCodeException(
            String message, Throwable cause,
            String errorCode, String localMessage) {
        super(message, cause, errorCode, localMessage);
    }
}
