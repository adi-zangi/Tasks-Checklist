// Adi Zangi
// 2018

// The app Tasks Checklist is used to add tasks to a checklist
// It enables to add tasks, delete tasks, and undo the last action

package com.example.adizangi.keeptrackofhomework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private TasksAdapter adapter;

    @Override
    // Initializes the app the way that is was before it was closed, or with an empty tasks list if
    // nothing was saved
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new TasksAdapter(this, R.layout.list_checkbox_item, new ArrayList());
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    // Called when the user taps the Add button
    // Opens a date picker window
    public void onAddClick(View view) {
        EditText taskText = findViewById(R.id.tasksText);
        Task newTask = new Task(taskText.getText().toString(), false);
        taskText.getText().clear();
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setActivity(this);
        datePicker.setTask(newTask);
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    // Called when the user taps the delete button
    // Deletes all the tasks that are checked
    public void onDeleteTasks(View view) {
        adapter.keepTasksInHistory();
        for (int i = adapter.getTasks().size() - 1; i >= 0; i--) {
            if (adapter.getTasks().get(i).isDone()) {
                Log.i(this.getClass().getSimpleName(), "deleting task " + adapter.getTasks().get(i));
                adapter.remove(adapter.getTasks().get(i));
            }
        }
        adapter.saveTasksToFile();
    }

    // Called when the user taps the undo button
    // Sets the state of the app to the state it was in before the user's last action
    public void onUndo(View view) {
        adapter.undoLastAction();
    }

    // Adds the task given by the user to the checklist
    public void addTask(Task newTask) {
        adapter.keepTasksInHistory();
        adapter.add(newTask);
        adapter.sort(new Task.TaskDateComparator());
        Log.i(this.getClass().getSimpleName(), "adding task " + newTask);
        adapter.saveTasksToFile();
    }

}
