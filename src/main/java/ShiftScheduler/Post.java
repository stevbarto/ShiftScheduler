package ShiftScheduler;

public class Post {

    private final boolean priorityFill;
    private int id;

    private String name;

    private int[] daysOn;

    private int shift;

    private int location;

    private int employeeId;

    public Post(int id, int shift, String name, int location, boolean priorityFill) {

        this.id = id;

        this.name = name;

        this.daysOn = new int[7];

        this.priorityFill = priorityFill;

        for (int i = 0; i < this.daysOn.length; i++) {
            this.daysOn[i] = 0;
        }

        this.shift = shift;

        this.location = location;

        this.employeeId = -1;

    }

    public int getId() {
        return this.id;
    }

    public int getShift() {
        return this.shift;
    }

    public void setDay(int day, int value) {

        daysOn[day] = value;

    }

    public int[] getDaysOn() {
        return this.daysOn;
    }

    public boolean getPriority() {
        return this.priorityFill;
    }

    public int getEmployeeId() {
        return this.employeeId;
    }

    public void setEmployeeId(int id) {
        this.employeeId = id;
    }

    public String getName() {

        return this.name;

    }

    public void setName(String name) {
        this.name = name;
    }
}
