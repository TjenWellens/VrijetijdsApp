package eu.tjenwellens.vrijetijdsapp.properties;

import java.util.Collection;

/**
 *
 * @author Tjen
 */
public enum PropertyType {
    PEOPLE, LOCATION, ENERGY, PRICE, TIME, TAGS, RATING;
    public static Property createEnergyProperty(String energy) {
        return new SingleValueProperty(ENERGY, energy);
    }

    public static Property createLocationProperty(String location) {
        return new SingleValueProperty(ENERGY, location);
    }

    public static Property createPeopleProperty(int min, int max) {
        return new MinMaxProperty(PEOPLE, min, max);
    }

    public static Property createPriceProperty(int min, int max) {
        return new MinMaxProperty(PRICE, min, max);
    }

    public static Property createRatingProperty(int rating) {
        return new RatingProperty(RATING, rating);
    }

    public static Property createTagsProperty(Collection<String> tags) {
        return new MultiValueProperty(TAGS, tags);
    }

    public static Property createTimeProperty(int min, int max) {
        return new MinMaxProperty(TIME, min, max);
    }
}
