package com.swgas.parser;

import com.swgas.marionette.Marionette;
import java.util.NoSuchElementException;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class ElementParser implements MarionetteParser<String> {

    /**
     * 
     * @param json
     * @return
     * @deprecated Use MarionetteUtil::toElement
     */
    @Deprecated(forRemoval = true)
    @Override
    public String parseFrom(JsonArray json) {
        JsonValue[] tuple = getTuple(json);
        if (tuple[0].getValueType() != JsonValue.ValueType.NULL) {
            String e = ((JsonObject) tuple[0]).getJsonString("error").getString();
            String m = ((JsonObject) tuple[0]).getJsonString("message").getString();
            if (MarionetteParser.NO_SUCH_ELEMENT_EXCEPTION.equals(e)) {
                throw new NoSuchElementException(m);
            }
            throw new RuntimeException(String.format("%s: %s", e, m));
        }
        if (tuple[1].getValueType() == JsonValue.ValueType.NULL) {
            return null;
        }
        return ((JsonObject) tuple[1]).getJsonObject("value").getString(Marionette.WEBELEMENT_KEY);
    }
}
