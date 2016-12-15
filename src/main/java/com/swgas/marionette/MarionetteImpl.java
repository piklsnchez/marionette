package com.swgas.marionette;

import com.swgas.exception.MarionetteException;
import com.swgas.parser.ElementParser;
import com.swgas.parser.MarionetteParser;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;


public class MarionetteImpl implements Marionette {
    private static final String CLASS = MarionetteImpl.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private static final long TIMEOUT = 10;
    private static final CharsetDecoder charsetDecoder = Charset.defaultCharset().newDecoder();
    private final AsynchronousSocketChannel channel;
    private CompletableFuture<String> futureQueue = CompletableFuture.completedFuture("");
    private int messageId = 0;
    
    protected MarionetteImpl(AsynchronousSocketChannel channel){
        this.channel = channel;
        readAsync().join();
    }
    
    @Override
    protected void finalize() throws Throwable{
        channel.close();
        super.finalize();
    }
    
    private CompletableFuture<String> readAsync(){
        CompletableFuture<String> ret = new CompletableFuture<>();
        ByteBuffer buf = ByteBuffer.allocateDirect(8);
        channel.read(buf, ret, new CompletionHandler<Integer, CompletableFuture>() {
            @Override
            public void completed(Integer len, CompletableFuture future) {
                buf.flip();
                String _size;
                try{
                    _size = charsetDecoder.decode(buf).toString();
                } catch(CharacterCodingException e){
                    throw new MarionetteException(e);
                }
                int pos = _size.indexOf(':');
                if(0 > pos){
                    future.completeExceptionally(new MarionetteException(String.format("%s dosen't contain a ':'", _size)));
                    return;
                }
                _size = _size.substring(0, pos);
                if(!_size.chars().allMatch(Character::isDigit)){
                    future.completeExceptionally(new MarionetteException(String.format("\"%s\" is not numeric", _size)));
                    return;
                }
                int size = Integer.parseInt(_size, 10);
                ByteBuffer bigBuf = ByteBuffer.allocate(size);
                buf.position(pos + 1);
                bigBuf.put(buf);
                channel.read(bigBuf
                , TIMEOUT
                , TimeUnit.SECONDS
                , future
                , new CompletionHandler<Integer, CompletableFuture>() {
                    @Override
                    public void completed(Integer len, CompletableFuture future) {
                        if(!bigBuf.hasRemaining()){
                            bigBuf.flip();
                            String result;
                            try{
                                result = charsetDecoder.decode(bigBuf).toString();
                            } catch(CharacterCodingException e){
                                throw new MarionetteException(e);
                            }
                            LOG.logp(Level.FINER, CLASS, "readAsync", String.format("RETURN %s", result.length() > 55 ? result.substring(0, 55).concat("...") : result));
                            future.complete(result);
                        } else {
                            channel.read(bigBuf, future, this);
                        }
                    }

                    @Override
                    public void failed(Throwable e, CompletableFuture future) {
                        future.completeExceptionally(e);
                    }
                });
            }

            @Override
            public void failed(Throwable e, CompletableFuture future) {
                future.completeExceptionally(e);
            }
        });
        return ret;
    }
    
    private CompletableFuture<String> writeAsync(String command){
        LOG.entering(CLASS, "writeAsync", command);
        //CompletableFuture<String> ret = new CompletableFuture<>();
        channel.write(ByteBuffer.wrap(String.format("%d:%s", command.length()
        , command).getBytes())
        , TIMEOUT
        , TimeUnit.SECONDS
        , null
        , new CompletionHandler<Integer, CompletableFuture>() {
            @Override
            public void completed(Integer result, CompletableFuture future) {
                futureQueue.complete("");
            }

            @Override
            public void failed(Throwable e, CompletableFuture future) {
                LOG.throwing(CLASS, "failed", e);
                futureQueue.completeExceptionally(e);
            }
        });
        return futureQueue = futureQueue.thenCompose(s -> readAsync());
    }

    @Override
    public CompletableFuture<String> getElementAttribute(String elementId, String attribute) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"name\": \"%s\"}]", messageId++, Command.getElementAttribute.getCommand(), elementId, attribute);
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<String> clickElement(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.clickElement.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> singleTap(String elementId, int x, int y) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"x\": %d, \"y\": %d}]", messageId++, Command.singleTap.getCommand(), elementId, x, y);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> getElementText(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.getElementText.getCommand(), elementId);
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<String> sendKeysToElement(String elementId, String text) {        
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();        
        text.codePoints().forEachOrdered(c -> arrayBuilder.add(new String(Character.toChars(c))));
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"value\": %s}]"
        , messageId++, Command.sendKeysToElement.getCommand(), elementId, text.codePoints()
        .collect(
            Json::createArrayBuilder
            , (builder, item) -> builder.add(new String(Character.toChars(item)))
            , (a,b)->{}
        ).build().toString());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> clearElement(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.clearElement.getCommand(), elementId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<Boolean> isElementSelected(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.isElementSelected.getCommand(), elementId);
        return writeAsync(command).thenApply(b -> (Boolean)MarionetteParser.OBJECT.parseFrom(b));
    }

    @Override
    public CompletableFuture<Boolean> isElementEnabled(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.isElementEnabled.getCommand(), elementId);
        return writeAsync(command).thenApply(b -> (Boolean)MarionetteParser.OBJECT.parseFrom(b));
    }

    @Override
    public CompletableFuture<Boolean> isElementDisplayed(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.isElementDisplayed.getCommand(), elementId);
        return writeAsync(command).thenApply(b -> (Boolean)MarionetteParser.OBJECT.parseFrom(b));
    }

    @Override
    public CompletableFuture<String> getElementTagName(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.getElementTagName.getCommand(), elementId);
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<Rectangle2D> getElementRectangle(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.getElementRect.getCommand(), elementId);
        return writeAsync(command).thenApply(r -> MarionetteParser.RECTANGLE.parseFrom(r));
    }

    @Override
    public CompletableFuture<String> getElementValueOfCssProperty(String elementId, String property) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"propertyName\": \"%s\"}]", messageId++, Command.getElementValueOfCssProperty.getCommand(), elementId, property);
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<String> acceptDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.acceptDialog.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> dismissDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.dismissDialog.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> getTextFromDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getTextFromDialog.getCommand());
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<String> sendKeysToDialog(String text) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\"}]", messageId++, Command.sendKeysToDialog.getCommand(), text);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> quitApplication(List<String> flags) {
        CompletableFuture<String> ret = new CompletableFuture<>();        
        if(!channel.isOpen()){
            ret.complete("[1,0,null,{}]");
            return ret;
        }
        String command = String.format("[0, %d, \"%s\", {\"flags\": %s}]"
        , messageId++, Command.quitApplication.getCommand(), (null == flags) ? "[]" : flags.stream().collect(Collectors.joining("\", \"", "[\"", "\"]")));
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
    public CompletableFuture<String> newSession(String sessionId) {
        String command = String.format("[0, %d, \"%s\", {\"capabilities\": {\"acceptSslCerts\":true}, \"sessionId\": \"%s\"}]", messageId++, Command.newSession.getCommand(), sessionId);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> newSession() {
        String command = String.format("[0, %d, \"%s\", {\"capabilities\": {\"acceptSslCerts\":true}, \"sessionId\": null}]", messageId++, Command.newSession.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> setTestName(String testName) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\"}]", messageId++, Command.setTestName.getCommand(), testName);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> deleteSession() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.deleteSession.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> setScriptTimeout(Duration timeout) {
        String command = String.format("[0, %d, \"%s\", {\"ms\": %d}]", messageId++, Command.setScriptTimeout.getCommand(), timeout.toMillis());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> setSearchTimeout(Duration timeout) {
        String command = String.format("[0, %d, \"%s\", {\"ms\": %d}]", messageId++, Command.setSearchTimeout.getCommand(), timeout.toMillis());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> getWindowHandle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowHandle.getCommand());
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<String> getCurrentChromeWindowHandle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCurrentChromeWindowHandle.getCommand());
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<Point2D> getWindowPosition() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowPosition.getCommand());
        return writeAsync(command).thenApply(p -> MarionetteParser.POINT.parseFrom(p));
    }

    /**
     * 
     * @param <T>
     * @param pointArg ex. {x:0, y:0}
     * @return 
     */
    @Override
    public CompletableFuture<String> setWindowPosition(Point2D pointArg) {
        String command = String.format("[0, %d, \"%s\", %s]", messageId++, Command.setWindowPosition.getCommand(), pointArg);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> getTitle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getTitle.getCommand());
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<List<String>> getWindowHandles() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowHandles.getCommand());
        return writeAsync(command)
        .thenApply(MarionetteParser.ARRAY::parseFrom)
        .thenApply(a -> a.stream().map(MarionetteParser.STRING::parseFrom).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<String>> getChromeWindowHandles() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getChromeWindowHandles.getCommand());
        return writeAsync(command)
        .thenApply(MarionetteParser.ARRAY::parseFrom)
        .thenApply(a -> a.stream().map(MarionetteParser.STRING::parseFrom).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<String> getPageSource() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getPageSource.getCommand());
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<String> close() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.close.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> closeChromeWindow() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.closeChromeWindow.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> setContext(Context context) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\"}]", messageId++, Command.setContext.getCommand(), context);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<Context> getContext() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getContext.getCommand());
        return writeAsync(command).thenApply(MarionetteParser.CONTEXT::parseFrom);
    }

    @Override
    public CompletableFuture<String> switchToWindow(String id) {
        String command = String.format("[0, %d, \"%s\", {\"name\": \"%s\"}]", messageId++, Command.switchToWindow.getCommand(), id);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> getActiveFrame() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getActiveFrame.getCommand());
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);        
    }

    @Override
    public CompletableFuture<String> switchToParentFrame() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.switchToParentFrame.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> switchToFrame(String id) {
        String command = String.format("[0, %d, \"%s\", {\"focus\": \"true\", \"id\": \"%s\"}]", messageId++, Command.switchToFrame.getCommand(), id);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> switchToShadowRoot(String id) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.switchToShadowRoot.getCommand(), id);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> getCurrentUrl() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCurrentUrl.getCommand());
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<String> getWindowType() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowType.getCommand());
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<String> get(String url) {
        String command = String.format("[0, %d, \"%s\", {\"url\": \"%s\"}]", messageId++, Command.get.getCommand(), url);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> timeouts(Timeout timeout, Duration time) {
        String command = String.format("[0, %d, \"%s\", {\"type\": \"%s\", \"ms\", %d}]", messageId++, Command.get.getCommand(), timeout, time.toMillis());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> goBack() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.goBack.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> goForward() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.goForward.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> refresh() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.refresh.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> executeJsScript(String script, String args, boolean async, boolean newSandbox, Duration scriptTimeout, Duration inactivityTimeout) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\", \"args\": %s, \"async\": %s, \"newSandbox\": %s, \"scriptTimeout\": %d, \"inactivityTimeout\": %d, \"filename\": null, \"line\": null}]"
        , messageId++, Command.executeJsScript.getCommand(), script, args, async, newSandbox, scriptTimeout.toMillis(), inactivityTimeout.toMillis());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> executeScript(String script, String args, boolean newSandbox, Duration scriptTimeout) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\", \"args\": %s, \"newSandbox\": %s, \"sandbox\": \"default\", \"scriptTimeout\": %d, \"filename\": null, \"line\": null}]"
        , messageId++, Command.executeScript.getCommand(), script, args, newSandbox, scriptTimeout.toMillis());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> executeAsyncScript(String script, String args, boolean newSandbox, Duration scriptTimeout, boolean debug) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\", \"args\": %s, \"newSandbox\": %s, \"sandbox\": null, \"scriptTimeout\": %d, \"line\": null, \"filename\": null, \"debug_script\": %s}]"
        , messageId++, Command.executeAsyncScript.getCommand(), script, args, newSandbox, scriptTimeout.toMillis(), debug);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> findElement(SearchMethod method, String value) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\", \"using\": \"%s\"}]", messageId++, Command.findElement.getCommand(), value, method);
        return writeAsync(command).thenApply(e -> MarionetteParser.ELEMENT.parseFrom(e));
    }

    @Override
    public CompletableFuture<List<String>> findElements(SearchMethod method, String value) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\", \"using\": \"%s\"}]", messageId++, Command.findElements.getCommand(), value, method);
        return writeAsync(command)
        .thenApply(elements -> MarionetteParser.ARRAY.parseFrom(elements))
        .thenApply(e -> e.stream().map(ElementParser::toElement).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<String> getActiveElement() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getActiveElement.getCommand());
        return writeAsync(command).thenApply(ElementParser::toElement);
    }

    @Override
    public CompletableFuture<String> log(LogLevel level, String message) {
        String command = String.format("[0, %d, \"%s\", {\"level\": \"%s\", \"value\": \"%s\"}]", messageId++, Command.log.getCommand(), level, message);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<List<String>> getLogs() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getLogs.getCommand());
        return writeAsync(command)
        .thenApply(MarionetteParser.ARRAY::parseFrom)
        .thenApply(a -> a.stream().map(MarionetteParser.STRING::parseFrom).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<String> importScript(String script) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\"}]", messageId++, Command.importScript.getCommand(), script);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> clearImportedScripts() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.clearImportedScripts.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> addCookie(String cookie) {
        String command = String.format("[0, %d, \"%s\", {\"cookie\": \"%s\"}]", messageId++, Command.addCookie.getCommand(), cookie);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> deleteAllCookies() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.deleteAllCookies.getCommand());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> deleteCookie(String name) {
        String command = String.format("[0, %d, \"%s\", {\"name\": \"%s\"}]", messageId++, Command.deleteCookie.getCommand(), name);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<List<String>> getCookies() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCookies.getCommand());
        return writeAsync(command)
        .thenApply(MarionetteParser.ARRAY::parseFrom)
        .thenApply(a -> a.stream().map(MarionetteParser.STRING::parseFrom).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<String> takeScreenshot() {
        String command = String.format("[0, %d, \"%s\", {\"id\": null, \"highlights\": null, \"full\": true}]", messageId++, Command.takeScreenshot.getCommand());
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<String> takeScreenshot(List<String> elementIds) {
        String command = String.format("[0, %d, \"%s\", {\"id\": null, \"highlights\": %s, \"full\": true}]"
        , messageId++, Command.takeScreenshot.getCommand(), elementIds.stream().collect(Collectors.joining("\", \"", "[\"", "\"]")));
        return writeAsync(command).thenApply(MarionetteParser.STRING::parseFrom);
    }

    @Override
    public CompletableFuture<Orientation> getScreenOrientation() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getScreenOrientation.getCommand());
        return writeAsync(command).thenApply(o -> MarionetteParser.ORIENTATION.parseFrom(o));
    }

    @Override
    public CompletableFuture<String> setScreenOrientation(Orientation orientation) {
        String command = String.format("[0, %d, \"%s\", {\"orientation\": \"%s\"}]", messageId++, Command.setScreenOrientation.getCommand(), orientation);
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<Dimension2D> getWindowSize() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowSize.getCommand());
        return writeAsync(command).thenApply(d -> MarionetteParser.DIMENSION.parseFrom(d));
    }

    @Override
    public CompletableFuture<String> setWindowSize(Dimension2D size) {
        String command = String.format("[0, %d, \"%s\", {\"width\": %f, \"height\": %f}]", messageId++, Command.setWindowSize.getCommand(), size.getWidth(), size.getHeight());
        return writeAsync(command);
    }

    @Override
    public CompletableFuture<String> maximizeWindow() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.maximizeWindow.getCommand());
        return writeAsync(command);
    }    
}
