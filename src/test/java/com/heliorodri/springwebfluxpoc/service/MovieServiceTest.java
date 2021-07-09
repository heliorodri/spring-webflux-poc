package com.heliorodri.springwebfluxpoc.service;

import com.heliorodri.springwebfluxpoc.domain.Movie;
import com.heliorodri.springwebfluxpoc.repository.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class MovieServiceTest {

    @InjectMocks
    private MovieService service;

    @Mock
    private static MovieRepository repository;

    private static final Movie movie = buildValidMovie();

    @BeforeAll
    public static void blockHoundSetup(){
        BlockHound.install(builder ->
                builder.allowBlockingCallsInside("java.util.UUID", "randomUUID"));
    }

    @BeforeEach
    public void setUp(){
        when(repository.findAll()).thenReturn(Flux.just(movie));
        when(repository.findById(anyInt())).thenReturn(Mono.just(movie));
        when(repository.save(buildMovieToBeSaved())).thenReturn(Mono.just(movie));
        when(repository.delete(any(Movie.class))).thenReturn(Mono.empty());

        when(repository.saveAll(Arrays.asList(buildMovieToBeSaved(), buildMovieToBeSaved())))
                .thenReturn(Flux.just(movie, movie));
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
        StepVerifier.create(service.findAll())
                .expectSubscription()
                .expectNext(movie)
                .verifyComplete();
    }

    @Test
    @DisplayName("it should find a movie by its id with success")
    public void itShouldReturnMonoMovieById(){
        StepVerifier.create(service.findById(MOVIE_ID))
                .expectSubscription()
                .expectNext(movie)
                .verifyComplete();
    }

    @Test
    @DisplayName("it should return a error when looking for a movieId that does not exists")
    public void itShouldReturnErrorWhenIdNotFound(){
        int movieNotFoundId = 2;

        when(repository.findById(movieNotFoundId)).thenReturn(Mono.empty());

        StepVerifier.create(service.findById(movieNotFoundId))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("it should save a movies with success")
    public void itShouldSaveTheMovieWithSuccess(){
        Movie movieToSave = buildMovieToBeSaved();

        StepVerifier.create(service.save(movieToSave))
                .expectSubscription()
                .expectNext(movie)
                .verifyComplete();
    }

    @Test
    @DisplayName("it should delete a movie -referenced by id- with success")
    public void itShouldDeleteTheMovieWithSuccess(){
        int idMovieToBeRemoved = 1;

        StepVerifier.create(service.delete(idMovieToBeRemoved))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("it should return error when trying to delete movie which does not exists")
    public void itShouldReturnErroWhenMovieIsNotFoundForDelete(){
        int movieNotFoundId = 2;

        when(repository.findById(movieNotFoundId)).thenReturn(Mono.empty());

        StepVerifier.create(service.delete(movieNotFoundId))
                .expectSubscription()
                .expectError()
                .verify();
    }

    @Test
    @DisplayName("it should update a movie with success")
    public void itShouldUpdateTheMovieWithSuccess(){
        when(repository.save(buildMovieToBeUpdated())).thenReturn(Mono.empty());

        StepVerifier.create(service.update(MOVIE_ID, buildMovieToBeUpdated()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("it should return error when trying to update a movie that does not exists")
    public void itShouldReturnErrorWhenMovieToUpdateDoesNotExists(){
        when(repository.findById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(service.update(MOVIE_ID, buildMovieToBeUpdated()))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("it should save a list of movies with success")
    public void itShouldSaveListOfMoviesWithSuccess(){
        List<Movie> moviesToSave = Arrays.asList(buildMovieToBeSaved(), buildMovieToBeSaved());

        StepVerifier.create(service.saveAll(moviesToSave))
                .expectSubscription()
                .expectNext(movie, movie)
                .verifyComplete();
    }

    @Test
    @DisplayName("it should not save a list of movies when at least one movie name is empty")
    public void itShouldNotSaveListOfMoviesWhenEmptyName(){
        List<Movie> moviesToSave = Arrays.asList(
                buildMovieToBeSaved(),
                buildMovieToBeSaved().withName("")
        );

        when(repository.saveAll(moviesToSave)).thenReturn(Flux.just(movie, movie.withName("")));

        StepVerifier.create(service.saveAll(moviesToSave))
                .expectSubscription()
                .expectNext(movie)
                .expectError()
                .verify();
    }

}