package eu.tjenwellens.vrijetijdsapp.storage.io;

import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.storage.StorageActiviteit;
import java.util.Collection;

/**
 *
 * @author Tjen
 */
class IOActiviteit extends StorageActiviteit {
    public IOActiviteit(String name, String description, String manual, Collection<Property> properties) {
        super(name, description, manual, properties);
    }
}
