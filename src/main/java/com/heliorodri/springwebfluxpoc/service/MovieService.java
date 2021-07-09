package com.heliorodri.springwebfluxpoc.service;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import com.heliorodri.springwebfluxpoc.repository.MovieRepository;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository repository;

    public Flux<Movie> findAll(){
        return repository.findAll();
    }

    public Mono<Movie> findById(int id){
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, "Movie with id " + id + " not found")));
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

    @Transactional
    public Flux<Movie> saveAll(List<Movie> movies) {
        return repository.saveAll(movies)
                .doOnNext(this::throwExceptionWhenEmptyName);
    }

    private void throwExceptionWhenEmptyName(Movie movie){
        if (StringUtil.isNullOrEmpty(movie.getName())){
            throw new ResponseStatusException(BAD_REQUEST, "Invalid Name");
        }
    }
}