package com.heliorodri.springwebfluxpoc.integration;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import com.heliorodri.springwebfluxpoc.repository.MovieRepository;
import com.heliorodri.springwebfluxpoc.service.MovieService;
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
import org.springframework.web.reactive.function.BodyInserters;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static com.heliorodri.springwebfluxpoc.service.util.MovieTestBuilder.MOVIE_ID;
import static com.heliorodri.springwebfluxpoc.service.util.MovieTestBuilder.buildMovieToBeSaved;
import static com.heliorodri.springwebfluxpoc.service.util.MovieTestBuilder.buildMovieToBeUpdated;
import static com.heliorodri.springwebfluxpoc.service.util.MovieTestBuilder.buildValidMovie;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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
        when(repository.findById(anyInt())).thenReturn(Mono.just(movie));
        when(repository.save(buildMovieToBeSaved())).thenReturn(Mono.just(movie));
        when(repository.delete(any(Movie.class))).thenReturn(Mono.empty());
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

    @Test
    @DisplayName("it should find a movie by its id with success")
    public void itShouldReturnMonoMovieById(){
        testClient
                .get()
                .uri("/movies/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .isEqualTo(movie);
    }

    @Test
    @DisplayName("it should return a error when looking for a movieId that does not exists")
    public void itShouldReturnErrorWhenIdNotFound(){
        when(repository.findById(4)).thenReturn(Mono.empty());

        testClient
                .get()
                .uri("/movies/4")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    @DisplayName("it should save a movies with success")
    public void itShouldSaveTheMovieWithSuccess(){
        Movie movieToSave = buildMovieToBeSaved();

        testClient
                .post()
                .uri("/movies")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(movieToSave))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Movie.class)
                .isEqualTo(movie);
    }

    @Test
    @DisplayName("it should return error when saving a movies with no name")
    public void itShouldReturnErrorWhenSavingMovieWithNoName(){
        Movie movieToSave = buildMovieToBeSaved().withName("");

        testClient
                .post()
                .uri("/movies/")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(movieToSave))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    @DisplayName("it should delete a movie -referenced by id- with success")
    public void itShouldDeleteTheMovieWithSuccess(){
        int idMovieToBeRemoved = 1;

        testClient
                .delete()
                .uri("/movies/{id}", idMovieToBeRemoved)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("it should return error when trying to delete movie which does not exists")
    public void itShouldReturnErroWhenMovieIsNotFoundForDelete(){
        int movieNotFoundId = 2;

        when(repository.findById(movieNotFoundId)).thenReturn(Mono.empty());

        testClient
                .delete()
                .uri("/movies/{id}", movieNotFoundId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    @DisplayName("it should update a movie with success")
    public void itShouldUpdateTheMovieWithSuccess(){
        Movie movieToUpdate = buildMovieToBeUpdated();
        when(repository.save(buildMovieToBeUpdated())).thenReturn(Mono.empty());

        testClient
                .put()
                .uri("/movies/{id}", MOVIE_ID)
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(movieToUpdate))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status", 200);
    }

    @Test
    @DisplayName("it should return error when trying to update a movie that does not exists")
    public void itShouldReturnErrorWhenUpdatingMovieIsNotFound(){
        Movie movieToUpdate = buildMovieToBeUpdated();
        int idMovieNotFound = 2;

        when(repository.findById(2)).thenReturn(Mono.empty());

        testClient
                .put()
                .uri("/movies/{id}", idMovieNotFound)
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(movieToUpdate))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status", 404);
    }

}
