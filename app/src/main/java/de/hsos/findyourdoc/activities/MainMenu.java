package de.hsos.findyourdoc.activities;

import static de.hsos.findyourdoc.logic.DocModel.getImageId;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hsos.findyourdoc.R;
import de.hsos.findyourdoc.logic.DocModel;
import de.hsos.findyourdoc.logic.DocModelRecyclerViewAdapter;
import de.hsos.findyourdoc.logic.ReminderBroadcast;
import de.hsos.findyourdoc.storage.DatabaseHelper;
import de.hsos.findyourdoc.storage.SharedPreferencesEnum;

public class MainMenu extends AppCompatActivity {

    private String username;
    private List<DocModel> docModelList;
    private DocModelRecyclerViewAdapter.DocRecyclerViewClickListener onClickListener;
    private DocModelRecyclerViewAdapter.DocRecyclerViewLongClickListener longClickListener;
    private RecyclerView recyclerView;
    private TextView nothingToShowTextView;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        this.databaseHelper = new DatabaseHelper(MainMenu.this);
        this.docModelList = new ArrayList<>();
        this.nothingToShowTextView = findViewById(R.id.nothingToShowDocTextView);
        this.recyclerView = findViewById(R.id.docRecyclerView);
        this.sharedPreferences = getSharedPreferences(String.valueOf(SharedPreferencesEnum.SHARED_PREFERENCES_ID), MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        this.username = sharedPreferences.getString(String.valueOf(SharedPreferencesEnum.USERNAME), "Dummy");

        // Comment for simulate opening the app for the first time
        // this.databaseHelper.dropCurrentTable();
        // this.databaseHelper.removeAll();

        if (loadInitialSettingSharedPref() && !applyInitialSettingsSharedPref()) {
            createDefaultDBEntries();
        }

        setUpDocTypeList();
        setUpAdapter();
        ReminderBroadcast.createNotificationChannel(this);
        ReminderBroadcast.setUpAlarms(databaseHelper, this, username);

        Button addNewDocButton = (Button) findViewById(R.id.addNewDocButton);
        addNewDocButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainMenu.this, DocCreation.class);
            startActivity(intent);
        });

        if (welcomeAlertSharedPref()) {
            showWelcomeAlert();
        }
    }

    private void createDefaultDBEntries() {
        String[] docTypeList = getResources().getStringArray(R.array.doc_types);
        @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        for (String docName : docTypeList) {
            int image = getImageId(docName, this);
            this.databaseHelper.addDocData(new DocModel(docName, image, date, "18:30", 3600000, true));
        }
        setInitialSettingsApplied();
    }

    private boolean loadInitialSettingSharedPref() {
        return this.sharedPreferences.getBoolean(String.valueOf(SharedPreferencesEnum.SET_DEFAULT_DOCTORS), false);
    }

    private boolean applyInitialSettingsSharedPref() {
        return this.sharedPreferences.getBoolean(String.valueOf(SharedPreferencesEnum.DEFAULT_DOCTORS_APPLIED), false);
    }

    private boolean welcomeAlertSharedPref() {
        return this.sharedPreferences.getBoolean(String.valueOf(SharedPreferencesEnum.WELCOME_SCREEN_MAIN_MENU), true);
    }

    private void showWelcomeAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_checkbox, null);
        CheckBox mCheckBox = view.findViewById(R.id.dialogCheckBox);
        builder.setView(view)
                .setIcon(R.drawable.hand_point_right_icon)
                .setCancelable(true)
                .setTitle(getString(R.string.main_menu_alert))
                .setMessage(getString(R.string.hello) + ", " + username + "! " + getString(R.string.main_menu_alert_text))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mCheckBox.isChecked()) {
                            setDoNotShowMainMenuAlertAgain();
                        }
                        dialogInterface.cancel();
                    }
                }).show();
    }

    private void setInitialSettingsApplied() {
        this.editor.putBoolean(String.valueOf(SharedPreferencesEnum.DEFAULT_DOCTORS_APPLIED), true);
        this.editor.apply();
    }

    private void setDoNotShowMainMenuAlertAgain() {
        this.editor.putBoolean(String.valueOf(SharedPreferencesEnum.WELCOME_SCREEN_MAIN_MENU), false);
        this.editor.apply();
    }

    private void setUpAdapter() {
        setOnClickListener();
        setLongClickListener();
        DocModelRecyclerViewAdapter adapter = new DocModelRecyclerViewAdapter(this, this.docModelList, this.onClickListener, this.longClickListener);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        this.onClickListener = new DocModelRecyclerViewAdapter.DocRecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(MainMenu.this.getApplicationContext(), DocInformation.class);
                intent.putExtra("docName", docModelList.get(position).getDocName());
                MainMenu.this.startActivity(intent);
            }
        };
    }

    private void setLongClickListener() {
        this.longClickListener = new DocModelRecyclerViewAdapter.DocRecyclerViewLongClickListener() {
            @Override
            public void onLongClick(View v, int position) {
                deleteEntryDialog(position);
            }
        };
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
        String docname = this.docModelList.get(pos).getDocName();
        this.databaseHelper.removeOneDoc(docname);
        this.docModelList.clear();
        setUpDocTypeList();
        setUpAdapter();
    }

    private void setUpDocTypeList() {
        Cursor cursor = this.databaseHelper.getDataCursorDocTable();
        while (cursor.moveToNext()) {
            String docName = cursor.getString(0);
            int image = cursor.getInt(1);
            String date = cursor.getString(2);
            String time = cursor.getString(3);
            int remindTime = cursor.getInt(4);
            boolean wasNotified;
            if (cursor.getInt(5) == 1) {
                wasNotified = true;
            } else {
                wasNotified = false;
            }
            Log.d("SetUp DocTypeList: ", docName + " || " + image + " || " + date +
                    " || " + time + " || " + remindTime);
            this.docModelList.add(new DocModel(docName, image, date, time, remindTime, wasNotified));
        }

        if (cursor.getCount() == 0) {
            this.nothingToShowTextView.setText(getString(R.string.no_entries_message));
        }
    }
}