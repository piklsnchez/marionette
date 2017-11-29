package com.swgas.model;

import javax.json.Json;
import com.swgas.rest.Jsonable;
import java.io.StringReader;
import java.util.Objects;
import javax.json.JsonObject;

public class Status implements Jsonable<Status> {
    private boolean ready;
    private String message;
    
    public Status(){}
    
    public Status(boolean ready, String message){
        this.ready   = ready;
        this.message = message;
    }
    
    /**
     * @return the ready
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * @param ready the ready to set
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toJson(){
        return toString();
    }
    
    @Override
    public Status fromJson(String json){
        JsonObject _json = Json.createReader(new StringReader(Objects.toString(json, "").isEmpty() ?  "{}" : json)).readObject();
        this.ready       = _json.getBoolean("ready", false);
        this.message     = _json.getString("message", null);
        return this;
    }
    
    @Override
    public String toString(){
        return Json.createObjectBuilder()
        .add("ready",   ready)
        .add("message", message)
        .build()
        .toString();
    }
}
