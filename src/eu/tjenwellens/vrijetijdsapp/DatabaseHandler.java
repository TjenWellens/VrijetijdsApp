package eu.tjenwellens.vrijetijdsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import eu.tjenwellens.vrijetijdsapp.filters.Filter;
import eu.tjenwellens.vrijetijdsapp.filters.FilterFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DatabaseHandler extends SQLiteOpenHelper {
    // singleton
    private static DatabaseHandler dbh;
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "vrijetijdsapp";
    private static final String TABLE_ACTIVITEITEN = "activiteiten";
    private static final String KEY_ACTIVITEIT_ID = "activiteit_id";
    private static final String KEY_ACTIVITEIT_NAME = "activiteit_name";
    private static final String KEY_ACTIVITEIT_DESCRIPTION = "activiteit_description";
    private static final String KEY_ACTIVITEIT_MANUAL = "activiteit_manual";
    private static final String TABLE_FILTERS = "filters";
    private static final String KEY_FILTER_ID = "filter_id";
    private static final String KEY_FILTER_TYPE = "filter_type";
    private static final String KEY_FILTER_NAME = "filter_name";
    private static final String KEY_FILTER_VALUE = "filter_value";
    // Create tables
    private static final String CREATE_ACTIVITEITEN_TABLE = "CREATE TABLE " + TABLE_ACTIVITEITEN + "("
            + KEY_ACTIVITEIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ACTIVITEIT_NAME + " TEXT,"
            + KEY_ACTIVITEIT_DESCRIPTION + " TEXT,"
            + KEY_ACTIVITEIT_MANUAL + " TEXT)";
    private static final String CREATE_FILTERS_TABLE = "CREATE TABLE " + TABLE_FILTERS + "("
            + KEY_FILTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ACTIVITEIT_ID + " INTEGER,"
            + KEY_FILTER_TYPE + " TEXT,"
            + KEY_FILTER_NAME + " TEXT,"
            + KEY_FILTER_VALUE + " TEXT)";

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHandler getInstance(Context context) {
        if (dbh == null) {
            dbh = new DatabaseHandler(context);
        }
        return dbh;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACTIVITEITEN_TABLE);
        db.execSQL(CREATE_FILTERS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITEITEN);
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTERS);

        // Create tables again
        onCreate(db);
    }

    public long addActiviteit(Activiteit activiteit) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            return addActiveit(db, activiteit);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public Set<Long> addActiviteiten(Collection<Activiteit> activiteiten) {
        SQLiteDatabase db = null;
        Set<Long> ids = new HashSet<Long>();
        try {
            db = this.getWritableDatabase();
            for (Activiteit activiteit : activiteiten) {
                ids.add(addActiveit(db, activiteit));
            }
            return ids;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="adding activiteiten">
    private long addActiveit(SQLiteDatabase db, Activiteit activiteit) {
        long activiteitId = addActiviteitValues(db, activiteit);
        if (activiteitId < 0) {
            return activiteitId;
        }
        addFilters(db, activiteit.getFilters().values(), activiteitId);
        return activiteitId;
    }

    private long addActiviteitValues(SQLiteDatabase db, Activiteit activiteit) {
        ContentValues values = new ContentValues();
        // id is automatically created
        values.put(KEY_ACTIVITEIT_NAME, activiteit.getName());
        values.put(KEY_ACTIVITEIT_DESCRIPTION, activiteit.getDescription());
        values.put(KEY_ACTIVITEIT_MANUAL, activiteit.getManual());

        // Inserting Row
        return db.insert(TABLE_ACTIVITEITEN, null, values);
    }

    private boolean addFilters(SQLiteDatabase db, Collection<Filter> filters, long activiteitId) {
        boolean success = true;
        for (Filter filter : filters) {
            success = addFilter(db, filter, activiteitId) >= 0 && success;
        }
        return success;
    }

    private long addFilter(SQLiteDatabase db, Filter filter, long activiteitId) {
        ContentValues values = new ContentValues();
        // id is automatically created
        values.put(KEY_ACTIVITEIT_ID, activiteitId);
        values.put(KEY_FILTER_TYPE, filter.getType().name());
        values.put(KEY_FILTER_NAME, filter.getName());
        values.put(KEY_FILTER_VALUE, filter.getValue());

        // Inserting Row
        return db.insert(TABLE_ACTIVITEITEN, null, values);
    }
    //</editor-fold>

    public List<Activiteit> getAllActiviteiten() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            return getAllActiviteiten(db);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private List<Activiteit> getAllActiviteiten(SQLiteDatabase db) {
        List<Activiteit> activiteiten = new LinkedList<Activiteit>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITEITEN;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                String manual = cursor.getString(3);
                List<Filter> filters = getFilters(db, id);
                // add
                activiteiten.add(new Activiteit(name, description, manual, filters));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return activiteiten;
    }

    private List<Filter> getFilters(SQLiteDatabase db, long activiteitId) {
        List<Filter> filters = new LinkedList<Filter>();
        Cursor cursor = db.query(TABLE_FILTERS, null, KEY_ACTIVITEIT_ID + " = " + activiteitId, null, null, null, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                int id = Integer.parseInt(cursor.getString(0));
//                int activiteitId = Integer.parseInt(cursor.getString(1));
                String type = cursor.getString(2);
                String name = cursor.getString(3);
                String value = cursor.getString(4);
                Filter filter = FilterFactory.createFilter(name, value, type);
                filters.add(filter);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return filters;
    }

    void tset(SQLiteDatabase db) {
        String grp = "";
        int activiteitId = 0;
        db.rawQuery("SELECT * FROM " + TABLE_FILTERS + " WHERE " + KEY_ACTIVITEIT_ID + " = " + grp, null);
        db.rawQuery("SELECT * FROM " + TABLE_FILTERS + " WHERE " + KEY_ACTIVITEIT_ID + " = ?", new String[]{grp});
        db.query(TABLE_FILTERS, null, KEY_ACTIVITEIT_ID + " = " + activiteitId, null, null, null, null);
    }
//    // Updating single contact
//    public int updateActiviteit(ActiviteitI activiteit) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_ID, activiteit.getActiviteitId());        // id
//        values.put(KEY_KAL_NAME, activiteit.getKalenderName()); // kalender
//        values.put(KEY_TITLE, activiteit.getActiviteitTitle());        // title
//        values.put(KEY_START_MILLIS, activiteit.getBeginMillis()); // begin
//        values.put(KEY_STOP_MILLIS, activiteit.getEndMillis()); // end
//        values.put(KEY_DETAILS, activiteit.getDescription()); // description
//        int result = db.update(TABLE_ACTIVITEITEN, values, KEY_ID + " = ?",
//                new String[]{
//                    String.valueOf(activiteit.getActiviteitId())
//                });
//        db.close();
//        // updating row
//        return result;
//    }
//
//    // Deleting single contact
//    public void deleteActiviteit(ActiviteitI activiteit) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_ACTIVITEITEN, KEY_ID + " = ?",
//                new String[]{
//                    String.valueOf(activiteit.getActiviteitId())
//                });
//        db.close();
//    }
//
//    // Getting contacts Count
//    public int getActiviteitenCount() {
//        String countQuery = "SELECT  * FROM " + TABLE_ACTIVITEITEN;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        int count = cursor.getCount();
//        cursor.close();
//
//        // return count
//        return count;
//    }
//
//    public void clearActiviteiten() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_ACTIVITEITEN, null, null);
//        db.close();
//    }
}