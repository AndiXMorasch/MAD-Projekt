package de.hsos.findyourdoc.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import de.hsos.findyourdoc.logic.DocModel;

/**
 * The database interface for the DatabaseHelper class.
 * More information about the methods can be found in the DatabaseHelper class.
 *
 * @author Andreas Morasch
 * @see DatabaseHelper
 */

public interface DatabaseInterface {

    Cursor getDataCursorDocTable();

    Cursor getDataCursorPdfTable();

    void onCreate(SQLiteDatabase sqLiteDatabase);

    void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1);

    boolean addDocData(DocModel docModel);

    boolean addPdfData(String docName, String uriAsString);

    String getDate(String docName);

    int getNotificationStatus(String docName);

    void updateNotificationStatus(String docName, int wasNotified);

    String getTime(String docName);

    int getRemindTime(String docName);

    boolean updateDocName(String oldDocName, String newDocname);

    void updateDate(String docName, String date);

    void updateTime(String docName, String time);

    void updateRemindTime(String docName, int newRemindTime);

    boolean checkExistence(String website);

    void removeOneDoc(String docName);

    void removeOnePDF(int id);

    void removeAll();

    void logAllEntries();

    void dropCurrentTable();

    Cursor getDataCursorPdfTableFromDoc(String docName);

    int countPDFEntriesToDocname(String docName);
}
