import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import workflow.Command;
import workflow.Database;
import workflow.Invoker;
import workflow.command.BackPageCommand;
import workflow.command.ChangePageCommand;
import workflow.command.ModifyDatabaseCommand;
import workflow.command.OnPageCommand;
import workflow.io.Input;
import workflow.io.Action;
import workflow.io.Output;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public final class Main {
    private Main() {

    }
    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(args[0]), Input.class);
        Invoker invoker = new Invoker();
        ArrayList<Output> output = new ArrayList<>();
        Database database = new Database(inputData.getUsers(), inputData.getMovies());
        ArrayList<Action> actions = inputData.getActions();
        for (int i = 0; i < actions.size(); i++) {
            Command command;
            if (actions.get(i).getType().equals("on page")) {
                // daca comanda este una de tip onPage
                 command = new OnPageCommand(database, actions.get(i));
            } else if (actions.get(i).getType().equals("change page")){
                // daca comanda este una de tip changePage
                 command = new ChangePageCommand(database, actions.get(i));
            } else if (actions.get(i).getType().equals("back")){
               // comanda de tip back
                command = new BackPageCommand(database);
            } else {
                // comanda de modificare a bazei de date
                command = new ModifyDatabaseCommand(database, actions.get(i));
            }
            database.setOutput(null);
            // rulam comanda prin invoker
            invoker.run(command);
            // luam output curent
            Output outputNow = database.getOutput();
            // daca exista informatie de pus in fisier
            if (outputNow != null) {
                Output loveDeepCopy = objectMapper.
                        readValue(objectMapper.writeValueAsBytes(outputNow), Output.class);
                output.add(loveDeepCopy);
            }
        }
        // in cazul in care avem un user logat cu un cont premium trebuie sa ii facem o recomandare
        if((database.getUserIndex() != -1)
                && (database.getUsers().get(database.getUserIndex()).getCredentials().getAccountType().equals("premium")))
        {
            // apelam metoda de recomandare
            database.makeRecommendation();
            // punem informatia in output
            Output outputNow = database.getOutput();
            Output loveDeepCopy = objectMapper.
                    readValue(objectMapper.writeValueAsBytes(outputNow), Output.class);
            output.add(loveDeepCopy);
        }
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(args[1]), output);
        objectWriter.writeValue(new File("output.txt"), output);
    }
}
