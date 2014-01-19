package eu.tjenwellens.vrijetijdsapp.storage;

import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyType;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author Tjen
 */
public abstract class StorageActiviteit implements Activiteit {
    private String name;
    private String description;
    private String manual;
    private Map<PropertyType, Property> properties;

    public StorageActiviteit(String name, String description, String manual, Collection<Property> properties) {
        this.name = name;
        this.description = description;
        this.manual = manual;
        this.properties = new EnumMap<PropertyType, Property>(PropertyType.class);
        for (Property property : properties) {
            if (property != null) {
                this.properties.put(property.getType(), property);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getManual() {
        return manual;
    }

    public Map<PropertyType, Property> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return name + ", " + description + ", " + manual + ", " + properties;
    }

    public int compareTo(Activiteit comp) {
        return name.compareTo(comp.getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StorageActiviteit other = (StorageActiviteit) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
