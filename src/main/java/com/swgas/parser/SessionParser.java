package com.swgas.parser;

import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class SessionParser {//implements MarionetteParser<String>{
    private static final String CLASS = SessionParser.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    
    public static String parseFrom(JsonArray s) {
        LOG.entering(CLASS, "parseFrom", s);
        String result;
        JsonObject success = get(s);
        JsonValue v;
        if(success.getValueType() == JsonValue.ValueType.NULL || null == (v = success.get("sessionId")) || v.getValueType() == JsonValue.ValueType.NULL){
            result = null;
        } else {
            result = ((JsonString)v).getString();
        }
        LOG.exiting(CLASS, "parseFrom", result);
        return result;
    }
    private static JsonValue[] getTuple(JsonArray json){
        JsonValue error    = json.get(2);
        JsonValue success  = json.get(3);
        return new JsonValue[]{error, success};
    }
    private static JsonObject get(JsonArray json){
        JsonValue[] tuple = getTuple(json);
        if(tuple[0].getValueType() != JsonValue.ValueType.NULL){
            throw new RuntimeException(String.format("%s: %s", ((JsonObject)tuple[0]).getJsonString("error").getString(), ((JsonObject)tuple[0]).getJsonString("message").getString()));
        }
        return (JsonObject)tuple[1];
    }
}
