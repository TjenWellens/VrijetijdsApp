package eu.tjenwellens.vrijetijdsapp.filters;

import java.util.Set;

/**
 *
 * @author Tjen
 */
public class FilterCriterium {
    private String name;
    private Set<String> values;

    public FilterCriterium(String name, Set<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public Set<String> getValues() {
        return values;
    }
}
