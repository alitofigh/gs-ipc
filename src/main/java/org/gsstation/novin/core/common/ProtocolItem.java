package org.gsstation.novin.core.common;

import org.gsstation.novin.core.exception.ItemType;

/**
 * Created by A_Tofigh at 07/16/2024
 */
public interface ProtocolItem {
    ItemType itemType();

    String nameId();

    String[] alternativeNames();

    TypeInfo typeInfo();
}
