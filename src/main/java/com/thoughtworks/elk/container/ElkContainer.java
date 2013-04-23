package com.thoughtworks.elk.container;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.sun.istack.internal.Nullable;
import com.thoughtworks.elk.container.exception.ElkContainerException;
import com.thoughtworks.elk.container.exception.ElkParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;

public class ElkContainer {

    private final HashMap<String, Object> objectList = newHashMap();
    private final ConfigXmlParser configParser;
    private HashSet<Class> classList = newHashSet();
    private HashMap beanList = newHashMap();

    public ElkContainer() {
        configParser = null;
    }

    public ElkContainer(String configFile) throws ElkParseException {
        configParser = new ConfigXmlParser(configFile);
    }

    public void addBean(Class clazz) {
        classList.add(clazz);
    }

    public <T> T getBean(final Class<T> clazz) throws ElkContainerException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (!classList.contains(clazz)) {
            return null;
        }
        if (constructors.length == 0) {
            return (T) getBean(findOneImplementClass(clazz));
        }
        if (beanList.get(clazz) == null) {
            buildBeanWithDependencies(clazz, constructors);
        }
        return (T) beanList.get(clazz);
    }

    private <T> void buildBeanWithDependencies(Class<T> clazz, Constructor<?>[] constructors) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        for (int i = 0; i < constructors.length; i++) {
            if (isParameterAllInBeanList(constructors[i].getParameterTypes())) {
                beanList.put(clazz, constructors[i].newInstance(getDependenciesObject(constructors[i].getParameterTypes())));
                break;
            }
        }
    }

    public Class findOneImplementClass(final Class clazz) {
        Set<Class> classes = findImplementClasses(clazz);
        if (classes.size() == 1) {
            return (Class) classes.toArray()[0];
        }
        return null;
    }

    private Set<Class> findImplementClasses(final Class clazz) {
        return filter(classList, new Predicate<Class>() {
            @Override
            public boolean apply(@Nullable Class clazzInContainer) {
                return Arrays.asList(clazzInContainer.getInterfaces()).contains(clazz);
            }
        });
    }

    private Object[] getDependenciesObject(Class<?>[] classes) {
        return transform(Arrays.asList(classes), new Function<Object, Object>() {
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

    public boolean isParameterAllInBeanList(Class<?>[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++) {
            if (filterDependencies(parameterTypes[i]).size() == 0) {
                return false;
            }
        }
        return true;
    }

    public void addChildContainer(ElkContainer childContainer) {
    }
}
