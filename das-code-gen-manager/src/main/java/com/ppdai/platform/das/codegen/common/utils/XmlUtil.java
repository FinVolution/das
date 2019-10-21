package com.ppdai.platform.das.codegen.common.utils;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmlUtil {
    public static Document getDocument(String fileName) throws Exception {
        if (fileName == null || fileName.length() == 0) {
            return null;
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = XmlUtil.class.getClassLoader();
        }
        URL url = classLoader.getResource(fileName);
        if (url == null) {
            return null;
        }
        SAXReader reader = new SAXReader();
        return reader.read(url);
    }

    public static Document getDocumentByPath(String fileName) throws Exception {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        URL url = new File(ResourceUtil.getSingleInstance().getDbXmlPath()).toURL();
        if (url == null) {
            return null;
        }
        SAXReader reader = new SAXReader();
        return reader.read(url);
    }

    public static List<Element> getChildElements(Element element, String elementName) {
        List<Element> elements = new ArrayList<>();
        for (Iterator<Element> it = element.elementIterator(); it.hasNext(); ) {
            Element e = it.next();
            if (!e.getName().equalsIgnoreCase(elementName)) {
                continue;
            }
            elements.add(e);
        }

        return elements;
    }

    public static boolean hasAttribute(Element element, String attributeName) {
        return element.attribute(attributeName) != null;
    }

    public static String getAttribute(Element element, String attributeName) {
        return element.attributeValue(attributeName);
    }

    public static void setAttribute(Element element, String attributeName, String value) {
        element.attributeValue(attributeName, value);
    }

    public static org.w3c.dom.Document parse(Document doc) throws Exception {
        if (doc == null) {
            return (null);
        }
        java.io.StringReader reader = new java.io.StringReader(doc.asXML());
        org.xml.sax.InputSource source = new org.xml.sax.InputSource(reader);
        javax.xml.parsers.DocumentBuilderFactory documentBuilderFactory =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder documentBuilder = documentBuilderFactory.
                newDocumentBuilder();
        return (documentBuilder.parse(source));
    }

    public static org.w3c.dom.Document getwWcDocumentByPath(String fileName) throws Exception {
        Document document = getDocumentByPath(fileName);
        return parse(document);
    }

}
