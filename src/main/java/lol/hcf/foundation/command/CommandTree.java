package lol.hcf.foundation.command;

import lol.hcf.foundation.command.config.CommandConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CommandTree extends Command {

    private static final String CHAT_SEPARATOR = ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH.toString() + String.join("", Collections.nCopies(53, "-"));;

    private final CommandConfig config;
    private final String description;
    private final String category;
    private final Map<String, CommandTree> subcommands;

    private String[] helpMessage;

    public CommandTree(CommandConfig config, String permission, String category, String description, boolean allowConsole, String... aliases) {
        super(permission, allowConsole, aliases);
        this.config = config;
        this.category = category;
        this.description = description;
        this.subcommands = new LinkedHashMap<>();
    }

    public CommandTree(CommandConfig config, String name) {
        this(config, null, null, null, true, name);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (this.helpMessage != null && (args.length == 0 || args[0].equalsIgnoreCase("help"))) {
            sender.sendMessage(this.helpMessage);
            return true;
        }

        CommandTree subcommand = args.length > 0 ? subcommands.get(args[0].toLowerCase()) : null;
        if (subcommand != null) {
            String[] cutArgs = new String[args.length - 1];
            System.arraycopy(args, 1, cutArgs, 0, cutArgs.length);
            return subcommand.onCommand(sender, command, label + ' ' + args[0].toLowerCase(), cutArgs);
        }

        if (this.category == null) {
            //sender.sendMessage(String.format(HardcoreFactions.getInstance().getConfiguration().getMessageConfig().unknownCommand, '/' + label.split(" ")[0].toLowerCase() + " help"));
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        // todo : implement
        return super.onTabComplete(sender, label, args);
    }

    protected final void addSubcommand(CommandTree command) {
        for (String alias : command.aliases) this.subcommands.put(alias, command);
    }

    /**
     * Populates the help message
     */
    protected void generateCommandData() {
        // Remove Duplicates and sort lexicographically by category, then by command's primary name (first in the aliases list)
        Comparator<CommandTree> c1 = Comparator.comparing((o) -> o.category, Comparator.naturalOrder());
        Comparator<CommandTree> c2 = Comparator.comparing((o) -> o.aliases[0], Comparator.naturalOrder());

        Set<CommandTree> commands = new TreeSet<>(c1.thenComparing(c2));
        commands.addAll(this.subcommands.values());

        List<String> help = new ArrayList<>(commands.size() * 3 + 2);
        help.add(CommandTree.CHAT_SEPARATOR);

        String lastCategory = null;
        for (CommandTree subcommand : commands) {
            if (!subcommand.category.equalsIgnoreCase(lastCategory)) {
                help.add(this.config.getSecondaryColor() + subcommand.category + ": ");
                lastCategory = subcommand.category;
            }

            help.add(this.config.getPrimaryColor()
                + "  /"
                + this.aliases[0]
                + ' '
                + subcommand.aliases[0]
                + ' '
                + subcommand.usage
                + ' '
                + ChatColor.GRAY
                + ChatColor.STRIKETHROUGH
                + "-"
                + ChatColor.RESET
                + ' '
                + ChatColor.GRAY
                + subcommand.description
            );

            help.add("");
        }

        help.set(help.size() - 1, CommandTree.CHAT_SEPARATOR);

        this.helpMessage = new String[help.size()];
        help.toArray(this.helpMessage);
    }
}
