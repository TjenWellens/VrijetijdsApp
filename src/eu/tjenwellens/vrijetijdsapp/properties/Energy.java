package eu.tjenwellens.vrijetijdsapp.properties;

import eu.tjenwellens.vrijetijdsapp.R;

/**
 *
 * @author Tjen
 */
public enum Energy implements ResourceIdEnum {
    ACTIVE(R.string.prop_energy_actief), CALM(R.string.prop_energy_rustig);
    private int resourceId;

    private Energy(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}
