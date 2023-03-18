package de.hsos.findyourdoc.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import de.hsos.findyourdoc.R;

public class DocCreation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_creation);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.toolbar_doc_creation_text));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}