package com.heliorodri.springwebfluxpoc.controller;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import com.heliorodri.springwebfluxpoc.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService service;

    @GetMapping
    @ResponseStatus(OK)
    public Flux<Movie> listAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Movie> findById(@PathVariable int id){
        log.info("Looking for movie with id {}", id);
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Mono<Movie> save(@Valid @RequestBody Movie movie) {
        log.info("Saving movie: {}", movie.getName());
        return service.save(movie);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> update(@PathVariable int id, @Valid @RequestBody Movie movie){
        log.info("updating movie with id: {}", id);

        return service.update(id, movie);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable int id){
        log.info("deleting movie with id: {}", id);

        return service.delete(id);
    }

}