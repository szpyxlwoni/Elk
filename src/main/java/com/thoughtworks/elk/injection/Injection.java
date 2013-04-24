package com.thoughtworks.elk.injection;

import com.thoughtworks.elk.container.ElkContainer;

import java.lang.reflect.InvocationTargetException;

public interface Injection {
    public <T> T buildBeanWithDependencies(Class<T> clazz, ElkContainer elkContainer)
            throws InstantiationException, IllegalAccessException, InvocationTargetException;
}
