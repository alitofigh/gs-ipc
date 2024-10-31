package org.gsstation.novin.core.exception;

import org.gsstation.novin.core.common.ProtocolItem;

import java.util.List;

/**
 * Created by A_Tofigh at 07/16/2024
 */
public class UnknownDataTypeException extends GsRuntimeException {
    public UnknownDataTypeException() {}

    public UnknownDataTypeException(String message) {
        super(message);
    }

    public UnknownDataTypeException(Throwable cause) {
        super(cause);
    }

    public UnknownDataTypeException(Throwable cause, String errorCode) {
        super(cause, errorCode);
    }

    public UnknownDataTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownDataTypeException(
            String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnknownDataTypeException(String message, String errorCode) {
        super(message, errorCode);
    }

    public UnknownDataTypeException(
            String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public UnknownDataTypeException(
            String message, String errorCode, String localMessage) {
        super(message, errorCode, localMessage);
    }

    public UnknownDataTypeException(
            String message, Throwable cause,
            String errorCode, String localMessage) {
        super(message, cause, errorCode, localMessage);
    }

    public UnknownDataTypeException(Throwable cause, ProtocolItem mainField) {
        super(cause, mainField);
    }

    public UnknownDataTypeException(
            Throwable cause, ProtocolItem mainField, ProtocolItem subField) {
        super(cause, mainField, subField);
    }

    public UnknownDataTypeException(
            Throwable cause, ProtocolItem mainField, List<ProtocolItem> subFields) {
        super(cause, mainField, subFields);
    }

    public UnknownDataTypeException(
            Throwable cause, ProtocolItem mainField, String errorCode) {
        super(cause, mainField, errorCode);
    }

    public UnknownDataTypeException(
            Throwable cause, ProtocolItem mainField,
            ProtocolItem subField, String errorCode) {
        super(cause, mainField, subField, errorCode);
    }

    public UnknownDataTypeException(
            Throwable cause, ProtocolItem mainField,
            List<ProtocolItem> subFields, String errorCode) {
        super(cause, mainField, subFields, errorCode);
    }

    public UnknownDataTypeException(String message, ProtocolItem mainField) {
        super(message, mainField);
    }

    public UnknownDataTypeException(
            String message, ProtocolItem mainField, ProtocolItem subField) {
        super(message, mainField, subField);
    }

    public UnknownDataTypeException(
            String message, ProtocolItem mainField, List<ProtocolItem> subFields) {
        super(message, mainField, subFields);
    }

    public UnknownDataTypeException(
            String message, ProtocolItem mainField, String errorCode) {
        super(message, mainField, errorCode);
    }

    public UnknownDataTypeException(
            String message, ProtocolItem mainField,
            ProtocolItem subField, String errorCode) {
        super(message, mainField, subField, errorCode);
    }

    public UnknownDataTypeException(
            String message, ProtocolItem mainField,
            List<ProtocolItem> subFields, String errorCode) {
        super(message, mainField, subFields, errorCode);
    }
}
