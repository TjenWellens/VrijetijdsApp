package eu.tjenwellens.vrijetijdsapp.filters;

import android.os.Parcelable;

/**
 *
 * @author Tjen
 */
public interface Filter extends Parcelable {
    String getName();

    boolean match(String value);

    String getValue();
    
    FilterType getType();
}
