package com.swgas.parser;

import com.swgas.marionette.Marionette;
import java.io.StringReader;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

@FunctionalInterface
public interface MarionetteParser<T> {
    public T parseFrom(String s);
    public static final MarionetteParser DEFAULT = s -> s;
    public static final MarionetteParser OBJECT = s -> {
        JsonArray jArray   = Json.createReader(new StringReader(s)).readArray();
        JsonValue error    = jArray.get(2);
        JsonObject success = jArray.getJsonObject(3);
        if(error.getValueType() != JsonValue.ValueType.NULL){
            throw new RuntimeException(String.format("%s: %s", ((JsonObject)error).getJsonString("error").getString(), ((JsonObject)error).getJsonString("message").getString()));
        }
        JsonValue v = success.get("value");
        if(null == v){
            return null;
        }
        switch(v.getValueType()){
            case NULL:   return null;
            case NUMBER: return ((JsonNumber)v).doubleValue();
            case STRING: return ((JsonString)v).getString();
            case OBJECT: return Objects.toString(v, "");
            case ARRAY:  return ((JsonArray)v).stream().map(Objects::toString).collect(Collectors.<String>toList());
            case FALSE:  return Boolean.FALSE;
            case TRUE:   return Boolean.TRUE;
            default:     return null;
        }
    };
    public static final MarionetteParser ELEMENT = s -> {
        JsonArray jArray   = Json.createReader(new StringReader(s)).readArray();
        JsonValue error    = jArray.get(2);
        JsonObject success = jArray.getJsonObject(3);
        if(error.getValueType() != JsonValue.ValueType.NULL){
            throw new RuntimeException(String.format("%s: %s", ((JsonObject)error).getJsonString("error").getString(), ((JsonObject)error).getJsonString("message").getString()));
        }
        return success.getJsonObject("value").getJsonString(Marionette.WEBELEMENT_KEY).getString();
    };
    public static final MarionetteParser BOOLEAN = s -> {
        JsonArray jArray   = Json.createReader(new StringReader(s)).readArray();
        JsonValue error    = jArray.get(2);
        JsonObject success = jArray.getJsonObject(3);
        if(error.getValueType() != JsonValue.ValueType.NULL){
            throw new RuntimeException(String.format("%s: %s", ((JsonObject)error).getJsonString("error").getString(), ((JsonObject)error).getJsonString("message").getString()));
        }
        return success.getBoolean("value");
    };
}
