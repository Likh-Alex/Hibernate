package com.dev.cinema;

import com.dev.cinema.exception.AuthenticationException;
import com.dev.cinema.lib.Injector;
import com.dev.cinema.model.CinemaHall;
import com.dev.cinema.model.Movie;
import com.dev.cinema.model.MovieSession;
import com.dev.cinema.model.ShoppingCart;
import com.dev.cinema.model.Ticket;
import com.dev.cinema.model.User;
import com.dev.cinema.security.AuthenticationService;
import com.dev.cinema.service.CinemaHallService;
import com.dev.cinema.service.MovieService;
import com.dev.cinema.service.MovieSessionService;
import com.dev.cinema.service.OrderService;
import com.dev.cinema.service.ShoppingCartService;
import com.dev.cinema.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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

        MovieSession movieSessionForYesterday = new MovieSession();
        movieSessionForYesterday.setMovie(robocop);
        movieSessionForYesterday.setCinemaHall(oldTypeCinemaHall);
        movieSessionForYesterday.setShowTime(LocalDateTime.now().minusDays(1));
        movieSessionService.add(movieSessionForYesterday);

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
            System.out.println("An error encountered while login the user");
        }

        final ShoppingCartService shoppingCartService = (ShoppingCartService) injector
                .getInstance(ShoppingCartService.class);
        final ShoppingCart shoppingCart1 = new ShoppingCart();
        final ShoppingCart shoppingCart2 = new ShoppingCart();
        final ShoppingCart shoppingCart3 = new ShoppingCart();

        User pavlo = new User();
        pavlo.setEmail("pavlo@gmail.com");
        pavlo.setPassword("1234");
        final User registeredPavlo = authenticationService.register(pavlo.getEmail(),
                pavlo.getPassword());

        User mykola = new User();
        mykola.setEmail("mykola@gmail.com");
        mykola.setPassword("qwerty");
        final User registeredMykola = authenticationService.register(mykola.getEmail(),
                mykola.getPassword());

        User roman = new User();
        roman.setEmail("roman@gmail.com");
        roman.setPassword("09876");
        final User registeredRoman = authenticationService.register(roman.getEmail(),
                roman.getPassword());

        Ticket ticket1 = new Ticket();
        ticket1.setUser(registeredPavlo);
        ticket1.setMovieSession(movieSessionForYesterday);

        Ticket ticket2 = new Ticket();
        ticket2.setUser(registeredMykola);
        ticket2.setMovieSession(movieSessionForToday);

        Ticket ticket3 = new Ticket();
        ticket3.setUser(registeredRoman);
        ticket3.setMovieSession(movieSessionForTomorrow);

        shoppingCart1.setUser(registeredPavlo);
        shoppingCart1.setTickets(List.of(ticket1));
        shoppingCart2.setUser(registeredMykola);
        shoppingCart2.setTickets(List.of(ticket2));
        shoppingCart3.setUser(registeredRoman);
        shoppingCart3.setTickets(List.of(ticket3));

        shoppingCartService.addSession(movieSessionForYesterday, registeredPavlo);
        shoppingCartService.addSession(movieSessionForToday, registeredMykola);
        shoppingCartService.addSession(movieSessionForTomorrow, registeredRoman);

        ShoppingCart cartByUser1 = shoppingCartService.getByUser(registeredPavlo);
        ShoppingCart cartByUser2 = shoppingCartService.getByUser(registeredMykola);
        ShoppingCart cartByUser3 = shoppingCartService.getByUser(registeredRoman);
        for (ShoppingCart shoppingCart : Arrays.asList(cartByUser1, cartByUser2, cartByUser3)) {
            System.out.println(shoppingCart.toString());
        }

        shoppingCartService.clear(cartByUser3);
        System.out.println(cartByUser3);

        System.out.println("+++++ORDER SERVICE+++++");
        final OrderService orderService = (OrderService) injector
                .getInstance(OrderService.class);

        shoppingCartService.addSession(movieSessionForToday, registeredMykola);
        ShoppingCart firstCartOfMykola = shoppingCartService.getByUser(registeredMykola);
        System.out.println("COMPLETE FIRST ORDER FOR SHOPPING CART 4\n");
        System.out.println(orderService.completeOrder(firstCartOfMykola));
        System.out.println("COMPLETED ORDER 4\n");
        System.out.println(firstCartOfMykola);

        shoppingCartService.addSession(movieSessionForTomorrow, registeredMykola);
        ShoppingCart secondCartOfMykola = shoppingCartService.getByUser(registeredMykola);

        System.out.println("COMPLETE ORDER FOR SHOPPING CART 5\n");
        System.out.println(orderService.completeOrder(secondCartOfMykola));
        System.out.println("COMPLETED ORDER 5\n");
        System.out.println(secondCartOfMykola);

        System.out.println("ORDER HISTORY BY USER\n");
        System.out.println(orderService.getOrdersHistory(registeredMykola).toString());
    }
}
