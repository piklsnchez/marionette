package com.swgas.parser;

import java.awt.geom.Point2D;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

@FunctionalInterface
public interface MarionetteParser<T> {
    public static final String NO_SUCH_ELEMENT_EXCEPTION = "no such element";
    public T parseFrom(String s);
    default JsonValue[] getTuple(String s){
        JsonArray jArray   = Json.createReader(new StringReader(s)).readArray();
        JsonValue error    = jArray.get(2);
        JsonValue success  = jArray.get(3);
        return new JsonValue[]{error, success};
    }
    default JsonObject get(String s){
        JsonValue[] tuple = getTuple(s);        
        if(tuple[0].getValueType() != JsonValue.ValueType.NULL){
            throw new RuntimeException(String.format("%s: %s", ((JsonObject)tuple[0]).getJsonString("error").getString(), ((JsonObject)tuple[0]).getJsonString("message").getString()));
        }
        return (JsonObject)tuple[1];
    }
    public static final MarionetteParser<String>  DEFAULT = s -> s;
    public static final MarionetteParser<Object>  OBJECT  = new ObjectParser<>();
    public static final MarionetteParser<String>  STRING  = new ObjectParser<>();
    public static final MarionetteParser<String>  ELEMENT = new ElementParser();
    public static final MarionetteParser<Point2D> POINT   = new PointParser();
}
