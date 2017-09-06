package com.swgas.marionette;

import com.swgas.exception.MarionetteException;
import com.swgas.exception.UnknownErrorException;
import com.swgas.rest.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
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
        CompletableFuture<Session> ret = new CompletableFuture<>();
        try{
            Path profileDirectory = Files.createTempDirectory("marionette");
            Files.newBufferedWriter(profileDirectory.resolve("user.js"))
            .append("user_pref(\"marionette.defaultPrefs.port\", 0);").append(System.lineSeparator())
            .append("user_pref(\"browser.startup.homepage_override.mstone\", \"ignore\");").append(System.lineSeparator()).close();
            List<String> commandList = Arrays.asList("firefox", "-marionette", "-profile", profileDirectory.toString(), "-new-instance");
            ProcessBuilder procBuilder = new ProcessBuilder(commandList);
            procBuilder.command(commandList);
            Process proc = procBuilder.start();
            LOG.info(Objects.toString(proc.info(), "no proc"));
            int port;
            try{
                port = CompletableFuture.supplyAsync(() -> new BufferedReader(new InputStreamReader(proc.getInputStream())).lines()
                    .mapToInt(
                        line -> {
                            //LOG.info(line);
                            Matcher match = PATTERN.matcher(line);
                            if(match.find()){
                                String _port = match.group(1);
                                LOG.info(_port);
                                if(_port.codePoints().allMatch(Character::isDigit)){
                                    return Integer.parseInt(_port, 10);
                                }
                            }
                            return 0;
                        }
                    ).filter(p -> p > 0)
                    .findFirst().orElseThrow(UnknownErrorException::new)
                ).get(10, TimeUnit.SECONDS);
            } catch(TimeoutException e){
                proc.destroy();
                new Session(null, proc, null, profileDirectory).close();
                throw e;
            }
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress("localhost", port), ret, new CompletionHandler<Void, CompletableFuture>(){
                @Override
                public void completed(Void result, CompletableFuture future) {
                    Session session = new Session(null, proc, new MarionetteImpl(channel), profileDirectory);
                    proc.onExit().thenRun(() -> session.close());
                    future.complete(session);
                }
                @Override
                public void failed(Throwable e, CompletableFuture future) {
                    future.completeExceptionally(new MarionetteException((e.getCause() instanceof ConnectException) ? e.getCause() : e));
                }            
            });
        } catch(IOException | InterruptedException | ExecutionException | TimeoutException e){
            throw new MarionetteException(e);
        }
        return ret;
    }
}
