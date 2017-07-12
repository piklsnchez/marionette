package com.swgas.parser;

import java.awt.geom.Rectangle2D;
import javax.json.JsonObject;

/**
 *
 * @author ocstest
 */
public class RectangleParser implements MarionetteParser<Rectangle2D>{
    @Override
    public Rectangle2D parseFrom(String s) {
        JsonObject value = (JsonObject)MarionetteParser.OBJECT.parseFrom(s);
        return new Rectangle2D.Double(value.getInt("x", 0), value.getInt("y", 0), value.getInt("w", 0), value.getInt("h", 0));
    }
}
