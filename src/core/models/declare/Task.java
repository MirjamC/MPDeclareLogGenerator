package core.models.declare;

/**
 * Created by Vasiliy on 2017-10-17.
 */
public class Task {
    String name;

    public Task() {
    }

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
