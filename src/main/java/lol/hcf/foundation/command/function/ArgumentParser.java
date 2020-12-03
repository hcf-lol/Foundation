package lol.hcf.foundation.command.function;

import java.util.function.Function;

@FunctionalInterface
public interface ArgumentParser<T> extends Function<String, T> {

    class Exception extends RuntimeException {
        private final boolean showCommandUsage;

        public Exception(String message, boolean showCommandUsage) {
            super(message);
            this.showCommandUsage = showCommandUsage;
        }

        public boolean shouldShowCommandUsage() {
            return this.showCommandUsage;
        }
    }

}
