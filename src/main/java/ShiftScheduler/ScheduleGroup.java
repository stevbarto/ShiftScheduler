package ShiftScheduler;

import java.util.ArrayList;

public class ScheduleGroup {

    private ArrayList<Schedule> schedules;

    private int size;

    private ShiftScheduler scheduler;

    public ScheduleGroup(ShiftScheduler s) {
        this.schedules = new ArrayList<Schedule>();
        this.size = 0;
        this.scheduler = s;
    }

    public ArrayList<Schedule> getSchedules() {
        return this.schedules;
    }

    public int size() {
        return this.size;
    }

    public Schedule removeSchedule(int id) {
        for (Schedule s:schedules) {
            if (s.getId() == id) {
                schedules.remove(s);
                return s;
            }
        }
        return null;
    }

    public boolean addSchedule(Schedule s) {

        if (size >= 4) {
            return false;
        }
        else if (size == 0) {
            schedules.add(s);
            return true;
        }
        else if (size == 1) {
            if (complimentaryShift(s, this.schedules.get(0))) {
                this.schedules.add(s);
                return true;
            }
            else {
                return false;
            }
        }

        boolean value = true;
        int count = 0;

        for (Schedule sched:schedules) {
            if(complimentaryShift(s,sched)) {
                count++;
            }
        }

        if (count < 1) {
            value = false;
        }

        return value;

    }

    private boolean complimentaryShift(Schedule first, Schedule second) {
        int[] rotatingDays = scheduler.getRotatingDaysOff();
        boolean value = true;

        for (Integer i:rotatingDays) {

            if (first.getWorkDays()[i] == 0 && second.getWorkDays()[i] == 0) {
                value = false;
            }
            else if (first.getWorkDays()[i] == 1 && second.getWorkDays()[i] == 1) {
                value = false;
            }

        }

        return value;
    }

}
