package com.thoughtworks.elk.movie;

public class Director {

    private Movie movie;

    public Director(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }
}
