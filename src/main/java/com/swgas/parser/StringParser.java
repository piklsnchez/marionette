package com.swgas.parser;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class StringParser implements MarionetteParser<String>{

    @Override
    public String parseFrom(String s) {
        JsonObject success = get(s);
        JsonValue v;
        if(success.getValueType() == JsonValue.ValueType.NULL || null == (v = success.get("value"))){
            return null;
        }
        return ((JsonString)v).getString();
    }    
}
