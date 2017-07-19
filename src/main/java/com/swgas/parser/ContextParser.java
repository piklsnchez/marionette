package com.swgas.parser;

import com.swgas.marionette.Marionette;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 *
 * @author ocstest
 */
public class ContextParser implements MarionetteParser<Marionette.Context>{
    @Override
    public Marionette.Context parseFrom(JsonArray json){        
        JsonObject value = (JsonObject)MarionetteParser.OBJECT.parseFrom(json);
        return Marionette.Context.valueOf(value.getString("context"));
    }
}
