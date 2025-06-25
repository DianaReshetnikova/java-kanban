package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/* InMemoryTaskManager - класс-менеджер, хранит всю информацию о различных типах задач в оперативной памяти.
Реализует методы интерфейса TaskManager.
В объекте класса, реализующего интерфейс HistoryManager, хранится 10 последних просмотренных задач. */

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, SubTask> subTasks;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks;

    protected int counterId;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime().isPresent() && task2.getStartTime().isPresent())
                if (task1.getStartTime().get().isAfter(task2.getStartTime().get()))
                    return 1;
                else return -1;
            return 0;//задач у которых не задано Время старта здесь не должно быть по ТЗ
        });
        counterId = 0;
    }

    private void addTaskToPrioritizedSet(Task task) {
        if (task.getStartTime().isPresent())
            prioritizedTasks.add(task);
    }

    @Override
    public Task createTask(Task newTask) {
        //метод должен вернуть false (задачи не перекрываются)
        if (newTask != null && !isTaskOverlapWithAnySavedTask(newTask)) {
            if (tasks.containsKey(newTask.getId()))
                return newTask;

            //Задаем уникальный Id новой задаче
            newTask.setId(++counterId);
            tasks.put(newTask.getId(), newTask);
            addTaskToPrioritizedSet(newTask);
        }

        return newTask;
    }

    @Override
    public Epic createEpic(Epic newEpic) {
        if (newEpic != null && !isTaskOverlapWithAnySavedTask(newEpic)) {
            if (epics.containsKey(newEpic.getId()))
                return newEpic;

            newEpic.setId(++counterId);
            updateEpicTime(newEpic);
//            if (newEpic.getStartTime().isPresent() && newEpic.getDuration().isPresent())
//                newEpic.setEndTime(newEpic.getStartTime().get().plus(newEpic.getDuration().get()));

            addTaskToPrioritizedSet(newEpic);
        }

        return newEpic;
    }

    @Override
    public SubTask createSubTask(SubTask newSubTask) {
        if (newSubTask != null && !isTaskOverlapWithAnySavedTask(newSubTask)) {
            //Если сабтаск с Id уже сущ-ет в мапе, или указан эпик которого нет в мапе эпиков
            //то возврат сабтаски
            if (subTasks.containsKey(newSubTask.getId()) ||
                    !epics.containsKey(newSubTask.getEpicId()))
                return newSubTask;

            newSubTask.setId(++counterId);
            subTasks.put(newSubTask.getId(), newSubTask);
            addTaskToPrioritizedSet(newSubTask);

            Epic epic = epics.get(newSubTask.getEpicId());
            epic.setSubTask(newSubTask.getId());

            updateEpicStatus(epic);
            updateEpicTime(epic);
        }

        return newSubTask;
    }


    @Override
    public Task updateTask(Task newTask) {
        if (newTask != null && !isTaskOverlapWithAnySavedTask(newTask)) {
            if (!tasks.containsKey(newTask.getId()))
                return newTask;

            tasks.put(newTask.getId(), newTask);
            addTaskToPrioritizedSet(newTask);
        }

        return newTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        if (newEpic != null && !isTaskOverlapWithAnySavedTask(newEpic)) {
            if (!epics.containsKey(newEpic.getId()))
                return newEpic;

            updateEpicStatus(newEpic);
            epics.put(newEpic.getId(), newEpic);

            updateEpicTime(newEpic);
        }

        return newEpic;
    }

    @Override
    public SubTask updateSubTask(SubTask newSubTask) {
        if (newSubTask != null && !isTaskOverlapWithAnySavedTask(newSubTask)) {
            if (!subTasks.containsKey(newSubTask.getId()) ||
                    !epics.containsKey(newSubTask.getEpicId()))
                return newSubTask;

            subTasks.put(newSubTask.getId(), newSubTask);
            addTaskToPrioritizedSet(newSubTask);

            Epic epic = epics.get(newSubTask.getEpicId());
            epic.removeSubTaskById(newSubTask.getId());
            epic.setSubTask(newSubTask.getId());

            updateEpicStatus(epic);
            updateEpicTime(epic);

            epics.put(epic.getId(), epic);
        }

        return newSubTask;
    }


    @Override
    public List<Task> getAllTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }


    //В HistoryManager история об ныне удаленных задачах, эпиках и подзадачах хранится и не стирается
    // думаю это логично, как логирование
    @Override
    public void deleteAllTasks() {
        tasks.clear();
        prioritizedTasks.removeAll(tasks.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();

        //Вместе с эпиками удаляются все их подзадачи - вторые не могут существовать без первых
        prioritizedTasks.removeAll(epics.values());
        prioritizedTasks.removeAll(subTasks.values());
    }

    //очищаю мапу сабтасок
    //прохожу по мапе эпиков и очищаю в каждом лист с Id сабтасок
    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();

//        epics.values().stream()
//                .peek(Epic::removeSubTasks)
//                .peek(this::updateEpicStatus)
//                .peek(this::updateEpicTime)
//                ;

        for (Epic epic : epics.values()) {
            epic.removeSubTasks();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }

        prioritizedTasks.removeAll(subTasks.values());
    }


    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        historyManager.add(subTasks.get(subTaskId));
        return subTasks.get(subTaskId);
    }


    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.get(id);

        historyManager.remove(task.getId());
        tasks.remove(task.getId());
        prioritizedTasks.remove(task);
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<Integer> epicSubTaskIds = epic.getSubTaskIds();
            for (var subTaskId : epicSubTaskIds) {
                SubTask subTask = subTasks.get(subTaskId);

                historyManager.remove(subTask.getId());
                subTasks.remove(subTask.getId());

                //удалить все подзадачи эпика из Приоритетных задач
                prioritizedTasks.remove(subTask);
            }

//            epicSubTaskIds.stream()
//                    .peek(historyManager::remove)
//                    .peek(subTasks::remove)
//                    .peek(subTaskId -> prioritizedTasks.remove(subTasks.get(subTaskId)));

            //удалить сам эпик отовсюду
            historyManager.remove(epic.getId());
            epics.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask != null) {
            historyManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
            prioritizedTasks.remove(subTask);

            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubTaskById(subTask.getId());
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
        }
    }


    @Override
    public List<SubTask> getSubTasksOfEpicById(int epicId) {
        List<SubTask> subTasksOfEpic = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subTasksOfEpic.add(subTasks.get(subTaskId));
            }
        }


//        List<SubTask> subTasksOfEpic = epics.get(epicId).getSubTaskIds().stream()
//                .map(subTasks::get)
//                .collect(Collectors.toList());


        return subTasksOfEpic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    //Метод, который будет проверять, пересекаются ли две задачи по времени выполнения, и возвращать true или false.
    //Если перекрываются - true, иначе - false
    private boolean isTwoTasksOverlap(Task task1, Task task2) {

        //Для избежания сравнения старой сохраненной задачи в списке с новой обновленной
        //(две версии одной и той же таски - старая и новая)
        if (Objects.equals(task1.getId(), task2.getId()))
            return false;

        if (task1.getStartTime().isPresent() && task2.getStartTime().isPresent()) {
            LocalDateTime startTime1 = task1.getStartTime().get();
            LocalDateTime startTime2 = task2.getStartTime().get();

            LocalDateTime endTime1;
            LocalDateTime endTime2;

            if (task1.getEndTime().isPresent() && task2.getEndTime().isPresent()) {
                endTime1 = task1.getEndTime().get();
                endTime2 = task2.getEndTime().get();

                return ((startTime2.isAfter(startTime1) || startTime2.equals(startTime1)) &&
                        (startTime2.isBefore(endTime1) || startTime2.equals(endTime1)))
                        ||
                        ((startTime2.isBefore(startTime1) || startTime2.equals(startTime1)) &&
                                (endTime2.isAfter(startTime1) || endTime2.equals(startTime1)))
                        ;

            } else if (task1.getEndTime().isPresent() && task2.getEndTime().isEmpty()) {
                endTime1 = task1.getEndTime().get();

                return ((startTime2.isAfter(startTime1) || startTime2.equals(startTime1)) &&
                        (startTime2.isBefore(endTime1) || startTime2.equals(endTime1)));
            } else if (task1.getEndTime().isEmpty() && task2.getEndTime().isPresent()) {
                endTime2 = task2.getEndTime().get();

                return ((startTime2.isBefore(startTime1) || startTime2.equals(startTime1)) &&
                        (endTime2.isAfter(startTime1) || endTime2.equals(startTime1)));
            } else {
                return startTime1.equals(startTime2);
            }
        }

        return true;
    }

    //Метод проверяющий пересекается ли задача с любой другой в списке менеджера
    public boolean isTaskOverlapWithAnySavedTask(Task targetTask) {

        //если StartTime не задан, считаем, что задача никого не перекроет
        //даже если задано поле Duration
        if (targetTask.getStartTime().isEmpty())
            return false;

        for (var task : prioritizedTasks) {
            if (isTwoTasksOverlap(targetTask, task))
                return true;
        }

        return false;
    }

    private void updateEpicStatus(Epic epic) {
        int cntStatusNEW = 0;
        int cntStatusDONE = 0;

        List<SubTask> subTasks = getSubTasksOfEpicById(epic.getId());

        for (SubTask subTask : subTasks) {
            if (subTask.getStatus().equals(Status.NEW))
                cntStatusNEW++;
            if (subTask.getStatus().equals(Status.DONE))
                cntStatusDONE++;
        }
        if (subTasks.isEmpty() || subTasks.size() == cntStatusNEW)
            epic.setStatus(Status.NEW);
        else if (subTasks.size() == cntStatusDONE)
            epic.setStatus(Status.DONE);
        else
            epic.setStatus(Status.IN_PROGRESS);
    }

    private void updateEpicTime(Epic epic) {
        //время начала -дата старта самой ранней подзадачи,
        // время завершения — время окончания самой поздней из задач.
        //Продолжительность эпика — сумма продолжительностей всех его подзадач.

        //Получить список подзадач для этого эпика
        List<SubTask> subtasks = getSubTasksOfEpicById(epic.getId());

        //при создании и обновлении эпика заходим сюда если нет подзадач
        if (subtasks != null && subtasks.isEmpty()) {
            if (epic.getStartTime().isPresent() && epic.getDuration().isPresent()) {
                epic.setEndTime(epic.getStartTime().get().plus(epic.getDuration().get()));
            }

            prioritizedTasks.remove(epic);
            Epic newEpic = new Epic(
                    epic.getTitle(),
                    epic.getDescription(),
                    epic.getId(),
                    epic.getStatus(),
                    epic.getStartTime().orElse(null),
                    epic.getDuration().orElse(null)
            );
            newEpic.setEndTime(epic.getEndTime().orElse(null));
            newEpic.setSubTasksList(epic.getSubTaskIds());
            epics.put(epic.getId(), epic);
            prioritizedTasks.add(epic);

            return;
        }

        //если есть сабтаски, нужно пересчитать время эпика, сравнив время сабтасок
        Optional<LocalDateTime> startTime = epic.getStartTime();//либо пусто, либо есть значения
        Optional<LocalDateTime> endTime = epic.getEndTime();

        //может вернуть пустое значение - если минимума нет
        Optional<LocalDateTime> minStartTime = subtasks.stream()
                .map(Task::getStartTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> maxEndTime = subtasks.stream()
                .map(Task::getEndTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(LocalDateTime::compareTo);


        //Я здесь уже запуталась с использованием Optional... Стала присваивать null - на сколько это корректно?
        LocalDateTime newStartTime = null;
        LocalDateTime newEndTime = null;
        Duration newDuration = null;

        //Идея подсказывает, что можно лямбды, но мне пока тяжело идет...
        if (minStartTime.isPresent()) {
            if (startTime.isPresent())//есть старое время не пусто и новый минимум не пуст, нужно их сравнить
                newStartTime = minStartTime.get().isBefore(startTime.get()) ? minStartTime.get() : startTime.get();
            else
                newStartTime = minStartTime.get();
        } else {
            //новый минимум среди сабтасок не найден
            //если старое значение не пусто, возвращаем его, иначе пишем null (если старое - пустое)
            newStartTime = startTime.orElse(null);
        }

        if (maxEndTime.isPresent()) {
            if (endTime.isPresent())
                newEndTime = maxEndTime.get().isAfter(endTime.get()) ? maxEndTime.get() : endTime.get();
            else
                newEndTime = maxEndTime.get();
        } else {
            newEndTime = endTime.orElse(null);
        }

        if (newStartTime != null && newEndTime != null)
            newDuration = Duration.between(newStartTime, newEndTime);

        //удалить старый эпик из списка приоритетных задач
        prioritizedTasks.remove(epic);

        //создаем новый эпик с обновленными значениями времени
        Epic newEpic = new Epic(
                epic.getTitle(),
                epic.getDescription(),
                epic.getId(),
                epic.getStatus(),
                newStartTime,
                newDuration
        );
        newEpic.setEndTime(newEndTime);
        newEpic.setSubTasksList(epic.getSubTaskIds());

        //заменяем в списке эпиков старое значение
        epics.put(epic.getId(), newEpic);

        //добавляем в список приоритетных задач новый обновленный эпик
        prioritizedTasks.add(newEpic);
    }
}
