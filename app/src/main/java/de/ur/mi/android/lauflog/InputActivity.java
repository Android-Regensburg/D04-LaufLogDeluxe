package de.ur.mi.android.lauflog;

import android.app.Activity;
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

import de.ur.mi.android.lauflog.request.RequestConfig;


/**
 * Activity zum Einlesen der einzelnen Bestandteile eines Eintrags für das LaufLog. Die Eingaben
 * der NutzerInnen werden als Result an die aufrufende Activity zurück gegeben.
 */
public class InputActivity extends AppCompatActivity {

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

    /**
     * Liest die aktuellen Inhalte der Eingabefelder aus, speichert diese in einem Intent und setzt
     * dieses als Ergebnis (Result) dieser Activity. Anschießend wird die Activity über den Aufruf
     * der finish-Methode beendet.
     */
    private void returnInputDataToCallingActivity() {
        Date selectedDate = getValueFromDatePicker(dateInput);
        float distanceEntered = getValueFromDistanceInput(distanceInput);
        int minutesEntered = getValueFromTimeInput(minutesInput);
        int secondsEntered = getValueFromTimeInput(secondsInput);
        Intent inputData = new Intent();
        inputData.putExtra(RequestConfig.DATE_KEY, selectedDate.getTime());
        inputData.putExtra(RequestConfig.DISTANCE_KEY, distanceEntered);
        inputData.putExtra(RequestConfig.MINUTES_KEY, minutesEntered);
        inputData.putExtra(RequestConfig.SECONDS_KEY, secondsEntered);
        setResult(Activity.RESULT_OK, inputData);
        finish();
    }

    /**
     * Erzeugt ein Date-Objekt aus den aktuell im DatePicker eingetragenen Werten für Tag, Monat
     * und Jahr.
     */
    private Date getValueFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    /**
     * Gibt den Wert des Eingabefelds (für die Distanz) als float-Wert zurück und fängt dabei den
     * Fall ab, dass keine Eingabe im Feld erfolgt ist.
     */
    private float getValueFromDistanceInput(EditText input) {
        if (input.getText() == null) {
            return 0;
        }
        return Float.parseFloat(input.getText().toString());
    }

    /**
     * Gibt den Wert des Eingabefelds (für die Minunten oder Sekunden) als int-Wert zurück und
     * fängt dabei den Fall ab, dass keine Eingabe im jeweiligen Feld erfolgt ist.
     */
    private int getValueFromTimeInput(EditText input) {
        if (input.getText() == null) {
            return 0;
        }
        return Integer.parseInt(input.getText().toString());
    }


}
