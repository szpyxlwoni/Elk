package com.thoughtworks.elk.container.test;


import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.container.exception.ElkContainerException;
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
        elkContainer = new ElkContainer();
        childContainer = new ElkContainer();
    }

    @Test
    public void ShouldGenerateChildContainer() throws InvocationTargetException, ElkContainerException, InstantiationException, IllegalAccessException {
        elkContainer.addBean(Titanic.class);
        elkContainer.addChildContainer(childContainer);
        childContainer.addBean(Hero.class);
        assertThat(childContainer.getBean(Titanic.class), notNullValue());
        assertThat(elkContainer.getBean(Hero.class), nullValue());
    }
}
