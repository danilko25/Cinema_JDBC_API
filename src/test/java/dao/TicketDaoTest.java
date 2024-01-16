package dao;

import entity.*;
import org.junit.jupiter.api.Test;
import utils.DBConnection;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TicketDaoTest {

    private static TicketDao ticketDao = TicketDao.getInstance();
    private static MovieDao movieDao = MovieDao.getInstance();
    private static ShowTimeDao showTimeDao = ShowTimeDao.getInstance();
    private static CinemaUserDao userDao = CinemaUserDao.getInstance();

    private CinemaUser createUser(String prefix, Role role){
        return new CinemaUser(null, prefix + "Name", prefix + "@gmail.com", prefix + "Password", role);
    }

    private Movie createMovie(String prefix, int duration){
        return new Movie(null, prefix + "Title", prefix + "Description", duration);
    }

    @Test
    void save() {
        String prefix = "SaveTicketTest";
        Movie movie = movieDao.save(createMovie(prefix, 180));
        CinemaUser user = userDao.save(createUser(prefix, Role.USER_ROLE));
        ShowTime showTime = showTimeDao.save(new ShowTime(
                null,
                movie,
                LocalDateTime.of(2024, 2, 1, 21, 0),
                5));
        Ticket ticketForSaving = new Ticket(null, user, showTime, "5B");
        ticketForSaving.setId(ticketDao.save(ticketForSaving).getId());

        var returnedTicket = new Ticket();

        try (var connection = DBConnection.getConnection();
             var statement = connection.createStatement()){
            var sql = "SELECT id, user_id, show_time_id, seat_num FROM ticket WHERE id = (SELECT MAX(id) FROM ticket)";
            var rs = statement.executeQuery(sql);
            while (rs.next()){
                var cinemaUserFromRs = new CinemaUser();
                cinemaUserFromRs.setId(rs.getLong("user_id"));

                var showTimeFromRs = new ShowTime();
                showTimeFromRs.setId(rs.getLong("show_time_id"));

                returnedTicket.setId(rs.getLong("id"));
                returnedTicket.setUser(cinemaUserFromRs);
                returnedTicket.setShowTime(showTimeFromRs);
                returnedTicket.setSeatNum(rs.getString("seat_num"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertEquals(ticketForSaving.getId() ,returnedTicket.getId());
        assertEquals(ticketForSaving.getUser().getId() ,returnedTicket.getUser().getId());
        assertEquals(ticketForSaving.getShowTime().getId() ,returnedTicket.getShowTime().getId());
        assertEquals(ticketForSaving.getSeatNum() ,returnedTicket.getSeatNum());
    }

    @Test
    void getAll() {
        String prefix = "getAllTicketsTest";
        Movie movie = movieDao.save(createMovie(prefix, 300));
        CinemaUser user = userDao.save(createUser(prefix, Role.ADMIN_ROLE));
        ShowTime showTime = showTimeDao.save(new ShowTime(
                null,
                movie,
                LocalDateTime.of(2024, 3, 3, 13, 13),
                3));
        int recordsCountBeforeAdding = ticketDao.getAll().size();
        Ticket ticket = ticketDao.save(new Ticket(null, user, showTime, "1A"));
        List<Ticket> tickets = ticketDao.getAll();
        int recordsCountAfterAdding = tickets.size();
        assertEquals(1, recordsCountAfterAdding-recordsCountBeforeAdding);
        isTicketsEquals(ticket, tickets.get(tickets.size()-1));
    }

    @Test
    void getById() {
        String prefix = "getTicketByIdTest";
        Movie movie = movieDao.save(createMovie(prefix, 90));
        CinemaUser user = userDao.save(createUser(prefix, Role.USER_ROLE));
        ShowTime showTime = showTimeDao.save(new ShowTime(
                null,
                movie,
                LocalDateTime.of(2024, 5, 5, 15, 30),
                3));
        Ticket ticket = ticketDao.save(new Ticket(null, user, showTime, "10C"));
        Optional<Ticket> ticketByIdOptional = ticketDao.getById(ticket.getId());
        assertTrue(ticketByIdOptional.isPresent());
        Ticket ticketById = ticketByIdOptional.get();
        isTicketsEquals(ticket, ticketById);
    }

    @Test
    void update() {
        String prefix = "updateTicketTest";
        Movie movie = movieDao.save(createMovie(prefix, 150));
        Movie movieForUpdate = movieDao.save(createMovie(prefix + "Updated", 250));
        CinemaUser user = userDao.save(createUser(prefix, Role.ADMIN_ROLE));
        CinemaUser userForUpdate = userDao.save(createUser(prefix + "Updated", Role.USER_ROLE));
        ShowTime showTime = showTimeDao.save(new ShowTime(
                null,
                movie,
                LocalDateTime.of(2024, 10, 10, 10, 50),
                1));
        ShowTime showTimeForUpdate = showTimeDao.save(new ShowTime(
                null,
                movieForUpdate,
                LocalDateTime.of(2025, 10, 15, 10, 50),
                3));
        Ticket ticket = ticketDao.save(new Ticket(null, user, showTime, "10C"));
        Ticket ticketForUpdate = new Ticket(ticket.getId(), userForUpdate, showTimeForUpdate, "2D");
        assertTrue(ticketDao.update(ticketForUpdate));
        Ticket updatedTicket = ticketDao.getById(ticket.getId()).get();
        isTicketsEquals(ticketForUpdate, updatedTicket);
    }

    @Test
    void delete() {
        String prefix = "deleteTicketTest";
        Movie movie = movieDao.save(createMovie(prefix, 215));
        CinemaUser user = userDao.save(createUser(prefix, Role.USER_ROLE));
        ShowTime showTime = showTimeDao.save(new ShowTime(
                null,
                movie,
                LocalDateTime.of(2024, 12, 25, 9, 20),
                4));
        Ticket ticket = ticketDao.save(new Ticket(null, user, showTime, "7D"));
        int recordsCountBeforeDeleting = ticketDao.getAll().size();
        ticketDao.delete(ticket.getId());
        int recordsCountAfterDeleting = ticketDao.getAll().size();
        assertEquals(1, recordsCountBeforeDeleting-recordsCountAfterDeleting);
        assertTrue(ticketDao.getById(ticket.getId()).isEmpty());
    }

    @Test
    void getAllTicketsByUserId() {
        String prefix = "getTicketsByUserIdTest";
        Movie movie = movieDao.save(createMovie(prefix, 15));
        CinemaUser user = userDao.save(createUser(prefix, Role.USER_ROLE));
        CinemaUser user2 = userDao.save(createUser("secondUser", Role.ADMIN_ROLE));
        ShowTime showTime = showTimeDao.save(new ShowTime(
                null,
                movie,
                LocalDateTime.of(2024, 12, 25, 9, 20),
                4));
        int recordsBeforeAdding = ticketDao.getAllTicketsByUserId(user.getId()).size();
        Ticket ticket = ticketDao.save(new Ticket(null, user, showTime, "5D"));
        Ticket ticket2 = ticketDao.save(new Ticket(null, user2, showTime, "7D"));
        Ticket ticket3 = ticketDao.save(new Ticket(null, user, showTime, "10A"));
        List<Ticket> ticketsWithParticularId = ticketDao.getAllTicketsByUserId(user.getId());
        int recordsAfterAdding = ticketsWithParticularId.size();
        assertEquals(2, recordsAfterAdding - recordsBeforeAdding);
        var lastRecord = ticketsWithParticularId.get(ticketsWithParticularId.size()-1);
        isTicketsEquals(ticket3, lastRecord);

    }

    @Test
    void getAllTicketsByShowTimeId() {
        String prefix = "getTicketsByShowTimeIdTest";
        Movie movie = movieDao.save(createMovie(prefix, 15));
        CinemaUser user = userDao.save(createUser(prefix, Role.USER_ROLE));
        ShowTime showTime = showTimeDao.save(new ShowTime(
                null,
                movie,
                LocalDateTime.of(2024, 12, 25, 9, 20),
                4));
        ShowTime showTime2 = showTimeDao.save(new ShowTime(
                null,
                movie,
                LocalDateTime.of(2025, 12, 25, 9, 20),
                5));
        int recordsBeforeAdding = ticketDao.getAllTicketsByShowTimeId(showTime.getId()).size();
        Ticket ticket = ticketDao.save(new Ticket(null, user, showTime, "5D"));
        Ticket ticket2 = ticketDao.save(new Ticket(null, user, showTime2, "1A"));
        Ticket ticket3 = ticketDao.save(new Ticket(null, user, showTime, "10F"));
        List<Ticket> tickets = ticketDao.getAllTicketsByShowTimeId(showTime.getId());
        int recordsAfterAdding = tickets.size();
        assertEquals(2, recordsAfterAdding - recordsBeforeAdding);
        var lastRecord = tickets.get(tickets.size()-1);
        isTicketsEquals(ticket3, lastRecord);
    }

    private void isTicketsEquals(Ticket exceptedTicket, Ticket actualTicket) {
        var exceptedUser = exceptedTicket.getUser();
        var exceptedShowTime = exceptedTicket.getShowTime();
        var exceptedMovie = exceptedShowTime.getMovie();

        var actualUser = actualTicket.getUser();
        var actualShowTime = actualTicket.getShowTime();
        var actualMovie = actualShowTime.getMovie();
        //MovieEquals
        assertEquals(exceptedMovie.getId(), actualMovie.getId());
        assertEquals(exceptedMovie.getTitle(), actualMovie.getTitle());
        assertEquals(exceptedMovie.getDescription(), actualMovie.getDescription());
        assertEquals(exceptedMovie.getDuration(), actualMovie.getDuration());
        //ShowTimeEquals
        assertEquals(exceptedShowTime.getId(), actualShowTime.getId());
        assertEquals(exceptedShowTime.getStart_time(), actualShowTime.getStart_time());
        assertEquals(exceptedShowTime.getScreen_id(), actualShowTime.getScreen_id());
        //UserEquals
        assertEquals(exceptedUser.getId(), actualUser.getId());
        assertEquals(exceptedUser.getName(), actualUser.getName());
        assertEquals(exceptedUser.getEmail(), actualUser.getEmail());
        assertEquals(exceptedUser.getPassword(), actualUser.getPassword());
        assertEquals(exceptedUser.getRole(), actualUser.getRole());
        //TicketEquals
        assertEquals(exceptedTicket.getId(), actualTicket.getId());
        assertEquals(exceptedTicket.getSeatNum(), actualTicket.getSeatNum());
    }
}