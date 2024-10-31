package org.gsstation.novin.util.configuration;

import org.gsstation.novin.TransactionData;
import org.gsstation.novin.core.exception.GsRuntimeException;
import org.gsstation.novin.core.exception.InvalidConfigurationException;
import org.jdom2.Element;


import java.beans.Introspector;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.gsstation.novin.core.common.Constants.*;
import static org.gsstation.novin.core.common.ProtocolRulesBase.*;
import static org.gsstation.novin.core.exception.InvalidConfigurationException.*;
import static org.gsstation.novin.util.configuration.ConfigurationHelper.*;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public class EasyConfiguration {
    public static final String PATH_CHILD_ACCESSOR = ".";
    private static final Pattern ALL_UPPERCASE_PATTERN =
            Pattern.compile("^([A-Z]+_)*[A-Z]+$");
    private static final Pattern DASH_CASE_PATTERN = Pattern.compile("-");
    private static final Pattern ITEM_TYPE_PATTERN =
            Pattern.compile("^\\$?(.*?)\\$(.*)");
    //ITEM_TYPE_PATTERN = Pattern.compile("^\\$(.*?)\\$(.*)");
    private static final String GsMain_PACKAGES_PREFIX = "org.gsstation.novin.core.common.system";
    private static final Set<String> GsMain_PACKAGES =
            Arrays.stream(Package.getPackages())
                    .map(Package::getName)
                    .filter(aPackage ->
                            aPackage.startsWith(GsMain_PACKAGES_PREFIX))
                    .collect(Collectors.toSet());
    private static final String CONFIG_ELEMENT_NULL =
            "No configuration element provided to this instance of "
                    + "EasyConfiguration, you should pass the target xml "
                    + "element when instantiating this object!";

    private Element configurationElement;
    private List<EasyConfiguration> referenceResolvers = new ArrayList<>();

    public EasyConfiguration(
            Element configurationElement, Element... resolverElements) {
        this.configurationElement = configurationElement;
        resolverElements =
                resolverElements == null ? new Element[0] : resolverElements;
        List<Element> realResolvers = new ArrayList<>();
        for (Element resolverElement : resolverElements) {
            if (resolverElement != null)
                realResolvers.add(resolverElement);
        }
        Element[] siblingElements =
                XmlHelper.getSiblings(configurationElement);
        siblingElements = siblingElements == null
                ? new Element[0] : siblingElements;
        /*referenceResolvers = new EasyConfiguration[
                realResolvers.size() + siblingElements.length + 1];*/
        referenceResolvers.add(this);
        this.referenceResolvers.addAll(realResolvers.stream()
                .map(EasyConfiguration::new).collect(Collectors.toList()));
        for (Element siblingElement : siblingElements)
            this.referenceResolvers.add(new EasyConfiguration(siblingElement));
    }

    public EasyConfiguration(
            Element configurationElement,
            List<EasyConfiguration> referenceResolvers) {
        this.configurationElement = configurationElement;
        if (referenceResolvers != null)
            this.referenceResolvers.addAll(referenceResolvers);
    }

    /**
     * Forces client code to use two-arg ctor for proper initialization,
     * even if object is newed with one arg, because this ctor has private
     * access then the other var-arg ctor is called, second param being a
     * zero-length array.
     */
    private EasyConfiguration(Element configurationElement) {
        this.configurationElement = configurationElement;
        referenceResolvers.add(this);
    }

    public EasyConfiguration() {}

    public String[][] parseMultiSectionSuperDescriptor(
            String completeMultiSectionDescriptor) {
        /*String[] sourceDescriptors =
                getStringArray(completeMultiSectionDescriptor);*/
        String[] sourceDescriptors =
                splitContainedDescriptors(completeMultiSectionDescriptor);
        String[] itemIds = new String[sourceDescriptors.length];
        String[] sourceRanges = new String[sourceDescriptors.length];
        String[] sourceDefaults = new String[sourceDescriptors.length];
        String[] prefixes = new String[sourceDescriptors.length];
        String[] suffixes = new String[sourceDescriptors.length];
        for (int i = 0; i < sourceDescriptors.length; i++) {
            String[] parsedParts = parseSourceDescriptor(sourceDescriptors[i]);
            itemIds[i] = parsedParts[0];
            sourceRanges[i] = parsedParts[1];
            sourceDefaults[i] = parsedParts[2];
            prefixes[i] = parsedParts[3];
            suffixes[i] = parsedParts[4];
        }
        return new String[][] {
                itemIds, sourceRanges, sourceDefaults, prefixes, suffixes };
    }

    /**
     * @param referenceDescriptor reference descriptor to parse
     * @return a 5-element string array with following contents:
     * [ itemId, itemRangeSpecifier, defaultValue, prefix, suffix ]
     * @throws InvalidConfigurationException
     */
    @SuppressWarnings("JavaDoc")
    public String[] parseSourceDescriptor(String referenceDescriptor) {
        String itemId;
        String sourceDefault;
        String prefix;
        String suffix;
        String[] referenceParts = extractReferenceParts(referenceDescriptor);
        prefix = referenceParts[1];
        suffix = referenceParts[2];
        if (referenceParts[0] == null)
            return new String[] { null, null, "", prefix, suffix };
        String[] itemAndItsDefault =
                DEFAULT_VALUE_SEPARATOR_PATTERN.split(referenceParts[0]);
        String[] itemAndRange;
        String[] defaultAndRange = new String[0];
        if (itemAndItsDefault.length == 1) {
            itemAndRange = SUBSTRING_RANGE_SEPARATOR_PATTERN
                    .split(itemAndItsDefault[0]);
        } else if (itemAndItsDefault.length == 2) {
            itemAndRange = SUBSTRING_RANGE_SEPARATOR_PATTERN
                    .split(itemAndItsDefault[0]);
            defaultAndRange = SUBSTRING_RANGE_SEPARATOR_PATTERN
                    .split(itemAndItsDefault[1]);
        } else {
            throw new InvalidConfigurationException(
                    "Too many values given in reference descriptor; "
                            + "invalid descriptor: " + referenceDescriptor);
        }
        String itemRangeSpecifier = "*";
        if (itemAndRange.length == 1) {
            itemId = itemAndRange[0];
        } else if (itemAndRange.length == 2) {
            itemId = itemAndRange[0];
            itemRangeSpecifier = itemAndRange[1];
        } else {
            throw new InvalidConfigurationException(
                    "Too many values given in reference descriptor's item "
                            + "and range part; invalid descriptor: "
                            + referenceDescriptor);
        }
        String defaultValue = "";
        String defaultRangeSpecifier = "*";
        if (defaultAndRange.length == 1) {
            defaultValue = defaultAndRange[0];
        } else if (defaultAndRange.length == 2) {
            defaultValue = defaultAndRange[0];
            defaultRangeSpecifier = defaultAndRange[1];
        } else if (defaultAndRange.length > 2) {
            throw new InvalidConfigurationException(
                    "Too many values given in reference descriptor's default "
                            + "and range part; invalid descriptor: "
                            + referenceDescriptor);
        }
        defaultValue = resolveImmediateReference(defaultValue, "");
        int[] defaultRange = getRangeIndices(defaultRangeSpecifier);
        sourceDefault = getEffectiveSourceValue(
                defaultValue, defaultRange[0], defaultRange[1]);
        return new String[] {
                itemId, itemRangeSpecifier, sourceDefault, prefix, suffix };
    }

    /**
     * @param referenceDescriptor reference descriptor to parse
     * @return a 2-element string array with following contents:
     * [ targetItemId, targetRangeValue ]
     * @throws InvalidConfigurationException
     */
    @SuppressWarnings("JavaDoc")
    public String[] parseTargetDescriptor(String referenceDescriptor) {
        String targetItemId;
        String targetRangeValue = "";
        String[] targetIdAndRange =
                SUBSTRING_RANGE_SEPARATOR_PATTERN.split(referenceDescriptor);
        if (targetIdAndRange.length == 1) {
            targetItemId = targetIdAndRange[0];
        } else if (targetIdAndRange.length == 2) {
            targetItemId = targetIdAndRange[0];
            targetRangeValue = targetIdAndRange[1];
        } else {
            throw new InvalidConfigurationException(
                    "Too many values given in transformation target "
                            + "descriptor; invalid descriptor: "
                            + referenceDescriptor);
        }
        return new String[] { targetItemId, targetRangeValue };
    }

    public Map.Entry<String, String[][]> parseFunctionDescriptor(
            String functionDescriptor) {
        String argumentId;
        String argumentDefault;
        String[] referenceParts = extractReferenceParts(functionDescriptor);
        if (referenceParts[0] == null)
            throw new InvalidConfigurationException(
                    "Malformed function descriptor: " + functionDescriptor);
        String[] functionAndItsArgs =
                FUNCTION_ARGS_START_PATTERN.split(referenceParts[0]);
        String functionId = functionAndItsArgs[0];
        List<String[]> argInfo = new ArrayList<>();
        if (functionAndItsArgs.length > 1) {
            String argumentsDescriptor = functionAndItsArgs[1];
            if (!FUNCTION_ARGS_END_PATTERN.matcher(argumentsDescriptor)
                    .matches())
                throw new InvalidConfigurationException(
                        "Function arguments not configured properly; missing "
                                + "closing parenthesis");
            argumentsDescriptor = argumentsDescriptor.substring(
                    0, argumentsDescriptor.length() - 1);
            String[] arguments = getStringArray(argumentsDescriptor);
            for (String argument : arguments) {
                String[] argumentAndItsDefault =
                        DEFAULT_VALUE_SEPARATOR_PATTERN.split(argument);
                String[] argumentAndRange;
                String[] defaultAndRange = new String[0];
                if (argumentAndItsDefault.length == 1) {
                    argumentAndRange = SUBSTRING_RANGE_SEPARATOR_PATTERN
                            .split(argumentAndItsDefault[0]);
                } else if (argumentAndItsDefault.length == 2) {
                    argumentAndRange = SUBSTRING_RANGE_SEPARATOR_PATTERN
                            .split(argumentAndItsDefault[0]);
                    defaultAndRange = SUBSTRING_RANGE_SEPARATOR_PATTERN
                            .split(argumentAndItsDefault[1]);
                } else {
                    throw new InvalidConfigurationException(
                            "Too many values given in argument descriptor; "
                                    + "invalid descriptor: " + argument);
                }
                String argumentRangeSpecifier = "*";
                if (argumentAndRange.length == 1) {
                    argumentId = argumentAndRange[0];
                } else if (argumentAndRange.length == 2) {
                    argumentId = argumentAndRange[0];
                    argumentRangeSpecifier = argumentAndRange[1];
                } else {
                    throw new InvalidConfigurationException(
                            "Too many values given in argument descriptor's "
                                    + "item and range part; invalid "
                                    + "descriptor: " + argument);
                }
                String defaultValue = "";
                String defaultRangeSpecifier = "*";
                if (defaultAndRange.length == 1) {
                    defaultValue = defaultAndRange[0];
                } else if (defaultAndRange.length == 2) {
                    defaultValue = defaultAndRange[0];
                    defaultRangeSpecifier = defaultAndRange[1];
                } else if (defaultAndRange.length > 2) {
                    throw new InvalidConfigurationException(
                            "Too many values given in argument descriptor's "
                                    + "default and range part; invalid "
                                    + "descriptor: " + argument);
                }
                int[] defaultRange = getRangeIndices(defaultRangeSpecifier);
                argumentDefault = getEffectiveSourceValue(
                        defaultValue, defaultRange[0], defaultRange[1]);
                argInfo.add(new String[] {
                        argumentId, argumentRangeSpecifier, argumentDefault });
            }
        }
        return new AbstractMap.SimpleEntry<>(
                functionId, argInfo.toArray(new String[argInfo.size()][]));
    }

    /*private String resolveReference(
            ProtocolMessage transactionData, String multiSectionDescriptor,
            @SuppressWarnings("SameParameterValue")
            Iso8583MessageVersion iso8583Version) {
        if (multiSectionDescriptor == null)
            return null;
        if (!multiSectionDescriptor.contains(DEFERRED_REFERENCE_START_MARKER)
                && !multiSectionDescriptor.contains(
                IMMEDIATE_REFERENCE_START_MARKER))
            return multiSectionDescriptor;
        if (!multiSectionDescriptor.contains(DEFERRED_REFERENCE_START_MARKER))
            return resolveImmediateReference(multiSectionDescriptor);
        StringBuilder combinedResolvedValue = new StringBuilder();
        *//*String[] referenceDescriptors =
                getStringArray(multiSectionDescriptor);*//*
        String[] referenceDescriptors =
                splitContainedDescriptors(multiSectionDescriptor);
        String[] itemIds = new String[referenceDescriptors.length];
        String[] rangeValues = new String[referenceDescriptors.length];
        String[] defaults = new String[referenceDescriptors.length];
        String[] prefixes = new String[referenceDescriptors.length];
        String[] suffixes = new String[referenceDescriptors.length];
        for (int i = 0; i < referenceDescriptors.length; i++) {
            String referenceDescriptor = referenceDescriptors[i];
            String[] referenceDescriptorParts =
                    parseSourceDescriptor(referenceDescriptor);
            itemIds[i] = referenceDescriptorParts[0];
            rangeValues[i] = referenceDescriptorParts[1];
            defaults[i] = referenceDescriptorParts[2];
            prefixes[i] = referenceDescriptorParts[3];
            suffixes[i] = referenceDescriptorParts[4];
        }
        ProtocolItem[] items = new ProtocolItem[itemIds.length];
        for (int i = 0; i < itemIds.length; i++) {
            if (itemIds[i] == null || itemIds[i].isEmpty())
                continue;
            items[i] = resolveItemFromId(itemIds[i], iso8583Version);
        }
        int[] ranges = new int[rangeValues.length * 2];
        for (int i = 0; i < rangeValues.length; i++) {
            int[] rangeIndices = getRangeIndices(rangeValues[i]);
            ranges[i * 2] = rangeIndices[0];
            ranges[i * 2 + 1] = rangeIndices[1];
        }
        for (int i = 0; i < items.length; i++) {
            *//*if (items[i] == null)
                continue;*//*
            String referenceValue = "";
            if (items[i] != null)
                try {
                    //referenceValue = transactionData.get(items[i]);
                    referenceValue =
                            resolveItemValue(items[i], transactionData);
                } catch (Exception e) {
                    throw new InvalidConfigurationException(e);
                }
            if (referenceValue == null || referenceValue.isEmpty())
                referenceValue = defaults[i];
            if (prefixes[i].isEmpty() && referenceValue.isEmpty()
                    && suffixes[i].isEmpty())
                continue;
            combinedResolvedValue.append(prefixes[i])
                    .append(getEffectiveSourceValue(referenceValue,
                            ranges[i * 2], ranges[i * 2 + 1]))
                    .append(suffixes[i]);
        }
        return combinedResolvedValue.toString();
    }*/

    private String[] splitContainedDescriptors(String sourceDescriptors) {
        if (sourceDescriptors == null || sourceDescriptors.isEmpty())
            return null;
        List<String> referencedItems = new ArrayList<>();
        int currentIndex = 0;
        int currentReferenceStartIndex;
        int nextReferenceStartIndex = 0;
        do {
            currentReferenceStartIndex = getDeferredReferenceStartIndex(
                    sourceDescriptors, currentIndex);
            if (currentReferenceStartIndex != -1) {
                int deferredReferenceEndIndex = getDeferredReferenceEndIndex(
                        sourceDescriptors, currentIndex);
                if (deferredReferenceEndIndex == -1)
                    throw new InvalidConfigurationException(String.format(
                            INVALID_VALUE_REFERENCE_MESSAGE,
                            sourceDescriptors));
                nextReferenceStartIndex = getDeferredReferenceStartIndex(
                        sourceDescriptors, currentReferenceStartIndex + 1);
                if (nextReferenceStartIndex == -1)
                    nextReferenceStartIndex = sourceDescriptors.length();
                referencedItems.add(sourceDescriptors.substring(
                        currentIndex, nextReferenceStartIndex));
            } else {
                referencedItems.add(sourceDescriptors.substring(currentIndex));
            }
            currentIndex = nextReferenceStartIndex;
        } while (currentReferenceStartIndex != -1
                && nextReferenceStartIndex != sourceDescriptors.length());
        return referencedItems.toArray(new String[0]);
    }

    private String[] extractReferenceParts(String valueReference) {
        String referencedItem = null;
        String prefix = "";
        String suffix = "";
        // Deferred (main) reference resolution
        int deferredReferenceStartIndex =
                getDeferredReferenceStartIndex(valueReference, 0);
        if (deferredReferenceStartIndex != -1) {
            int deferredReferenceEndIndex = getDeferredReferenceEndIndex(
                    valueReference, deferredReferenceStartIndex);
            if (deferredReferenceEndIndex == -1)
                throw new InvalidConfigurationException(String.format(
                        INVALID_VALUE_REFERENCE_MESSAGE, valueReference));
            referencedItem = valueReference.substring(
                    deferredReferenceStartIndex
                            + DEFERRED_REFERENCE_START_MARKER.length(),
                    deferredReferenceEndIndex);
            if (deferredReferenceStartIndex > 0)
                prefix = valueReference.substring(
                        0, deferredReferenceStartIndex);
            if (deferredReferenceEndIndex < valueReference.length() - 1)
                suffix = valueReference.substring(
                        deferredReferenceEndIndex + 1, valueReference.length());
        } else {
            prefix = valueReference;
        }
        // Reference prefix resolution
        String prefixReferencedValue = "";
        String prefixLeftHand = "";
        String prefixRightHand = "";
        int prefixReferenceStartIndex =
                getImmediateReferenceStartIndex(prefix, 0);
        if (prefixReferenceStartIndex != -1) {
            int prefixReferenceEndIndex = getImmediateReferenceEndIndex(
                    prefix, prefixReferenceStartIndex);
            if (prefixReferenceEndIndex == -1)
                throw new InvalidConfigurationException(String.format(
                        INVALID_VALUE_REFERENCE_MESSAGE, prefix));
            prefixReferencedValue = prefix.substring(
                    prefixReferenceStartIndex, prefixReferenceEndIndex
                            + IMMEDIATE_REFERENCE_END_MARKER.length());
            prefixReferencedValue =
                    resolveImmediateReference(prefixReferencedValue);
            if (prefixReferenceStartIndex > 0)
                prefixLeftHand = prefix.substring(0, prefixReferenceStartIndex);
            if (prefixReferenceEndIndex < prefix.length() - 1)
                prefixRightHand = prefix.substring(prefixReferenceEndIndex
                                + IMMEDIATE_REFERENCE_END_MARKER.length(),
                        prefix.length());
        } else {
            prefixLeftHand = prefix;
        }
        prefix = prefixLeftHand + prefixReferencedValue + prefixRightHand;
        // Reference suffix resolution
        String suffixReferencedValue = "";
        String suffixLeftHand = "";
        String suffixRightHand = "";
        int suffixReferenceStartIndex =
                getImmediateReferenceStartIndex(suffix, 0);
        if (suffixReferenceStartIndex != -1) {
            int suffixReferenceEndIndex = getImmediateReferenceEndIndex(
                    suffix, suffixReferenceStartIndex);
            if (suffixReferenceEndIndex == -1)
                throw new InvalidConfigurationException(String.format(
                        INVALID_VALUE_REFERENCE_MESSAGE, suffix));
            suffixReferencedValue = suffix.substring(
                    suffixReferenceStartIndex, suffixReferenceEndIndex
                            + IMMEDIATE_REFERENCE_END_MARKER.length());
            suffixReferencedValue =
                    resolveImmediateReference(suffixReferencedValue);
            if (suffixReferenceStartIndex > 0)
                suffixLeftHand = suffix.substring(0, suffixReferenceStartIndex);
            if (suffixReferenceEndIndex < suffix.length() - 1)
                suffixRightHand = suffix.substring(suffixReferenceEndIndex
                                + IMMEDIATE_REFERENCE_END_MARKER.length(),
                        suffix.length());
        } else {
            suffixLeftHand = suffix;
        }
        suffix = suffixLeftHand + suffixReferencedValue + suffixRightHand;
        // All parts contained in reference, prefix and suffix already resolved
        return new String[] { referencedItem, prefix, suffix };
    }

    private <T> T resolveImmediateReference(
            String valueReferencePath, Class<T> valueType, T defaultValue) {
        try {
            T value;
            if (valueReferencePath == null || valueReferencePath.isEmpty())
                return defaultValue;
            String resolvedValue = valueReferencePath;
            int valueReferenceStartIndex =
                    getImmediateReferenceStartIndex(valueReferencePath, 0);
            if (valueReferenceStartIndex != -1) {
                int valueEndIndex = getImmediateReferenceEndIndex(
                        valueReferencePath, valueReferenceStartIndex);
                if (valueEndIndex == -1)
                    throw new InvalidConfigurationException(String.format(
                            INVALID_VALUE_REFERENCE_MESSAGE,
                            valueReferencePath));
                String valuePath = valueReferencePath.substring(
                        valueReferenceStartIndex
                                + IMMEDIATE_REFERENCE_START_MARKER.length(),
                        valueEndIndex);
                String restOfValue = null;
                if (valueEndIndex + 1 < valueReferencePath.length()) {
                    restOfValue =
                            valueReferencePath.substring(valueEndIndex + 1);
                    restOfValue = (String) resolveImmediateReference(
                            restOfValue, valueType, defaultValue);
                }
                int parentEndIndex =
                        valuePath.indexOf(PATH_CHILD_ACCESSOR);
                if (parentEndIndex == -1) {
                    //Objects.requireNonNull(referenceResolvers);
                    for (EasyConfiguration easyConfig : referenceResolvers) {
                        resolvedValue =
                                easyConfig.getAttributeValue(valuePath, "");
                        if (!resolvedValue.isEmpty())
                            break;
                    }
                } else {
                    String pathParent = valuePath.substring(0, parentEndIndex);
                    String pathChild = valuePath.substring(parentEndIndex
                            + PATH_CHILD_ACCESSOR.length());
                    for (EasyConfiguration easyConfig : referenceResolvers) {
                        Element valueParent =
                                easyConfig.getChild(pathParent);
                        if (valueParent != null) {
                            resolvedValue =
                                    findAttributeValue(pathParent, pathChild);
                            if (resolvedValue != null)
                                break;
                        }
                    }
                }
                if (resolvedValue == null)
                    throw new InvalidConfigurationException(String.format(
                            UNRESOLVED_VALUE_REFERENCE_MESSAGE, valuePath));
                if (restOfValue != null)
                    resolvedValue += restOfValue;
            }
            value = valueType == String.class ? (T) resolvedValue
                    : (T) valueType.getMethod("valueOf", String.class)
                    .invoke(null, resolvedValue);
            return value != null ? value : defaultValue;
        } catch (InvalidConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidConfigurationException(e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private <T> T resolveImmediateReference(String valuePath, T defaultValue) {
        return resolveImmediateReference(valuePath, defaultValue != null
                ? (Class<T>) defaultValue.getClass()
                : (Class<T>) String.class, defaultValue);
    }

    public <T> T resolveImmediateReference(
            String valuePath, Class<T> valueType) {
        return resolveImmediateReference(valuePath, valueType, null);
    }

    public String resolveImmediateReference(String valuePath) {
        return resolveImmediateReference(valuePath, String.class, null);
    }

    /**
     * Resolves a direct value reference like ${switch-iin} or two-level value
     * reference path like ${postbank.iin} into its actual value utilizing this
     * element data itself and other reference resolver elements provided. If
     * value is already a direct value (i.e. does not start with special symbol
     * '#{') then it is returned after doing any necessary type castings.
     */
    private <T> T resolveDeferredReference(
            TransactionData transactionData, String valueReferencePath,
            Class<T> valueType, T defaultValue) {
        Objects.requireNonNull(configurationElement, CONFIG_ELEMENT_NULL);
        try {
            T value;
            if (valueReferencePath == null || valueReferencePath.isEmpty())
                return defaultValue;
            String resolvedValue = valueReferencePath;
            int valueReferenceStartIndex =
                    getDeferredReferenceStartIndex(valueReferencePath, 0);
            if (valueReferenceStartIndex != -1) {
                int valueEndIndex = valueReferencePath.indexOf(
                        DEFERRED_REFERENCE_END_MARKER,
                        valueReferenceStartIndex);
                if (valueEndIndex == -1)
                    throw new InvalidConfigurationException(String.format(
                            INVALID_VALUE_REFERENCE_MESSAGE,
                            valueReferencePath));
                String valuePath = valueReferencePath.substring(
                        valueReferenceStartIndex
                                + DEFERRED_REFERENCE_START_MARKER.length(),
                        valueEndIndex);
                int parentEndIndex =
                        valuePath.indexOf(PATH_CHILD_ACCESSOR);
                if (parentEndIndex == -1) {
                    /*resolvedValue = transactionData.getIsoMsg().get(
                            Iso8583Helper.fieldFromId(valuePath,
                                    transactionData.getIso8583Message()
                                            .getMessageVersion()));
                    if (resolvedValue == null || resolvedValue.isEmpty()) {
                        for (EasyConfiguration easyConfig
                                : referenceResolvers) {
                            resolvedValue =
                                    easyConfig.getAttributeValue(valuePath, "");
                            if (!resolvedValue.isEmpty())
                                break;
                        }
                    }*/
                } else {
                    String pathParent = valuePath.substring(0, parentEndIndex);
                    String pathChild = valuePath.substring(parentEndIndex
                            + PATH_CHILD_ACCESSOR.length());
                    // How it relates to transactionData?
                    for (EasyConfiguration easyConfig : referenceResolvers) {
                        Element valueParent =
                                configurationElement.getChild(pathParent);
                        if (valueParent != null) {
                            resolvedValue =
                                    findAttributeValue(pathParent, pathChild);
                            if (resolvedValue != null)
                                break;
                        }
                    }
                }
                if (resolvedValue == null)
                    throw new InvalidConfigurationException(String.format(
                            UNRESOLVED_VALUE_REFERENCE_MESSAGE, valuePath));
            }
            value = valueType.equals(String.class) ? (T) resolvedValue
                    : (T) valueType.getMethod("valueOf", String.class)
                    .invoke(null, resolvedValue);
            return value != null ? value : defaultValue;
        } catch (InvalidConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidConfigurationException(e);
        }
    }

    public <T> T resolveDeferredReference(
            TransactionData transactionData, String valuePath, T defaultValue) {
        return resolveDeferredReference(transactionData, valuePath,
                defaultValue != null ? (Class<T>) defaultValue.getClass()
                        : (Class<T>) String.class, defaultValue);
    }

    public <T> T resolveDeferredReference(
            TransactionData transactionData,
            String valuePath, Class<T> valueType) {
        return resolveDeferredReference(
                transactionData, valuePath, valueType, null);
    }

    public String resolveDeferredReference(
            TransactionData transactionData, String valuePath) {
        return resolveDeferredReference(
                transactionData, valuePath, String.class, null);
    }

    public static String getImmediateReferenceName(String valueReference) {
        return getReferenceName(valueReference,
                IMMEDIATE_REFERENCE_START_MARKER,
                IMMEDIATE_REFERENCE_END_MARKER);
    }

    public static String getDeferredReferenceName(String valueReference) {
        return getReferenceName(valueReference,
                DEFERRED_REFERENCE_START_MARKER,
                DEFERRED_REFERENCE_END_MARKER);
    }

    private static String getReferenceName(
            String valueReference, String referenceStartMarker,
            String referenceEndMarker) {
        int valueReferenceStartIndex =
                valueReference.indexOf(referenceStartMarker);
        if (valueReferenceStartIndex != -1) {
            int valueEndIndex = valueReference.indexOf(
                    referenceEndMarker, valueReferenceStartIndex);
            if (valueEndIndex == -1)
                throw new InvalidConfigurationException(String.format(
                        INVALID_VALUE_REFERENCE_MESSAGE, valueReference));
            valueReference = valueReference.substring(
                    valueReferenceStartIndex + referenceStartMarker.length(),
                    valueEndIndex);
        }
        return valueReference;
    }

    private static int getReferenceStartIndex(
            String valueReference, String marker, int fromIndex) {
        if (valueReference == null || valueReference.isEmpty())
            return -1;
        int referenceStartIndex;
        do {
            referenceStartIndex = valueReference.indexOf(marker, fromIndex);
            if (referenceStartIndex != -1 && (referenceStartIndex == 0
                    || valueReference.charAt(referenceStartIndex - 1) != '\\'))
                return referenceStartIndex;
            fromIndex = referenceStartIndex + 1;
        } while (referenceStartIndex != -1);
        return referenceStartIndex;
    }

    private static int getReferenceEndIndex(
            String valueReference, String marker, int fromIndex) {
        if (valueReference == null || valueReference.isEmpty())
            return -1;
        int referenceEndIndex;
        do {
            referenceEndIndex = valueReference.indexOf(marker, fromIndex);
            if (referenceEndIndex != -1 && (referenceEndIndex == 0
                    || valueReference.charAt(referenceEndIndex - 1) != '\\'))
                return referenceEndIndex;
            fromIndex = referenceEndIndex + 1;
        } while (referenceEndIndex != -1);
        return referenceEndIndex;
    }

    private static int getDeferredReferenceStartIndex(
            String valueReference, int fromIndex) {
        return getReferenceStartIndex(
                valueReference, DEFERRED_REFERENCE_START_MARKER, fromIndex);
    }

    private static int getDeferredReferenceEndIndex(
            String valueReference, int fromIndex) {
        return getReferenceEndIndex(
                valueReference, DEFERRED_REFERENCE_END_MARKER, fromIndex);
    }

    @SuppressWarnings("SameParameterValue")
    private static int getImmediateReferenceStartIndex(
            String valueReference, int fromIndex) {
        return getReferenceStartIndex(
                valueReference, IMMEDIATE_REFERENCE_START_MARKER, fromIndex);
    }

    private static int getImmediateReferenceEndIndex(
            String valueReference, int fromIndex) {
        return getReferenceEndIndex(
                valueReference, IMMEDIATE_REFERENCE_END_MARKER, fromIndex);
    }

    public static boolean isImmediateReference(String valueDescriptor) {
        return getImmediateReferenceStartIndex(valueDescriptor, 0) != -1;
    }

    public static boolean isDeferredReference(String valueDescriptor) {
        return getDeferredReferenceStartIndex(valueDescriptor, 0) != -1;
    }

    /**
     * @return Either the value of attributeName, or 'value' attribute of a
     * 'property' element which its 'name' attribute is equal to
     * attributeName, or the 'value' attribute of a child named
     * attributeName or this namesake child's text, or an element named
     * childName's attributeName or a namesake grandchild element's
     * 'value' attribute or its child text (i.e a direct child of
     * child element with the same name as attributeName).
     * If found value is a reference to another value configured
     * elsewhere then it is resolved to its actual value via a call to
     * {@link #resolveImmediateReference}.
     * @see #resolveImmediateReference
     */
    @SuppressWarnings("WeakerAccess")
    public <T> T findAttributeValue(
            String childName, String attributeName,
            Class<T> valueType, T defaultValue) {
        Objects.requireNonNull(configurationElement, CONFIG_ELEMENT_NULL);
        String valueString = null;
        List<Element> childElements =
                configurationElement.getChildren(childName);
        /* Check for a child among all children named childName where it has
        an attribute named 'attributeName' and return its value */
        if (childElements != null && !childElements.isEmpty()) {
            for (Element childElement : childElements) {
                valueString = childElement.getAttributeValue(
                        attributeName);
                /* If previously, attribute not found, check for grandchild
                properties like:
                <child>
                    <property name="attributeName" value="someValue" />
                </child> */
                if (valueString == null) {
                    List<Element> grandchildProperties = childElement
                            .getChildren(PROPERTY_CONFIG_NAME);
                    if (grandchildProperties != null
                            && !grandchildProperties.isEmpty()) {
                        for (Element grandchildProperty
                                : grandchildProperties) {
                            if (attributeName.equals(grandchildProperty
                                    .getAttributeValue(NAME_ATTRIBUTE_NAME))) {
                                valueString = grandchildProperty
                                        .getAttributeValue(
                                                VALUE_ATTRIBUTE_NAME);
                                break;
                            }
                        }
                    }
                    /* If previously attribute not found, check for a
                    namesake child of a child (a child which has the name
                    'attributeName') where it has an attribute named 'value'
                    and return its value */
                    if (valueString == null) {
                        String named = childElement.getAttributeValue(
                                NAME_ATTRIBUTE_NAME);
                        if (attributeName.equals(named)) {
                            valueString =
                                    childElement.getAttributeValue(
                                            VALUE_ATTRIBUTE_NAME);
                            if (valueString == null)
                                valueString = childElement.getText();
                            if (valueString != null)
                                break;
                        } else {
                            Element grandChildElement =
                                    childElement.getChild(attributeName);
                            if (grandChildElement != null) {
                                valueString = grandChildElement
                                        .getAttributeValue(
                                                VALUE_ATTRIBUTE_NAME);
                                /* If previously, attribute not found,
                                check for this grandchild's text child
                                value and return its text */
                                if (valueString == null) {
                                    valueString =
                                            grandChildElement.getTextTrim();
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        /* Now the search space for the main element is over, continue to
        search space of direct root element */
        if (childName.isEmpty() && valueString == null) {
            /* If previously attribute not found, check for a direct attribute
             of this element (configurationElement) with the name
             'attributeName' and return its value */
            valueString =
                    configurationElement.getAttributeValue(attributeName);
            /* If previously, attribute not found, check for a child like:
            <property name="attributeName" value="someValue" />
            (all children named 'property') and if such return 'someValue' */
            if (valueString == null) {
                List<Element> childProperties =
                        configurationElement.getChildren(PROPERTY_CONFIG_NAME);
                if (childProperties != null && !childProperties.isEmpty()) {
                    for (Element childProperty : childProperties) {
                        if (attributeName.equals(
                                childProperty.getAttributeValue(
                                        NAME_ATTRIBUTE_NAME))) {
                            valueString = childProperty.getAttributeValue(
                                    VALUE_ATTRIBUTE_NAME);
                            break;
                        }
                    }
                }
                /* If previously, attribute not found, check for a namesake
                child of this element (a child which has the name
                'attributeName') and(if not null) treat it as the main
                element, i.e. first check for a 'value' attribute and if
                not found check for its text */
                if (valueString == null) {
                    Element namesakeChild =
                            configurationElement.getChild(attributeName);
                    if (namesakeChild != null) {
                        valueString = namesakeChild.getAttributeValue("value");
                        if (valueString == null)
                            valueString = namesakeChild.getTextTrim();
                    }
                }
            }
        }
        /*if (childName.isEmpty() && attributeName.isEmpty()
                && valueString == null)
            valueString = configurationElement.getTextTrim();*/
        /* Do not do below, at least it needs a module be up, which is useless at config phase
        if (valueString != null)
            valueString = resolveReference(null, valueString);*/
        T value = resolveImmediateReference(
                valueString, valueType, defaultValue);
        if (value == null)
            throw new InvalidConfigurationException(String.format(
                    MISSING_CONFIGURATION_ITEM_MESSAGE, attributeName));
        return value;
    }

    /**
     * @see #findAttributeValue
     */
    public <T> T findAttributeValue(
            String childName, String attributeName, T defaultValue) {
        return findAttributeValue(childName, attributeName,
                defaultValue != null ? (Class<T>) defaultValue.getClass()
                        : (Class<T>) String.class, defaultValue);
    }

    /**
     * @see #findAttributeValue
     */
    public <T> T findAttributeValue(
            String childName, String attributeName, Class<T> valueType) {
        return findAttributeValue(
                childName, attributeName, valueType, null);
    }

    /**
     * @see #findAttributeValue
     */
    public String findAttributeValue(String childName, String attributeName) {
        return findAttributeValue(
                childName, attributeName, String.class, null);
    }

    /**
     * This method is a special case of {@link #findAttributeValue} in which
     * attributeName is 'value', but its convenience and readability make it
     * worth to have such a method besides general {@link #findAttributeValue}.
     *
     * @return The value of either 'value' attribute of child element or value
     * of child element's text child. If found value is a reference to
     * another value configured elsewhere then it is resolved to its
     * actual value via a call to {@link #resolveImmediateReference}.
     * @see #findAttributeValue
     * @see #resolveImmediateReference
     */
    @SuppressWarnings("WeakerAccess")
    public <T> T findElementValue(
            String childName, Class<T> valueType, T defaultValue) {
        Objects.requireNonNull(configurationElement, CONFIG_ELEMENT_NULL);
        String valueString;
        /* First check for a direct attribute of this element
         * (configurationElement) with the name 'value' and return its value */
        valueString =
                configurationElement.getAttributeValue(VALUE_ATTRIBUTE_NAME);
        /* If previously value not found, check for this element's text
        child and if present return the text */
        if (valueString == null)
            valueString = configurationElement.getTextTrim();
        /* If previously value not found, check for a child among all
        children named childName and if a 'value' attribute is present
        return its value */
        if (valueString.isEmpty()) {
            List<Element> childElements =
                    configurationElement.getChildren(childName);
            if (childElements != null) {
                /* Attribute value has precedence over child text setting
                (i.e. if set, text is ignored) */
                for (Element childElement : childElements) {
                    valueString = childElement.getAttributeValue(
                            VALUE_ATTRIBUTE_NAME);
                    /* If previously value not found, check for a child's
                    text child (among all) and if present return text */
                    if (valueString == null) {
                        valueString = childElement.getTextTrim();
                        if (!valueString.isEmpty())
                            break;
                    } else {
                        break;
                    }
                }
            }
        }
        T value = resolveImmediateReference(
                valueString, valueType, defaultValue);
        if (value == null)
            throw new InvalidConfigurationException(String.format(
                    MISSING_CONFIGURATION_ITEM_MESSAGE,
                    childName.isEmpty() ? "ElementValue" : childName));
        return value;
    }

    /**
     * @see #findElementValue
     */
    public <T> T findElementValue(String childName, T defaultValue) {
        return findElementValue(childName, defaultValue != null
                ? (Class<T>) defaultValue.getClass()
                : (Class<T>) String.class, defaultValue);
    }

    /**
     * @see #findElementValue
     */
    public <T> T findElementValue(String childName, Class<T> valueType) {
        return findElementValue(childName, valueType, null);
    }

    /**
     * @see #findElementValue
     */
    public String findElementValue(String childName) {
        return findElementValue(childName, String.class, null);
    }

    /**
     * @return Either the value of attributeName, or the text of a name-sake
     * child element's 'value' attribute or its child text (i.e a
     * direct child element with the same name as attribute name).
     * If found value is a reference to another value configured
     * elsewhere then it is resolved to its actual value via a call to
     * {@link #resolveImmediateReference}.
     * @see #findAttributeValue
     * @see #resolveImmediateReference
     */
    public <T> T getAttributeValue(
            String attributeName, Class<T> valueType, T defaultValue) {
        return findAttributeValue("", attributeName, valueType, defaultValue);
    }

    /**
     * @see #getAttributeValue
     */
    public <T> T getAttributeValue(String attributeName, T defaultValue) {
        return getAttributeValue(attributeName, defaultValue != null
                ? (Class<T>) defaultValue.getClass()
                : (Class<T>) String.class, defaultValue);
    }

    /**
     * @see #getAttributeValue
     */
    public <T> T getAttributeValue(String attributeName, Class<T> valueType) {
        return getAttributeValue(attributeName, valueType, null);
    }

    /**
     * @see #getAttributeValue
     */
    public String getAttributeValue(String attributeName) {
        return getAttributeValue(attributeName, String.class, null);
    }

    /**
     * This method is a special case of {@link #findAttributeValue} in which
     * attributeName is 'value', but its convenience and readability make it
     * worth to have such a method besides general {@link #findAttributeValue}.
     * If found value is a reference to another value configured elsewhere
     * then it is resolved to its actual value via a call to
     * {@link #resolveImmediateReference}.
     *
     * @return The value of either 'value' attribute or element's text.
     * @see #findAttributeValue
     * @see #resolveImmediateReference
     */
    @SuppressWarnings("WeakerAccess")
    public <T> T getElementValue(Class<T> valueType, T defaultValue) {
        return findElementValue("", valueType, defaultValue);
    }

    /**
     * @see #getElementValue
     */
    public <T> T getElementValue(T defaultValue) {
        return getElementValue(defaultValue != null
                ? (Class<T>) defaultValue.getClass()
                : (Class<T>) String.class, defaultValue);
    }

    /**
     * @see #getElementValue
     */
    public <T> T getElementValue(Class<T> valueType) {
        return getElementValue(valueType, null);
    }

    /**
     * @see #getElementValue
     */
    public String getElementValue() {
        return getElementValue(String.class, null);
    }

    public boolean getBooleanValue(String configKey)
            throws InvalidConfigurationException {
        return isTrue(getAttributeValue(configKey, "no"));
    }

    public int getIntegerValue(String configKey) {
        return Integer.parseInt(getAttributeValue(configKey));
    }

    public EasyConfiguration getConfigSegment(String childName) {
        Element configElement = getChild(childName);
        if (configElement == null)
            throw new InvalidConfigurationException(String.format(
                    "No configuration segment under element '%s' for child "
                            + "'%s'", getRootName(), childName));
        return new EasyConfiguration(configElement, getReferenceResolvers());
    }

    public EasyConfiguration tryConfigSegment(String childName) {
        Element configElement = getChild(childName);
        return configElement == null ? null
                : new EasyConfiguration(configElement, referenceResolvers);
    }

    public List<EasyConfiguration> getConfigSegments(String childName) {
        return getChildren(childName).stream().map(element -> {
            try {
                return new EasyConfiguration(element, referenceResolvers);
            } catch (Exception e) {
                throw new GsRuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public List<EasyConfiguration> getConfigSegments() {
        return getChildren().stream().map(element -> {
            try {
                return new EasyConfiguration(element);
            } catch (Exception e) {
                throw new GsRuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public List<Element> getChildren(String childName) {
        Objects.requireNonNull(configurationElement, CONFIG_ELEMENT_NULL);
        return configurationElement.getChildren(childName);
    }

    public List<Element> getChildren() {
        Objects.requireNonNull(configurationElement, CONFIG_ELEMENT_NULL);
        return configurationElement.getChildren();
    }

    public Element getChild(String childName) {
        Objects.requireNonNull(configurationElement, CONFIG_ELEMENT_NULL);
        return configurationElement.getChild(childName);
    }

    public List<Element> getChildren(String childName, String grandchildName) {
        Objects.requireNonNull(configurationElement, CONFIG_ELEMENT_NULL);
        List<Element> fragmentElements = new ArrayList<>();
        Element fragmentElement = configurationElement.getChild(childName);
        /*if (fragmentElement != null) {
            Iterator<Element> iterator = fragmentElement.getDescendants(
                    o -> o instanceof Element
                            && grandchildName.equals(((Element) o).getName()));
            while (iterator.hasNext())
                fragmentElements.add(iterator.next());
        }*/
        return fragmentElements;
    }

    public Element getChild(String childName, String grandchildName) {
        Objects.requireNonNull(configurationElement, CONFIG_ELEMENT_NULL);
        Element fragmentElement = configurationElement.getChild(childName);
        /*if (fragmentElement != null) {
            Iterator<Element> iterator = fragmentElement.getDescendants(
                    o -> o instanceof Element
                            && grandchildName.equals(((Element) o).getName()));
            while (iterator.hasNext()) {
                fragmentElement = iterator.next();
                if (childName.equals(fragmentElement.getName()))
                    break;
            }
        }*/
        return fragmentElement;
    }

    public Element getConfigElement() {
        Objects.requireNonNull(configurationElement, CONFIG_ELEMENT_NULL);
        return configurationElement;
    }

    public int getIntegerValue(String configKey, Integer defaultValue) {
        return Integer.parseInt(
                getAttributeValue(configKey, "" + defaultValue));
    }

    public double getDoubleValue(String configKey) {
        return Double.parseDouble(getAttributeValue(configKey));
    }

    public double getDoubleValue(String configKey, Double defaultValue) {
        return Double.parseDouble(
                getAttributeValue(configKey, "" + defaultValue));
    }

    public List<EasyConfiguration> getReferenceResolvers() {
        return referenceResolvers;
    }

    public void addReferenceResolver(EasyConfiguration referenceResolver) {
        if (referenceResolver == null)
            return;
        referenceResolvers.add(referenceResolver);
    }

    public void addResolverElement(Element resolverElement) {
        if (resolverElement == null)
            return;
        referenceResolvers.add(new EasyConfiguration(resolverElement));
    }

    public static String convertToDashCasing(String camelCaseName) {
        if (ALL_UPPERCASE_PATTERN.matcher(camelCaseName).matches())
            return camelCaseName.replaceAll("_", "-").toLowerCase();
        String dashCaseName = Introspector.decapitalize(camelCaseName);
        dashCaseName = dashCaseName.replaceAll("([A-Z])", "-$1");
        return dashCaseName.toLowerCase();
    }

    public static String convertToCamelCasing(String dashCaseName) {
        String[] splitName = DASH_CASE_PATTERN.split(dashCaseName);
        StringBuilder camelCaseName = new StringBuilder();
        for (int i = 0; i < splitName.length; i++) {
            if (i == 0)
                camelCaseName.append(splitName[i]);
            else
                camelCaseName.append(splitName[i].substring(0, 1).toUpperCase())
                        .append(splitName[i].substring(1));
        }
        return camelCaseName.toString();
    }

    public static String convertToPascalCasing(String dashCaseName) {
        String pascalCamelCaseName = convertToCamelCasing(dashCaseName);
        pascalCamelCaseName = pascalCamelCaseName.substring(0, 1).toUpperCase()
                + pascalCamelCaseName.substring(1);
        return pascalCamelCaseName;
    }

    /**
     *
     * @return element name
     */
    public String getRootName() {
        Objects.requireNonNull(configurationElement, CONFIG_ELEMENT_NULL);
        return configurationElement.getName();
    }
}
