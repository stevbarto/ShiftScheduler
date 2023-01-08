package ShiftScheduler;

import ShiftScheduler.ShiftScheduler;
import ShiftScheduler.WindowController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

/**
 * Controller for the login window, handles user interface for login operations.
 * @author Steven Barton
 */
public class LoginController extends WindowController {

    private String userName;
    private String passWord;
    private ShiftScheduler scheduler;


    private String failedLoginMessage;

    private SimpleDateFormat formatDate;

    private Parent root;

    @FXML
    private Button loginButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Label errorMessage;

    @FXML
    private Label regionLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label usernameLabel;

    /**
     * Method initializes the login controller operations.
     */
    public void initialize() {
        failedLoginMessage = "Incorrect username or password!";

        //schedule = new Schedule();
        userName = "0";
        passWord = "0";

        //formatDate = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    }

    /**
     * Listener method retrieves user input into the username field.
     */
    public void userNameListener() {
        usernameField.setOnKeyReleased(event -> {
            this.userName = usernameField.getText();
        });
    }

    /**
     * Listener method retrieves user input into the password field.
     */
    public void passWordListener() {
        passwordField.setOnKeyReleased(event -> {
            this.passWord = passwordField.getText();

            // TODO: Code to try to get the password to not be visible...
            //char saveLast = passWord.charAt(passWord.length() - 1);
            // String setField = "";
            //for (int i = 0; i < passWord.length() - 1; i++) {
            //    setField = setField + '*';
            // }
            //setField = setField + saveLast;

            //passwordField.setText(setField);
        });
    }

    /**
     * Listener method recieves input to the login button.  Handles authentication and initialization of the application.
     */
    public void loginButtonListener() {
        loginButton.setOnMouseClicked(event -> {

            Stage thisStage = (Stage) loginButton.getScene().getWindow();

            ShiftSchedulerController controller = new ShiftSchedulerController();

            //scheduler.viewWindow.buildScene("../ShiftScheduler/src/main/resources/ShiftScheduler.fxml", new ShiftScheduler.ShiftSchedulerController(), schedule);

            // SchedulerController schedulerController = loader.getController();
            // Now able to access methods from the other controller.

            while (userName.equals("0") || passWord.equals("0")) {
                this.userName = usernameField.getText();
                this.passWord = passwordField.getText();
            }

            if (scheduler.validateUser(userName, passWord)) {

                // code here to initialize the schedule window
                try {

                    scheduler.loadSchedulerScreen();

                    thisStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //usernameField.clear();
                userName = "0";
                //passwordField.clear();
                passWord = "0";
                errorMessage.setText(failedLoginMessage);
                errorMessage.setVisible(true);
            }

        });
    }

    /**
     * Listener method recieves input into the cancel button, exits the application.
     */
    public void cancelButtonListener() {
        cancelButton.setOnMouseClicked(event -> {
            Platform.exit();
        });
    }

    public void setScheduler(ShiftScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void loadWindowData(ShiftScheduler scheduler) {
        //No code here for the login screen, this is to pass schedule objects to forms for saving data.
    }

    @Override
    public void writeMessage(String message) {
        //No code here for login screen, lambda expression used instead.
    }
}