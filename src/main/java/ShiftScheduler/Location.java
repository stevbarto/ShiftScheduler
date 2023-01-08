package ShiftScheduler;

import java.util.ArrayList;

public class Location {

    int id;

    String name;

    String address;

    String city;

    String zip;

    String state;

    public Location(int id, String name, String address, String zip, String city, String state) {

        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.state = state;

    }

}
