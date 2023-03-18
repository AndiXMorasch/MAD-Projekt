package de.hsos.findyourdoc.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.text.DateFormat;
import java.util.Calendar;

import de.hsos.findyourdoc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private TextView dateTextView;
    private String docName;
    private String date;
    private final Calendar calendar = Calendar.getInstance();

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.docName = getArguments().getString("docname");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        this.dateTextView = view.findViewById(R.id.dateTextViewDocCreation);
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.setMinDate(calendar.getTimeInMillis());
        initializeDate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(this::dateChanged);
        }

        Button nextFragmentButton = view.findViewById(R.id.buttonGoToTimeFragment);
        nextFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                bundle.putString("docname", docName);
                Navigation.findNavController(view).navigate(R.id.action_calendarFragment_to_timeFragment, bundle);
            }
        });

        return view;
    }

    private void initializeDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        this.dateTextView.setText(formattedDate);
        this.date = dateStringBuilder(calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }

    private void dateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        this.dateTextView.setText(formattedDate);
        this.date = dateStringBuilder(dayOfMonth, month, year);
    }

    public static String dateStringBuilder(int day, int month, int year) {
        String date;
        month++;
        if (day < 10) {
            date = "0" + day;
        } else {
            date = String.valueOf(day);
        }

        if (month < 10) {
            date += ".0" + month;
        } else {
            date += "." + month;
        }

        date += "." + year;
        return date;
    }

    public static int[] extractDataFromDate(String date) {
        String[] datePartsString = date.split("\\.");
        int[] datePartsInt = new int[3];
        datePartsInt[0] = Integer.parseInt(datePartsString[0]);
        datePartsInt[1] = Integer.parseInt(datePartsString[1]);
        datePartsInt[2] = Integer.parseInt(datePartsString[2]);
        return datePartsInt;
    }
}