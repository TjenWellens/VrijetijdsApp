package eu.tjenwellens.vrijetijdsapp.properties;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Tjen
 */
public class MultiValueProperty extends GeneralProperty {
    private static final String DEFAULT_SPLITTER = ",";
    private Set<String> values;

    MultiValueProperty(PropertyType type, Collection<String> values) {
        super(type);
        this.values = new HashSet<String>(values);
    }

    public Set<String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return getValue(DEFAULT_SPLITTER);
    }

    public String getValue(String splitter) {
        StringBuilder s = new StringBuilder();
        boolean first = true;
        for (String value : values) {
            if (first) {
                first = false;
            } else {
                s.append(splitter);
            }
            s.append(value);
        }
        return s.toString();
    }

    static Set<String> parseMultiSet(String value) {
        Set<String> set = new HashSet<String>();
        String[] tags = value.split(MultiValueProperty.DEFAULT_SPLITTER);
        for (int i = 0; i < tags.length; i++) {
            set.add(tags[i].trim());
        }
        return set;
    }
}
