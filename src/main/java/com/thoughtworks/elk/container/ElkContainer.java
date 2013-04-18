package com.thoughtworks.elk.container;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ElkContainer {

    private final BeanPool beanPool = new BeanPool();

    public ElkContainer(String propertyPath) {
        Document document = loadXmlFile(propertyPath);
        NodeList beans = document.getElementsByTagName("bean");
        for (int i = 0; i < beans.getLength(); i++) {
            NamedNodeMap attributes = beans.item(i).getAttributes();

            NodeList childNodes = beans.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node properties = childNodes.item(i);
                if (properties.getNodeName().equals("constructor-arg")) {
                    beanPool.register(getValue(attributes, "id"), getValue(attributes, "class"), getRefValue(properties, "ref"), getRefValue(properties, "type"));
                    break;
                } else {
                    beanPool.register(getValue(attributes, "id"), getValue(attributes, "class"));
                    break;
                }
            }
        }
    }

    private String getRefValue(Node node, String key) {
        return node.getAttributes().getNamedItem(key).getNodeValue();
    }

    private String getValue(NamedNodeMap attributes, String key) {
        return attributes.getNamedItem(key).getNodeValue();
    }

    private Document loadXmlFile(String propertyPath) {
        File fXmlFile = new File(propertyPath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public Object getBean(String beanName) {
        return beanPool.getBean(beanName);
    }
}
