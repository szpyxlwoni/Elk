package com.thoughtworks.elk.container;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.sun.istack.internal.Nullable;
import com.thoughtworks.elk.container.exception.ElkContainerException;
import com.thoughtworks.elk.container.exception.ElkParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;

public class ElkContainer {

    private final HashMap<String, Object> objectList = newHashMap();
    private final ConfigXmlParser configParser;
    private HashSet<Class> classList = newHashSet();

    public ElkContainer(String configFile) throws ElkParseException {
        configParser = new ConfigXmlParser(configFile);
    }

    public Object getBean(String beanId) throws ElkContainerException {
        if (objectList.get(beanId) == null) {
            newBean(beanId);
        }
        return objectList.get(beanId);
    }

    private void newBean(String beanId) throws ElkContainerException {
        try {
            Class<?> beanClass = Class.forName(configParser.getBeanClass(beanId));
            List dependencies = configParser.getConstructorDependenciesClass(beanId);
            if (dependencies.size() == 0) {
                buildWithSetterOrNoDependencies(beanId, beanClass);
            } else {
                buildWithConstructor(beanId, beanClass, dependencies);
            }
        } catch (Exception e) {
            throw new ElkContainerException(e.getMessage());
        }
    }

    private void buildWithSetterOrNoDependencies(String beanId, Class<?> beanClass) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException, ElkContainerException, ClassNotFoundException {
        objectList.put(beanId, beanClass.newInstance());
        List<Property> properties = configParser.getProperties(beanId);
        if (properties.size() != 0) {
            callSetterInjection(beanId, beanClass, properties);
        }
    }

    private void callSetterInjection(String beanId, Class<?> beanClass, List<Property> properties)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, ElkContainerException {
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

    private void buildWithConstructor(String beanId, Class<?> beanClass, List dependencies) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> declaredConstructor = beanClass.getDeclaredConstructor(configParser.getDependenciesClass(dependencies));
        Object object = declaredConstructor.newInstance(getDependenciesObject(configParser.getConstructorDependenciesName(beanId)));
        objectList.put(beanId, object);
    }

    private Object[] getDependenciesObject(List constructorDependenciesName) {
        return transform(constructorDependenciesName, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object o) {
                try {
                    return getBean((String) o);
                } catch (ElkContainerException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).toArray(new Object[0]);
    }

    public void addBean(Class clazz) {
        classList.add(clazz);
    }

    public <T> T getBean(final Class<T> clazz) throws ElkContainerException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?>[] constructors = clazz.getConstructors();
        ArrayList<Class> classes = newArrayList();
        for (int i = 0; i < constructors.length; i++) {
            Class<?>[] parameterTypes = constructors[i].getParameterTypes();
            for (int j = 0; j < parameterTypes.length; j++) {
                Set<Class> dependency = filterDependencies(parameterTypes[j]);
                if (dependency.size() == 1) {
                    classes.add((Class) dependency.toArray()[0]);
                }
            }
            if (parameterTypes.length == classes.size()) {
                return (T) constructors[i].newInstance(getDependenciesObject(classes));
            }
            classes.clear();
        }
        return null;
    }

    private Object[] getDependenciesObject(ArrayList<Class> classesArr) {
        return transform(classesArr, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object o) {
                try {
                    return getBean((Class<Object>) o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).toArray();
    }

    private <T> Set<Class> filterDependencies(final Class<T> clazz) {
        return filter(classList, new Predicate<Class>() {
            @Override
            public boolean apply(@Nullable Class clazzInContainer) {
                return clazzInContainer == clazz || Arrays.asList(clazzInContainer.getInterfaces()).contains(clazz);
            }
        });
    }
}
