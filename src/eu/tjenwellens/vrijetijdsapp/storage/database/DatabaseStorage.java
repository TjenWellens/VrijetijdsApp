package eu.tjenwellens.vrijetijdsapp.storage.database;

import android.content.Context;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.storage.StorageStrategy;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tjen
 */
public class DatabaseStorage implements StorageStrategy {
    private DatabaseHandler dbh;

    public DatabaseStorage(Context context) {
        this.dbh = new DatabaseHandler(context);
    }

    public Activiteit createActiviteit(String name, String description, String manual, Set<Property> properties) {
        DatabaseActiviteit da = new DatabaseActiviteit(name, description, manual, properties);
        dbh.addActiviteit(da);
        return da;
    }

    public Activiteit getActiviteit(String name) {
        return dbh.getActiviteitByName(name);
    }

    public List<Activiteit> getAllActiviteiten() {
        return dbh.getAllActiviteiten();
    }
}
