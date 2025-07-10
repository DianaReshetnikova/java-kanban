package web_api;

public enum Endpoint {
    //Tasks
    GET_TASKS,
    GET_TASK_ID,
    POST_TASK,
    DELETE_TASK_ID,
    //Subtasks
    GET_SUBTASKS,
    GET_SUBTASK_ID,
    POST_SUBTASK,
    DELETE_SUBTASK_ID,
    //Epics
    GET_EPICS,
    GET_EPIC_ID,
    GET_SUBTASKS_OF_EPIC,
    POST_EPIC,
    DELETE_EPIC_ID,
    //History and Prioritized
    GET_HISTORY,
    GET_PRIORITIZED,
    UNKNOWN
}