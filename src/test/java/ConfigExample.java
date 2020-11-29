import com.google.gson.*;
import lol.hcf.foundation.data.impl.json.DataTypeAdapter;
import lol.hcf.foundation.data.impl.json.TypeAdapterContainer;
import lol.hcf.foundation.data.impl.yml.ConfigurationFile;
import lol.hcf.foundation.data.impl.json.DataFile;
import org.bukkit.ChatColor;

import java.io.File;
import java.lang.reflect.Type;

public class ConfigExample {

    public static class YamlTest extends ConfigurationFile {
        public YamlTest(File configFile) {
            super(configFile);
        }

        public final String coloredString = ChatColor.RED + "abc";
        public final String stringWithAmpersand = "&abc";
        public final int iVal = 4;
    }

    public static class CustomObject {

        public final String test1 = "a";

        public static class TypeAdapter implements DataTypeAdapter<CustomObject> {
            @Override
            public CustomObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new CustomObject();
            }

            @Override
            public JsonElement serialize(CustomObject src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive("b");
            }
        }

    }

    public static class JsonTest extends DataFile {

        public JsonTest(File targetFile) {
            super(targetFile, new TypeAdapterContainer<>(CustomObject.class, new CustomObject.TypeAdapter()));
        }

        public final CustomObject testVal = new CustomObject();
        public final String test1 = "a";
        public final String test2 = "b";
        public final int iVal = 5;
    }

    public static void main(String[] args) {
        System.out.println(new JsonTest(null).toString());
    }

}
