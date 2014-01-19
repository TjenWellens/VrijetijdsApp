package eu.tjenwellens.vrijetijdsapp.storage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.properties.*;
import java.util.Arrays;
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
    private boolean newDatabase = false;
    // Database Version
    private static final int DATABASE_VERSION = 11;
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
            + KEY_ACTIVITEIT_NAME + " TEXT UNIQUE,"
            + KEY_ACTIVITEIT_DESCRIPTION + " TEXT,"
            + KEY_ACTIVITEIT_MANUAL + " TEXT)";

    private void createDebugEntries(SQLiteDatabase db) {
        List<Activiteit> acts = new LinkedList<Activiteit>();
        linkActProp(acts, "Actief", PropertyType.createEnergyProperty("actief"));
        linkActProp(acts, "Rustig", PropertyType.createEnergyProperty("rustig"));

        linkActProp(acts, "Binnen", PropertyType.createLocationProperty("binnen"));
        linkActProp(acts, "Buiten", PropertyType.createLocationProperty("buiten"));

        linkActProp(acts, "Price 5-10", PropertyType.createPriceProperty(5, 10));
        linkActProp(acts, "Price 10-100", PropertyType.createPriceProperty(10, 100));
        linkActProp(acts, "Price 0-50", PropertyType.createPriceProperty(0, 50));

        linkActProp(acts, "People 5-10", PropertyType.createPeopleProperty(5, 10));
        linkActProp(acts, "People 10-100", PropertyType.createPeopleProperty(10, 100));
        linkActProp(acts, "People 0-50", PropertyType.createPeopleProperty(0, 50));

        linkActProp(acts, "Time 5-10", PropertyType.createTimeProperty(5, 10));
        linkActProp(acts, "Time 10-100", PropertyType.createTimeProperty(10, 100));
        linkActProp(acts, "Time 0-50", PropertyType.createTimeProperty(0, 50));

        linkActProp(acts, "Rating -10", PropertyType.createRatingProperty(Rating.FUN));
        linkActProp(acts, "Rating -5", PropertyType.createRatingProperty(Rating.NOT_FUN));
        linkActProp(acts, "Rating 5", PropertyType.createRatingProperty(Rating.NOT_TRY));
        linkActProp(acts, "Rating 10", PropertyType.createRatingProperty(Rating.TRY));

        Set<String> tags = new HashSet<String>();
        tags.add("a");
        linkActProp(acts, "Tags a", PropertyType.createTagsProperty(tags));
        tags.add("b");
        linkActProp(acts, "Tags a,b", PropertyType.createTagsProperty(tags));
        tags.add("c");
        linkActProp(acts, "Tags a,b,c", PropertyType.createTagsProperty(tags));
        for (Activiteit activiteit : acts) {
            addActiveit(db, activiteit);
        }
    }

    private void linkActProp(List<Activiteit> acts, String name, Property prop) {
        Collection<Property> ps = new LinkedList<Property>();
        ps.add(prop);
        acts.add(new DatabaseActiviteit(name, null, null, ps));
    }

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
                create.append(',').append(KEY_PROPERTY_RATING).append(" TEXT");
                break;
            default:
                throw new PropertyTypeUnknownException(type);
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

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (newDatabase) {
            newDatabase = false;
            createDebugEntries(db);
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
        newDatabase = true;
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

    public Set<Long> addAllActiviteiten(Collection<Activiteit> activiteiten) {
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
            case LOCATION:
                values.put(KEY_PROPERTY_VALUE, ((SingleValueProperty) property).getValue());
                break;
            case TIME:
            case PEOPLE:
            case PRICE:
                values.put(KEY_PROPERTY_MIN, ((MinMaxProperty) property).getMin());
                values.put(KEY_PROPERTY_MAX, ((MinMaxProperty) property).getMax());
                break;
            case RATING:
                values.put(KEY_PROPERTY_RATING, ((RatingProperty) property).getRating().name());
                break;
            case TAGS:
                for (String value : ((MultiValueProperty) property).getValues()) {
                    values.put(KEY_PROPERTY_VALUE, value);
                    db.insert(table, null, values);
                    values.remove(KEY_PROPERTY_VALUE);
                }
                return 0;
            default:
                throw new PropertyTypeUnknownException(type);
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
                    property = PropertyType.createEnergyProperty(cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_VALUE)));
                    break;
                case LOCATION:
                    property = PropertyType.createLocationProperty(cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_VALUE)));
                    break;
                case TIME:
                    property = PropertyType.createTimeProperty(cursor.getInt(cursor.getColumnIndex(KEY_PROPERTY_MIN)), cursor.getInt(cursor.getColumnIndex(KEY_PROPERTY_MAX)));
                    break;
                case PEOPLE:
                    property = PropertyType.createPeopleProperty(cursor.getInt(cursor.getColumnIndex(KEY_PROPERTY_MIN)), cursor.getInt(cursor.getColumnIndex(KEY_PROPERTY_MAX)));
                    break;
                case PRICE:
                    property = PropertyType.createPriceProperty(cursor.getInt(cursor.getColumnIndex(KEY_PROPERTY_MIN)), cursor.getInt(cursor.getColumnIndex(KEY_PROPERTY_MAX)));
                    break;
                case RATING:
                    property = PropertyType.createRatingProperty(Rating.valueOf(cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_RATING))));
                    break;
                case TAGS:
                    LinkedList<String> values = new LinkedList<String>();
                    do {
                        values.add(cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_VALUE)));
                    } while (cursor.moveToNext());
                    property = PropertyType.createTagsProperty(values);
                    break;
                default:
                    throw new PropertyTypeUnknownException(type);
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
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String NOT = " NOT ";

    public Set<Activiteit> filterActiviteiten(Set<Filter> filters) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            return getActiviteitenById(db, filterPropertiesIds(db, filters));
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private Set<Activiteit> getActiviteitenById(SQLiteDatabase db, Set<Long> ids) {
        Set<Activiteit> activiteiten = new HashSet<Activiteit>();
        for (Long id : ids) {
            activiteiten.add(getActiviteitById(db, id));
        }
        return activiteiten;
    }

    private Activiteit getActiviteitById(SQLiteDatabase db, Long id) {
        Cursor cursor = db.query(TABLE_ACTIVITEITEN, null, KEY_ACTIVITEIT_ID + " = " + id, null, null, null, null);
        if (!cursor.moveToFirst()) {
            return null;
        }
//        long id = cursor.getLong(0);
        String name = cursor.getString(1);
        String description = cursor.getString(2);
        String manual = cursor.getString(3);
        cursor.close();
        return new DatabaseActiviteit(name, description, manual, getProperties(db, id));
    }

    private Set<Long> filterPropertiesIds(SQLiteDatabase db, Set<Filter> filters) {
        Set<Long> ids = getAllIds(db);
        for (Filter filter : filters) {
            ids.removeAll(filterPropertyIds(db, filter, true));
        }
        return ids;
    }

    private Set<Long> getAllIds(SQLiteDatabase db) {
        Set<Long> ids = new HashSet<Long>();
        Cursor c = db.query(TABLE_ACTIVITEITEN, new String[]{KEY_ACTIVITEIT_ID}, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                ids.add(c.getLong(0));
            } while (c.moveToNext());
        }
        c.close();
        return ids;
    }

    private Set<Long> filterPropertyIds(SQLiteDatabase db, Filter filter, boolean reverse) {
        Set<Long> ids = new HashSet<Long>();
        StringBuilder selection = new StringBuilder();
        if (reverse) {
            selection.append(NOT);
        }
        selection.append('(');
        String[] selectionArgs = null;
        switch (filter.getType()) {
            case ENERGY:
                String energy = filter.getValue();
                selection.append(KEY_PROPERTY_VALUE).append(" = ?");
                selectionArgs = new String[]{energy};
                break;
            case LOCATION:
                String location = filter.getValue();
                selection.append(KEY_PROPERTY_VALUE).append(" = ?");
                selectionArgs = new String[]{location};
                break;
            case TIME:
                int time = Integer.parseInt(filter.getValue());
                selection.append(KEY_PROPERTY_MIN).append(" < ").append(time).append(AND).append(time).append(" < ").append(KEY_PROPERTY_MAX);
                break;
            case PEOPLE:
                int ppl = Integer.parseInt(filter.getValue());
                selection.append(KEY_PROPERTY_MIN).append(" < ").append(ppl).append(AND).append(ppl).append(" < ").append(KEY_PROPERTY_MAX);
                break;
            case PRICE:
                int price = Integer.parseInt(filter.getValue());
                selection.append(KEY_PROPERTY_MIN).append(" < ").append(price);
//                // price > min
//                selection.append(AND).append(price).append(" < ").append(KEY_PROPERTY_MAX);
                break;
            case RATING:
                int rating = Integer.parseInt(filter.getValue());
                selection.append(KEY_PROPERTY_RATING).append(" >= ").append(rating);
                break;
            case TAGS:
                // TODO: tags werken niet tegoei, zit het probleem hier?
                /*
                 * act: a
                 * act: a|b
                 * act: a|b|c
                 * search a -> a            correct
                 * search b -> null         instead of 2
                 * search c -> null         instead of 1
                 * search a,b -> 2: a a,b   instead of 3
                 * search a,b,b -> 3        instead of 3
                 */
                String tags = filter.getValue();
                selectionArgs = tags.split(Filter.SPLITTER);
                boolean first = true;
                for (String tag : Arrays.asList(selectionArgs)) {
                    if (first) {
                        first = false;
                    } else {
                        selection.append(OR);
                    }
                    selection.append(KEY_PROPERTY_VALUE).append(" = ?");
                }
                break;
            default:
                throw new PropertyTypeUnknownException(filter.getType());
        }
        selection.append(')');
        Cursor c = db.query(PROPERTY_TABLES.get(filter.getType()), new String[]{KEY_ACTIVITEIT_ID}, selection.toString(), selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            do {
                ids.add(c.getLong(0));
            } while (c.moveToNext());
        }
        c.close();
        return ids;
    }

    public Activiteit updateActiviteit(Activiteit oldActiviteit, String newName, String newDescription, String newManual, Set<Property> newProperties) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            return updateActiviteit(db, oldActiviteit, newName, newDescription, newManual, newProperties);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private long getActiviteitId(SQLiteDatabase db, String name) {
        long id = -1;
        Cursor c = db.query(TABLE_ACTIVITEITEN, new String[]{KEY_ACTIVITEIT_ID}, KEY_ACTIVITEIT_NAME + " = ?", new String[]{name}, null, null, null);
        if (c.moveToFirst()) {
            id = c.getLong(0);
        }
        c.close();
        return id;
    }

    private long updateActiviteitValues(SQLiteDatabase db, Activiteit oldActiviteit, String newName, String newDescription, String newManual) {
        long id = getActiviteitId(db, oldActiviteit.getName());
        ContentValues values = new ContentValues();
        if (differ(oldActiviteit.getName(), newName)) {
            values.put(KEY_ACTIVITEIT_NAME, newName);
        }
        if (differ(oldActiviteit.getDescription(), newDescription)) {
            values.put(KEY_ACTIVITEIT_DESCRIPTION, newDescription);
        }
        if (differ(oldActiviteit.getManual(), newManual)) {
            values.put(KEY_ACTIVITEIT_MANUAL, newManual);
        }
        db.update(TABLE_ACTIVITEITEN, values, KEY_ACTIVITEIT_ID + " = " + id, null);
        return id;
    }

    private void updateProperties(SQLiteDatabase db, long activiteitId, Collection<Property> oldProps, Collection<Property> newProps) {
        removeProperties(db, oldProps, activiteitId);
        addProperties(db, newProps, activiteitId);
    }

    private Activiteit updateActiviteit(SQLiteDatabase db, Activiteit oldActiviteit, String newName, String newDescription, String newManual, Set<Property> newProperties) {
        // activiteit
        long activiteitId = updateActiviteitValues(db, oldActiviteit, newName, newDescription, newManual);
        // properties
        updateProperties(db, activiteitId, oldActiviteit.getProperties().values(), newProperties);
        return getActiviteitById(db, activiteitId);
    }

    private int removeProperties(SQLiteDatabase db, Collection<Property> properties, long activiteitId) {
        int rows = 0;
        for (Property property : properties) {
            rows += removeProperty(db, property, activiteitId);
        }
        return rows;
    }

    private int removeProperty(SQLiteDatabase db, Property property, long activiteitId) {
        return db.delete(PROPERTY_TABLES.get(property.getType()), KEY_ACTIVITEIT_ID + " = " + activiteitId, null);
    }

    private boolean differ(String a, String b) {
        if (a == null && b == null) {
            return false;
        } else if (a == null || b == null) {
            return true;
        } else {
            return a.equals(b);
        }
    }

    public boolean removeActiviteit(Activiteit oldActiviteit) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            return removeActiviteit(db, oldActiviteit);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private boolean removeActiviteit(SQLiteDatabase db, Activiteit oldActiviteit) {
        long id = getActiviteitId(db, oldActiviteit.getName());
        if (!removeActiviteitValues(db, id)) {
            return false;
        }
        removeProperties(db, oldActiviteit.getProperties().values(), id);
        return true;
    }

    private boolean removeActiviteitValues(SQLiteDatabase db, long id) {
        return db.delete(TABLE_ACTIVITEITEN, KEY_ACTIVITEIT_ID + " = " + id, null) > 0;
    }
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
