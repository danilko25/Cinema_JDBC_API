package services;

import dao.IDao;
import dao.MovieDao;
import dto.MovieDto;
import entity.Movie;
import mapper.movie.FromMovieDtoMapper;
import mapper.Mapper;
import mapper.movie.MovieDtoMapper;
import services.interfaces.MovieService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MovieServiceImpl implements MovieService {

    IDao<Movie, Long> movieDao = MovieDao.getInstance();

    Mapper<MovieDto, Movie> mapperToMovieDto = MovieDtoMapper.getInstance();

    private MovieServiceImpl() {
    }

    private static final MovieServiceImpl INSTANCE = new MovieServiceImpl();

    public static MovieServiceImpl getInstance(){
        return INSTANCE;
    }

    @Override
    public List<MovieDto> findAll() {
        return movieDao.getAll().stream().map(movie -> mapperToMovieDto.mapFrom(movie)).collect(Collectors.toList());
    }

    @Override
    public Optional<MovieDto> findById(long id) throws RuntimeException{
        var movieOptional = movieDao.getById(id);
        return movieOptional.map(movie -> mapperToMovieDto.mapFrom(movie));
    }

    @Override
    public Long save(MovieDto movie) {
        return movieDao.save(FromMovieDtoMapper.getInstance().mapFrom(movie)).getId();
    }

    @Override
    public boolean update(MovieDto movie) {
        return movieDao.update(FromMovieDtoMapper.getInstance().mapFrom(movie));
    }

    @Override
    public boolean delete(MovieDto movie) {
        return movieDao.delete(movie.getId());
    }
}
