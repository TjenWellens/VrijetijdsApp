package eu.tjenwellens.vrijetijdsapp.properties;

/**
 *
 * @author Tjen
 */
public class RatingProperty extends GeneralProperty {
    public static final int FUN = 10;
    public static final int TRY = 5;
    public static final int NOT_TRY = -5;
    public static final int NOT_FUN = -10;
    private int rating;

    RatingProperty(PropertyType type, int rating) {
        super(type);
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return String.valueOf(rating);
    }
}
