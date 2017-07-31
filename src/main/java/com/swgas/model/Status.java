package com.swgas.model;

import javax.json.Json;

public class Status {
    private boolean ready;
    private String message;
    
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
    public String toString(){
        return Json.createObjectBuilder()
            .add("ready",   ready)
            .add("message", message)
            .build()
            .toString();
    }
}
