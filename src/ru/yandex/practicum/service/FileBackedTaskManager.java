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
    private final Path filePath;

    public FileBackedTaskManager(Path filePath) {
        this.filePath = filePath;
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

    //Cохраняет текущее состояние менеджера (задачи, подзадачи и эпики) в указанный файл.
    private void save() {
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
}
