package lol.hcf.foundation.command;

import org.bukkit.command.CommandSender;

public interface CommandExecutor {
    void execute(Command command, CommandSender sender, String[] args);
}
