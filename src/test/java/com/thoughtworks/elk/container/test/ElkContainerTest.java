package com.thoughtworks.elk.container.test;

import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.container.exception.ElkContainerException;
import com.thoughtworks.elk.movie.*;
import com.thoughtworks.elk.movie.test.Hero;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
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
    public void should_add_bean_and_get_bean_through_class() throws Exception {
        elkContainer.addBean(Hollywood.class);

        Hollywood hollywood = elkContainer.getBean(Hollywood.class);

        assertThat(hollywood, notNullValue());
    }

    @Test
    public void should_get_bean_added_all_dependencies() throws Exception {
        elkContainer.addBean(Hollywood.class);
        elkContainer.addBean(Titanic.class);
        elkContainer.addBean(Director.class);

        Director director = elkContainer.getBean(Director.class);

        assertThat(director, notNullValue());
    }

    @Test
    public void should_get_a_bean_given_xml_file_without_dependencies() throws ElkContainerException {
        Object movie = elkContainer.getBean("movie");

        assertThat(movie, is(instanceOf(Movie.class)));
        assertThat(movie, notNullValue());
    }

    @Test
    public void should_get_a_bean_given_xml_file_with_dependencies() throws ElkContainerException {
        Director director = (Director) elkContainer.getBean("director");

        assertThat(director, notNullValue());
        assertThat(director.getMovie(), notNullValue());
    }
    
    @Test
    public void should_not_build_bean_duplicate_bean() throws ElkContainerException {
        Movie movie = (Movie) elkContainer.getBean("movie");
        Director director = (Director) elkContainer.getBean("director");
        
        assertThat(movie, is(director.getMovie()));
    }

    @Test
    public void should_get_a_bean_given_xml_with_setter_injection() throws ElkContainerException {
        DirectorSetter director = (DirectorSetter) elkContainerSetter.getBean("director");

        assertThat(director, notNullValue());
        assertThat(director.getMovie(), notNullValue());
        assertThat(director, is(instanceOf(DirectorSetter.class)));
    }

    @Test
    public void shouldNotGetABeanWithoutAdded() throws InvocationTargetException, ElkContainerException, InstantiationException, IllegalAccessException {
        ElkContainer container = new ElkContainer();
        container.addBean(Titanic.class);
        assertThat(container.getBean(Titanic.class),is(instanceOf(Titanic.class)));
        assertThat(container.getBean(Hero.class),is(nullValue()));
    }
}
