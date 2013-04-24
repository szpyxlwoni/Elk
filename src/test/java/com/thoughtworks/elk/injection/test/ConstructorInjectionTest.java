package com.thoughtworks.elk.injection.test;

import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.container.Injection;
import com.thoughtworks.elk.injection.ConstructorInjection;
import com.thoughtworks.elk.movie.Director;
import com.thoughtworks.elk.movie.Hollywood;
import com.thoughtworks.elk.movie.Titanic;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ConstructorInjectionTest {
    @Test
    public void should_build_a_bean_with_dependencies()
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Injection injection = new ConstructorInjection();
        ElkContainer elkContainer = new ElkContainer(injection);
        elkContainer.addBean(Titanic.class);
        elkContainer.addBean(Hollywood.class);
        elkContainer.addBean(Director.class);

        Director director = injection.buildBeanWithDependencies(Director.class, elkContainer);

        assertThat(director, notNullValue());
    }
}
