package dao;

import entity.CinemaUser;
import entity.Movie;
import entity.Role;
import entity.ShowTime;
import org.junit.jupiter.api.Test;
import utils.DBConnection;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ShowTimeDaoTest {

    private Movie createMovie(String prefix, int duration){
        return new Movie(null, prefix + "Title", prefix + "Description", duration);
    }

    private ShowTimeDao showTimeDao = ShowTimeDao.getInstance();
    private MovieDao movieDao = MovieDao.getInstance();

    @Test
    void save() {
        Movie savedMovie = movieDao.save(createMovie("SaveTest", 60));
        ShowTime showTime = new ShowTime(null, savedMovie, LocalDateTime.of(2024, Month.JANUARY, 20, 13, 50), 2);
        ShowTime lastShowTimeRecordFromDB = null;
        showTime = showTimeDao.save(showTime);
        String selectSQL = """
                SELECT s.id, s.movie_id, s.start_time, s.screen_id, m.title, m.description, m.duration 
                FROM show_time AS s JOIN movie m on m.id = s.movie_id 
                WHERE s.id = (SELECT MAX(id) FROM show_time)
                """;


        try (var connection = DBConnection.getConnection();
             var statement = connection.createStatement()){
            var rs = statement.executeQuery(selectSQL);
            while (rs.next()){
                lastShowTimeRecordFromDB = new ShowTime();
                var movie = new Movie();
                movie.setId(rs.getLong("movie_id"));
                movie.setTitle(rs.getString("title"));
                movie.setDescription(rs.getString("description"));
                movie.setDuration(rs.getInt("duration"));
                lastShowTimeRecordFromDB.setId(rs.getLong("id"));
                lastShowTimeRecordFromDB.setStart_time(rs.getTimestamp("start_time").toLocalDateTime());
                lastShowTimeRecordFromDB.setScreen_id(rs.getInt("screen_id"));
                lastShowTimeRecordFromDB.setMovie(movie);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(lastShowTimeRecordFromDB);
        isEquals(lastShowTimeRecordFromDB, showTime);
    }

    @Test
    void getAll() {
        Movie movie = movieDao.save(createMovie("getAllTest", 210));
        int countOfRecordsBeforeAdding = showTimeDao.getAll().size();
        ShowTime showTime = showTimeDao.save(new ShowTime(null, movie, LocalDateTime.of(2024, 2, 10, 15, 30), 5));
        List<ShowTime> showTimes = showTimeDao.getAll();
        int countOfRecordsAfterAdding = showTimes.size();
        assertEquals(1, countOfRecordsAfterAdding-countOfRecordsBeforeAdding);
        ShowTime lastRecord = showTimes.get(countOfRecordsAfterAdding-1);
        isEquals(showTime, lastRecord);
    }

    @Test
    void getById() {
        Movie movie = movieDao.save(createMovie("getByIdTest", 60));
        ShowTime showTime = showTimeDao.save(
                new ShowTime(null, movie,
                        LocalDateTime.of(2024, 1, 22, 10, 0),
                        2));
        Optional<ShowTime> returnedShowTimeOptional = showTimeDao.getById(showTime.getId());
        assertTrue(returnedShowTimeOptional.isPresent());
        ShowTime returnedShowTime = returnedShowTimeOptional.get();
        isEquals(showTime, returnedShowTime);
    }

    @Test
    void update() {
        Movie movie = movieDao.save(createMovie("updateTest", 120));
        ShowTime showTime = showTimeDao.save(
                new ShowTime(null, movie,
                        LocalDateTime.of(2024, 1, 30, 11, 40),
                        1));
        ShowTime showTime2 = showTimeDao.save(
                new ShowTime(null, movie,
                        LocalDateTime.of(2024, 1, 30, 11, 40),
                        1));
        Movie newMovie = movieDao.save(createMovie("updated", 300));
        ShowTime showTimeForUpdate = new ShowTime(
                showTime.getId(),
                newMovie,
                LocalDateTime.of(2025, 1, 1, 20, 0),
                10);
        assertTrue(showTimeDao.update(showTimeForUpdate));
        var showTimeOptional = showTimeDao.getById(showTime.getId());
        assertTrue(showTimeOptional.isPresent());
        ShowTime updatedShowTime = showTimeOptional.get();
        isEquals(showTimeForUpdate, updatedShowTime);
    }

    @Test
    void delete() {
        Movie movie = movieDao.save(createMovie("deleteTest", 600));
        ShowTime showTime = showTimeDao.save(
                new ShowTime(
                        null,
                        movie,
                        LocalDateTime.of(2030, 6, 25, 15, 30),
                        3));
        int recordsCountBeforeDeleting = showTimeDao.getAll().size();
        showTimeDao.delete(showTime.getId());
        int recordsCountAfterDeleting = showTimeDao.getAll().size();
        assertTrue(showTimeDao.getById(showTime.getId()).isEmpty());
        assertEquals(1, recordsCountBeforeDeleting-recordsCountAfterDeleting);
    }

    private void isEquals(ShowTime excepted, ShowTime actual){
        assertEquals(excepted.getId(), actual.getId());
        assertEquals(excepted.getMovie().getId(), actual.getMovie().getId());
        assertEquals(excepted.getMovie().getTitle(), actual.getMovie().getTitle());
        assertEquals(excepted.getMovie().getDescription(), actual.getMovie().getDescription());
        assertEquals(excepted.getMovie().getDuration(), actual.getMovie().getDuration());
        assertEquals(excepted.getStart_time(), actual.getStart_time());
        assertEquals(excepted.getScreen_id(), actual.getScreen_id());
    }
}