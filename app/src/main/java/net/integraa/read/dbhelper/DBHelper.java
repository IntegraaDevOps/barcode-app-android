package net.integraa.read.dbhelper;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "barcodeDB.db";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ERROR = "error";
    public static final String COLUMN_STACK = "stack";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(
                "create table error " +
                        "(id integer primary key, date text,error text,stack text)"

        );
        sqLiteDatabase.execSQL(
                "create table settings " +
                        "(id integer primary key, " +
                        "type text," +
                        "enabled text)"

        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS error");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS settings");
        onCreate(sqLiteDatabase);
    }

    public void updateSettings(String type,String enabled){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("UPDATE settings SET enabled = '"+enabled+"' WHERE type = '"+type+"' ");
        }catch (Exception e){
            Log.e("ErrorSQL",e.getMessage());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void insertSettings (String type, String enabled) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO settings " +
                    "(type,enabled) " +
                    "VALUES ('"+type+"','"+enabled+"');");
        }catch (Exception e){
            Log.e("ErrorSQL",e.getMessage());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void insertError (String data, String errore, String stack) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO error " +
                    "(date,error,stack) " +
                    "VALUES ('"+data+"','"+errore+"','"+stack+"');");
        }catch (Exception e){
            Log.e("ErrorSQL",e.getMessage());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Cursor getSettings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from settings order by enabled desc", null );
        return res;
    }

    public Cursor getSettingsEnabled() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from settings where enabled = 1 order by enabled desc", null );
        return res;
    }


    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from error order by id desc", null );
        return res;
    }

    public Cursor getDataInfo(String s) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from error where id="+s+"", null );
        return res;
    }

    public void removeAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM error");
        }catch (Exception e){
            Log.e("ErrorSQL",e.getMessage());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void exportDB(Context mContext, String dbName,String packageName) {
        try {

            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {

                File bkp_directory = new File(sd+"/bkp_database_intergraa/");
                if ( !bkp_directory.exists() ){
                    bkp_directory.mkdir();
                }

                String currentDBPath = "//data//"+packageName+"//databases//"+dbName+"";
                String backupDBPath = "export_"+dbName;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(bkp_directory, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(mContext,"Esportazione completata",Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            AlertDialog.Builder info = new AlertDialog.Builder(mContext,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            info.setTitle("Attenzione");
            info.setMessage("Si è verificato un problema durante la fase di esportazione\n"+e.getMessage());
            info.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            info.setCancelable(false);
            info.show();
        }
    }
}
