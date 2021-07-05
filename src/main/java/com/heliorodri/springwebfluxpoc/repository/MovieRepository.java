package com.heliorodri.springwebfluxpoc.repository;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MovieRepository extends ReactiveCrudRepository<Movie, Integer> {}