package com.thoughtworks.elk.container.test;

import com.thoughtworks.elk.container.BeanPool;
import com.thoughtworks.elk.container.ElkContainer;
import com.thoughtworks.elk.movie.Director;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

public class ElkContainerTest {
    @Test
    public void should_get_a_movie_given_a_direct() {
        ElkContainer elkContainer = new ElkContainer("src/main/resources/elkContainer.xml");
        Object director = elkContainer.getBean("director");
        assertThat(((Director) director).getMovie(), notNullValue());
    }
}
