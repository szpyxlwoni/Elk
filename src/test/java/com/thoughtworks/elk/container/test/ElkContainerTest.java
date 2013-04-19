package com.thoughtworks.elk.container.test;

import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.container.exception.ElkContainerException;
import com.thoughtworks.elk.movie.Director;
import com.thoughtworks.elk.movie.Movie;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ElkContainerTest {

    private ElkContainer elkContainer;

    @Before
    public void setUp() throws Exception {
        elkContainer = new ElkContainer("testConstructorInjection.xml");
    }

    @Test
    public void should_get_a_bean_given_xml_file_without_dependencies() throws ElkContainerException {
        Object movie = elkContainer.getBean("movie");
        assertThat(movie, is(instanceOf(Movie.class)));
    }

    @Test
    public void should_get_a_bean_given_xml_file_with_dependencies() throws ElkContainerException {
        Director director = (Director) elkContainer.getBean("director");
        assertThat(director, is(instanceOf(Director.class)));
    }
}
