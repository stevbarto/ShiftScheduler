package ShiftScheduler;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnvironmentGenerator {

    private ShiftScheduler scheduler;

    public EnvironmentGenerator(ShiftScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void run() {
        try {
            loadLocations();
            //loadPosts();
            loadShifts(scheduler.getStartDate());
            //loadEmployees();
            //setShiftInfo();
            assignPreferences();
        }
        catch (IOException | CsvException e) {
            // Stuff...
        }

        System.out.println("Environment generated");

    }

    public void loadLastSchedule() throws IOException, CsvException {

        File file = new File("Locations.csv");

        FileReader fileRead = new FileReader(file);

        CSVReader reader = new CSVReader(fileRead);

        List<String[]> data = reader.readAll();

        reader.close();

        ArrayList<ArrayList<String>> schedules = new ArrayList<ArrayList<String>>();
        ArrayList<LocalDate> scheduleDays = new ArrayList<LocalDate>();

        for (int i = 17; i < data.get(0).length; i++) {
            scheduleDays.add(scheduler.createDate(data.get(0)[i]));
        }

        for (int i = 17; i < data.size(); i++) {

            ArrayList<String> line = new ArrayList<String>();

            for (String s:data.get(i)) {
                line.add(s);
            }

            schedules.add(line);

        }

        // TODO: add the matrix and dates to the shift/scheduler
        ArrayList<Shift> shifts = scheduler.getShifts();
        Shift shift = null;
        for (Shift s:shifts) {
            if (s.getName().equals("day")) {
                shift = s;
            }
        }

        if (shift != null) {
            shift.setShiftMatrix(schedules);
            shift.setShiftDays(scheduleDays);
        }

    }

    public void loadLocations() throws IOException, CsvException {
        File file = new File("Locations.csv");

        FileReader fileRead = new FileReader(file);

        CSVReader reader = new CSVReader(fileRead);

        List<String[]> data = reader.readAll();

        reader.close();

        for (int i = 0; i < data.size(); i++) {
            String[] line = data.get(i);

            if (line[0].equals("\uFEFFid")) {
                continue;
            }

            Location location = new Location(Integer.parseInt(line[0]), line[1], line[2], line[3], line[4], line[5]);

            scheduler.addLocation(location);
        }


    }

    public void loadPosts() throws IOException, CsvException {

        File file = new File("Posts.csv");

        FileReader fileRead = new FileReader(file);

        CSVReader reader = new CSVReader(fileRead);

        List<String[]> data = reader.readAll();

        reader.close();

        for (String[] a:data) {
            if (a[0].equals("id") || a[0].equals("")) {
                continue;
            }

            boolean priPost = false;

            String[] letters = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M"};

            for (String s:letters) {
                if (a[2].equals(s)) {
                    priPost = true;
                }
            }

            Post p = new Post(Integer.parseInt(a[0]), Integer.parseInt(a[1]), a[2], Integer.parseInt(a[3]), priPost);

            // Add days to each post
            for (int k = 4; k < 11; k++) {
                p.setDay(k - 4, Integer.parseInt(a[k]));
            }

            scheduler.addPost(p);

        }

    }

    private void loadShifts(LocalDate startDate) throws IOException, CsvException {

        File file = new File("Shifts.csv");

        FileReader fileRead = new FileReader(file);

        CSVReader reader = new CSVReader(fileRead);

        List<String[]> data = reader.readAll();

        reader.close();

        for (String[] a:data) {
            if (a[0].equals("id")) {
                continue;
            }

            Shift newShift = new Shift(Integer.parseInt(a[0]), a[1], Integer.parseInt(a[2]), Integer.parseInt(a[3]),
                    Integer.parseInt(a[4]), Integer.parseInt(a[5]), scheduler, startDate);

            scheduler.addShift(newShift);
        }

    }

    private void loadEmployees() throws IOException, CsvException {
        File file = new File("Employees.csv");

        FileReader fileRead = new FileReader(file);

        CSVReader reader = new CSVReader(fileRead);

        List<String[]> data = reader.readAll();

        reader.close();

        Random rand = new Random();


        for (int i = 0; i < data.size(); i++) {
            String[] line = data.get(i);

            if(line[0].equals("id")) {
                continue;
            }

            Employee employee = new Employee(Integer.parseInt(line[0]), line[1], line[2], line[3], line[4], line[5],
                    line[6], Integer.parseInt(line[7]), createDate(line[8]), Integer.parseInt(line[9]),
                    createDate(line[10]));

            employee.addOffDay(LocalDate.of(2022, 11, rand.nextInt(29) + 1));

            scheduler.addEmployee(employee);
        }

        System.out.println("Employees Generated");

    }

    private void setShiftInfo() throws IOException, CsvException {

        ArrayList<Employee> employees = scheduler.getEmployees();

        ArrayList<Post> posts = scheduler.getPosts();

        ArrayList<Integer> idNums = new ArrayList<Integer>();

        for (int i = 0; i < posts.size(); i++) {

            idNums.add(posts.get(i).getId());
        }

        Random rand2 = new Random();

        while (idNums.size() > 0) {
            int rndIndex = rand2.nextInt(employees.size());

            Employee current = employees.get(rndIndex);

            if(current.getShift() == -1) {

                int post = idNums.remove(0);
                int shift = posts.get(post).getShift();

                current.setShift(shift);
                current.setPost(post);
            }
        }

        ArrayList<Employee> unassigned = new ArrayList<Employee>();

        for (Employee e:employees) {
            if (e.getShift() == -1 && e.getPost() == -1) {
                unassigned.add(e);
            }
        }

        // Get percentages of posts on each shift, add that percentage of remining employees to each shift as rovers.
        int dayPosts = 0;
        int swingPosts = 0;
        int gravePosts = 0;

        for (Post p:posts) {
            // increment the shift numbers if the post belongs to it...
            int check = p.getShift();
            if (check == 0) {
                dayPosts++;
            }
            else if (check == 1) {
                swingPosts++;
            }
            else {
                gravePosts++;
            }
        }

        double swingRatio = swingPosts / posts.size();
        double graveRatio = gravePosts / posts.size();

        int graveNum = (int) Math.floor(unassigned.size() * graveRatio);
        int swingNum = (int) Math.floor(unassigned.size() * swingRatio);

        Random rand = new Random();

        int[] nightPost = {42,43};
        int[] swingPost = {42,43};
        int[] dayPost = {13,14};


        while (graveNum > 0) {

            Employee current =  unassigned.remove(rand.nextInt(unassigned.size()));

            current.setPost(nightPost[rand.nextInt(2)]);
            current.setShift(2);

            graveNum--;

        }

        while (swingNum > 0) {

            Employee current =  unassigned.remove(rand.nextInt(unassigned.size()));

            current.setPost(swingPost[rand.nextInt(2)]);
            current.setShift(1);

            swingNum--;

        }

        while (unassigned.size() > 0) {

            Employee current =  unassigned.remove(rand.nextInt(unassigned.size()));

            current.setPost(dayPost[rand.nextInt(2)]);
            current.setShift(0);

        }
    }

    // TODO: Assign shift preferences randomly to employees
    // TODO: Assign employees to shifts based on preference
    public void assignPreferences() {
        Random rand = new Random();

        ArrayList<Employee> employees = scheduler.getEmployees();

        for (Employee e:employees) {
            e.setPreferredShift(rand.nextInt(3));
        }
    }

    // TODO: Organize employees into groups by location
    // TODO: Add specialties to employees
    // TODO: Generate remaining .csv files needed

    private LocalDate createDate(String dateString) {

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

}
