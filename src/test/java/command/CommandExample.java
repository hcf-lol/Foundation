package command;

import lol.hcf.foundation.command.Command;
import lol.hcf.foundation.command.annotation.Argument;
import lol.hcf.foundation.command.annotation.CommandEntryPoint;
import lol.hcf.foundation.command.annotation.Optional;
import lol.hcf.foundation.command.config.CommandConfiguration;
import lol.hcf.foundation.command.parse.CommandTypeAdapter;
import lol.hcf.foundation.command.tree.CommandTree;
import org.bukkit.command.CommandSender;

public class CommandExample {

    public static class CommandTest extends Command<CommandConfiguration> {
        public CommandTest(CommandTypeAdapter typeAdapter, CommandConfiguration config) {
            super(typeAdapter, config, "test.perm", true, "test");
        }

        @CommandEntryPoint
        public void onCommand(CommandSender sender, @Argument(name = "testInt") @Optional Integer testVal) {
            if (testVal == null) {
                sender.sendMessage("no testval specified, but that's ok because it was optional!");
                return;
            }

            sender.sendMessage(Integer.toString(testVal));
        }
    }

    public static void main(String[] args) {
        CommandConfiguration config = new CommandConfiguration(){};
        CommandTree<?> tree = CommandTree.builder(config)
                .setAliases("testTree")
                .registerTypeMapping(Integer.class, (input) -> Integer.parseInt(input) + 1)
                .registerSubcommand(CommandTest::new, "Utility Commands", "Test Command")
                .build();

        tree.onCommand(new MockCommandSender(), null, "testTree", new String[]{"test", "420"});
    }
}
