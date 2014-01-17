package eu.tjenwellens.vrijetijdsapp.storage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.properties.*;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 5;
    // Database Name
    private static final String DATABASE_NAME = "vrijetijdsapp";
    private static final String TABLE_ACTIVITEITEN = "activiteiten";
    private static final String KEY_ACTIVITEIT_ID = "act_id";
    private static final String KEY_ACTIVITEIT_NAME = "act_name";
    private static final String KEY_ACTIVITEIT_DESCRIPTION = "act_description";
    private static final String KEY_ACTIVITEIT_MANUAL = "act_manual";
    private static final String KEY_PROPERTY_ID = "prop_id";
    private static final String KEY_PROPERTY_VALUE = "prop_value";
    private static final String KEY_PROPERTY_MIN = "prop_min";
    private static final String KEY_PROPERTY_MAX = "prop_max";
    private static final String KEY_PROPERTY_RATING = "prop_rating";
    private static final Map<PropertyType, String> PROPERTY_TABLES = new EnumMap<PropertyType, String>(PropertyType.class);
    // Create tables
    private static final String CREATE_ACTIVITEITEN_TABLE = "CREATE TABLE " + TABLE_ACTIVITEITEN + "("
            + KEY_ACTIVITEIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ACTIVITEIT_NAME + " TEXT,"
            + KEY_ACTIVITEIT_DESCRIPTION + " TEXT,"
            + KEY_ACTIVITEIT_MANUAL + " TEXT)";

    private void createPropertyTable(SQLiteDatabase db, PropertyType type) {
        Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, "DB create table: " + PROPERTY_TABLES.get(type));
        StringBuilder create = new StringBuilder("CREATE TABLE ");
        create.append(PROPERTY_TABLES.get(type));
        create.append("(");
        create.append(KEY_PROPERTY_ID);
        create.append(" INTEGER PRIMARY KEY AUTOINCREMENT");
        create.append(',');
        create.append(KEY_ACTIVITEIT_ID);
        create.append(", INTEGER");
        switch (type) {
            case ENERGY:
            case LOCATION:
            case TAGS:
                create.append(',').append(KEY_PROPERTY_VALUE).append(" TEXT");
                break;
            case TIME:
            case PEOPLE:
            case PRICE:
                create.append(',').append(KEY_PROPERTY_MIN).append(" INTEGER");
                create.append(',').append(KEY_PROPERTY_MAX).append(" INTEGER");
                break;
            case RATING:
                create.append(',').append(KEY_PROPERTY_RATING).append(" INTEGER");
                break;
            default:
                throw new PropertyTypeUnknowmException(type);
        }
        create.append(')');
        db.execSQL(create.toString());
    }

    DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        fillvarPROPERTY_TABLES();
    }

    private void fillvarPROPERTY_TABLES() {
        for (PropertyType propertyType : PropertyType.values()) {
            PROPERTY_TABLES.put(propertyType, "property_" + propertyType.name());
        }
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACTIVITEITEN_TABLE);
        for (PropertyType propertyType : PROPERTY_TABLES.keySet()) {
            createPropertyTable(db, propertyType);
        }
    }

    private void logTables(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex("name"));
                Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, "DB table: " + name);
            } while (c.moveToNext());
        } else {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, "DB has no tables!!!!!");
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, "DB has no tables!!!!!");
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, "DB has no tables!!!!!");
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, "DB has no tables!!!!!");
        }
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITEITEN);
        for (String table : PROPERTY_TABLES.values()) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
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
        addProperties(db, activiteit.getProperties().values(), activiteitId);
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

    private boolean addProperties(SQLiteDatabase db, Collection<Property> properties, long activiteitId) {
        boolean success = true;
        for (Property property : properties) {
            success = addProperty(db, property, activiteitId) >= 0 && success;
        }
        return success;
    }

    private long addProperty(SQLiteDatabase db, Property property, long activiteitId) {
        PropertyType type = property.getType();
        String table = PROPERTY_TABLES.get(type);
        ContentValues values = new ContentValues();
        values.put(KEY_ACTIVITEIT_ID, activiteitId);
//        values.put(KEY_PROPERTY_TYPE, type.name());
        switch (type) {
            case ENERGY:
                break;
            case LOCATION:
            case TIME:
                values.put(KEY_PROPERTY_VALUE, ((SingleValueProperty) property).getValue());
                break;
            case PEOPLE:
            case PRICE:
                values.put(KEY_PROPERTY_MIN, ((MinMaxProperty) property).getMin());
                values.put(KEY_PROPERTY_MAX, ((MinMaxProperty) property).getMax());
                break;
            case RATING:
                values.put(KEY_PROPERTY_RATING, ((MinMaxProperty) property).getMin());
                break;
            case TAGS:
                for (String value : ((MultiValueProperty) property).getValues()) {
                    values.put(KEY_PROPERTY_VALUE, value);
                    db.insert(table, null, values);
                    values.remove(KEY_PROPERTY_VALUE);
                }
                return 0;
            default:
                throw new PropertyTypeUnknowmException(type);
        }
        // Inserting Row
        return db.insert(table, null, values);
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
                List<Property> properties = getProperties(db, id);
                // add
                //TODO
                activiteiten.add(new DatabaseActiviteit(name, description, manual, properties));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return activiteiten;
    }

    private Property getProperty(SQLiteDatabase db, long activiteitId, PropertyType type) {
        Cursor cursor = db.query(PROPERTY_TABLES.get(type), null, KEY_ACTIVITEIT_ID + " = " + activiteitId, null, null, null, null);
        Property property = null;
        if (cursor.moveToFirst()) {
//            long id = cursor.getLong(0);
            switch (type) {
                case ENERGY:
                    property = PropertyFactory.createEnergyProperty(cursor.getString(1));
                    break;
                case LOCATION:
                    property = PropertyFactory.createLocationProperty(cursor.getString(1));
                    break;
                case TIME:
                    property = PropertyFactory.createTimeProperty(cursor.getInt(1), cursor.getInt(2));
                    break;
                case PEOPLE:
                    property = PropertyFactory.createPeopleProperty(cursor.getInt(1), cursor.getInt(2));
                    break;
                case PRICE:
                    property = PropertyFactory.createPriceProperty(cursor.getInt(1), cursor.getInt(2));
                    break;
                case RATING:
                    property = PropertyFactory.createRatingProperty(cursor.getInt(1));
                    break;
                case TAGS:
                    LinkedList<String> values = new LinkedList<String>();
                    do {
                        values.add(cursor.getString(1));
                    } while (cursor.moveToNext());
                    property = PropertyFactory.createTagsProperty(values);
                    break;
                default:
                    throw new PropertyTypeUnknowmException(type);
            }
        }
        cursor.close();
        return property;
    }

    private List<Property> getProperties(SQLiteDatabase db, long activiteitId) {
        List<Property> properties = new LinkedList<Property>();
        for (PropertyType propertyType : PROPERTY_TABLES.keySet()) {
            Property p = getProperty(db, activiteitId, propertyType);
            if (p != null) {
                properties.add(p);
            }
        }
        return properties;
    }

    public Activiteit getActiviteitByName(String name) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            return getActiviteitByName(db, name);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private Activiteit getActiviteitByName(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(TABLE_ACTIVITEITEN, null, KEY_ACTIVITEIT_NAME + " = ?", new String[]{name}, null, null, null);
        if (!cursor.moveToFirst()) {
            return null;
        }
        long id = cursor.getLong(0);
//        name=cursor.getString(1);
        String description = cursor.getString(2);
        String manual = cursor.getString(3);
        cursor.close();
        return new DatabaseActiviteit(name, description, manual, getProperties(db, id));
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
