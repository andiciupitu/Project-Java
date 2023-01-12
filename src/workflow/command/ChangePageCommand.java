package workflow.command;

import workflow.io.Action;
import workflow.Command;
import workflow.Database;

public final class ChangePageCommand implements Command {
    private Database database;
    private Action action;

    public ChangePageCommand(final Database database, final Action action) {
        this.database = database;
        this.action = action;
    }

    @Override
    public void execute() {
        database.getPage().changePage(database, action);
    }
}
