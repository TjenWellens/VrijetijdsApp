package eu.tjenwellens.vrijetijdsapp.storage.database;

import android.content.Context;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.properties.Filter;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.properties.Rating;
import eu.tjenwellens.vrijetijdsapp.storage.StorageStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
            // TODO: check if applies to selection?
            selection.add(da);
            return da;
        } else {
            return null;
        }
    }

    public List<Activiteit> selectAllActiviteiten() {
        return this.selection = dbh.getAllActiviteiten();
    }

    public List<Activiteit> selectWithFilters(Set<Filter> filters) {
        return this.selection = new ArrayList<Activiteit>(dbh.filterActiviteiten(filters));
    }

    public List<Activiteit> getSelection() {
        return selection;
    }

    public Activiteit getActiviteit(String name) {
        return dbh.getActiviteitByName(name);
    }

    public Activiteit updateActiviteit(Activiteit oldActiviteit, String newName, String newDescription, String newManual, Set<Property> newProperties) {
        Activiteit newActiviteit = this.dbh.updateActiviteit(oldActiviteit, newName, newDescription, newManual, newProperties);
        if (newActiviteit != null) {
            selection.remove(oldActiviteit);
            selection.add(newActiviteit);
        }
        return newActiviteit;
    }

    public boolean removeActiviteit(Activiteit oldActiviteit) {
        boolean success = this.dbh.removeActiviteit(oldActiviteit);
        if (success && selection != null) {
            selection.remove(oldActiviteit);
        }
        return success;
    }

    public String getRandomNameFromSelection() {
        if (selection == null || selection.isEmpty()) {
            return null;
        }
        return selection.get(random(selection.size())).getName();
    }

    private int random(int max) {
        return new Random().nextInt(max);
    }

    public boolean updateRating(Activiteit activiteit, Rating newRating) {
        return dbh.updateRating(activiteit, newRating);
    }
}
