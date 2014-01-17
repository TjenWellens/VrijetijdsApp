package eu.tjenwellens.vrijetijdsapp.storage;

import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tjen
 */
public interface StorageStrategy {
    Activiteit createActiviteit(String name, String description, String manual, Set<Property> properties);

    Activiteit getActiviteit(String name);

    public List<Activiteit> getAllActiviteiten();
}