package workflow;

import workflow.pages.unauthenticated.UnauthenticatedHomepage;
import workflow.pages.Page;
import workflow.io.Output;
import workflow.fileio.Movie;
import workflow.fileio.User;

import java.util.ArrayList;

public final class Database {
    private final ArrayList<User> users;
    private final ArrayList<Movie> movies;
    private ArrayList<String> pages = new ArrayList<String>();

    public ArrayList<String> getPages() {
        return pages;
    }

    public void setPages(ArrayList<String> pages) {
        this.pages = pages;
    }

    private Page page;
    private Output output = null;
    private int userIndex = -1;
    private ArrayList<Movie> currentMoviesList;

    public Database(final ArrayList<User> users, final ArrayList<Movie> movies) {
        this.users = users;
        this.movies = movies;
        this.page = new UnauthenticatedHomepage();
        this.currentMoviesList = new ArrayList<>();
    }

    /**
     * Setez lista curenta de filme pentru utilizatorul curent, exceptand cele banate
     */
    public void setCurrentMoviesList() {
        currentMoviesList = new ArrayList<>();
        String country = users.get(userIndex).getCredentials().getCountry();
        for (Movie movie : movies) {
            if (!movie.getCountriesBanned().contains(country)) {
                currentMoviesList.add(movie);
            }
        }
    }

    /**
     * Setam in clasa de Output eroare
     */
    public void setErrorOutput() {
        this.output = new Output("Error", new ArrayList<>(), null);
    }

    /**
     * Setam in clasa de Output, lista de filme curenta si utilizatorul curent
     */
    public void setSuccessOutput() {
        User u = null;
        if (userIndex != -1) {
            u = users.get(userIndex);
        }
        this.output = new Output(null, currentMoviesList, u);
    }

    /**
     * Metoda pentru eliminarea utilizatorului in cazul actiunii de logout
     */
    public void resetUser() {
        userIndex = -1;
        currentMoviesList = new ArrayList<>();
    }

    public int getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(final int userIndex) {
        this.userIndex = userIndex;
    }

    public ArrayList<Movie> getCurrentMoviesList() {
        return currentMoviesList;
    }

    public void setCurrentMoviesList(final ArrayList<Movie> currentMoviesList) {
        this.currentMoviesList = currentMoviesList;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(final Output output) {
        this.output = output;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(final Page page) {
        this.page = page;
    }
}
