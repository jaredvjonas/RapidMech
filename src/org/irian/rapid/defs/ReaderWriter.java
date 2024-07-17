package org.irian.rapid.defs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.irian.rapid.defs.item.BasicItemDef;

import java.io.*;

public class ReaderWriter {
    public static MechDef readMech(InputStream stream) {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(stream), MechDef.class);
    }

    public static ChasisDef readChassis(InputStream stream) {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(stream), ChasisDef.class);
    }

    public static BasicItemDef readItem(InputStream stream) {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(stream), BasicItemDef.class);
    }

    public static void writeMech(MechDef def, OutputStream stream) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        gson.toJson(def, writer);
        writer.flush();
    }

    public static void writeChassis(ChasisDef def, OutputStream stream) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        gson.toJson(def, writer);
        writer.flush();
    }
}
