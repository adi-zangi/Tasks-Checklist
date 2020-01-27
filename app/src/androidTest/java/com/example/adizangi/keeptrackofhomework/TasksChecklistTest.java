// Tests the app by running it

package com.example.adizangi.keeptrackofhomework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.intercepting.SingleActivityFactory;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.internal.deps.dagger.internal.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.anything;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class TasksChecklistTest {

    public class ControlledActivityTestRule<T extends Activity> extends ActivityTestRule<T> {
        public ControlledActivityTestRule(Class<T> activityClass) {
            super(activityClass, false);
        }

        public ControlledActivityTestRule(Class<T> activityClass, boolean initialTouchMode) {
            super(activityClass, initialTouchMode, true);
        }

        public ControlledActivityTestRule(Class<T> activityClass, boolean initialTouchMode, boolean launchActivity) {
            super(activityClass, initialTouchMode, launchActivity);
        }

        public ControlledActivityTestRule
                (SingleActivityFactory<T> injector, boolean initialTouchMode, boolean launchActivity) {
            super(injector, initialTouchMode, launchActivity);
        }

        public void finish() {
            finishActivity();
        }

        public void relaunchActivity() {
            finishActivity();
            launchActivity();
        }

        public void launchActivity() {
            launchActivity(getActivityIntent());
        }
    }

    private static final String TASKS_FILENAME = "tasks.txt";

    private Task task1 = new Task("Task 1", new Date(2018 - 1900, 6 - 1, 9));
    private Task task2 = new Task("Task 2", new Date(2018 - 1900, 7 - 1, 14));
    private Task task3 = new Task("Task 3", new Date(2018 - 1900, 8 - 1, 17));
    private Task task4 = new Task("Task 4", new Date(2019 - 1900, 4 - 1, 23));

    private boolean deleteFileOnCreate = true;

    private SingleActivityFactory<MainActivity> injectedFactory = new SingleActivityFactory<MainActivity>(MainActivity.class) {
        @Override
        protected MainActivity create(Intent intent) {
            if (deleteFileOnCreate) {
                deleteTasksToFile();
            }
            MainActivity activity = new MainActivity();
            return activity;
        }
    };

    @Rule
    public ControlledActivityTestRule<MainActivity> mActivityRule = new ControlledActivityTestRule<>(
            injectedFactory, false, true);

    @Test
    public void testAddTask() {
        Log.i(this.getClass().getSimpleName(), "testing add task ");
        validateTaskList(new ArrayList());
        addTask(task1);
        validateTaskList(new ArrayList(Arrays.asList(task1)));
    }

    @Test
    public void testAddTaskWithNoText() {
        Log.i(this.getClass().getSimpleName(), "testing add task with no text");
        Task task = new Task("", false);
        Date date = new Date(2018 - 1900, 6 - 1, 9);
        task.setDueDate(date);
        validateTaskList(new ArrayList());
        addTask(task);
        validateTaskList(new ArrayList(Arrays.asList(task)));
    }

    @Test
    public void testAddTaskWithLongText() {
        Log.i(this.getClass().getSimpleName(), "testing add task with long text");
        Task task = new Task("abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcde"
                + "fghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789", false);
        Date date = new Date(2018 - 1900, 6 - 1, 9);
        task.setDueDate(date);
        validateTaskList(new ArrayList());
        addTask(task);
        validateTaskList(new ArrayList(Arrays.asList(task)));
    }

    @Test
    public void testSortTasks() {
        Log.i(this.getClass().getSimpleName(), "testing sort task ");
        validateTaskList(new ArrayList());
        addTask(task2);
        validateTaskList(new ArrayList(Arrays.asList(task2)));
        addTask(task1);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2)));
        addTask(task4);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2, task4)));
        addTask(task3);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2, task3, task4)));
    }

    @Test
    public void testToggleCheckbox(){
        Log.i(this.getClass().getSimpleName(), "testing toggle checkbox ");
        validateTaskList(new ArrayList());
        addTask(task3);
        validateTaskList(new ArrayList(Arrays.asList(task3)));
        toggleCheckbox(task3, 0);
        validateTaskList(new ArrayList(Arrays.asList(task3)));
        toggleCheckbox(task3, 0);
        validateTaskList(new ArrayList(Arrays.asList(task3)));
        addTask(task2);
        validateTaskList(new ArrayList(Arrays.asList(task2, task3)));
        toggleCheckbox(task2, 0);
        validateTaskList(new ArrayList(Arrays.asList(task2, task3)));
        toggleCheckbox(task2, 0);
        validateTaskList(new ArrayList(Arrays.asList(task2, task3)));
        toggleCheckbox(task2, 0);
        addTask(task1);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2, task3)));
    }

    @Test
    public void testDeleteTasks() {
        Log.i(this.getClass().getSimpleName(), "testing delete task ");
        validateTaskList(new ArrayList());
        addTask(task1);
        validateTaskList(new ArrayList(Arrays.asList(task1)));
        addTask(task2);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2)));
        addTask(task3);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2, task3)));
        deleteTask(task2, 1);
        validateTaskList(new ArrayList(Arrays.asList(task1, task3)));
        deleteTask(task3, 1);
        validateTaskList(new ArrayList(Arrays.asList(task1)));
        deleteTask(task1, 0);
        validateTaskList(new ArrayList());
    }

    @Test
    public void testUndo() {
        Log.i(this.getClass().getSimpleName(), "testing undo task ");
        validateTaskList(new ArrayList());
        addTask(task1);
        validateTaskList(new ArrayList(Arrays.asList(task1)));
        addTask(task2);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2)));
        addTask(task3);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2, task3)));
        addTask(task4);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2, task3, task4)));
        clickUndo();
        validateTaskList(new ArrayList(Arrays.asList(task1, task2, task3)));
        clickUndo();
        validateTaskList(new ArrayList(Arrays.asList(task1, task2)));
        clickUndo();
        validateTaskList(new ArrayList(Arrays.asList(task1)));
        clickUndo();
        validateTaskList(new ArrayList());
        addTask(task1);
        validateTaskList(new ArrayList(Arrays.asList(task1)));
        addTask(task2);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2)));
        deleteTask(task2, 1);
        validateTaskList(new ArrayList(Arrays.asList(task1)));
        deleteTask(task1, 0);
        validateTaskList(new ArrayList());
        clickUndo();
        validateTaskList(new ArrayList(Arrays.asList(task1)));
        clickUndo();
        validateTaskList(new ArrayList(Arrays.asList(task1, task2)));
    }

    @Test
    public void testPersistenceTasks() {
        Log.i(this.getClass().getSimpleName(), "testing persistence tasks ");
        validateTaskList(new ArrayList());
        addTask(task1);
        addTask(task2);
        addTask(task3);
        validateTaskList(new ArrayList(Arrays.asList(task1, task2, task3)));
        restartApplication();
        validateTaskList(new ArrayList(Arrays.asList(task1, task2, task3)));
    }

    public static Matcher<Object> withItemContent(String expectedText) {
        checkNotNull(expectedText);
        return withItemContent(Matchers.equalTo(expectedText));
    }

    @SuppressWarnings("rawtypes")
    public static Matcher<Object> withItemContent(final Matcher<String> itemTextMatcher) {
        checkNotNull(itemTextMatcher);
        return new BoundedMatcher<Object, CheckBox>(CheckBox.class) {
            @Override
            public boolean matchesSafely(CheckBox checkbox) {
                return itemTextMatcher.matches(checkbox.getText());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with item content: ");
                itemTextMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<Object> containPainFlags(int flag) {
        return new BoundedMatcher<Object, CheckBox>(CheckBox.class) {
            @Override
            public boolean matchesSafely(CheckBox checkbox) {
                return (checkbox.getPaintFlags() & flag) == flag ;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Contain Pain Flags: " + flag);
            }
        };
    }

    public static Matcher<Object> notContainPainFlags(int flag) {
        return new BoundedMatcher<Object, CheckBox>(CheckBox.class) {
            @Override
            public boolean matchesSafely(CheckBox checkbox) {
                return (checkbox.getPaintFlags() & flag) == 0 ;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Not Contain Pain Flags: " + flag);
            }
        };
    }

    private void addTask(Task task) {
        onView(withId(R.id.tasksText))
                .perform(typeText(task.getTask()), closeSoftKeyboard());
        onView(withId(R.id.addButton)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(task.getDueDate().getYear(), task.getDueDate().getMonth() + 1, task.getDueDate().getDate()));
        onView(withId(android.R.id.button1)).perform(click());
    }

    private void deleteTask(Task task, int position) {
        if (!task.isDone()) {
            task.setDone(true);
            onData(anything())
                    .inAdapterView(withId(R.id.listView))
                    .atPosition(position).perform(click());
        }
        onView(withId(R.id.deleteButton)).perform(click());
    }

    public void deleteTasksToFile() {
        try {
            Log.i(this.getClass().getSimpleName(), "delete task file");
            Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
            FileOutputStream outputStream = context.openFileOutput(TASKS_FILENAME, context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(new ArrayList<Task>());
            objectOutputStream.close();
            outputStream.close();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Failed to delete task file", e);
        }
    }

    private void toggleCheckbox(Task task, int position) {
        task.setDone(!task.isDone());
        onData(anything())
                .inAdapterView(withId(R.id.listView))
                .atPosition(position).perform(click());
    }

    private void clickUndo() {
        onView(withId(R.id.undoButton)).perform(click());
    }

    private void restartApplication() {
        deleteFileOnCreate = false;
        mActivityRule.relaunchActivity();
    }
    private void validateTaskList(ArrayList<Task> expectedTasks) {
        int numTasks = ((ListView) mActivityRule.getActivity().findViewById(R.id.listView)).getAdapter().getCount();
        assertEquals(expectedTasks.size(), numTasks);
        for (int i = 0; i< expectedTasks.size(); i++) {
            if (expectedTasks.get(i).isDone()) {
                onData(anything())
                        .inAdapterView(withId(R.id.listView)).atPosition(i)
                        .check(matches(withItemContent(expectedTasks.get(i).toString())))
                        .check(matches(containPainFlags(Paint.STRIKE_THRU_TEXT_FLAG)))
                        .check(matches(isChecked()));
            } else {
                onData(anything())
                        .inAdapterView(withId(R.id.listView)).atPosition(i)
                        .check(matches(withItemContent(expectedTasks.get(i).toString())))
                        .check(matches(notContainPainFlags(Paint.STRIKE_THRU_TEXT_FLAG)))
                        .check(matches(isNotChecked()));
            }
        }
    }

}
