package workflow.pages.unauthenticated;

import workflow.Database;
import workflow.pages.Page;
import workflow.io.Action;

public final class UnauthenticatedHomepage implements Page {
    @Override
    public void onPage(final Database database, final Action action) {
        database.setErrorOutput();
    }

    @Override
    public void changePage(final Database database, final Action action) {
        // daca suntem neautentificati putem schimba pe pagina de Login sau Register
        switch (action.getPage()) {
            case "login" :
                database.setPage(new LoginPage());
                break;
            case "register" :
                database.setPage(new RegisterPage());
                break;
            default :
                database.setErrorOutput();
                break;
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
