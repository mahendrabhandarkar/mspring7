package com.ks.mspring7.typeadapter;
/*
* GsonBuilder builder = new GsonBuilder();
* builder.registerTypeAdapter(Integer.class, new IntegerTypeAdapter());
* builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
* builder.registerTypeAdapter(Double.class, new DoubleTypeAdapter());
* Gson gson = builder.serializeNulls().create();
*

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class DateTypeAdapter extends TypeAdapter<Date>{
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }else if (reader.peek() == null) {
            reader.nextNull();
            return null;
        }

        String stringValue = reader.nextString();
        try {
            if(stringValue.isEmpty()) {
                return null;
            }else if(stringValue.trim() == "") {
                return null;
            }else {
                Date value = dateFormat.parse(stringValue);
                return value;
            }
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public void write(JsonWriter writer, Date value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        writer.value(dateFormat.format(value));
    }
}
*/