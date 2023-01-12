package workflow.command;

import workflow.Command;
import workflow.Database;

public class BackPageCommand implements Command {

    private Database database;
    public BackPageCommand(Database database) {
        this.database = database;
    }
    @Override
    public void execute() {
        database.getPage().backPage(database);
    }
}
