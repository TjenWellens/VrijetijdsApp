package eu.tjenwellens.vrijetijdsapp.storage.database;

import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.storage.StorageActiviteit;
import java.util.Collection;

/**
 *
 * @author Tjen
 */
class DatabaseActiviteit extends StorageActiviteit {
    public DatabaseActiviteit(String name, String description, String manual, Collection<Property> properties) {
        super(name, description, manual, properties);
    }
}
