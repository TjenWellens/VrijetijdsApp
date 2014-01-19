package eu.tjenwellens.vrijetijdsapp.properties;

import eu.tjenwellens.vrijetijdsapp.R;

/**
 *
 * @author Tjen
 */
public enum Rating {
    FUN(R.string.prop_rating_fun), TRY(R.string.prop_rating_try), NOT_TRY(R.string.prop_rating_notry), NOT_FUN(R.string.prop_rating_nofun);
    private int resourceId;

    private Rating(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}
