package com.thoughtworks.elk.container;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.sun.istack.internal.Nullable;
import com.thoughtworks.elk.container.exception.ElkContainerException;
import com.thoughtworks.elk.container.exception.ElkParseException;
import com.thoughtworks.elk.injection.ConstructorInjection;
import com.thoughtworks.elk.injection.Injection;
import com.thoughtworks.elk.injection.SetterInjection;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;

public class ElkContainer {
    private final ConfigFileParser configParser;
    private final Injection injection;
    private final HashSet<Class> classList;
    private HashMap beanList = newHashMap();

    private HashSet<ElkContainer> children = null;
    private ElkContainer parent = null;

    public ElkContainer(Injection injection) {
        this.injection = injection;
        configParser = null;
        classList = newHashSet();
    }

    public ElkContainer(String configFilePath) {
        configParser = new ConfigFileParser(configFilePath);
        injection = configParser.getInjection();
        classList = configParser.getClassList();
    }

    public void register(Class clazz) {
        classList.add(clazz);
    }

    public <T> T getBean(final Class<T> clazz) throws ElkContainerException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (!validScope(clazz)) return null;

        if (clazz.isInterface()) return (T) getBean(findOneImplementClass(clazz));

        if (beanList.get(clazz) == null) {
            T instance = injection.buildBeanWithDependencies(clazz, this);
            beanList.put(clazz, instance);
            return instance;
        }

        return (T) beanList.get(clazz);
    }

    public Class findOneImplementClass(final Class clazz) {
        return visit(this, new ContainerVisitor<Class>() {
            @Override
            public boolean isAppropriateContainer(ElkContainer container) {
                return isImplementHaveBeenContained(clazz, container.classList);
            }

            @Override
            public Class returnWhenAppropriateContainerFound(ElkContainer container) {
                return (Class) findImplementClasses(clazz, container.classList).toArray()[0];
            }

            @Override
            public Class returnWhenNoMoreParentContainer() {
                return null;
            }
        });
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

    public boolean isParameterAllInClassList(Class<?>[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!validScope(parameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean validScope(final Class clazz) {
        return visit(this, new ContainerVisitor<Boolean>() {
            @Override
            public boolean isAppropriateContainer(ElkContainer container) {
                return isClassHaveBeenContained(clazz, container.classList)
                        || isImplementHaveBeenContained(clazz, container.classList);
            }

            @Override
            public Boolean returnWhenAppropriateContainerFound(ElkContainer container) {
                return true;
            }

            @Override
            public Boolean returnWhenNoMoreParentContainer() {
                return false;
            }
        });
    }

    private <T> T visit(ElkContainer container, ContainerVisitor<T> visitor) {
        ElkContainer currentContainer = container;
        while (currentContainer != null) {
            if (visitor.isAppropriateContainer(currentContainer)) {
                return visitor.returnWhenAppropriateContainerFound(currentContainer);
            }
            if (currentContainer.parent == null) {
                return visitor.returnWhenNoMoreParentContainer();
            }
            currentContainer = currentContainer.parent;
        }
        return visitor.returnWhenNoMoreParentContainer();
    }

    interface ContainerVisitor<RETURN_TYPE> {
        boolean isAppropriateContainer(ElkContainer container);
        RETURN_TYPE returnWhenAppropriateContainerFound(ElkContainer container);
        RETURN_TYPE returnWhenNoMoreParentContainer();
    }

    private boolean isClassHaveBeenContained(final Class clazz, HashSet currentList) {
        return filter(currentList, new Predicate<Class>() {
            @Override
            public boolean apply(@Nullable Class clazzInContainer) {
                return clazzInContainer == clazz;
            }
        }).size() == 1;
    }

    public Object[] getDependenciesObject(Class<?>[] classes) {
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
