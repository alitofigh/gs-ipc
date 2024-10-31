package org.gsstation.novin.core.common;

import java.util.regex.Pattern;

import static org.gsstation.novin.core.common.Iso8583DataType.ALPHA_NUMERIC_SPECIAL_BINARY;
import static org.gsstation.novin.core.common.LengthType.LLLLVAR;

/**
 * Created by A_Tofigh at 07/16/2024
 */
public interface TypeInfo {
    TypeInfo UNKNOWN = new TypeInfo() {
        @Override
        public Iso8583DataType dataType() {
            return ALPHA_NUMERIC_SPECIAL_BINARY;
        }

        @Override
        public LengthType lengthType() {
            return LLLLVAR;
        }

        @Override
        public int fixedLength() {
            return -1;
        }

        @Override
        public int minLength() {
            return 0;
        }

        @Override
        public int maxLength() {
            return 9999;
        }

        @Override
        public Pattern pattern() {
            return null;
        }
    };

    Iso8583DataType dataType();

    LengthType lengthType();

    int fixedLength();

    int minLength();

    int maxLength();

    // Optional (but recommended) property useful in data validation
    Pattern pattern();
}
