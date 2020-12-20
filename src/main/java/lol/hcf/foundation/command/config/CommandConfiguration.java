package lol.hcf.foundation.command.config;

import org.bukkit.ChatColor;

public interface CommandConfiguration {

    String CHAT_SEPARATOR = ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + new String(new char[53]).replace('\0', '-');

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
        return ChatColor.RED + "Unknown command. You can use \"%s\" for help.";
    }

    default String getInvalidCommandUsageError() {
        return ChatColor.RED + "Invalid command usage.";
    }
}
