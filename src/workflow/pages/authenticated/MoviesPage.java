package workflow.pages.authenticated;

import workflow.Database;
import workflow.pages.unauthenticated.UnauthenticatedHomepage;
import workflow.pages.Page;
import workflow.io.Action;
import workflow.fileio.Filters;
import workflow.fileio.Movie;
import workflow.fileio.User;


import java.util.ArrayList;
import java.util.Collections;

public final class MoviesPage implements Page {
    /**
     * Metoda pentru cautarea filmelor care incep cu un anume subsir
     * @param database baza de date
     * @param startsWith subsirul cu care incepe filmul cautat
     */
    private void searchMovies(final Database database, final String startsWith) {
        database.setCurrentMoviesList(new ArrayList<>());
        for (Movie movie : database.getMovies()) {
            // adaugam toate filmele care incep cu acel subsir
            if (movie.getName().startsWith(startsWith)) {
                database.getCurrentMoviesList().add(movie);
            }
        }
        User u = database.getUsers().get(database.getUserIndex());
        /* din lista filmelor realizata precedent le scoatem pe cele care sunt
        banate pentru utilizatorul curent */
        database.getCurrentMoviesList().
                removeIf(movie -> movie.getCountriesBanned().
                        contains(u.getCredentials().getCountry()));
        database.setSuccessOutput();
    }

    @Override
    public void backPage(final Database database) {
        if (database.getPages().size() > 0) {
            String previousPage = database.getPages().get(database.getPages().size() - 1);
            database.getPages().remove(database.getPages().size() - 1);
            switch (previousPage) {
                case "see details" -> database.setPage(new SeeDetailsPage());
                case "upgrades" -> database.setPage(new UpgradesPage());
                case "homepage" -> database.setPage(new AuthenticatedHomepage());
                default -> database.setErrorOutput();
            }
        } else {
            database.setErrorOutput();
        }
    }

    @Override
    public void onPage(final Database database, final Action action) {
        switch (action.getFeature()) {
            // de pe pagina de filme putem realiza doar actiuniile de cautare sau filtrare
            case "search" -> searchMovies(database, action.getStartsWith());
            case "filter" -> filterMovies(database, action.getFilters());
            default -> database.setErrorOutput();
        }

    }

    @Override
    public void changePage(final Database database, final Action action) {
        switch (action.getPage()) {
            /* de pe pagina de 'Movies' putem merge pe pagina de 'See Details'/'Movies'
             sau sa realizam delogarea */
            case "logout" -> {
                database.setPage(new UnauthenticatedHomepage());
                database.resetUser();
                database.setPages(new ArrayList<String>());
            }
            case "movies" -> {
                database.getPages().add("movies");
                database.setPage(new MoviesPage());
                database.setCurrentMoviesList();
                database.setSuccessOutput();
            }
            case "see details" -> {
                seeDetails(database, action);
            }
            case "homepage" -> database.setPage(new AuthenticatedHomepage());
            default -> database.setErrorOutput();
        }
    }
    @Override
    public void modifyDatabase(final Database database, final Action action) {
        switch (action.getFeature()) {
            case "add" -> database.addMovie(action.getAddedMovie());
            case "delete" -> database.deleteMovie(action.getDeletedMovie());
            default -> database.setErrorOutput();
        }
    }

    /**
     * Metoda pentru a vedea detaliile unui film
     * @param database baza de date
     * @param action actiunea
     */
    public void seeDetails(final Database database, final Action action) {
        for (Movie m : database.getCurrentMoviesList()) {
            if (m.getName().equals(action.getMovie())) {
                ArrayList<Movie> movies = new ArrayList<>();
                movies.add(m);
                // adaugam filmul gasit in lista de filme curenta
                database.setCurrentMoviesList(movies);
                // setam noua pagina
                database.getPages().add("upgrades");
                database.setPage(new SeeDetailsPage(database, m));
                database.setSuccessOutput();
                return;
            }
        }
        database.setErrorOutput();
    }

    /**
     * Metoda pentru filtrarea filmelor dupa actori si/sau gen
     * @param database baza de date
     * @param filters criteriile de filtrare
     */
    public void filterMovies(final Database database, final Filters filters) {
        database.setCurrentMoviesList(new ArrayList<>());
        database.getCurrentMoviesList().addAll(database.getMovies());
        if (filters.getSort() != null) {
            ratingSort(database, filters);
            durationSort(database, filters);
        }
        if (filters.getContains() != null) {
            if (filters.getContains().getActors() != null) {
                // scoatem filmele care nu contin actorul/actorii cautati
                database.getCurrentMoviesList().removeIf(
                        movie -> !(movie.getActors().
                                containsAll(filters.getContains().getActors())));
            }
            if (filters.getContains().getGenre() != null) {
                // scoatem filmele care nu contin genul cautat
                database.getCurrentMoviesList().removeIf(
                        movie -> !(movie.getGenres().
                                containsAll(filters.getContains().getGenre())));
            }
        }
        User u = database.getUsers().get(database.getUserIndex());
        // scoatem filmele care sunt banate pentru utilizatorul curent
        database.getCurrentMoviesList().removeIf(
                movie -> movie.getCountriesBanned().contains(u.getCredentials().getCountry()));
        database.setSuccessOutput();
    }

    /**
     * Metoda de sortare dupa durata
     * @param database baza de date
     * @param filters criterii de filtrare
     */
    public void durationSort(final Database database, final Filters filters) {
        String durationSort = filters.getSort().getDuration();
        if (durationSort != null) {
            if (durationSort.equals("increasing")) {
                Collections.sort(database.getCurrentMoviesList(), (o1, o2) -> {
                    if (o1.getRating() < o2.getDuration()) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
            } else {
                Collections.sort(database.getCurrentMoviesList(), (o1, o2) -> {
                    if (o1.getDuration() > o2.getDuration()) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
            }
        }
    }

    /**
     * Metoda de sortare dupa rating
     * @param database baza de date
     * @param filters criterii de filtrare
     */
    public void ratingSort(final Database database, final Filters filters) {
        String ratingSort = filters.getSort().getRating();
        if (ratingSort != null) {
            if (ratingSort.equals("increasing")) {
                Collections.sort(database.getCurrentMoviesList(), (o1, o2) -> {
                    if (o1.getRating() < o2.getRating()) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
            } else {
                Collections.sort(database.getCurrentMoviesList(), (o1, o2) -> {
                    if (o1.getRating() > o2.getRating()) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
            }
        }
    }
}

