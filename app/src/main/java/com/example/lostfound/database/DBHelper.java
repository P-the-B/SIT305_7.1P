package com.example.lostfound.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lostfound.model.LostItem;

import java.util.ArrayList;
import java.util.List;

// SQLite database handler
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "lostfound.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE = "items";

    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title"; // NEW
    public static final String COL_TYPE = "type";
    public static final String COL_NAME = "name";
    public static final String COL_PHONE = "phone";
    public static final String COL_DESC = "description";
    public static final String COL_DATE = "date";
    public static final String COL_LOCATION = "location";
    public static final String COL_IMAGE = "image_uri";
    public static final String COL_TIMESTAMP = "timestamp";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_TYPE + " TEXT, "
                + COL_NAME + " TEXT, "
                + COL_PHONE + " TEXT, "
                + COL_DESC + " TEXT, "
                + COL_DATE + " TEXT, "
                + COL_LOCATION + " TEXT, "
                + COL_IMAGE + " TEXT, "
                + COL_TIMESTAMP + " TEXT"
                + ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long insertItem(LostItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put(COL_TITLE, item.getTitle());
        v.put(COL_TYPE, item.getType());
        v.put(COL_NAME, item.getName());
        v.put(COL_PHONE, item.getPhone());
        v.put(COL_DESC, item.getDescription());
        v.put(COL_DATE, item.getDate());
        v.put(COL_LOCATION, item.getLocation());
        v.put(COL_IMAGE, item.getImageUri());
        v.put(COL_TIMESTAMP, item.getTimestamp());

        return db.insert(TABLE, null, v);
    }

    public List<LostItem> getAllItems() {

        List<LostItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(TABLE, null, null, null, null, null, COL_ID + " DESC");

        if (c != null) {
            while (c.moveToNext()) {

                LostItem item = new LostItem();

                item.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
                item.setTitle(c.getString(c.getColumnIndexOrThrow(COL_TITLE)));
                item.setType(c.getString(c.getColumnIndexOrThrow(COL_TYPE)));
                item.setName(c.getString(c.getColumnIndexOrThrow(COL_NAME)));
                item.setPhone(c.getString(c.getColumnIndexOrThrow(COL_PHONE)));
                item.setDescription(c.getString(c.getColumnIndexOrThrow(COL_DESC)));
                item.setDate(c.getString(c.getColumnIndexOrThrow(COL_DATE)));
                item.setLocation(c.getString(c.getColumnIndexOrThrow(COL_LOCATION)));
                item.setImageUri(c.getString(c.getColumnIndexOrThrow(COL_IMAGE)));
                item.setTimestamp(c.getString(c.getColumnIndexOrThrow(COL_TIMESTAMP)));

                list.add(item);
            }
            c.close();
        }

        return list;
    }

    public int deleteItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
}