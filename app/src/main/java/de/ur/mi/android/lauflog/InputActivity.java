package de.ur.mi.android.lauflog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class InputActivity extends AppCompatActivity {

    public static final String DATE_KEY = "DATE_KEY";
    public static final String DISTANCE_KEY = "DISTANCE_KEY";
    public static final String MINUTES_KEY = "MINUTES_KEY";
    public static final String SECONDS_KEY = "SECONDS_KEY";

    private DatePicker dateInput;
    private EditText distanceInput;
    private EditText minutesInput;
    private EditText secondsInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_input);
        dateInput = findViewById(R.id.date_input_picker);
        distanceInput = findViewById(R.id.distance_input_text);
        minutesInput = findViewById(R.id.time_minutes_input_text);
        secondsInput = findViewById(R.id.time_seconds_input_text);
        Button saveButton = findViewById(R.id.save_entry_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnInputDataToCallingActivity();
            }
        });
    }

    private void returnInputDataToCallingActivity() {
        Date selectedDate = getValueFromDatePicker(dateInput);
        float distanceEntered = getValueFromDistanceInput(distanceInput);
        int minutesEntered = getValueFromTimeInput(minutesInput);
        int secondsEntered = getValueFromTimeInput(secondsInput);
        Intent inputData = new Intent();
        inputData.putExtra(DATE_KEY, selectedDate.getTime());
        inputData.putExtra(DISTANCE_KEY, distanceEntered);
        inputData.putExtra(MINUTES_KEY, minutesEntered);
        inputData.putExtra(SECONDS_KEY, secondsEntered);
        setResult(MainActivity.RESULT_CODE_FOR_VALID_LOG_ENTRY, inputData);
        finish();
    }

    private Date getValueFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    private float getValueFromDistanceInput(EditText input) {
        if (input.getText() == null) {
            return 0;
        }
        return Float.parseFloat(input.getText().toString());
    }

    private int getValueFromTimeInput(EditText input) {
        if (input.getText() == null) {
            return 0;
        }
        return Integer.parseInt(input.getText().toString());
    }


}
