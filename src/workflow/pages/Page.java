package workflow.pages;

import workflow.Database;
import workflow.io.Action;

import javax.xml.crypto.Data;

public interface Page {
    /**
     * metoda abstracta de 'on page'
     */
    void onPage(Database database, Action action);

    /**
     * metoda abstracta de schimbare a paginii
     */
    void changePage(Database database, Action action);

    /**
     *
     */
    void backPage(Database database);
    void modifyDatabase(Database database, Action action);
}
