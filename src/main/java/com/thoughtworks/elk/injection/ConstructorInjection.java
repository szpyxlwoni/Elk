package com.thoughtworks.elk.injection;

import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.container.Injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConstructorInjection implements Injection {
    public <T> T buildBeanWithDependencies(Class<T> clazz, ElkContainer elkContainer)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            if (elkContainer.isParameterAllInBeanList(constructors[i].getParameterTypes())) {
                return (T) constructors[i].newInstance(elkContainer.getDependenciesObject(constructors[i].getParameterTypes()));
            }
        }
        return null;
    }
}
