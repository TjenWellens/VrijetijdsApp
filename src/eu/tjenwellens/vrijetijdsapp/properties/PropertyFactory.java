package eu.tjenwellens.vrijetijdsapp.properties;

import java.util.Collection;

/**
 *
 * @author Tjen
 */
public class PropertyFactory {
    public static Property createEnergyProperty(String energy) {
        return new SingleValueProperty(PropertyType.ENERGY, energy);
    }

    public static Property createLocationProperty(String location) {
        return new SingleValueProperty(PropertyType.LOCATION, location);
    }

    public static Property createPeopleProperty(int min, int max) {
        return new MinMaxProperty(PropertyType.PEOPLE, min, max);
    }

    public static Property createPriceProperty(int min, int max) {
        return new MinMaxProperty(PropertyType.PRICE, min, max);
    }

    public static Property createRatingProperty(int rating) {
        return new RatingProperty(PropertyType.RATING, rating);
    }

    public static Property createTagsProperty(Collection<String> tags) {
        return new MultiValueProperty(PropertyType.TAGS, tags);
    }

    public static Property createTimeProperty(int min, int max) {
        return new MinMaxProperty(PropertyType.TIME, min, max);
    }
}
