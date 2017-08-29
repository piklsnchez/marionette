package com.swgas.rest;

import java.net.URI;
import java.util.logging.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Server implements AutoCloseable {
    private static final String CLASS = Server.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private static final String LISTENER_NAME = "grizzly";
    private HttpServer server;

    public Server(){
        this("http://localhost:8080/");
    }
    
    public Server(String uri) {
        // create a resource config that scans for JAX-RS resources
        final ResourceConfig rc = new ResourceConfig().packages("com.swgas.rest");
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(uri), rc);
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
}
