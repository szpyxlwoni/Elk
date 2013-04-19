package com.thoughtworks.elk.container;

import com.google.common.base.Function;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;

public class ElkContainer {

    private HashMap<String, Object> objectList = newHashMap();
    private final ConfigXmlParser configParser;

    public ElkContainer(String configFilePath) {
        configParser = new ConfigXmlParser(configFilePath);
    }

    public Object getBean(String beanId) {
        try {
            Class<?> beanClass = Class.forName(configParser.getBeanClass(beanId));
            List dependencies = configParser.getConstructorDependenciesClass(beanId);
            if (dependencies.size() == 0) {
                objectList.put(beanId, beanClass.newInstance());
            } else {
                Constructor<?> declaredConstructor = beanClass.getDeclaredConstructor(toClassArray(dependencies));
                Object object = declaredConstructor.newInstance(toObjectArray(configParser.getConstructorDependenciesName(beanId)));
                objectList.put(beanId, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectList.get(beanId);
    }

    private Object[] toObjectArray(List constructorDependenciesName) {
        return transform(constructorDependenciesName, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable java.lang.Object o) {
                return getBean((String) o);
            }
        }).toArray(new Object[0]);
    }

    private Class[] toClassArray(List dependencies) {
        return (Class[]) transform(dependencies, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object o) {
                try {
                    return Class.forName((String) o);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).toArray(new Class[0]);
    }

}
