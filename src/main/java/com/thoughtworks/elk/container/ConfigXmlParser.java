package com.thoughtworks.elk.container;

import com.google.common.base.Function;
import com.sun.istack.internal.Nullable;
import com.thoughtworks.elk.container.exception.ElkContainerException;
import com.thoughtworks.elk.container.exception.ElkParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

public class ConfigXmlParser {

    private Document doc;

    public ConfigXmlParser(String configFilePath) throws ElkParseException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(configFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(resourceAsStream);
        } catch (Exception e) {
            throw new ElkParseException(e.getMessage());
        }
    }

    public Node getNode(String beanId) {
        NodeList nodeList = doc.getElementsByTagName("bean");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals(beanId)) {
                return node;
            }
        }
        return null;
    }

    public ArrayList<Node> getChildNodes(String parentId, String childName) {
        NodeList nodeList = getNode(parentId).getChildNodes();
        ArrayList<Node> nodeArrayList = newArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals(childName)) {
                nodeArrayList.add(node);
            }
        }
        return nodeArrayList;
    }

    private String getAttribute(String beanId, String attributeName) {
        return getNode(beanId).getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    public String getBeanClass(String beanId) {
        return getAttribute(beanId, "class");
    }

    public List getConstructorDependenciesClass(String beanId) {
        return getChildNodeAttribute(beanId, "constructor-arg", "type");
    }

    public List getConstructorDependenciesName(String beanId) {
        return getChildNodeAttribute(beanId, "constructor-arg", "ref");
    }

    private List getChildNodeAttribute(String beanId, String childName, final String attribute) {
        ArrayList<Node> childNodes = getChildNodes(beanId, childName);
        return transform(childNodes, new Function<Node, Object>() {
            @Override
            public Object apply(@Nullable Node node) {
                return node.getAttributes().getNamedItem(attribute).getNodeValue();
            }
        });
    }

    public List getProperties(String beanId) {
        List<String> propertyName = getChildNodeAttribute(beanId, "property", "name");
        List<String> propertyRef = getChildNodeAttribute(beanId, "property", "ref");
        List<String> propertyType = getChildNodeAttribute(beanId, "property", "type");
        List<Property> properties = newArrayList();
        for (int i = 0; i < propertyName.size(); i++) {
            properties.add(new Property(propertyName.get(i), propertyRef.get(i), propertyType.get(i)));
        }
        return properties;
    }

    Class[] getDependenciesClass(List dependencies) {
        return (Class[]) transform(dependencies, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object o) {
                try {
                    return Class.forName((String) o);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).toArray(new Class[0]);
    }
}
