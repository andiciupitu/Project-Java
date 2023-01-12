package workflow.pages.authenticated;

import workflow.Database;
import workflow.pages.unauthenticated.UnauthenticatedHomepage;
import workflow.pages.Page;
import workflow.io.Action;

import java.util.ArrayList;

public final class AuthenticatedHomepage implements Page {
    @Override
    public void onPage(final Database database, final Action action) {
        database.setErrorOutput();
    }

    @Override
    public void changePage(final Database database, final Action action) {
        // daca suntem autentificati putem schimba pe pagina Movies/Upgrades sau ne putem deloga
        switch (action.getPage()) {
            case "logout" -> {
                database.setPage(new UnauthenticatedHomepage());
                database.resetUser();
                database.setPages(new ArrayList<String>());
            }
            case "movies" -> {
                database.setPage(new MoviesPage());
                database.setCurrentMoviesList();
                database.setSuccessOutput();
                database.getPages().add("movies");
            }
            case "upgrades" -> {
                database.setPage(new UpgradesPage());
                database.getPages().add("upgrades");
            }
            default -> database.setErrorOutput();
        }
    }

    @Override
    public void backPage(Database database) {
        database.setErrorOutput();
    }
    @Override
    public void modifyDatabase(Database database, Action action) {

    }
}
