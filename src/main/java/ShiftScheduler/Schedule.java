package ShiftScheduler;

import java.time.LocalDate;
import java.util.ArrayList;

public class Schedule {
    // Id number for the schedule instance.
    private int id;
    // Id number of the employee assigned to the schedule instance.
    private int employeeId;
    private String employeeName;
    // Matrix of posts assigned to the employee.  The matrix is [days]x[posts].
    private ArrayList<Post> posts;
    // The beginning date of the schedule, the schedule will be two weeks.
    private LocalDate startDate;

    private int[] workDays;
    private String[] schedule;

    private int weeklyHours;
    private int dailyHours;

    private int index;
    private int maxWorkWeek;

    public Schedule(int id, int weeklyHours, int dailyHours, LocalDate startDate) {

        this.id = id;
        this.weeklyHours = weeklyHours;
        this.dailyHours = dailyHours;
        this.employeeId = -1;
        this.startDate = null;
        this.posts = new ArrayList<Post>();
        this.workDays = new int[14];
        this.index = 0;
        this.schedule = new String[14];

        this.startDate = startDate;

    }

    public int getMaxWeek() {
        return this.maxWorkWeek;
    }

    public void setEmployeeId(int id) {
        this.employeeId = id;
    }

    public int getEmployeeId() {
        return this.employeeId;
    }

    public void setStartDate(LocalDate date) {
        this.startDate = date;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public ArrayList<Post> getPosts() {
        return this.posts;
    }

    public boolean addDay(int dayCode) {

        if (index >= 14) {
            return false;
        }

        this.workDays[index] = dayCode;
        index++;

        int max = 0;
        int wd = 0;
        for (Integer i:workDays) {
            if (i == 1) {
                wd++;
            }
            else if (i == 0) {
                wd = 0;
            }
            if (wd > max) {
                max = wd;
            }
        }
        this.maxWorkWeek = max;

        return true;
    }

    public int[] getWorkDays() {

        return this.workDays;

    }

    public int getId() {
        return this.id;
    }

    public String getDayOne() {
        return schedule[0];
    }

    public String getDayTwo() {
        return schedule[1];
    }

    public String getDayThree() {
        return schedule[2];
    }

    public String getDayFour() {
        return schedule[3];
    }

    public String getDayFive() {
        return schedule[4];
    }

    public String getDaySix() {
        return schedule[5];
    }

    public String getDaySeven() {
        return schedule[6];
    }

    public String getDayEight() {
        return schedule[7];
    }

    public String getDayNine() {
        return schedule[8];
    }

    public String getDayTen() {
        return schedule[9];
    }

    public String getDayEleven() {
        return schedule[10];
    }

    public String getDayTwelve() {
        return schedule[11];
    }

    public String getDayThirteen() {
        return schedule[12];
    }

    public String getDayFourteen() {
        return schedule[13];
    }

    public void addPost(int day, String post) {
        this.schedule[day] = post;
    }

    public String[] getSchedule() {
        return this.schedule;
    }

    public void removePost(int day) {
        this.schedule[day] = "";
    }

    public void setEmployeeName(String name) {
        this.employeeName = name;
    }

    public String getEmployeeName() {
        return this.employeeName;
    }

    public void removeEmployeeId() {
        this.employeeId = -1;
    }

    public void removeEmployeeName() {
        this.employeeName = "";
    }
 }

