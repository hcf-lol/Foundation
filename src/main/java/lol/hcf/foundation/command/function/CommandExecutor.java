package lol.hcf.foundation.command.function;

import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;

public interface CommandExecutor extends BiConsumer<CommandSender, String[]> {

}
