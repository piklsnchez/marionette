package com.swgas.model;

import javax.json.Json;

public class JsonError {
    private String error;
    private String message;
    private String stacktrace;
    
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
    public String toString(){
        return Json.createObjectBuilder()
            .add("error", getError())
            .add("message", getMessage())
            .add("stacktrace", getStacktrace())
            .build().toString();
    }
}
