// Controls the tasks list

package com.example.adizangi.keeptrackofhomework;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Stack;


public class TasksAdapter extends ArrayAdapter {

    private static final String TASKS_FILENAME = "tasks.txt";

    private final int resource;

    private ArrayList<Task> tasks;

    private Stack<ArrayList<Task>> tasksHistoryStack;

    // Creates a TasksAdapter for the app with the given context
    public TasksAdapter(Context context, int resource, ArrayList<Task> tasks) {
        super(context, resource, tasks);
        this.resource = resource;
        this.tasks = tasks;
        tasksHistoryStack = new Stack();
        readTasksFromFile();
    }

    @NonNull
    @Override
    // Fills each position of the ListView with a CheckBox View
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(resource, parent, false);
        CheckBox checkBox = view.findViewById(R.id.singleCheckBox);
        Task taskObject = (Task) getItem(position);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            // Makes the task text strike-through when the user checks the task in the given View
            // Undoes this when the user un-checks the task in the given View
            public void onClick(View v) {
                CheckBox checked = (CheckBox) v;
                Log.i(this.getClass().getSimpleName(), "toggle checkbox in tasks " + taskObject);
                if (checked.isChecked()) {
                    taskObject.setDone(true);
                    checked.setPaintFlags(checked.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    taskObject.setDone(false);
                    checked.setPaintFlags(checked.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                saveTasksToFile();
            }
        });
        checkBox.setText(taskObject.toString());
        if (taskObject.isDone()) {
            checkBox.setChecked(true);
            checkBox.setPaintFlags(checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        return view;
    }

    // Reads the tasks from the tasks file and adds them to the ListView
    public void readTasksFromFile() {
        File file = new File(getContext().getFilesDir(),TASKS_FILENAME);
        if (file.exists()) {
            try {
                FileInputStream inputStream = getContext().openFileInput(TASKS_FILENAME);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                addAll((ArrayList<Task>) objectInputStream.readObject());
                objectInputStream.close();
                inputStream.close();
                Log.i(this.getClass().getSimpleName(), "read tasks " + tasks);
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), "Failed to read tasks");
                e.printStackTrace();
            }
        }
    }

    // Saves the tasks list to a file
    public void saveTasksToFile() {
        try {
            Log.i(this.getClass().getSimpleName(), "saving tasks " + tasks);
            FileOutputStream outputStream = getContext().openFileOutput(TASKS_FILENAME, getContext().MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(tasks);
            objectOutputStream.close();
            outputStream.close();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Failed to save tasks " + tasks);
            e.printStackTrace();
        }
    }

    // Keeps the current tasks in the history
    public void keepTasksInHistory() {
        tasksHistoryStack.push((ArrayList<Task>)tasks.clone());
    }

    public void undoLastAction() {
        if (!tasksHistoryStack.empty()) {
            ArrayList<Task> tasksHistory = tasksHistoryStack.pop();
            clear();
            Log.i(this.getClass().getSimpleName(), "undo tasks " + tasksHistory);
            addAll(tasksHistory);
            saveTasksToFile();
        }
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

}
