package com.swgas.parser;

import com.swgas.marionette.Marionette;
import java.awt.geom.Rectangle2D;
import javax.json.JsonObject;

/**
 *
 * @author ocstest
 */
public class OrientationParser implements MarionetteParser<Marionette.Orientation>{
    @Override
    public Marionette.Orientation parseFrom(String s) {
        JsonObject value = (JsonObject)MarionetteParser.OBJECT.parseFrom(s);
        return Marionette.Orientation.valueOf(value.getString("orientation"));
    }    
    
}
