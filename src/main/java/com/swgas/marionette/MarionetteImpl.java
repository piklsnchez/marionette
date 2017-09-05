package com.swgas.marionette;

import com.swgas.exception.MarionetteException;
import com.swgas.model.Timeouts;
import com.swgas.util.MarionetteUtil;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;


public class MarionetteImpl implements Marionette {
    private static final String                    CLASS           = MarionetteImpl.class.getName();
    private static final Logger                    LOG             = Logger.getLogger(CLASS);
    private static final int                   MAX_READ_LOG_LENGTH = 127;
    private static final Cleaner                   CLEANER         = Cleaner.create();
    private static final long                      TIMEOUT         = 30;
    private        final AsynchronousSocketChannel channel;
    private              int                       messageId       = 0;
    
    
    protected MarionetteImpl(AsynchronousSocketChannel channel){
        this.channel = channel;
        CLEANER.register(this, this::shutdown);
        try{
            readAsync().get(TIMEOUT, TimeUnit.SECONDS);
        }catch(ExecutionException | InterruptedException | TimeoutException e){
            LOG.throwing(CLASS, "<init>", e);
            throw new MarionetteException(e);
        }
    }
    
    public void shutdown(){
        try{
            channel.close();
        } catch(IOException e){}
    }
    
    private CompletableFuture<JsonArray> readAsync(){
        CompletableFuture<JsonArray> ret = new CompletableFuture<>();
        ByteBuffer buf = ByteBuffer.allocateDirect(8);
        channel.read(buf, TIMEOUT, TimeUnit.SECONDS, ret, new CompletionHandler<Integer, CompletableFuture<JsonArray>>() {
            @Override
            public void completed(Integer len, CompletableFuture<JsonArray> future) {
                buf.flip();
                int size;
                try{
                    size = MarionetteUtil.parseIncomingPayloadLength(buf);
                } catch(Exception e){
                    future.completeExceptionally(e);
                    return;
                }
                ByteBuffer bigBuf = ByteBuffer.allocate(size);
                bigBuf.put(buf);
                channel.read(bigBuf
                , TIMEOUT
                , TimeUnit.SECONDS
                , future
                , new CompletionHandler<Integer, CompletableFuture<JsonArray>>() {
                    @Override
                    public void completed(Integer len, CompletableFuture<JsonArray> future) {
                        if(!bigBuf.hasRemaining()){
                            bigBuf.flip();
                            try{
                                JsonArray result = MarionetteUtil.parseIncomingMessage(bigBuf);
                                LOG.logp(Level.FINER, this.getClass().getName(), "readAsync", String.format("RETURN %s", result.toString().length() > MAX_READ_LOG_LENGTH ? result.toString().substring(0, MAX_READ_LOG_LENGTH).concat("...") : result));
                                future.complete(result);
                            } catch(Exception e){
                                future.completeExceptionally(e);
                            }
                        } else {
                            channel.read(bigBuf, future, this);
                        }
                    }
                    @Override
                    public void failed(Throwable e, CompletableFuture future) {
                        LOG.throwing(this.getClass().getName(), "failed", e);
                        if(e instanceof InterruptedByTimeoutException){
                            shutdown();
                        }
                        future.completeExceptionally(e);
                    }
                });
            }
            @Override
            public void failed(Throwable e, CompletableFuture future) {
                LOG.throwing(this.getClass().getName(), "failed", e);
                if(e instanceof InterruptedByTimeoutException){
                    shutdown();
                }
                future.completeExceptionally(e);
            }
        });
        return ret;
    }
    
    private CompletableFuture<JsonArray> writeAsync(String command){
        LOG.entering(CLASS, "writeAsync", command);
        CompletableFuture<JsonArray> ret = new CompletableFuture<>();
        channel.write(ByteBuffer.wrap(String.format("%d:%s", command.length()
        , command).getBytes())
        , TIMEOUT
        , TimeUnit.SECONDS
        , null
        , new CompletionHandler<Integer, CompletableFuture>() {
            @Override
            public void completed(Integer result, CompletableFuture future) {
                ret.complete(JsonArray.EMPTY_JSON_ARRAY);
            }

            @Override
            public void failed(Throwable e, CompletableFuture future) {
                LOG.throwing(CLASS, "failed", e);
                ret.completeExceptionally(e);
            }
        });
        return ret.thenCompose(s -> readAsync());
    }

    @Override
    public CompletableFuture<JsonArray> newSession(String sessionId) {
        String command = String.format("[0, %d, \"%s\", {\"capabilities\": {\"acceptSslCerts\":true}, \"sessionId\": \"%s\"}]", messageId++, Command.newSession.getCommand(), sessionId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> newSession() {
        String command = String.format("[0, %d, \"%s\", {\"capabilities\": {\"acceptSslCerts\":true}, \"sessionId\": null}]", messageId++, Command.newSession.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> deleteSession() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.deleteSession.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> quitApplication(List<String> flags) {
        LOG.entering(CLASS, "quitApplication", flags.toArray());
        CompletableFuture<JsonArray> ret = new CompletableFuture<>();        
        if(!channel.isOpen()){
            ret.complete(Json.createArrayBuilder().add(1).add(0).addNull().add(JsonObject.EMPTY_JSON_OBJECT).build());
            return ret;
        }
        String command = String.format("[0, %d, \"%s\", {\"flags\": %s}]"
        , messageId++, Command.quitApplication.getCommand(), (null == flags) ? "[]" : flags.stream().collect(Collectors.joining("\", \"", "[\"", "\"]")));
        LOG.exiting(CLASS, "quitApplication");
        return writeAsync(command).thenCompose(s -> {
            try{
                channel.close();
                ret.complete(s);
            } catch(IOException e){
                LOG.warning(e.toString());
                ret.completeExceptionally(e);
            }
            return ret;
        });
    }

    @Override
    public CompletableFuture<JsonArray> getElementAttribute(String elementId, String attribute) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"name\": \"%s\"}]", messageId++, Command.getElementAttribute.getCommand(), elementId, attribute);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getElementProperty(String elementId, String property) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"name\": \"%s\"}]", messageId++, Command.getElementProperty.getCommand(), elementId, property);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> clickElement(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.clickElement.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getElementText(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.getElementText.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> sendKeysToElement(String elementId, String text) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"text\": \"%s\"}]"
        , messageId++, Command.sendKeysToElement.getCommand(), elementId, text);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> clearElement(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.clearElement.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> isElementSelected(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.isElementSelected.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> isElementEnabled(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.isElementEnabled.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> isElementDisplayed(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.isElementDisplayed.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getElementTagName(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.getElementTagName.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getElementRectangle(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.getElementRect.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getElementCssProperty(String elementId, String property) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"propertyName\": \"%s\"}]", messageId++, Command.getElementValueOfCssProperty.getCommand(), elementId, property);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> singleTap(String elementId, int x, int y) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"x\": %d, \"y\": %d}]", messageId++, Command.singleTap.getCommand(), elementId, x, y);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> acceptDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.acceptDialog.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> dismissDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.dismissDialog.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getTextFromDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getTextFromDialog.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> sendKeysToDialog(String text) {
        String command = String.format("[0, %d, \"%s\", {\"text\": \"%s\"}]", messageId++, Command.sendKeysToDialog.getCommand(), text);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> setTestName(String testName) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\"}]", messageId++, Command.setTestName.getCommand(), testName);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> setScriptTimeout(Duration timeout) {
        String command = String.format("[0, %d, \"%s\", {\"ms\": %d}]", messageId++, Command.setScriptTimeout.getCommand(), timeout.toMillis());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> setSearchTimeout(Duration timeout) {
        String command = String.format("[0, %d, \"%s\", {\"ms\": %d}]", messageId++, Command.setSearchTimeout.getCommand(), timeout.toMillis());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getWindowHandle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowHandle.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getCurrentChromeWindowHandle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCurrentChromeWindowHandle.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getTitle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getTitle.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getWindowHandles() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowHandles.getCommand());
        return writeAsync(command);
        //.thenApply(MarionetteParser.ARRAY::parseFrom)
        //.thenApply(a -> a.stream().map(handle -> Objects.toString(handle, "")).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<JsonArray> getChromeWindowHandles() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getChromeWindowHandles.getCommand());
        return writeAsync(command);
        //.thenApply(MarionetteParser.ARRAY::parseFrom)
        //.thenApply(a -> a.stream().map(handle -> Objects.toString(handle, "")).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<JsonArray> getPageSource() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getPageSource.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> close() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.close.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> closeChromeWindow() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.closeChromeWindow.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> setContext(Context context) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\"}]", messageId++, Command.setContext.getCommand(), context);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getContext() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getContext.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> switchToWindow(String id) {
        String command = String.format("[0, %d, \"%s\", {\"name\": \"%s\"}]", messageId++, Command.switchToWindow.getCommand(), id);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getActiveFrame() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getActiveFrame.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> switchToParentFrame() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.switchToParentFrame.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> switchToFrame() {
        String command = String.format("[0, %d, \"%s\", {\"focus\": \"true\"}]", messageId++, Command.switchToFrame.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> switchToFrame(String id) {
        String command = String.format("[0, %d, \"%s\", {\"focus\": \"true\", \"id\": %s}]", messageId++, Command.switchToFrame.getCommand(), null == id ? "null" : "\""+id+"\"");
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> switchToFrame(int id) {
        String command = String.format("[0, %d, \"%s\", {\"focus\": \"true\", \"id\": %d}]", messageId++, Command.switchToFrame.getCommand(), id);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> switchToShadowRoot() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.switchToShadowRoot.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> switchToShadowRoot(String id) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.switchToShadowRoot.getCommand(), id);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getCurrentUrl() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCurrentUrl.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getWindowType() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowType.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> get(String url) {
        String command = String.format("[0, %d, \"%s\", {\"url\": \"%s\"}]", messageId++, Command.get.getCommand(), url);
        return writeAsync(command);
    }

    /**
     * 
     * @param com.swgas.model.Timeouts
     * @return 
     */
    @Override
    public CompletableFuture<JsonArray> setTimeouts(Timeouts timeouts) {
        String command = String.format("[0, %d, \"%s\", %s]", messageId++, Command.setTimeouts.getCommand(), timeouts.toWebDriverString());
        return writeAsync(command);
    }
    
    @Override
    public CompletableFuture<JsonArray> getTimeouts(){
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getTimeouts.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> goBack() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.goBack.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> goForward() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.goForward.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> refresh() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.refresh.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> executeScript(String script, String args, Boolean newSandbox, Duration scriptTimeout) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\", \"args\": %s, \"newSandbox\": %s, \"sandbox\": \"default\", \"scriptTimeout\": %s, \"filename\": null, \"line\": null}]"
        , messageId++, Command.executeScript.getCommand(), script.replace("\"", "\\\""), args, newSandbox, scriptTimeout == null ? null : scriptTimeout.toMillis());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> executeAsyncScript(String script, String args, Boolean newSandbox, Duration scriptTimeout, Boolean debug) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\", \"args\": %s, \"newSandbox\": %s, \"sandbox\": null, \"scriptTimeout\": %s, \"line\": null, \"filename\": null, \"debug_script\": %s}]"
        , messageId++, Command.executeAsyncScript.getCommand(), script.replace("\"", "\\\""), args, newSandbox, scriptTimeout == null ? null : scriptTimeout.toMillis(), debug);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> findElement(SearchMethod method, String value) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\", \"using\": \"%s\"}]", messageId++, Command.findElement.getCommand(), value, method);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> findElements(SearchMethod method, String value) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\", \"using\": \"%s\"}]", messageId++, Command.findElements.getCommand(), value, method);
        return writeAsync(command);
        //.thenApply(elements -> MarionetteParser.ARRAY.parseFrom(elements))
        //.thenApply(e -> e.stream().map(ElementParser::toElement).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<JsonArray> findElementFromElement(SearchMethod method, String value, String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"element\": \"%s\", \"value\": \"%s\", \"using\": \"%s\"}]", messageId++, Command.findElement.getCommand(), elementId, value, method);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> findElementsFromElement(SearchMethod method, String value, String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"element\": \"%s\", \"value\": \"%s\", \"using\": \"%s\"}]", messageId++, Command.findElements.getCommand(), elementId, value, method);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getActiveElement() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getActiveElement.getCommand());
        return writeAsync(command);//.thenApply(ElementParser::toElement);
    }

    @Override
    public CompletableFuture<JsonArray> log(LogLevel level, String message) {
        String command = String.format("[0, %d, \"%s\", {\"level\": \"%s\", \"value\": \"%s\"}]", messageId++, Command.log.getCommand(), level, message);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getLogs() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getLogs.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> addCookie(String cookie) {
        String command = String.format("[0, %d, \"%s\", {\"cookie\": %s}]", messageId++, Command.addCookie.getCommand(), cookie);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> deleteAllCookies() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.deleteAllCookies.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> deleteCookie(String name) {
        String command = String.format("[0, %d, \"%s\", {\"name\": \"%s\"}]", messageId++, Command.deleteCookie.getCommand(), name);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getCookies() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCookies.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> takeScreenshot() {
        String command = String.format("[0, %d, \"%s\", {\"id\": null, \"highlights\": null, \"full\": true}]", messageId++, Command.takeScreenshot.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> takeScreenshot(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"highlights\": null, \"full\": true, \"scroll\": true}]"
        , messageId++, Command.takeScreenshot.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getScreenOrientation() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getScreenOrientation.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> setScreenOrientation(Orientation orientation) {
        String command = String.format("[0, %d, \"%s\", {\"orientation\": \"%s\"}]", messageId++, Command.setScreenOrientation.getCommand(), orientation);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> getWindowRect() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowRect.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> setWindowRect(Rectangle2D size) {
        String command = String.format("[0, %d, \"%s\", {\"width\": %f, \"height\": %f}]", messageId++, Command.setWindowRect.getCommand(), size.getWidth(), size.getHeight());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> minimizeWindow() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.minimizeWindow.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> maximizeWindow() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.maximizeWindow.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> fullscreen() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.fullscreen.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<JsonArray> performActions() {
        throw new com.swgas.exception.UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<JsonArray> releaseActions() {
        throw new com.swgas.exception.UnsupportedOperationException();
    }
    
    @Override
    public String toString(){
        return Arrays.toString(new Object[]{messageId, channel});
    }
}
