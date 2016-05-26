package homework6.group.runrun;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by WEI-ZHE on 2016/5/25.
 */
public class PaceDB {

    public static final String TABLE_NAME = "Pace";

    public static final String KEY_ID = "_ID";
    public static final String Column_Date = "Date";
    public static final String Column_Pace = "Pace";

    private SQLiteDatabase db;

    public PaceDB(Context context){
        db = DBHelper.getDatabase(context);
    }

    public void close(){
        db.close();
    }

    public PaceData insert(PaceData item){
        ContentValues cv = new ContentValues();

        cv.put(Column_Date, item.getDate());
        cv.put(Column_Pace, item.getPace());

        long id = db.insert(TABLE_NAME, null, cv);

        item.setId(id);

        return item;
    }

    public ArrayList<PaceData> getAll() {
        ArrayList<PaceData> result = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public PaceData getRecord(Cursor cursor) {
        PaceData result = new PaceData();
        result.setId(cursor.getLong(0));
        result.setDate(cursor.getString(1));
        result.setPace(cursor.getInt(2));
        return result;
    }

    public boolean delete(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    public void modify(PaceData item){

        ContentValues values = new ContentValues();
        values.put(Column_Date, item.getDate());
        values.put(Column_Pace, item.getPace());

        db.update(TABLE_NAME, values, "_ID=" + item.getId() , null);
    }

    public PaceData queryDate(String date){
        Cursor cursor =db.query(TABLE_NAME, null, "Date='" + date +"'", null, null, null, null, null);
        if(cursor.getCount() > 0){
            cursor.moveToNext();
            return getRecord(cursor);
        }
        return null;
    }
}
