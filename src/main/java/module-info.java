module ShiftScheduler {

    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    requires com.google.common;
    requires javaml;

    opens ShiftScheduler to javafx.fxml;

    exports ShiftScheduler;
    exports sample;

}