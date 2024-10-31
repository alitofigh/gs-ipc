package org.gsstation.novin.util.configuration;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public class XmlHelper {
    private static final String DEFAULT_ROOT_ELEMENT_NAME =
            "response-info";
    public static final String STANDALONE_XML_FRAGMENT_KEY =
            "standalone-xml-fragment";

    private static String toXml(Map.Entry[] entries) {
        StringBuilder stringBuilder = new StringBuilder();
        /*Document xmlDoc = new Document();
        Element root = new Element(DEFAULT_ROOT_ELEMENT_NAME);
        xmlDoc.setRootElement(root);*/
        for (Map.Entry entry : entries) {
            if (STANDALONE_XML_FRAGMENT_KEY.equals(entry.getKey()))
                stringBuilder.append(entry.getValue() != null
                        ? entry.getValue().toString() : "");
            else
                stringBuilder
                        .append("<")
                        .append(entry.getKey())
                        .append(">")
                        .append(entry.getValue() != null
                                ? entry.getValue().toString() : "")
                        .append("</")
                        .append(entry.getKey())
                        .append(">");
        }
        return stringBuilder.toString();
    }

    public static String toXml(Map<String, ?> map) {
        return toXml(map.entrySet().toArray(new Map.Entry[0]));
    }

    private static String toFragment(Map.Entry[] entries) {
        StringBuilder stringBuilder = new StringBuilder();
        /*Document xmlDoc = new Document();
        Element root = new Element(DEFAULT_ROOT_ELEMENT_NAME);
        xmlDoc.setRootElement(root);*/
        for (Map.Entry entry : entries) {
            if (STANDALONE_XML_FRAGMENT_KEY.equals(entry.getKey()))
                stringBuilder.append(entry.getValue() != null
                        ? entry.getValue().toString() : "");
            else
                stringBuilder
                        .append("<")
                        .append(entry.getKey())
                        .append(" value=\"")
                        .append(entry.getValue() != null
                                ? entry.getValue().toString()
                                .replace("\"", "\\\"") : "")
                        .append("\" />");
        }
        return stringBuilder.toString();
    }

    public static String toFragment(Map<String, ?> map) {
        return toFragment(map.entrySet().toArray(new Map.Entry[0]));
    }

    public static String toXmlFragment(
            Map<String, ?> map, String rootElementName) {
        return "<" + rootElementName + ">"
                + toFragment(map.entrySet().toArray(new Map.Entry[0]))
                + "</" + rootElementName + ">";
    }

    public static String toXmlFragment(Map<String, ?> map) {
        return "<" + DEFAULT_ROOT_ELEMENT_NAME + ">"
                + toFragment(map.entrySet().toArray(new Map.Entry[0]))
                + "</" + DEFAULT_ROOT_ELEMENT_NAME + ">";
    }

    public static String toXmlFragment(Map.Entry[] entries) {
        return "<" + DEFAULT_ROOT_ELEMENT_NAME + ">"
                + toFragment(entries) + "</" + DEFAULT_ROOT_ELEMENT_NAME + ">";
    }

    public static String toXmlFragment(List<Map.Entry<String, ?>> entries) {
        return "<" + DEFAULT_ROOT_ELEMENT_NAME + ">"
                + toFragment(entries.toArray(new Map.Entry[0]))
                + "</" + DEFAULT_ROOT_ELEMENT_NAME + ">";
    }

    public static String toRootedXml(
            Map<String, ?> map, String rootElementName) {
        return "<" + rootElementName + ">"
                + toXml(map.entrySet().toArray(new Map.Entry[0]))
                + "</" + rootElementName + ">";
    }

    public static String toRootedXml(Map<String, ?> map) {
        return "<" + DEFAULT_ROOT_ELEMENT_NAME + ">"
                + toXml(map.entrySet().toArray(new Map.Entry[0]))
                + "</" + DEFAULT_ROOT_ELEMENT_NAME + ">";
    }

    public static String toRootedXml(Map.Entry[] entries) {
        return "<" + DEFAULT_ROOT_ELEMENT_NAME + ">"
                + toXml(entries) + "</" + DEFAULT_ROOT_ELEMENT_NAME + ">";
    }

    public static String toRootedXml(List<Map.Entry<String, ?>> entries) {
        return "<" + DEFAULT_ROOT_ELEMENT_NAME + ">"
                + toXml(entries.toArray(new Map.Entry[0]))
                + "</" + DEFAULT_ROOT_ELEMENT_NAME + ">";
    }

    public static Element toXmlElement(Object obj) {
        return toXmlElement(obj, obj.getClass().getSimpleName());
    }

    public static Element toXmlElement(Object obj, String elementName) {
        Element element = new Element(elementName);
        Method[] methods = obj.getClass().getMethods();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            try {
                if (f.isAnnotationPresent(ExcludeFromXml.class))
                    continue;
                String fieldName = f.getName();
                Method getterMethod = null;
                for (Method aMethodList : methods)
                    if (aMethodList.getName().equals(
                            "get" + ("" + fieldName.charAt(0)).toUpperCase()
                                    + fieldName.substring(1))) {
                        getterMethod = aMethodList;
                        break;
                    }
                if (getterMethod == null)
                    continue;
                Object property = getterMethod.invoke(obj);
                if (property == null)
                    continue;
                if (property instanceof Map) {
                    Iterator iterator = ((Map) property).entrySet().iterator();
                    Object o1;
                    while (iterator.hasNext()) {
                        o1 = ((Map.Entry) iterator.next()).getValue();
                        //noinspection unchecked
                        element.getChildren().add(toXmlElement(o1));
                    }
                } else
                    element.setAttribute(fieldName, property.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return element;
    }

    public static String toXmlString(Object obj) {
        return new XMLOutputter().outputString(toXmlElement(obj));
    }

    public static String toXmlString(Object obj, String elementName) {
        return new XMLOutputter().outputString(toXmlElement(obj, elementName));
    }

    public static String toXmlString(Element element) {
        return new XMLOutputter().outputString(element);
    }

    @SuppressWarnings("unchecked")
    public static Element[] getSiblings(Element element) {
        Element parent = element.getParentElement();
        if (parent != null)
            return ((List<Element>) parent.getChildren(element.getName()))
                    .toArray(new Element[0]);
        return null;
    }

    public static Element readRootElement(String filePath)
            throws IOException, JDOMException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(fileInputStream).getRootElement();
    }
}
