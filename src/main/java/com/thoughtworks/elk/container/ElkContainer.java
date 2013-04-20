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
        callSetterInjection(beanId, beanClass);
    }

    private void callSetterInjection(String beanId, Class<?> beanClass) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, ElkContainerException {
        List<Property> properties = configParser.getProperties(beanId);
        for (int i = 0; i < properties.size(); i++) {
            Property property = properties.get(i);
            Class<?> parameterClass = Class.forName(property.getType());
            Method declaredMethod = beanClass.getDeclaredMethod(changeNameToSetMethod(property.getName()), parameterClass);
            declaredMethod.invoke(objectList.get(beanId), getBean(property.getRef()));
        }
    }

    private String changeNameToSetMethod(String propertyName) {
        return "set" + propertyName.replaceFirst(propertyName.charAt(0) + "", String.valueOf(propertyName.charAt(0)).toUpperCase());
    }

    private void buildWithDependencies(String beanId, Class<?> beanClass, List dependencies) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> declaredConstructor = beanClass.getDeclaredConstructor(getDependenciesClass(dependencies));
        Object object = declaredConstructor.newInstance(getDependenciesObject(configParser.getConstructorDependenciesName(beanId)));
        objectList.put(beanId, object);
    }

    private Object[] getDependenciesObject(List constructorDependenciesName) {
        return transform(constructorDependenciesName, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable java.lang.Object o) {
                try {
                    return getBean((String) o);
                } catch (ElkContainerException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).toArray(new Object[0]);
    }

    private Class[] getDependenciesClass(List dependencies) {
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
