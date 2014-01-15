package eu.tjenwellens.vrijetijdsapp.filters;

import android.os.Parcel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tjen
 */
public class MultiValueFilter extends AbstactFilter {
    private Set<String> possibilities;

    public MultiValueFilter(String name, Collection<String> possibilities) {
        super(name);
        this.possibilities = new HashSet(possibilities);
    }

    public boolean match(String value) {
        if (value == null) {
            return false;
        }
        return possibilities.contains(value);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        boolean first = true;
        for (String string : possibilities) {
            if (first) {
                first = false;
            } else {
                s.append('|');
            }
            s.append(string);
        }
        return s.toString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeStringList(new ArrayList(possibilities));
    }
    public static final Creator<MultiValueFilter> CREATOR = new Creator<MultiValueFilter>() {
        public MultiValueFilter createFromParcel(Parcel in) {
            return new MultiValueFilter(in);
        }

        public MultiValueFilter[] newArray(int size) {
            return new MultiValueFilter[size];
        }
    };

    private MultiValueFilter(Parcel in) {
        super(in);
        List<String> list = new LinkedList<String>();
        in.readStringList(list);
        this.possibilities = new HashSet(list);
    }
}
