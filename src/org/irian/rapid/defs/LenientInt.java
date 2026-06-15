package org.irian.rapid.defs;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Parses an int field that some RT source defs write as a float (e.g. "TopSpeed": 90.4),
 * which gson's strict nextInt() rejects. Reads any number and rounds to int; writes plain int
 * so regenerated output stays integer-valued (no cosmetic 90.0 diff across every chassisdef).
 */
public class LenientInt extends TypeAdapter<Integer> {
    @Override
    public Integer read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0;
        }
        return (int) Math.round(in.nextDouble());
    }

    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        out.value(value.intValue());
    }
}
