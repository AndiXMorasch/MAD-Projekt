package de.hsos.findyourdoc.storage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.hsos.findyourdoc.logic.DocModel;


/**
 * The DatabaseHelper class extends the SQLiteOpenHelper and implements the DatabaseInterface.
 * Purpose of this class is to be able to make and edit entries to the database
 * which is holding the doctor names with dates and their vector images as an int.
 *
 * @author Andreas Morasch
 */

public class DatabaseHelper extends SQLiteOpenHelper implements DatabaseInterface {

    // database name
    private static final String DATABASE_NAME = "doc_database";

    // doc table
    private static final String DOC_TABLE_NAME = "doc_table";
    private static final String DOCNAME_COL = "docname";
    private static final String IMAGE_COL = "image";
    private static final String DATE_COL = "date";
    private static final String TIME_COL = "time";
    private static final String REMIND_TIME_COL = "remind_time";
    private static final String WASNOTIFIED_COL = "notified";

    // pdf table
    private static final String PDF_TABLE_NAME = "pdf_table";
    private static final String PDF_ID_COL = "id";
    private static final String PDF_URI = "uri";

    /**
     * Constructor for the DatabaseHelper class.
     *
     * @param context Context needs to be given to let the DatabaseHelper know, where the method call was made from.
     */

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * At the start of the application this method will check whether a table (database file) already exists or not.
     * If not, creates one with the initialized static final class attributes.
     *
     * @param db SQLiteDatabase object in order to check the existence
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DOC_TABLE_NAME + " (" + DOCNAME_COL + " TEXT PRIMARY KEY, " +
                IMAGE_COL + " INTEGER, " + DATE_COL + " TEXT, " + TIME_COL + " TEXT, " + REMIND_TIME_COL + " INTEGER, " + WASNOTIFIED_COL + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PDF_TABLE_NAME + " (" + PDF_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DOCNAME_COL + " TEXT, " + PDF_URI + " TEXT)");
    }

    /**
     * This method is only called when the corresponding table (database file) already exists, but the stored version number is lower
     * than requested in the constructor. It will drop the current table and create a new one with the correct version.
     *
     * @param db         SQLiteDatabase object in order to create the new table
     * @param oldVersion old version number
     * @param newVersion new version number
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DOC_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PDF_TABLE_NAME);
        onCreate(db);
    }

    /**
     * This method adds data (docName, image and data) to the current SQLite database table.
     *
     * @param docModel DocModel to be stored
     * @return boolean true if the storage was successful and false if not
     */

    public boolean addDocData(DocModel docModel) {
        // Check if this doc already exists
        if (checkExistence(docModel.getDocName())) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOCNAME_COL, docModel.getDocName());
        contentValues.put(DATE_COL, docModel.getDate());
        contentValues.put(IMAGE_COL, docModel.getImage());
        contentValues.put(TIME_COL, docModel.getTime());
        contentValues.put(REMIND_TIME_COL, docModel.getRemindTime());
        if (docModel.wasNotified()) {
            contentValues.put(WASNOTIFIED_COL, 1);
        } else {
            contentValues.put(WASNOTIFIED_COL, 0);
        }

        long result = db.insert(DOC_TABLE_NAME, null, contentValues);
        return result != -1;
    }

    /**
     * This method adds data (docName and uriAsString) to the current SQLite database table.
     *
     * @param docName     Doctors name to be stored
     * @param uriAsString Uri of the local PDF file as a String
     * @return boolean true if the storage was successful and false if not
     */

    public boolean addPdfData(String docName, String uriAsString) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOCNAME_COL, docName);
        contentValues.put(PDF_URI, uriAsString);

        long result = db.insert(PDF_TABLE_NAME, null, contentValues);
        return result != -1;
    }

    /**
     * This method updates the doctors name based on the key attribute (oldDocName).
     *
     * @param oldDocName old doctor name to be found and replaced
     * @param newDocname new doctor name which replaces the old doctor name
     */

    @Override
    public boolean updateDocName(String oldDocName, String newDocname) {
        if (oldDocName.equals(newDocname)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + DOC_TABLE_NAME + " SET " + DOCNAME_COL + " = '" + newDocname + "'" +
                " WHERE " + DOCNAME_COL + " = '" + oldDocName + "'");
        return true;
    }

    /**
     * This method updates the corresponding date based on the key attribute (docName).
     *
     * @param docName doctor name as the key attribute to search for
     * @param date    new date to replace the old one
     */

    @Override
    public void updateDate(String docName, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + DOC_TABLE_NAME + " SET " + DATE_COL + " = '" + date + "'" +
                " WHERE " + DOCNAME_COL + " = '" + docName + "'");
    }

    /**
     * This method updates the corresponding time of the entry based on the key attribute (docName).
     *
     * @param docName doctor name as the key attribute to search for
     * @param time    new time to replace the old one
     */

    @Override
    public void updateTime(String docName, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + DOC_TABLE_NAME + " SET " + TIME_COL + " = '" + time + "'" +
                " WHERE " + DOCNAME_COL + " = '" + docName + "'");
    }

    /**
     * This method updates the corresponding remind time of the entry based on the key attribute (docName).
     *
     * @param docName       doctor name as the key attribute to search for
     * @param newRemindTime new remind time to replace the old one
     */

    @Override
    public void updateRemindTime(String docName, int newRemindTime) {
        if (getRemindTime(docName) == newRemindTime) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + DOC_TABLE_NAME + " SET " + REMIND_TIME_COL + " = '" + newRemindTime + "'" +
                " WHERE " + DOCNAME_COL + " = '" + docName + "'");
    }

    /**
     * This method removes one specific database entry based on the key attribute (docName).
     *
     * @param docName doctor name to search for and remove the specific entry
     */

    @Override
    public void removeOneDoc(String docName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DOC_TABLE_NAME, DOCNAME_COL + "= '" + docName + "'", null);
    }

    /**
     * This method removes one specific database entry based on the key attribute (id).
     *
     * @param id id to search for and remove the specific entry
     */

    @Override
    public void removeOnePDF(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PDF_TABLE_NAME, PDF_ID_COL + "= '" + id + "'", null);
    }

    /**
     * This method removes all database entries.
     */

    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DOC_TABLE_NAME);
        db.execSQL("DELETE FROM " + PDF_TABLE_NAME);
    }

    /**
     * This method searches for a specific date based on the key attribute (docName).
     *
     * @param docName doctor name to search for
     * @return specific date of this db entry
     */

    public String getDate(String docName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + DATE_COL + " FROM " + DOC_TABLE_NAME + " WHERE " + DOCNAME_COL + "="
                + "'" + docName + "'";
        @SuppressLint("Recycle") Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        return data.getString(0);
    }

    /**
     * This method searches for a specific boolean value "wasNotified" based on the key attribute (docName).
     * This boolean value gives information whether the user already have been informed about the pending
     * appointment or not. If true, this will prevent the PopUp-message which inform about the appointment
     * to be send all over again.
     *
     * @param docName doctor name to search for
     * @return specific notified-status (0 or 1) of this db entry
     */

    @Override
    public int getNotificationStatus(String docName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + WASNOTIFIED_COL + " FROM " + DOC_TABLE_NAME + " WHERE " + DOCNAME_COL + "="
                + "'" + docName + "'";
        @SuppressLint("Recycle") Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        return data.getInt(0);
    }

    /**
     * This method updates the corresponding notified-status of the entry based on the key attribute (docName).
     *
     * @param docName     doctor name as the key attribute to search for
     * @param wasNotified notified-status (0 or 1) to be updated
     */

    @Override
    public void updateNotificationStatus(String docName, int wasNotified) {
        if (getNotificationStatus(docName) == wasNotified) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + DOC_TABLE_NAME + " SET " + WASNOTIFIED_COL + " = '" + wasNotified + "'" +
                " WHERE " + DOCNAME_COL + " = '" + docName + "'");
    }

    /**
     * This method resets the corresponding notified-status of the entry to zero
     * based on the key attribute (docName).
     *
     * @param docName doctor name as the key attribute to search for
     */

    public void resetNotificationStatus(String docName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + DOC_TABLE_NAME + " SET " + WASNOTIFIED_COL + " = '" + "0" + "'" +
                " WHERE " + DOCNAME_COL + " = '" + docName + "'");
    }

    /**
     * This method searches for a specific time based on the key attribute (docName).
     *
     * @param docName doctor name to search for
     * @return specific time of this db entry
     */

    public String getTime(String docName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + TIME_COL + " FROM " + DOC_TABLE_NAME + " WHERE " + DOCNAME_COL + "="
                + "'" + docName + "'";
        @SuppressLint("Recycle") Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        return data.getString(0);
    }

    /**
     * This method searches for a specific remind time based on the key attribute (docName).
     *
     * @param docName doctor name to search for
     * @return specific remind time of this db entry
     */

    public int getRemindTime(String docName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + REMIND_TIME_COL + " FROM " + DOC_TABLE_NAME + " WHERE " + DOCNAME_COL + "="
                + "'" + docName + "'";
        @SuppressLint("Recycle") Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        return data.getInt(0);
    }

    /**
     * This method checks if there is an existing key attribute (docName) in the database.
     *
     * @param docName doctor name to search for
     * @return boolean true if doctor was found (exists), false if it was not found (not exists)
     */

    public boolean checkExistence(String docName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + DOCNAME_COL + " FROM " + DOC_TABLE_NAME + " WHERE " + DOCNAME_COL + "="
                + "'" + docName + "'";
        @SuppressLint("Recycle") Cursor data = db.rawQuery(query, null);
        return data.getCount() != 0;
    }

    /**
     * This method will log all entries of both tables in the Logcat window.
     */

    @Override
    public void logAllEntries() {
        Cursor docTableData = getDataCursorDocTable();
        while (docTableData.moveToNext()) {
            Log.d("DOC_TABLE: ", docTableData.getString(0) + " || " + docTableData.getString(1)
                    + " || " + docTableData.getInt(2) + " || " + docTableData.getString(3) + " || " +
                    docTableData.getInt(4));
        }

        Cursor pdfTableData = getDataCursorPdfTable();
        while (pdfTableData.moveToNext()) {
            Log.d("PDF_TABLE: ", pdfTableData.getInt(0) + " || " + pdfTableData.getString(1)
                    + " || " + pdfTableData.getInt(2));
        }
    }

    /**
     * This method will drop the current table if exists and create a new (empty) one.
     */

    @Override
    public void dropCurrentTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DOC_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PDF_TABLE_NAME);
        onCreate(db);
    }

    /**
     * This method returns a data cursor concerning the current database.
     * It can be used for iterations and to log data for example, since you
     * basically have a snapshot of the whole database in one variable.
     *
     * @return current tables data cursor (Doc Table)
     */

    @SuppressLint("Recycle")
    public Cursor getDataCursorDocTable() {
        String query = "SELECT * FROM " + DOC_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        if (db != null) {
            data = db.rawQuery(query, null);
        }
        return data;
    }

    /**
     * This method returns a data cursor concerning the current database.
     * It can be used for iterations and to log data for example, since you
     * basically have a snapshot of the whole database in one variable.
     *
     * @return current tables data cursor (PDF Table)
     */

    public Cursor getDataCursorPdfTable() {
        String query = "SELECT * FROM " + PDF_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        if (db != null) {
            data = db.rawQuery(query, null);
        }
        return data;
    }

    /**
     * This method returns a data cursor concerning the current database based
     * on the given parameter docName.
     *
     * @return all entries found from the given parameter docName
     */

    public Cursor getDataCursorPdfTableFromDoc(String docName) {
        String query = "SELECT * FROM " + PDF_TABLE_NAME + " WHERE " + DOCNAME_COL + "="
                + "'" + docName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        if (db != null) {
            data = db.rawQuery(query, null);
        }
        return data;
    }

    /**
     * This method returns the number of elements found based on the given parameter docName.
     * It can be used for counting purposes.
     *
     * @return number of elements in the PDF table found based on the given parameter docName
     */

    public int countPDFEntriesToDocname(String docName) {
        String query = "SELECT * FROM " + PDF_TABLE_NAME + " WHERE " + DOCNAME_COL + "="
                + "'" + docName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        if (db != null) {
            data = db.rawQuery(query, null);
        }
        assert data != null;
        int count = data.getCount();
        data.close();
        return count;
    }
}
