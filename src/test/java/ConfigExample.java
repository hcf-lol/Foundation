import lol.hcf.foundation.data.ConfigurationFile;
import lol.hcf.foundation.data.DataFile;
import org.bukkit.ChatColor;

import java.io.File;

public class ConfigExample {

    public static class YamlTest extends ConfigurationFile {
        public YamlTest(File configFile) {
            super(configFile);
        }

        public final String coloredString = ChatColor.RED + "abc";
        public final String stringWithAmpersand = "&abc";
        public final int iVal = 4;
    }

    public static class JsonTest extends DataFile {
        public JsonTest(File targetFile) {
            super(targetFile);
        }

        public final String test1 = "a";
        public final String test2 = "b";
        public final int iVal = 5;
    }

}
