package com.thoughtworks.elk.movie.test;

import com.thoughtworks.elk.movie.Director;
import com.thoughtworks.elk.movie.Hollywood;
import com.thoughtworks.elk.movie.Movie;
import com.thoughtworks.elk.movie.Titanic;
import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class DirectorTest {
    @Test
    public void should_get_his_movie() {
        Director director = new Director(new Titanic(), new Hollywood());
        Movie movie = director.getMovie();
        assertThat(movie, notNullValue());
    }
}
