package ShiftScheduler;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

/**
 * Class designed to parse data for the C951 capstone project.  Takes name data from baby names data and Polish
 * names data from kaggle.com and parses it into a csv file that is usable for the application.  Takes address data
 * from the random 200 something random addresses data from kaggle.com to assign addresses to each generated name.
 * @author Steven J. Barton
 */
public class DataFileGenerator {

    private ShiftScheduler scheduler;
    private ArrayList<ArrayList> dataFile;
    private int employeeId;
    private int locationId;
    private LocalDate startDate;

    private int totalSick;


    MetricsTool metricsTool;

    public DataFileGenerator(ShiftScheduler scheduler) throws IOException {
        dataFile = new ArrayList<ArrayList>();

        this.scheduler = scheduler;

        for (int i = 0; i < 200; i++) {
            dataFile.add(new ArrayList<String>());
        }

        employeeId = 0;
        locationId = 0;

        metricsTool = new MetricsTool(scheduler);

        this.startDate = LocalDate.of(2021,11,1);

        this.totalSick = 0;
    }

    public void generateNewCsvFiles() throws IOException {

        ArrayList<ArrayList <String>> employeeRecords = importData("src/main/resources/employee_data_import.csv");
        ArrayList<ArrayList <String>> locationRecords = importData("src/main/resources/location_data_import.csv");
        ArrayList<ArrayList <String>> distanceRecords = importData("src/main/resources/distance_matrix.csv");
        ArrayList<ArrayList <String>> postRecords = importData("src/main/resources/post_data_import.csv");

        // TODO: Create and handle shifts, pay periods

        File employeeFile = new File("Employees.csv");
        File locationFile = new File("Locations.csv");
        File distanceFile = new File("Distances.csv");
        File postFile = new File("Posts.csv");
        File shiftFile = new File("Shifts.csv");


        if (employeeFile.createNewFile() && locationFile.createNewFile() && distanceFile.createNewFile() &&
                postFile.createNewFile() && shiftFile.createNewFile()) {
            System.out.println("Files created");
        }

        // The below block parses in the raw schedules in the base directory.
        try {

            int year = 2021;
            int intMonth = 11;

            for (int i = 0; i < 11; i++) {
                String month = String.valueOf(intMonth);
                if (month.length() == 1) {
                    month = "0" + month;
                }

                this.startDate = LocalDate.of(year, intMonth, 1);

                buildScheduleData("src/main/resources/" + year + "_" + month + "_Raw.csv", startDate);

                if (intMonth < 12) {
                    intMonth++;
                }
                else {
                    intMonth = 1;
                    year++;
                }
            }

        } catch (CsvException e) {
            e.printStackTrace();
        }

        // The statements below build records for different objects to be used in the final solution.
        writeEmployees(employeeRecords);
        writeLocations(locationRecords);
        writeDistances(distanceRecords);
        writePosts(postRecords);
        writeShifts();

    }

    private void writeShifts() throws IOException {

        ArrayList<String> line = new ArrayList<String>();

        File file = new File("Shifts.csv");

        FileWriter outPut = new FileWriter(file);

        CSVWriter writer = new CSVWriter(outPut);

        line.add("id");
        line.add("name");
        line.add("weekHours");
        line.add("shiftHours");
        line.add("maxWeek");
        line.add("startHour");

        writeToCsv(line, writer);

        line.clear();

        line.add("0");
        line.add("day");
        line.add("40");
        line.add("8");
        line.add("7");
        line.add("7");

        writeToCsv(line, writer);

        writer.close();
    }

    private void writePosts(ArrayList<ArrayList<String>> postRecords) throws IOException {

        // id,Shift,PostName,Sat,Sun,Mon,Tue,Wed,Thu,Fri

        ArrayList<String> line = new ArrayList<String>();

        File file = new File("Posts.csv");

        FileWriter outPut = new FileWriter(file);

        CSVWriter writer = new CSVWriter(outPut);

        line.add("id");
        line.add("shift");
        line.add("name");
        line.add("location");
        line.add("sat");
        line.add("sun");
        line.add("mon");
        line.add("tue");
        line.add("wed");
        line.add("thu");
        line.add("fri");

        writeToCsv(line, writer);

        line.clear();

        for(ArrayList<String> l:postRecords) {

            if (l.get(0).equals("﻿id")) {
                continue;
            }

            line.addAll(l);

            writeToCsv(line, writer);

            line.clear();
        }

        writer.close();

    }

    private void writeDistances(ArrayList<ArrayList<String>> distanceRecords) throws IOException {

        // read order: remove home, address, id, d0, d1, d2, d3, d4, d5, d6, d7, d8
        // write order: id, address, d0, d1, d2, d3, d4, d5, d6, d7, d8

        ArrayList<String> line = new ArrayList<String>();

        File file = new File("Distances.csv");

        FileWriter outPut = new FileWriter(file);

        CSVWriter writer = new CSVWriter(outPut);

        line.add("id");
        line.add("address");
        line.add("d0");
        line.add("d1");
        line.add("d2");
        line.add("d3");
        line.add("d4");
        line.add("d5");
        line.add("d6");
        line.add("d7");
        line.add("d8");

        writeToCsv(line, writer);

        line.clear();

        for(ArrayList<String> l:distanceRecords) {

            if (l.get(0).equals("")) {
                continue;
            }

            line.addAll(l);

            writeToCsv(line, writer);

            line.clear();
        }

        writer.close();

    }

    /**
     * Method which writes employee records to the CSV file.
     * @param employeeRecords ArrayList of ArrayLists each containing String fields for each employee.
     * @throws IOException
     */
    private void writeEmployees(ArrayList<ArrayList<String>> employeeRecords) throws IOException {

        // id,first,last,address,zip,city,state,shift,posts,available_day,primary,hire_date

        ArrayList<String> line = new ArrayList<String>();

        File file = new File("Employees.csv");

        FileWriter outPut = new FileWriter(file);

        CSVWriter writer = new CSVWriter(outPut);

        line.add("id");
        line.add("first");
        line.add("last");
        line.add("address");
        line.add("zip");
        line.add("city");
        line.add("state");
        line.add("shift");
        line.add("posts");
        line.add("available_day");
        line.add("primary");
        line.add("hire_date");

        writeToCsv(line, writer);

        line.clear();

        for(ArrayList<String> l:employeeRecords) {
            // insert employee ID
            if (!l.get(0).equals("\uFEFFfirst")) {

                line.add(employeeId + "");
                employeeId++;
                line.addAll(l);

                // TODO: Need to handle these fields properly.  Null is a placeholder now...
                for (int i = 0; i < 5; i++) {
                    line.add(null);
                }

                writeToCsv(line, writer);

            }

            line.clear();

        }

        writer.close();
    }

    /**
     * Method to write location records to the csv file.
     * @param locationRecords An ArrayList of ArrayLists which each hold String fields for each location.
     */
    private void writeLocations(ArrayList<ArrayList<String>> locationRecords) throws IOException {

        // location,address,zip,city,state

        ArrayList<String> line = new ArrayList<String>();


        File file = new File("Locations.csv");

        FileWriter outPut = new FileWriter(file);

        CSVWriter writer = new CSVWriter(outPut);

        // Loop here to prep location data for writing.
        for(ArrayList<String> l:locationRecords) {

            if (!l.get(0).equals("\uFEFFlocation")) {

                line.addAll(l);

                writeToCsv(line, writer);

            }

            line.clear();

        }

        writer.close();
    }

    /**
     * Method which imports data from a csv file.
     * @param filePath String value of the path to the csv file.
     * @return ArrayList of ArrayLists contining strings for each comma separated field.
     * @throws FileNotFoundException If the file is not at the path specified.
     */
    private ArrayList<ArrayList<String>> importData(String filePath) throws FileNotFoundException {
        // ArrayList holding the fields to be used in the data set.
        ArrayList<ArrayList <String>> dataImport = new ArrayList<ArrayList <String>>();

        // Code to enable reading of file data.
        File nameFile = new File(filePath);
        Scanner fileReader = new Scanner(nameFile);

        String line = "";

        StringBuilder dataIn = new StringBuilder();

        int index = 0;

        // Loop here to retrieve lines of data

        while (fileReader.hasNextLine()) {

            dataImport.add(new ArrayList<String>());

            line = fileReader.nextLine();

            for (int i = 0; i < line.length(); i++) {

                if (line.charAt(i) != ',') {
                    dataIn.append(line.charAt(i));
                }
                else if (line.charAt(i) == ',') {
                    dataImport.get(index).add(dataIn.toString());
                    dataIn = new StringBuilder();
                }

            }

            dataImport.get(index).add(dataIn.toString());
            dataIn = new StringBuilder();

            index++;

        }

        return dataImport;
    }

    /**
     * Identifies if a character is an integer or not.
     * @param c Character value.
     * @return True if c is an integer, false if not.
     */
    private boolean isInteger(char c) {
        return (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' ||
                c == '5' || c == '6' || c == '7' || c == '8' || c == '9');
    }

    /**
     * Identifies if a character is a part of the alphabet or not.
     * @param c Character value.
     * @return True if c is in the alphabet, false if not.
     */
    private boolean isAlpha(char c) {
        return (c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f' || c == 'g' || c == 'h' || c == 'i' ||
                c == 'j' || c == 'k' || c == 'l' || c == 'm' || c == 'n' || c == 'o' || c == 'p' || c == 'q' || c == 'r' ||
                c == 's' || c == 't' || c == 'u' || c == 'v' || c == 'w' || c == 'x' || c == 'y' || c == 'z' || c == 'A' ||
                c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F' || c == 'G' || c == 'H' || c == 'I' || c == 'J' ||
                c == 'K' || c == 'L' || c == 'M' || c == 'N' || c == 'O' || c == 'P' || c == 'Q' || c == 'R' || c == 'S' ||
                c == 'T' || c == 'U' || c == 'V' || c == 'W' || c == 'X' || c == 'Y' || c == 'Z');
    }

    /**
     * Method to write input to a csv file.
     * @param line String array containing String values to write.
     * @param writer CSVWriter Object with FileWriter used as parameter to that and a File as parameter to that.
     * @throws IOException
     */
    private void writeToCsv(ArrayList<String> line, CSVWriter writer) throws IOException {

        String[] copy = new String[line.size()];

        int i = 0;

        for (String s:line) {
            copy[i] = s;
            i++;
        }

        writer.writeNext(copy);

    }

    public void buildScheduleData(String path, LocalDate startDate) throws IOException, CsvException {

        int startDay = startDate.getDayOfWeek().getValue();
        int monthDays = startDate.getMonth().length(false);

        ArrayList<ArrayList<Integer>> combinationStrings = new ArrayList<ArrayList<Integer>>();
        ArrayList<String> groups = new ArrayList<String>();
        ArrayList<String[]> groupData = new ArrayList<>();

        // Pull existing shift designators from the database, these are just the values in each cell.
        File shiftValues = new File("ShiftValues.csv");

        ArrayList<String> postData = new ArrayList<String>();

        FileReader readExisting = new FileReader(shiftValues);
        CSVReader readCurrent = new CSVReader(readExisting);
        List<String[]> currentData = readCurrent.readAll();
        readCurrent.close();

        if(!shiftValues.exists()) {
            shiftValues.createNewFile();
        }
        else {

            for (String[] s:currentData) {
                for (int i = 0; i < s.length; i++) {
                    if (!postData.contains(s[i])) {
                        postData.add(s[i]);
                    }
                }
            }
        }

        // Pull the raw schedule data file
        File file = new File(path);
        FileReader fileRead = new FileReader(file);
        CSVReader reader = new CSVReader(fileRead);
        List<String[]> data = reader.readAll();
        reader.close();

        scheduler.compileHistoricManning(data, startDate);

        // Prepare to write any new unique shift values to the database
        FileWriter fileWriter = new FileWriter(shiftValues, true);
        CSVWriter csvWriter = new CSVWriter(fileWriter);

        // Array entry format:
        // {0:on/off bit,1:sick number,2-N:on/off array combos}
        ArrayList<ArrayList<Integer>> weekCombos = new ArrayList<ArrayList<Integer>>();
        int index = 0;

        // Pull all shift values to ensure any new ones are logged
        for (String[] a:data) {
            // Add line for the next employee schedule data
            combinationStrings.add(new ArrayList<Integer>());
            // Add the on/off bit to declare if the first number is days on or off
            if (a[0].equals("OFF")) {
                combinationStrings.get(index).add(0);
            }
            else {
                combinationStrings.get(index).add(1);
            }
            // Add a column for sick day count
            combinationStrings.get(index).add(0);
            // Add a column for off day count
            combinationStrings.get(index).add(0);
            // Add a column for the max week length
            combinationStrings.get(index).add(0);

            // Store any unique post labels
            for (int i = 0; i < a.length; i++) {

                String[] next = new String[1];

                if (!postData.contains(a[i]) && a[i].length() < 5) {

                    postData.add(a[i]);
                    next[0] = a[i];
                    csvWriter.writeNext(next);

                }
            }

            // write the combinations of days on and off to a data structure/database
            // count the sick days
            int onWork = 0;
            int offWork = 0;
            int sick = 0;
            int off = 0;
            int max = 0;

            for (String s:a) {
                if (s.equalsIgnoreCase("OFF") || s.equalsIgnoreCase("*OF")) {
                    // Need to store the onWork value if its not zero
                    // Need to reset onWork to zero
                    if (onWork > 0) {
                        combinationStrings.get(index).add(onWork);
                        if (onWork > max) {
                            max = onWork;
                        }
                        onWork = 0;
                        off++;
                    }
                    offWork++;
                }
                else {
                    // Need to store to offWork value if its not zero
                    // Need to reset offWork to zero
                    if (offWork > 0) {
                        combinationStrings.get(index).add(offWork);
                        offWork = 0;
                    }
                    onWork++;
                }

                if (s.equalsIgnoreCase("SCK")) {
                    sick++;
                }

            }

            onWork--;
            offWork--;

            if (onWork > 0) {
                combinationStrings.get(index).add(onWork);
                onWork = 0;
            }
            else if (offWork > 0) {
                combinationStrings.get(index).add(offWork);
                offWork = 0;
            }

            combinationStrings.get(index).set(1,sick);
            combinationStrings.get(index).set(2,off);
            combinationStrings.get(index).set(3,max);

            while (combinationStrings.get(index).size() < 18) {
                combinationStrings.get(index).add(0);
            }

            // ******************************** add classification criteria ***********************************
            // How to rank these based off sick days, week length, and off days as well. Number of sections as well?

            int monthDivisionScore = metricsTool.divisonRating(combinationStrings, index);
            
            int workDaysScore = metricsTool.daysRating(combinationStrings, index, startDate);
            
            int priDaysOff = metricsTool.priDayRating(combinationStrings, index, startDate);

            scheduler.addHistoricInstance(sick, max, off, monthDivisionScore, workDaysScore, priDaysOff);

            index++;
        }

        csvWriter.close();

        storeParsedData(combinationStrings, groups);

    }

    private void writeScores(ArrayList<String[]> groupData) throws IOException {

        File file = new File("GroupScores.csv");

        FileWriter writeFile = new FileWriter(file, true);

        CSVWriter writer = new CSVWriter(writeFile);

        file.delete();
        file.createNewFile();

        for (String[] arr:groupData) {
            writer.writeNext(arr);
        }

        writer.close();

    }

    private void storeParsedData(ArrayList<ArrayList<Integer>> data, ArrayList<String> groups) throws IOException {

        File rawData = new File("RawData.csv");

        rawData.delete();
        rawData.createNewFile();

        FileWriter fileWriter = new FileWriter(rawData, true);
        CSVWriter csvWriter = new CSVWriter(fileWriter);

        int index = 0;
        for (ArrayList<Integer> a:data) {

            String[] arr = new String[a.size() + 1];

            for (int i = 0; i < a.size(); i++) {
                arr[i] = String.valueOf(a.get(i));
            }

            csvWriter.writeNext(arr);
        }


        csvWriter.close();

        writeGroups(groups);


    }

    private void writeGroups(ArrayList<String> groups) throws IOException {

        File groupData = new File("GroupData.csv");

        groupData.delete();
        groupData.createNewFile();



        FileWriter fileWriter2 = new FileWriter(groupData, true);
        CSVWriter csvWriter2 = new CSVWriter(fileWriter2);

        int index2 = 0;
        for (String s:groups) {
            String[] arr = {s};

            csvWriter2.writeNext(arr);

        }


        csvWriter2.close();

    }


}
