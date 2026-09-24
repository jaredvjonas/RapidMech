package org.irian.rapid.defs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.irian.rapid.defs.chassis.ChassisDefault;
import org.irian.rapid.defs.item.BasicItemDef;

import java.io.*;
import java.util.List;

public class ReaderWriter {
    public static MechDef readMech(InputStream stream) {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(stream), MechDef.class);
    }

    public static ChasisDef readChassis(InputStream stream) {
        Gson gson = new Gson();
        JsonElement json = JsonParser.parseReader(new InputStreamReader(stream));
        ChasisDef def = gson.fromJson(json, ChasisDef.class);
        // Custom.ChassisDefaults is not part of the written model; keep it on the side for promote-chassis-default.
        if (def != null && json.isJsonObject()) {
            JsonElement custom = json.getAsJsonObject().get("Custom");
            if (custom != null && custom.isJsonObject() && custom.getAsJsonObject().has("ChassisDefaults")) {
                def.SourceChassisDefaults = gson.fromJson(custom.getAsJsonObject().get("ChassisDefaults"),
                        new TypeToken<List<ChassisDefault>>() {}.getType());
            }
        }
        return def;
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
