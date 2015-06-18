package eu.tjenwellens.vrijetijdsapp.storage.io;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.R;
import eu.tjenwellens.vrijetijdsapp.properties.Energy;
import eu.tjenwellens.vrijetijdsapp.properties.Location;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyType;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyTypeUnknownException;
import eu.tjenwellens.vrijetijdsapp.properties.Rating;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tjen
 */
public class FileIO {
    private static final Logger LOGGER = Logger.getLogger(FileIO.class.toString());
    private static final String IGNORE = "#";
    private static final String SPLITTER_PAIR = ";";
    private static final String SPLITTER_KEY_VALUE = "=";
    private static final String NAME = "act_name";
    private static final String DESCRIPTION = "act_desc";
    private static final String MANUAL = "act_manual";

    private static Map<String, String> parseKeyValue(String text) {
        Map<String, String> keyValuePairs = new HashMap<String, String>();
        String[] key_values = text.split(SPLITTER_PAIR);
        for (String string : key_values) {
            String[] key_value = string.split(SPLITTER_KEY_VALUE, 2);
            if (key_value.length < 2) {
                LOGGER.log(Level.SEVERE, "Faulty key-value pair, skipping: {0}", string);
                continue;
            }
            keyValuePairs.put(key_value[0], key_value[1]);
        }
        return keyValuePairs;
    }

    private static Activiteit convertMapToActiviteit(Map<String, String> keyValuePairs) {
        String actName = keyValuePairs.get(NAME);
        if (actName == null || actName.isEmpty()) {
            return null;
        }
        String actDesc = keyValuePairs.get(DESCRIPTION);
        actDesc = actDesc == null ? "" : actDesc;
        String actMan = keyValuePairs.get(MANUAL);
        actMan = actMan == null ? "" : actMan;
        Set<Property> props = new HashSet<Property>();
        for (PropertyType type : PropertyType.values()) {
            String value = keyValuePairs.get(type.name());
            Property p = null;
            if (value == null || value.isEmpty()) {
                continue;
            }
            try {
                switch (type) {
                    case ENERGY:
                        p = PropertyType.createEnergyProperty(Energy.valueOf(value));
                        break;
                    case LOCATION:
                        p = PropertyType.createLocationProperty(Location.valueOf(value));
                        break;
                    case PEOPLE:
                        p = PropertyType.createPeopleProperty(value);
                        break;
                    case PRICE:
                        p = PropertyType.createPriceProperty(value);
                        break;
                    case RATING:
                        p = PropertyType.createRatingProperty(Rating.valueOf(value));
                        break;
                    case TAGS:
                        p = PropertyType.createTagsProperty(value);
                        break;
                    case TIME:
                        p = PropertyType.createTimeProperty(value);
                        break;
                    default:
                        throw new PropertyTypeUnknownException(type);
                }
            } catch (PropertyTypeUnknownException e) {
                LOGGER.log(Level.SEVERE, "Exception with type=" + type + ", value=" + value, e);
                continue;
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.SEVERE, "Exception with type=" + type + ", value=" + value, e);
                continue;
            }
            if (p != null) {
                props.add(p);
            }
        }
        return new IOActiviteit(actName, actDesc, actMan, props);
    }

    private static Activiteit parseStringToActiviteit(String line) {
        return line.startsWith(IGNORE) ? null : convertMapToActiviteit(parseKeyValue(line));
    }

    private static String parseActiviteitToString(Activiteit a) {
        StringBuilder sb = new StringBuilder();
        sb.append(NAME).append(SPLITTER_KEY_VALUE).append(a.getName()).append(SPLITTER_PAIR);
        sb.append(DESCRIPTION).append(SPLITTER_KEY_VALUE).append(a.getDescription()).append(SPLITTER_PAIR);
        sb.append(MANUAL).append(SPLITTER_KEY_VALUE).append(a.getManual()).append(SPLITTER_PAIR);
        for (Property p : a.getProperties().values()) {
            sb.append(p.getType().name()).append(SPLITTER_KEY_VALUE).append(p.toString()).append(SPLITTER_PAIR);
        }
        return sb.toString();
    }

    public static boolean writeToFile(Context context, String filename, Collection<Activiteit> activiteiten) {
        if(!isExternalStorageWritable()){
            LOGGER.log(Level.WARNING, "Tried to write to external storage but it is not readable.");
            Toast.makeText(context, context.getString(R.string.external_storage_not_writeable), Toast.LENGTH_SHORT).show();
            return false;
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(getFile(context, filename));
            for (Activiteit activiteit : activiteiten) {
                out.println(parseActiviteitToString(activiteit));
            }
            out.close();
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "File not found.", e);
            Toast.makeText(context, context.getString(R.string.file_not_found) + filename, Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        Toast.makeText(context, context.getString(R.string.file_saved), Toast.LENGTH_SHORT).show();
        return true;
    }

    public static List<Activiteit> readFromFile(Context context, String filename) {
        if(!isExternalStorageReadable()){
            LOGGER.log(Level.WARNING, "Tried to read external storage but is not readable.");
            Toast.makeText(context, context.getString(R.string.external_storage_not_readable), Toast.LENGTH_SHORT).show();
            return null;
        }
        List<Activiteit> activiteiten = new ArrayList<Activiteit>();
        try {
            BufferedReader myReader = new BufferedReader(new FileReader(getFile(context, filename)));
            String line;
            while ((line = myReader.readLine()) != null) {
                activiteiten.add(parseStringToActiviteit(line));
            }
            activiteiten.remove(null);
            myReader.close();
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "File not found.", e);
            Toast.makeText(context, context.getString(R.string.file_not_found) + filename, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error reading from file.", e);
            Toast.makeText(context, context.getString(R.string.file_write_error) + filename, Toast.LENGTH_SHORT).show();
        }
        return activiteiten;
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private static File getFile(Context context, String filename) {
        return new File(context.getExternalFilesDir(null), filename);
    }
}
