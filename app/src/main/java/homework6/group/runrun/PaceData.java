package homework6.group.runrun;

/**
 * Created by WEI-ZHE on 2016/5/25.
 */
public class PaceData {

    long id;
    private String date;
    private int pace;

    public PaceData() {
    }

    public PaceData(String date, int pace) {
        this.date = date;
        this.pace = pace;
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

    public int getPace() {
        return pace;
    }

    public void setPace(int pace) {
        this.pace = pace;
    }
}
