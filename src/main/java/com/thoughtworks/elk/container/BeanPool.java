package com.thoughtworks.elk.container;

import com.google.common.base.Function;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;

public class BeanPool {
    private HashMap beanList = newHashMap();
    private HashMap<String, Object> beanPool = newHashMap();

    public Object getBean(String beanId) {
        if (beanPool.get(beanId) != null) {
            return beanPool.get(beanId);
        }
        try {
            Bean bean = (Bean) beanList.get(beanId);
            Class<?> beanClass = Class.forName(bean.getClazz());
            return newInstance(bean, beanClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object newInstance(Bean bean, Class<?> beanClass) {
        if (bean.getRef() == null) {
            try {
                beanPool.put(bean.getId(), beanClass.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return beanPool.get(bean.getId());
        }
        return newInstanceByRef(bean, beanClass);
    }

    private Object newInstanceByRef(Bean bean, Class<?> beanClass) {
        ArrayList refObjects = newArrayList();
        for (Object refBeanId : bean.getRef()) {
            Bean refBean = (Bean) beanList.get(refBeanId);
            Object refObject = getBean(refBean.getId());
            refObjects.add(refObject);
        }
        try {
            Class[] classes = (Class[]) transform(bean.getType(), new Function() {
                @Override
                public Object apply(@Nullable java.lang.Object o) {
                    try {
                        return Class.forName(String.valueOf(o));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }).toArray(new Class[0]);
            Constructor<?> declaredConstructor = beanClass.getDeclaredConstructor(classes);
            beanPool.put(bean.getId(), declaredConstructor.newInstance(refObjects.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanPool.get(bean.getId());
    }

    public void register(String id, String clazz, ArrayList ref, ArrayList type) {
        Bean bean = new Bean();
        bean.setId(id);
        bean.setClazz(clazz);
        bean.setRef(ref);
        bean.setType(type);

        beanList.put(id, bean);
    }

    public void register(String id, String clazz) {
        register(id, clazz, null, null);
    }
}