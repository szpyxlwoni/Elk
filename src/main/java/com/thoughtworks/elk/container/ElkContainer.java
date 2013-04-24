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

    private <T> T buildBeanWithDependencies(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            if (isParameterAllInBeanList(constructors[i].getParameterTypes())) {
                return (T) constructors[i].newInstance(getDependenciesObject(constructors[i].getParameterTypes()));
            }
        }
        return null;
    }

    public Class findOneImplementClass(final Class clazz) {
        if (isImplementHaveBeenContained(clazz, classList)) return (Class) findImplementClasses(clazz, classList).toArray()[0];

        return findImplementClassesInAncestor(clazz);
    }

    private Class findImplementClassesInAncestor(Class clazz) {
        ElkContainer parentContainer = parent;
        while (parentContainer != null) {
            if (isImplementHaveBeenContained(clazz, parentContainer.classList)) {
                return (Class) findImplementClasses(clazz, parentContainer.classList).toArray()[0];
            }
            if (parentContainer.parent == null) {
                return null;
            }
            parentContainer = parentContainer.parent;
        }
        return null;
    }

    private boolean isImplementHaveBeenContained(Class clazz, HashSet currentList) {
        return findImplementClasses(clazz, currentList).size() == 1;
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
        if (isInClassList(clazz, classList) || isAncestorContains(clazz)) {
            return true;
        }
        return false;
    }

    private boolean isInClassList(Class clazz, HashSet<Class> currentList) {
        return isClassHaveBeenContained(clazz, currentList) || isImplementHaveBeenContained(clazz, currentList);
    }

    private boolean isClassHaveBeenContained(Class clazz, HashSet currentList) {
        return findClasses(clazz, currentList).size() == 1;
    }

    private Set<Class> findImplementClasses(final Class clazz, HashSet<Class> currentList) {
        return filter(currentList, new Predicate<Class>() {
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

    private <T> Set<Class> findClasses(final Class<T> clazz, HashSet<Class> currentList) {
        return filter(currentList, new Predicate<Class>() {
            @Override
            public boolean apply(@Nullable Class clazzInContainer) {
                return clazzInContainer == clazz;
            }
        });
    }

    public boolean isAncestorContains(Class clazz) {
        ElkContainer parentContainer = parent;
        while (parentContainer != null) {
            if (isInClassList(clazz, parentContainer.classList)) {
                return true;
            }
            if (parentContainer.parent == null) {
                return false;
            }
            parentContainer = parentContainer.parent;
        }
        return false;
    }

    public void addChildContainer(ElkContainer childContainer) {
        if (children == null) {
            children = new HashSet<ElkContainer>();
        }
        children.add(childContainer);
        childContainer.parent = this;
    }
}
