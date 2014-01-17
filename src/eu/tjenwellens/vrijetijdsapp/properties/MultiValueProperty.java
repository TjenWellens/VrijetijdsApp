package eu.tjenwellens.vrijetijdsapp.properties;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Tjen
 */
public class MultiValueProperty extends GeneralProperty {
    private static final String SPLITTER = "|";
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
        StringBuilder s = new StringBuilder("MultiValueProperty{" + super.getType().toString() + ",values=");
        boolean first = true;
        for (String value : values) {
            if (first) {
                first = false;
            } else {
                s.append(SPLITTER);
            }
            s.append(value);
        }
        s.append('}');
        return s.toString();
    }
}
