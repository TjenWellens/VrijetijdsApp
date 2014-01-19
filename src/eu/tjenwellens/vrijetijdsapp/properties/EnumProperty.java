package eu.tjenwellens.vrijetijdsapp.properties;

/**
 *
 * @author Tjen
 */
public class EnumProperty<T extends ResourceIdEnum> extends GeneralProperty{
    private T value;

    public EnumProperty(PropertyType type, T value) {
        super(type);
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
