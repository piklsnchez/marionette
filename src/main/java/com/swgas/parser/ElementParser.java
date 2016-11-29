package com.swgas.parser;

import com.swgas.marionette.Marionette;
import static com.swgas.parser.MarionetteParser.NO_SUCH_ELEMENT_EXCEPTION;
import java.io.StringReader;
import java.util.NoSuchElementException;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class ElementParser implements MarionetteParser<String> {

    @Override
    public String parseFrom(String s) {
        JsonValue[] tuple = getTuple(s);
        if (tuple[0].getValueType() != JsonValue.ValueType.NULL) {
            String e = ((JsonObject) tuple[0]).getJsonString("error").getString();
            String m = ((JsonObject) tuple[0]).getJsonString("message").getString();
            if (NO_SUCH_ELEMENT_EXCEPTION.equals(e)) {
                throw new NoSuchElementException(m);
            }
            throw new RuntimeException(String.format("%s: %s", e, m));
        }
        if (tuple[1].getValueType() == JsonValue.ValueType.NULL) {
            return null;
        }
        return ((JsonObject) tuple[1]).getJsonObject("value").getJsonString(Marionette.WEBELEMENT_KEY).getString();
    }
    
    public static String toElement(String ele){
        try(JsonReader reader = Json.createReader(new StringReader(ele))){
            return reader.readObject().getString(Marionette.WEBELEMENT_KEY);
        }
    }
}
