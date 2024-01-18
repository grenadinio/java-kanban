package model;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(Epic epic, String name, String description) {
        super(name, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
