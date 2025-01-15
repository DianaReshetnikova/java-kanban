package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Status;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<SubTask> subTasks;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        subTasks = new ArrayList<>();
    }

    public void setSubTask(SubTask subTask) {
        this.subTasks.add(subTask);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void updateEpicStatus(){
        int cntStatusNEW = 0;
        int cntStatusDONE = 0;

        for (SubTask subTask : this.getSubTasks()){
            if (subTask.getStatus().equals(Status.NEW))
                cntStatusNEW++;
            if (subTask.getStatus().equals(Status.DONE))
                cntStatusDONE++;
        }
        if(subTasks.size() == 0 || subTasks.size() == cntStatusNEW)
            setStatus(Status.NEW);
        else if (subTasks.size() == cntStatusDONE)
            setStatus(Status.DONE);
        else
            setStatus(Status.IN_PROGRESS);
    }

    public void removeSubTaskById(int id) {
        for (SubTask subTask : subTasks){
            if(subTask.id.equals(id)){
                subTasks.remove(subTask);
                return;
            }
        }
    }

    public void removeSubTasks(){
        subTasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
