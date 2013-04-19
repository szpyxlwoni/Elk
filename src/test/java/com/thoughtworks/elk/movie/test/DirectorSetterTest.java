package com.thoughtworks.elk.movie.test;

import com.thoughtworks.elk.movie.DirectorSetter;
import com.thoughtworks.elk.movie.Titanic;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

public class DirectorSetterTest {
    @Test
    public void should_get_movie() {
        DirectorSetter directorSetter = new DirectorSetter();

        directorSetter.setMovie(new Titanic());

        assertThat(directorSetter.getMovie(), notNullValue());
    }
}
