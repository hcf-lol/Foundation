package lol.hcf.foundation.command.parse;

import lol.hcf.foundation.command.function.ArgumentParser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandTypeAdapter {

    private static final ArgumentParser<Integer> PARSE_INT = wrapException(Integer::parseInt, (e) -> new ArgumentParser.Exception("Invalid number entered.", true));
    private static final ArgumentParser<Float> PARSE_FLOAT = wrapException(Float::parseFloat, (e) -> new ArgumentParser.Exception("Invalid number entered.", true));
    private static final ArgumentParser<Boolean> PARSE_BOOLEAN = wrapException(CommandTypeAdapter::parseBoolean, (e) -> new ArgumentParser.Exception("Expected true/false.", true));

    private static final ArgumentParser<String> PARSE_STRING = (in) -> in;

    private final Map<Class<?>, ArgumentParser<?>> parserMap;

    public CommandTypeAdapter() {
        this.parserMap = new HashMap<>();

        this.registerTypeMapping(Integer.class, CommandTypeAdapter.PARSE_INT);
        this.registerTypeMapping(Float.class, CommandTypeAdapter.PARSE_FLOAT);
        this.registerTypeMapping(String.class, CommandTypeAdapter.PARSE_STRING);
        this.registerTypeMapping(Boolean.class, CommandTypeAdapter.PARSE_BOOLEAN);
    }

    public ArgumentParser<?> getParser(Class<?> clazz) {
        return this.parserMap.get(clazz);
    }

    public void registerTypeMapping(Class<?> clazz, ArgumentParser<?> parser) {
        this.parserMap.put(clazz, parser);
    }

    public static <T> ArgumentParser<T> wrapException(Function<String, T> function, Function<Exception, ArgumentParser.Exception> throwableSupplier) {
        return (in) -> {
            try {
                return function.apply(in);
            } catch (Exception e) {
                throw throwableSupplier.apply(e);
            }
        };
    }

    private static boolean parseBoolean(String in) {
        if (!in.equalsIgnoreCase("true") && !in.equalsIgnoreCase("false")) throw new RuntimeException();
        return Boolean.parseBoolean(in);
    }
}
