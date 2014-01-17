package eu.tjenwellens.vrijetijdsapp.properties;

/**
 *
 * @author Tjen
 */
abstract class GeneralProperty implements Property {
    protected PropertyType type;

    GeneralProperty(PropertyType type) {
        this.type = type;
    }

    @Override
    public PropertyType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Property{" + "type=" + type + '}';
    }
}
