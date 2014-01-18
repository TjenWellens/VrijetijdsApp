package eu.tjenwellens.vrijetijdsapp.storage.database;

import android.content.Context;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.properties.Filter;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.storage.StorageStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tjen
 */
public class DatabaseStorage implements StorageStrategy {
    private DatabaseHandler dbh;
    List<Activiteit> selection = null;

    public DatabaseStorage(Context context) {
        this.dbh = new DatabaseHandler(context);
    }

    public Activiteit createActiviteit(String name, String description, String manual, Set<Property> properties) {
        DatabaseActiviteit da = new DatabaseActiviteit(name, description, manual, properties);
        if (dbh.addActiviteit(da) >= 0) {
            return da;
        } else {
            return null;
        }
    }

    public Activiteit getActiviteit(String name) {
        return dbh.getActiviteitByName(name);
    }

    public List<Activiteit> getAllActiviteiten() {
        return dbh.getAllActiviteiten();
    }

    public List<Activiteit> getLatestSelection() {
        return selection;
    }

    public List<Activiteit> filterActiviteiten(Set<Filter> filters) {
        return this.selection = new ArrayList<Activiteit>(dbh.filterActiviteiten(filters));
    }

    public Activiteit updateActiviteit(Activiteit oldActiviteit, String newName, String newDescription, String newManual, Set<Property> newProperties) {
        return this.dbh.updateActiviteit(oldActiviteit, newName, newDescription, newManual, newProperties);
    }
}
