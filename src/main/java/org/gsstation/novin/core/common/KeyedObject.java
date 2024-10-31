package org.gsstation.novin.core.common;

import org.gsstation.novin.core.exception.InvalidProtocolItemException;

import java.io.Serializable;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public interface KeyedObject <T> extends Serializable {

    T getKey();

    void setKey(T key);
}
