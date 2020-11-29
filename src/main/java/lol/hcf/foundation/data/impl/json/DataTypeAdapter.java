package lol.hcf.foundation.data.impl.json;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * An implementation class for JSON serialization / deserialization,
 * used in conjunction with {@link DataFile} and {@link TypeAdapterContainer}
 *
 * @param <T> The type target of which to serialize/deserialize
 */
public interface DataTypeAdapter<T> extends JsonSerializer<T>, JsonDeserializer<T> {}
