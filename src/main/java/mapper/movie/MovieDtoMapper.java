package mapper.movie;

import dto.MovieDto;
import entity.Movie;
import mapper.Mapper;

public class MovieDtoMapper implements Mapper<MovieDto, Movie> {

    private MovieDtoMapper() {
    }

    private static final MovieDtoMapper INSTANCE = new MovieDtoMapper();

    public static MovieDtoMapper getInstance(){
        return INSTANCE;
    };

    @Override
    public MovieDto mapFrom(Movie entity) {
        return new MovieDto(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getDuration());
    }
}
