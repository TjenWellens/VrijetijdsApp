package eu.tjenwellens.vrijetijdsapp.properties;

import eu.tjenwellens.vrijetijdsapp.R;

/**
 *
 * @author Tjen
 */
public enum Location implements ResourceIdEnum {
    INSIDE(R.string.prop_location_binnen), OUTSIDE(R.string.prop_location_buiten);
    private int resourceId;

    private Location(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}
