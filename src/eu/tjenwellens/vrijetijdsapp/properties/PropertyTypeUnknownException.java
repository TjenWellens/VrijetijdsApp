package eu.tjenwellens.vrijetijdsapp.properties;

/**
 *
 * @author Tjen
 */
public class PropertyTypeUnknownException extends RuntimeException {
    public PropertyTypeUnknownException(PropertyType type) {
        super("Property unknown: " + type);
    }
}
