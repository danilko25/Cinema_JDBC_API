package dao.interfaces;

import entity.CinemaUser;
import entity.Movie;

import java.util.List;
import java.util.Optional;

public interface IUserDao {
    CinemaUser save(CinemaUser entity);
    List<CinemaUser> getAll();
    Optional<CinemaUser> getById(Long id);
    boolean update(CinemaUser entity);
    boolean delete(Long id);
    Optional<CinemaUser> getUserByEmailAndPassword(String email, String password);
}
