package de.hsos.findyourdoc.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import de.hsos.findyourdoc.R;
import de.hsos.findyourdoc.activities.MainMenu;
import de.hsos.findyourdoc.logic.DocModel;
import de.hsos.findyourdoc.storage.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private String docname;
    private String date;
    private TimePicker timePicker;
    private TextView timeTextView;
    private String time;
    private String[] hourTypes;
    private AutoCompleteTextView autoCompleteTextView;

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.docname = getArguments().getString("docname");
            this.date = getArguments().getString("date");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time, container, false);

        this.timeTextView = view.findViewById(R.id.timeTextViewDocCreation);
        this.timePicker = view.findViewById(R.id.timePicker);
        this.timePicker.setOnTimeChangedListener(this::timeChanged);
        this.databaseHelper = new DatabaseHelper(getContext());
        this.autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        this.hourTypes = getResources().getStringArray(R.array.hour_types);
        autoCompleteTextView.setText(getString(R.string.one_hour));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, hourTypes);
        this.autoCompleteTextView.setAdapter(arrayAdapter);
        initializeTime();

        Button finishCreationButton = view.findViewById(R.id.buttonFinishCreation);
        finishCreationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishCreation();
            }
        });

        return view;
    }

    private void initializeTime() {
        int hour = timePicker.getHour();
        int min = timePicker.getMinute();
        String time = timeStringBuilder(hour, min);
        this.time = time;
        this.timeTextView.setText(time);
    }

    private void timeChanged(TimePicker timePicker, int i, int i1) {
        int hour = timePicker.getHour();
        int min = timePicker.getMinute();
        String time = timeStringBuilder(hour, min);
        this.time = time;
        this.timeTextView.setText(time);
    }

    public static String timeStringBuilder(int hour, int min) {
        String time;
        if (hour < 10) {
            time = "0" + hour;
        } else {
            time = String.valueOf(hour);
        }

        if (min < 10) {
            time += ":0" + min;
        } else {
            time += ":" + min;
        }

        /*
        if (hour >= 12) {
            time += " pm";
        } else {
            time += " am";
        }
         */
        return time;
    }

    private int getTimeInMillisFromDropdownMenu(String dropDownText) {
        int millis;

        if (dropDownText.equals(getString(R.string.thirty_minutes))) {
            millis = 1800000;
        } else if (dropDownText.equals(getString(R.string.one_hour))) {
            millis = 3600000;
        } else if (dropDownText.equals(getString(R.string.two_hours))) {
            millis = 7200000;
        } else {
            millis = 86400000;
        }
        return millis;
    }

    public static int[] extractDataFromTime(String time) {
        time = time.substring(0, Math.min(time.length(), 5));
        String[] timePartsString = time.split(":");
        int[] timePartsInt = new int[2];
        timePartsInt[0] = Integer.parseInt(timePartsString[0]);
        timePartsInt[1] = Integer.parseInt(timePartsString[1]);
        return timePartsInt;
    }

    private void finishCreation() {
        int imageId = DocModel.getImageId(this.docname, this.requireContext());
        int remindTime = getTimeInMillisFromDropdownMenu(autoCompleteTextView.getText().toString());
        
        DocModel docModel = new DocModel(this.docname, imageId, this.date, this.time, remindTime, false);

        boolean dataAdded = this.databaseHelper.addDocData(docModel);

        if (dataAdded) {
            Toast.makeText(getContext(), getString(R.string.docAdded), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(getContext(), MainMenu.class);
        startActivity(intent);
    }
}