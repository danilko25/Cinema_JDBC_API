package dao;

import entity.Movie;
import entity.ShowTime;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShowTimeDao implements IDao<ShowTime, Long> {

    private ShowTimeDao() {
    }

    private static final ShowTimeDao INSTANCE = new ShowTimeDao();

    public static ShowTimeDao getInstance(){
        return INSTANCE;
    }

    private static final String INSERT_SQL = """
            INSERT INTO show_time(movie_id, start_time, screen_id) VALUES (?, ?, ?)
            """;

    private static final String SELECT_SQL = """
            SELECT s.id, s.movie_id, s.start_time, s.screen_id, m.title, m.description, m.duration 
            FROM show_time AS s JOIN movie m on m.id = s.movie_id
            """;

    private static final String SELECT_BY_ID_SQL = SELECT_SQL + " WHERE s.id = ?";

    private static final String UPDATE_SQL = """
            UPDATE show_time SET movie_id = ?, start_time = ?, screen_id = ? WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM show_time WHERE id = ?
            """;

    @Override
    public ShowTime save(ShowTime showTime) {
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
            statement.setLong(1, showTime.getMovie().getId());
            statement.setTimestamp(2, Timestamp.valueOf(showTime.getStart_time()));
            statement.setInt(3, showTime.getScreen_id());
            statement.executeUpdate();
            var rs = statement.getGeneratedKeys();
            if (rs.next()){
                showTime.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return showTime;
    }

    @Override
    public List<ShowTime> getAll() {
        List<ShowTime> showTimes = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
             var statement = connection.createStatement()){
            var rs =statement.executeQuery(SELECT_SQL);
            while (rs.next()){
                showTimes.add(getShowTimeFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return showTimes;
    }



    @Override
    public Optional<ShowTime> getById(Long id) {
        ShowTime showTime = null;
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(SELECT_BY_ID_SQL)){
            statement.setLong(1, id);
            var rs = statement.executeQuery();
            if (rs.next()){
                showTime = getShowTimeFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(showTime);
    }

    @Override
    public boolean update(ShowTime showTime) {
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL)){
            statement.setLong(1, showTime.getMovie().getId());
            statement.setTimestamp(2, Timestamp.valueOf(showTime.getStart_time()));
            statement.setInt(3, showTime.getScreen_id());
            statement.setLong(4, showTime.getId());
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

    private ShowTime getShowTimeFromResultSet(ResultSet rs) throws SQLException {
        var showTime = new ShowTime();
        var movie = new Movie();
        movie.setId(rs.getLong("movie_id"));
        movie.setTitle(rs.getString("title"));
        movie.setDescription(rs.getString("description"));
        movie.setDuration(rs.getInt("duration"));
        showTime.setId(rs.getLong("id"));
        showTime.setStart_time(rs.getTimestamp("start_time").toLocalDateTime());
        showTime.setScreen_id(rs.getInt("screen_id"));
        showTime.setMovie(movie);
        return showTime;
    }
}
