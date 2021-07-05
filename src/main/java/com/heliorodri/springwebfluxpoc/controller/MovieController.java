package com.heliorodri.springwebfluxpoc.controller;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import com.heliorodri.springwebfluxpoc.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService service;

    @GetMapping
    public Flux<Movie> listAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Movie> findById(@PathVariable int id){
        return service.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, "Movie with id " + id + " not found")));
    }

}