package dao;

import dao.interfaces.IUserDao;
import entity.CinemaUser;
import entity.Movie;
import entity.Role;
import utils.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CinemaUserDao implements IUserDao {

    private CinemaUserDao() {
    }

    private static final CinemaUserDao INSTANCE = new CinemaUserDao();

    public static CinemaUserDao getInstance(){
        return INSTANCE;
    }

    private static final String INSERT_SQL = """
            INSERT INTO cinema_user(name, email, password, role) VALUES (?, ?, ?, ?)
            """;
    private static final String SELECT_SQL = """
            SELECT id, name, email, password, role FROM cinema_user
            """;

    private static final  String SELECT_BY_ID = SELECT_SQL + " WHERE id = ?";

    private static final String UPDATE_SQL = """
            UPDATE cinema_user SET name = ?, email = ?, password = ?, role = ? WHERE id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE FROM cinema_user WHERE id = ?
            """;

    private static final String SELECT_BY_EMAIL_AND_PASSWORD_SQL = SELECT_SQL + """
            WHERE email = ? AND password = ?
            """;

    @Override
    public CinemaUser save(CinemaUser user) {
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            var role = user.getRole();
            if (role==null){
                role = Role.USER_ROLE;
            }
            statement.setString(4, role.name());
            statement.execute();
            var rs = statement.getGeneratedKeys();
            if(rs.next()){
                user.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public List<CinemaUser> getAll() {
        List<CinemaUser> users = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
             var statement = connection.createStatement()) {
            var rs = statement.executeQuery(SELECT_SQL);
            while (rs.next()){
                users.add(getUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public Optional<CinemaUser> getById(Long id) {
        CinemaUser user = null;
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setLong(1, id);
            var rs = statement.executeQuery();
            if(rs.next()) {
                user = getUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }


    @Override
    public boolean update(CinemaUser user) {
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL) ){
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getRole().name());
            statement.setLong(5, user.getId());
            return (statement.executeUpdate())>0;
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

    public Optional<CinemaUser> getUserByEmailAndPassword(String email, String password){
        try (var connection = DBConnection.getConnection();
             var statement = connection.prepareStatement(SELECT_BY_EMAIL_AND_PASSWORD_SQL)){
            statement.setString(1, email);
            statement.setString(2, password);
            var rs = statement.executeQuery();
            if (rs.next()){
                return Optional.of(getUserFromResultSet(rs));
            }else return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private CinemaUser getUserFromResultSet(ResultSet rs) throws SQLException {
        CinemaUser user = new CinemaUser();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        Optional<Role> roleOptional = Role.find(rs.getString("role"));
        var role = roleOptional.orElse(Role.USER_ROLE);
        user.setRole(role);
        return user;
    }
}
