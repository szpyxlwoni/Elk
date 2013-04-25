package com.thoughtworks.elk.injection;

import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.container.exception.ElkContainerException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SetterInjection implements Injection {
    @Override
    public <T> T buildBeanWithDependencies(Class<T> clazz, ElkContainer elkContainer)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Method[] methods = clazz.getMethods();
        T instance = clazz.newInstance();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().startsWith("set") && elkContainer.isParameterAllInClassList(methods[i].getParameterTypes())) {
                try {
                    methods[i].invoke(instance, elkContainer.getBean(methods[i].getParameterTypes()[0]));
                } catch (ElkContainerException e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }
}
