package com.thoughtworks.elk.container;

import java.lang.reflect.Constructor;
import java.util.HashMap;

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
        Bean refBean = (Bean) beanList.get(bean.getRef());
        Object refObject = getBean(refBean.getId());
        try {
            Constructor<?> declaredConstructor = beanClass.getDeclaredConstructor(Class.forName(bean.getType()));
            beanPool.put(bean.getId(), declaredConstructor.newInstance(refObject));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanPool.get(bean.getId());
    }

    public void register(String id, String clazz, String ref, String type) {
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