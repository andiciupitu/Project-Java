package workflow.pages;

import workflow.Database;
import workflow.io.Action;


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
     * metoda abstracta pentru schimbarea paginii pe pagina precedenta
     */
    void backPage(Database database);

    /**
     * metoda abstracta pentru modificarea bazei de date(adaugare/stergere film)
     * @param database baza de date
     * @param action actiunea
     */
    void modifyDatabase(Database database, Action action);
}
