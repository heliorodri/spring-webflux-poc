package com.heliorodri.springwebfluxpoc.service;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import com.heliorodri.springwebfluxpoc.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository repository;

    public Flux<Movie> findAll(){
        return repository.findAll();
    }

}