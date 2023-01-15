package workflow.pages.authenticated;

import workflow.Database;
import workflow.pages.unauthenticated.UnauthenticatedHomepage;
import workflow.pages.Page;
import workflow.io.Action;
import workflow.fileio.User;

import java.util.ArrayList;

public final class UpgradesPage implements Page {
    public static final int TOKENS_COUNT = 10;
    @Override
    public void onPage(final Database database, final Action action) {
        // de pe pagina de upgrades putem cumpara cont premium sau tokens
        switch (action.getFeature()) {
            case "buy premium account" -> buyPremiumAccount(database);
            case "buy tokens" -> buyTokens(database, action);
            default -> database.setErrorOutput();
        }
    }

    @Override
    public void changePage(final Database database, final Action action) {
        // de pe pagina de upgrades ne putem muta pe movies sau putem realiza delogarea
        switch (action.getPage()) {
            case "logout" -> {
                database.setPage(new UnauthenticatedHomepage());
                database.resetUser();
                database.setPages(new ArrayList<String>());
            }
            case "movies" -> {
                database.getPages().add("upgrades");
                database.setPage(new MoviesPage());
                database.setCurrentMoviesList();
                database.setSuccessOutput();
            }
            case "homepage" -> database.setPage(new AuthenticatedHomepage());
            default -> database.setErrorOutput();
        }
    }

    @Override
    public void backPage(Database database) {
        if (database.getPages().size() > 0) {
            String previousPage = database.getPages().get(database.getPages().size() - 1);
            database.getPages().remove(database.getPages().size() - 1);
            switch(previousPage) {
                case "see details" -> database.setPage(new SeeDetailsPage());
                case "movies" -> database.setPage(new MoviesPage());
                case "upgrades" -> database.setPage(new UpgradesPage());
                case "homepage" -> database.setPage(new AuthenticatedHomepage());
                default -> database.setErrorOutput();
            }
        } else {
            database.setErrorOutput();
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
     * Metoda pentru a cumpara cont premium
     * @param database baza de date
     */
    public void buyPremiumAccount(final Database database) {
        User user = database.getUsers().get(database.getUserIndex());
        // daca contul utilizatorului este deja premium eroare
        if (user.getCredentials().getAccountType().equals("premium")) {
            database.setErrorOutput();
        } else {
            // daca are destule tokens
            if (user.getTokensCount() >= TOKENS_COUNT) {
                /* setam contul ca fiind unul premium si scadem cu 10 tokens
                 (pretul pentru a cumpara un cont premium) */
                user.getCredentials().setAccountType("premium");
                user.setTokensCount(user.getTokensCount() - TOKENS_COUNT);
                database.setOutput(null);
            } else {
                database.setErrorOutput();
            }
        }
    }

    /**
     * Metoda pentru a schimba balance cu tokens
     * @param database baza de date
     * @param action actiune
     */
    public void buyTokens(final Database database, final Action action) {
        User user = database.getUsers().get(database.getUserIndex());
        int userBalance = Integer.parseInt(user.getCredentials().getBalance());
        // daca vrea sa cumpere mai multe tokens decat are balance eroare
        if (action.getCount() > userBalance) {
            database.setErrorOutput();
        } else {
            // realiza schimbarea intre balance si tokens
            String newBalance = String.valueOf(userBalance - action.getCount());
            user.getCredentials().setBalance(newBalance);
            user.setTokensCount(user.getTokensCount() + action.getCount());
            database.setOutput(null);
        }
    }

}
