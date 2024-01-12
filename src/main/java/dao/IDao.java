package dao;

import java.util.List;
import java.util.Optional;

public interface IDao <T, K>{
    T save(T entity);
    List<T> getAll();
    Optional<T> getById(K id);
    boolean update(T entity);
    boolean delete(K id);
}
