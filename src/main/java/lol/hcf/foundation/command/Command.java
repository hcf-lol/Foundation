package lol.hcf.foundation.command;

import lol.hcf.foundation.command.annotation.Argument;
import lol.hcf.foundation.command.annotation.CommandEntryPoint;
import lol.hcf.foundation.command.annotation.Optional;
import lol.hcf.foundation.command.config.CommandConfiguration;
import lol.hcf.foundation.command.function.ArgumentParser;
import lol.hcf.foundation.command.function.CommandExecutor;
import lol.hcf.foundation.command.parse.CommandTypeAdapter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class Command<C extends CommandConfiguration> implements org.bukkit.command.CommandExecutor, TabCompleter {

    protected final static Method DEFINE_CLASS;

    protected final C config;
    protected final String[] aliases;
    protected final String permission;
    protected final boolean playerOnly;

    protected String usage;

    private final CommandExecutor executor;

    public Command(CommandTypeAdapter typeAdapter, C config, String permission, boolean playerOnly, String... aliases) {
        this.config = config;
        this.aliases = aliases;
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.executor = this.generateCommandExecutor(this.findEntryPoint(), typeAdapter);
    }

    public Command(C config, String permission, boolean playerOnly, String... aliases) {
        this(new CommandTypeAdapter(), config, permission, playerOnly, aliases);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (this.playerOnly && !(sender instanceof Player)) {
            sender.sendMessage("This command cannot be executed if you are not a player.");
            return true;
        }

        if (!sender.hasPermission(this.permission)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        try {
            this.executor.accept(sender, args);
        } catch (ArgumentParser.Exception e) {
            sender.sendMessage(e.getMessage());
            if (e.shouldShowCommandUsage()) {
                sender.sendMessage(ChatColor.RED.toString() + '/' + label + ' ' + this.usage);
            }
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage(this.config.getInvalidCommandUsageError());
            sender.sendMessage(ChatColor.RED.toString() + '/' + label + ' ' + this.usage);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (this.playerOnly && !(sender instanceof Player)) return null;
        if (!sender.hasPermission(this.permission)) return null;

        return this.onTabComplete(sender, alias, args);
    }

    protected List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public String getUsage() {
        return this.usage;
    }

    protected Method findEntryPoint() {
        return Arrays.stream(this.getClass().getMethods()).filter((m) -> m.isAnnotationPresent(CommandEntryPoint.class)).findFirst().orElseThrow(() -> new RuntimeException("no command entry point"));
    }

    protected CommandExecutor generateCommandExecutor(Method target, CommandTypeAdapter typeAdapter) {
        StringBuilder usageBuilder = new StringBuilder();
        // Maintain insertion order but remove duplicates by using a LinkedHashSet
        Set<Class<?>> uniqueParameters = Arrays.stream(target.getParameterTypes()).filter((p) -> p != CommandSender.class).collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Class<?>, String> parserMapping = new HashMap<>();

        ClassNode node = new ClassNode();
        node.version = V1_8;
        node.access = ACC_PUBLIC;
        node.name = getClassName(CommandExecutor.class) + '$' + this.getClass().getSimpleName() + '$' + target.getName();
        node.superName = getClassName(Object.class);
        node.interfaces.add(getClassName(CommandExecutor.class));

        List<Class<?>> concatParameters = new ArrayList<>(uniqueParameters.size() + 1);
        concatParameters.add(target.getDeclaringClass());

        for (int i = 0; i < uniqueParameters.size(); i++) concatParameters.add(Function.class);

        node.fields.add(new FieldNode(ACC_PRIVATE | ACC_FINAL, "target", 'L' + getClassName(target.getDeclaringClass()) + ';', null, null));

        MethodNode constructor = new MethodNode(ACC_PUBLIC, "<init>", generateMethodDescriptor(void.class, concatParameters), null, null);
        InsnList instructions = constructor.instructions;

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, getClassName(Object.class), "<init>", "()V"));

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new FieldInsnNode(PUTFIELD, node.name, "target", 'L' + getClassName(target.getDeclaringClass()) + ';'));

        int uniqueParameterIndex = 1;
        for (Class<?> parameter : uniqueParameters) {
            String name = "parser" + uniqueParameterIndex;
            String descriptor = 'L' + getClassName(Function.class) + ';';

            instructions.add(new VarInsnNode(ALOAD, 0));
            instructions.add(new VarInsnNode(ALOAD, uniqueParameterIndex + 1));
            instructions.add(new FieldInsnNode(PUTFIELD, node.name, name, descriptor));

            node.fields.add(new FieldNode(ACC_PRIVATE | ACC_FINAL, name, descriptor, null, null));
            parserMapping.put(parameter, name);
            uniqueParameterIndex++;
        }

        instructions.add(new InsnNode(RETURN));
        node.methods.add(constructor);
        MethodNode accept = new MethodNode(ACC_PUBLIC, "accept", generateMethodDescriptor(void.class, Arrays.asList(CommandSender.class, String[].class)), null, null);
        instructions = accept.instructions;

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, node.name, "target", 'L' + getClassName(target.getDeclaringClass()) + ';'));

        int parameterIndex = 0;
        for (Parameter parameter : target.getParameters()) {
            Class<?> parameterType = parameter.getType();
            if (parameterType == CommandSender.class) {
                instructions.add(new VarInsnNode(ALOAD, 1));
                continue;
            }

            String argumentName = parameter.isAnnotationPresent(Argument.class) ? parameter.getAnnotation(Argument.class).name() : parameter.getName();
            usageBuilder.append('[').append(argumentName).append("] ");

            boolean optionalParameter = parameter.isAnnotationPresent(Optional.class);
            LabelNode parse = new LabelNode();
            LabelNode end = new LabelNode();

            if (optionalParameter) {
                instructions.add(new LdcInsnNode(parameterIndex));
                instructions.add(new VarInsnNode(ALOAD, 2));
                instructions.add(new InsnNode(ARRAYLENGTH));
                instructions.add(new JumpInsnNode(IF_ICMPLT, parse));
                instructions.add(new InsnNode(ACONST_NULL));
                instructions.add(new JumpInsnNode(GOTO, end));
            }

            instructions.add(parse);
            instructions.add(new VarInsnNode(ALOAD, 0));
            instructions.add(new FieldInsnNode(GETFIELD, node.name, parserMapping.get(parameterType), 'L' + getClassName(Function.class) + ';'));
            instructions.add(new VarInsnNode(ALOAD, 2));
            instructions.add(new LdcInsnNode(parameterIndex));
            instructions.add(new InsnNode(AALOAD));

            instructions.add(new MethodInsnNode(INVOKEINTERFACE, getClassName(Function.class), "apply", "(Ljava/lang/Object;)Ljava/lang/Object;"));
            instructions.add(new TypeInsnNode(CHECKCAST, getClassName(parameterType)));
            instructions.add(end);

            parameterIndex++;
        }

        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, getClassName(target.getDeclaringClass()), target.getName(), generateMethodDescriptor(target.getReturnType(), Arrays.asList(target.getParameterTypes()))));
        instructions.add(new InsnNode(RETURN));

        node.methods.add(accept);

        MethodNode bridge = new MethodNode(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "accept", generateMethodDescriptor(void.class, Arrays.asList(Object.class, Object.class)), null, null);
        bridge.instructions.add(new VarInsnNode(ALOAD, 0));
        bridge.instructions.add(new VarInsnNode(ALOAD, 1));
        bridge.instructions.add(new TypeInsnNode(CHECKCAST, getClassName(CommandSender.class)));
        bridge.instructions.add(new VarInsnNode(ALOAD, 2));
        bridge.instructions.add(new TypeInsnNode(CHECKCAST, getClassName(String[].class)));
        bridge.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, node.name, "accept", accept.desc));
        bridge.instructions.add(new InsnNode(RETURN));

        node.methods.add(bridge);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        node.accept(cw);

        this.usage = usageBuilder.toString();

        try {
            byte[] classFileBuffer = cw.toByteArray();
            Class<?> clazz = (Class<?>) Command.DEFINE_CLASS.invoke(this.getClass().getClassLoader(), node.name.replace('/', '.'), classFileBuffer, 0, classFileBuffer.length);
            Constructor<?> c = clazz.getConstructor(concatParameters.toArray(new Class<?>[0]));

            List<Object> parameters = new ArrayList<>(uniqueParameters.size() + 1);
            parameters.add(this);
            for (Class<?> type : uniqueParameters) {
                parameters.add(typeAdapter.getParser(type));
            }

            return (CommandExecutor) c.newInstance(parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getClassName(Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

    private static String generateMethodDescriptor(Class<?> returnType, Iterable<Class<?>> types) {
        StringBuilder builder = new StringBuilder();
        builder.append('(');

        for (Class<?> type : types) {
            builder.append(Type.getType(type).getDescriptor());
        }

        return builder.append(')').append(Type.getType(returnType).getDescriptor()).toString();
    }

    static {
        try {
            DEFINE_CLASS = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            Command.DEFINE_CLASS.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
