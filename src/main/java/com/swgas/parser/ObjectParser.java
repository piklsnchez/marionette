package com.swgas.parser;

import com.swgas.parser.MarionetteParser;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class ObjectParser<T> implements MarionetteParser<T>{
    
    @Override
    public T parseFrom(String s) {
        JsonObject success = get(s);
        JsonValue v;
        if(success.getValueType() == JsonValue.ValueType.NULL || null == (v = success.get("value"))){
            return null;
        }
        switch(v.getValueType()){
            case NULL:   return null;
            case NUMBER: return (T)Double.valueOf(((JsonNumber)v).doubleValue());
            case STRING: return (T)((JsonString)v).getString();
            case OBJECT: return (T)v;
            case ARRAY:  return (T)((JsonArray)v).stream().map(Objects::toString).collect(Collectors.<String>toList());
            case FALSE:  return (T)Boolean.FALSE;
            case TRUE:   return (T)Boolean.TRUE;
            default:     return null;
        }
    }
}
