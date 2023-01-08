package ShiftScheduler;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.util.ArrayList;

public class MetricsTool {

    private final ShiftScheduler scheduler;

    public MetricsTool(ShiftScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public int priDayRating(ArrayList<ArrayList<Integer>> combinations, int index, LocalDate start) {

        int[] offDays = scheduler.getRotatingDaysOff();

        int day = start.getDayOfWeek().getValue();

        ArrayList<Integer> instance = booleanSchedule(combinations, index);

        int priOff = 0;

        for (Integer intg:offDays) {
            for (int i = 0; i < instance.size(); i++) {
                if (intg == 0) {
                    continue;
                }
                if ((i + day) % intg == 0 && instance.get(i) == 0) {
                    priOff++;
                }
            }
        }

        return priOff;

    }

    public int daysRating(ArrayList<ArrayList<Integer>> combinations, int index, LocalDate start) {

        // get the month and length of month n
        int n = start.lengthOfMonth();
        int length = n;

        // get the correction arithmetic to match the date with the day of week
        // (i + day) % X where X = target day of week, i is the day of month
        int day = start.getDayOfWeek().getValue();

        // iterate n times and subtract 1 from the n value (not the loop iterations) for every weekend day
        for (int i = 0; i < n; i++) {
            if ((i + day) % 6 == 0 || (i + day) % 7 == 0) {
                length--;
            }
        }
        // the remaining number n is the number of theoretical work days for the month
        // count the actual work days in the month
        ArrayList<Integer> boolSchedule = booleanSchedule(combinations, index);
        int actual = 0;
        for (Integer i:boolSchedule) {
            if (i == 1) {
                actual++;
            }
        }

        return (int) Math.ceil(n/actual);

    }

    public int divisonRating(ArrayList<ArrayList<Integer>> combinationStrings, int index) {

        int divisions = 0;

        for (int i = 4; i < combinationStrings.get(index).size(); i++) {
            if (combinationStrings.get(index).get(i) != 0) {
                divisions++;
            }
        }

        int value = 0;

        if (divisions == 8) {
            value = 10;
        }
        else if (divisions >= 6 && divisions < 8) {
            value = 6;
        }
        else if (divisions <= 10 && divisions >8) {
            value = 3;
        }

        return value;

    }

    public int offRating(ArrayList<ArrayList<Integer>> combinations, int index, LocalDate start) {

        // get the month and length of month n
        int n = start.lengthOfMonth();
        int length = 0;

        // get the correction arithmetic to match the date with the day of week
        // (i + day) % X where X = target day of week, i is the day of month
        int day = start.getDayOfWeek().getValue();

        // iterate n times and subtract 1 from the n value (not the loop iterations) for every weekend day
        for (int i = 0; i < n; i++) {
            if ((i + day) % 6 == 0 || (i + day) % 7 == 0) {
                length++;
            }
        }
        // the remaining number n is the number of theoretical work days for the month
        // count the actual work days in the month
        ArrayList<Integer> boolSchedule = booleanSchedule(combinations, index);
        int actual = 0;
        for (Integer i:boolSchedule) {
            if (i == 0) {
                actual++;
            }
        }

        // return a value based on if the acutal work number is within 1 or 2 of n
        int value = 0;

        if (Math.abs(actual - length) == 0) {
            value = 2;
        }
        else if (0 < Math.abs(actual - length) && Math.abs(actual - length) <= 2) {
            value = 1;
        }

        return value;

    }

    public int weekLengthRate(ArrayList<ArrayList<Integer>> combinations, int index) {

        int maxLength = combinations.get(index).get(3);

        return 10 - maxLength;

    }

    public int sickRate(ArrayList<ArrayList<Integer>> combinations, int index) {

        return combinations.get(index).get(1);

    }

    public boolean goodWorkWeek(ArrayList<ArrayList<Integer>> combinations, int index) {

        int[] onIndex = {4, 6, 8, 10, 12, 14, 16};
        int[] offIndex = {5, 7, 9, 11, 13, 15, 17};

        ArrayList<Integer> instance = booleanSchedule(combinations, index);

        int workDays = 0;
        int offDays = 0;

        for (Integer i:instance) {
            if (i == 1) {
                workDays++;
            }
            else {
                offDays++;
            }
        }

        if (offDays == 0) {
            return false;
        }
        return workDays / offDays >= 2.3 && workDays / offDays <= 2.7;

    }

    public boolean goodPriorityDaysOff(ArrayList<ArrayList<Integer>> combinations, int index, LocalDate start) {

        int day = start.getDayOfWeek().getValue();

        ArrayList<Integer> instance = booleanSchedule(combinations, index);

        int priOff = 0;

        for (int i = 0; i < instance.size(); i++) {
            if ((i + day) % 7 == 0 && instance.get(i) == 0) {
                priOff++;
            }
        }

        return priOff >= 2;
    }

    public ArrayList<Integer> booleanSchedule(ArrayList<ArrayList<Integer>> combinations, int index) {

        // initiate an on/off bit
        boolean startOn = combinations.get(index).get(0) == 1;

        int bit = 0;

        if (startOn) {
            bit = 1;
        }

        // initiate a bucket array to hold on/off bits
        ArrayList<Integer> instance = new ArrayList<Integer>();

        // for each index in combinations, add a corresponding number of correct bits to the instance array
        ArrayList<Integer> source = combinations.get(index);
        for (int i = 4; i < source.size(); i++) {
            int chunk = source.get(i);
            for (int j = 0; j < chunk; j++) {
                instance.add(bit);
            }
            // alternate the on/off bit
            if (bit == 0) {
                bit = 1;
            }
            else {
                bit = 0;
            }
        }

        return instance;

    }

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

    public int getMax(XYChart.Series series1) {

        ObservableList list = series1.getData();

        String test = list.get(0).toString();

        String getIt = "";
        int intg = 7;
        while (test.charAt(intg) != ',') {
            getIt = getIt + test.charAt(intg);
            intg++;
        }

        int[] nummers = new int[getIt.length()];

        for (int i = 0; i < getIt.length(); i++) {
            nummers[i] = Integer.parseInt(getIt.charAt(i) + "");
        }

        int max = 0;
        for (int i = 0; i < nummers.length; i++) {
            if (nummers[i] > max) {
                max = nummers[i];
            }
        }

        return max;

    }

    public int getMin(XYChart.Series series1) {

        ObservableList list = series1.getData();

        String test = list.get(0).toString();

        String getIt = "";
        int intg = 7;
        while (test.charAt(intg) != ',') {
            getIt = getIt + test.charAt(intg);
            intg++;
        }

        int[] nummers = new int[getIt.length()];

        for (int i = 0; i < getIt.length(); i++) {
            nummers[i] = Integer.parseInt(getIt.charAt(i) + "");
        }

        int min = 0;
        for (int i = 0; i < nummers.length; i++) {
            if (nummers[i] < min) {
                min = nummers[i];
            }
        }

        return min;

    }
}
