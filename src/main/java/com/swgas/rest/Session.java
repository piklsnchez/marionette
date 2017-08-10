package com.swgas.rest;

import com.swgas.marionette.Marionette;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;

public class Session implements Closeable{
    private static final String CLASS    = Session.class.getName();
    private static final Logger LOG      = Logger.getLogger(CLASS);
    private static final Cleaner CLEANER = Cleaner.create();
            
    private String     sessionId;
    private Process    proc;
    private Marionette client;
    private Path       profileDirectory;

    public Session(){
        this(null, null, null, null);
    }
    
    public Session(String sessionId, Process proc, Marionette client, Path profileDirectory){
        CLEANER.register(this, this::close);
        //LOG.entering(CLASS, "<init>", Stream.of(sessionId, proc, client).toArray());
        this.sessionId        = sessionId;
        this.proc             = proc;
        this.client           = client;
        this.profileDirectory = profileDirectory;
        //LOG.exiting(CLASS, "<init>", this);
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
        //LOG.entering(CLASS, "setSessionId", sessionId);
        this.sessionId = sessionId;
        //LOG.exiting(CLASS, "setSessionId");
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
        //LOG.entering(CLASS, "setProc", proc);
        this.proc = proc;
        //LOG.exiting(CLASS, "setProc");
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
        //LOG.entering(CLASS, "setClient", client);
        this.client = client;
        //LOG.exiting(CLASS, "setClient");
    }
    
    public Path getProfileDirectory(){
        return profileDirectory;
    }
    
    public void setProfileDirectory(Path profileDirectory){
        this.profileDirectory = profileDirectory;
    }
    
    @Override
    public String toString(){
        return Stream.of(sessionId, proc, client).map(a -> Objects.toString(a, "\u2400")).reduce("", (a,b) -> String.format("%s|%s",a,b));
    }
    
    @Override
    public void close(){
        LOG.entering(CLASS, "close");
        try{
            Files.walkFileTree(profileDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException{
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        // directory iteration failed
                        LOG.warning(e.toString());
                        return FileVisitResult.CONTINUE;
                    }
                }
            });
            LOG.exiting(CLASS, "close");
        } catch(Exception e){
            LOG.logp(Level.WARNING, CLASS, "close", e.toString(), e);
        }
    }
}
