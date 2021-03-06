package com.swgas.rest;

import com.swgas.model.JsonError;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.json.Json;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http2.Http2AddOn;
import org.glassfish.grizzly.http2.Http2Configuration;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

public class Server implements AutoCloseable {
    private static final String CLASS = Server.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private static final String LISTENER_NAME = "grizzly";
    private HttpServer server;

    public Server(){
        this("http://0.0.0.0:8008/");
    }
    
    public Server(String uri) {
        // create a resource config that scans for JAX-RS resources
        final ResourceConfig rc = new ResourceConfig().packages("com.swgas.rest");
        rc.register(JacksonFeature.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(uri), rc, false);
        server.getListener("grizzly").registerAddOn(new Http2AddOn(Http2Configuration.builder().build()));
        server.getServerConfiguration().setDefaultErrorPageGenerator(
            (Request request, int status, String reasonPhrase, String description, Throwable exception) -> 
                Json.createObjectBuilder()
                .add("request", request.toString())
                .add("status", status)
                .add("reasonPhrase", reasonPhrase)
                .add("description", description)
                .add("error", new JsonError(exception.toString(), exception.getMessage()
                    , Arrays.stream(exception.getStackTrace()).reduce("", (s, st) -> String.join("  \n  ", s, ""+st), String::concat)).toJson()
                ).build().toString()
        );
        try{
            server.start();
        } catch(IOException e){
            LOG.throwing(CLASS, "<init>", e);
            throw new RuntimeException(e);
        }
        LOG.finest(String.format("Starting server: %s", server.getListener(LISTENER_NAME)));
    }

    public static void main(String[] args) throws Exception {
        try(Server server = args.length == 0 ? new Server() : new Server(args[0])){
            System.out.println(String.format("Jersey app started with WADL available at application.wadl\n%s\nHit enter to stop it...", server.server.getListener(LISTENER_NAME).toString()));
            System.in.read();
        }
    }
    
    @Override
    public void close(){
        LOG.finest(String.format("Shutting down server: %s", server.getListener(LISTENER_NAME)));
        server.shutdownNow();
    }
    
    @Override
    public String toString(){
        return server.getListener(LISTENER_NAME).toString();
    }
}
