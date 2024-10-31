package org.gsstation.novin.core.common;

import org.gsstation.novin.core.exception.UnknownDataTypeException;

/**
 * Created by A_Tofigh at 07/16/2024
 */
public enum Iso8583DataType {
    NUMERIC("n", "Numeric", "عددی"),
    BINARY("b", "Binary", "دودویی"),
    ALPHA("a", "Alpha", "کاراکتر های حرفی"),
    SPECIAL("s", "Special", "کاراکتر های ویژه"),
    ALPHA_NUMERIC("an", "Alpha-Numeric", ""),
    NUMERIC_SPECIAL("ns", "Numeric-Special", ""),
    ALPHA_SPECIAL("as", "Alpha-Special", ""),
    ALPHA_NUMERIC_SPECIAL("ans", "Alpha-Numeric-Special", ""),
    ALPHA_NUMERIC_SPECIAL_BINARY("ansb", "Alpha-Numeric-Special-Binary", ""),
    TRACK2("z", "Track2", ""),
    HEX("h", "Hex", "کاراکتر های عددی مبنای 16");

    private String code;
    private String nameId;
    private String description;

    private Iso8583DataType(
            String code, String nameId, String description) {
        this.code = code;
        this.nameId = nameId;
        this.description = description;
    }

    public static Iso8583DataType fromCode(String code)
            throws UnknownDataTypeException {
        for (Iso8583DataType dataType : Iso8583DataType.values()) {
            if (dataType.code.equalsIgnoreCase(code))
                return dataType;
        }
        throw new UnknownDataTypeException("Unknown data type code: " + code);
    }

    public static Iso8583DataType fromName(String name)
            throws UnknownDataTypeException {
        for (Iso8583DataType dataType : Iso8583DataType.values()) {
            if (dataType.code.equalsIgnoreCase(name)
                    || dataType.nameId().equalsIgnoreCase(name))
                return dataType;
        }
        throw new UnknownDataTypeException("Unknown data type name: " + name);
    }

    public String code() {
        return code;
    }

    public String nameId() {
        return nameId;
    }

    public String description() {
        return description;
    }
}
