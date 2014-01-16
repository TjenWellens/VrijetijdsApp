package eu.tjenwellens.vrijetijdsapp.filters;

/**
 *
 * @author Tjen
 */
public class FilterFactory {
    public static Filter createFilter(String name, String value, String type) {
        switch (FilterType.valueOf(type)) {
            case SINGLE_VALUE:
                return SingleValueFilter.fromValue(name, value);
            case MULTI_VALUE:
                return MultiValueFilter.fromValue(name, value);
            case MIN_MAX:
                return MinMaxFilter.fromValue(name, value);
        }
        return null;
    }
}
