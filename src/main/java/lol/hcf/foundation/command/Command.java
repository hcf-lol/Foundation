package lol.hcf.foundation.command;

import lol.hcf.foundation.command.annotation.Argument;
import lol.hcf.foundation.command.annotation.CommandEntryPoint;
import lol.hcf.foundation.command.annotation.Optional;
import lol.hcf.foundation.command.argument.ArgumentParser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.objectweb.asm.Opcodes.*;

public abstract class Command implements org.bukkit.command.CommandExecutor, TabCompleter {

    private static final Method DEFINE_CLASS;

    static {
        try {
            DEFINE_CLASS = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            Command.DEFINE_CLASS.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected final String[] aliases;
    protected final String usage;
    protected final String permission;
    protected final boolean allowConsole;

    private final CommandExecutor executor;

    /**
     * Generates a {@link CommandExecutor} implementation. This method is expensive in creating and generating
     * a new class instance via ASM & Reflection. Subsequent calls to {@link Command#onCommand(CommandSender, org.bukkit.command.Command, String, String[])}
     * will result in fast/direct access speed.
     */
    public Command(String permission, boolean allowConsole, String... aliases) {
        this.aliases = aliases;
        this.permission = permission;
        this.allowConsole = allowConsole;

        Class<?> declaringClass = this.getClass();
        Method method = Arrays.stream(declaringClass.getDeclaredMethods()).filter((m) -> m.isAnnotationPresent(CommandEntryPoint.class)).findFirst().orElseThrow(() -> new RuntimeException("no command entry point found"));

        StringBuilder usageBuilder = new StringBuilder();

        ClassNode node = new ClassNode();

        node.version = V1_8;
        node.access = ACC_PUBLIC;
        node.name = this.getClassName(CommandExecutor.class) + "$" + declaringClass.getSimpleName();
        node.superName = this.getClassName(Object.class);
        node.interfaces.add(this.getClassName(CommandExecutor.class));

        MethodNode executeNode = new MethodNode(ACC_PUBLIC, "execute", this.generateMethodDescriptor(void.class, Command.class, CommandSender.class, String[].class), null, null);
        InsnList executeInstructions = executeNode.instructions;
        executeInstructions.add(new VarInsnNode(ALOAD, 1));
        executeInstructions.add(new TypeInsnNode(CHECKCAST, this.getClassName(declaringClass)));
        executeInstructions.add(new VarInsnNode(ALOAD, 2));

        StringBuilder methodDescriptor = new StringBuilder();
        methodDescriptor.append("(").append(Type.getType(CommandSender.class).getDescriptor());

        for (int i = 1; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            String parameterName = parameter.isAnnotationPresent(Argument.class) ? parameter.getAnnotation(Argument.class).name() : parameter.getName();
            usageBuilder.append("[").append(parameterName).append("] ");


            executeInstructions.add(new FieldInsnNode(
                GETSTATIC,
                this.getClassName(ArgumentParser.class),
                "PARSE_" + parameter.getType().getSimpleName().toUpperCase(),
                'L' + this.getClassName(Function.class) + ';'
            ));

            if (i == method.getParameters().length - 1 && parameter.isAnnotationPresent(Optional.class)) {
                LabelNode endLabel = new LabelNode();
                LabelNode elseLabel = new LabelNode();

                executeInstructions.add(new LdcInsnNode(i - 1));
                executeInstructions.add(new VarInsnNode(ALOAD, 3));
                executeInstructions.add(new InsnNode(ARRAYLENGTH));
                executeInstructions.add(new JumpInsnNode(IF_ICMPLT, elseLabel));

                executeInstructions.add(new InsnNode(ACONST_NULL));
                executeInstructions.add(new JumpInsnNode(GOTO, endLabel));
                executeInstructions.add(elseLabel);

                executeInstructions.add(new VarInsnNode(ALOAD, 3));
                executeInstructions.add(new LdcInsnNode(i - 1));
                executeInstructions.add(new InsnNode(AALOAD));
                executeInstructions.add(endLabel);

            } else {

                executeInstructions.add(new VarInsnNode(ALOAD, 3));
                executeInstructions.add(new LdcInsnNode(i - 1));
                executeInstructions.add(new InsnNode(AALOAD));
            }

            executeInstructions.add(new MethodInsnNode(INVOKEINTERFACE, this.getClassName(Function.class), "apply", "(Ljava/lang/Object;)Ljava/lang/Object;"));
            executeInstructions.add(new TypeInsnNode(CHECKCAST, this.getClassName(parameter.getType())));

            methodDescriptor.append(Type.getType(parameter.getType()).getDescriptor());
        }

        methodDescriptor.append(")V");

        executeInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, this.getClassName(declaringClass), method.getName(), methodDescriptor.toString()));
        executeInstructions.add(new InsnNode(RETURN));

        MethodNode constructor = new MethodNode(ACC_PUBLIC, "<init>", this.generateMethodDescriptor(void.class), null, null);
        constructor.instructions.add(new VarInsnNode(ALOAD, 0));
        constructor.instructions.add(new MethodInsnNode(INVOKESPECIAL, this.getClassName(Object.class), "<init>", "()V"));
        constructor.instructions.add(new InsnNode(RETURN));

        node.methods.add(constructor);
        node.methods.add(executeNode);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        try {
            byte[] buffer = writer.toByteArray();
            Class<?> clazz = (Class<?>) Command.DEFINE_CLASS.invoke(this.getClass().getClassLoader(),node.name.replace('/', '.'), buffer, 0, buffer.length);
            this.executor = (CommandExecutor) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (usageBuilder.length() > 0) usageBuilder.deleteCharAt(usageBuilder.length() - 1);
        this.usage = usageBuilder.toString();
    }

    protected List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!this.allowConsole && !(sender instanceof Player)) {
            sender.sendMessage("This command cannot be executed if you are not a player.");
            return true;
        }

        if (!sender.hasPermission(this.permission)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        try {
            this.executor.execute(this, sender, args);
        } catch (ArgumentParser.ArgumentException | ArrayIndexOutOfBoundsException e) {
            sender.sendMessage(ChatColor.RED + "Invalid command usage:");
            sender.sendMessage(ChatColor.RED.toString() + '/' + label + ' ' + this.usage);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!sender.hasPermission(this.permission)) {
            sender.sendMessage(command.getPermissionMessage());
            return null;
        }

        return this.onTabComplete(sender, label, args);
    }

    private String getClassName(Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

    private String generateMethodDescriptor(Class<?> returnType, Class<?>... parameters) {
        StringBuilder builder = new StringBuilder();
        builder.append('(');

        for (Class<?> parameter : parameters) {
            builder.append(Type.getType(parameter).getDescriptor());
        }

        builder.append(')').append(Type.getType(returnType).getDescriptor());

        return builder.toString();
    }
}
