package com.swgas.model;

import com.swgas.rest.Jsonable;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;

public class WebElement implements Jsonable<WebElement> {
    public static final String ID  = "element-6066-11e4-a52e-4f735466cecf";
    public static final String KEY = "ELEMENT";
    
    private String id;
    
    public WebElement(){}
    
    public WebElement(String id){
        this.id = id;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public String toJson(){
        return toString();
    }
    
    @Override
    public WebElement fromJson(String json){
        JsonObject _json = Json.createReader(new StringReader(json)).readObject();
        this.id = _json.getString(KEY, null);
        if(null == id){
            id = _json.getString("element", null);
        }
        return this;
    }
    
    @Override
    public String toString(){
        return Json.createObjectBuilder()
        .add("element", id)
        .build().toString();
    }
}
