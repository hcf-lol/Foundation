package lol.hcf.foundation.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class DataFileStorage extends FileStorage<DataFileStorage> {

    private static transient final Gson GSON;

    public DataFileStorage(File targetFile) {
        super(targetFile);
    }

    @Override
    public DataFileStorage dump(Writer out) {
        DataFileStorage.GSON.toJson(this, out);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataFileStorage parse(Reader in) {
        Map<String, Object> data = DataFileStorage.GSON.fromJson(in, HashMap.class);
        super.reflectiveFieldSet(data);
        return this;
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        GSON = builder.create();
    }
}
