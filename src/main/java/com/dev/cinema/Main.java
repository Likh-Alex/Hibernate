package com.dev.cinema;

import com.dev.cinema.exception.AuthenticationException;
import com.dev.cinema.lib.Injector;
import com.dev.cinema.model.CinemaHall;
import com.dev.cinema.model.Movie;
import com.dev.cinema.model.MovieSession;
import com.dev.cinema.model.User;
import com.dev.cinema.security.AuthenticationService;
import com.dev.cinema.service.CinemaHallService;
import com.dev.cinema.service.MovieService;
import com.dev.cinema.service.MovieSessionService;
import com.dev.cinema.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("com.dev.cinema");

    public static void main(String[] args) {
        final MovieService movieService = (MovieService) injector.getInstance(MovieService.class);
        Movie raceMovie = new Movie();
        Movie mask = new Movie();
        Movie robocop = new Movie();
        raceMovie.setTitle("Fast and Furious");
        mask.setTitle("The Mask");
        robocop.setTitle("Robocop");
        movieService.add(raceMovie);
        movieService.add(mask);
        movieService.add(robocop);
        movieService.getAll().forEach(System.out::println);

        final CinemaHallService cinemaHallService = (CinemaHallService) injector
                .getInstance(CinemaHallService.class);
        CinemaHall imaxCinemaHall = new CinemaHall();
        final CinemaHall oldTypeCinemaHall = new CinemaHall();
        CinemaHall modernCinemaHall = new CinemaHall();

        imaxCinemaHall.setCapacity(100);
        imaxCinemaHall.setDescription("Imax Cinema Hall");
        modernCinemaHall.setCapacity(50);
        modernCinemaHall.setDescription("Modern Cinema Hall");
        oldTypeCinemaHall.setCapacity(30);
        oldTypeCinemaHall.setDescription("Old type Cinema Hall");

        cinemaHallService.add(modernCinemaHall);
        cinemaHallService.add(oldTypeCinemaHall);
        cinemaHallService.add(imaxCinemaHall);
        cinemaHallService.getAll().forEach(System.out::println);

        final MovieSessionService movieSessionService = (MovieSessionService) injector
                .getInstance(MovieSessionService.class);
        MovieSession movieSessionForToday = new MovieSession();
        movieSessionForToday.setCinemaHall(imaxCinemaHall);
        movieSessionForToday.setMovie(raceMovie);
        movieSessionForToday.setShowTime(LocalDateTime.now());
        movieSessionService.add(movieSessionForToday);

        MovieSession movieSessionForTomorrow = new MovieSession();
        movieSessionForTomorrow.setMovie(mask);
        movieSessionForTomorrow.setCinemaHall(modernCinemaHall);
        movieSessionForTomorrow.setShowTime(LocalDateTime.now().plusDays(1));
        movieSessionService.add(movieSessionForTomorrow);

        MovieSession yesterdayMovieSession = new MovieSession();
        yesterdayMovieSession.setMovie(robocop);
        yesterdayMovieSession.setCinemaHall(oldTypeCinemaHall);
        yesterdayMovieSession.setShowTime(LocalDateTime.now().minusDays(1));
        movieSessionService.add(yesterdayMovieSession);

        List<MovieSession> availableSessions = movieSessionService
                .findAvailableSessions(raceMovie.getId(), LocalDate.now());
        List<MovieSession> availableSessions1 = movieSessionService
                .findAvailableSessions(mask.getId(), LocalDate.now().plusDays(1L));
        List<MovieSession> availableSessions2 = movieSessionService
                .findAvailableSessions(robocop.getId(), LocalDate.now().minusDays(1L));
        availableSessions.forEach(System.out::println);
        availableSessions1.forEach(System.out::println);
        availableSessions2.forEach(System.out::println);

        final UserService userService = (UserService) injector
                .getInstance(UserService.class);
        User sasha = new User();
        sasha.setEmail("sasha@gmail.com");
        sasha.setPassword("1234");
        System.out.println(userService.add(sasha));
        System.out.println(userService.findByEmail(sasha.getEmail()));
        String invalidEmail = "user@gmail.com";
        System.out.println(userService.findByEmail(invalidEmail));
        User gena = new User();
        gena.setEmail("sasha@gmail.com");
        gena.setPassword("1234");
        //      throws an error :
        //      ERROR: Duplicate entry 'sasha@gmail.com'
        //      System.out.println(userService.add(gena));

        final AuthenticationService authenticationService = (AuthenticationService) injector
                .getInstance(AuthenticationService.class);
        String login = "tolik@gmail.com";
        String password = "4321";
        User tolik = authenticationService.register(login, password);
        System.out.println(tolik);
        try {
            User userByLogin = authenticationService.login(tolik.getEmail(), password);
            System.out.println(userByLogin.toString());
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }
}
