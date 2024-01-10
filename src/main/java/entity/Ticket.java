package entity;

import java.util.Objects;

public class Ticket {
    private Long id;
    private CinemaUser user;
    private ShowTime showTime;
    private String seatNum;

    public Ticket() {
    }

    public Ticket(Long id, CinemaUser user, ShowTime showTime, String seatNum) {
        this.id = id;
        this.user = user;
        this.showTime = showTime;
        this.seatNum = seatNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CinemaUser getUser() {
        return user;
    }

    public void setUser(CinemaUser user) {
        this.user = user;
    }

    public ShowTime getShowTime() {
        return showTime;
    }

    public void setShowTime(ShowTime showTime) {
        this.showTime = showTime;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id) && Objects.equals(user, ticket.user) && Objects.equals(showTime, ticket.showTime) && Objects.equals(seatNum, ticket.seatNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, showTime, seatNum);
    }
}
