package services.interfaces;

import dto.MovieDto;
import entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<MovieDto> findAll();
    Optional<MovieDto> findById(long id);
    Long save(MovieDto movie);
    boolean update(MovieDto movie);
    boolean delete(MovieDto movie);
}
