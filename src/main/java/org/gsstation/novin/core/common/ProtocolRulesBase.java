package org.gsstation.novin.core.common;

import org.gsstation.novin.core.exception.GsRuntimeException;
import org.gsstation.novin.core.module.ContextWrapper;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public abstract class ProtocolRulesBase {
    public static final int MESSAGE_ARRIVAL_TIMESTAMP_FIELD = 111;
    public static final String MESSAGE_ARRIVAL_TIMESTAMP = "arrival-timestamp";
    public static final int LISTENER_MODULE_FIELD = 110;
    public static final String LISTENER_MODULE = "listener-module";
    public static final String REMOTE_ENDPOINT = "remote-endpoint";
    public static final String DEFAULT_ORIGINAL_MESSAGE_KEY = "MSG_Key";
    public static final String ORIGINAL_MESSAGE_KEY = "original-message-key";
    public static final String DEFAULT_SPACE_URI = "tspace:default";
    public static final String SPACE_URI = "space-uri";
    public static final String INPUT_QUEUE = "input-queue";
    public static final String OUTPUT_QUEUE = "output-queue";
    public static final int DEFAULT_IN_SPACE_TIMEOUT = 15;
    public static final int DEFAULT_TRANSACTION_PROCESSING_TIMEOUT = 24;
    public static final String IN_SPACE_TIMEOUT = "in-space-timeout";
    public static final String CONTEXT_ADDITIONAL_DATA = "context-additional-data";
    public static final String INVALID_REQUEST_TRANSACTION_GROUP_NAME =
            "invalid-request-transaction";
    public static final String INVALID_TRANSACTION_GROUP_NAME = "invalid-transaction";
    public static final String TARGET_TRANSACTION_GROUP_NAME = "target-transaction";
    public static final String MAIN_LOGGER_QBEAN_NAME = "main-logger";
    public static final String MAIN_LOGGER_QBEAN_LEGACY_NAME = "Q2";
    public static final String STANDARD_TIME_FORMAT = "HH:mm:ss.SSS";
    public static final String INTEGER_NUMBER_REGEXP =
            "[0-9]+";
    public static final Pattern MULTIPLE_VALUES_SEPARATORS_PATTERN =
            Pattern.compile("(?<!\\\\)[\\s,;]\\s*");
    public static final Pattern ESCAPED_MULTIPLE_VALUES_SEPARATORS_PATTERN =
            Pattern.compile("\\\\([\\s,;]\\s*)");
    public static final Pattern FUNCTION_ARGS_START_PATTERN =
            Pattern.compile("(?<!\\\\)\\(");
    public static final Pattern FUNCTION_ARGS_END_PATTERN =
            Pattern.compile(".*?(?<!\\\\)\\)$");
    public static final Pattern DEFAULT_VALUE_SEPARATOR_PATTERN =
            Pattern.compile("(?<!\\\\)[=]");
    public static final Pattern SUBSTRING_RANGE_SEPARATOR_PATTERN =
            Pattern.compile("(?<!\\\\)[:]");
    public static final String DEFERRED_REFERENCE_START_MARKER = "#{";
    public static final String IMMEDIATE_REFERENCE_END_MARKER = "}";
    public static final String DEFERRED_REFERENCE_END_MARKER = "}";
    public static final String IMMEDIATE_REFERENCE_START_MARKER = "${";
    public static final String ALL_VALUES_WILDCARD = "*";
    public static final int END_OF_VALUE_INDEX_PLACEHOLDER = -100_000;
    public static final String ANY_VALUE_WILDCARD = "?";
    public static final int CONTEXT_MEANING_PLACEHOLDER = -100_050;
    public static final int CURRENT_CHAR_INDEX_PLACEHOLDER = -100_001;
    public static final String LENGTHWISE_SUBSTRING_MARKER = "*";
    public static final int LENGTHWISE_SUBSTRING_DUMMY_INDEX = -100_100;
    public static final int LVAR_INDICATOR = -100_010;
    public static final int LLVAR_INDICATOR = -100_011;
    public static final int LLLVAR_INDICATOR = -100_012;
    public static final int LLLLVAR_INDICATOR = -100_013;
    public static final int LLLLLVAR_INDICATOR = -100_014;
    public static final String ALL_KEY_VALUE_SEPARATORS_REGEXP =
            "\\s*:\\s*|\\s*=\\s*";
    public static final Set<String> LVAR_CONFIG_VALUES =
            new HashSet<>(Arrays.asList("L", "l"));
    public static final Set<String> LLVAR_CONFIG_VALUES =
            new HashSet<>(Arrays.asList("LL", "ll"));
    public static final Set<String> LLLVAR_CONFIG_VALUES =
            new HashSet<>(Arrays.asList("LLL", "lll"));
    public static final Set<String> LLLLVAR_CONFIG_VALUES =
            new HashSet<>(Arrays.asList("LLLL", "llll"));
    public static final Set<String> LLLLLVAR_CONFIG_VALUES =
            new HashSet<>(Arrays.asList("LLLLL", "lllll"));

    public static ISOMsg extractIsoMsg(Context context) {
        ISOMsg isoMessage;
        String originalMessageKey =
                context.getString(ORIGINAL_MESSAGE_KEY);
        if (originalMessageKey == null)
            originalMessageKey = ContextWrapper.getOriginalMessageKey();
        Object originalMessage = context.get(originalMessageKey);
        if (originalMessage instanceof ISOMsg)
            isoMessage = (ISOMsg) originalMessage;
        else
            throw new GsRuntimeException(
                    "Unknown original message type: " + originalMessage);
        return isoMessage;
    }
}
