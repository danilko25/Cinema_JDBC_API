package dao;

import entity.*;
import utils.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDao implements IDao<Ticket, Long> {

    private TicketDao() {}

    private static TicketDao INSTANCE = new TicketDao();

    public static TicketDao getInstance(){
        return INSTANCE;
    }

    private static final TicketDao ticketDao = TicketDao.getInstance();

    private static final ShowTimeDao showtimeDao = ShowTimeDao.getInstance();

    private static final MovieDao movieDao = MovieDao.getInstance();

    private static final String INSERT_SQL = """
            INSERT INTO ticket (user_id, show_time_id, seat_num) VALUES (?, ?, ?)
            """;

    private static final String SELECT_SQL = """
            SELECT t.id, t.user_id, t.show_time_id, t.seat_num, 
            cu.name, cu.email, cu.password, cu.role,
            st.movie_id, st.start_time, st.screen_id,
            m.title, m.description, m.duration
            FROM ticket AS t
            JOIN cinema_user cu on cu.id = t.user_id
            JOIN show_time st on st.id = t.show_time_id
            JOIN movie m on m.id = st.movie_id
            """;

    private static final String SELECT_BY_ID_SQL = SELECT_SQL + " WHERE t.id = ?";

    private static final String SELECT_BY_USER_ID_SQL = SELECT_SQL + " WHERE t.user_id = ?";

    private static final String SELECT_BY_SHOW_TIME_ID_SQL = SELECT_SQL + " WHERE t.show_time_id = ?";



    private static final String UPDATE_SQL = """
            UPDATE ticket SET user_id = ?, show_time_id = ?, seat_num = ? WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM ticket WHERE id = ?
            """;

    @Override
    public Ticket save(Ticket ticket) {
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
            statement.setLong(1, ticket.getUser().getId());
            statement.setLong(2, ticket.getShowTime().getId());
            statement.setString(3, ticket.getSeatNum());
            statement.executeUpdate();
            var rs = statement.getGeneratedKeys();
            if (rs.next()){
                ticket.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ticket;
    }

    @Override
    public List<Ticket> getAll() {
        List<Ticket> tickets = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
             var statement = connection.createStatement()){
            var rs = statement.executeQuery(SELECT_SQL);
            while (rs.next()){
                tickets.add(getTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tickets;
    }

    @Override
    public Optional<Ticket> getById(Long id) {
        Ticket ticket = null;
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(SELECT_BY_ID_SQL)){
            statement.setLong(1, id);
            var rs = statement.executeQuery();
            if (rs.next()){
                ticket = getTicketFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(ticket);
    }

    @Override
    public boolean update(Ticket ticket) {
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL)){
            statement.setLong(1, ticket.getUser().getId());
            statement.setLong(2, ticket.getShowTime().getId());
            statement.setString(3, ticket.getSeatNum());
            statement.setLong(4, ticket.getId());
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

    public List<Ticket> getAllTicketsByUserId(Long userId){
        return getAllByParticularId(SELECT_BY_USER_ID_SQL, userId);
    }

    public List<Ticket> getAllTicketsByShowTimeId(Long showTimeId){
        return getAllByParticularId(SELECT_BY_SHOW_TIME_ID_SQL, showTimeId);
    }

    private Ticket getTicketFromResultSet(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        ShowTime showTime = new ShowTime();
        CinemaUser user = new CinemaUser();
        Ticket ticket = new Ticket();

        movie.setId(rs.getLong("movie_id"));
        movie.setTitle(rs.getString("title"));
        movie.setDescription(rs.getString("description"));
        movie.setDuration(rs.getInt("duration"));

        showTime.setId(rs.getLong("show_time_id"));
        showTime.setMovie(movie);
        showTime.setStart_time(rs.getTimestamp("start_time").toLocalDateTime());
        showTime.setScreen_id(rs.getInt("screen_id"));

        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(Role.valueOf(rs.getString("role")));

        ticket.setId(rs.getLong("id"));
        ticket.setUser(user);
        ticket.setShowTime(showTime);
        ticket.setSeatNum(rs.getString("seat_num"));

        return ticket;
    }

    private List<Ticket> getAllByParticularId(String SQL, Long id) {
        List<Ticket> tickets = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(SQL)){
            statement.setLong(1, id);
            var rs = statement.executeQuery();
            while (rs.next()){
                tickets.add(getTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tickets;
    }
}
