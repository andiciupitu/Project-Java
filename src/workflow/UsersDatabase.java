package workflow;

import workflow.fileio.Credentials;
import workflow.fileio.Movie;
import workflow.fileio.Notifications;
import workflow.fileio.User;

import java.util.ArrayList;

public class UsersDatabase {
    private ArrayList<User> users;
    private ArrayList<String> subscribedGenre;
    public UsersDatabase() {

    }
    public UsersDatabase(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "UsersDatabase{" +
                "users=" + users +
                ", subscribedGenre=" + subscribedGenre +
                '}';
    }
}
