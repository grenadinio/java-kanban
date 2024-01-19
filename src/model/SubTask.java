package model;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(Integer epicId, String name, String description) {
        super(name, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}
