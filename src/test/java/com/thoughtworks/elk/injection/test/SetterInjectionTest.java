package com.thoughtworks.elk.injection.test;

import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.injection.Injection;
import com.thoughtworks.elk.injection.SetterInjection;
import com.thoughtworks.elk.movie.DirectorSetter;
import com.thoughtworks.elk.movie.Hollywood;
import com.thoughtworks.elk.movie.Titanic;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class SetterInjectionTest {
    @Test
    public void should_build_a_bean_with_dependencies()
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Injection injection = new SetterInjection();
        ElkContainer elkContainer = new ElkContainer(injection);
        elkContainer.register(Titanic.class);
        elkContainer.register(Hollywood.class);
        elkContainer.register(DirectorSetter.class);

        DirectorSetter director = injection.buildBeanWithDependencies(DirectorSetter.class, elkContainer);

        assertThat(director, notNullValue());
        assertThat(director.getMovie(), notNullValue());
    }
}
