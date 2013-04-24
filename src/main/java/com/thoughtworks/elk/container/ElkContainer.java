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
    private final ConfigXmlParser configParser;
    private HashSet<Class> classList = newHashSet();
    private HashMap beanList = newHashMap();

    private HashSet<ElkContainer> children = null;
    private ElkContainer parent = null;

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
        if (!validScope(clazz)) return null;

        if (clazz.isInterface()) return (T) getBean(findOneImplementClass(clazz));

        if (beanList.get(clazz) == null) return buildBeanWithDependencies(clazz);

        return (T) beanList.get(clazz);
    }

    public Class findOneImplementClass(final Class clazz) {
        ElkContainer currentContainer = this;
        while (currentContainer != null) {
            if (isImplementHaveBeenContained(clazz, currentContainer.classList)) {
                return (Class) findImplementClasses(clazz, currentContainer.classList).toArray()[0];
            }

            if (currentContainer.parent == null) return null;

            currentContainer = currentContainer.parent;
        }
        return null;
    }

    private boolean isImplementHaveBeenContained(Class clazz, HashSet currentList) {
        return findImplementClasses(clazz, currentList).size() == 1;
    }

    private Set<Class> findImplementClasses(final Class clazz, HashSet<Class> currentList) {
        return filter(currentList, new Predicate<Class>() {
            @Override
            public boolean apply(@Nullable Class clazzInContainer) {
                return Arrays.asList(clazzInContainer.getInterfaces()).contains(clazz);
            }
        });
    }

    private <T> T buildBeanWithDependencies(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            if (isParameterAllInBeanList(constructors[i].getParameterTypes())) {
                return (T) constructors[i].newInstance(getDependenciesObject(constructors[i].getParameterTypes()));
            }
        }
        return null;
    }

    public boolean isParameterAllInBeanList(Class<?>[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!validScope(parameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

    public <T> boolean validScope(Class<T> clazz) {
        ElkContainer currentContainer = this;
        while (currentContainer != null) {
            if (isClassHaveBeenContained(clazz, currentContainer.classList) || isImplementHaveBeenContained(clazz, currentContainer.classList)) {
                return true;
            }
            if (currentContainer.parent == null) {
                return false;
            }
            currentContainer = currentContainer.parent;
        }
        return false;
    }

    private boolean isClassHaveBeenContained(final Class clazz, HashSet currentList) {
        return filter(currentList, new Predicate<Class>() {
            @Override
            public boolean apply(@Nullable Class clazzInContainer) {
                return clazzInContainer == clazz;
            }
        }).size() == 1;
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

    public void addChildContainer(ElkContainer childContainer) {
        if (children == null) {
            children = new HashSet<ElkContainer>();
        }
        children.add(childContainer);
        childContainer.parent = this;
    }
}
