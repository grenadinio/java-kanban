package service;

public enum TaskStatus {
    NEW("Новый"),
    IN_PROGRESS("В процессе"),
    DONE("Завершен");

    private final String nameStatus;

    TaskStatus(String nameStatus) {
        this.nameStatus = nameStatus;
    }

    public String getName(){
        return nameStatus;
    }
}
