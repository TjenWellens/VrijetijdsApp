package eu.tjenwellens.vrijetijdsapp.properties;

import eu.tjenwellens.vrijetijdsapp.R;
import java.util.Collection;

/**
 *
 * @author Tjen
 */
public enum PropertyType {
    PEOPLE(R.string.prop_people),
    LOCATION(R.string.prop_location),
    ENERGY(R.string.prop_energy),
    PRICE(R.string.prop_price),
    TIME(R.string.prop_time),
    TAGS(R.string.prop_tags),
    RATING(R.string.prop_rating);
    private int resourceId;

    private PropertyType(int resourceId) {
        this.resourceId = resourceId;
    }

    public static Property createEnergyProperty(Energy energy) {
        return new EnumProperty<Energy>(ENERGY, energy);
    }

    public static Property createLocationProperty(Location location) {
        return new EnumProperty<Location>(LOCATION, location);
    }

    public static Property createPeopleProperty(int min, int max) {
        return new MinMaxProperty(PEOPLE, min, max);
    }

    public static Property createPriceProperty(int min, int max) {
        return new MinMaxProperty(PRICE, min, max);
    }

    public static Property createRatingProperty(Rating rating) {
        return new EnumProperty<Rating>(RATING, rating);
    }

    public static Property createTagsProperty(Collection<String> tags) {
        return new MultiValueProperty(TAGS, tags);
    }

    public static Property createTimeProperty(int min, int max) {
        return new MinMaxProperty(TIME, min, max);
    }

    public int getResourceId() {
        return resourceId;
    }
}
