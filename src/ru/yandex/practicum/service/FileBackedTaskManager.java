package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/*
 * Вторая реализация интерфейса TaskManager.
 * Класс менеджера, который после каждой модифицирующей операции (создание, изменение, удаление)
 * будет автоматически сохранять все задачи и их состояние в специальный файл в папке проекта.
 */
public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static Path filePath;

    public FileBackedTaskManager(Path filePath) {
        FileBackedTaskManager.filePath = filePath;
    }


    @Override
    public Task createTask(Task newTask) {
        Task task = super.createTask(newTask);
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
        return task;
    }

    @Override
    public Epic createEpic(Epic newEpic) {
        Epic epic = super.createEpic(newEpic);
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask newSubTask) {
        SubTask subTask = super.createSubTask(newSubTask);
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
        return subTask;
    }

    @Override
    public Task updateTask(Task newTask) {
        Task task = super.updateTask(newTask);
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
        return task;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic epic = super.updateEpic(newEpic);
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
        return epic;
    }

    @Override
    public SubTask updateSubTask(SubTask newSubTask) {
        SubTask subTask = super.updateSubTask(newSubTask);
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
        return subTask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        super.deleteSubTaskById(subTaskId);
        try {
            save();
        } catch (ManagerSaveException ex) {
            System.out.println(ex.getMessage());
        }
    }


    //Cохраняет текущее состояние менеджера (задачи, подзадачи и эпики) в указанный файл.
    public void save() throws ManagerSaveException {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(filePath.getFileName().toString()))) {
            StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic\n");

            for (var item : getAllTasksList())
                sb.append(toString(item)).append("\n");

            for (var item : getAllEpicsList())
                sb.append(toString(item)).append("\n");

            for (var item : getAllSubTasksList())
                sb.append(toString(item)).append("\n");

            bf.write(sb.toString());

        } catch (IOException ex) {
            throw new ManagerSaveException("Возникла ошибка при записи в файл: " + filePath.getFileName().toString());
        }
    }

    //Восстанавливает данные менеджера из файла при запуске программы
    public static FileBackedTaskManager loadFromFile(Path filePath) {
        FileBackedTaskManager fileBackedManager = new FileBackedTaskManager(filePath);

        try {
            List<String> tasksList = Files.readAllLines(filePath);//считать все задачи в виде строк
            if (tasksList.isEmpty())
                return fileBackedManager;

            //1 строка опускается - служебная
            for (var item : tasksList.subList(1, tasksList.size())) {
                Task task = fileBackedManager.fromString(item);

                //Поиск последнего максимального Id задачи
                if (task.getId() > fileBackedManager.counterId)
                    fileBackedManager.counterId = task.getId();

                switch (task.getTaskType()) {
                    case TASK -> fileBackedManager.tasks.put(task.getId(), task);
                    case EPIC -> fileBackedManager.epics.put(task.getId(), (Epic) task);
                    case SUBTASK -> {
                        fileBackedManager.subTasks.put(task.getId(), (SubTask) task);
                        Epic parentEpic = fileBackedManager.epics.get(((SubTask) task).getEpicId());
                        parentEpic.setSubTask(task.getId());
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Произошла ошибка во время чтения файлов");
        }

        return fileBackedManager;
    }

    //Переводит объект Task в строку
    private String toString(Task task) {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();

        sb.append(task.getId()).append(delimiter);
        sb.append(task.getTaskType()).append(delimiter);
        sb.append(task.getTitle()).append(delimiter);
        sb.append(task.getStatus()).append(delimiter);
        sb.append(task.getDescription()).append(delimiter);

        if (task instanceof SubTask)
            sb.append(((SubTask) task).getEpicId());

        return sb.toString();
    }

    //Восстанавливает объект Task из строки
    private Task fromString(String value) {
        String[] splitArray = value.split(",");

        int id = Integer.parseInt(splitArray[0]);
        TaskType taskType = TaskType.valueOf(splitArray[1]);
        String name = splitArray[2];
        Status status = Status.valueOf(splitArray[3]);
        String description = splitArray[4];
        int epicId = -1;
        if (taskType.equals(TaskType.SUBTASK))
            epicId = Integer.parseInt(splitArray[5]);

        return switch (taskType) {
            case EPIC -> new Epic(name, description, id, status);
            case SUBTASK -> new SubTask(name, description, status, epicId, id);
            case TASK -> new Task(name, description, status, id);
        };
    }


    public static void main(String[] args) {

        try {
            Path filePath = Paths.get("TasksList.csv");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(filePath);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            } else {
                fileBackedTaskManager = loadFromFile(filePath);
                System.out.println("Данные, полученные из файла:");
                printAllTasks(fileBackedTaskManager);
            }


            Task task1 = new Task("Task 1", "Task 1 description", Status.NEW);
            task1 = fileBackedTaskManager.createTask(task1);

            Epic epic1 = new Epic("Epic 1", "Epic 1 description");
            epic1 = fileBackedTaskManager.createEpic(epic1);

            SubTask subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.DONE, epic1.getId());
            subTask1 = fileBackedTaskManager.createSubTask(subTask1);
            SubTask subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, epic1.getId());
            subTask2 = fileBackedTaskManager.createSubTask(subTask2);

            System.out.println("\n_______Данные, получнные из файла и новые добавленные задачи_______\n");
            printAllTasks(fileBackedTaskManager);


        } catch (IOException e) {
            System.out.println("Произошла ошибка во время создания файла");
        }
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
