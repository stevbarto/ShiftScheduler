package ShiftScheduler;

import java.time.LocalDate;
import java.util.ArrayList;

public class Employee {

    private final int ID;
    private final String FIRST;
    private final String LAST;
    private final String ADDRESS;
    private final String ZIP;
    private final String CITY;
    private final String STATE;
    private LocalDate availableDate;
    private int post;
    private int primaryPost;
    private final LocalDate HIREDATE;

    private int shiftId;

    private int preferredShiftId;

    private Schedule currentSchedule;
    private Schedule lastSchedule;
    private Schedule nextSchedule;

    private ArrayList<LocalDate> offDays;

    private ArrayList<String> training;
    private Schedule schedule;

    public Employee(int id, String first, String last, String address, String zip, String city, String state,
                    int shift, LocalDate availableDate, int primary, LocalDate hireDate) {

        this.ID = id;
        this.FIRST = first;
        this.LAST = last;
        this.ADDRESS = address;
        this.ZIP = zip;
        this.CITY = city;
        this.STATE = state;
        this.shiftId = shift;
        this.availableDate = availableDate;
        this.primaryPost = primary;
        this.post = -1;
        this.HIREDATE = hireDate;

        this.currentSchedule = null;
        this.preferredShiftId = -1;
        this.training = new ArrayList<String>();

        offDays = new ArrayList<LocalDate>();

    }

    // methods...
    public int getId() {
        return this.ID;
    }

    public void setShift(int shift) {
        this.shiftId = shift;
    }

    public int getShift() {
        return this.shiftId;
    }

    public void setPreferredShift(int shift) {
        this.preferredShiftId = shift;
    }

    public int getPreferredShift() {
        return this.preferredShiftId;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public int getPost() {
        return this.post;
    }

    public int getPrimaryPost() { return this.primaryPost; }

    public void setSchedule(Schedule s) {
        this.lastSchedule = this.currentSchedule;
        this.currentSchedule = s;
    }

    public Schedule getCurrentSchedule() {
        return this.currentSchedule;
    }

    public void addOffDay(LocalDate date) {
        this.offDays.add(date);
    }

    public ArrayList<LocalDate> getOffDays() {
        return this.offDays;
    }

    public String getName() {
        return this.LAST;
    }

    public void addSchedule(Schedule selected) {

        this.schedule = selected;

    }

    public void removeSchedule() {
        this.schedule = null;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }
}
