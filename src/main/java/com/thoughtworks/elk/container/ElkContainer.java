package com.thoughtworks.elk.container;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;

public class ElkContainer {

    private final BeanPool beanPool = new BeanPool();

    public ElkContainer(String propertyPath) {
        Document document = loadXmlFile(propertyPath);
        NodeList beans = document.getElementsByTagName("bean");
        for (int i = 0; i < beans.getLength(); i++) {
            Node node = beans.item(i);
            NamedNodeMap attributes = node.getAttributes();
            NodeList childNodes = node.getChildNodes();
            if (childNodes.getLength() == 1) {
                beanPool.register(getValue(attributes, "id"), getValue(attributes, "class"));
                continue;
            }
            registerComponentByRef(attributes, childNodes);
        }
    }

    private void registerComponentByRef(NamedNodeMap attributes, NodeList childNodes) {
        ArrayList refs = newArrayList();
        ArrayList types = newArrayList();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            if (childNode.getNodeName().equals("constructor-arg")) {
                refs.add(getRefValue(childNode, "ref"));
                types.add(getRefValue(childNode, "type"));
            }
        }
        beanPool.register(getValue(attributes, "id"), getValue(attributes, "class"), refs, types);
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
