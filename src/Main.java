import ru.yandex.practicum.model.Task;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        //Создание task1 и task2 задач и обновление task1
        Task task1 = new Task("Task 1", "Task 1 description", Status.NEW);
        task1 = taskManager.createTask(task1);
        System.out.println("Задача создана:\n" + taskManager.getTaskById(task1.getId()));

        Task task2 = new Task("Task 2", "Task 2 description", Status.NEW);
        task2 = taskManager.createTask(task2);
        System.out.println("Задача создана:\n" + taskManager.getTaskById(task2.getId()));

        Task task1Updated = new Task(task1.getTitle(), task1.getDescription(), Status.DONE, task1.getId());
        task1 = taskManager.updateTask(task1Updated);
        System.out.println("Задача изменена:\n" + taskManager.getTaskById(task1Updated.getId()));


        //Создание epic1 с двумя subTask и epic2 с одной subTask
        Epic epic1 = new Epic("Epic 1", "Epic 1 description");
        epic1 = taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.DONE, epic1.getId());
        subTask1 = taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, epic1.getId());
        subTask2 = taskManager.createSubTask(subTask2);

        Epic epic2 = new Epic("Epic 2", "Epic 2 description");
        epic2 = taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask("SubTask 3", "SubTask 3 description", Status.IN_PROGRESS, epic2.getId());
        subTask3 = taskManager.createSubTask(subTask3);

        //Вывод
        System.out.println("\nВсе Task: \n" + taskManager.getAllTasksList());
        System.out.println("Все Epic: \n" + taskManager.getAllEpicsList());
        System.out.println("Все SubTask: \n" + taskManager.getAllSubTasksList());

        //Обновление статуса у subTask3
        SubTask subTask3Updated = new SubTask("SubTask 3-Updated", "SubTask 3-Updated description",
                Status.DONE, epic2.getId(), subTask3.getId());
        subTask3 = taskManager.updateSubTask(subTask3Updated);
        System.out.println("\nEpic 2 после изменения subTask3:\n " + taskManager.getEpicById(epic2.getId()));

        taskManager.deleteSubTaskById(subTask1.getId());
        System.out.println("\nВсе Epic после удаления subTask1: \n" + taskManager.getAllEpicsList());
        System.out.println("Все SubTask: \n" + taskManager.getAllSubTasksList());

        taskManager.deleteEpicById(epic1.getId());
        System.out.println("\nВсе Epic после удаления epic1: \n" + taskManager.getAllEpicsList());
        System.out.println("Все SubTask: \n" + taskManager.getAllSubTasksList());
    }
}
