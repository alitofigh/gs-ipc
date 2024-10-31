package org.gsstation.novin.core.exception;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public class TransactionProcessingTimeoutException extends GsException {
    public static final String TRANSACTION_PROCESSING_TIMEOUT_MESSAGE =
            "Transaction processing could not be completed in the specified "
                    + "time frame; transaction processing time: %s, "
                    + "configured timeout: %s";

    public TransactionProcessingTimeoutException() {
    }

    public TransactionProcessingTimeoutException(String message) {
        super(message);
    }

    public TransactionProcessingTimeoutException(Throwable cause) {
        super(cause);
    }

    public TransactionProcessingTimeoutException(
            String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionProcessingTimeoutException(
            String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TransactionProcessingTimeoutException(
            String message, String errorCode) {
        super(message, errorCode);
    }

    public TransactionProcessingTimeoutException(
            String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public TransactionProcessingTimeoutException(
            String message, String errorCode, String localMessage) {
        super(message, errorCode, localMessage);
    }

    public TransactionProcessingTimeoutException(
            String message, Throwable cause,
            String errorCode, String localMessage) {
        super(message, cause, errorCode, localMessage);
    }
}
