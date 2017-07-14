package com.swgas.rest;

import com.swgas.marionette.Marionette;

public class Session {
    private String sessionId;
    private Process proc;
    private Marionette client;

    public Session(){}
    
    public Session(String sessionId, Process proc, Marionette client){
        this.sessionId = sessionId;
        this.proc      = proc;
        this.client    = client;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @return the proc
     */
    public Process getProc() {
        return proc;
    }

    /**
     * @param proc the proc to set
     */
    public void setProc(Process proc) {
        this.proc = proc;
    }

    /**
     * @return the client
     */
    public Marionette getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(Marionette client) {
        this.client = client;
    }
    
}
