package com.swgas.parser;

import java.awt.geom.Dimension2D;
import javax.json.JsonObject;

/**
 *
 * @author ocstest
 */
public class DimensionParser implements MarionetteParser<Dimension2D>{
    @Override
    public Dimension2D parseFrom(String dimension){        
        JsonObject value = (JsonObject)MarionetteParser.OBJECT.parseFrom(dimension);
        Dimension2D d = new Dimension2D() {
            private double width;
            private double height;
            @Override
            public double getWidth() {
                return width;
            }

            @Override
            public double getHeight() {
                return height;
            }

            @Override
            public void setSize(double width, double height) {
                this.width = width;
                this.height = height;
            }
        };
        d.setSize(value.getJsonNumber("width").doubleValue(), value.getJsonNumber("height").doubleValue());
        return d;
    }
}
