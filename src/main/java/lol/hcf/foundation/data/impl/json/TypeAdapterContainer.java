package lol.hcf.foundation.data.impl.json;

/**
 * This class is a container for the {@link java.lang.reflect.Type} of type T, and
 * a corresponding {@link DataTypeAdapter}
 *
 * @param <T> The target type for serialization/deserialization
 */
public class TypeAdapterContainer<T> {

    private final Class<? extends T> typeOfT;
    private final DataTypeAdapter<? extends T> adapter;

    public TypeAdapterContainer(Class<? extends T> typeOfT, DataTypeAdapter<? extends T> adapter) {
        this.typeOfT = typeOfT;
        this.adapter = adapter;
    }

    public Class<? extends T> getTypeOfT() {
        return typeOfT;
    }

    public DataTypeAdapter<? extends T> getAdapter() {
        return adapter;
    }
}
