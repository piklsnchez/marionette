package com.swgas.util;

import com.swgas.exception.MarionetteException;
import com.swgas.marionette.Marionette;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class MarionetteUtil {
    private static final String CLASS = MarionetteUtil.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    
    private static final CharsetDecoder CHARSET_DECODER = Charset.defaultCharset().newDecoder();
    
    public static JsonObject toJsonObject(String s){
        return Json.createReader(new StringReader(s)).readObject();
    }
    
    public static String createResult(String key, String value){
        return Json.createObjectBuilder().add(key, value).build().toString();
    }
    
    /**
     * parse the message length out of the buffer and move position to beginning of message
     * @param buffer
     * @return 
     */
    public static int parseIncomingPayloadLength(ByteBuffer buffer){
        String _size;
        try{
            _size = CHARSET_DECODER.decode(buffer).toString();
        } catch(CharacterCodingException e){
            throw new MarionetteException(e);
        }
        int pos = _size.indexOf(':');
        if(0 > pos){
            throw new MarionetteException(String.format("%s dosen't contain a ':'", _size));
        }
        _size = _size.substring(0, pos);
        if(!_size.chars().allMatch(Character::isDigit)){
            throw new MarionetteException(String.format("\"%s\" is not numeric", _size));
        }
        buffer.position(pos + 1);
        return Integer.parseInt(_size, 10);
    }
    
    public static JsonArray parseIncomingMessage(ByteBuffer buffer){
        JsonArray result = JsonArray.EMPTY_JSON_ARRAY;
        try{
            String buf = CHARSET_DECODER.decode(buffer).toString();
            JsonValue json = Json.createReader(new StringReader(buf)).readValue();
            if(buf.startsWith("[")){
                result = json.asJsonArray();
            } else {
                result = Json.createArrayBuilder().add(-1).add(-1).addNull().add(json.asJsonObject()).build();
            }
        } catch(CharacterCodingException e){
            throw new MarionetteException(e);
        }
        return result;
    }
    
    private static JsonValue[] getTuple(JsonArray json){
        JsonValue error    = json.get(2);
        JsonValue success  = json.get(3);
        return new JsonValue[]{error, success};
    }
    
    private static JsonValue get(JsonArray json){
        JsonValue[] tuple = getTuple(json);
        if(tuple[0].getValueType() != JsonValue.ValueType.NULL){
            throw new MarionetteException(String.format("%s: %s"
                    , tuple[0].asJsonObject().getString("error")
                    , tuple[0].asJsonObject().getString("message"))
                , new Throwable(tuple[0].asJsonObject().getString("stacktrace")));
        }
        return tuple[1];
    }
    
    public static String parseToValue(JsonArray json){
        return get(json).asJsonObject().getString("value");
    }
    
    public static String parseToSession(JsonArray json) {
        return get(json).asJsonObject().getString("sessionId");
    }
    
    public static List<String> parseToList(JsonArray json) {
        return get(json).asJsonArray().stream().map(j -> {
            switch(j.getValueType()){
                case STRING: return ((JsonString)j).getString();
                case NULL:   return null;
                default:     return Objects.toString(j);
            }
        }).collect(Collectors.toList());
    }
    
    public static String parseToElement(JsonArray json) {
        return get(json).asJsonObject().getString(Marionette.W3C_WEBELEMENT_KEY);
    }
    
    public static JsonObject parseToObject(JsonArray json){
        return get(json).asJsonObject();
    }
}
