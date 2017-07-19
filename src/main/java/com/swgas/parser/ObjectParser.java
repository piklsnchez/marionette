package com.swgas.parser;

import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class ObjectParser<T> implements MarionetteParser<T>{
    private static final Logger LOG = Logger.getLogger(ObjectParser.class.getName());
    
    @Override
    public T parseFrom(JsonArray json) {
        JsonObject success = get(json);
        JsonValue.ValueType successType = success.getValueType();
        JsonValue v;
        if(successType == JsonValue.ValueType.NULL){
            return null;
        }
        v = success.get("value");
        if(null != v){
            JsonValue.ValueType valueType = v.getValueType();
            switch(valueType){
                case NULL:   return null;
                case NUMBER: return (T)Double.valueOf(((JsonNumber)v).doubleValue());
                case STRING: return (T)((JsonString)v).getString();
                case OBJECT: return (T)v;
                case ARRAY:  return (T)((JsonArray)v).stream().map(Objects::toString).collect(Collectors.<String>toList());
                case FALSE:  return (T)Boolean.FALSE;
                case TRUE:   return (T)Boolean.TRUE;
                default:     LOG.info(String.format("valueType: %s", valueType)); return null;
            }
        } else {
            switch(success.getValueType()){
                case NULL:   return null;
                case NUMBER: return (T)Double.valueOf(((JsonNumber)success).doubleValue());
                case STRING: return (T)((JsonString)success).getString();
                case OBJECT: return (T)success;
                case ARRAY:  return (T)((JsonArray)success).stream().map(Objects::toString).collect(Collectors.<String>toList());
                case FALSE:  return (T)Boolean.FALSE;
                case TRUE:   return (T)Boolean.TRUE;
                default:     LOG.info(String.format("successType: %s", successType)); return null;
            }
        }
    }
}
