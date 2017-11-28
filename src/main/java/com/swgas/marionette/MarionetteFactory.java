package com.swgas.marionette;

import com.swgas.exception.MarionetteException;
import com.swgas.rest.Session;
import com.swgas.util.ZipUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MarionetteFactory {
    private static final String CLASS    = MarionetteFactory.class.getName();
    private static final Logger LOG      = Logger.getLogger(CLASS);
    private static final Pattern PATTERN = Pattern.compile("Listening on port (\\d+)");
    
    public static CompletableFuture<Marionette> getAsync(String host, int port){
        CompletableFuture<Marionette> ret = new CompletableFuture<>();
        try{
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress(host, port), ret, new CompletionHandler<Void, CompletableFuture>(){
                @Override
                public void completed(Void result, CompletableFuture future) {
                    future.complete(new MarionetteImpl(channel));                    
                }
                @Override
                public void failed(Throwable e, CompletableFuture future) {
                    future.completeExceptionally(new MarionetteException((e.getCause() instanceof ConnectException) ? e.getCause() : e));
                }            
            });
        } catch(IOException e){
            throw new MarionetteException(e);
        }
        return ret;
    }
    
    public static CompletableFuture<Session> createSession(){
        LOG.entering(CLASS, "createSession");
        CompletableFuture<Session> ret = new CompletableFuture<>();
        Session session = new Session();
        Path profileDirectory = Paths.get(System.getProperty("java.io.tmpdir"), String.format("marionette%s", UUID.randomUUID().toString()));
        try{
            ZipUtils.unZip(MarionetteFactory.class.getClassLoader().getResourceAsStream("marionette.zip"), profileDirectory);
            session.setProfileDirectory(profileDirectory);
            Files.newBufferedWriter(profileDirectory.resolve("user.js"))
            .append("user_pref(\"marionette.defaultPrefs.port\", 0);")                     .append(System.lineSeparator())
            .append("user_pref(\"browser.startup.homepage_override.mstone\", \"ignore\");").append(System.lineSeparator())
            .append("user_pref(\"browser.safebrowsing.downloads.remote.enabled\", false);").append(System.lineSeparator())
            .append("user_pref(\"browser.search.geoip.url\", \"\");")                      .append(System.lineSeparator())
            .append("user_pref(\"browser.selfsupport.url\", \"\");")                       .append(System.lineSeparator())
            .append("user_pref(\"browser.aboutHomeSnippets.updateUrl\", \"\");")           .append(System.lineSeparator())
            .append("user_pref(\"datareporting.healthreport.uploadEnabled\", false);")     .append(System.lineSeparator())
            .append("user_pref(\"datareporting.healthreport.service.enabled\", false);")   .append(System.lineSeparator())
            .append("user_pref(\"datareporting.healthreport.service.firstRun\", false);")  .append(System.lineSeparator())
            .append("user_pref(\"extensions.blocklist.enabled\", false);")                 .append(System.lineSeparator())
            .append("user_pref(\"extensions.getAddons.cache.enabled\", false);")           .append(System.lineSeparator())
            .append("user_pref(\"media.gmp-provider.enabled\", false);")                   .append(System.lineSeparator())
            .append("user_pref(\"media.gmp-manager.url\", \"\");")                         .append(System.lineSeparator())
            .append("user_pref(\"media.gmp-gmpopenh264.enabled\", false);")                .append(System.lineSeparator())
            .close();
            Process proc = new ProcessBuilder(Arrays.asList("firefox", "-marionette", "-profile", profileDirectory.toString(), "-new-instance")).start();
            session.setProc(proc);
            LOG.info(proc.info().toString());
            int port = getPort(session);
            if(0 > port){
                throw new MarionetteException("Couldn't get port");
            }
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress("localhost", port), ret, new CompletionHandler<Void, CompletableFuture>(){
                @Override
                public void completed(Void result, CompletableFuture future) {
                    session.setClient(new MarionetteImpl(channel));
                    proc.onExit().thenRun(() -> session.close());
                    future.complete(session);
                }
                @Override
                public void failed(Throwable e, CompletableFuture future) {
                    future.completeExceptionally(new MarionetteException((e.getCause() instanceof ConnectException) ? e.getCause() : e));
                }
            });
        } catch(IOException | MarionetteException e){
            if(e instanceof MarionetteException){
                try{
                    LOG.warning(Files.lines(profileDirectory.resolve("prefs.js")).reduce(String::concat).orElse("?"));
                } catch(IOException _e){
                    LOG.warning(_e.toString());
                }
                session.getProc().destroy();
            }
            throw new MarionetteException(e);
        }
        LOG.exiting(CLASS, "createSession", ret);
        return ret;
    }
    
    private static int getPort(Session session){
        LOG.entering(CLASS, "getPort", session);
        int result = -1;
        do{
            try(BufferedReader reader = Files.newBufferedReader(session.getProfileDirectory().resolve("prefs.js"))){
                reader.mark(20000);
                if(reader.lines().noneMatch(l -> l.startsWith("user_pref(\"marionette.port\","))){
                    continue;
                }
                reader.reset();
                String line = reader.lines().filter(l -> l.startsWith("user_pref(\"marionette.port\",")).findFirst().orElse("empty, -1);");
                result = Integer.parseInt(line.split(", ")[1].replace(");", ""), 10);
            } catch(Exception e){
                LOG.log(Level.WARNING, e.toString(), e);
            }
        } while(result == -1);
        LOG.exiting(CLASS, "getPort", result);
        return result;
    }
}
