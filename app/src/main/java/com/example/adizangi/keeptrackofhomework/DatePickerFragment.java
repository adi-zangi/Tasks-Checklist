// Creates a date picker dialog

package com.example.adizangi.keeptrackofhomework;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import java.util.Calendar;
import java.util.Date;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private MainActivity activity;

    private Task task;

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    // Creates a date picker dialog with an option to pick a day, month, and year
    // Uses the current date as the default date in the picker
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    // Called when the user taps OK in the dialog
    // Adds the task given by the user with the given date
    public void onDateSet(DatePicker view, int year, int month, int day) {
        task.setDueDate(new Date(year - 1900, month, day));
        activity.addTask(task);
    }



}
