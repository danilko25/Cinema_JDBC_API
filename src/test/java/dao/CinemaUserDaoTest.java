package dao;

import entity.CinemaUser;
import entity.Role;
import org.junit.jupiter.api.*;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CinemaUserDaoTest {

    private Connection connection;

    private static CinemaUserDao userDao = CinemaUserDao.getInstance();

    @BeforeEach
    public void setup() throws SQLException {
        connection = DBConnection.getConnection();
    }

    @AfterEach
    public void teardown() throws SQLException {
        connection.close();
    }


    @Test
    void save() throws SQLException {
        CinemaUser user = new CinemaUser();
        user.setName("TestName");
        user.setEmail("test@gmail.com");
        user.setPassword("passwordForTest1234");
        user.setRole(Role.USER_ROLE);
        user = userDao.save(user);
        var statement = connection.createStatement();
        var rs = statement.executeQuery("SELECT id, name, email, password, role FROM cinema_user WHERE id = (SELECT MAX(ID) FROM cinema_user)");
        CinemaUser getUserFromDB = null;
        if (rs.next()){
            getUserFromDB = new CinemaUser();
            getUserFromDB.setId(rs.getLong("id"));
            getUserFromDB.setName(rs.getString("name"));
            getUserFromDB.setEmail(rs.getString("email"));
            getUserFromDB.setPassword(rs.getString("password"));
            Optional<Role> roleOptional = Role.find(rs.getString("role"));
            var role = roleOptional.orElse(Role.USER_ROLE);
            getUserFromDB.setRole(role);
        }
        assertNotNull(getUserFromDB);
        assertEquals(user.getId(), getUserFromDB.getId());
        assertEquals(user.getName(), getUserFromDB.getName());
        assertEquals(user.getPassword(), getUserFromDB.getPassword());
        assertEquals(user.getEmail(), getUserFromDB.getEmail());
        assertEquals(user.getRole(), getUserFromDB.getRole());
    }

    @Test
    void getAll() throws SQLException {
        int beforeAdding = userDao.getAll().size();
        CinemaUser user1 = new CinemaUser();
        user1.setName("first");
        user1.setEmail("first@gmail.com");
        user1.setPassword("firstPassword");
        user1.setRole(Role.ADMIN_ROLE);
        userDao.save(user1);
        CinemaUser user2 = new CinemaUser();
        user2.setName("second");
        user2.setEmail("second@gmail.com");
        user2.setPassword("secondPassword");
        user2.setRole(Role.USER_ROLE);
        userDao.save(user2);
        List<CinemaUser> users = userDao.getAll();
        int afterAdding = users.size();
        assertEquals(2, afterAdding - beforeAdding);
        assertEquals(user2.getName(), users.get(afterAdding-1).getName());
    }

    @Test
    void getById() {
        CinemaUser user3 = new CinemaUser();
        user3.setName("third");
        user3.setEmail("third@gmail.com");
        user3.setPassword("thirdPassword");
        user3.setRole(Role.USER_ROLE);
        user3 = userDao.save(user3);
        Long id = user3.getId();
        var returnedUserOptional = userDao.getById(id);
        assertTrue(returnedUserOptional.isPresent());
        var returnedUser = returnedUserOptional.get();
        assertEquals(user3.getId(), returnedUser.getId());
        assertEquals(user3.getName(), returnedUser.getName());
        assertEquals(user3.getEmail(), returnedUser.getEmail());
        assertEquals(user3.getPassword(), returnedUser.getPassword());
        assertEquals(user3.getRole(), returnedUser.getRole());
    }

    @Test
    void update() {
        CinemaUser user = new CinemaUser();
        user.setName("old");
        user.setEmail("old@gmail.com");
        user.setPassword("oldPassword");
        Long id = userDao.save(user).getId();
        CinemaUser userForUpdate = new CinemaUser();
        userForUpdate.setId(id);
        userForUpdate.setName("updated");
        userForUpdate.setEmail("updated@gmail.com");
        userForUpdate.setPassword("updatedPassword");
        userForUpdate.setRole(Role.USER_ROLE);
        var isUpdate = userDao.update(userForUpdate);
        assertTrue(isUpdate);
        var updatedUserOptional = userDao.getById(id);
        assertTrue(updatedUserOptional.isPresent());
        var updatedUser = updatedUserOptional.get();
        assertEquals(userForUpdate.getId(), updatedUser.getId());
        assertEquals(userForUpdate.getName(), updatedUser.getName());
        assertEquals(userForUpdate.getPassword(), updatedUser.getPassword());
        assertEquals(userForUpdate.getEmail(), updatedUser.getEmail());
        assertEquals(userForUpdate.getRole(), updatedUser.getRole());
    }

    @Test
    void delete() {
        CinemaUser user1 = new CinemaUser();
        user1.setName("delete");
        user1.setEmail("delete@gmail.com");
        user1.setPassword("deletePassword");
        user1.setRole(Role.ADMIN_ROLE);
        Long id = userDao.save(user1).getId();
        int beforeDeleting = userDao.getAll().size();
        userDao.delete(id);
        int afterDeleting = userDao.getAll().size();
        var userOptional = userDao.getById(id);
        assertTrue(userOptional.isEmpty());
        assertEquals(1, beforeDeleting - afterDeleting);
    }

    @Test
    void login(){
        CinemaUser loginUser = new CinemaUser();
        loginUser.setName("loginTest");
        String email = "login@gmail.com";
        loginUser.setEmail(email);
        String password = "loginPassword";
        loginUser.setPassword(password);
        loginUser.setRole(Role.ADMIN_ROLE);
        userDao.save(loginUser);
        var userOptional = userDao.getUserByEmailAndPassword(email, password);
        assertTrue(userOptional.isPresent());
        var emptyOptional = userDao.getUserByEmailAndPassword("not exist", "no password");
        assertTrue(emptyOptional.isEmpty());
    }
}