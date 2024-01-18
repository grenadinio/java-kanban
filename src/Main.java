import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManager;
import service.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        //Создание двух задач
        System.out.println("-".repeat(20) + "Создание двух задач" + "-".repeat(20));

        Task firstTask = taskManager.createTask(new Task("Новая задача","Описание первой задачи."));
        System.out.println("Создали задачу: " + firstTask);

        Task secondTask = taskManager.createTask(new Task("Вторая задача","Описание второй задачи."));
        System.out.println("Создали задачу: " + secondTask);

        //Создание первого эпика
        System.out.println("-".repeat(20) + "Создание первого эпика" + "-".repeat(20));

        Epic firstEpic = taskManager.createEpic(new Epic("Первый эпик","Описание первого эпика"));
        System.out.println("Создали эпик: " + firstEpic);

        SubTask firstSubTask = taskManager.createSubTask(new SubTask(firstEpic,"Первая подзадача","Описание первой подзадачи"));
        SubTask secondSubTask = taskManager.createSubTask(new SubTask(firstEpic,"Вторая подзадача","Описание второй подзадачи"));
        System.out.println("Создали подзадачу: " + firstSubTask);
        System.out.println("Создали подзадачу: " + secondSubTask);
        System.out.println("Эпик с двумя подзадачами: " + firstEpic);

        //Создание второго эпика
        System.out.println("-".repeat(20) + "Создание второго эпика" + "-".repeat(20));

        Epic secondEpic = taskManager.createEpic(new Epic("Первый эпик","Описание первого эпика"));
        System.out.println("Создали эпик: " + secondEpic);

        SubTask subTask = taskManager.createSubTask(new SubTask(secondEpic,"Новая подзадача","Описание новой подзадачи"));
        System.out.println("Создали подзадачу: " + subTask);
        System.out.println("Эпик с одной подзадачей: " + secondEpic);

        //Изменяем статусы
        System.out.println("-".repeat(20) + "Изменение статусов" + "-".repeat(20));

        firstTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(firstTask);
        System.out.println(firstTask);

        secondTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(secondTask);
        System.out.println(secondTask);

        firstSubTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(firstSubTask);
        System.out.println(firstEpic);

        secondSubTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(secondSubTask);
        System.out.println(firstEpic);

        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask);
        System.out.println(secondEpic);

        //Удаление
        System.out.println("-".repeat(20) + "Удаление" + "-".repeat(20));

        System.out.println("Задачи до удаления: " + taskManager.getTasks());
        taskManager.removeTaskById(secondTask.getId());
        System.out.println("Задачи после удаления второй задачи: " + taskManager.getTasks());

        System.out.println("Эпики до удаления: " + taskManager.getEpics());
        taskManager.removeEpicById(firstEpic.getId());
        System.out.println("Эпики после удаления первого эпика: " + taskManager.getEpics());
    }
}
