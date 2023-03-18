package de.hsos.findyourdoc.logic;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import de.hsos.findyourdoc.R;

public class DocModel extends AppCompatActivity {
    private String docName;
    private String date;
    private int image;
    private String time;
    private int remindTime;

    private boolean wasNotified;

    public DocModel(String docName, int image, String date, String time, int remindTime, boolean wasNotified) {
        this.docName = docName;
        this.date = date;
        this.image = image;
        this.time = time;
        this.remindTime = remindTime;
        this.wasNotified = wasNotified;
    }

    public String getDocName() {
        return docName;
    }

    public String getDate() {
        return date;
    }

    public int getImage() {
        return image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(int remindTime) {
        this.remindTime = remindTime;
    }

    public boolean wasNotified() {
        return wasNotified;
    }

    public void setWasNotified(boolean wasNotified) {
        this.wasNotified = wasNotified;
    }

    public static int getImageId(String docName, Context context) {
        int id;
        if (docName.equals(getStringByLocal((Activity) context, R.string.general_doc, "en"))
                || docName.equals(getStringByLocal((Activity) context, R.string.general_doc, "de"))) {
            id = R.drawable.general_doc_icon;
        } else if (docName.equals(getStringByLocal((Activity) context, R.string.physio_doc, "en"))
                || docName.equals(getStringByLocal((Activity) context, R.string.physio_doc, "de"))) {
            id = R.drawable.physio_icon;
        } else if (docName.equals(getStringByLocal((Activity) context, R.string.dentist_doc, "en"))
                || docName.equals(getStringByLocal((Activity) context, R.string.dentist_doc, "de"))) {
            id = R.drawable.tooth_icon;
        } else if (docName.equals(getStringByLocal((Activity) context, R.string.neuro_doc, "en"))
                || docName.equals(getStringByLocal((Activity) context, R.string.neuro_doc, "de"))) {
            id = R.drawable.brain_icon;
        } else if (docName.equals(getStringByLocal((Activity) context, R.string.eye_doc, "en"))
                || docName.equals(getStringByLocal((Activity) context, R.string.eye_doc, "de"))) {
            id = R.drawable.eye_icon;
        } else if (docName.equals(getStringByLocal((Activity) context, R.string.skin_doc, "en"))
                || docName.equals(getStringByLocal((Activity) context, R.string.skin_doc, "de"))) {
            id = R.drawable.skin_icon;
        } else {
            id = R.drawable.default_doc_icon;
        }
        return id;
    }

    @NonNull
    private static String getStringByLocal(Activity context, int id, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return context.createConfigurationContext(configuration).getResources().getString(id);
    }
}
