import lol.hcf.foundation.data.ConfigurationFileStorage;
import lol.hcf.foundation.data.DataFileStorage;
import org.bukkit.ChatColor;

import java.io.File;

public class ConfigExample {

    public static class YamlTest extends ConfigurationFileStorage {
        public YamlTest(File configFile) {
            super(configFile);
        }

        public final String coloredString = ChatColor.RED + "abc";
        public final String stringWithAmpersand = "&abc";
        public final int iVal = 4;
    }

    public static class JsonTest extends DataFileStorage {
        public JsonTest(File targetFile) {
            super(targetFile);
        }

        public final String test1 = "a";
        public final String test2 = "b";
        public final int iVal = 5;
    }

}
