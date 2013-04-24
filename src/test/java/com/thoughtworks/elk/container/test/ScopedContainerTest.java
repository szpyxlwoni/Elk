package com.thoughtworks.elk.container.test;


import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.container.exception.ElkContainerException;
import com.thoughtworks.elk.injection.ConstructorInjection;
import com.thoughtworks.elk.movie.Company;
import com.thoughtworks.elk.movie.Hollywood;
import com.thoughtworks.elk.movie.Titanic;
import com.thoughtworks.elk.movie.test.Hero;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ScopedContainerTest {
    private ElkContainer elkContainer;
    private ElkContainer childContainer;

    @Before
    public void before() {
        elkContainer = new ElkContainer(new ConstructorInjection());
        childContainer = new ElkContainer(new ConstructorInjection());
    }

    @Test
    public void shouldGenerateChildContainer() throws InvocationTargetException, ElkContainerException, InstantiationException, IllegalAccessException {

        elkContainer.addBean(Titanic.class);
        elkContainer.addChildContainer(childContainer);
        childContainer.addBean(Hero.class);
        assertThat(childContainer.getBean(Titanic.class), is(IsInstanceOf.instanceOf(Titanic.class)));
        assertThat(elkContainer.getBean(Hero.class), nullValue());
    }

    @Test
    public void testAddChildContainer() throws InvocationTargetException, ElkContainerException, InstantiationException, IllegalAccessException {
        ElkContainer parentContainer = new ElkContainer(new ConstructorInjection());
        ElkContainer childContainer = new ElkContainer(new ConstructorInjection());
        parentContainer.addChildContainer(childContainer);
        parentContainer.addBean(Hollywood.class);
        parentContainer.addBean(Company.class);
        childContainer.addBean(Hero.class);
        assertThat(childContainer.validScope(Company.class),is(true));
        assertThat(childContainer.getBean(Company.class), notNullValue());
        assertThat(parentContainer.getBean(Hero.class), is(nullValue()));

    }
}
