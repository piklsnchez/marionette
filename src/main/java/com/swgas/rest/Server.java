package com.swgas.rest;

import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Server {

    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources
        final ResourceConfig rc = new ResourceConfig().packages("com.swgas.rest");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8080/"), rc);
    }

    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at application.wadl\n%s\nHit enter to stop it...", server.getListener("grizzly").toString()));
        System.in.read();
        server.shutdownNow();
    }
}
