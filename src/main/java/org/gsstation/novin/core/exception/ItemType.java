package org.gsstation.novin.core.exception;

import java.util.Arrays;

import static org.gsstation.novin.util.validation.ValidationHelper.validateArrayBounds;

/**
 * Created by A_Tofigh at 07/16/2024
 */
public enum ItemType {
    ISO_FIELD("field", "f", "fld", "iso-field", "iso8583-field",
            "iso-message-field"),
    ISO_ATTRIBUTE("attribute", "a", "attr", "iso-bean-attribute",
            "iso-message-attribute", "iso-attribute"),
    PROPERTY("property", "p", "m", "prop", "map"),
    STATE("state", "s", "transaction-state", "tx-state"),
    FUNCTION("function", "n", "fn", "func", "fu"),
    CLASS("class", "c", "cls"),
    LITERAL("literal", "l", "ltr");

    private String name;
    private String[] alternativeNames = new String[0];

    ItemType(String... names) {
        try {
            validateArrayBounds(names, 1, "");
        } catch (Exception e) {
            throw new GsRuntimeException(e);
        }
        this.name = names[0];
        if (names.length > 1)
            this.alternativeNames =
                    Arrays.copyOfRange(names, 1, names.length);
    }

    public static ItemType fromName(String name)
            throws InvalidConfigurationException {
        for (ItemType itemType : ItemType.values()) {
            if (itemType.name.equalsIgnoreCase(name))
                return itemType;
            for (String altName : itemType.alternativeNames) {
                if (altName.equalsIgnoreCase(name))
                    return itemType;
            }
        }
        throw new InvalidConfigurationException(
                "No item type with such name; invalid item type: " + name);
    }

    public String nameId() {
        return name;
    }

    public String[] alternativeNames() {
        return alternativeNames;
    }

    @Override
    public String toString() {
        return name;
    }
}
