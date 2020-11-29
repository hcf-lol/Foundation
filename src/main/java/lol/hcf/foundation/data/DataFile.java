package lol.hcf.foundation.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class DataFile extends StorageFile<DataFile> {

    private static transient final Gson GSON;

    public DataFile(File targetFile) {
        super(targetFile);
    }

    @Override
    public DataFile dump(Writer out) {
        DataFile.GSON.toJson(this, out);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataFile parse(Reader in) {
        Map<String, Object> data = DataFile.GSON.fromJson(in, HashMap.class);
        super.reflectiveFieldSet(data);
        return this;
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        GSON = builder.create();
    }
}
