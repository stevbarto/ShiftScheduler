package ShiftScheduler;

/**
 * Window controller interface, defines suggested common functions and a common parent for all controllers.
 * @author Steven Barton
 */
public abstract class WindowController {

    Schedule schedule;

    /**
     * Loads a schedule object to provide data to the controller.
     * @param scheduler ShiftScheduler
     */
    public abstract void loadWindowData(ShiftScheduler scheduler);

    /**
     * Writes messages to the screen as needed.
     * @param message String
     */
    public abstract void writeMessage(String message);
}
