package com.heliorodri.springwebfluxpoc.service;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import com.heliorodri.springwebfluxpoc.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository repository;

    public Flux<Movie> findAll(){
        return repository.findAll();
    }

    public Mono<Movie> findById(int id){
        return repository.findById(id);
    }

    public Mono<Movie> save(Movie movie) {
        return repository.save(movie);
    }

    public Mono<Void> update(int id, Movie movie) {
        return findById(id)
                .map(movieToSave -> movieToSave.withName(movie.getName()))
                .flatMap(repository::save)
                .then();
    }

    public Mono<Void> delete(int id) {
        return findById(id).flatMap(repository::delete);
    }
}