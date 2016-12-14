package com.swgas.parser;

import com.swgas.marionette.Marionette;
import javax.json.JsonObject;

/**
 *
 * @author ocstest
 */
public class ContextParser implements MarionetteParser<Marionette.Context>{
    @Override
    public Marionette.Context parseFrom(String c){        
        JsonObject value = (JsonObject)MarionetteParser.OBJECT.parseFrom(c);
        return Marionette.Context.valueOf(value.getString("context"));
    }
}
