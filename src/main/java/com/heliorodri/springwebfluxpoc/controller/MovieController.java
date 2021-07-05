package com.heliorodri.springwebfluxpoc.controller;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import com.heliorodri.springwebfluxpoc.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("movies")
@Slf4j
@RequiredArgsConstructor
public class MovieController {

    private final MovieRepository repository;

    @GetMapping
    public Flux<Movie> listAll() {
        log.info("Getting all movies");
        return repository.findAll();
    }

}