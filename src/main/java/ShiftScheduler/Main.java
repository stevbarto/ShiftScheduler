package ShiftScheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main extends Application {

    private FXMLLoader loader;

    private LoginController loginController;

    WindowMaker viewWindow;
    private Stage windowStage;
    private Parent parent;
    private Scene scene;

    private ShiftScheduler scheduler;
    private DataFileGenerator creator;
    private EnvironmentGenerator config;



    /**
     * Start method to begin execution of the user interface.
     * @param stage JavaFX Stage
     * @throws Exception If the fxml file is not in place.
     */
    @Override
    public void start(Stage stage) throws Exception {

        scheduler = new ShiftScheduler();
        createGenerators();

        final FXMLLoader loader = new FXMLLoader();
        loader.setClassLoader(getClass().getClassLoader());
        final String address = "../ShiftScheduler/src/main/resources/LogIn.fxml";
        final InputStream fxmlStream = new FileInputStream(address);
        final Parent parent = loader.load(fxmlStream);

        //loader = new FXMLLoader(getClass().getResource("src/main/resources/LogIn.fxml"));

        //Parent parent = loader.load();
        //Parent parent = FXMLLoader.load(getClass().getResource("Schedule.fxml"));
        loginController = loader.getController();

        loginController.setScheduler(scheduler);

        Scene scene = new Scene(parent);

        stage.setTitle("Log In");
        //scheduleStage.setTitle("Schedule");

        stage.setScene(scene);

        stage.show();

    }


    public static void main(String[] args) {

        launch(args);

    }

    private void createGenerators() {

        this.creator = null;
        try {
            this.creator = new DataFileGenerator(this.scheduler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.config = new EnvironmentGenerator(this.scheduler);

        File distances = new File("Distances.csv");
        File employees = new File("Employees.csv");
        File locations = new File("Locations.csv");
        File posts = new File("Posts.csv");
        File shifts = new File("Shifts.csv");
        File scheduleFile = new File("LastSchedule.csv");

        boolean filesExist = distances.exists() && employees.exists() &&
                locations.exists() && posts.exists() && shifts.exists() && scheduleFile.exists();

        try {
            if (!filesExist) {
                this.creator.generateNewCsvFiles();
                this.config.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.scheduler.loadData();

    }

}
