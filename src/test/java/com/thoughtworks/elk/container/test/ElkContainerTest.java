package com.thoughtworks.elk.container.test;

import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.container.exception.ElkContainerException;
import com.thoughtworks.elk.movie.Director;
import com.thoughtworks.elk.movie.DirectorSetter;
import com.thoughtworks.elk.movie.Movie;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ElkContainerTest {

    private ElkContainer elkContainer;
    private ElkContainer elkContainerSetter;

    @Before
    public void setUp() throws Exception {
        elkContainer = new ElkContainer("testConstructorInjection.xml");
        elkContainerSetter = new ElkContainer("testSetterInjection.xml");
    }

    @Test
    public void should_get_a_bean_given_xml_file_without_dependencies() throws ElkContainerException {
        Object movie = elkContainer.getBean("movie");
        assertThat(movie, is(instanceOf(Movie.class)));
    }

    @Test
    public void should_get_a_bean_given_xml_file_with_dependencies() throws ElkContainerException {
        Director director = (Director) elkContainer.getBean("director");

        assertThat(director, notNullValue());
        assertThat(director.getMovie(), notNullValue());
    }

    @Test
    public void should_get_a_bean_given_xml_with_setter_injection() throws ElkContainerException {
        DirectorSetter director = (DirectorSetter) elkContainerSetter.getBean("director");

        assertThat(director, notNullValue());
        assertThat(director.getMovie(), notNullValue());
        assertThat(director, is(instanceOf(DirectorSetter.class)));
    }
}
