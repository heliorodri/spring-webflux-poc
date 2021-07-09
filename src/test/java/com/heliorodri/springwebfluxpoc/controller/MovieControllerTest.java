package com.heliorodri.springwebfluxpoc.controller;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import com.heliorodri.springwebfluxpoc.service.MovieService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static com.heliorodri.springwebfluxpoc.service.util.MovieTestBuilder.buildValidMovie;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class MovieControllerTest{

    @InjectMocks
    private MovieController controller;

    @Mock
    private static MovieService service;

    private static final Movie movie = buildValidMovie();

    @BeforeAll
    public static void blockHoundSetup(){
        BlockHound.install();
    }

    @BeforeEach
    public void setUp(){
        when(service.findAll()).thenReturn(Flux.just(movie));
    }

    @Test
    public void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(0);
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

    @Test
    @DisplayName("it should return all movies(flux) with success")
    public void itShouldReturnFluxOfAllMovies(){
        StepVerifier.create(controller.listAll())
                .expectSubscription()
                .expectNext(movie)
                .verifyComplete();
    }

}