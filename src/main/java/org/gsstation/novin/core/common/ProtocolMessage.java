package org.gsstation.novin.core.common;

import org.gsstation.novin.core.exception.InvalidProtocolItemException;

import java.util.Date;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public interface ProtocolMessage extends KeyedObject<String> {
    <T extends ProtocolItem, U> U get(T item)
            throws InvalidProtocolItemException;

    <T extends ProtocolItem> void set(T item, String value)
            throws InvalidProtocolItemException;

    <T extends ProtocolItem> void set(T item, byte[] value)
            throws InvalidProtocolItemException;

    <T> T get(String item) throws InvalidProtocolItemException;

    // NB! Prefer values of type String, otherwise you may get in trouble
    void set(String item, Object value) throws InvalidProtocolItemException;

    <T extends ProtocolItem> void unset(T item)
            throws InvalidProtocolItemException;

    <T extends ProtocolItem> boolean hasItem(T item)
            throws InvalidProtocolItemException;

    Long getMessageId();

    void setMessageId(Long messageId);

    boolean isRequest() throws InvalidProtocolItemException;

    ResponseCode getResponseCode() throws InvalidProtocolItemException;

    void setResponseCode(ResponseCode responseCode)
            throws InvalidProtocolItemException;

    Date getMessageTimestamp();

    void setMessageTimestamp(Date timestamp);

    Integer getPartitionKey();

    void setPartitionKey(Integer partitionKey);
}
