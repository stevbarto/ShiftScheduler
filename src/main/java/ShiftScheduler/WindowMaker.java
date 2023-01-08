package ShiftScheduler;

/**
 * Iterface for a lambda function to create windows based on the data parameters. Eliminates redundant blocks of code
 * used for opening new windows within the application.  Provides one simple syntax for opening each window and passing
 * data to the new window.
 *
 * @author Steven Barton
 */
public interface WindowMaker {

    /**
     * Builds the window based on the provided parameters.
     * @param windowFile
     * @param controller
     * @param schedule
     */
    public void buildScene(String windowFile, WindowController controller, Schedule schedule);
}
