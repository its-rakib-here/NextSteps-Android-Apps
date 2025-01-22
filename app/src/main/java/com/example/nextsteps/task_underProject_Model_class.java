package com.example.nextsteps;

public class task_underProject_Model_class {
    private int taskId;
    private String taskName;
    private String taskDescription;
    private String distributedBy;
    private String status;
    private String deadline;

    // Constructor
    public task_underProject_Model_class(int taskId, String taskName, String taskDescription, String distributedBy, String status, String deadline) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.distributedBy = distributedBy;
        this.status = status;
        this.deadline = deadline;
    }

    // Getters and Setters
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getDistributedBy() {
        return distributedBy;
    }

    public void setDistributedBy(String distributedBy) {
        this.distributedBy = distributedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", distributedBy='" + distributedBy + '\'' +
                ", status='" + status + '\'' +
                ", deadline='" + deadline + '\'' +
                '}';
    }
}

