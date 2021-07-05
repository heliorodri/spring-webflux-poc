package com.heliorodri.springwebfluxpoc.repository;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MovieRepository extends ReactiveCrudRepository<Movie, Integer> {

    Mono<Movie> findById(int id);

}