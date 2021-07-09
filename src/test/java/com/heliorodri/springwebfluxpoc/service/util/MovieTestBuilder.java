package com.heliorodri.springwebfluxpoc.service.util;

import com.heliorodri.springwebfluxpoc.domain.Movie;

public class MovieTestBuilder {

    private static final int MOVIE_ID = 1;
    private static final String MOVIE_NAME = "Movie Test";

    public static Movie buildMovieToBeSaved() {
        return Movie.builder()
                .name(MOVIE_NAME)
                .build();
    }

    public static Movie buildValidMovie() {
        return Movie.builder()
                .id(MOVIE_ID)
                .name(MOVIE_NAME)
                .build();
    }

    public static Movie buildMovieToBeUpdated() {
        return Movie.builder()
                .id(MOVIE_ID)
                .name(MOVIE_NAME + " updated")
                .build();
    }

    public static Movie buildMovieWithNoName() {
        return Movie.builder()
                .id(MOVIE_ID)
                .name("")
                .build();
    }

}
