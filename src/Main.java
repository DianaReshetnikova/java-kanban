import ru.yandex.practicum.model.Task;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.service.Managers;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.TaskManager;
import web_api.HttpTaskServer;

public class Main {

    public static void main(String[] args) {

        /*TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task 1", "Task 1 description", Status.NEW);
        task1 = taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Task 2 description", Status.NEW);
        task2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic 1 description");
        epic1 = taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.DONE, epic1.getId());
        subTask1 = taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, epic1.getId());
        subTask2 = taskManager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("SubTask 3", "SubTask 3 description", Status.IN_PROGRESS, epic1.getId());
        subTask3 = taskManager.createSubTask(subTask3);

        Epic epic2 = new Epic("Epic 2", "Epic 2 description");
        epic2 = taskManager.createEpic(epic2);


        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        taskManager.getEpicById(epic1.getId());

        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getSubTaskById(subTask3.getId());
        taskManager.getSubTaskById(subTask1.getId());

        printHistoryTasks(taskManager);

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteSubTaskById(subTask2.getId());

        printHistoryTasks(taskManager);

        printAllTasks(taskManager);*/
    }

    private static void printHistoryTasks(TaskManager manager) {
        System.out.println("_____История просмотра задач_____");
        for (var item : manager.getHistory()) {
            System.out.println(item);
        }
        System.out.println('\n');
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("_____Задачи_____");
        for (Task task : manager.getAllTasksList()) {
            System.out.println(task);
        }
        System.out.println("_____Эпики_____");
        for (Task epic : manager.getAllEpicsList()) {
            System.out.println(epic);

            for (Task task : manager.getSubTasksOfEpicById(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("_____Подзадачи_____");
        for (Task subtask : manager.getAllSubTasksList()) {
            System.out.println(subtask);
        }
    }
}