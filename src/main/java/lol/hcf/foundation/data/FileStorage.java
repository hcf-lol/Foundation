package lol.hcf.foundation.data;

import com.google.gson.JsonParseException;
import lol.hcf.foundation.data.impl.yml.ConfigurationFile;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Storage base class
 * This class is used as a base for any human-readable text operations
 * for the serialization/deserialization of the base class' fields into
 * a specified format.
 *
 * This class can be assumed/allowed to use the unsafe changing of a immutable type to a mutable one
 * via reflection, for the reasons of deserialization.
 *
 * @param <T> Recursive generic pattern to return a strong reference to this, used in the builder pattern
 */
public abstract class FileStorage<T extends FileStorage<?>> {

    protected static transient final Field MODIFIERS_FIELD;

    /**
     * Target file to read/write from.
     */
    protected transient final File targetFile;

    public FileStorage(File targetFile) {
        this.targetFile = targetFile;
    }

    /**
     * Reads the configuration/data from a reader, this function must be assumed
     * to be non-optimal and/or slow
     *
     * @param in The input {@link Reader} that will be parsed
     * @return Returns the <b>same</b> instance of the current object
     */
    public abstract T parse(Reader in);

    /**
     * Reads the configuration/data from a reader, this function must be assumed
     * to be non-optimal and/or slow
     *
     * @param out The output {@link Writer} to write to.
     * @return Returns the <b>same</b> instance of the current object
     */
    public abstract T dump(Writer out);

    /**
     * Reads the configuration/data from a reader, this function must be assumed
     * to be non-optimal and/or slow
     *
     * Utility function to parse a string
     * @param input The string to parse
     * @return Returns the <b>same</b> instance of the current object
     */
    public T parse(String input) {
        return this.parse(new StringReader(input));
    }

    /**
     * Writes the configuration/data to a {@link StringWriter} and then returned as a {@link String},
     * this function must be assumed to be non-optimal and/or slow
     *
     * Utility function to parse a string
     * @return Returns the string interpretation of the data help by this object
     */
    public String dumpToString() {
        StringWriter writer = new StringWriter();
        this.dump(writer);
        return writer.toString();
    }

    /**
     * Saves the current file / writes it to the targetFile
     */
    public void save() {
        if (!this.targetFile.getParentFile().exists()) {
            if (!this.targetFile.getParentFile().mkdirs())
                throw new RuntimeException("failed to create new directory: " + this.targetFile.getParentFile().getPath());
        }

        try (Writer writer = new FileWriter(this.targetFile)) {
            this.dump(writer);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("failed to create new file: " + this.targetFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException("failed to write to file: " + this.targetFile.getPath());
        }
    }

    /**
     * Loads the data from the file and performs error-checking / validation
     */
    public void load() {
        if (!this.targetFile.exists()) this.save();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.targetFile))) {
            StringBuilder sb = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line).append('\n');
            }

            this.parse(sb.toString());
        } catch (JsonParseException | YAMLException e) {
            String error = "Malformed Data: " + this.targetFile.getPath() + ", Renaming " + this.targetFile.getName() + " -> " + this.targetFile.getName() + ".old";
            System.err.println(e.getMessage());
            System.err.println(error);
            File target = new File(this.targetFile.getParentFile(), this.targetFile.getName() + ".old");
            if (!this.targetFile.renameTo(target)) throw new RuntimeException("failed to rename file");
            this.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        FileStorage<?> that = (FileStorage<?>) o;
        return this.targetFile.equals(that.targetFile);
    }

    @Override
    public int hashCode() {
        return this.targetFile.hashCode();
    }

    @Override
    public String toString() {
        return this.dumpToString();
    }

    /**
     * This method may or may not be used by any implementation of {@link FileStorage}
     * This method will break if any {@link SecurityManager} is preventing this operation.
     * @param object The data entries to be reflectively brought into the current instance of the lowest class in the current hierarchy
     */
    protected void reflectiveFieldSet(Object object) {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;
                if (Modifier.isFinal(field.getModifiers())) {
                    ConfigurationFile.MODIFIERS_FIELD.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                }

                field.setAccessible(true);
                field.set(this, field.get(object));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            MODIFIERS_FIELD = Field.class.getDeclaredField("modifiers");
            FileStorage.MODIFIERS_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
