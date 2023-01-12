package workflow.io;

import workflow.fileio.Movie;
import workflow.fileio.User;

import java.util.ArrayList;

public final class Output {
    private String error;
    private ArrayList<Movie> currentMoviesList;
    private User currentUser;

    public Output() {

    }

    public Output(final String error,
                  final ArrayList<Movie> currentMoviesList,
                  final User currentUser) {
        this.error = error;
        this.currentMoviesList = currentMoviesList;
        this.currentUser = currentUser;
    }

    public String getError() {
        return error;
    }

    public ArrayList<Movie> getCurrentMoviesList() {
        return currentMoviesList;
    }

    public User getCurrentUser() {
        return currentUser;
    }

}
