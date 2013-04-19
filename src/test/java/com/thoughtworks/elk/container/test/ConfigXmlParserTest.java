package com.thoughtworks.elk.container.test;

import com.thoughtworks.elk.container.ConfigXmlParser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfigXmlParserTest {

    private ConfigXmlParser configXmlParser;

    @Before
    public void setUp() throws Exception {
        configXmlParser = new ConfigXmlParser("testElkContainer.xml");
    }

    @Test
    public void should_return_a_node_given_bean_id() {
        Node company = configXmlParser.getNode("company");

        assertThat(company, notNullValue());
        assertThat(company.getNodeName(), is("bean"));
    }

    @Test
    public void should_return_a_node_given_parent_bean_id_and_child_node_name() {
        ArrayList<Node> nodeArrayList = configXmlParser.getChildNodes("company", "description");

        assertThat(nodeArrayList.size(), is(1));
        assertThat(nodeArrayList.get(0).getChildNodes().item(0).getNodeValue(), is("test"));
    }

    @Test
    public void should_get_class_name_given_a_bean_id() {
        String attribute = configXmlParser.getBeanClass("company");

        assertThat(attribute, is("com.thoughtworks.elk.movie.Hollywood"));
    }

    @Test
    public void should_get_constructor_dependencies_class_given_a_bean_id() {
        List<String> constructorDependencies = configXmlParser.getConstructorDependenciesClass("director");

        assertThat(constructorDependencies.get(0), is("com.thoughtworks.elk.movie.Movie"));
        assertThat(constructorDependencies.size(), is(2));
    }

    @Test
    public void should_get_constructor_dependencies_name_given_a_bean_id() {
        List<String> constructorDependencies = configXmlParser.getConstructorDependenciesName("director");

        assertThat(constructorDependencies.get(0), is("movie"));
        assertThat(constructorDependencies.size(), is(2));
    }
}
