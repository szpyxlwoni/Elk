package com.thoughtworks.elk.container;

import com.google.common.base.Function;
import com.sun.istack.internal.Nullable;
import com.thoughtworks.elk.container.exception.ElkContainerException;
import com.thoughtworks.elk.container.exception.ElkParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;

public class ElkContainer {

    private HashMap<String, Object> objectList = newHashMap();
    private final ConfigXmlParser configParser;

    public ElkContainer(String configFilePath) throws ElkParseException {
        configParser = new ConfigXmlParser(configFilePath);
    }

    public Object getBean(String beanId) throws ElkContainerException {
        if (objectList.get(beanId) != null) {
            return objectList.get(beanId);
        }
        try {
            Class<?> beanClass = Class.forName(configParser.getBeanClass(beanId));
            List dependencies = configParser.getConstructorDependenciesClass(beanId);
            if (dependencies.size() == 0) {
                buildWithoutDependencies(beanId, beanClass);
            } else {
                buildWithDependencies(beanId, beanClass, dependencies);
            }
        } catch (Exception e) {
            throw new ElkContainerException(e.getMessage());
        }
        return objectList.get(beanId);
    }

    private void buildWithoutDependencies(String beanId, Class<?> beanClass) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException, ElkContainerException, ClassNotFoundException {
        objectList.put(beanId, beanClass.newInstance());
        List<String> propertiesName = configParser.getPropertiesName(beanId);
        List<String> propertiesRef = configParser.getPropertiesRef(beanId);
        List<String> propertiesType = configParser.getPropertiesType(beanId);
        for (int i = 0; i < propertiesName.size(); i++) {
            String propertyName = propertiesName.get(i);
            propertyName = "set" + propertyName.replaceFirst(propertyName.charAt(0) + "", String.valueOf(propertyName.charAt(0)).toUpperCase());
            Method declaredMethod = beanClass.getDeclaredMethod(propertyName, Class.forName(configParser.getBeanClass(propertiesType.get(i))));
            declaredMethod.invoke(getBean(propertiesRef.get(i)));
        }
    }

    private void buildWithDependencies(String beanId, Class<?> beanClass, List dependencies) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> declaredConstructor = beanClass.getDeclaredConstructor(toClassArray(dependencies));
        Object object = declaredConstructor.newInstance(toObjectArray(configParser.getConstructorDependenciesName(beanId)));
        objectList.put(beanId, object);
    }

    private Object[] toObjectArray(List constructorDependenciesName) {
        return transform(constructorDependenciesName, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable java.lang.Object o) {
                try {
                    return getBean((String) o);
                } catch (ElkContainerException e) {
                    e.printStackTrace();
                    return null;
                }
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
