package dao;

import entity.Movie;
import utils.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MovieDao implements IDao<Movie, Long> {

    private MovieDao() {
    }

    private static final MovieDao INSTANCE = new MovieDao();

    public static MovieDao getInstance(){
        return INSTANCE;
    }

    private static final String INSERT_SQL = """
            INSERT INTO movie (title, description, duration) VALUES (?, ?, ?)
            """;

    private static final String SELECT_SQL = """
            SELECT id, title, description, duration FROM movie
            """;

    public static final String SELECT_BY_ID_SQL = SELECT_SQL + " WHERE id = ?";

    public static final String UPDATE_SQL = """
            UPDATE movie SET title = ?, description = ?, duration = ? WHERE id = ?
            """;

    public static final String DELETE_SQL = """
            DELETE FROM movie WHERE id = ?
            """;

    @Override
    public Movie save(Movie movie) {
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getDescription());
            statement.setInt(3, movie.getDuration());
            statement.executeUpdate();
            var rs = statement.getGeneratedKeys();
            if (rs.next()){
                movie.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movie;
    }

    @Override
    public List<Movie> getAll() {
        List<Movie> movies = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
             var statement = connection.createStatement()){
            var rs = statement.executeQuery(SELECT_SQL);
            while (rs.next()){
                movies.add(getMovieFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movies;
    }



    @Override
    public Optional<Movie> getById(Long id) {
        Movie movie = null;
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(SELECT_BY_ID_SQL)){
            statement.setLong(1, id);
            var rs = statement.executeQuery();
            if (rs.next()){
                movie = getMovieFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(movie);
    }

    @Override
    public boolean update(Movie movie) {
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL)){
            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getDescription());
            statement.setInt(3, movie.getDuration());
            statement.setLong(4, movie.getId());
            return statement.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(DELETE_SQL)){
            statement.setLong(1, id);
            return statement.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Movie getMovieFromResultSet(ResultSet rs) throws SQLException {
        var movie = new Movie();
        movie.setId(rs.getLong("id"));
        movie.setTitle(rs.getString("title"));
        movie.setDescription(rs.getString("description"));
        movie.setDuration(rs.getInt("duration"));
        return movie;
    }
}
