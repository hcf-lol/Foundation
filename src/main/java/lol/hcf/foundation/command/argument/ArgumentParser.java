package lol.hcf.foundation.command.argument;

import java.util.function.Function;

public final class ArgumentParser {

    public static final Function<String, Integer> PARSE_INTEGER = (value) -> wrapException(Integer::parseInt, value);
    public static final Function<String, Float> PARSE_FLOAT = (value) -> wrapException(Float::parseFloat, value);
    public static final Function<String, String> PARSE_STRING = (value) -> value;

    private static <T, R> R wrapException(Function<T, R> function, T t) {
        try {
            return function.apply(t);
        } catch (Exception e) {
            throw new ArgumentException(e);
        }
    }

    public static class ArgumentException extends RuntimeException {
        public ArgumentException(Exception e) {
            super(e);
        }
    }

}
