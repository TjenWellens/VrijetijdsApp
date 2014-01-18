package eu.tjenwellens.vrijetijdsapp.storage;

import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.properties.Filter;
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

    List<Activiteit> getAllActiviteiten();

    List<Activiteit> getLatestSelection();

    List<Activiteit> filterActiviteiten(Set<Filter> filters);

    Activiteit updateActiviteit(Activiteit oldActiviteit, String newName, String newDescription, String newManual, Set<Property> newProperties);
}
