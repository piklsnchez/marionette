package com.swgas.parser;

import java.awt.geom.Point2D;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class PointParser implements MarionetteParser<Point2D>{

    @Override
    public Point2D parseFrom(JsonArray json) {
        JsonObject value = (JsonObject)MarionetteParser.OBJECT.parseFrom(json);
        return new Point2D.Double(value.getInt("x", 0), value.getInt("y", 0));
    }
}
