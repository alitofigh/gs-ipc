package org.gsstation.novin.util.configuration;

import org.gsstation.novin.core.exception.InvalidConfigurationException;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import static jdk.nashorn.internal.runtime.JSType.toInteger;
import static org.gsstation.novin.core.common.ProtocolRulesBase.*;
import static org.gsstation.novin.core.exception.InvalidConfigurationException.EXPECTED_KEY_VALUE_MESSAGE;
import static org.gsstation.novin.util.validation.StringUtil.fixWidthSpacePad;
import static org.gsstation.novin.util.validation.StringUtil.fixWidthZeroPad;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public class ConfigurationHelper {
    public static final Pattern RANGE_EXTRACTOR_PATTERN =
            Pattern.compile("(?<!\\\\)[.:]{2}");
    public static final Pattern RANGE_SPECIFIER_PATTERN =
            Pattern.compile("(.{1,10})[.:]{2}(.{1,10})?");
    private static final String POSITIVE_VALUE = "positive";
    private static final String NEGATIVE_VALUE = "negative";
    private static final String PLUS_MARKER = "+";
    private static final String MINUS_MARKER = "-";
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public static String[] getStringArray(String valuesString) {
        if (valuesString == null || valuesString.isEmpty())
            return new String[0];
        String[] probablyEscapedValues =
                MULTIPLE_VALUES_SEPARATORS_PATTERN.split(valuesString);
        return Arrays.stream(probablyEscapedValues).map(value ->
                ESCAPED_MULTIPLE_VALUES_SEPARATORS_PATTERN.matcher(value)
                        .replaceAll("$1")).toArray(String[]::new);
    }

    public static List<String> getStringList(String valuesString) {
        if (valuesString == null || valuesString.isEmpty())
            return new ArrayList<>();
        return Arrays.asList(getStringArray(valuesString));
    }

    public static <T> T[] getValueArray(
            String valuesString, Class<T> targetType) {
        String[] stringArray = getStringArray(valuesString);
        if (Integer.class.equals(targetType)) {
            Integer[] integerArray = new Integer[stringArray.length];
            for (int i = 0; i < stringArray.length; i++) {
                if (!stringArray[i].trim().isEmpty())
                    integerArray[i] = Integer.parseInt(stringArray[i].trim());
            }
            return (T[]) integerArray;
        }
        return (T[]) stringArray;
    }

    public static <T> List<T> getValueList(
            String valuesString, Class<T> targetType) {
        List<String> stringList = getStringList(valuesString);
        if (Integer.class.equals(targetType)) {
            List<Integer> integerList = stringList.stream()
                    .filter(value -> !value.trim().isEmpty())
                    .map(value -> Integer.valueOf(value.trim()))
                    .collect(Collectors.toList());
            return (List<T>) integerList;
        }
        return (List<T>) stringList;
    }

    public static Set<String> getStringSet(String valuesString) {
        return new HashSet<>(getStringList(valuesString));
    }

    public static <T> Set<T> getValueSet(
            String valuesString, Class<? extends T> targetType) {
        if (Integer.class.equals(targetType))
            return (Set<T>) new HashSet<>(
                    getValueList(valuesString, Integer.class));
        return (Set<T>) getStringSet(valuesString);
    }

    public static String[] getRangeParts(String rangeString) {
        return RANGE_EXTRACTOR_PATTERN.split(rangeString);
    }

    public static boolean isRange(String rangeString) {
        return RANGE_SPECIFIER_PATTERN.matcher(rangeString).matches();
        //return RANGE_EXTRACTOR_PATTERN.split(rangeString).length > 1;
        // There is discrepancy between split and matcher(), for our regex split works
        //return RANGE_EXTRACTOR_PATTERN.matcher(rangeString).matches();
    }

    public static int[] getIntRangeValues(String rangeString) {
        long[] rangeValues = getLongRangeValues(rangeString);
        if (rangeValues[0] == Long.MIN_VALUE)
            rangeValues[0] = Integer.MIN_VALUE;
        if (rangeValues[0] == Long.MAX_VALUE)
            rangeValues[0] = Integer.MAX_VALUE;
        if (rangeValues[1] == Long.MIN_VALUE)
            rangeValues[1] = Integer.MIN_VALUE;
        if (rangeValues[1] == Long.MAX_VALUE)
            rangeValues[1] = Integer.MAX_VALUE;
        return new int[] { (int) rangeValues[0], (int) rangeValues[1] };
    }

    public static long[] getLongRangeValues(String rangeString) {
        String[] rangeValuesSpecifier = getRangeParts(rangeString);
        long[] rangeValues = new long[2];
        if (rangeValuesSpecifier.length == 1) {
            if (ALL_VALUES_WILDCARD.equals(rangeValuesSpecifier[0])
                    || rangeValuesSpecifier[0].isEmpty()) {
                rangeValues[0] = Long.MIN_VALUE;
                rangeValues[1] = Long.MAX_VALUE;
            } else if (POSITIVE_VALUE.equalsIgnoreCase(rangeValuesSpecifier[0])
                    || PLUS_MARKER.equalsIgnoreCase(rangeValuesSpecifier[0])) {
                rangeValues[0] = 0;
                rangeValues[1] = Long.MAX_VALUE;
            } else if (NEGATIVE_VALUE.equalsIgnoreCase(rangeValuesSpecifier[0])
                    || MINUS_MARKER.equalsIgnoreCase(rangeValuesSpecifier[0])) {
                rangeValues[0] = Long.MIN_VALUE;
                rangeValues[1] = 0;
            } else {
                rangeValues[0] = Long.parseLong(rangeValuesSpecifier[0]);
                rangeValues[1] = Long.MAX_VALUE;
            }
        } else if (rangeValuesSpecifier.length == 2) {
            if (ALL_VALUES_WILDCARD.equals(rangeValuesSpecifier[0])
                    || rangeValuesSpecifier[0].isEmpty())
                rangeValues[0] = Long.MIN_VALUE;
            else
                rangeValues[0] = Long.parseLong(rangeValuesSpecifier[0]);
            if (ALL_VALUES_WILDCARD.equals(rangeValuesSpecifier[1])
                    || rangeValuesSpecifier[1].isEmpty())
                rangeValues[1] = Long.MAX_VALUE;
            else
                rangeValues[1] = Long.parseLong(rangeValuesSpecifier[1]);
        } else {
            throw new InvalidConfigurationException(
                    "Invalid range value parts; expected 1 or 2 but found: "
                            + rangeValuesSpecifier.length);
        }
        return rangeValues;
    }

    public static int[] getRangeIndices(String valueRange) {
        int[] rangeIndices = { 0, END_OF_VALUE_INDEX_PLACEHOLDER };
        if (valueRange != null && !valueRange.isEmpty()) {
            String[] valueRangeIndices = getRangeParts(valueRange);
            if (valueRangeIndices.length == 1
                    && !valueRangeIndices[0].isEmpty()) {
                if (ALL_VALUES_WILDCARD.equals(valueRangeIndices[0])) {
                    rangeIndices[0] = 0;
                } else if (ANY_VALUE_WILDCARD.equals(valueRangeIndices[0])) {
                    rangeIndices[0] = CONTEXT_MEANING_PLACEHOLDER;
                } else {
                    rangeIndices[0] = resolveToIndex(valueRangeIndices[0]);
                    rangeIndices[1] = isRange(valueRange)
                            ? END_OF_VALUE_INDEX_PLACEHOLDER
                            : CURRENT_CHAR_INDEX_PLACEHOLDER;
                }
            } else if (valueRangeIndices.length == 2) {
                if (!valueRangeIndices[0].isEmpty()) {
                    if (ALL_VALUES_WILDCARD.equals(valueRangeIndices[0]))
                        rangeIndices[0] = 0;
                    else if (ANY_VALUE_WILDCARD.equals(valueRangeIndices[0]))
                        rangeIndices[0] = CONTEXT_MEANING_PLACEHOLDER;
                    else
                        rangeIndices[0] = resolveToIndex(valueRangeIndices[0]);
                }
                if (!valueRangeIndices[1].isEmpty()) {
                    if (ALL_VALUES_WILDCARD.equals(valueRangeIndices[1])) {
                        rangeIndices[1] = END_OF_VALUE_INDEX_PLACEHOLDER;
                    } else if (ANY_VALUE_WILDCARD.equals(
                            valueRangeIndices[1])) {
                        rangeIndices[1] = CONTEXT_MEANING_PLACEHOLDER;
                    } else if (valueRangeIndices[1]
                            .startsWith(LENGTHWISE_SUBSTRING_MARKER)) {
                        int endIndex = Integer.parseInt(
                                valueRangeIndices[1].substring(1));
                        rangeIndices[1] =
                                LENGTHWISE_SUBSTRING_DUMMY_INDEX - endIndex;
                    } else {
                        int endIndex = resolveToIndex(valueRangeIndices[1]);
                        rangeIndices[1] =
                                endIndex > 0 ? endIndex + 1 : endIndex;
                    }
                }
            }
        }
        return rangeIndices;
    }

    public static int resolveToIndex(String indexSymbol) {
        int resolvedIndex;
        if (ANY_VALUE_WILDCARD.equals(indexSymbol))
            resolvedIndex = CONTEXT_MEANING_PLACEHOLDER;
        else if (LVAR_CONFIG_VALUES.contains(indexSymbol))
            resolvedIndex = LVAR_INDICATOR;
        else if (LLVAR_CONFIG_VALUES.contains(indexSymbol))
            resolvedIndex = LLVAR_INDICATOR;
        else if (LLLVAR_CONFIG_VALUES.contains(indexSymbol))
            resolvedIndex = LLLVAR_INDICATOR;
        else if (LLLLVAR_CONFIG_VALUES.contains(indexSymbol))
            resolvedIndex = LLLLVAR_INDICATOR;
        else if (LLLLLVAR_CONFIG_VALUES.contains(indexSymbol))
            resolvedIndex = LLLLLVAR_INDICATOR;
        else
            resolvedIndex = Integer.parseInt(indexSymbol);
        return resolvedIndex;
    }

    /**
     *
     * @param sourceValue the containing string from which value is extracted
     * @param startIndex start index as defined by {@link ConfigurationHelper#getRangeIndices}
     * @param endIndexRef end index as defined by {@link ConfigurationHelper#getRangeIndices}.
     *                    This input will be updated upon return with the actual
     *                    end index computed by the rules set forth in that
     *                    method to convert placeholder values into real indices
     * @return final value extracted from sourceValue after calculating correct
     * start and end indices
     * @see ConfigurationHelper#getRangeIndices
     */
    public static String getEffectiveSourceValue(
            String sourceValue, int startIndex, AtomicInteger endIndexRef) {
        if (sourceValue == null || sourceValue.isEmpty())
            return sourceValue;
        String effectiveValue = "";
        int endIndex = endIndexRef.get();
        if (startIndex < 0) {
            if (CONTEXT_MEANING_PLACEHOLDER == startIndex)
                /*throw new GsMainRuntimeException(String.format(
                        "Start index denoted by context-meaning placeholder "
                                + "(-100000) should have already been "
                                + "replaced by actual value"));*/
                return "";
            startIndex = sourceValue.length() + startIndex;
            if (startIndex < 0)
                /*throw new GsMainRuntimeException(String.format(
                        "Start index out of range: %s (length %s)",
                        startIndex - sourceValue.length(),
                        sourceValue.length()));*/
                return "";
            if (endIndex > END_OF_VALUE_INDEX_PLACEHOLDER) {
                if (endIndex < 0)
                    endIndex = sourceValue.length() + endIndex + 1;
                else
                    endIndex = startIndex;
                if (endIndex < 0)
                /*throw new GsMainRuntimeException(String.format(
                        "End index out of range: %s (length %s)",
                        endIndex - sourceValue.length(),
                        sourceValue.length()));*/
                    return "";
            }
        }
        if (CURRENT_CHAR_INDEX_PLACEHOLDER == endIndex) {
            endIndex = startIndex + 1;
            effectiveValue = sourceValue.substring(startIndex, endIndex);
        } else if (LVAR_INDICATOR == endIndex) {
            int effectiveLength = Integer.parseInt(
                    sourceValue.substring(startIndex, startIndex + 1));
            endIndex = startIndex + 1 + effectiveLength;
            effectiveValue = sourceValue.substring(startIndex + 1, endIndex);
        } else if (LLVAR_INDICATOR == endIndex) {
            int effectiveLength = Integer.parseInt(
                    sourceValue.substring(startIndex, startIndex + 2));
            endIndex = startIndex + 2 + effectiveLength;
            effectiveValue = sourceValue.substring(startIndex + 2, endIndex);
        } else if (LLLVAR_INDICATOR == endIndex) {
            int effectiveLength = Integer.parseInt(
                    sourceValue.substring(startIndex, startIndex + 3));
            endIndex = startIndex + 3 + effectiveLength;
            effectiveValue = sourceValue.substring(startIndex + 3, endIndex);
        } else if (LLLLVAR_INDICATOR == endIndex) {
            int effectiveLength = Integer.parseInt(
                    sourceValue.substring(startIndex, startIndex + 4));
            endIndex = startIndex + 4 + effectiveLength;
            effectiveValue = sourceValue.substring(startIndex + 4, endIndex);
        } else if (LLLLLVAR_INDICATOR == endIndex) {
            int effectiveLength = Integer.parseInt(
                    sourceValue.substring(startIndex, startIndex + 5));
            endIndex = startIndex + 5 + effectiveLength;
            effectiveValue = sourceValue.substring(startIndex + 5, endIndex);
        } else if (END_OF_VALUE_INDEX_PLACEHOLDER == endIndex) {
            endIndex = sourceValue.length();
            effectiveValue = sourceValue.substring(startIndex, endIndex);
        } else if (endIndex < LENGTHWISE_SUBSTRING_DUMMY_INDEX) {
            int desiredLength = LENGTHWISE_SUBSTRING_DUMMY_INDEX - endIndex;
            endIndex = startIndex + desiredLength;
            if (endIndex <= sourceValue.length())
                effectiveValue = sourceValue.substring(startIndex, endIndex);
        } else if (/*startIndex >= 0 && */endIndex <= sourceValue.length()) {
            effectiveValue = sourceValue.substring(startIndex, endIndex);
        }
        endIndexRef.set(endIndex);
        return effectiveValue;
    }

    public static String getEffectiveSourceValue(
            String sourceValue, int startIndex, int endIndex) {
        return getEffectiveSourceValue(
                sourceValue, startIndex, new AtomicInteger(endIndex));
    }

    /**
     * Decouple index resolution from value extraction to make it more reusable
     * @param sourceValue the containing byte array from which value is extracted
     * @param startIndex start index as defined by {@link ConfigurationHelper#getRangeIndices}
     * @param endIndex end index as defined by {@link ConfigurationHelper#getRangeIndices}
     * @return resolved indices after calculating correct start and end indices
     * @see ConfigurationHelper#getRangeIndices
     */
/*    public static Map.Entry<Integer, Integer> resolveReadIndices(
            byte[] sourceValue, int startIndex, int endIndex) {
        if (sourceValue == null)
            sourceValue = EMPTY_BYTE_ARRAY;
        Map.Entry<Integer, Integer> unresolvedIndices =
                new AbstractMap.SimpleImmutableEntry<>(startIndex, endIndex);
        if (startIndex < 0) {
            if (CONTEXT_MEANING_PLACEHOLDER == startIndex)
                *//*throw new GsMainRuntimeException(String.format(
                        "Start index denoted by context-meaning placeholder "
                                + "(-100000) should have already been "
                                + "replaced by actual value"));*//*
                return unresolvedIndices;
            startIndex = sourceValue.length + startIndex;
            if (startIndex < 0)
                *//*throw new GsMainRuntimeException(String.format(
                        "Start index out of range: %s (length %s)",
                        startIndex - sourceValue.length(),
                        sourceValue.length()));*//*
                return unresolvedIndices;
            if (endIndex > END_OF_VALUE_INDEX_PLACEHOLDER) {
                if (endIndex < 0)
                    endIndex = sourceValue.length + endIndex + 1;
                else
                    endIndex = startIndex;
                if (endIndex < 0)
                    *//*throw new GsMainRuntimeException(String.format(
                            "End index out of range: %s (length %s)",
                            endIndex - sourceValue.length(),
                            sourceValue.length()));*//*
                    return unresolvedIndices;
            }
        }
        if (CURRENT_CHAR_INDEX_PLACEHOLDER == endIndex) {
            endIndex = startIndex + 1;
        } else if (LVAR_INDICATOR == endIndex) {
            int effectiveLength = sourceValue[startIndex];
            endIndex = startIndex + 1 + effectiveLength;
        } else if (LLVAR_INDICATOR == endIndex) {
            int effectiveLength = toInteger(Arrays.copyOfRange(
                    sourceValue, startIndex, startIndex + 2));
            endIndex = startIndex + 2 + effectiveLength;
        } else if (LLLVAR_INDICATOR == endIndex) {
            int effectiveLength = toInteger(Arrays.copyOfRange(
                    sourceValue, startIndex, startIndex + 3));
            endIndex = startIndex + 3 + effectiveLength;
        } else if (LLLLVAR_INDICATOR == endIndex) {
            int effectiveLength = toInteger(Arrays.copyOfRange(
                    sourceValue, startIndex, startIndex + 4));
            endIndex = startIndex + 4 + effectiveLength;
        } else if (LLLLLVAR_INDICATOR == endIndex) {
            int effectiveLength = toInteger(Arrays.copyOfRange(
                    sourceValue, startIndex, startIndex + 5));
            endIndex = startIndex + 5 + effectiveLength;
        } else if (END_OF_VALUE_INDEX_PLACEHOLDER == endIndex) {
            endIndex = sourceValue.length;
        } else if (endIndex < LENGTHWISE_SUBSTRING_DUMMY_INDEX) {
            int desiredLength = LENGTHWISE_SUBSTRING_DUMMY_INDEX - endIndex;
            endIndex = startIndex + desiredLength;
        }
        return new AbstractMap.SimpleImmutableEntry<>(startIndex, endIndex);
    }*/

    public static Map.Entry<Integer, Integer> resolveReadIndices(
            String sourceValue, int startIndex, int endIndex) {
        if (sourceValue == null)
            sourceValue = "";
        Map.Entry<Integer, Integer> unresolvedIndices =
                new AbstractMap.SimpleImmutableEntry<>(startIndex, endIndex);
        if (startIndex < 0) {
            if (CONTEXT_MEANING_PLACEHOLDER == startIndex)
                /*throw new GsMainRuntimeException(String.format(
                        "Start index denoted by context-meaning placeholder "
                                + "(-100000) should have already been "
                                + "replaced by actual value"));*/
                return unresolvedIndices;
            startIndex = sourceValue.length() + startIndex;
            if (startIndex < 0)
                /*throw new GsMainRuntimeException(String.format(
                        "Start index out of range: %s (length %s)",
                        startIndex - sourceValue.length(),
                        sourceValue.length()));*/
                return unresolvedIndices;
            if (endIndex > END_OF_VALUE_INDEX_PLACEHOLDER) {
                if (endIndex < 0)
                    endIndex = sourceValue.length() + endIndex + 1;
                else
                    endIndex = startIndex;
                if (endIndex < 0)
                    /*throw new GsMainRuntimeException(String.format(
                            "End index out of range: %s (length %s)",
                            endIndex - sourceValue.length(),
                            sourceValue.length()));*/
                    return unresolvedIndices;
            }
        }
        if (CURRENT_CHAR_INDEX_PLACEHOLDER == endIndex) {
            endIndex = startIndex + 1;
        } else if (LVAR_INDICATOR == endIndex) {
            int effectiveLength = sourceValue.charAt(startIndex);
            endIndex = startIndex + 1 + effectiveLength;
        } else if (LLVAR_INDICATOR == endIndex) {
            int effectiveLength = Integer.parseInt(
                    sourceValue.substring(startIndex, startIndex + 2));
            endIndex = startIndex + 2 + effectiveLength;
        } else if (LLLVAR_INDICATOR == endIndex) {
            int effectiveLength = Integer.parseInt(
                    sourceValue.substring(startIndex, startIndex + 3));
            endIndex = startIndex + 3 + effectiveLength;
        } else if (LLLLVAR_INDICATOR == endIndex) {
            int effectiveLength = Integer.parseInt(
                    sourceValue.substring(startIndex, startIndex + 4));
            endIndex = startIndex + 4 + effectiveLength;
        } else if (LLLLLVAR_INDICATOR == endIndex) {
            int effectiveLength = Integer.parseInt(
                    sourceValue.substring(startIndex, startIndex + 5));
            endIndex = startIndex + 5 + effectiveLength;
        } else if (END_OF_VALUE_INDEX_PLACEHOLDER == endIndex) {
            endIndex = sourceValue.length();
        } else if (endIndex < LENGTHWISE_SUBSTRING_DUMMY_INDEX) {
            int desiredLength = LENGTHWISE_SUBSTRING_DUMMY_INDEX - endIndex;
            endIndex = startIndex + desiredLength;
        }
        return new AbstractMap.SimpleImmutableEntry<>(startIndex, endIndex);
    }

    /*public static byte[] getValueRange(
            byte[] sourceValue, int startIndex, int endIndex) {
        if (sourceValue == null)
            return null;
        Map.Entry<Integer, Integer> resolvedIndices =
                resolveReadIndices(sourceValue, startIndex, endIndex);
        startIndex = resolvedIndices.getKey();
        endIndex = resolvedIndices.getValue();
        if (startIndex < 0 || endIndex < 0) // including CONTEXT_MEANING_PLACEHOLDER
            return EMPTY_BYTE_ARRAY;
        if (endIndex > sourceValue.length)
            endIndex = sourceValue.length;
        return Arrays.copyOfRange(sourceValue, startIndex, endIndex);
    }*/

    public static String getValueRange(
            String sourceValue, int startIndex, int endIndex) {
        if (sourceValue == null)
            return null;
        Map.Entry<Integer, Integer> resolvedIndices =
                resolveReadIndices(sourceValue, startIndex, endIndex);
        startIndex = resolvedIndices.getKey();
        endIndex = resolvedIndices.getValue();
        if (startIndex < 0 || endIndex < 0) // including CONTEXT_MEANING_PLACEHOLDER
            return "";
        if (endIndex > sourceValue.length())
            endIndex = sourceValue.length();
        return sourceValue.substring(startIndex, endIndex);
    }

    /**
     * Embeds the second argument (B) to first argument (A) from A's start index
     * to A's end index. If B is smaller than endIndex - startIndex then it is
     * padded with space to fit completely in between. Also this method takes
     * care of cases when B needs to specify its length (with two digits as
     * length (LL), three, ...)
     * @param valueToEmbedTo the value into which next parameter is inserted
     * @param valueToBeInserted the value to be embedded into first param
     * @param startIndex the index from which you start insertion
     * @param endIndex the index until which the insertion should continue, do
     *                 padding if necessary. Also this parameter may specify
     *                 whether the second param should be prefixed with its
     *                 length and how.
     * @return the result of inserting second parameter into first considering
     * that the indices may specify variable length insertion (LL, LLL, ...)
     * and other special values to get resolved first and then acted upon
     */
    public static String getEffectiveTargetValue(
            String valueToEmbedTo, String valueToBeInserted,
            int startIndex, int endIndex) {
        if (valueToEmbedTo == null)
            valueToEmbedTo = "";
        if (valueToBeInserted == null)
            valueToBeInserted = "";
        String fieldValuePrefix;
        if (startIndex <= valueToEmbedTo.length())
            fieldValuePrefix = valueToEmbedTo.substring(0, startIndex);
        else
            fieldValuePrefix =
                    fixWidthSpacePad("", startIndex - valueToEmbedTo.length());
        int suffixStartIndex = startIndex;
        if (CURRENT_CHAR_INDEX_PLACEHOLDER == endIndex) {
            suffixStartIndex = startIndex + 1;
        } else if (LVAR_INDICATOR == endIndex) {
            fieldValuePrefix += fixWidthZeroPad(valueToBeInserted.length(), 1);
            suffixStartIndex =
                    fieldValuePrefix.length() + valueToBeInserted.length();
        } else if (LLVAR_INDICATOR == endIndex) {
            fieldValuePrefix += fixWidthZeroPad(valueToBeInserted.length(), 2);
            suffixStartIndex =
                    fieldValuePrefix.length() + valueToBeInserted.length();
        } else if (LLLVAR_INDICATOR == endIndex) {
            fieldValuePrefix += fixWidthZeroPad(valueToBeInserted.length(), 3);
            suffixStartIndex =
                    fieldValuePrefix.length() + valueToBeInserted.length();
        } else if (LLLLVAR_INDICATOR == endIndex) {
            fieldValuePrefix += fixWidthZeroPad(valueToBeInserted.length(), 4);
            suffixStartIndex =
                    fieldValuePrefix.length() + valueToBeInserted.length();
        } else if (LLLLLVAR_INDICATOR == endIndex) {
            fieldValuePrefix += fixWidthZeroPad(valueToBeInserted.length(), 5);
            suffixStartIndex =
                    fieldValuePrefix.length() + valueToBeInserted.length();
        } else if (END_OF_VALUE_INDEX_PLACEHOLDER == endIndex) {
            suffixStartIndex = startIndex + valueToEmbedTo.length();//valueToBeInserted.length();
        } else { // endIndex is a positive normal index, rectify valueToBeInserted
            valueToBeInserted =
                    fixWidthSpacePad(valueToBeInserted, endIndex - startIndex);
        }
        String fieldValueSuffix = "";
        if (suffixStartIndex < valueToEmbedTo.length())
            fieldValueSuffix = valueToEmbedTo.substring(suffixStartIndex);
        return fieldValuePrefix + valueToBeInserted + fieldValueSuffix;
    }

    /**
     * Decouple index resolution from value insertion to make it more reusable.
     * @param valueToEmbedTo the value into which next parameter is inserted
     * @param valueToBeInserted the value to be embedded into first param
     * @param startIndex the index from which you start insertion
     * @param endIndex the index until which the insertion should continue, do
     *                 padding if necessary. Also this parameter may specify
     *                 whether the second param should be prefixed with its
     *                 length and how.
     * @return resolved indices marking prefix end index (where value should be
     * inserted) and suffix start index (where value insertion should end
     * resulting to either truncating the value or stuffing it with extra
     * padding characters), also the rules regarding to marker (special) index
     * values like LL, LLL, ... are honored and resolved to actual values
     */
    public static Map.Entry<Integer, Integer> resolveEmbedIndices(
            byte[] valueToEmbedTo, byte[] valueToBeInserted,
            int startIndex, int endIndex) {
        if (valueToEmbedTo == null)
            valueToEmbedTo = EMPTY_BYTE_ARRAY;
        if (valueToBeInserted == null)
            valueToBeInserted = EMPTY_BYTE_ARRAY;
        int prefixEndIndex = startIndex;
        int suffixStartIndex = endIndex;
        if (prefixEndIndex < 0) {
            if (CONTEXT_MEANING_PLACEHOLDER == startIndex)
                prefixEndIndex = 0;
            else
                prefixEndIndex = valueToEmbedTo.length + startIndex;
            if (prefixEndIndex < 0)
                prefixEndIndex = 0;
            if (suffixStartIndex > END_OF_VALUE_INDEX_PLACEHOLDER) {
                if (suffixStartIndex < 0)
                    suffixStartIndex = valueToEmbedTo.length + endIndex + 1;
                else
                    suffixStartIndex = prefixEndIndex;
                if (suffixStartIndex < 0)
                    //suffixStartIndex = valueToBeInserted.length;
                    suffixStartIndex = endIndex - startIndex;
            }
        }
        if (endIndex == CURRENT_CHAR_INDEX_PLACEHOLDER)
            suffixStartIndex = prefixEndIndex + 1;
        else if (LVAR_INDICATOR == endIndex)
            suffixStartIndex = prefixEndIndex + 1 + valueToBeInserted.length;
        else if (LLVAR_INDICATOR == endIndex)
            suffixStartIndex = prefixEndIndex + 2 + valueToBeInserted.length;
        else if (LLLVAR_INDICATOR == endIndex)
            suffixStartIndex = prefixEndIndex + 3 + valueToBeInserted.length;
        else if (LLLLVAR_INDICATOR == endIndex)
            suffixStartIndex = prefixEndIndex + 4 + valueToBeInserted.length;
        else if (LLLLLVAR_INDICATOR == endIndex)
            suffixStartIndex = prefixEndIndex + 5 + valueToBeInserted.length;
        else if (END_OF_VALUE_INDEX_PLACEHOLDER == endIndex)
            suffixStartIndex = prefixEndIndex + valueToBeInserted.length;
        else if (CONTEXT_MEANING_PLACEHOLDER == endIndex)
            suffixStartIndex = prefixEndIndex + valueToBeInserted.length;
        else if (endIndex < LENGTHWISE_SUBSTRING_DUMMY_INDEX)
            suffixStartIndex = prefixEndIndex
                    + (LENGTHWISE_SUBSTRING_DUMMY_INDEX - endIndex);
        /*if (suffixStartIndex > valueToEmbedTo.length)
            suffixStartIndex = valueToEmbedTo.length;*/
        return new AbstractMap.SimpleImmutableEntry<>(prefixEndIndex, suffixStartIndex);
    }

    public static Map.Entry<Integer, Integer> resolveEmbedIndices(
            String valueToEmbedTo, String valueToBeInserted,
            int startIndex, int endIndex) {
        if (valueToEmbedTo == null)
            valueToEmbedTo = "";
        if (valueToBeInserted == null)
            valueToBeInserted = "";
        int prefixEndIndex = startIndex;
        int suffixStartIndex = endIndex;
        if (prefixEndIndex < 0) {
            if (CONTEXT_MEANING_PLACEHOLDER == startIndex)
                prefixEndIndex = 0;
            else
                prefixEndIndex = valueToEmbedTo.length() + startIndex;
            if (prefixEndIndex < 0)
                prefixEndIndex = 0;
            if (suffixStartIndex > END_OF_VALUE_INDEX_PLACEHOLDER) {
                if (suffixStartIndex < 0)
                    suffixStartIndex = valueToEmbedTo.length() + endIndex + 1;
                else
                    suffixStartIndex = prefixEndIndex;
                if (suffixStartIndex < 0)
                    //suffixStartIndex = valueToBeInserted.length();
                    suffixStartIndex = endIndex - startIndex;
            }
        }
        if (CURRENT_CHAR_INDEX_PLACEHOLDER == endIndex)
            suffixStartIndex = prefixEndIndex + 1;
        else if (LVAR_INDICATOR == endIndex)
            suffixStartIndex = prefixEndIndex + 1 + valueToBeInserted.length();
        else if (LLVAR_INDICATOR == endIndex)
            suffixStartIndex = prefixEndIndex + 2 + valueToBeInserted.length();
        else if (LLLVAR_INDICATOR == endIndex)
            suffixStartIndex = prefixEndIndex + 3 + valueToBeInserted.length();
        else if (LLLLVAR_INDICATOR == endIndex)
            suffixStartIndex = prefixEndIndex + 4 + valueToBeInserted.length();
        else if (LLLLLVAR_INDICATOR == endIndex)
            suffixStartIndex = prefixEndIndex + 5 + valueToBeInserted.length();
        else if (END_OF_VALUE_INDEX_PLACEHOLDER == endIndex)
            suffixStartIndex = prefixEndIndex + valueToBeInserted.length();
        else if (CONTEXT_MEANING_PLACEHOLDER == endIndex)
            suffixStartIndex = prefixEndIndex + valueToBeInserted.length();
        else if (endIndex < LENGTHWISE_SUBSTRING_DUMMY_INDEX)
            suffixStartIndex = prefixEndIndex
                    + (LENGTHWISE_SUBSTRING_DUMMY_INDEX - endIndex);
        /*if (suffixStartIndex > valueToEmbedTo.length())
            suffixStartIndex = valueToEmbedTo.length();*/
        return new AbstractMap.SimpleImmutableEntry<>(prefixEndIndex, suffixStartIndex);
    }

    /**
     * Embeds the second argument (B) to first argument (A) from A's start index
     * to A's end index. If B is smaller than endIndex - startIndex then it is
     * padded with space to fit completely in between. Also this method takes
     * care of cases when B needs to specify its length (with two digits as
     * length (LL), three, ...)
     * @param valueToEmbedTo the value into which next parameter is inserted
     * @param valueToBeInserted the value to be embedded into first param
     * @param startIndex the index from which you start insertion
     * @param endIndex the index until which the insertion should continue, do
     *                 padding if necessary. Also this parameter may specify
     *                 whether the second param should be prefixed with its
     *                 length and how.
     * @return the result of inserting second parameter into first considering
     * that the indices may specify variable length insertion (LL, LLL, ...)
     * and other special values to get resolved first and then acted upon
     */
    /*public static byte[] getMergedValue(
            byte[] valueToEmbedTo, byte[] valueToBeInserted,
            int startIndex, int endIndex) {
        if (valueToEmbedTo == null)
            valueToEmbedTo = EMPTY_BYTE_ARRAY;
        if (valueToBeInserted == null)
            valueToBeInserted = EMPTY_BYTE_ARRAY;
        Map.Entry<Integer, Integer> resolvedIndices = resolveEmbedIndices(
                valueToEmbedTo, valueToBeInserted, startIndex, endIndex);
        int prefixEndIndex = resolvedIndices.getKey();
        int suffixStartIndex = resolvedIndices.getValue();
        ByteBuffer fieldValuePrefix = ByteBuffer.allocate(9_999);
        // Below truncates or stuffs with 0 corresponding to the value of startIndex
        fieldValuePrefix.put(Arrays.copyOf(valueToEmbedTo, prefixEndIndex));
        if (CURRENT_CHAR_INDEX_PLACEHOLDER == endIndex)
            valueToBeInserted = Arrays.copyOf(valueToBeInserted, 1);
        else if (LVAR_INDICATOR == endIndex)
            fieldValuePrefix.put(
                    new byte[] { (byte) valueToBeInserted.length });
        else if (LLVAR_INDICATOR == endIndex)
            fieldValuePrefix.put(fixSizeZeroPad(
                    toMinimumBytes(valueToBeInserted.length), 2));
        else if (LLLVAR_INDICATOR == endIndex)
            fieldValuePrefix.put(fixSizeZeroPad(
                    toMinimumBytes(valueToBeInserted.length), 3));
        else if (LLLLVAR_INDICATOR == endIndex)
            fieldValuePrefix.put(fixSizeZeroPad(
                    toMinimumBytes(valueToBeInserted.length), 4));
        else if (LLLLLVAR_INDICATOR == endIndex)
            fieldValuePrefix.put(fixSizeZeroPad(
                    toMinimumBytes(valueToBeInserted.length), 5));
        else
            *//* In case value to be inserted is bigger than to fit into the
            range from start index to end index, truncate it to fit *//*
            valueToBeInserted = Arrays.copyOf(
                    valueToBeInserted, suffixStartIndex - prefixEndIndex);
        ByteBuffer fieldValueSuffix = ByteBuffer.wrap(EMPTY_BYTE_ARRAY);
        if (suffixStartIndex < valueToEmbedTo.length) {
            int suffixLength = valueToEmbedTo.length - suffixStartIndex;
            fieldValueSuffix = ByteBuffer.allocate(suffixLength);
            fieldValueSuffix.put(
                    valueToEmbedTo, suffixStartIndex, suffixLength);
        }
        ByteBuffer finalValue = ByteBuffer.allocate(fieldValuePrefix.position()
                + valueToBeInserted.length + fieldValueSuffix.position());
        finalValue.put((ByteBuffer) fieldValuePrefix.flip())
                .put(valueToBeInserted)
                .put((ByteBuffer) fieldValueSuffix.flip());
        return finalValue.array();
    }*/

    public static String getMergedValue(
            String valueToEmbedTo, String valueToBeInserted,
            int startIndex, int endIndex) {
        if (valueToEmbedTo == null)
            valueToEmbedTo = "";
        if (valueToBeInserted == null)
            valueToBeInserted = "";
        Map.Entry<Integer, Integer> resolvedIndices = resolveEmbedIndices(
                valueToEmbedTo, valueToBeInserted, startIndex, endIndex);
        int prefixEndIndex = resolvedIndices.getKey();
        int suffixStartIndex = resolvedIndices.getValue();
        String fieldValuePrefix;
        if (prefixEndIndex <= valueToEmbedTo.length())
            fieldValuePrefix = valueToEmbedTo.substring(0, prefixEndIndex);
        else
            fieldValuePrefix = fixWidthSpacePad(valueToEmbedTo, prefixEndIndex);
        if (CURRENT_CHAR_INDEX_PLACEHOLDER == endIndex)
            valueToBeInserted = "" + valueToBeInserted.charAt(0);
        else if (LVAR_INDICATOR == endIndex)
            fieldValuePrefix += fixWidthZeroPad(valueToBeInserted.length(), 1);
        else if (LLVAR_INDICATOR == endIndex)
            fieldValuePrefix += fixWidthZeroPad(valueToBeInserted.length(), 2);
        else if (LLLVAR_INDICATOR == endIndex)
            fieldValuePrefix += fixWidthZeroPad(valueToBeInserted.length(), 3);
        else if (LLLLVAR_INDICATOR == endIndex)
            fieldValuePrefix += fixWidthZeroPad(valueToBeInserted.length(), 4);
        else if (LLLLLVAR_INDICATOR == endIndex)
            fieldValuePrefix += fixWidthZeroPad(valueToBeInserted.length(), 5);
        else /* In case value to be inserted is bigger than to fit into the
             range from start index to end index, truncate it to fit */
            valueToBeInserted = fixWidthSpacePad(
                    valueToBeInserted, suffixStartIndex - prefixEndIndex);
        String fieldValueSuffix = "";
        if (suffixStartIndex < valueToEmbedTo.length())
            fieldValueSuffix = valueToEmbedTo.substring(suffixStartIndex);
        return fieldValuePrefix + valueToBeInserted + fieldValueSuffix;
    }

    public static Map<String, String> getKeyValueMap(String items) {
        Map<String, String> map = new HashMap<>();
        Stream.of(getKeyValueArray(items)).forEach(
                entry -> map.put(entry.getKey(), entry.getValue()));
        return map;
    }

    public static Map.Entry<String, String>[] getKeyValueArray(String items) {
        String[] itemArray = getStringArray(items);
        Map.Entry<String, String>[] keyValuePairs = new Map.Entry[itemArray.length];
        for (int i = 0; i < itemArray.length; i++) {
            String[] itemParts =
                    itemArray[i].split(ALL_KEY_VALUE_SEPARATORS_REGEXP);
            if (itemParts.length != 2)
                throw new InvalidConfigurationException(
                        EXPECTED_KEY_VALUE_MESSAGE);
            keyValuePairs[i] =
                    new AbstractMap.SimpleEntry<>(itemParts[0].trim(), itemParts[1].trim());
        }
        return keyValuePairs;
    }

    public static List<Map.Entry<String, String>> getKeyValueList(String items) {
        return Arrays.asList(getKeyValueArray(items));
    }

    public static Set<Map.Entry<String, String>> getKeyValueSet(String items) {
        return new HashSet<>(Arrays.asList(getKeyValueArray(items)));
    }

    public static boolean isTrue(String value) {
        return "true".equalsIgnoreCase(value)
                || "yes".equalsIgnoreCase(value)
                || "on".equalsIgnoreCase(value)
                || "ok".equalsIgnoreCase(value)
                || "1".equals(value);
    }

    public static Map.Entry<String, String> getCanonicalDateTimeWithParseFormat(
            String dateTimeString, String itemName) {
        dateTimeString = canonicalizeFormattedDateTime(dateTimeString);
        String parsePattern = detectParsePattern(dateTimeString, itemName);
        return new AbstractMap.SimpleImmutableEntry<>(dateTimeString, parsePattern);
    }

    public static String canonicalizeFormattedDateTime(String dateTimeString) {
        // TODO first split over . and : and adding required zeros where necessary
        String[] dateTimeParts = dateTimeString.split("[^0-9]");
        StringBuilder simpleDateTimeFormatBuilder = new StringBuilder();
        for (String dateTimePart : dateTimeParts) {
            if (dateTimePart.length() % 2 == 1)
                dateTimePart = fixWidthZeroPad(
                        dateTimePart, dateTimePart.length() + 1);
            simpleDateTimeFormatBuilder.append(dateTimePart);
        }
        return simpleDateTimeFormatBuilder.toString();
    }

    public static String detectParsePattern(
            String dateString, String itemName) {
        String parsePattern;
        if (dateString.length() == 14)
            parsePattern = "yyyyMMddHHmmss";
        else if (dateString.length() == 8)
            parsePattern = "yyyyMMdd";
        else if (dateString.length() == 6)
            parsePattern = "HHmmss";
        else
            throw new InvalidConfigurationException("Malformed "
                    + (itemName == null || itemName.isEmpty() ? "date time"
                    : itemName) + ": '" + dateString
                    + "'; supported date time formats: 'yyyy.MM.dd HH:mm:ss', "
                    + "'yyyy/MM/dd HH:mm:ss', 'yyyyMMddHHmmss', 'yyyy.MM.dd', "
                    + "'yyyy/MM/dd', 'yyyyMMdd', 'HH:mm:ss' and 'HHmmss'");
        return parsePattern;
    }

    public static List<Integer> parseToIntegers(List<String> items) {
        return items.stream().map(item -> {
                    List<Integer> allIntegers = new ArrayList<>();
                    if (isRange(item)) {
                        int[] rangeEdges = getIntRangeValues(item);
                        for (int i = rangeEdges[0]; i < rangeEdges[1]; i++)
                            allIntegers.add(i);
                    } else {
                        allIntegers.add(Integer.parseInt(item));
                    }
                    return allIntegers;
                }).reduce((list1, list2) -> { list1.addAll(list2); return list1; })
                .orElse(new ArrayList<>());
    }

    public static List<Long> parseToLongs(List<String> items) {
        return items.stream().map(item -> {
                    List<Long> allLongs = new ArrayList<>();
                    if (isRange(item)) {
                        long[] rangeEdges = getLongRangeValues(item);
                        for (long i = rangeEdges[0]; i < rangeEdges[1]; i++)
                            allLongs.add(i);
                    } else {
                        allLongs.add(Long.parseLong(item));
                    }
                    return allLongs;
                }).reduce((list1, list2) -> { list1.addAll(list2); return list1; })
                .orElse(new ArrayList<>());
    }

    public static List<Double> parseToDoubles(List<String> items) {
        return items.stream().map(item -> {
                    List<Double> allIntegers = new ArrayList<>();
                    if (isRange(item)) {
                        long[] rangeEdges = getLongRangeValues(item);
                        for (long i = rangeEdges[0]; i < rangeEdges[1]; i++)
                            allIntegers.add((double) i);
                    } else {
                        allIntegers.add(Double.parseDouble(item));
                    }
                    return allIntegers;
                }).reduce((list1, list2) -> { list1.addAll(list2); return list1; })
                .orElse(new ArrayList<>());
    }
}
