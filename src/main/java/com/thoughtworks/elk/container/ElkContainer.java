package com.thoughtworks.elk.container;

import com.thoughtworks.elk.movie.Director;
import com.thoughtworks.elk.movie.Movie;
import com.thoughtworks.elk.movie.Titanic;

import java.util.HashMap;

import static com.google.common.collect.Maps.newHashMap;

public class ElkContainer {
    static HashMap beanList = newHashMap();

    public ElkContainer() {
        this.add("movie", new Titanic());
        this.add("director", new Director((Movie) this.getBean("movie")));
    }

    private void add(String beanName, Object bean) {
        beanList.put(beanName, bean);
    }

    public Object getBean(String beanName) {
        return beanList.get(beanName);
    }
}
