package com.swgas.rest;

import com.swgas.marionette.Marionette;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Session {
    private static final String CLASS = Session.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
            
    private String     sessionId;
    private Process    proc;
    private Marionette client;

    public Session(){
        this(null, null, null);
    }
    
    public Session(String sessionId, Process proc, Marionette client){
        LOG.entering(CLASS, "<init>", Stream.of(sessionId, proc, client).toArray());
        this.sessionId = sessionId;
        this.proc      = proc;
        this.client    = client;
        LOG.exiting(CLASS, "<init>", this);
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
        LOG.entering(CLASS, "setSessionId", sessionId);
        this.sessionId = sessionId;
        LOG.exiting(CLASS, "setSessionId");
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
        LOG.entering(CLASS, "setProc", proc);
        this.proc = proc;
        LOG.exiting(CLASS, "setProc");
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
        LOG.entering(CLASS, "setClient", client);
        this.client = client;
        LOG.exiting(CLASS, "setClient");
    }
    
    @Override
    public String toString(){
        return Stream.of(sessionId, proc, client).map(a -> Objects.toString(a, "\u2400")).reduce("", (a,b) -> String.format("%s|%s",a,b));
    }
}
