package workflow.pages.unauthenticated;

import workflow.pages.authenticated.AuthenticatedHomepage;
import workflow.Database;
import workflow.pages.Page;
import workflow.io.Action;
import workflow.fileio.Credentials;
import workflow.fileio.User;

import java.util.ArrayList;

public final class LoginPage implements Page {
    /**
     * Metoda care seteaza eroare in cazul in care nu se poate realiza logarea
     * @param database baza de date
     */
    public void loginError(final Database database) {
        database.setPage(new UnauthenticatedHomepage());
        database.setErrorOutput();
    }

    /**
     * Metoda de realizare a logarii, setam pagina ca fiind cea de autentificare,
     * index ului userului curent
     * @param database baza de date
     * @param user utilizatorul
     */
    public void loginSuccess(final Database database, final User user) {
        database.setCurrentMoviesList(new ArrayList<>());
        database.setPage(new AuthenticatedHomepage());
        database.setUserIndex(database.getUsers().indexOf(user));
        database.setSuccessOutput();
    }

    /**
     * Metoda care verifica daca logarea este posibila
     * @param database baza de date
     * @param credentials acreditările
     */
    public void login(final Database database, final Credentials credentials) {
        String username = credentials.getName();
        String password = credentials.getPassword();
        for (User user : database.getUsers()) {
            Credentials userCreds = user.getCredentials();
            // daca numele de utilizator si parola sunt egale cu cele din baza de date
            if (userCreds.getName().equals(username)) {
                if (userCreds.getPassword().equals(password)) {
                    loginSuccess(database, user);
                } else {
                    loginError(database);
                }
                return;
            }
        }
        loginError(database);
    }

    @Override
    public void onPage(final Database database, final Action action) {
        // de pe pagina de login putem realiza doar actiunea de logare
        if (action.getFeature().equals("login")) {
            login(database, action.getCredentials());
        } else {
            database.setErrorOutput();
        }
    }

    @Override
    public void changePage(final Database database, final Action action) {
        // de pe pagina de login ne putem muta doar pe pagina de inregistrare
        if (action.getPage().equals("register")) {
            database.setPage(new RegisterPage());
        } else {
            loginError(database);
        }
    }

    @Override
    public void backPage(Database database) {
        database.setErrorOutput();
    }
    @Override
    public void modifyDatabase(Database database, Action action) {
        switch (action.getFeature()) {
            case "add" -> database.addMovie(action.getAddedMovie());
            case "delete" -> database.deleteMovie(action.getDeletedMovie());
            default -> database.setErrorOutput();
        }
    }
}
