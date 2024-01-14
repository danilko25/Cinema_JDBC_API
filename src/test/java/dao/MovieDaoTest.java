package dao;

import entity.Movie;
import org.junit.jupiter.api.Test;
import utils.DBConnection;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MovieDaoTest {

    public static MovieDao movieDao = MovieDao.getInstance();

    private static Movie createSpecialMovie(String prefix, int duration){
        return new Movie(null, prefix + "Title", prefix + "Description", duration);
    }

    @Test
    void save() {
        Movie movie = createSpecialMovie("saveTest", 180);
        movie = movieDao.save(movie);
        String selectLastRecord = """
                SELECT id, title, description, duration FROM movie 
                WHERE id = (SELECT MAX(ID) FROM movie)
                """;
        Movie lastMovieRecordInDB = new Movie();
        try (var connection = DBConnection.getConnection();
             var statement = connection.createStatement()){
            var rs = statement.executeQuery(selectLastRecord);
            if (rs.next()){
                lastMovieRecordInDB.setId(rs.getLong("id"));
                lastMovieRecordInDB.setTitle(rs.getString("title"));
                lastMovieRecordInDB.setDescription(rs.getString("description"));
                lastMovieRecordInDB.setDuration(rs.getInt("duration"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertEquals(movie.getId(), lastMovieRecordInDB.getId());
        assertEquals(movie.getTitle(), lastMovieRecordInDB.getTitle());
        assertEquals(movie.getDescription(), lastMovieRecordInDB.getDescription());
        assertEquals(movie.getDuration(), lastMovieRecordInDB.getDuration());
    }

    @Test
    void getAll() {
        Movie movie = createSpecialMovie("getAllTest", 100);
        int recordsBeforeAdding = movieDao.getAll().size();
        movie = movieDao.save(movie);
        List<Movie> movies = movieDao.getAll();
        int recordsAfterAdding = movies.size();
        assertEquals(1, recordsAfterAdding - recordsBeforeAdding);
        assertEquals(movie.getId(), movies.get(movies.size()-1).getId());
        assertEquals(movie.getTitle(), movies.get(movies.size()-1).getTitle());
        assertEquals(movie.getDescription(), movies.get(movies.size()-1).getDescription());
        assertEquals(movie.getDuration(), movies.get(movies.size()-1).getDuration());
    }

    @Test
    void getById() {
        Movie movie = createSpecialMovie("getByIdTest", 300);
        long id = movieDao.save(movie).getId();
        Optional<Movie> foundedMovieOptional = movieDao.getById(id);
        assertTrue(foundedMovieOptional.isPresent());
        Movie foundedMovie = foundedMovieOptional.get();
        assertEquals(movie.getId(), foundedMovie.getId());
        assertEquals(movie.getTitle(), foundedMovie.getTitle());
        assertEquals(movie.getDescription(), foundedMovie.getDescription());
        assertEquals(movie.getDuration(), foundedMovie.getDuration());
    }



    @Test
    void update() {
        Movie movie = createSpecialMovie("updateTest", 100);
        String updatedTitle = "UpdatedTitle";
        String updatedDescription = "updatedDescription";
        int updatedDuration = 60;
        Movie movieForUpdate = movieDao.save(movie);
        movieForUpdate.setTitle(updatedTitle);
        movieForUpdate.setDescription(updatedDescription);
        movieForUpdate.setDuration(updatedDuration);
        boolean isUpdated = movieDao.update(movieForUpdate);
        assertTrue(isUpdated);
        assertTrue(movieDao.getById(movieForUpdate.getId()).isPresent());
        Movie updatedMovie = movieDao.getById(movieForUpdate.getId()).get();
        assertEquals(movieForUpdate.getId(), updatedMovie.getId());
        assertEquals(updatedTitle, updatedMovie.getTitle());
        assertEquals(updatedDescription, updatedMovie.getDescription());
        assertEquals(updatedDuration, updatedMovie.getDuration());
    }

    @Test
    void delete() {
        Movie movieForDelete = createSpecialMovie("deleteTest", 200);
        var movieId = movieDao.save(movieForDelete).getId();
        int recordsCountBeforeDeleting = movieDao.getAll().size();
        movieDao.delete(movieId);
        int recordsCountAfterDeleting = movieDao.getAll().size();
        var movieOptional = movieDao.getById(movieId);
        assertTrue(movieOptional.isEmpty());
        assertEquals(1, recordsCountBeforeDeleting-recordsCountAfterDeleting);


    }
}