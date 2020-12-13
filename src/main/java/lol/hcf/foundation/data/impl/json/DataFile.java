package lol.hcf.foundation.data.impl.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lol.hcf.foundation.data.FileStorage;
import lol.hcf.foundation.data.impl.yml.ConfigurationFile;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class DataFile extends FileStorage<DataFile> {

    private transient final Gson gson;

    public DataFile(File targetFile, TypeAdapterContainer<?>... adapters) {
        super(targetFile);
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        for (TypeAdapterContainer<?> container : adapters) {
            builder.registerTypeAdapter(container.getTypeOfT(), container.getAdapter());
        }

        this.gson = builder.create();
    }

    @Override
    public DataFile dump(Writer out) {
        this.gson.toJson(this, out);
        return this;
    }

    @Override
    public DataFile parse(Reader in) {
        Object obj = this.gson.fromJson(in, this.getClass());
        super.reflectiveFieldSet(obj);
        return this;
    }
}
