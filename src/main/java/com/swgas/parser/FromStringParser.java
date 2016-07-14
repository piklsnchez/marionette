package com.swgas.parser;

import com.swgas.marionette.Marionette;
import java.io.StringReader;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author ocstest
 */
@FunctionalInterface
public interface FromStringParser<T> {
    public T parseFrom(String s);
    public static final FromStringParser DEFAULT = s -> s;
    public static final FromStringParser JSON = s -> {
        JsonArray jArray   = Json.createReader(new StringReader(s)).readArray();
        JsonValue error    = jArray.get(2);
        JsonObject success = jArray.getJsonObject(3);
        if(error.getValueType() != JsonValue.ValueType.NULL){
            throw new RuntimeException(String.format("%s: %s", ((JsonObject)error).getJsonString("error").getString(), ((JsonObject)error).getJsonString("message").getString()));
        }
        return success.getOrDefault("value", JsonValue.NULL).toString();
    };
    public static final FromStringParser ELEMENT = s -> {
        JsonArray jArray   = Json.createReader(new StringReader(s)).readArray();
        JsonValue error    = jArray.get(2);
        JsonObject success = jArray.getJsonObject(3);
        if(error.getValueType() != JsonValue.ValueType.NULL){
            throw new RuntimeException(String.format("%s: %s", ((JsonObject)error).getJsonString("error").getString(), ((JsonObject)error).getJsonString("message").getString()));
        }
        return success.getJsonObject("value").getJsonString(Marionette.WEBELEMENT_KEY).getString();
    };
}
