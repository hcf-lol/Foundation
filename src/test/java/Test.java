import lol.hcf.foundation.data.ConfigurationFile;
import org.bukkit.ChatColor;

import java.io.File;

public class Test {

    public static class TestConfig extends ConfigurationFile {
        public TestConfig(File configFile) {
            super(configFile);
        }

        public final String testVar1 = "&4&l&b&c";
        public final String testVar2 = ChatColor.RED + "abc";
        public final String testVar3 = "1";
    }

    public static void main(String[] args) {

    }

}
