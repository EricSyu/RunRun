package homework6.group.runrun;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLData;


public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static SQLiteDatabase database;
    private static String database_name = "RunDB";

    public DBHelper(Context context){
        super(context,database_name,null,VERSION);
    }

    /*function to call database*/
    public static SQLiteDatabase getDatabase(Context context){
        if(database == null || !database.isOpen()){
            database = new DBHelper(context).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE_TABLE_Run = "create table Run ( _ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,Date TEXT, Distance INTEGER, Time INTEGER, Speed INTEGER)";
        String DATABASE_CREATE_TABLE_Pace = "create table Pace ( _ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,Date TEXT, Pace INTEGER)";

        db.execSQL(DATABASE_CREATE_TABLE_Run);
        db.execSQL(DATABASE_CREATE_TABLE_Pace);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Run");
        db.execSQL("DROP TABLE IF EXISTS Pace");
        onCreate(db);
    }
}
