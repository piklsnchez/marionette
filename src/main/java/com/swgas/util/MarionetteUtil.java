package com.swgas.util;

import com.swgas.exception.InvalidArgumentException;
import com.swgas.exception.MarionetteException;
import com.swgas.exception.NoSuchWindowException;
import com.swgas.exception.UnknownErrorException;
import com.swgas.exception.UnsupportedOperationException;
import com.swgas.marionette.Marionette;
import com.swgas.model.JsonError;
import java.awt.geom.Rectangle2D;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.ws.rs.WebApplicationException;

public class MarionetteUtil {
    private static final String CLASS = MarionetteUtil.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    
    private static final CharsetDecoder CHARSET_DECODER = Charset.defaultCharset().newDecoder();
    
    public static JsonObject parseJsonObject(String s){
        return Json.createReader(new StringReader(s)).readObject();
    }
    
    public static JsonArray parseJsonArray(String json){
        return Json.createReader(new StringReader(json)).readArray();
    }
    
    public static Rectangle2D parseRectangle(String json){
        JsonObject value = Json.createReader(new StringReader(json)).readObject();
        return new Rectangle2D.Double(value.getInt("x", 0), value.getInt("y", 0), value.getInt("width", 0), value.getInt("height", 0));
    }
    
    public static String createJson(String key, String value){
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
            JsonError error = new JsonError().fromJson(tuple[0].toString());
            throw new MarionetteException(error);
        }
        return tuple[1];
    }
    
    public static String toStringValue(JsonArray json){
        return get(json).asJsonObject().getString("value");
    }
    
    public static JsonValue toJsonValue(JsonArray json){
        return Optional.ofNullable(get(json).asJsonObject().get("value")).orElse(JsonValue.NULL);
    }
    
    public static boolean toBooleanValue(JsonArray json){
        return get(json).asJsonObject().getBoolean("value");
    }
    
    public static String toSession(JsonArray json) {
        return get(json).asJsonObject().getString("sessionId");
    }
    
    public static List<String> toList(JsonArray json) {
        return get(json).asJsonArray().stream().map(j -> {
            switch(j.getValueType()){
                case STRING: return ((JsonString)j).getString();
                case NULL:   return null;
                default:     return Objects.toString(j);
            }
        }).collect(Collectors.toList());
    }
    
    public static String toElement(JsonArray json) {
        return get(json).asJsonObject().getJsonObject("value").getString(Marionette.WEBELEMENT_KEY);
    }
    
    public static List<String> toElements(JsonArray json){        
        return get(json).asJsonArray().stream().map(e -> e.asJsonObject().getString(Marionette.WEBELEMENT_KEY)).collect(Collectors.toList());
    }
    
    public static JsonObject toObject(JsonArray json){
        return get(json).asJsonObject();
    }
    
    public static JsonArray toArray(JsonArray json){
        LOG.entering(CLASS, "toArray", json);
        try{
            JsonArray result = get(json).asJsonArray();
            LOG.exiting(CLASS, "toArray", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "toArray", e);
            throw e;
        }
    }
    
    public static Rectangle2D toRectangle(JsonArray json){
        JsonObject value = get(json).asJsonObject();
        return new Rectangle2D.Double(value.getInt("x", 0), value.getInt("y", 0), value.getInt("w", 0), value.getInt("h", 0));
    }
    
    public static WebApplicationException castException(Exception e){
        if(e instanceof MarionetteException){
            JsonError error = ((MarionetteException) e).getJsonError();
            switch(error.getError()){
                case "invalid argument":
                    return new InvalidArgumentException(error);
                case "no such window":
                    return new NoSuchWindowException(error);
                case "unsuppoerted operation":
                    return new UnsupportedOperationException(error);
                default:
                    return new UnknownErrorException(e, error);
            }
        } else if(e instanceof TimeoutException){
            return new com.swgas.exception.TimeoutException(e);
        } else {
            return new UnknownErrorException(e instanceof ExecutionException ? e.getCause() : e);                
        }
    }
}
