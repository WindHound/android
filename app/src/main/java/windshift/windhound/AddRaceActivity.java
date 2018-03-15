package windshift.windhound;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddRaceActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private boolean editStart;
    private Button button_start_date;
    private Button button_start_time;
    private Button button_end_date;
    private Button button_end_time;
    private Date startDate;
    private Date endDate;
    private DateFormat dateFormat;
    private DateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_race);

        /* Toolbar Configuration */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Change back button colour
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorAccent),
                PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setTitle("Add Race");

        /* Start Date Field Configuration*/
        button_start_date = findViewById(R.id.button_start_date);
        dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy");
        startDate = Calendar.getInstance().getTime();
        button_start_date.setText(dateFormat.format(startDate));

        /* Start Time Field Configuration*/
        button_start_time = findViewById(R.id.button_start_time);
        timeFormat = new SimpleDateFormat("k:mm");
        button_start_time.setText(timeFormat.format(startDate));

        /* End Date Field Configuration */
        button_end_date = findViewById(R.id.button_end_date);
        endDate = Calendar.getInstance().getTime();
        button_end_date.setText(dateFormat.format(endDate));

        /* End Time Field Configuration */
        button_end_time = findViewById(R.id.button_end_time);
        button_end_time.setText(timeFormat.format(endDate));
    }

    // When the back button in the toolbar is pressed, return to the parent activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void editStartDate(View v) {
        editStart = true;
        showDatePicker(v);
    }

    public void editEndDate(View v) {
        editStart = false;
        showDatePicker(v);
    }

    public void showDatePicker(View v) {
        DialogFragment datePickerFragment = new DatePickerDialogFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (editStart) {
            startDate = new Date(year - 1900, month, day, startDate.getHours(),
                    startDate.getMinutes());
            button_start_date.setText(dateFormat.format(startDate));
        } else {
            endDate = new Date(year - 1900, month, day, endDate.getHours(),
                    endDate.getMinutes());
            button_end_date.setText(dateFormat.format(endDate));
        }
    }

    public void editStartTime(View v) {
        editStart = true;
        showTimePicker(v);
    }

    public void editEndTime(View v) {
        editStart = false;
        showTimePicker(v);
    }

    public void showTimePicker(View v) {
        DialogFragment timePickerFragment = new TimePickerDialogFragment();
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (editStart) {
            startDate = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDay(),
                    hourOfDay, minute);
            button_start_time.setText(timeFormat.format(startDate));
        } else {
            endDate = new Date(endDate.getYear(), endDate.getMonth(), endDate.getDay(), hourOfDay,
                    minute);
            button_end_time.setText(timeFormat.format(endDate));
        }
    }

}
