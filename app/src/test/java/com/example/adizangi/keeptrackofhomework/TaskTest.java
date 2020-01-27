// Tests the Task class's toString and compare methods

package com.example.adizangi.keeptrackofhomework;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;


public class TaskTest {

    private Task task1 = new Task("Task 1", new Date(2018 - 1900, 1 - 1, 4));
    private Task task2 = new Task("Task 2", new Date(2018 - 1900, 5 - 1, 10));
    private Task task3 = new Task("Task 3", new Date(2018 - 1900, 12 - 1, 29));
    private Task task4 = new Task("Task 4", new Date(2018 - 1900, 12 - 1, 29));
    private Task task5 = new Task("Task 5", new Date(2020 - 1900, 12 - 1, 29));

    @Test
    public void testToString() {

        String actual1 = task1.toString();
        String expected1 = "01/04: Task 1";
        assertEquals(expected1, actual1);

        String actual2 = task2.toString();
        String expected2 = "05/10: Task 2";
        assertEquals(expected2, actual2);

        String actual3 = task3.toString();
        String expected3 = "12/29: Task 3";
        assertEquals(expected3, actual3);
    }

    @Test
    public void testComparator() {

        ArrayList<Task> actual = new ArrayList<>(Arrays.asList(task2, task3, task1));
        actual.sort(new Task.TaskDateComparator());
        ArrayList<Task> expected = new ArrayList<>(Arrays.asList(task1, task2, task3));
        assertEquals(expected, actual);

        actual.add(task5);
        actual.sort(new Task.TaskDateComparator());
        expected = new ArrayList<>(Arrays.asList(task1, task2, task3, task5));
        assertEquals(expected, actual);

        actual.add(task4);
        actual.sort(new Task.TaskDateComparator());
        expected = new ArrayList<>(Arrays.asList(task1, task2, task3, task4, task5));
        assertEquals(expected, actual);
    }

}
