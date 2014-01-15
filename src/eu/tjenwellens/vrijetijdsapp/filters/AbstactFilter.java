package eu.tjenwellens.vrijetijdsapp.filters;

import android.os.Parcel;

/**
 *
 * @author Tjen
 */
public abstract class AbstactFilter implements Filter {
    protected String name;

    public AbstactFilter(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstactFilter other = (AbstactFilter) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
    }

    protected AbstactFilter(Parcel in) {
        this.name = in.readString();
    }
}
