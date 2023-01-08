package ShiftScheduler;

import com.opencsv.exceptions.CsvException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class ShiftSchedulerController extends WindowController {

        private static final String[] DAYATTRIBUTES = {"dayOne", "dayTwo", "dayThree", "dayFour", "dayFive", "daySix",
                "daySeven", "dayEight", "dayNine", "dayTen", "dayEleven", "dayTwelve", "dayThirteen", "dayFourteen"};

        private ShiftScheduler scheduler;

        private KMeansResults analysis;

        private int axisX;
        private int axisY;

        private int seriesOneMax;
        private int seriesTwoMax;
        private int seriesThreeMax;
        private int seriesOneMin;
        private int seriesTwoMin;
        private int seriesThreeMin;

        private boolean groupOne;
        private boolean groupTwo;
        private boolean groupThree;

        ObservableList<Schedule> availableSchedules;
        ObservableList<Schedule> templateSchedules;
        ObservableList<Schedule> assignedSchedules;
        ObservableList<Employee> employees;
        ObservableList<String> employeeNames;
        ObservableList<Employee> assignedEmployees;
        ObservableList<Post> posts;
        ObservableList<String> postNames;
        ObservableList<Post> assignedPosts;

        ArrayList<TableColumn<Schedule, String>> templateCols;
        ArrayList<TableColumn<Schedule, String>> availableCols;
        ArrayList<TableColumn<Schedule, String>> assignedCols;
        ArrayList<Spinner> shiftMins;
        ArrayList<Spinner> postMenus;
        ArrayList<Label> totalLabels;

        LocalDate startDate;

        Employee o1;
        Employee o2;


        @FXML
        private Slider addDailyHours;

        @FXML
        private Slider addMaxDays;

        @FXML
        private TableColumn<Schedule, Integer> addSelector;

        @FXML
        private TextField addShiftName;

        @FXML
        private Slider addStartHour;

        @FXML
        private Slider addWeeklyHours;

        @FXML
        private TableColumn<Schedule, String> assignedDayEight;

        @FXML
        private TableColumn<Schedule, String> assignedDayEleven;

        @FXML
        private TableColumn<Schedule, String> assignedDayFive;

        @FXML
        private TableColumn<Schedule, String> assignedDayFour;

        @FXML
        private TableColumn<Schedule, String> assignedDayFourteen;

        @FXML
        private TableColumn<Schedule, String> assignedDayNine;

        @FXML
        private TableColumn<Schedule, String> assignedDayOne;

        @FXML
        private TableColumn<Schedule, String> assignedDaySeven;

        @FXML
        private TableColumn<Schedule, String> assignedDaySix;

        @FXML
        private TableColumn<Schedule, String> assignedDayTen;

        @FXML
        private TableColumn<Schedule, String> assignedDayThirteen;

        @FXML
        private TableColumn<Schedule, String> assignedDayThree;

        @FXML
        private TableColumn<Schedule, String> assignedDayTwelve;

        @FXML
        private TableColumn<Schedule, String> assignedDayTwo;

        @FXML
        private TableView<Schedule> assignedTable;

        @FXML
        private TableColumn<Schedule, String> availableDayEight;

        @FXML
        private TableColumn<Schedule, String> availableDayEleven;

        @FXML
        private TableColumn<Schedule, String> availableDayFive;

        @FXML
        private TableColumn<Schedule, String> availableDayFour;

        @FXML
        private TableColumn<Schedule, String> availableDayFourteen;

        @FXML
        private TableColumn<Schedule, String> availableDayNine;

        @FXML
        private TableColumn<Schedule, String> availableDayOne;

        @FXML
        private TableColumn<Schedule, String> availableDaySeven;

        @FXML
        private TableColumn<Schedule, String> availableDaySix;

        @FXML
        private TableColumn<Schedule, String> availableDayTen;

        @FXML
        private TableColumn<Schedule, String> availableDayThirteen;

        @FXML
        private TableColumn<Schedule, String> availableDayThree;

        @FXML
        private TableColumn<Schedule, String> availableDayTwelve;

        @FXML
        private TableColumn<Schedule, String> availableDayTwo;

        @FXML
        private TableView<Schedule> availableTable;

        @FXML
        private NumberAxis lineChartScale1;

        @FXML
        private NumberAxis lineChartScale2;

        @FXML
        private DatePicker periodStartDate;

        @FXML
        private ScatterChart<Number, Number> scatterPlot;

        @FXML
        private NumberAxis scatterPlotScale1;

        @FXML
        private NumberAxis scatterPlotScale2;

        @FXML
        private AnchorPane scheduleDataTab;

        @FXML
        private TableView<Schedule> scheduleTemplateTable;

        @FXML
        private AnchorPane schedulerAnchor;

        @FXML
        private Pane schedulerPane;

        @FXML
        private TableColumn<Schedule, String> templateDayEight;

        @FXML
        private TableColumn<Schedule, String> templateDayEleven;

        @FXML
        private TableColumn<Schedule, String> templateDayFive;

        @FXML
        private TableColumn<Schedule, String> templateDayFour;

        @FXML
        private TableColumn<Schedule, String> templateDayFourteen;

        @FXML
        private TableColumn<Schedule, String> templateDayNine;

        @FXML
        private TableColumn<Schedule, String> templateDayOne;

        @FXML
        private TableColumn<Schedule, String> templateDaySeven;

        @FXML
        private TableColumn<Schedule, String> templateDaySix;

        @FXML
        private TableColumn<Schedule, String> templateDayTen;

        @FXML
        private TableColumn<Schedule, String> templateDayThirteen;

        @FXML
        private TableColumn<Schedule, String> templateDayThree;

        @FXML
        private TableColumn<Schedule, String> templateDayTwelve;

        @FXML
        private TableColumn<Schedule, String> templateDayTwo;

        @FXML
        private Slider updateDailyHours;

        @FXML
        private Slider updateMaxDays;

        @FXML
        private TextField updateShiftName;

        @FXML
        private Slider updateStartHour;

        @FXML
        private Slider updateWeeklyHours;

        @FXML
        private TabPane mainTabPane;

        @FXML
        private LineChart<Number, Number> lineChart;

        @FXML
        private RadioButton groupOneButton;

        @FXML
        private RadioButton groupTwoButton;

        @FXML
        private RadioButton groupThreeButton;

        @FXML
        private Spinner<Post> postFive;

        @FXML
        private Spinner<Post> postFour;

        @FXML
        private Spinner<Post> postFourteen;

        @FXML
        private Spinner<Post> postNine;

        @FXML
        private Spinner<Post> postOne;

        @FXML
        private Spinner<Post> postSeven;

        @FXML
        private Spinner<Post> postSix;

        @FXML
        private Spinner<Post> postTen;

        @FXML
        private Spinner<Post> postThirteen;

        @FXML
        private Spinner<Post> postThree;

        @FXML
        private Spinner<Post> postTwelve;

        @FXML
        private Spinner<Post> postTwo;

        @FXML
        private Spinner<Post> postEight;

        @FXML
        private Spinner<Post> postEleven;

        @FXML
        private Button removeButton;

        @FXML
        private Spinner<Integer> dayEight;

        @FXML
        private Spinner<Integer> dayEleven;

        @FXML
        private Spinner<Integer> dayFive;

        @FXML
        private Spinner<Integer> dayFour;

        @FXML
        private Spinner<Integer> dayFourteen;

        @FXML
        private Spinner<Integer> dayNine;

        @FXML
        private Spinner<Integer> dayOne;

        @FXML
        private Spinner<Integer> daySeven;

        @FXML
        private Spinner<Integer> daySix;

        @FXML
        private Spinner<Integer> dayTen;

        @FXML
        private Spinner<Integer> dayThirteen;

        @FXML
        private Spinner<Integer> dayThree;

        @FXML
        private Spinner<Integer> dayTwelve;

        @FXML
        private Spinner<Integer> dayTwo;

        @FXML
        private Spinner<String> employeeMenu;

        @FXML
        private Button addButton;

        @FXML
        private Label totalEight;

        @FXML
        private Label totalEleven;

        @FXML
        private Label totalThirteen;

        @FXML
        private Label totalFive;

        @FXML
        private Label totalFour;

        @FXML
        private Label totalFourteen;

        @FXML
        private Label totalNine;

        @FXML
        private Label totalOne;

        @FXML
        private Label totalSeven;

        @FXML
        private Label totalSix;

        @FXML
        private Label totalTen;

        @FXML
        private Label totalThree;

        @FXML
        private Label totalTwelve;

        @FXML
        private Label totalTwo;

        @FXML
        private TableView<Schedule> assignedEmployeeTable;

        @FXML
        private TableColumn<Schedule, String> employeeCol;

        @FXML
        private Slider weekLengthSlider;

        @FXML
        private Button generateButton;

        @FXML
        private Label numSchedules;

        @FXML
        private CheckBox rotateFr;

        @FXML
        private CheckBox rotateSa;

        @FXML
        private CheckBox rotateSu;

        @FXML
        private CheckBox rotateTh;

        @FXML
        private CheckBox rotateTu;

        @FXML
        private CheckBox rotateWe;

        @FXML
        private Slider hrPerDay;

        @FXML
        private Slider hrPerWeek;

        @FXML
        private CheckBox rotateMo;

        @FXML
        private CheckBox allSa;

        private int selectorIndex;

        private boolean initializing;
        private ObservableList<String> postNamesOne;
        private ObservableList<String> postNamesTwo;
        private ObservableList<String> postNamesThree;
        private ObservableList<String> postNamesFour;
        private ObservableList<String> postNamesFive;
        private ObservableList<String> postNamesSix;
        private ObservableList<String> postNamesSeven;
        private ObservableList<String> postNamesEight;
        private ObservableList<String> postNamesNine;
        private ObservableList<String> postNamesTen;
        private ObservableList<String> postNamesEleven;
        private ObservableList<String> postNamesTwelve;
        private ObservableList<String> postNamesThirteen;
        private ObservableList<String> postNamesFourteen;
        private ArrayList<ObservableList<String>> postLists;

        public ShiftSchedulerController() {
        }

        public void initialize() {

                selectorIndex = 0;

                mainTabPane.setStyle("-fx-font-size: 11pt; -fx-font-family: Arial;");
                scatterPlotScale1.setStyle("-fx-font-size: 11pt; -fx-font-family: Arial;");
                scatterPlotScale2.setStyle("-fx-font-size: 11pt; -fx-font-family: Arial;");
                lineChart.getXAxis().setStyle("-fx-font-size: 11pt; -fx-font-family: Arial;");
                lineChart.getYAxis().setStyle("-fx-font-size: 11pt; -fx-font-family: Arial;");

                periodStartDate.setOnAction(event -> {
                        periodStartDateSpinnerListener();
                });
                addButton.setOnAction(event -> {
                        addButtonListener();
                });
                removeButton.setOnAction(event -> {
                        removeButtonListener();
                });
                employeeMenu.setOnMouseReleased(event -> {
                        employeeMenuListener();
                });

                ArrayList<CheckBox> rotOffBoxes = new ArrayList<CheckBox>(Arrays.asList(rotateMo, rotateTu, rotateWe, rotateTh, rotateFr, rotateSa, rotateSu));

                generateButton.setOnAction(event -> {
                        scheduler.setDailyHours((int) hrPerDay.getValue());
                        scheduler.setWeeklyHours((int) hrPerWeek.getValue());

                        for (int i = 0; i < rotOffBoxes.size(); i++) {
                                if (rotOffBoxes.get(i).isSelected()) {
                                        try {
                                                scheduler.addRotatingDayOff(i);
                                        } catch (Exception e) {
                                                e.printStackTrace();
                                        }
                                }
                                else {
                                        scheduler.removeRotatingDayOff(i);
                                }
                        }


                        try {
                                clearChartData();
                                prepChartData();
                        } catch (IOException e) {
                                e.printStackTrace();
                        } catch (CsvException e) {
                                e.printStackTrace();
                        }
                        //freshTemplateSchedules();
                });

                shiftMins = new ArrayList<Spinner>(Arrays.asList(dayOne, dayTwo, dayThree, dayFour, dayFive, daySix,
                        daySeven, dayEight, dayNine, dayTen, dayEleven, dayTwelve, dayThirteen, dayFourteen));

                postMenus = new ArrayList<Spinner>(Arrays.asList(postOne, postTwo, postThree, postFour, postFive,
                        postSix, postSeven, postEight, postNine, postTen, postEleven, postTwelve, postThirteen, postFourteen));

                for (int i = 0; i < shiftMins.size(); i++) {
                        shiftMins.get(i).setOnMouseReleased(event -> {
                                shiftMinimumListener();
                        });
                }

                for (int i = 0; i < postMenus.size(); i++) {
                        postMenus.get(i).setOnMouseReleased(event -> {
                                postMenuListener();
                        });
                }

                templateCols = new ArrayList<TableColumn<Schedule, String>>(Arrays.asList(templateDayOne, templateDayTwo,
                                templateDayThree, templateDayFour, templateDayFive, templateDaySix, templateDaySeven,
                                templateDayEight, templateDayNine, templateDayTen, templateDayEleven, templateDayTwelve,
                                templateDayThirteen, templateDayFourteen));

                availableCols = new ArrayList<TableColumn<Schedule, String>>(Arrays.asList(availableDayOne, availableDayTwo,
                                availableDayThree, availableDayFour, availableDayFive, availableDaySix, availableDaySeven,
                                availableDayEight, availableDayNine, availableDayTen, availableDayEleven, availableDayTwelve,
                                availableDayThirteen, availableDayFourteen));

                assignedCols = new ArrayList<TableColumn<Schedule, String>>(Arrays.asList(assignedDayOne, assignedDayTwo,
                                assignedDayThree, assignedDayFour, assignedDayFive, assignedDaySix, assignedDaySeven,
                                assignedDayEight, assignedDayNine, assignedDayTen, assignedDayEleven, assignedDayTwelve,
                                assignedDayThirteen, assignedDayFourteen));

                this.assignedSchedules = (FXCollections.observableList(new ArrayList<Schedule>()));
                this.assignedEmployees = (FXCollections.observableList(new ArrayList<Employee>()));
                this.assignedPosts = (FXCollections.observableList(new ArrayList<Post>()));
                this.availableSchedules = (FXCollections.observableList(new ArrayList<Schedule>()));

                availableTable.setOnMouseClicked(event -> {
                        adjustPostsAndEmployees();
                });

                assignedTable.setOnMouseClicked(event -> {

                });

                availableTable.getSelectionModel().selectFirst();

        }

        private void clearChartData() {
                scatterPlot.getData().clear();
                lineChart.getData().clear();
        }

        private void adjustPostsAndEmployees() {

        }

        private void employeeMenuListener() {

                // Get the first schedule from the list of one is not selected...
                // availableTable.getSelectionModel().selectFirst();
                if (availableTable.getSelectionModel().getSelectedItem() == null) {
                        return;
                        // TODO: display a message here asking user to select a schedule...
                }

                Schedule selectedSched = availableTable.getSelectionModel().getSelectedItem();


                // Get the selected employee
                Employee selected = null;

                ArrayList<Employee> employeeList = scheduler.getEmployees();

                for (Employee e: employeeList) {
                        if (employeeMenu.getValue().equals(e.getName())) {
                                selected = e;
                        }
                }

                // Get work days from the schedule
                selectedSched.getWorkDays();
                // Get the primary post for the selected employee
                int primary = selected.getPrimaryPost();


                // Get the post object from the id
                Post thisPost = null;

                for (Post p:scheduler.getPosts()) {
                        if (p.getId() == primary) {
                                thisPost = p;
                        }
                }

                // Set the spinner value to the post where the employee is a primary if they are one


                for (int i = 0; i < postMenus.size(); i++) {

                        if (selectedSched.getWorkDays()[i] == 1) {
                                postMenus.get(i).getValueFactory().setValue(thisPost.getName());
                        }
                        else {
                                postMenus.get(i).getValueFactory().setValue("OFF");
                        }
                }
        }

        private void postMenuListener() {
        }

        private void shiftMinimumListener() {
        }

        private void removeButtonListener() {
                // TODO: Employees not getting added back in on remove...
                // If there are no employees assigned, get out
                if (assignedEmployees.size() == 0) {
                        return;
                }

                // Get the selected schedule and employee, remove from assigned and add to available
                Schedule selected = assignedTable.getSelectionModel().getSelectedItem();
                int selectInt = assignedTable.getSelectionModel().getSelectedIndex();

                int employeeId = selected.getEmployeeId();


                Employee empl = null;

                for (Employee e:assignedEmployees) {
                        if (e.getId() == employeeId) {
                                empl = e;
                        }
                }
                assignedEmployees.remove(empl);
                employees.add(empl);
                employeeNames.add(empl.getName());
                assignedSchedules.remove(selected);
                employees.sort((Employee o1, Employee o2)->o1.getName().compareTo(o2.getName()));
                employeeNames.sort((String o1, String o2)->o1.compareTo(o2));


                String[] postNames = selected.getSchedule();

                for (int i = 0; i < postNames.length; i++) {
                        if (!postNames[i].equals("OFF")) {
                                if (!postLists.get(i).contains(postNames[i])) {
                                        postLists.get(i).add(postNames[i]);
                                        sortPostLists();
                                }
                        }
                }


                // Selector index decrement
                this.selectorIndex--;

                // Get the work on and off days, update sum labels
                int[] posts = selected.getWorkDays();

                for (int i = 0; i < totalLabels.size(); i++) {
                        if (posts[i] == 1) {
                                int val = Integer.parseInt(totalLabels.get(i).getText());
                                val--;
                                totalLabels.get(i).setText(String.valueOf(val));
                        }
                }

                assignedTable.getSelectionModel().selectRange(0,0);
        }

        private void addButtonListener() {

                if (employeeMenu.getValue() != null && availableTable.getSelectionModel().getSelectedItem() != null) {

                        // Add all posts to the employee
                        Schedule s = availableTable.getSelectionModel().getSelectedItem();
                        String employeeName = employeeMenu.getValue();
                        Schedule selected = new Schedule(900, s.getEmployeeId(), scheduler.getDailyHours(), scheduler.getStartDate());

                        for (Integer i:s.getWorkDays()) {
                                selected.addDay(i);
                        }

                        int ind = 0;
                        for (String str:s.getSchedule()) {
                                selected.addPost(ind, str);
                                ind++;
                        }

                        selected.setEmployeeName(employeeName);

                        Employee empl = null;

                        for (Employee e:employees) {
                                if (e.getName().equals(employeeName)) {
                                        selected.setEmployeeId(e.getId());
                                        empl = e;
                                }
                        }

                        employees.remove(empl);
                        employees.sort((Employee o1, Employee o2)->o1.getName().compareTo(o2.getName()));
                        assignedEmployees.add(empl);
                        assignedSchedules.add(selected);
                        employeeNames.remove(empl.getName());
                        employeeNames.sort((String o1, String o2)->o1.compareTo(o2));
                        int[] remArr = new int[14];
                        for (int i = 0; i < postMenus.size(); i++) {

                                String remove = "";

                                if (selected.getWorkDays()[i] == 0) {
                                        selected.addPost(i, "OFF");
                                        remArr[i] = 0;
                                }
                                else {
                                        selected.addPost(i, postMenus.get(i).getValue() + "");
                                        if (!postMenus.get(i).getValue().equals("N") &&
                                                !postMenus.get(i).getValue().equals("O") &&
                                                !postMenus.get(i).getValue().equals("P") &&
                                                !postMenus.get(i).getValue().equals("Q") &&
                                                !postMenus.get(i).getValue().equals("R")) {

                                                remove = postMenus.get(i).getValueFactory().getValue().toString();
                                                remArr[i] = 1;
                                        }
                                        else {
                                                remArr[i] = 0;
                                        }
                                }

                                if (remArr[i] == 1) {
                                        postLists.get(i).remove(remove);
                                }

                                for (int j = 0; j < 14; j++) {
                                        remArr[j] = 0;
                                }

                        }

                        initializing = false;

                        sumColumns();


                }


        }

        private void sumColumns() {

                int size = assignedSchedules.size();

                ArrayList<Integer> skip = new ArrayList<Integer>();
                for (int i = 0; i < totalLabels.size(); i++) {
                        int shiftMin = scheduler.shiftMins[i];
                        int value = size - shiftMin;

                        if (assignedTable.getItems().size() > 0) {

                                Schedule selected = assignedTable.getItems().get(assignedTable.getItems().size() - 1);

                                int[] days = selected.getWorkDays();

                                if (days[i] == 1) {
                                        totalLabels.get(i).setText(String.valueOf(value));
                                }
                                else {
                                        skip.add(i);
                                }
                        }
                        else {
                                totalLabels.get(i).setText(String.valueOf(value));
                        }

                        if (value < 0) {
                                totalLabels.get(i).setTextFill(Color.web("#fc0303"));
                        }
                        else {
                                if (!skip.contains(i)) {
                                        totalLabels.get(i).setTextFill(Color.web("#0d0000"));
                                }
                        }
                }

                if (!initializing) {
                        this.selectorIndex++;
                }


        }

        private void useSelectionButtonListener() {

                ObservableList<Schedule> selection = scheduleTemplateTable.getSelectionModel().getSelectedItems();

                for (Schedule s:selection) {
                        this.templateSchedules.remove(s);
                        this.availableSchedules.add(s);
                }

        }

        private void periodStartDateSpinnerListener() {

                if (periodStartDate.getValue().getDayOfWeek().getValue() != 1) {
                        periodStartDate.setPromptText("Monday only!");
                }
                else {
                        scheduler.setStartDate(periodStartDate.getValue().getYear(),
                                periodStartDate.getValue().getMonthValue(), periodStartDate.getValue().getDayOfMonth());
                        updateTableHeaders();
                }

        }

        private void updateTableHeaders() {

                String[] days = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};

                for (int i = 0; i < 14; i++) {
                        LocalDate date = periodStartDate.getValue().plusDays(i);

                        String display = days[date.getDayOfWeek().getValue() - 1] +
                                " " + date.getMonthValue() + "/" + date.getDayOfMonth();

                        templateCols.get(i).setText(display);

                        availableCols.get(i).setText(display);

                        assignedCols.get(i).setText(display);
                }

        }

        private void prepChartData() throws IOException, CsvException {

                freshTemplateSchedules();

                // Read in the GroupData.csv file as the data set
                ArrayList<ArrayList<Integer>> rawData = this.scheduler.getHistoricDataMatrix();

                // Select the two axes
                this.axisX = scheduler.getXAxis();
                this.axisY = scheduler.getYAxis();

                // Get cluster data for the y axis
                this.analysis = new KMeansResults(scheduler.getClusterNum());
                this.analysis = scheduler.analyzeHistoricData(axisX, axisY);

                ArrayList<Integer> keys1 = this.analysis.getKeys(0);
                ArrayList<Integer> keys2 = this.analysis.getKeys(1);
                ArrayList<Integer> keys3 = this.analysis.getKeys(2);


                // Add data to new sets based on clustering (one attribute only is what works)
                XYChart.Series series1 = new XYChart.Series();
                series1.setName("KMeans Cluster One");
                XYChart.Series series2 = new XYChart.Series();
                series2.setName("KMeans Cluster Two");
                XYChart.Series series3 = new XYChart.Series();
                series3.setName("KMeans Cluster Three");
                XYChart.Series series4 = new XYChart.Series();
                series4.setName("Current max week length setting: " + scheduler.getMaxWeek());

                for (int i = 0; i < keys1.size(); i++) {
                        for (int j = 0; j < rawData.size(); j++) {
                                if (keys1.get(i) == j) {
                                        series1.getData().add(new XYChart.Data(rawData.get(j).get(axisX), rawData.get(j).get(axisY)));
                                }
                        }
                }

                for (int i = 0; i < keys2.size(); i++) {
                        for (int j = 0; j < rawData.size(); j++) {
                                if (keys2.get(i) == j) {
                                        series2.getData().add(new XYChart.Data(rawData.get(j).get(axisX), rawData.get(j).get(axisY)));
                                }
                        }
                }

                for (int i = 0; i < keys3.size(); i++) {
                        for (int j = 0; j < rawData.size(); j++) {
                                if (keys3.get(i) == j) {
                                        series3.getData().add(new XYChart.Data(rawData.get(j).get(axisX), rawData.get(j).get(axisY)));
                                }
                        }
                }

                for (int i = 0; i < rawData.size(); i++) {
                        series4.getData().add(new XYChart.Data(scheduler.getMaxWeek(), rawData.get(i).get(axisY)));
                }

                this.seriesOneMax = scheduler.metricsTool.getMax(series1);
                this.seriesTwoMax = scheduler.metricsTool.getMax(series2);
                this.seriesThreeMax = scheduler.metricsTool.getMax(series2);
                this.seriesOneMin = scheduler.metricsTool.getMin(series1);
                this.seriesTwoMin = scheduler.metricsTool.getMin(series2);
                this.seriesThreeMin = scheduler.metricsTool.getMin(series3);

                // Load the sets to the graphs
                populateLineChart();
                populateScatterChart(series1, series2, series3, series4);

        }

        private void freshTemplateSchedules() {
                scheduler.setMaxWeek((int) weekLengthSlider.getValue());
                this.availableSchedules = (FXCollections.observableList(scheduler.generateSchedules()));
                displayTemplateTable(this.availableSchedules);
                numSchedules.setText("Available Schedules: \n        " + this.availableSchedules.size());

        }

        private void displayTemplateTable(ObservableList<Schedule> availableSchedules) {

                // Build the template table on tab 1
                scheduleTemplateTable.setItems(availableSchedules);

                for (int i = 0; i < this.templateCols.size(); i++) {
                        templateCols.get(i).setCellValueFactory(new PropertyValueFactory<Schedule, String>(this.DAYATTRIBUTES[i]));
                }

                scheduleTemplateTable.getColumns().setAll(templateCols);

                // build the available table on tab 2

                availableTable.setItems(availableSchedules);

                for (int i = 0; i < availableCols.size(); i++) {
                        availableCols.get(i).setCellValueFactory(new PropertyValueFactory<Schedule, String>(this.DAYATTRIBUTES[i]));
                }

                availableTable.getColumns().setAll(availableCols);

                // build the empty selected table on tab 2

                assignedTable.setItems(assignedSchedules);

                for (int i = 0; i < assignedCols.size(); i++) {
                        assignedCols.get(i).setCellValueFactory(new PropertyValueFactory<Schedule, String>(this.DAYATTRIBUTES[i]));
                }

                assignedTable.getColumns().setAll(assignedCols);

                // build the employee name column by the assigned table

                assignedEmployeeTable.setItems(assignedSchedules);

                employeeCol.setCellValueFactory(new PropertyValueFactory<Schedule, String>("employeeName"));

                assignedEmployeeTable.getColumns().setAll(employeeCol);

        }

        private void populateScatterChart(XYChart.Series data1, XYChart.Series data2, XYChart.Series data3, XYChart.Series data4) {

                scatterPlot.getXAxis().setLabel(scheduler.AXISNAMES[this.axisX]);
                scatterPlot.getYAxis().setLabel(scheduler.AXISNAMES[this.axisY]);

                scatterPlot.getData().addAll(data4);
                scatterPlot.getData().addAll(data1);
                scatterPlot.getData().addAll(data2);
                scatterPlot.getData().addAll(data3);

                scatterPlot.setVisible(true);

        }

        private void populateLineChart() {

                int[] shiftMins = {13,13,15,15,15,18,15,13,13,15,15,18,15};

                XYChart.Series series = new XYChart.Series();
                series.setName("Historic shift manning");
                XYChart.Series series2 = new XYChart.Series();
                series2.setName("Shift minimums");

                for (int i = 0; i < scheduler.monthlyManning.size(); i++) {
                        for (int j = 0; j < scheduler.monthlyManning.get(i).length; j++) {
                                series.getData().add(new XYChart.Data(scheduler.correspondingDays.get(i)[j].getDayOfYear(), scheduler.monthlyManning.get(i)[j]));
                        }
                }

                int k = 0;
                for (int i = 0; i < scheduler.monthlyManning.size(); i++) {
                        for (int j = 0; j < scheduler.monthlyManning.get(i).length; j++) {
                                series2.getData().add(new XYChart.Data(scheduler.correspondingDays.get(i)[j].getDayOfYear(), shiftMins[k]));
                                if (k == shiftMins.length -1) {
                                        k = 0;
                                }
                        }
                }

                lineChart.getXAxis().setLabel("Days");
                lineChart.getYAxis().setLabel("Manning");

                NumberAxis xAxis2 = new NumberAxis();
                xAxis2.setLabel("X Data");
                NumberAxis yAxis2 = new NumberAxis();
                yAxis2.setLabel("Y Data");

                lineChart.getData().addAll(series2);
                lineChart.getData().addAll(series);

                lineChart.setCreateSymbols(false);

                lineChart.setVisible(true);

        }

        @Override
        public void loadWindowData(ShiftScheduler scheduler) {

                initializing = false;

                this.scheduler = scheduler;

                // Pull in an initial analysis of historic shift data...
                this.analysis = null;

                this.availableSchedules = FXCollections.observableArrayList(scheduler.getShifts().get(0).getAvailableSchedules());

                try {
                        scheduler.addRotatingDayOff(6);
                } catch (Exception e) {
                        e.printStackTrace();
                }

                //CTFontCreateUIFontForLanguage()
                try {
                        prepChartData();
                } catch (IOException e) {
                        e.printStackTrace();
                } catch (CsvException e) {
                        e.printStackTrace();
                }

                this.employees = (FXCollections.observableList(scheduler.getEmployees()));
                this.posts = (FXCollections.observableList(scheduler.getPosts()));
                ArrayList<String> eName = new ArrayList<String>();

                for (Employee e:employees) {
                        eName.add(e.getName());
                }

                this.employeeNames = (FXCollections.observableList(eName));
                this.assignedEmployees = (FXCollections.observableList(new ArrayList<Employee>()));

                employeeMenu.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<String>(employeeNames));

                resetPostLists();

                for (int i = 0; i < postMenus.size(); i++) {
                        postMenus.get(i).setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<String>(postLists.get(i)));
                        postMenus.get(i).getValueFactory().setValue("N");
                }

                for (int i = 0; i < shiftMins.size(); i++) {
                        shiftMins.get(i).setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,scheduler.getEmployees().size(), scheduler.shiftMins[i]));
                }

                totalLabels = new ArrayList<Label>(Arrays.asList(totalOne, totalTwo, totalThree, totalFour, totalFive,
                        totalSix, totalSeven, totalEight, totalNine, totalTen, totalEleven, totalTwelve, totalThirteen,
                        totalFourteen));

                for (int i = 0; i < totalLabels.size(); i++) {
                        totalLabels.get(i).setText(String.valueOf(scheduler.shiftMins[i]));
                }

                employees.sort((Employee o1, Employee o2)->o1.getName().compareTo(o2.getName()));
                employeeNames.sort((String o1, String o2)->o1.compareTo(o2));

                sumColumns();


        }

        private void resetPostLists() {


                this.postNamesOne = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesTwo = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesThree = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesFour = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesFive = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesSix = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesSeven = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesEight = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesNine = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesTen = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesEleven = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesTwelve = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesThirteen = (FXCollections.observableList(new ArrayList<String>()));
                this.postNamesFourteen = (FXCollections.observableList(new ArrayList<String>()));
/*
                this.remPostOne = (FXCollections.observableList(pName));
                this.remPostTwo = (FXCollections.observableList(pName));
                this.remPostThree = (FXCollections.observableList(pName));
                this.remPostFour = (FXCollections.observableList(pName));
                this.remPostFive = (FXCollections.observableList(pName));
                this.remPostSix = (FXCollections.observableList(pName));
                this.remPostSeven = (FXCollections.observableList(pName));
                this.remPostEight = (FXCollections.observableList(pName));
                this.remPostNine = (FXCollections.observableList(pName));
                this.remPostTen = (FXCollections.observableList(pName));
                this.remPostEleven = (FXCollections.observableList(pName));
                this.remPostTwelve = (FXCollections.observableList(pName));
                this.remPostThirteen = (FXCollections.observableList(pName));
                this.remPostFourteen = (FXCollections.observableList(pName));

 */

                this.postLists = new ArrayList<ObservableList<String>>(Arrays.asList(postNamesOne, postNamesTwo,
                        postNamesThree, postNamesFour, postNamesFive, postNamesSix, postNamesSeven, postNamesEight,
                        postNamesNine, postNamesTen, postNamesEleven, postNamesTwelve, postNamesThirteen, postNamesFourteen));

                for (int i = 0; i < postLists.size(); i++) {
                        for (Post p:posts) {
                                postLists.get(i).add(p.getName());
                        }
                }

        }

        private void sortPostLists() {
                for (ObservableList<String> p: postLists) {
                        p.sort((String o1, String o2)->o1.compareTo(o2));
                }
        }

        @Override
        public void writeMessage(String message) {

        }

}
