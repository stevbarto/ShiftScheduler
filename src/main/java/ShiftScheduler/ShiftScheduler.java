package ShiftScheduler;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShiftScheduler {

    private static final int CLUSTERS = 3;
    static final String[] AXISNAMES = {"Sick Days", "Max work week", "Num off days", "Num split work periods", "(Month length)/(Work days)", "Num priority days off"};
    static final String[] POSTNAMES = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R"};
    static final int[][] POSTRULES = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,0,0,1,1,1,1,1,0,0},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,0,0,1,1,1,1,1,0,0},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {0,0,0,1,0,0,0,0,0,0,1,0,0,0},
            {0,0,0,1,0,0,0,0,0,0,1,0,0,0},
            {0,0,0,1,0,0,0,0,0,0,1,0,0,0}};
    public static final String[] PRIORITYPOST = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"};
    public static final ArrayList<String> OFFVALS = new ArrayList<String>(Arrays.asList("OFF", "SCK", "FNL", "VAC", "FML", "\uFEFFFML",
            "\uFEFFVAC", "Hol", "Mil", "MIL", "Sic", "*OF", "SIC", "Vac", "HOL", "SKP", "V", "\uFEFFSCK", "FSC"));

    public static int[] shiftMins;

    private LocalDate startDate;
    private ArrayList<Employee> employees;
    private ArrayList<Shift> shifts;
    private ArrayList<Location> locations;
    private ArrayList<Post> posts;

    private int weeklyHours;
    private int dailyHours;
    private int maxWeek;

    private int employeeId;
    private int locationId;
    private int postId;
    private int shiftId;

    private int[] rotatingOffDays;

    private ArrayList<ArrayList<Double>> mapGraph;

    KMeansAnalyze analyzer;

    WindowMaker viewWindow;
    private Stage windowStage;
    private Parent parent;
    private Scene scene;
    private FXMLLoader loader;
    private int xAxis;
    private int yAxis;
    private ArrayList<ArrayList<Integer>> historicDataMatrix;

    MetricsTool metricsTool;

    public static ArrayList<int[]> monthlyManning;
    public static ArrayList<LocalDate[]> correspondingDays;

    public ShiftScheduler() {

        rotatingOffDays = new int[]{0,0,0,0,0,0,0};

        this.shiftMins = new int[this.POSTRULES[0].length];

        for (int i = 0; i < this.POSTRULES[0].length; i++) {
            int sum = 0;
            for (int j = 0; j < this.POSTRULES.length; j++) {
                sum = sum + this.POSTRULES[j][i];
            }
            shiftMins[i] = sum;
        }

        monthlyManning = new ArrayList<int[]>();
        correspondingDays = new ArrayList<LocalDate[]>();


        metricsTool = new MetricsTool(this);

        historicDataMatrix = new ArrayList<ArrayList<Integer>>();

        this.weeklyHours = 40;
        this.dailyHours = 8;
        this.maxWeek = 7;
        // TODO: Here, need to generate lists based off of the csv files!
        this.employees = new ArrayList<Employee>();
        this.shifts = new ArrayList<Shift>();
        this.locations = new ArrayList<Location>();
        this.posts = new ArrayList<Post>();

        mapGraph = new ArrayList<ArrayList<Double>>();

        analyzer = new KMeansAnalyze(this);

        this.xAxis = 1;
        this.yAxis = 0;

        this.startDate = LocalDate.now();

        //writeLambdas();

    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(int year, int month, int day) {
        this.startDate = LocalDate.of(year, month, day);
    }

    public KMeansAnalyze getAnalyzer() {
        return this.analyzer;
    }

    public void addShift(Shift shift) {
        this.shifts.add(shift);
    }

    public ArrayList<Shift> getShifts() {
        return this.shifts;
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }

    public ArrayList<Employee> getEmployees() {
        return this.employees;
    }

    public void addLocation(Location location) {
        this.locations.add(location);
    }

    public ArrayList<Location> getLocations() {
        return this.locations;
    }

    public void addPost(Post post) {
        this.posts.add(post);
    }

    public ArrayList<Post> getPosts() {
        return this.posts;
    }

    public void setWeeklyHours(int hours) {
        this.weeklyHours = hours;
    }

    public int getWeeklyHours() {
        return this.weeklyHours;
    }

    public void setDailyHours(int hours) {
        this.dailyHours = hours;
    }

    public int getDailyHours() {
        return this.dailyHours;
    }

    public void setMaxWeek(int days) {
        this.maxWeek = days;
        for (Shift s:this.shifts) {
            s.setMaxWeek(days);
        }
    }

    public int getMaxWeek() {
        return this.maxWeek;
    }

    /**
     * Method to add a rotating day off for employees (they should have half of this off in a month)
     * 0 = Monday to 6 = Sunday
     * Only values 0 to 6 should be entered, algorithm will fail if 7+ is entered.
     * 0 1 2 3 4 5 6
     * M T W R F S S
     * @param day
     */
    public void addRotatingDayOff(int day) throws Exception {

        if (day > 6) {
            throw new Exception("Value is too large! Enter 0 to 6!");
        }

        this.rotatingOffDays[day] = 1;
    }

    public void removeRotatingDayOff(int day) {
        this.rotatingOffDays[day] = 0;
    }

    public int[] getRotatingDaysOff() {
        return this.rotatingOffDays;
    }

    public ArrayList<ArrayList<Double>> getMapGraph() {
        return this.mapGraph;
    }

    public void loadData() {

        try {
            loadUserConfigs();
            loadLocations();
            loadDistances();
            loadPosts();
            loadShifts();
            loadEmployees();
        }
        catch (IOException | CsvException e) {
            // stuff
        }

        System.out.println("Data loaded");

    }

    private void loadUserConfigs() {
        // TODO:  Load user configuration settings here.  There needs to be a file structure to store this state...
    }

    private void loadEmployees() throws IOException {
        File file = new File("Employees.csv");

        FileReader fileRead = new FileReader(file);

        CSVReader reader = new CSVReader(fileRead);

        int maxId = 0;

        List<String[]> employeeData = null;
        try {
            employeeData = reader.readAll();
        } catch (CsvException e) {
            e.printStackTrace();
        }

        for (int i = 1; i < employeeData.size(); i++) {

            String[] line = employeeData.get(i);

            int id = Integer.parseInt(line[0]);

            if (id > maxId) {
                maxId = id;
            }

            Employee newPerson = new Employee(id,line[1],line[2],line[3],line[4],line[5],line[6],
                    Integer.parseInt(line[7]),createDate(line[8]),Integer.parseInt(line[9]),createDate(line[10]));

            this.employees.add(newPerson);

        }

        this.employeeId = maxId + 1;

        reader.close();
    }

    private void loadLocations() throws IOException {
        File file = new File("Locations.csv");

        FileReader fileRead = new FileReader(file);

        CSVReader reader = new CSVReader(fileRead);

        List<String[]> locationData = null;

        int maxId = 0;

        try {
            locationData = reader.readAll();
        }
        catch (CsvException e) {
            // stuff
        }

        for (String[] s:locationData) {

            if (s[0].equals("\uFEFFid")) {
                continue;
            }

            int currentId = Integer.parseInt(s[0]);

            if (currentId > maxId) {
                maxId = currentId;
            }

            Location location = new Location(Integer.parseInt(s[0]), s[1], s[2], s[3], s[4], s[5]);

            locations.add(location);

        }

        locationId = maxId + 1;

        reader.close();
    }

    private void loadPosts() throws IOException, CsvException {
        File file = new File("Posts.csv");

        FileReader fileRead = new FileReader(file);

        CSVReader reader = new CSVReader(fileRead);

        List<String[]> postData = reader.readAll();

        int maxId = 0;

        for (String[] a:postData) {

            if (a[0].equals("id") || a[0].equals("")) {
                continue;
            }

            int currentId = Integer.parseInt(a[0]);

            if (currentId > maxId) {
                maxId = currentId;
            }

            // TODO: Need to store posts with boolean if its priority in csv files.
            Post post = new Post(Integer.parseInt(a[0]), Integer.parseInt(a[1]), a[2], Integer.parseInt(a[3]), false);

            for (int i = 4; i < 11; i++) {
                post.setDay(i - 4, Integer.parseInt(a[i]));
            }

            this.posts.add(post);

        }

        postId = maxId + 1;

        reader.close();
    }

    private void loadShifts() throws IOException, CsvException {
        File file = new File("Shifts.csv");

        FileReader fileRead = new FileReader(file);

        CSVReader reader = new CSVReader(fileRead);

        List<String[]> shiftData = reader.readAll();

        int maxId = 0;

        for (String[] a:shiftData) {
            if (a[0].equals("id")) {
                continue;
            }

            int currentId = Integer.parseInt(a[0]);

            if (currentId > maxId) {
                maxId = currentId;
            }

            Shift shift = new Shift(Integer.parseInt(a[0]), a[1], Integer.parseInt(a[2]), Integer.parseInt(a[3]),
                    Integer.parseInt(a[4]), Integer.parseInt(a[5]), this, startDate);

            this.shifts.add(shift);

        }

        shiftId = maxId + 1;

        reader.close();
    }

    public void compileHistoricManning(List<String[]> shiftData, LocalDate start) {

        int arrSize = shiftData.get(0).length;

        int[] manning = new int[arrSize];
        LocalDate[] monthlyDates = new LocalDate[arrSize];

        for (int i = 0; i < shiftData.get(0).length; i++) {
            int lineSum = 0;
            int index = 0;
            for (int j = 0; j < shiftData.size(); j++) {
                index = j;
                if (!this.OFFVALS.contains(shiftData.get(j)[i])) {
                    lineSum = lineSum + 1;
                }
            }
            manning[i] = lineSum;
            monthlyDates[i] = start.plusDays(1 * i);
        }
        monthlyManning.add(manning);
        correspondingDays.add(monthlyDates);
    }

    private void loadDistances() throws IOException, CsvException {
        File file = new File("Distances.csv");

        FileReader fileRead = new FileReader(file);

        CSVReader reader = new CSVReader(fileRead);

        List<String[]> distanceData = reader.readAll();

        for (String[] a:distanceData) {
            if (!a[0].equals("home")) {
                continue;
            }

            int id = Integer.parseInt(a[2]);
            mapGraph.add(id, new ArrayList<Double>());


            for (int i = 3; i < a.length; i++) {
                mapGraph.get(id).add(Double.parseDouble(a[i]));
            }
        }

        reader.close();

        System.out.println("Map graph created");
    }

    public LocalDate createDate(String dateString) {

        int year = 0;
        int month = 0;
        int day = 0;

        String yearStr = "";
        String monthStr = "";
        String dayStr = "";

        int i = 0;
        while(dateString.charAt(i) != '/') {
            monthStr = monthStr + dateString.charAt(i);
            i++;
        }

        i++;

        while(dateString.charAt(i) != '/') {
            dayStr = dayStr + dateString.charAt(i);
            i++;
        }

        i++;

        while(i < dateString.length()) {
            yearStr = yearStr + dateString.charAt(i);
            i++;
        }

        year = Integer.parseInt(yearStr);
        month = Integer.parseInt(monthStr);
        day = Integer.parseInt(dayStr);

        LocalDate now = LocalDate.now();

        int yearNow = now.getYear();

        String nowStr = String.valueOf(yearNow);

        String mil = nowStr.charAt(0) + "";
        String cen = nowStr.charAt(1) + "";
        String dec = nowStr.charAt(2) + "";
        String yr = nowStr.charAt(3) + "";

        String convertDec = dec + yr + "";

        int check = Integer.parseInt(convertDec);

        int century = Integer.parseInt(mil + cen + "00");

        if (check > year) {
            century = century - 100;
        }

        year = year + century;

        return LocalDate.of(year,month,day);

    }

    public KMeansResults getAnalysis(ArrayList<ArrayList<Integer>> data) {

        KMeansResults results = null;

        try {
             results = analyzer.classifyData(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }

        return results;
    }

    public KMeansResults analyzeHistoricData(int a, int b) {

        ArrayList<ArrayList<Integer>> data = getHistoricDataMatrix();

        // Build array to pass into algorithm
        ArrayList<ArrayList <Integer>> dataArray = new ArrayList<ArrayList<Integer>>();
        dataArray.add(new ArrayList<Integer>());
        dataArray.add(new ArrayList<Integer>());


        // Populate the array with data at the two parameter indices
        for (int i = 0; i < data.size(); i++) {
            dataArray.get(0).add(data.get(i).get(a));
            dataArray.get(1).add(data.get(i).get(b));
        }

        // Pass data to call the algorithm
        KMeansResults results = getAnalysis(dataArray);

        return results;
    }

    public boolean validateUser(String userName, String passWord) {

        return true;

    }

    public void loadSchedulerScreen() throws IOException {

        System.out.println("Starting scheduler window.");

        FXMLLoader loader = new FXMLLoader();
        loader.setClassLoader(getClass().getClassLoader()); // set the plugin's class loader
        String address = "../ShiftScheduler/src/main/resources/ShiftScheduler.fxml";
        InputStream fxmlStream = new FileInputStream(address);
        Parent parent = loader.load(fxmlStream);
        // loader.setLocation(getClass().getResource(windowFile));

        //loader = new FXMLLoader(getClass().getResource(windowFile));

        // Initiate new controller from parameter to which data needs to be sent
        ShiftSchedulerController shiftSchedulerController = loader.getController();
        //loader.setController(c);

        //ctrl.setSchedule(this);
        shiftSchedulerController.loadWindowData(this);

        scene = new Scene(parent);
        String cssSheet = "../ShiftScheduler/src/main/resources/styleSheet.css";
        scene.getStylesheets().add(cssSheet);

        windowStage = new Stage();

        windowStage.setTitle("Schedule");

        windowStage.setScene(scene);

        windowStage.show();
    }

    /**
     * Method to set the xAxis for scatter plotting.
     * index 0 = number of sick days
     * index 1 = max number of days in a work week
     * index 2 = number of off days in the month
     * index 3 = number of splits between sets of work days
     * index 4 = ceiling value of total workdays/days in the month
     * index 5 = number of priority days off
     * @return int X axis value
     */
    public int getXAxis() {
        return this.xAxis;
    }

    public void setXAxis(int x) {
        this.xAxis = x;
    }

    /**
     * Method to set the yAxis for scatter plotting.
     * index 0 = number of sick days
     * index 1 = max number of days in a work week
     * index 2 = number of off days in the month
     * index 3 = number of splits between sets of work days
     * index 4 = ceiling value of total workdays/days in the month
     * index 5 = number of priority days off
     * @return int Y axis value
     */
    public int getYAxis() {
        return this.yAxis;
    }

    public void setYAxis(int y) {
        this.yAxis = y;
    }

    public void addHistoricInstance(int sickScore, int weekLengthScore, int offScore, int monthDivisionScore, int workDaysScore, int priDaysOff) {

        int index = historicDataMatrix.size();

        this.historicDataMatrix.add(new ArrayList<Integer>());

        historicDataMatrix.get(index).add(sickScore);
        historicDataMatrix.get(index).add(weekLengthScore);
        historicDataMatrix.get(index).add(offScore);
        historicDataMatrix.get(index).add(monthDivisionScore);
        historicDataMatrix.get(index).add(workDaysScore);
        historicDataMatrix.get(index).add(priDaysOff);

    }

    public ArrayList<ArrayList<Integer>> getHistoricDataMatrix() {
        return this.historicDataMatrix;
    }

    public int getClusterNum() {
        return this.CLUSTERS;
    }

    public ArrayList<Schedule> generateSchedules() {

        Shift shift = shifts.get(0);

        shift.generateSchedules();

        return shift.getAvailableSchedules();

    }
}
