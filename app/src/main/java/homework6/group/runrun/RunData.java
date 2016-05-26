package homework6.group.runrun;

/**
 * Created by WEI-ZHE on 2016/5/25.
 */
public class RunData {

    private long id;
    private String date;
    private int distance,time,speed;

    public RunData() {
    }

    public RunData(String date, int distance, int time, int speed) {
        this.date = date;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
