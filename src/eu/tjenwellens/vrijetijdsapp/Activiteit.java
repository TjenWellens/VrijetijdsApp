package eu.tjenwellens.vrijetijdsapp;

import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyType;
import java.util.Map;

/**
 *
 * @author Tjen
 */
public interface Activiteit {
    String getName();

    String getDescription();

    String getManual();

    Map<PropertyType, Property> getProperties();
}
