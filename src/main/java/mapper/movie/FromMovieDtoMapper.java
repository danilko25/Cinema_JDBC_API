package mapper.movie;

import dto.MovieDto;
import entity.Movie;
import mapper.Mapper;

public class FromMovieDtoMapper implements Mapper<Movie, MovieDto> {
    private FromMovieDtoMapper() {
    }

    private static FromMovieDtoMapper INSTANCE = new FromMovieDtoMapper();

    public static FromMovieDtoMapper getInstance(){return INSTANCE;}

    @Override
    public Movie mapFrom(MovieDto entity) {
        return new Movie(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getDuration());
    }
}
