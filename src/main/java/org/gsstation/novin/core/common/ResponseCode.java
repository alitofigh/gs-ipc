package org.gsstation.novin.core.common;

import java.io.Serializable;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public interface ResponseCode extends Serializable {
    String code();

    String description();

    ResponseCode UNKNOWN = new ResponseCode() {
        @Override
        public String code() {
            return "-1";
        }

        @Override
        public String description() {
            return "unknown";
        }
    };
}
