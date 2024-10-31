package org.gsstation.novin.core.exception;

/**
 * Created by A_Tofigh at 08/03/2024
 */
public class InvalidMacException extends GsRuntimeException {
    public static final String DIFFERENT_MAC =
            "The message mac differs from calculated mac; message mac: %s, "
                    + "calculated mac; %s";
    public static final String DIFFERENT_MAC_WITH_HANDLER =
            "The message mac differs from calculated mac; message mac: %s, "
                    + "calculated mac: %s, security handler: %s";

    public InvalidMacException() {
    }

    public InvalidMacException(String message) {
        super(message);
    }

    public InvalidMacException(Throwable cause) {
        super(cause);
    }

    public InvalidMacException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMacException(
            String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidMacException(String message, String errorCode) {
        super(message, errorCode);
    }

    public InvalidMacException(
            String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public InvalidMacException(
            String message, String errorCode, String localMessage) {
        super(message, errorCode, localMessage);
    }

    public InvalidMacException(
            String message, Throwable cause,
            String errorCode, String localMessage) {
        super(message, cause, errorCode, localMessage);
    }
}
