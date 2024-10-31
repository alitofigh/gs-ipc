package org.gsstation.novin.util.validation;

import org.gsstation.novin.core.exception.ValidationException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by A_Tofigh at 07/16/2024
 */
public class ValidationHelper {
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("\\d+");
    private static final Pattern ALPHA_NUMERIC_PATTERN =
            Pattern.compile("[A-Za-z0-9]*");
    private static final Pattern ALPHABETIC_PATTERN =
            Pattern.compile("[A-Za-z]*");
    private static final Pattern IBAN_PATTERN =
            Pattern.compile("[A-Z]{2}\\d{24}");
    private static final HashMap<Character, String>
            IBAN_CHAR_TO_NUMBER_MAP = new HashMap<>();
    private static final BigInteger BIG_INTEGER_97 = new BigInteger("97");

    static {
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('A', "10");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('B', "11");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('C', "12");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('D', "13");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('E', "14");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('F', "15");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('G', "16");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('H', "17");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('I', "18");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('J', "19");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('K', "20");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('L', "21");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('M', "22");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('N', "23");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('O', "24");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('P', "25");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('Q', "26");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('R', "27");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('S', "28");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('T', "29");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('U', "30");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('V', "31");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('W', "32");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('X', "33");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('Y', "34");
        ValidationHelper.IBAN_CHAR_TO_NUMBER_MAP.put('Z', "35");
    }

    public static void validateNonNull(Object itemValue, String message)
            throws ValidationException {
        if (itemValue == null)
            throw new ValidationException(message);
    }

    public static void validateNumeric(Object itemValue, String message)
            throws ValidationException {
        if (!NUMERIC_PATTERN.matcher(itemValue.toString()).matches())
            throw new ValidationException(message);
    }

    public static void validateAlphaNumeric(Object itemValue, String message)
            throws ValidationException {
        if (!ALPHA_NUMERIC_PATTERN.matcher(itemValue.toString()).matches())
            throw new ValidationException(message);
    }

    public static void validateAlphabetic(Object itemValue, String message)
            throws ValidationException {
        if (!ALPHABETIC_PATTERN.matcher(itemValue.toString()).matches())
            throw new ValidationException(message);
    }

    public static void validateLength(
            Object itemValue, int min, int max, String message)
            throws ValidationException {
        String value = itemValue.toString();
        if (value.length() < min && value.length() > max)
            throw new ValidationException(message);
    }

    public static void validateLength(
            Object itemValue, int length, String message)
            throws ValidationException {
        if (itemValue.toString().length() != length)
            throw new ValidationException(message);
    }

    public static void validateMinLength(
            Object itemValue, int min, String message)
            throws ValidationException {
        String value = itemValue.toString();
        if (value.length() < min)
            throw new ValidationException(message);
    }

    public static void validateMaxLength(
            Object itemValue, int max, String message)
            throws ValidationException {
        String value = itemValue.toString();
        if (value.length() > max)
            throw new ValidationException(message);
    }

    public static void validateArrayBounds(
            Object[] value, int index, String message)
            throws ValidationException {
        Objects.requireNonNull(value, message);
        if (index >= value.length)
            throw new ValidationException(message);
    }
}
