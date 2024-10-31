package org.gsstation.novin.core.common;

import org.gsstation.novin.core.exception.GsRuntimeException;

/**
 * Created by A_Tofigh at 07/16/2024
 */
public enum LengthType {
    FIXED("f", "Fixed length", "طول ثابت"),
    LLVAR("ll", "Variable length, initial 2 digits specify subsequent actual length",
            "طول متغیر، دو رقم ابتدایی، طول واقعی داده های پس از آن را مشخص می کنند"),
    LLLVAR("lll", "Variable length, initial 3 digits specify actual length",
            "طول متغیر، سه رقم ابتدایی، طول واقعی داده های پس از آن را مشخص می کنند"),
    LLLLVAR("llll", "Variable length, initial 4 digits specify actual length",
            "طول متغیر، چهار رقم ابتدایی، طول واقعی داده های پس از آن را مشخص می کنند");

    private String code;
    private String descriptionEn;
    private String descriptionFa;

    private LengthType(
            String code, String descriptionEn, String descriptionFa) {
        this.code = code;
        this.descriptionEn = descriptionEn;
        this.descriptionFa = descriptionFa;
    }

    public static LengthType fromCode(String codeOrName) {
        for (LengthType lengthType : LengthType.values()) {
            if (lengthType.code.equalsIgnoreCase(codeOrName)
                    || lengthType.name().equalsIgnoreCase(codeOrName))
                return lengthType;
        }
        throw new GsRuntimeException("Invalid length type: " + codeOrName);
    }

    public String code() {
        return code;
    }

    public String descriptionEn() {
        return descriptionEn;
    }

    public String descriptionFa() {
        return descriptionFa;
    }
}
