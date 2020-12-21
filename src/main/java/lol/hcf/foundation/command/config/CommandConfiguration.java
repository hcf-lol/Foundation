package lol.hcf.foundation.command.config;

import org.bukkit.ChatColor;

public interface CommandConfiguration {

    String CHAT_SEPARATOR = ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + new String(new char[53]).replace('\0', '-');
    String COMMAND_NOT_FOUND = ChatColor.RED + "Unknown command. You can use \"%s\" for help.";
    String INVALID_USAGE = ChatColor.RED + "Invalid command usage.";

    default String getPrimaryColor() {
        return ChatColor.YELLOW.toString();
    }

    default String getSecondaryColor() {
        return ChatColor.BLUE.toString();
    }

    default String getChatSeparator() {
        return CommandConfiguration.CHAT_SEPARATOR;
    }

    default String getCommandNotFoundError() {
        return CommandConfiguration.COMMAND_NOT_FOUND;
    }

    default String getInvalidCommandUsageError() {
        return CommandConfiguration.INVALID_USAGE;
    }
}
