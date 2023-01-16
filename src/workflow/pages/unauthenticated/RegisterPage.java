package workflow.pages.unauthenticated;

import workflow.Database;
import workflow.pages.authenticated.AuthenticatedHomepage;
import workflow.pages.Page;
import workflow.io.Action;
import workflow.fileio.User;

public final class RegisterPage implements Page {
    /**
     * Metoda pentru cazul in care nu se poate realiza inregistrarea
     * @param database baza de date
     */
    public void registerError(final Database database) {
        database.setPage(new UnauthenticatedHomepage());
        database.setErrorOutput();
    }

    /**
     * Metoda care realizeaza inregistrarea unui nou utilizator si adaugarea lui in baza de date
     * @param database baza de date
     * @param action actiunea
     */
    public void registerSuccess(final Database database, final Action action) {
        User user = new User(action.getCredentials());
        database.getUsers().add(user);
        database.setUserIndex(database.getUsers().indexOf(user));
        database.setPage(new AuthenticatedHomepage());
        database.setSuccessOutput();
    }

    /**
     * Metoda care verifica posbilitatea de inregistrare a unui nou utilizator
     * @param database baza de date
     * @param action actiunea
     */
    public void register(final Database database, final Action action) {
        String username = action.getCredentials().getName();
        for (User user : database.getUsers()) {
            if (user.getCredentials().getName().equals(username)) {
                // daca exista deja un utilizator in baza de date cu acelasi nume -> eroare
                registerError(database);
                return;
            }
        }
        registerSuccess(database, action);
    }

    @Override
    public void onPage(final Database database, final Action action) {
        // de pagina de register pot realiza doar actiunea de inregistrare
        if (action.getFeature().equals("register")) {
            register(database, action);
            return;
        }
        registerError(database);
    }

    @Override
    public void changePage(final Database database, final Action action) {
        // de pagina de register ma pot muta doar pe pagina de logare
        if (action.getFeature().equals("login")) {
            database.setPage(new LoginPage());
        } else {
            registerError(database);
        }
    }

    @Override
    public void backPage(final Database database) {
        database.setErrorOutput();
    }
    @Override
    public void modifyDatabase(final Database database, final Action action) {
        switch (action.getFeature()) {
            case "add" -> database.addMovie(action.getAddedMovie());
            case "delete" -> database.deleteMovie(action.getDeletedMovie());
            default -> database.setErrorOutput();
        }
    }
}
