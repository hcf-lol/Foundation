package lol.hcf.foundation.command.tree;

import lol.hcf.foundation.command.Command;
import lol.hcf.foundation.command.config.CommandConfiguration;
import lol.hcf.foundation.command.function.ArgumentParser;
import lol.hcf.foundation.command.function.CommandExecutor;
import lol.hcf.foundation.command.parse.CommandTypeAdapter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandTree<C extends CommandConfiguration> extends Command<C> {

    private final String[] helpMessage;
    private final Map<String, Command<?>> subcommands;

    private final String[] tabCompletionOptions;

    private CommandTree(C config, String[] helpMessage, Map<String, Command<?>> subcommands, String... aliases) {
        super(null, config, null, false, aliases);
        this.helpMessage = helpMessage;
        this.subcommands = subcommands;

        Set<String> commands = new TreeSet<>(Comparator.naturalOrder());
        commands.addAll(subcommands.values().stream().map((command) -> command.getAliases()[0].toLowerCase()).collect(Collectors.toSet()));
        this.tabCompletionOptions = commands.toArray(new String[0]);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(this.helpMessage);
            return true;
        }

        Command<?> entry = this.subcommands.get(args[0].toLowerCase());
        if (entry == null) {
            sender.sendMessage(String.format(super.config.getCommandNotFoundError(), '/' + label + " help"));
            return true;
        }

        String subLabel = label + ' ' + args[0].toLowerCase();

        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);

        try {
            return entry.onCommand(sender, command, subLabel, subArgs);
        } catch (ArgumentParser.Exception e) {
            sender.sendMessage(e.getMessage());
            if (e.shouldShowCommandUsage()) {
                sender.sendMessage(super.config.getInvalidCommandUsageError());
                sender.sendMessage(ChatColor.RED.toString() + '/' + subLabel + ' ' + entry.getUsage());
            }
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage(super.config.getInvalidCommandUsageError());
            sender.sendMessage(ChatColor.RED.toString() + '/' + subLabel + ' ' + entry.getUsage());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(this.tabCompletionOptions.length);
            for (String option : this.tabCompletionOptions) {
                if (option.startsWith(args[0].toLowerCase())) options.add(option);
            }

            return options;
        }

        Command<?> entry = this.subcommands.get(args[0].toLowerCase());
        if (entry == null) {
            return null;
        }

        String subLabel = alias + ' ' + args[0].toLowerCase();

        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
        return entry.onTabComplete(sender, command, subLabel, subArgs);
    }

    public void setCommand(org.bukkit.command.PluginCommand command) {
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    protected Method findEntryPoint() {
        return null;
    }

    @Override
    protected CommandExecutor generateCommandExecutor(Method target, CommandTypeAdapter typeAdapter) {
        return null;
    }

    public static <C extends CommandConfiguration> CommandTreeBuilder<C> builder(C config) {
        return new CommandTreeBuilder<>(config);
    }

    public static class CommandEntry {
        private final String category;
        private final String description;
        private final Command<?> command;

        public CommandEntry(String category, String description, Command<?> command) {
            this.category = category;
            this.description = description;
            this.command = command;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }

        public Command<?> getCommand() {
            return command;
        }
    }

    public static class CommandTreeBuilder<C extends CommandConfiguration> {
        private String[] aliases;
        private C commandConfig;
        private Function<Set<CommandEntry>, String[]> helpMessageGenerator;

        private final CommandTypeAdapter typeAdapter;
        private final Map<String, CommandEntry> subcommands;

        public CommandTreeBuilder(C commandConfig) {
            this.aliases = null;
            this.commandConfig = commandConfig;
            this.helpMessageGenerator = this::generateHelpMessage;
            this.typeAdapter = new CommandTypeAdapter();
            this.subcommands = new HashMap<>();
        }

        public CommandTreeBuilder<C> registerSubcommand(BiFunction<CommandTypeAdapter, CommandConfiguration, Command<C>> function, String category, String description) {
            CommandEntry entry = new CommandEntry(category, description, function.apply(this.typeAdapter, this.commandConfig));
            Arrays.stream(entry.command.getAliases()).forEach((alias) -> this.subcommands.put(alias.toLowerCase(), entry));
            return this;
        }

        public <T> CommandTreeBuilder<C> registerTypeMapping(Class<T> clazz, ArgumentParser<T> parser) {
            this.typeAdapter.registerTypeMapping(clazz, parser);
            return this;
        }

        public CommandTreeBuilder<C> setAliases(String... aliases) {
            this.aliases = aliases;
            return this;
        }

        public CommandTreeBuilder<C> setCommandConfig(C commandConfig) {
            this.commandConfig = commandConfig;
            return this;
        }

        public CommandTreeBuilder<C> setHelpMessageGenerator(Function<Set<CommandEntry>, String[]> helpMessageGenerator) {
            this.helpMessageGenerator = helpMessageGenerator;
            return this;
        }

        public CommandTree<C> build() {
            Map<String, Command<?>> commands = new HashMap<>();
            this.subcommands.forEach((k, v) -> commands.put(k, v.command));
            return new CommandTree<>(this.commandConfig, generateHelpMessage(new HashSet<>(this.subcommands.values())), commands);
        }

        private String[] generateHelpMessage(Set<CommandEntry> commands) {
            Comparator<CommandEntry> categoryComparator = Comparator.comparing(CommandEntry::getCategory, Comparator.naturalOrder());
            Comparator<CommandEntry> nameComparator = Comparator.comparing((entry) -> entry.command.getAliases()[0], Comparator.naturalOrder());

            Set<CommandEntry> entries = new TreeSet<>(categoryComparator.thenComparing(nameComparator));
            entries.addAll(commands);

            List<String> help = new ArrayList<>(commands.size() * 3 + 2);
            help.add(this.commandConfig.getChatSeparator());

            String lastCategory = null;
            for (CommandEntry entry : entries) {
                if (!entry.category.equalsIgnoreCase(lastCategory)) {
                    help.add(this.commandConfig.getSecondaryColor() + entry.category + ": ");
                    lastCategory = entry.category;
                }

                help.add(this.commandConfig.getPrimaryColor()
                        + "  /"
                        + this.aliases[0]
                        + ' '
                        + entry.command.getAliases()[0]
                        + ' '
                        + entry.command.getUsage()
                        + ' '
                        + ChatColor.GRAY
                        + ChatColor.STRIKETHROUGH
                        + "-"
                        + ChatColor.RESET
                        + ' '
                        + ChatColor.GRAY
                        + entry.description
                );

                help.add("");
            }

            help.set(help.size() - 1, this.commandConfig.getChatSeparator());

            return help.toArray(new String[0]);
        }
    }
}
