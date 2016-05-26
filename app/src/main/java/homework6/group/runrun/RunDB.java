package homework6.group.runrun;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by WEI-ZHE on 2016/5/25.
 */
public class RunDB {

    public static final String TABLE_NAME = "Run";

    public static final String KEY_ID = "_ID";
    public static final String Column_Date = "Date";
    public static final String Column_Distance = "Distance";
    public static final String Column_Time = "Time";
    public static final String Column_Speed = "Speed";

    private SQLiteDatabase db;

    public RunDB(Context context){
        db = DBHelper.getDatabase(context);
    }

    public void close(){
        db.close();
    }

    public RunData insert(RunData item){
        ContentValues cv = new ContentValues();

        cv.put(Column_Date, item.getDate());
        cv.put(Column_Distance, item.getDistance());
        cv.put(Column_Time, item.getTime());
        cv.put(Column_Speed, item.getSpeed());

        long id = db.insert(TABLE_NAME, null, cv);

        item.setId(id);

        return item;
    }

    public ArrayList<RunData> getAll() {
        ArrayList<RunData> result = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public RunData getRecord(Cursor cursor) {
        RunData result = new RunData();
        result.setId(cursor.getLong(0));
        result.setDate(cursor.getString(1));
        result.setDistance(cursor.getInt(2));
        result.setTime(cursor.getInt(3));
        result.setSpeed(cursor.getInt(4));
        return result;
    }

    public boolean delete(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(TABLE_NAME, where , null) > 0;
    }
}
