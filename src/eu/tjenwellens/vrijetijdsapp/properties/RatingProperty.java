package eu.tjenwellens.vrijetijdsapp.properties;

/**
 *
 * @author Tjen
 */
public class RatingProperty extends GeneralProperty {
    private Rating rating;

    RatingProperty(PropertyType type, Rating rating) {
        super(type);
        this.rating = rating;
    }

    public Rating getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return String.valueOf(rating);
    }
}
