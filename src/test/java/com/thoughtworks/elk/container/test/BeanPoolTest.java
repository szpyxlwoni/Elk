package com.thoughtworks.elk.container.test;

import com.thoughtworks.elk.container.BeanPool;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

public class BeanPoolTest {
    @Test
    public void should_register_bean_to_bean_pool() {
        BeanPool beanPool = new BeanPool();
        beanPool.register("movie", "com.thoughtworks.elk.movie.Titanic");
        assertThat(beanPool.getBean("movie"), notNullValue());
    }
}
