package workflow.command;

import workflow.Command;
import workflow.Database;
import workflow.io.Action;

public class ModifyDatabaseCommand implements Command {
    private Database database;
    private Action action;
    public ModifyDatabaseCommand(Database database, Action action) {
        this.database = database;
        this.action = action;
    }

    @Override
    public void execute() {
        database.getPage().modifyDatabase(database, action);
    }
}
