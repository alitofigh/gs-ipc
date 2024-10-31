package org.gsstation.novin.core.exception;

import org.gsstation.novin.core.common.ProtocolItem;

import java.util.List;

/**
 * Created by A_Tofigh at 07/16/2024
 */
public class ValidationException extends GsRuntimeException {
    public static final String INVALID_INPUT_MESSAGE =
            "The given input does not satisfy expected or standard "
                    + "requirements; invalid input: %s, invalid value: %s";
    public static final String NEGATIVE_VALUE_MESSAGE =
            "It is not acceptable for the given value to be negative; "
                    + "invalid input: %s, invalid value: %s";
    public static final String EXACT_LENGTH_EXPECTED_MESSAGE =
            "The given value does not have the expected (exact) length; "
                    + "invalid input: %s, invalid length: %s, "
                    + "expected (exact) length: %s";
    public static final String LENGTH_BELOW_LIMIT_MESSAGE =
            "The given value length is lesser than minimum allowed limit; "
                    + "invalid input: %s, invalid length: %s, "
                    + "minimum allowed length: %s";
    public static final String LENGTH_ABOVE_LIMIT_MESSAGE =
            "The given value length exceeds maximum allowed limit; "
                    + "invalid input: %s, invalid length: %s, "
                    + "maximum allowed length: %s";
    public static final String NULL_VALUE_ENCOUNTERED_MESSAGE =
            "Null cannot be accepted as the value for input; "
                    + "invalid input: %s, invalid value: %s";
    public static final String VALUE_TOO_LARGE_MESSAGE =
            "The given value exceeds maximum defined limit; "
                    + "invalid input: %s, invalid value: %s";
    public static final String VALUE_TOO_SMALL_MESSAGE =
            "The given value is under minimum defined limit; "
                    + "invalid input: %s, invalid value: %s";
    public static final String INCOMPLIANT_FORMAT_MESSAGE =
            "The given value does not comply with the expected format; "
                    + "expected pattern: '%s', invalid value: %s";
    public static final String INVALID_CELLPHONE_NO_MESSAGE =
            "The given cellphone number does not conform to standard patterns; "
                    + "invalid cellphone no.: %s";

    private List<Integer> missingFields;
    private List<Integer> malformedFields;

    public ValidationException() {
    }

    public ValidationException(
            String message, List<Integer> missingFields,
            List<Integer> malformedFields) {
        super(message);
        this.missingFields = missingFields;
        this.malformedFields = malformedFields;
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(Throwable cause, String errorCode) {
        super(cause, errorCode);
    }

    public ValidationException(
            List<Integer> missingFields, List<Integer> malformedFields) {
        this.missingFields = missingFields;
        this.malformedFields = malformedFields;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(
            String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ValidationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public ValidationException(
            String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public ValidationException(
            String message, String errorCode, String localMessage) {
        super(message, errorCode, localMessage);
    }

    public ValidationException(
            String message, Throwable cause,
            String errorCode, String localMessage) {
        super(message, cause, errorCode, localMessage);
    }

    public List<Integer> getMissingFields() {
        return missingFields;
    }

    public ValidationException(Throwable cause, ProtocolItem mainItem) {
        super(cause, mainItem);
    }

    public ValidationException(
            Throwable cause, ProtocolItem mainItem, ProtocolItem subItem) {
        super(cause, mainItem, subItem);
    }

    public ValidationException(
            Throwable cause, ProtocolItem mainItem,
            List<ProtocolItem> subItems) {
        super(cause, mainItem, subItems);
    }

    public ValidationException(
            Throwable cause, ProtocolItem mainItem, String errorCode) {
        super(cause, mainItem, errorCode);
    }

    public ValidationException(
            Throwable cause, ProtocolItem mainItem,
            ProtocolItem subItem, String errorCode) {
        super(cause, mainItem, subItem, errorCode);
    }

    public ValidationException(
            Throwable cause, ProtocolItem mainItem,
            List<ProtocolItem> subItems, String errorCode) {
        super(cause, mainItem, subItems, errorCode);
    }

    public ValidationException(String message, ProtocolItem mainItem) {
        super(message, mainItem);
    }

    public ValidationException(
            String message, ProtocolItem mainItem, ProtocolItem subItem) {
        super(message, mainItem, subItem);
    }

    public ValidationException(
            String message, ProtocolItem mainItem,
            List<ProtocolItem> subItems) {
        super(message, mainItem, subItems);
    }

    public ValidationException(
            String message, ProtocolItem mainItem, String errorCode) {
        super(message, mainItem, errorCode);
    }

    public ValidationException(
            String message, ProtocolItem mainItem,
            ProtocolItem subItem, String errorCode) {
        super(message, mainItem, subItem, errorCode);
    }

    public ValidationException(
            String message, ProtocolItem mainItem,
            List<ProtocolItem> subItems, String errorCode) {
        super(message, mainItem, subItems, errorCode);
    }

    public List<Integer> getMalformedFields() {
        return malformedFields;
    }
}
