package lol.hcf.foundation.command.function;

import java.util.function.Function;

@FunctionalInterface
public interface ArgumentParser<T> extends Function<String, T> {

    class Exception extends RuntimeException {

        private boolean showCommandUsage;

        public Exception(String message, boolean showCommandUsage) {
            super(message);
        }

        public boolean shouldShowCommandUsage() {
            return this.showCommandUsage;
        }
    }

}
