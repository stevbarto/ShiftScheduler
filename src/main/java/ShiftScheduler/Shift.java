package ShiftScheduler;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.*;
import net.sf.javaml.core.DefaultDataset;


import java.io.*;
import java.time.LocalDate;
import java.util.*;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class Shift {

    private LocalDate prevDate;
    private LocalDate startDate;
    private int id;

    private String name;

    // Config variables
    private int weeklyHours;
    private int shiftHours;
    private int maxWeek;
    private int startHour;

    private ArrayList<Schedule> availableSchedules;
    private ArrayList<Schedule> goodSchedules;
    private ArrayList<Schedule> okSchedules;

    ArrayList<ArrayList<String>> shiftMatrix;
    ArrayList<ArrayList<String>> prevShiftMatrix;
    ArrayList<ArrayList<String>> nextShiftMatrix;

    ArrayList<Employee> primaries;
    ArrayList<Employee> general;

    private int scheduleIds;

    private ShiftScheduler scheduler;
    private ArrayList<LocalDate> shiftDates;

    ArrayList<ScheduleGroup> groups;

    Dataset data;
    Classifier clsf;

    MetricsTool metricsTool;

    KMeansAnalyze analyzer;

    /**
     *
     * @param id
     * @param weeklyHours
     * @param hoursInShift
     * @param maxWeek
     * @param startHour
     */
    public Shift(int id, String name, int weeklyHours, int hoursInShift, int maxWeek, int startHour,
                 ShiftScheduler scheduler, LocalDate startDate) {

        this.startDate = startDate;

        this.id = id;
        this.name = name;
        this.weeklyHours = weeklyHours;
        this.shiftHours = hoursInShift;
        this.maxWeek = maxWeek;
        this.startHour = startHour;

        shiftMatrix = new ArrayList<ArrayList<String>>();
        prevShiftMatrix = new ArrayList<ArrayList<String>>();
        nextShiftMatrix = new ArrayList<ArrayList<String>>();
        shiftDates = new ArrayList<LocalDate>();

        groups = new ArrayList<ScheduleGroup>();

        this.availableSchedules = new ArrayList<Schedule>();
        this.okSchedules = new ArrayList<Schedule>();
        this.goodSchedules = new ArrayList<Schedule>();

        this.scheduleIds = 0;

        this.scheduler = scheduler;

        this.prevDate = startDate;

        int month = prevDate.getMonthValue();
        int year = prevDate.getYear();

        if (month == 12) {
            year++;
            month = 1;
        }

        startDate = LocalDate.of(year, month, 1);

        generateSchedules();

        metricsTool = new MetricsTool(scheduler);

        analyzer = scheduler.getAnalyzer();

    }

    public void setWeeklyHours(int hours) {
        this.weeklyHours = hours;
    }

    public int getWeeklyHours() {
        return this.weeklyHours;
    }

    public void setMaxWeek(int max) {
        this.maxWeek = max;
    }

    public int getMaxWeek() {
        return this.maxWeek;
    }

    public void setShiftHours(int hours) {
        this.shiftHours = hours;
    }

    public int getShiftHours() {
        return this.shiftHours;
    }

    public void setStartHour(int hour) {
        this.startHour = hour;
    }

    public int getStartHour() {
        return this.startHour;
    }

    private void buildTrainAlgorithm() throws IOException, CsvException {

        FileReader fileRead = new FileReader("RawData.csv");
        CSVReader csvRead = new CSVReader(fileRead);
        List<String[]> stringData = csvRead.readAll();
        csvRead.close();

        FileReader fileReadGroup = new FileReader("GroupData.csv");
        CSVReader csvReadGroup = new CSVReader(fileReadGroup);
        List<String[]> groupData = csvReadGroup.readAll();
        csvReadGroup.close();

        data = new DefaultDataset();

        int groupIndex = 0;
        for (String[] a:stringData) {
            double[] line = new double[a.length];
            int index = 0;
            for (String s:a) {
                if (!s.equals("")) {
                    line[index] = Double.parseDouble(s);
                }
                index++;
            }

            Instance lineInst = new DenseInstance(line);
            lineInst.setClassValue(groupData.get(groupIndex)[0]);
            groupIndex++;
            data.add(lineInst);
        }

        clsf = new KNearestNeighbors(5);

        clsf.buildClassifier(data);

        FileReader fileRead2 = new FileReader("RawData.csv");
        CSVReader csvRead2 = new CSVReader(fileRead2);
        List<String[]> stringData2 = csvRead2.readAll();
        csvRead2.close();

        Dataset dataForClassification = new DefaultDataset();

        for (String[] a:stringData2) {
            double[] line = new double[a.length];
            int index = 0;
            for (String s:a) {
                if (!s.equals("")) {
                    line[index] = Double.parseDouble(s);
                }
                index++;
            }
            int idIndex = 0;
            Instance lineInst = new DenseInstance(line);
            lineInst.setClassValue(idIndex);
            idIndex++;
            dataForClassification.add(lineInst);

        }

        Map<Object, PerformanceMeasure> pm = EvaluateDataset.testDataset(clsf, dataForClassification);

        for (Object o:pm.keySet()) {
            System.out.println(o+": "+pm.get(o).getAccuracy());
        }

    }

    // TODO: Need to create groupings of 4 shifts with complementary days off
    // TODO: Need to assign employees to shift groups by primary post first checking day off requirements
    // TODO: Need to fill shift groups using remaining employees by distance, preference, and day off requirements
    // TODO: Employee schedule matrix
    // TODO: Post matrix to fill using decision tree from employee matrix

    public void generateSchedules() {

        this.availableSchedules.clear();

        // Start generating schedules, this is a generic matrix to generate all permutations with repetition.
        int[][] rawMatrix = generateMatrix();

        // Takes the raw int matrix of permutations and turns it into a String matrix formatted for generating schedules.
        ArrayList<String[]> allCombos = generateFormattedStringMatrix(rawMatrix);

        // Matrix to hold generated int values...
        ArrayList<ArrayList<Integer>> generatedIntMatrixArrayList = new ArrayList<ArrayList<Integer>>();

        // Create an int arraylist using the string arraylist
        generatedIntMatrixArrayList = createIntArrayList(allCombos);

        ArrayList<String> processedCombos = new ArrayList<String>();

        for (int i = 0; i < generatedIntMatrixArrayList.size(); i++) {
            for (int j = 0; j < generatedIntMatrixArrayList.get(i).size(); j++) {
                String line = generatedIntMatrixArrayList.get(i).get(j) + "";

                processedCombos.add(line);
            }
        }

        // Filters out weeks that don't meet user preferences.
        ArrayList<ArrayList<Integer>> scheduleMatrix = filterWeekPreferences(processedCombos);

        // Filter for day off settings.
        ArrayList<ArrayList<Integer>> filteredMatrix = filterDaysOff(scheduleMatrix);

        // Remove duplicates
        ArrayList<ArrayList<Integer>>intFinalMatrix =  removeDuplicates(filteredMatrix);

        // Create schedule objects for available schedules that meet parameters.
        for (ArrayList<Integer> a:intFinalMatrix) {

            Schedule newSched = new Schedule(this.scheduleIds, this.weeklyHours, this.shiftHours, this.startDate);
            this.scheduleIds++;

            int loc = 0;
            for(Integer i:a) {

                newSched.addDay(i);
                if (i == 1) {
                    newSched.addPost(loc, "WORK");
                }
                else {
                    newSched.addPost(loc, "OFF");
                }
                loc++;

            }

            availableSchedules.add(newSched);
        }

        File file = new File("GroupData.csv");
        file.delete();


        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter wr = null;
        try {
            wr = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVWriter writer = new CSVWriter(wr);

        for (int i = 0; i < intFinalMatrix.size(); i++) {

            ArrayList<Integer> line = getMetrics(intFinalMatrix, i, startDate);
            String[] stringLine = new String[line.size()];

            for (int j = 0; j < line.size(); j++) {
                stringLine[j] = String.valueOf(line.get(j));
            }

            writer.writeNext(stringLine);
        }


        System.out.print("Schedules generated");

    }

    private ArrayList<Integer> getMetrics(ArrayList<ArrayList<Integer>> intFinalMatrix, int index, LocalDate startDate) {
        this.metricsTool = new MetricsTool(scheduler);

        int sickScore = metricsTool.sickRate(intFinalMatrix, index);

        int weekLengthScore = metricsTool.weekLengthRate(intFinalMatrix, index);

        int offScore = metricsTool.offRating(intFinalMatrix, index, startDate);

        int monthDivisionScore = metricsTool.divisonRating(intFinalMatrix, index);

        int workDaysScore = metricsTool.daysRating(intFinalMatrix, index, startDate);

        int priDaysOff = metricsTool.priDayRating(intFinalMatrix, index, startDate);

        ArrayList<Integer> values = new ArrayList<Integer>();

        values.add(sickScore);
        values.add(weekLengthScore);
        values.add(offScore);
        values.add(monthDivisionScore);
        values.add(workDaysScore);
        values.add(priDaysOff);

        return values;
    }

    private ArrayList<ArrayList<Integer>> createIntArrayList(ArrayList<String[]> allCombos) {

        ArrayList<ArrayList<Integer>> processed = new ArrayList<ArrayList<Integer>>();

        int index = 0;
        for (String[] a:allCombos) {
            processed.add(new ArrayList<Integer>());
            for (String s:a) {
                processed.get(index).add(Integer.parseInt(s));
            }
            index++;
        }

        return processed;

    }

    private int[][] generateMatrix() {

        int[][] matrix = new int[1000][10];

        for (int i = 0; i < matrix.length; i++) {

            matrix[i][0] = i * 10;

            for (int j = 1; j < matrix[i].length; j++) {
                matrix[i][j] = matrix[i][j - 1] + 1;
            }

        }

        return matrix;
    }

    private ArrayList<String[]> generateFormattedStringMatrix(int[][] matrix) {

        ArrayList<String[]> allCombos = new ArrayList<>();
        // This takes the 2d array and generates a list of String value combos with zeros added for further processing
        for (int i = 0; i < matrix.length; i++) {

            String[] line = new String[matrix[i].length];

            for (int j = 0; j < matrix[i].length; j++) {

                String current = "";

                int comboSum = 0;
                current = String.valueOf(matrix[i][j]);

                for (int k = 0; k < current.length(); k++) {
                    comboSum = comboSum + Integer.parseInt(current.charAt(k) + "");
                }

                if (comboSum == 14) {

                    if (current.length() == 2) {
                        current = "00" + current;
                    }
                    else if (current.length() == 3) {
                        current = "0" + current;
                    }
                }
                line[j] = current;


            }
            allCombos.add(line);

        }

        return allCombos;
    }

    private ArrayList<ArrayList<Integer>> filterWeekPreferences(ArrayList<String> allCombos) {
        ArrayList<String> firstCombos = new ArrayList<String>();
        ArrayList<String> secondCombos = new ArrayList<String>();

        for (String s:allCombos) {

            int hoursOnSum = 0;
            int hoursOffSum = 0;

            int maxLength = 0;
            int totHoursSum = 0;

            for (int i = 0; i < s.length(); i++) {

                int nextValue = Integer.parseInt(s.charAt(i) + "");

                int workDayHours = nextValue * shiftHours;

                if (nextValue > maxLength) {
                    maxLength = nextValue;
                }

                totHoursSum = totHoursSum + workDayHours;

                if (i % 2 == 0) {
                    hoursOnSum = hoursOnSum + workDayHours;
                }
                if (i % 2 == 1) {
                    hoursOffSum = hoursOffSum + workDayHours;
                }

            }
            if (hoursOnSum == weeklyHours * 2 && maxLength <= maxWeek && totHoursSum == 112) {
                firstCombos.add(s);
            }
            else if (hoursOffSum == weeklyHours * 2 && maxLength <= maxWeek && totHoursSum == 112) {
                secondCombos.add(s);
            }
        }

        // The new schedule matrix after filtering
        ArrayList<ArrayList<Integer>> scheduleMatrix = new ArrayList<ArrayList<Integer>>();

        // Add all the index 0 and 2 work weeks
        int lastSpot = 0;
        for (int i = 0; i < firstCombos.size(); i++) {

            scheduleMatrix.add(new ArrayList<Integer>());

            String currentString = firstCombos.get(i);

            for(int j = 0; j < currentString.length(); j++) {

                int currentStep = Integer.parseInt(currentString.charAt(j) + "");

                for (int k = 0; k < currentStep; k++) {
                    if (j % 2 == 0) {
                        scheduleMatrix.get(i).add(1);
                    }
                    if (j % 2 == 1) {
                        scheduleMatrix.get(i).add(0);
                    }
                }

            }
            lastSpot = i + 1;
        }

        // Add all the index 1 and 3 work weeks
        for (int i = lastSpot; i < secondCombos.size() + lastSpot; i++) {

            scheduleMatrix.add(new ArrayList<Integer>());

            String currentString = secondCombos.get(i - lastSpot);

            for(int j = 0; j < currentString.length(); j++) {

                int currentStep = Integer.parseInt(currentString.charAt(j) + "");

                for (int k = 0; k < currentStep; k++) {
                    if (j == 1 || j == 3) {
                        scheduleMatrix.get(i).add(1);
                    }
                    if (j != 1 && j != 3) {
                        scheduleMatrix.get(i).add(0);
                    }
                }

            }
        }

        scheduleMatrix = filterWeekLength(scheduleMatrix);

        return scheduleMatrix;
    }

    private ArrayList<ArrayList<Integer>> filterWeekLength(ArrayList<ArrayList<Integer>> scheduleMatrix) {

        ArrayList<ArrayList<Integer>> returnMatrix = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> returns = new ArrayList<Integer>();

        int length = 0;
        int maxLength = 0;
        int arrayIndex = 0;

        for (ArrayList<Integer> a:scheduleMatrix) {
            length = 0;
            maxLength = 0;
            if (edgeWeek(a)) {
                int frontLength = 0;
                int backLength = 0;

                int front = 0;
                int back = a.size() - 1;
                while (a.get(front) == 1 || a.get(back) == 1) {
                    if (a.get(front) == 1) {
                        frontLength++;
                        front++;
                    }
                    if (a.get(back) == 1) {
                        backLength++;
                        back--;
                    }
                }

                length = frontLength + backLength;
            }
            else if (!edgeWeek(a)) {
                int stretch = 0;
                for (Integer i:a) {
                    if (i == 1) {
                        stretch++;
                        if (stretch > maxLength) {
                            maxLength = stretch;
                        }
                    }
                    else {
                        stretch = 0;
                    }
                }

            }

            if (maxLength < length) {
                maxLength = length;
            }

            if (maxLength <= maxWeek) {
                returns.add(arrayIndex);
            }
            arrayIndex++;

        }

        for (Integer i:returns) {
            returnMatrix.add(scheduleMatrix.get(i));
        }

        return returnMatrix;

    }

    private boolean edgeWeek(ArrayList<Integer> a) {

        if (a.get(0) == 1 && a.get(a.size() - 1) == 0) {
            return false;
        }
        else if (a.get(0) == 0 && a.get(a.size() - 1) == 1) {
            return false;
        }

        return true;

    }

    private ArrayList<ArrayList<Integer>> filterDaysOff(ArrayList<ArrayList<Integer>> scheduleMatrix) {

        ArrayList<ArrayList<Integer>> finalMatrix = new ArrayList<ArrayList<Integer>>();

        int[] rotatingDaysOff = scheduler.getRotatingDaysOff();

        ArrayList<Integer> indices = new ArrayList<Integer>();

        for (int i = 0; i < rotatingDaysOff.length; i++) {
            if (rotatingDaysOff[i] == 1) {
                indices.add(i);
            }
        }

        for (ArrayList<Integer> a:scheduleMatrix) {
            boolean addIt = true;
            for (Integer i: indices) {
                if (!validDaysOff(a, i)) {
                    addIt = false;
                }
            }
            if (addIt) {
                finalMatrix.add(a);
            }
        }



        return finalMatrix;
    }

    private boolean validDaysOff(ArrayList<Integer> a, int ind) {

            boolean add1 = false;
            boolean add2 = false;
            for (int i = 0; i < a.size() / 2; i++) {
                if (a.get(i) == 0 && a.get(i + 7) == 1) {
                    if (i == ind) {
                        add1 = true;
                    }
                } else if (a.get(i) == 1 && a.get(i + 7) == 0) {
                    if (i == ind) {
                        add2 = true;
                    }
                }
            }

        return add1 || add2;
    }

    private ArrayList<ArrayList<Integer>> removeDuplicates(ArrayList<ArrayList<Integer>> matrix) {

        for (int i = 0; i < matrix.size(); i++) {

            boolean duplicate = true;

            for (int j = i + 1; j < matrix.size(); j++) {

                for (int k = 0; k < matrix.get(j).size(); k++) {
                    if (matrix.get(i).get(k) != matrix.get(j).get(k)) {
                        duplicate = false;
                        break;
                    }
                }

                if (duplicate) {
                    matrix.remove(j);
                    j--;
                }

            }
        }

        return matrix;
    }

    private void generateScheduleGroups() {
        // Generate an array full of groups of four schedules with complementary days off
        while (availableSchedules.size() > 0) {
            ScheduleGroup group = new ScheduleGroup(scheduler);
            group.addSchedule(availableSchedules.get(0));
            int count = 0;
            int index = 0;
            while (count < 2) {
                if (group.addSchedule(availableSchedules.get(index))) {
                    count++;
                }
                else {
                    index++;
                }
            }
            this.groups.add(group);
        }
    }

    private void assignEmployeesToSchedules() {

        int[][] postAssignmentMatrix = new int[scheduler.getPosts().size()][scheduler.getEmployees().size()];

        for (int i = 0; i < scheduler.getPosts().size(); i++) {
            for (int j = 0; j < scheduler.getEmployees().size(); j++) {
                postAssignmentMatrix[i][j] = 0;
            }
        }

        int[][] scheduleAssignmentMatrix = new int[availableSchedules.size()][scheduler.getEmployees().size()];

        for (int i = 0; i < availableSchedules.size(); i++) {
            for (int j = 0; j < scheduler.getEmployees().size(); j++) {
                scheduleAssignmentMatrix[i][j] = 8;
            }
        }

        primaries = new ArrayList<Employee>();

        general = new ArrayList<Employee>();

        ArrayList<Employee> all = scheduler.getEmployees();

        ArrayList<Post> posts = scheduler.getPosts();

        // Assign employees who are primaries on priority posts to their post.
        for (Employee e:all) {
            for (Post p:posts) {
                if (p.getId() == e.getPost()) {
                    if (p.getPriority()) {
                        primaries.add(e);
                        postAssignmentMatrix[p.getId()][e.getId()]++;
                    }
                    else {
                        general.add(e);
                    }
                }
            }
        }

        // Assign scores to each employee in matrix with the schedules to designate who is best to assign to a schedule.
        for (ScheduleGroup g:groups) {
            for (Schedule s:g.getSchedules()) {
                for (Employee e:primaries) {
                    for (LocalDate d:e.getOffDays()) {
                        if (s.getWorkDays()[d.getDayOfWeek().getValue()] != 0) {
                            scheduleAssignmentMatrix[s.getId()][e.getId()]--;

                        }
                    }
                }
            }
        }
        for (ScheduleGroup g:groups) {
            for (Schedule s:g.getSchedules()) {
                for (Employee e:general) {
                    for (LocalDate d:e.getOffDays()) {
                        if (s.getWorkDays()[d.getDayOfWeek().getValue()] != 0) {
                            scheduleAssignmentMatrix[s.getId()][e.getId()]--;

                        }
                    }
                }
            }
        }

        // Now employees are assigned to shifts based on scores in the matrix.
        for (int i = 0; i < primaries.size(); i++) {
            int max = 0;
            int index = 0;
            for (int j = 0; j < availableSchedules.size(); j++) {
                if (scheduleAssignmentMatrix[j][i] > max) {
                    max = scheduleAssignmentMatrix[j][i];
                    index = j;
                }
            }
            scheduler.getEmployees().get(i).setSchedule(availableSchedules.get(index));
            availableSchedules.get(index).setEmployeeId(i);
        }

    }

    private void fillPosts() {
        ArrayList<Post> posts = scheduler.getPosts();
        int[][] fillMatrix = new int[posts.size()][shiftDates.size()];

        for (int i = 0; i < posts.size(); i++) {
            for (int j = 0; j < shiftDates.size(); j++) {
                if (posts.get(i).getDaysOn()[j] == 1) {
                    fillMatrix[i][j] = 1;
                }
                else {
                    fillMatrix[i][j] = 0;
                }
            }
        }

        for (int i = 0; i < primaries.size(); i++) {
            ArrayList<LocalDate> offDays = primaries.get(i).getOffDays();
            Schedule schedule = primaries.get(i).getCurrentSchedule();

            // Go through dates
            int index = 0;
            boolean onWork = true;
            for (int j = 0; j < shiftDates.size(); j++) {
                // Check if the employee is on
                for (LocalDate l:offDays) {
                    if (shiftDates.get(j).equals(l)) {
                        onWork = false;
                    }

                }
                // Check by other metric if the employee is on
                if (schedule.getWorkDays()[index] == 0) {
                    onWork = false;
                }
                // If on, assign primaries to their posts, no need to check distances for primaries.
                if (onWork) {
                    // Set the post for the employee here...
                    for (Post p:posts) {
                        if (p.getId() == primaries.get(i).getPrimaryPost() && fillMatrix[p.getId()][shiftDates.get(j).getDayOfWeek().getValue()] == 1) {
                            primaries.get(i).setPost(p.getId());
                            p.setEmployeeId(primaries.get(i).getId());
                            fillMatrix[p.getId()][index] = 2;
                        }
                    }
                }

                index++;
            }
        }

        ArrayList<ArrayList<Double>> distMatrix = scheduler.getMapGraph();

        // Iterate through general post employees
        for (int i = 0; i < general.size(); i++) {
            ArrayList<LocalDate> offDays = general.get(i).getOffDays();
            Schedule schedule = general.get(i).getCurrentSchedule();

            boolean onWork = true;
            // Iterate through shift dates
            for (int j = 0; j < shiftDates.size(); j++) {
                // Check if the employee is on requested leave or assignment
                for (int k = 0; k < offDays.size(); k++) {
                    if (shiftDates.get(j).equals(offDays.get(k))) {
                        onWork = false;
                    }
                }

                // Check by normal weekly days off if the employee is on
                if (schedule.getWorkDays()[j] == 0) {
                    onWork = false;
                }

            }

            if (!onWork) {
                continue;
            }



            // Check for the minimum distances to posts
            ArrayList<Double> distances = distMatrix.get(general.get(i).getId());
            double min = distances.get(0);
            int postIndex = 0;
            for (int l = 1; l < distances.size(); l++) {
                if (distances.get(l) < min && posts.get(l).getEmployeeId() == -1) {
                    min = distances.get(l);
                    postIndex = l;
                }
            }

            for (int j = 0; j < shiftDates.size(); j++) {

                // TODO: Assign employees to the nearest available open post
                Post p = posts.get(postIndex);
                if (p.getDaysOn()[j] == 1) {
                    general.get(i).setPost(p.getId());
                    p.setEmployeeId(general.get(i).getId());
                    fillMatrix[p.getId()][j] = 2;
                }

            }

        }

        // TODO: Assign leftover employees to rover positions.
        for (Employee e:general) {

        }
    }

    public void setShiftMatrix(ArrayList<ArrayList<String>> shiftMatrix) {
        this.prevShiftMatrix = this.shiftMatrix;
        this.shiftMatrix = shiftMatrix;
    }

    public void setShiftDays(ArrayList<LocalDate> shiftDates) {
        this.shiftDates = shiftDates;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Schedule> getAvailableSchedules() {

        return this.availableSchedules;

    }
}
