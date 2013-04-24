package com.thoughtworks.elk.container.test;

import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.container.exception.ElkContainerException;
import com.thoughtworks.elk.movie.*;
import com.thoughtworks.elk.movie.test.Hero;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ElkContainerTest {

    private ElkContainer elkContainer;

    @Before
    public void setUp() throws Exception {
        elkContainer = new ElkContainer("testConstructorInjection.xml");
        elkContainer.addBean(Hollywood.class);
        elkContainer.addBean(Director.class);
    }

    @Test
    public void should_add_bean_and_get_bean_through_class() throws Exception {
        Hollywood hollywood = elkContainer.getBean(Hollywood.class);

        assertThat(hollywood, notNullValue());
    }

    @Test
    public void should_get_bean_added_all_dependencies() throws Exception {
        elkContainer.addBean(Titanic.class);

        Director director = elkContainer.getBean(Director.class);

        assertThat(director, notNullValue());
    }

    @Test
    public void should_parameter_can_not_be_found_given_not_enough_bean() throws Exception {
        boolean parameterAllInBeanList = elkContainer.isParameterAllInBeanList(Director.class.getConstructor(Movie.class, Company.class).getParameterTypes());

        assertFalse(parameterAllInBeanList);
    }

    @Test
    public void should_parameter_can_be_found_given_enough_bean() throws Exception {
        elkContainer.addBean(Titanic.class);

        boolean parameterAllInBeanList = elkContainer.isParameterAllInBeanList(Director.class.getConstructor(Movie.class, Company.class).getParameterTypes());

        assertTrue(parameterAllInBeanList);
    }

    @Test
    public void should_get_implement_class_given_interface() throws Exception {
        Class implementClass = elkContainer.findOneImplementClass(Company.class);

        assertThat(implementClass.toString(), is("class com.thoughtworks.elk.movie.Hollywood"));
    }

    @Test
    public void should_find_implement_class_in_current_container() throws InvocationTargetException, ElkContainerException, InstantiationException, IllegalAccessException {
        elkContainer.addBean(Titanic.class);

        assertThat(elkContainer.getBean(Movie.class), instanceOf(Movie.class));
        assertThat(elkContainer.getBean(Movie.class), notNullValue());
    }

    @Test
    public void should_find_implement_class_in_parent_container() throws InvocationTargetException, ElkContainerException, InstantiationException, IllegalAccessException {
        elkContainer.addBean(Titanic.class);
        ElkContainer childContainer = new ElkContainer();
        elkContainer.addChildContainer(childContainer);

        assertThat(childContainer.getBean(Movie.class), instanceOf(Movie.class));
        assertThat(childContainer.getBean(Movie.class), notNullValue());
    }

    @Test
    public void shouldNotGetABeanWithoutAdded() throws InvocationTargetException, InstantiationException, IllegalAccessException, ElkContainerException {
        elkContainer.addBean(Titanic.class);

        assertThat(elkContainer.getBean(Titanic.class), is(instanceOf(Titanic.class)));
        assertThat(elkContainer.getBean(Hero.class), is(nullValue()));
    }

    @Test
    public void parentShouldContainsHero() {
        ElkContainer container = new ElkContainer();
        container.addBean(Hero.class);
        ElkContainer childContainer = new ElkContainer();
        container.addChildContainer(childContainer);
        ElkContainer grandsonContainer = new ElkContainer();
        childContainer.addChildContainer(grandsonContainer);
        assertThat(grandsonContainer.validScope(Hero.class), is(true));
    }
}
