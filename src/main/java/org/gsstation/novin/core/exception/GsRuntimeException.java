package org.gsstation.novin.core.exception;

import org.gsstation.novin.core.common.ProtocolItem;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by A_Tofigh at 07/16/2024
 */
public class GsRuntimeException extends RuntimeException {
    protected String errorCode;
    protected String localMessage;
    protected List<Map.Entry<? extends ProtocolItem, List<? extends ProtocolItem>>>
            errorSites;

    public GsRuntimeException() {}

    public GsRuntimeException(String message) {
        super(message);
    }

    public GsRuntimeException(Throwable cause) {
        super(cause);
    }

    public GsRuntimeException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public GsRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GsRuntimeException(
            String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GsRuntimeException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public GsRuntimeException(
            String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public GsRuntimeException(
            String message, String errorCode, String localMessage) {
        super(message);
        this.errorCode = errorCode;
        this.localMessage = localMessage;
    }

    public GsRuntimeException(
            String message, Throwable cause,
            String errorCode, String localMessage) {
        super(message, cause);
        this.errorCode = errorCode;
        this.localMessage = localMessage;
    }

    public GsRuntimeException(Throwable cause, ProtocolItem mainItem) {
        super(cause);
        errorSites = new ArrayList<>();
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, new ArrayList<>()));
    }

    public GsRuntimeException(
            Throwable cause, ProtocolItem mainItem, ProtocolItem subItem) {
        super(cause);
        errorSites = new ArrayList<>();
        List<ProtocolItem> subItems = new ArrayList<>();
        subItems.add(subItem);
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, subItems));
    }

    public GsRuntimeException(
            Throwable cause, ProtocolItem mainItem,
            List<ProtocolItem> subItems) {
        super(cause);
        errorSites = new ArrayList<>();
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, subItems));
    }

    public GsRuntimeException(
            Throwable cause, ProtocolItem mainItem, String errorCode) {
        super(cause);
        errorSites = new ArrayList<>();
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, new ArrayList<>()));
        this.errorCode = errorCode;
    }

    public GsRuntimeException(
            Throwable cause, ProtocolItem mainItem,
            ProtocolItem subItem, String errorCode) {
        super(cause);
        errorSites = new ArrayList<>();
        List<ProtocolItem> subItems = new ArrayList<>();
        subItems.add(subItem);
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, subItems));
        this.errorCode = errorCode;
    }

    public GsRuntimeException(
            Throwable cause, ProtocolItem mainItem,
            List<ProtocolItem> subItems, String errorCode) {
        super(cause);
        errorSites = new ArrayList<>();
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, subItems));
        this.errorCode = errorCode;
    }

    public GsRuntimeException(String message, ProtocolItem mainItem) {
        super(message);
        errorSites = new ArrayList<>();
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, new ArrayList<>()));
    }

    public GsRuntimeException(
            String message, ProtocolItem mainItem, ProtocolItem subItem) {
        super(message);
        errorSites = new ArrayList<>();
        List<ProtocolItem> subItems = new ArrayList<>();
        subItems.add(subItem);
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, subItems));
    }

    public GsRuntimeException(
            String message, ProtocolItem mainItem,
            List<ProtocolItem> subItems) {
        super(message);
        errorSites = new ArrayList<>();
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, subItems));
    }

    public GsRuntimeException(
            String message, ProtocolItem mainItem, String errorCode) {
        super(message);
        errorSites = new ArrayList<>();
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, new ArrayList<>()));
        this.errorCode = errorCode;
    }

    public GsRuntimeException(
            String message, ProtocolItem mainItem,
            ProtocolItem subItem, String errorCode) {
        super(message);
        errorSites = new ArrayList<>();
        List<ProtocolItem> subItems = new ArrayList<>();
        subItems.add(subItem);
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, subItems));
        this.errorCode = errorCode;
    }

    public GsRuntimeException(
            String message, ProtocolItem mainItem,
            List<ProtocolItem> subItems, String errorCode) {
        super(message);
        errorSites = new ArrayList<>();
        errorSites.add(new AbstractMap.SimpleEntry<>(mainItem, subItems));
        this.errorCode = errorCode;
    }

    public List<Map.Entry<? extends ProtocolItem, List<? extends ProtocolItem>>>
    getErrorSites() {
        return errorSites;
    }

    public void setErrorSites(
            List<Map.Entry<? extends ProtocolItem, List<? extends ProtocolItem>>>
                    errorSites) {
        this.errorSites = errorSites;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getLocalMessage() {
        return localMessage;
    }

    public void setLocalMessage(String localMessage) {
        this.localMessage = localMessage;
    }
}
