package com.swgas.model;

import com.swgas.rest.Jsonable;
import java.io.StringReader;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObject;

public class JsonError implements Jsonable<JsonError>{
    private String error;
    private String message;
    private String stacktrace;
    
    public JsonError(){}
    
    public JsonError(String error, String message, String stacktrace){
        this.error      = error;
        this.message    = message;
        this.stacktrace = stacktrace;
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
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

    /**
     * @return the stacktrace
     */
    public String getStacktrace() {
        return stacktrace;
    }

    /**
     * @param stacktrace the stacktrace to set
     */
    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }
    
    @Override
    public String toJson(){
        return toString();
    }
    
    @Override
    public JsonError fromJson(String json){
        JsonObject _json = Json.createReader(new StringReader(Objects.toString(json, "").isEmpty() ? "{}" : json)).readObject();
        this.error       = _json.getString("error", null);
        this.message     = _json.getString("message", null);
        this.stacktrace  = _json.getString("stacktrace", null);
        return this;
    }
    
    @Override
    public String toString(){
        return Json.createObjectBuilder()
            .add("error",      error)
            .add("message",    message)
            .add("stacktrace", stacktrace)
            .build().toString();
    }
}
