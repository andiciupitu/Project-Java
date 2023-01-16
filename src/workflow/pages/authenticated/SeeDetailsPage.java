package workflow.pages.authenticated;

import workflow.Database;
import workflow.pages.unauthenticated.UnauthenticatedHomepage;
import workflow.pages.Page;
import workflow.io.Action;
import workflow.fileio.Movie;
import workflow.fileio.User;

import java.util.ArrayList;

public class SeeDetailsPage implements Page {
    private Database database;
    private User user;
    private Movie movie;

    public static final int MAX_RATE = 5;
    public SeeDetailsPage(final Database database, final Movie movie) {
        this.database = database;
        this.movie = movie;
        this.user = database.getUsers().get(database.getUserIndex());
    }

    public SeeDetailsPage() {
    }
    public SeeDetailsPage(Database database, User user, Movie movie) {
        this.database = database;
        this.user = user;
        this.movie = movie;
    }


    @Override
    public void onPage(final Database db, final Action action) {
        // pe pagina SeeDetails putem cumpara, viziona, aprecia, evalua filme, abona
        switch (action.getFeature()) {
            case "purchase" -> purchase();
            case "watch" -> watch();
            case "like" -> like();
            case "rate" -> rate(action.getRate());
            case "subscribe" -> subscribe(action.getSubscribedGenre());
            default -> db.setErrorOutput();
        }
    }

    @Override
    public void changePage(final Database db, final Action action) {
        /* de pe pagina SeeDetails ne putem muta pe pagina de Movies/Upgrades sau
         sa ne delogam */
        switch (action.getPage()) {
            case "logout" -> {
                db.setPage(new UnauthenticatedHomepage());
                db.resetUser();
                db.setPages(new ArrayList<String>());
            }
            case "movies" -> {
                db.getPages().add("see details");
                db.setPage(new MoviesPage());
                db.setCurrentMoviesList();
                db.setSuccessOutput();
            }
            case "upgrades" -> {
                db.getPages().add("see details");
                db.setPage(new UpgradesPage());
            }

            case "homepage" -> db.setPage(new AuthenticatedHomepage());
            default -> db.setErrorOutput();
        }
    }

    @Override
    public void backPage(Database db) {
        if (db.getPages().size() > 0) {
            String previousPage = db.getPages().get(db.getPages().size() - 1);
            db.getPages().remove(db.getPages().size() - 1);
            switch(previousPage) {
                case "movies" ->  {
                    db.setPage(new MoviesPage());
                    db.setCurrentMoviesList();
                    db.setSuccessOutput();}
                case "upgrades" -> db.setPage(new UpgradesPage());
                case "homepage" -> db.setPage(new AuthenticatedHomepage());
                default -> db.setErrorOutput();
            }
        } else {
            db.setErrorOutput();
        }
    }
    @Override
    public void modifyDatabase(Database database, Action action) {
        switch (action.getFeature()) {
            case "add" -> database.addMovie(action.getAddedMovie());
            case "delete" -> database.deleteMovie(action.getDeletedMovie());
            default -> database.setErrorOutput();
        }
    }

    /**
     * Metoda care verifica daca cumva filmul este cumparat, in caz contrar daca utilizatorul
     * este unul premium sau standard
     */
    public void purchase() {
        if (user.getPurchasedMovies().contains(movie)) {
            database.setErrorOutput();
        } else {
            if (user.getCredentials().getAccountType().equals("premium")) {
                premiumUserPurchase();
            } else {
                standardUserPurchase();
            }
        }
    }

    /**
     * Metoda de cumparare adresata utilizatorilor care detin un cont premium
     */
    public void premiumUserPurchase() {
        // se verifica daca utilizatorul mai are filme gratis(din cele 15)
        if (user.getNumFreePremiumMovies() > 0) {
            user.getPurchasedMovies().add(movie);
            user.setNumFreePremiumMovies(user.getNumFreePremiumMovies() - 1);
            database.setSuccessOutput();
        } else {
            // in caz contrar va achizitiona filmul cu tokeni
            standardUserPurchase();
        }
    }

    /**
     * Metoda de achiztionare a unul film, adresata unui utilizator standard
     */
    public void standardUserPurchase() {
        int moviePrice = 2;
        // se verifica daca are peste 2 tokeni
        if (user.getTokensCount() >= moviePrice) {
            // se adauga in lista de filme cumparate
            user.getPurchasedMovies().add(movie);
            // se scad din tokeni
            user.setTokensCount(user.getTokensCount() - moviePrice);
            database.setSuccessOutput();
        } else {
            database.setErrorOutput();
        }
    }

    /**
     * Metoda de a adauga un film in cele vizionate
     */
    public void watch() {
        // daca filmul se afla printre cele cumparate
        if (user.getPurchasedMovies().contains(movie)) {
            // daca filmul nu cumva este deja vizionat
            if (user.getWatchedMovies().contains(movie)) {
                database.setErrorOutput();
            } else {
                // se adauga in lista celor vizionate
                user.getWatchedMovies().add(movie);
                database.setSuccessOutput();
            }
        } else {
            database.setErrorOutput();
        }
    }

    /**
     * Metoda de apreciere a unui film vizionat
     */
    public void like() {
        // daca se afla printre cele cumparate
        if (user.getPurchasedMovies().contains(movie)) {
            // daca se afla printre cele vizionate
            if (user.getWatchedMovies().contains(movie)) {
                // se verifica daca nu cumva a fost apreciat filmul
                if (user.getLikedMovies().contains(movie)) {
                    database.setErrorOutput();
                } else {
                    // se adauga in lista celor apreciate
                    user.getLikedMovies().add(movie);
                    // filmului ii se incrementeaza numarul de aprecieri
                    movie.setNumLikes(movie.getNumLikes() + 1);
                    database.setSuccessOutput();
                }
            } else {
                database.setErrorOutput();
            }
        } else {
            database.setErrorOutput();
        }
    }

    /**
     * Metoda de evaluare a unui film deja vizionat
     * @param rate nota pe care vrea utilizatorul sa o ofere filmului
     */
    private void rate(final int rate) {
        // nota posibila pe care o pot oferi utilizatori trebuie sa se afle intre [1,5]
        if (rate < 1 || rate > MAX_RATE) {
            database.setErrorOutput();
        } else {
            // se cauta in lista de filme cumparate
            if (user.getPurchasedMovies().contains(movie)) {
                // se cauta in lista de filme vizionate
                if (user.getWatchedMovies().contains(movie)) {
                    // se verifica daca nu cumva a fost deja evaluat de acest utilizator
                    if (user.getRatedMovies().contains(movie)) {
                        database.setErrorOutput();
                    } else {
                        // se adauga in lista celor evaluate
                        user.getRatedMovies().add(movie);
                        movie.rate(rate);
                        database.setSuccessOutput();
                    }
                } else {
                    database.setErrorOutput();
                }
            } else {
                database.setErrorOutput();
            }
        }
    }

    /**
     * metoda de abonare la un anume gen
     * @param subscribedGenre tipul de film la care vrem sa ne abonam
     */
    public void subscribe(String subscribedGenre) {
        database.setErrorOutput();
    }
}

