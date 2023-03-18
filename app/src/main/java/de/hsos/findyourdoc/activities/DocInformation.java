package de.hsos.findyourdoc.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hsos.findyourdoc.R;
import de.hsos.findyourdoc.fragments.CalendarFragment;
import de.hsos.findyourdoc.fragments.TimeFragment;
import de.hsos.findyourdoc.logic.FileModel;
import de.hsos.findyourdoc.logic.FileModelRecyclerViewAdapter;
import de.hsos.findyourdoc.logic.ReminderBroadcast;
import de.hsos.findyourdoc.storage.DatabaseHelper;
import de.hsos.findyourdoc.storage.SharedPreferencesEnum;

public class DocInformation extends AppCompatActivity {

    private String docName;
    private String username;
    private TextView nothingToShowPDFTextView;
    private List<FileModel> fileModelList;
    private RecyclerView recyclerView;
    private FileModelRecyclerViewAdapter.PDFRecyclerViewClickListener onClickListener;
    private FileModelRecyclerViewAdapter.PDFRecyclerViewLongClickListener onLongClickListener;
    private DatabaseHelper databaseHelper;
    private TextView dateTextView;
    private TextView timeTextView;
    private Button saveChangesButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String initDate;
    private String initTime;
    private String date;
    private String time;
    private String[] hourTypes;
    private int remindTimeInMillis;
    private final Calendar calendar = Calendar.getInstance();
    private AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_information);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            docName = extras.getString("docName");
        }

        this.dateTextView = findViewById(R.id.dateTextViewDocInformation);
        this.timeTextView = findViewById(R.id.timeTextViewDocInformation);
        ImageView editDateButton = findViewById(R.id.editDateButton);
        ImageView editTimeButton = findViewById(R.id.editTimeButton);
        EditText docNameEditText = findViewById(R.id.docNameTextInputFieldEditTextDocInformation);
        docNameEditText.setText(docName);
        this.recyclerView = findViewById(R.id.pdfRecyclerView);
        this.nothingToShowPDFTextView = findViewById(R.id.nothingToShowPDFTextView);
        this.databaseHelper = new DatabaseHelper(DocInformation.this);
        this.fileModelList = new ArrayList<>();
        this.saveChangesButton = findViewById(R.id.saveChangesButton);
        this.sharedPreferences = getSharedPreferences(String.valueOf(SharedPreferencesEnum.SHARED_PREFERENCES_ID), MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        this.date = this.databaseHelper.getDate(this.docName);
        this.time = this.databaseHelper.getTime(this.docName);
        this.initDate = date;
        this.initTime = time;
        this.remindTimeInMillis = this.databaseHelper.getRemindTime(this.docName);
        this.hourTypes = getResources().getStringArray(R.array.hour_types);
        this.autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        this.username = sharedPreferences.getString(String.valueOf(SharedPreferencesEnum.USERNAME), "Dummy");

        setUpDropDownMenuText();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, hourTypes);
        this.autoCompleteTextView.setAdapter(arrayAdapter);

        setUpTextViews();
        setUpFileModelList();
        setUpAdapter();

        if (welcomeAlertSharedPref()) {
            showDocInformationAlert();
        }

        ActivityResultLauncher<String> startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        databaseHelper.addPdfData(docName, uri.toString());
                        setUpFileModelList();
                        setUpAdapter();
                        Log.d("URI PATH: ", uri.toString());
                    }
                });

        docNameEditText.setOnFocusChangeListener((view, isFocused) -> {
            if (isFocused) {
                saveChangesButton.setEnabled(true);
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                saveChangesButton.setEnabled(true);
            }
        });

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR);
        final int min = calendar.get(Calendar.MINUTE);

        editDateButton.setOnClickListener(view -> {
            DatePickerDialog dpDialog = new DatePickerDialog
                    (DocInformation.this, (datePicker, newYear, newMonth, newDayOfMonth) -> {
                        date = CalendarFragment.dateStringBuilder(newDayOfMonth, newMonth, newYear);
                        setUpTextViews();
                        if (!date.equals(initDate)) {
                            saveChangesButton.setEnabled(true);
                        }
                    }, year, month, dayOfMonth);
            dpDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            dpDialog.show();
        });

        editTimeButton.setOnClickListener(view -> {
            TimePickerDialog tpDialog = new TimePickerDialog
                    (DocInformation.this, (timePicker, newHour, newMin) -> {
                        time = TimeFragment.timeStringBuilder(newHour, newMin);
                        setUpTextViews();
                        if (!time.equals(initTime)) {
                            saveChangesButton.setEnabled(true);
                        }
                    }, hour, min, true);
            tpDialog.show();
        });

        Button uploadPDFButton = findViewById(R.id.upload_PDF_Button);
        uploadPDFButton.setOnClickListener(view -> startActivityIntent.launch("application/pdf"));

        this.saveChangesButton.setOnClickListener(view -> {
            if ((databaseHelper.checkExistence(docNameEditText.getText().toString()) && !docName.equals(docNameEditText.getText().toString()))
                    || docNameEditText.getText().toString().isEmpty()
                    || docNameEditText.getText().toString().isBlank()) {
                Toast.makeText(DocInformation.this, getString(R.string.cannotSaveName), Toast.LENGTH_LONG).show();
                return;
            } else if (docNameEditText.getText().toString().length() > 20) {
                Toast.makeText(DocInformation.this, getString(R.string.numberOfCharsExceeded), Toast.LENGTH_LONG).show();
                return;
            } else {
                databaseHelper.updateDocName(docName, docNameEditText.getText().toString());
                docName = docNameEditText.getText().toString();
            }

            if (!date.equals(initDate) || !time.equals(initTime)) {
                databaseHelper.resetNotificationStatus(docName);
            }
            databaseHelper.updateRemindTime(docName,
                    getTimeInMillisFromDropdownMenu(autoCompleteTextView.getText().toString()));
            databaseHelper.updateDate(docName, date);
            initDate = date;
            databaseHelper.updateTime(docName, time);
            initTime = time;
            ReminderBroadcast.createNotificationChannel(this);
            ReminderBroadcast.setUpAlarms(databaseHelper, this, username);
            Toast.makeText(DocInformation.this, getString(R.string.changes_saved), Toast.LENGTH_LONG).show();
        });
    }

    private void showDocInformationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_checkbox, null);
        CheckBox mCheckBox = view.findViewById(R.id.dialogCheckBox);
        builder.setView(view)
                .setIcon(R.drawable.hand_point_to_top)
                .setCancelable(true)
                .setTitle(getString(R.string.information_alert_title))
                .setMessage(getString(R.string.information_alert_text))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mCheckBox.isChecked()) {
                            setDoNotShowInformationAlertAgain();
                        }
                        dialogInterface.cancel();
                    }
                }).show();
    }

    private void setDoNotShowInformationAlertAgain() {
        this.editor.putBoolean(String.valueOf(SharedPreferencesEnum.WELCOME_SCREEN_DOC_INFORMATION), false);
        this.editor.apply();
    }

    private boolean welcomeAlertSharedPref() {
        return this.sharedPreferences.getBoolean(String.valueOf(SharedPreferencesEnum.WELCOME_SCREEN_DOC_INFORMATION), true);
    }

    private void setUpDropDownMenuText() {
        int remindTime = databaseHelper.getRemindTime(docName);
        if (remindTime == 1800000) {
            autoCompleteTextView.setText(getString(R.string.thirty_minutes));
        } else if (remindTime == 3600000) {
            autoCompleteTextView.setText(getString(R.string.one_hour));
        } else if (remindTime == 7200000) {
            autoCompleteTextView.setText(getString(R.string.two_hours));
        } else {
            autoCompleteTextView.setText(getString(R.string.one_day));
        }
    }

    private void setUpTextViews() {
        String date = getString(R.string.appointment_date) + " " + this.date;
        this.dateTextView.setText(date);

        String time = getString(R.string.appointment_time) + " " + this.time;
        this.timeTextView.setText(time);

        String nothingUploaded = getString(R.string.no_documents_uploaded);
        this.nothingToShowPDFTextView.setText(nothingUploaded);
    }

    private void setUpFileModelList() {
        this.fileModelList.clear();
        Cursor cursor = this.databaseHelper.getDataCursorPdfTableFromDoc(this.docName);
        int amountOfElements = this.databaseHelper.countPDFEntriesToDocname(this.docName);

        if (amountOfElements == 0) {
            this.nothingToShowPDFTextView.setVisibility(View.VISIBLE);
        } else {
            this.nothingToShowPDFTextView.setVisibility(View.GONE);
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String docName = cursor.getString(1);
            String uri = cursor.getString(2);
            Log.d("SetUp FileList: ", docName + " || " + uri);
            this.fileModelList.add(new FileModel(id, R.drawable.pdf_icon, docName, uri));
        }
    }

    private void setUpAdapter() {
        setOnClickListener();
        setLongClickListener();
        FileModelRecyclerViewAdapter adapter = new FileModelRecyclerViewAdapter
                (this, this.fileModelList, this.onClickListener, this.onLongClickListener);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        this.onClickListener = (v, position) -> {
            Intent intent = new Intent(DocInformation.this, PDFActivity.class);
            Uri uri = Uri.parse(fileModelList.get(position).getUri());
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("uri", uri);
            this.startActivity(intent);
        };
    }

    private void setLongClickListener() {
        this.onLongClickListener = (v, position) -> deleteEntryDialog(position);
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

    private void deleteEntryDialog(int pos) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_entry)).setMessage(getString(R.string.delete_entry_warning_text))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> deleteEntry(pos))
                .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> dialogInterface.cancel()).show();
    }

    private void deleteEntry(int pos) {
        int id = this.fileModelList.get(pos).getId();
        this.databaseHelper.removeOnePDF(id);

        if (fileModelList.isEmpty()) {
            this.nothingToShowPDFTextView.setText(R.string.no_documents_uploaded);
        }

        setUpFileModelList();
        setUpAdapter();
    }
}