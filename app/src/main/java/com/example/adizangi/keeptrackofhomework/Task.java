// Represents a task
// Each Task has text, a due date, and a current state (done or not done)

package com.example.adizangi.keeptrackofhomework;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;


public class Task implements Serializable {

    private String task;

    private boolean isDone;

    private Date dueDate;

    // Creates a Task with the given text and state
    public Task(String task, boolean isDone) {
        this.task = task;
        this.isDone = isDone;
    }

    // Creates a Task with the given text, state, and due date
    public Task(String task, boolean isDone, Date dueDate) {
        this.task = task;
        this.isDone = isDone;
        this.dueDate = dueDate;
    }

    // Creates a Task with the given text and due date
    public Task(String task, Date dueDate) {
        this.task = task;
        this.dueDate = dueDate;
    }

    public String getTask() {
        return task;
    }

    public boolean isDone() {
        return isDone;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    // Returns a string that represents the task
    // Format is the date (month/day), a semicolon, and the task
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        return simpleDateFormat.format(dueDate) + ": " + task;
    }

    public static class TaskDateComparator  implements Comparator<Task> {
        @Override
        // Returns a number less than 0 if the first task's date precedes the  second task's date
        // Returns a number greater than 0 if the second task's date precedes the first task's date
        // Returns 0 if the tasks have the same date
        public int compare(Task t1, Task t2) {
            Date d1 = t1.getDueDate();
            Date d2 = t2.getDueDate();
            return d1.compareTo(d2);
        }
    }

}
