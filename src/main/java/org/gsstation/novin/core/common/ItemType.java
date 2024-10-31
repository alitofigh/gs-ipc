package org.gsstation.novin.core.common;

import java.util.Arrays;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public enum ItemType {
    PROPERTY("property", "p");

    private String name;
    private String[] alternativeNames = new String[0];
    ItemType(String... names) {
        this.name = names[0];
        if (names.length > 1)
            this.alternativeNames =
                    Arrays.copyOfRange(names, 1, names.length);
    }
}
