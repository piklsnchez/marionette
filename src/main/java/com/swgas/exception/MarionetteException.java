package com.swgas.exception;

import com.swgas.model.JsonError;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author ocstest
 */
public class MarionetteException extends RuntimeException{
    private JsonError jsonError;
    public MarionetteException(){super();}
    public MarionetteException(Throwable t){
        super(t);
        this.jsonError = toJsonError(t);
    }
    public MarionetteException(String message){
        super(message);
    }
    public MarionetteException(String message, Throwable t){
        super(message, t);
        this.jsonError = toJsonError(t);
    }
    public MarionetteException(JsonError error){
        this.jsonError = error;
    }
    
    public void setJsonError(JsonError error){
        this.jsonError = error;
    }
    
    public JsonError getJsonError(){
        return jsonError;
    }
    
    private static JsonError toJsonError(Throwable t){
        return new JsonError(
            t.getClass().getName()
            , t.getMessage()
            , Arrays.stream(t.getStackTrace()).map(Objects::toString).reduce("", (a, b) -> String.format("%s\n%s", a, b))
        );
    }
}
