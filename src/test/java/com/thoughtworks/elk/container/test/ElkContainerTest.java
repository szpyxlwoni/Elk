package com.thoughtworks.elk.container.test;

import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.movie.Director;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

public class ElkContainerTest {

    private ElkContainer elkContainer;

    @Before
    public void setUp() throws Exception {
        elkContainer = new ElkContainer("testConstructorInjection.xml");
    }

    @Test
    public void should_get_a_bean_given_xml_file_without_dependencies() {
        Object movie = elkContainer.getBean("movie");
        assertThat(movie, notNullValue());
    }

    @Test
    public void should_get_a_bean_given_xml_file_with_dependencies() {
        Director director = (Director) elkContainer.getBean("director");
        assertThat(director, notNullValue());
    }
}
