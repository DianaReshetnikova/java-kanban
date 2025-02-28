import ru.yandex.practicum.model.Task;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.service.Managers;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        //Создание task1 и task2 задач и обновление task1
        Task task1 = new Task("Task 1", "Task 1 description", Status.NEW);
        task1 = taskManager.createTask(task1);

        taskManager.getTaskById(task1.getId());

        Task task2 = new Task("Task 2", "Task 2 description", Status.NEW);
        task2 = taskManager.createTask(task2);

        taskManager.getTaskById(task2.getId());

        //изменили Task 1
        Task task1Updated = new Task(task1.getTitle(), task1.getDescription(), Status.DONE, task1.getId());
        task1 = taskManager.updateTask(task1Updated);

        taskManager.getTaskById(task1.getId());

        //Создание epic1 с двумя subTask и epic2 с одной subTask
        Epic epic1 = new Epic("Epic 1", "Epic 1 description");
        epic1 = taskManager.createEpic(epic1);

        taskManager.getEpicById(epic1.getId());

        SubTask subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.DONE, epic1.getId());
        subTask1 = taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, epic1.getId());
        subTask2 = taskManager.createSubTask(subTask2);

        Epic epic2 = new Epic("Epic 2", "Epic 2 description");
        epic2 = taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask("SubTask 3", "SubTask 3 description", Status.IN_PROGRESS, epic2.getId());
        subTask3 = taskManager.createSubTask(subTask3);

        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getSubTaskById(subTask3.getId());

        //Обновление статуса у subTask3
        SubTask subTask3Updated = new SubTask("SubTask 3-Updated", "SubTask 3-Updated description",
                Status.DONE, epic2.getId(), subTask3.getId());
        subTask3 = taskManager.updateSubTask(subTask3Updated);

        printAllTasks(taskManager);

        taskManager.deleteSubTaskById(subTask1.getId());
        taskManager.deleteEpicById(epic1.getId());
    }


    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasksList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpicsList()) {
            System.out.println(epic);

            for (Task task : manager.getSubTasksOfEpicById(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasksList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
