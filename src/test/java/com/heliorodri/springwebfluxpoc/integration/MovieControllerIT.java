package com.heliorodri.springwebfluxpoc.integration;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import com.heliorodri.springwebfluxpoc.repository.MovieRepository;
import com.heliorodri.springwebfluxpoc.service.MovieService;
import com.heliorodri.springwebfluxpoc.service.util.MovieTestBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
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
@WebFluxTest
@Import(MovieService.class)
public class MovieControllerIT {

    @MockBean
    private MovieRepository repository;

    @Autowired
    private WebTestClient testClient;

    private final Movie movie = buildValidMovie();

    @BeforeAll
    public static void blockHoundSetup(){
        BlockHound.install();
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

    @BeforeEach
    public void setUp() {
        when(repository.findAll()).thenReturn(Flux.just(movie));
    }

    @Test
    @DisplayName("it should return all movies(flux) with success")
    public void itShouldReturnFluxOfAllMovies(){
        testClient
                .get()
                .uri("/movies")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Movie.class)
                .hasSize(1)
                .contains(movie);
    }

}