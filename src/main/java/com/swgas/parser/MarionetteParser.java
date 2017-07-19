package com.swgas.parser;

import com.swgas.marionette.Marionette;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

@FunctionalInterface
public interface MarionetteParser<T> {
    public static final String NO_SUCH_ELEMENT_EXCEPTION = "no such element";
    public T parseFrom(JsonArray json);
    default JsonValue[] getTuple(JsonArray json){
        JsonValue error    = json.get(2);
        JsonValue success  = json.get(3);
        return new JsonValue[]{error, success};
    }
    default JsonObject get(JsonArray json){
        JsonValue[] tuple = getTuple(json);
        if(tuple[0].getValueType() != JsonValue.ValueType.NULL){
            throw new RuntimeException(String.format("%s: %s", ((JsonObject)tuple[0]).getJsonString("error").getString(), ((JsonObject)tuple[0]).getJsonString("message").getString()));
        }
        return (JsonObject)tuple[1];
    }
    //public static final MarionetteParser<String>                 DEFAULT     = s -> s;
    public static final MarionetteParser<? super Object>         OBJECT      = new ObjectParser<>();
    public static final MarionetteParser<String>                 STRING      = new StringParser();
    public static final MarionetteParser<String>                 ELEMENT     = new ElementParser();
    public static final MarionetteParser<Point2D>                POINT       = new PointParser();
    public static final MarionetteParser<Rectangle2D>            RECTANGLE   = new RectangleParser();
    public static final MarionetteParser<Marionette.Orientation> ORIENTATION = new OrientationParser();
    public static final MarionetteParser<Dimension2D>            DIMENSION   = new DimensionParser();
    public static final MarionetteParser<Marionette.Context>     CONTEXT     = new ContextParser();
}
