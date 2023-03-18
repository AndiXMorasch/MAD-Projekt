package de.hsos.findyourdoc.activities;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import de.hsos.findyourdoc.R;

public class PDFActivity extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        //Uri uri = Uri.parse(getIntent().getStringExtra("uri"));
        Uri uri = getIntent().getParcelableExtra("uri");

        //Log.d("PDF PATH: ----- ", getIntent().getStringExtra("uri"));

        pdfView = findViewById(R.id.pdfActivityView);
        pdfView.fromUri(uri).load();
    }
}