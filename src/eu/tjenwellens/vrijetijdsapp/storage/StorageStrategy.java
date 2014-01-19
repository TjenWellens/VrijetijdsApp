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

    Activiteit updateActiviteit(Activiteit oldActiviteit, String newName, String newDescription, String newManual, Set<Property> newProperties);

    boolean removeActiviteit(Activiteit oldActiviteit);

    List<Activiteit> selectAllActiviteiten();

    List<Activiteit> selectWithFilters(Set<Filter> filters);

    List<Activiteit> getSelection();

    String getRandomNameFromSelection();
}
