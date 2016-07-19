package com.swgas.parser;

import com.swgas.marionette.Marionette;
import java.io.StringReader;
import java.util.NoSuchElementException;
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
    public static final String NO_SUCH_ELEMENT_EXCEPTION = "no such element";
    public T parseFrom(String s);
    public static final MarionetteParser DEFAULT = s -> s;
    public static final MarionetteParser OBJECT = s -> {
        JsonArray jArray   = Json.createReader(new StringReader(s)).readArray();
        JsonValue error    = jArray.get(2);
        JsonValue success  = jArray.get(3);
        if(error.getValueType() != JsonValue.ValueType.NULL){
            throw new RuntimeException(String.format("%s: %s", ((JsonObject)error).getJsonString("error").getString(), ((JsonObject)error).getJsonString("message").getString()));
        }
        JsonValue v;
        if(success.getValueType() == JsonValue.ValueType.NULL || null == (v = ((JsonObject)success).get("value"))){
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
        JsonValue success  = jArray.get(3);
        if(error.getValueType() != JsonValue.ValueType.NULL){
            String e = ((JsonObject)error).getJsonString("error").getString();
            String m = ((JsonObject)error).getJsonString("message").getString();
            if(NO_SUCH_ELEMENT_EXCEPTION.equals(e)){
                throw new NoSuchElementException(m);
            }
            throw new RuntimeException(String.format("%s: %s", e, m));
        }
        if(success.getValueType() == JsonValue.ValueType.NULL){
            return null;
        }
        return ((JsonObject)success).getJsonObject("value").getJsonString(Marionette.WEBELEMENT_KEY).getString();
    };
}
