package com.swgas.parser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class ArrayParser implements MarionetteParser<List<String>> {
    @Override
    public List<String> parseFrom(String s) {
        JsonArray success = getArray(s);
        if (success.getValueType() == JsonValue.ValueType.NULL) {
            return null;
        }
        return success.stream().map(j -> {
            switch(j.getValueType()){
                case STRING: return ((JsonString)j).getString();
                case NULL:   return null;
                default:     return Objects.toString(j);
            }
        }).collect(Collectors.toList());
    }
}
