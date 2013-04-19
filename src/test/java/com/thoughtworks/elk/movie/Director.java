package com.thoughtworks.elk.movie;

public class Director {

    private Movie movie;

    public Director(Movie movie, Company company) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }
}
