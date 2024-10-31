package org.gsstation.novin.core.exception;

/**
 * Created by A_Tofigh at 07/19/2024
 */
public class GeneralDatabaseException extends GsRuntimeException {
    public GeneralDatabaseException() {
    }

    public GeneralDatabaseException(String message) {
        super(message);
    }

    public GeneralDatabaseException(Throwable cause) {
        super(cause);
    }

    public GeneralDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralDatabaseException(
            String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GeneralDatabaseException(String message, String errorCode) {
        super(message, errorCode);
    }

    public GeneralDatabaseException(
            String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public GeneralDatabaseException(
            String message, String errorCode, String localMessage) {
        super(message, errorCode, localMessage);
    }

    public GeneralDatabaseException(
            String message, Throwable cause,
            String errorCode, String localMessage) {
        super(message, cause, errorCode, localMessage);
    }
}
