package eu.tjenwellens.vrijetijdsapp.properties;

/**
 *
 * @author Tjen
 */
public class PropertyTypeUnknowmException extends RuntimeException {
    public PropertyTypeUnknowmException(PropertyType type) {
        super("Property unknown: " + type);
    }
}
