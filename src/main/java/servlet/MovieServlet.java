package servlet;

import dto.MovieDto;
import dto.ShowTimeDto;
import exceptions.EntityNotExistException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.MovieServiceImpl;
import services.ShowTimeServiceImpl;
import services.interfaces.MovieService;
import services.interfaces.ShowTimeService;

import java.io.IOException;
import java.util.List;

@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
    private final MovieService movieService = MovieServiceImpl.getInstance();
    private final ShowTimeService showTimeService = ShowTimeServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("id")!=null){
            try {
                Long movieId = Long.valueOf(req.getParameter("id"));
                var movieOptional = movieService.findById(movieId);
                if(movieOptional.isEmpty()){
                    throw new EntityNotExistException("Entity with id " + movieId + " doesn't exist");
                }
                var movie = movieOptional.get();
                List<ShowTimeDto> showTimes = showTimeService.getByMovieId(movieId);
                req.setAttribute("movie", movie);
                req.setAttribute("showTimes", showTimes);
                req.getRequestDispatcher("WEB-INF/jsp/movieDetail.jsp").forward(req, resp);
            } catch (EntityNotExistException e){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            req.setAttribute("movies", movieService.findAll());
            req.getRequestDispatcher("WEB-INF/jsp/moviesList.jsp").forward(req, resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        int duration = Integer.parseInt(req.getParameter("duration"));


        System.out.println(title);
        System.out.println(description);
        System.out.println(duration);
        MovieDto movieDto = new MovieDto(title, description, duration);
        MovieServiceImpl.getInstance().save(movieDto);
        resp.sendRedirect("/movies");
    }
}
