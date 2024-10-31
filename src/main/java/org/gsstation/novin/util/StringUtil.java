package org.gsstation.novin.util;

/**
 * Created by A_Tofigh at 7/13/2024
 */

public class StringUtil {

    public static String byteToString2(byte[] b) {
        StringBuilder data = null;
        if(b.length > 0) {
            data = new StringBuilder();
            for (byte value : b) {
                data.append((char) (value & 255));
            }
            return data.toString();
        }
        return null;
    }
}
