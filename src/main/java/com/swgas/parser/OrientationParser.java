package com.swgas.parser;

import com.swgas.marionette.Marionette;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 *
 * @author ocstest
 */
public class OrientationParser implements MarionetteParser<Marionette.Orientation>{
    @Override
    public Marionette.Orientation parseFrom(JsonArray json) {
        JsonObject value = (JsonObject)MarionetteParser.OBJECT.parseFrom(json);
        return Marionette.Orientation.valueOf(value.getString("orientation"));
    }    
    
}
