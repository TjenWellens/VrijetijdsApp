package eu.tjenwellens.vrijetijdsapp.properties;

import java.util.Collection;

/**
 *
 * @author Tjen
 */
public class Filter {
    public static final String SPLITTER = "|";
    private PropertyType type;
    private String value;

    public Filter(PropertyType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Filter(PropertyType type, Collection<String> values) {
        this.type = type;
        StringBuilder valueBuilder = new StringBuilder();
        for (String v : values) {
            valueBuilder.append(v);
        }
        this.value = valueBuilder.toString();
    }

    public PropertyType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Filter{" + "type=" + type + ", value=" + value + '}';
    }
}
