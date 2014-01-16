package eu.tjenwellens.vrijetijdsapp.filters;

import android.os.Parcel;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String SPLITTER = "|";
    private Set<String> possibilities;

    public MultiValueFilter(String name, Collection<String> possibilities) {
        this(name, new HashSet(possibilities));
    }

    public static MultiValueFilter fromValue(String name, String value) {
        HashSet<String> possibilities = new HashSet();
        String[] values = value.split(SPLITTER);
        possibilities.addAll(Arrays.asList(values));
        return new MultiValueFilter(name, possibilities);
    }

    private MultiValueFilter(String name, HashSet<String> possibilities) {
        super(name);
        this.possibilities = possibilities;
    }

    private MultiValueFilter(Parcel in) {
        super(in);
        List<String> list = new LinkedList<String>();
        in.readStringList(list);
        this.possibilities = new HashSet(list);
    }

    public boolean match(String value) {
        if (value == null) {
            return false;
        }
        return possibilities.contains(value);
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

    public String getValue() {
        StringBuilder s = new StringBuilder();
        boolean first = true;
        for (String string : possibilities) {
            if (first) {
                first = false;
            } else {
                s.append(SPLITTER);
            }
            s.append(string);
        }
        return s.toString();
    }

    public FilterType getType() {
        return FilterType.MULTI_VALUE;
    }
}
