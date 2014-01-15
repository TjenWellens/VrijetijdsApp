package eu.tjenwellens.vrijetijdsapp;

import eu.tjenwellens.vrijetijdsapp.filters.Filter;
import eu.tjenwellens.vrijetijdsapp.filters.FilterCriterium;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tjen
 */
public class Activiteit {
    private String name;
    private String description;
    private String manual;
    private Map<String, Filter> filters;

    public Activiteit(String name, String description, String manual, Collection<Filter> filters) {
        this.name = name;
        this.description = description;
        this.manual = manual;
        this.filters = new HashMap<String, Filter>();
        for (Filter filter : filters) {
            this.filters.put(filter.getName(), filter);
        }
    }

    public boolean matchesAllCriteria(Collection<FilterCriterium> criteria) {
        for (FilterCriterium criterium : criteria) {
            Filter filter = filters.get(criterium.getName());
            if (filter == null) {
                continue;
            }
            for (String value : criterium.getValues()) {
                if (!filter.match(value)) {
                    return false;
                } else {
                    break;
                }
            }
        }
        return true;
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

    public Map<String, Filter> getFilters() {
        return filters;
    }

    @Override
    public String toString() {
        return "Activiteit{" + "name=" + name + ", description=" + description + ", manual=" + manual + ", filters(" + filters.size() + ")=" + filters + '}';
    }
}
