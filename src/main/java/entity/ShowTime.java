package entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class ShowTime {
    private Long id;
    private Movie movie;
    private LocalDateTime start_time;
    private int screen_id;

    public ShowTime() {
    }

    public ShowTime(Long id, Movie movie, LocalDateTime start_time, int screen_id) {
        this.id = id;
        this.movie = movie;
        this.start_time = start_time;
        this.screen_id = screen_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalDateTime start_time) {
        this.start_time = start_time;
    }

    public int getScreen_id() {
        return screen_id;
    }

    public void setScreen_id(int screen_id) {
        this.screen_id = screen_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShowTime showTime = (ShowTime) o;
        return screen_id == showTime.screen_id && Objects.equals(id, showTime.id) && Objects.equals(movie, showTime.movie) && Objects.equals(start_time, showTime.start_time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
