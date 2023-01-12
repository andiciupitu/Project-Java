package workflow;

public final class Invoker {
    /**
     * Metoda de Run a invokerului
     * @param command
     */
    public void run(final Command command) {
        command.execute();
    }
}
