import lol.hcf.foundation.command.Command;
import lol.hcf.foundation.command.CommandTree;
import lol.hcf.foundation.command.annotation.Argument;
import lol.hcf.foundation.command.annotation.CommandEntryPoint;
import lol.hcf.foundation.command.annotation.Optional;
import lol.hcf.foundation.command.config.CommandConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandExample {

    public static class CommandTest extends Command {
        public CommandTest() {
            super("test.command", false, "test");
        }

        @CommandEntryPoint
        public void onCommand(CommandSender sender, @Argument(name = "cool arg") String argument, @Optional String optional) {}
    }

    public static class CommandTreeExample extends CommandTree {
        public CommandTreeExample() {
            super(new CommandConfig() {
                @Override
                public String getPrimaryColor() {
                    return ChatColor.YELLOW.toString();
                }

                @Override
                public String getSecondaryColor() {
                    return ChatColor.BLUE.toString();
                }
            }, "tree");

            super.addSubcommand(new CommandTreeExampleElement(super.config));
            super.generateCommandData();
        }

        private static class CommandTreeExampleElement extends CommandTree {
            public CommandTreeExampleElement(CommandConfig config) {
                super(config, "command.example", "Help Commands", "An example command", false, "example");
            }

            @CommandEntryPoint
            public void onCommand(CommandSender sender) {
                sender.sendMessage("example text");
            }
        }

    }
}
