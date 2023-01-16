package workflow.command;

import workflow.Command;
import workflow.Database;

public final class BackPageCommand implements Command {

    private Database database;
    public BackPageCommand(final Database database) {
        this.database = database;
    }
    @Override
    public void execute() {
        database.getPage().backPage(database);
    }
}
