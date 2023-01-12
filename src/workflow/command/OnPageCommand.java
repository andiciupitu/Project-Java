package workflow.command;

import workflow.io.Action;
import workflow.Command;
import workflow.Database;

public final class OnPageCommand implements Command {
    private Database database;
    private Action action;

    public OnPageCommand(final Database database, final Action action) {
        this.database = database;
        this.action = action;
    }

    @Override
    public void execute() {
        database.setOutput(null);
        database.getPage().onPage(database, action);
    }
}
