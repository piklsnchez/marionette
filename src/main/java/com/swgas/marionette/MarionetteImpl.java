package com.swgas.marionette;

import com.swgas.exception.MarionetteException;
import com.swgas.parser.FromStringParser;
import com.swgas.parser.ToStringParser;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.NotYetConnectedException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MarionetteImpl implements Marionette {
    private static final String CLASS = MarionetteImpl.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private AsynchronousSocketChannel channel;
    private int messageId = 0;
    
    public MarionetteImpl(){
        this("localhost", 2828);
    }
    
    public MarionetteImpl(String host, int port){
        throw new MarionetteException(new NoSuchMethodException("Use Factory"));
    }
    
    protected MarionetteImpl(AsynchronousSocketChannel channel){
        this.channel = channel;
        readAsync(-1).join();
    }
    
    @Override
    protected void finalize() throws Throwable{        
        super.finalize();
        channel.close();
    }
    
    private CompletableFuture<String> readAsync(int id){
        CompletableFuture<String> ret = new CompletableFuture<>();
        byte[] byteBuf = new byte[8];
        ByteBuffer buf = ByteBuffer.wrap(byteBuf);
        channel.read(buf, ret, new CompletionHandler<Integer, CompletableFuture>() {
            @Override
            public void completed(Integer len, CompletableFuture future) {
                StringBuilder builder = new StringBuilder();
                int pos;
                for(pos = 0; byteBuf[pos] != ':'; pos++){}
                String _size = new String(byteBuf, 0, pos);
                if(!_size.chars().allMatch(Character::isDigit)){
                    future.completeExceptionally(new MarionetteException(String.format("\"%s\" is not numeric", _size)));
                    return;
                }
                int size = Integer.parseInt(_size, 10);
                builder.append(new String(byteBuf, pos + 1, len - pos - 1));
                ByteBuffer bigBuf = ByteBuffer.allocate(size);
                channel.read(bigBuf, future, new CompletionHandler<Integer, CompletableFuture>() {
                    @Override
                    public void completed(Integer len, CompletableFuture future) {
                        bigBuf.flip();
                        byte[] b = new byte[len];
                        bigBuf.get(b);
                        String result = builder.append(new String(b)).toString();
                        LOG.info(String.format("readAsync: messageId: %d: %s", id, result));
                        future.complete(result);
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
    
    private <T> T read(){
        LOG.entering(CLASS, "read");
        StringBuilder result = new StringBuilder();
        byte[] byteBuf = new byte[8];
        ByteBuffer buf = ByteBuffer.wrap(byteBuf);
        try{
            Integer r = channel.read(buf).get(30, TimeUnit.SECONDS);//block
            //LOG.info(String.format("read: \"%s\"(%d) from socket", new String(byteBuf), r));
            for(int i = 0; i < byteBuf.length; i++){
                if(byteBuf[i] == ':'){
                    String sLength = new String(byteBuf, 0, i);
                    if(!sLength.chars().allMatch(Character::isDigit)){
                        throw new MarionetteException(String.format("\"%s\" is not numeric", sLength));
                    }
                    int length = Integer.parseInt(sLength, 10);
                    int skip = byteBuf.length - 1 - i;
                    result.append(new String(byteBuf, i + 1, skip));
                    buf = ByteBuffer.allocate(length - skip);
                    r = channel.read(buf).get(30, TimeUnit.SECONDS);//block
                    //LOG.info(String.format("read %d more bytes from socket", r));
                    buf.flip();
                    result.append(StandardCharsets.UTF_8.decode(buf));
                    buf.clear();
                    break;
                }
            }
        } catch(NotYetConnectedException e){
            throw new MarionetteException(e);
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        int len = result.length();
        LOG.info(String.format("read %d bytes", len));
        LOG.exiting(CLASS, "read", (len > 55) ? String.format("%s...%s", result.substring(0, 55), result.substring(len -3)) : result);
        return null;
    }
    
    private CompletableFuture<Integer> writeAsync(String command){
        LOG.entering(CLASS, "writeAsync", command);
        CompletableFuture<Integer> ret = new CompletableFuture<>();
        channel.write(ByteBuffer.wrap(String.format("%d:%s", command.length(), command).getBytes()), ret, new CompletionHandler<Integer, CompletableFuture>() {
            @Override
            public void completed(Integer result, CompletableFuture future) {
                future.complete(result);
            }

            @Override
            public void failed(Throwable e, CompletableFuture future) {
                future.completeExceptionally(e);
            }
        });
        return ret;
    }
    
    private void write(String command){
        LOG.entering(CLASS, "write", command);
        try{
            Integer w = channel.write(ByteBuffer.wrap(String.format("%d:%s", command.length(), command).getBytes())).get(30, TimeUnit.SECONDS);
            LOG.info(String.format("wrote %d bytes", w));
        }catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        LOG.exiting(CLASS, "write");
    }

    @Override
    public CompletableFuture<String> getElementAttribute(String elementId, String attribute) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"name\": \"%s\"}]", messageId, Command.getElementAttribute.getCommand(), elementId, attribute);
        return writeAsync(command).thenCompose(i -> readAsync(messageId++));
    }

    @Override
    public CompletableFuture<String> clickElement(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId, Command.clickElement.getCommand(), elementId);
        return writeAsync(command).thenCompose(i -> readAsync(messageId++));
    }

    @Override
    public CompletableFuture<String> singleTap(String elementId, int x, int y) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"x\": %d, \"y\": %d}]", messageId++, Command.singleTap.getCommand(), elementId, x, y);
        write(command);
        return read();
    }

    @Override
    public <T> T singleTap(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"x\": %d, \"y\": %d}]", messageId++, Command.singleTap.getCommand(), elementId, 0, 0);
        write(command);
        return read();
    }

    @Override
    public <T> T getElementText(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.getElementText.getCommand(), elementId);
        write(command);
        return read();
    }

    @Override
    public <T> T sendKeysToElement(String elementId, String text) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"value\": %s}]"
        , messageId++, Command.sendKeysToElement.getCommand(), elementId, text.chars().mapToObj(c -> Objects.toString((char)c)).collect(Collectors.joining("\", \"", "[\"", "\"]")));
        write(command);
        return read();
    }

    @Override
    public <T> T clearElement(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.clearElement.getCommand(), elementId);
        write(command);
        return read();
    }

    @Override
    public <T> T isElementSelected(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.isElementSelected.getCommand(), elementId);
        write(command);
        return read();
    }

    @Override
    public <T> T isElementEnabled(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.isElementEnabled.getCommand(), elementId);
        write(command);
        return read();
    }

    @Override
    public <T> T isElementDisplayed(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.isElementDisplayed.getCommand(), elementId);
        write(command);
        return read();
    }

    @Override
    public <T> T getElementTagName(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.getElementTagName.getCommand(), elementId);
        write(command);
        return read();
    }

    @Override
    public <T> T getElementRectangle(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.getElementRect.getCommand(), elementId);
        write(command);
        return read();
    }

    @Override
    public <T> T getElementValueOfCssProperty(String elementId, String property) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\", \"propertyName\": \"%s\"}]", messageId++, Command.getElementValueOfCssProperty.getCommand(), elementId, property);
        write(command);
        return read();
    }

    @Override
    public <T> T acceptDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.acceptDialog.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T dismissDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.dismissDialog.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T getTextFromDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getTextFromDialog.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T sendKeysToDialog(String text) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\"}]", messageId++, Command.sendKeysToDialog.getCommand(), text);
        write(command);
        return read();
    }

    @Override
    public CompletableFuture<String> quitApplication(List<String> flags) {
        CompletableFuture<String> ret = new CompletableFuture<>();
        String command = String.format("[0, %d, \"%s\", {\"flags\": %s}]"
        , messageId, Command.quitApplication.getCommand(), flags.stream().collect(Collectors.joining("\", \"", "[\"", "\"]")));
        return writeAsync(command).thenCompose(i -> readAsync(messageId)).thenCompose(s -> {
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
        String command = String.format("[0, %d, \"%s\", {\"capabilities\": null, \"sessionId\": \"%s\"}]", messageId, Command.newSession.getCommand(), sessionId);
        return writeAsync(command).thenCompose(i -> readAsync(messageId++));
    }

    @Override
    public CompletableFuture<String> newSession() {
        String command = String.format("[0, %d, \"%s\", {\"capabilities\": null, \"sessionId\": null}]", messageId, Command.newSession.getCommand());
        return writeAsync(command).thenCompose(i -> readAsync(messageId++));
    }

    @Override
    public <T> T setTestName(String testName) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\"}]", messageId++, Command.setTestName.getCommand(), testName);
        write(command);
        return read();
    }

    @Override
    public <T> T deleteSession() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.deleteSession.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T setScriptTimeout(Duration timeout) {
        String command = String.format("[0, %d, \"%s\", {\"ms\": %d}]", messageId++, Command.setScriptTimeout.getCommand(), timeout.toMillis());
        write(command);
        return read();
    }

    @Override
    public <T> T setSearchTimeout(Duration timeout) {
        String command = String.format("[0, %d, \"%s\", {\"ms\": %d}]", messageId++, Command.setSearchTimeout.getCommand(), timeout.toMillis());
        write(command);
        return read();
    }

    @Override
    public <T> T getWindowHandle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowHandle.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T getCurrentChromeWindowHandle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCurrentChromeWindowHandle.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T getWindowPosition() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowPosition.getCommand());
        write(command);
        return read();
    }

    /**
     * 
     * @param <T>
     * @param pointArg ex. {x:0, y:0}
     * @return 
     */
    @Override
    public <T> T setWindowPosition(String pointArg) {
        String command = String.format("[0, %d, \"%s\", %s]", messageId++, Command.setWindowPosition.getCommand(), pointArg);
        write(command);
        return read();
    }

    @Override
    public <T> T getTitle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getTitle.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T getWindowHandles() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowHandles.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T getChromeWindowHandles() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getChromeWindowHandles.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T getPageSource() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getPageSource.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T close() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.close.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T closeChromeWindow() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.closeChromeWindow.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T setContext(Context context) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\"}]", messageId++, Command.setContext.getCommand(), context);
        write(command);
        return read();
    }

    @Override
    public <T> T getContext() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getContext.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T switchToWindow(String id) {
        String command = String.format("[0, %d, \"%s\", {\"name\": \"%s\"}]", messageId++, Command.switchToWindow.getCommand(), id);
        write(command);
        return read();
    }

    @Override
    public <T> T getActiveFrame() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getActiveFrame.getCommand());
        write(command);
        return read();
        
    }

    @Override
    public <T> T switchToParentFrame() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.switchToParentFrame.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T switchToFrame(String id) {
        String command = String.format("[0, %d, \"%s\", {\"focus\": \"true\", \"id\": \"%s\"}]", messageId++, Command.switchToFrame.getCommand(), id);
        write(command);
        return read();
    }

    @Override
    public <T> T switchToShadowRoot(String id) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.switchToShadowRoot.getCommand(), id);
        write(command);
        return read();
    }

    @Override
    public CompletableFuture<String> getCurrentUrl() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId, Command.getCurrentUrl.getCommand());
        return writeAsync(command).thenCompose(i -> readAsync(messageId++));
    }

    @Override
    public <T> T getWindowType() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowType.getCommand());
        write(command);
        return read();
    }

    @Override
    public CompletableFuture<String> get(String url) {
        String command = String.format("[0, %d, \"%s\", {\"url\": \"%s\"}]", messageId, Command.get.getCommand(), url);
        return writeAsync(command).thenCompose(i -> readAsync(messageId++));
    }

    @Override
    public <T> T timeouts(Timeout timeout, Duration time) {
        String command = String.format("[0, %d, \"%s\", {\"type\": \"%s\", \"ms\", %d}]", messageId++, Command.get.getCommand(), timeout, time.toMillis());
        write(command);
        return read();
    }

    @Override
    public <T> T goBack() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.goBack.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T goForward() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.goForward.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T refresh() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.refresh.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T executeJsScript(String script, String args, boolean async, boolean newSandbox, Duration scriptTimeout, Duration inactivityTimeout) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\", \"args\": %s, \"async\": %s, \"newSandbox\": %s, \"scriptTimeout\": %d, \"inactivityTimeout\": %d, \"filename\": null, \"line\": null}]"
        , messageId++, Command.executeJsScript.getCommand(), script, args, async, newSandbox, scriptTimeout.toMillis(), inactivityTimeout.toMillis());
        write(command);
        return read();
    }

    @Override
    public <T> T executeScript(String script, String args, boolean newSandbox, Duration scriptTimeout) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\", \"args\": %s, \"newSandbox\": %s, \"sandbox\": null, \"scriptTimeout\": %d, \"filename\": null, \"line\": null}]"
        , messageId++, Command.executeScript.getCommand(), script, args, newSandbox, scriptTimeout.toMillis());
        write(command);
        return read();
    }

    @Override
    public <T> T executeAsyncScript(String script, String args, boolean newSandbox, Duration scriptTimeout, boolean debug) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\", \"args\": %s, \"newSandbox\": %s, \"sandbox\": null, \"scriptTimeout\": %d, \"line\": null, \"filename\": null, \"debug_script\": %s}]"
        , messageId++, Command.executeAsyncScript.getCommand(), script, args, newSandbox, scriptTimeout.toMillis(), debug);
        write(command);
        return read();
    }

    @Override
    public CompletableFuture<String> findElement(SearchMethod method, String value) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\", \"using\": \"%s\"}]", messageId, Command.findElement.getCommand(), value, method);
        return writeAsync(command).thenCompose(i -> readAsync(messageId++));
    }

    @Override
    public <T> T findElements(SearchMethod method, String value) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\", \"using\": \"%s\"}]", messageId++, Command.findElements.getCommand(), value, method);
        write(command);
        return read();
    }

    @Override
    public <T> T getActiveElement() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getActiveElement.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T log(LogLevel level, String message) {
        String command = String.format("[0, %d, \"%s\", {\"level\": \"%s\", \"value\": \"%s\"}]", messageId++, Command.log.getCommand(), level, message);
        write(command);
        return read();
    }

    @Override
    public List<String> getLogs() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getLogs.getCommand());
        write(command);
        return Stream.of((String)read()).collect(Collectors.toList());
    }

    @Override
    public <T> T importScript(String script) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\"}]", messageId++, Command.importScript.getCommand(), script);
        write(command);
        return read();
    }

    @Override
    public <T> T clearImportedScripts() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.clearImportedScripts.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T addCookie(String cookie) {
        String command = String.format("[0, %d, \"%s\", {\"cookie\": \"%s\"}]", messageId++, Command.addCookie.getCommand(), cookie);
        write(command);
        return read();
    }

    @Override
    public <T> T deleteAllCookies() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.deleteAllCookies.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T deleteCookie(String name) {
        String command = String.format("[0, %d, \"%s\", {\"name\": \"%s\"}]", messageId++, Command.deleteCookie.getCommand(), name);
        write(command);
        return read();
    }

    @Override
    public <T> T getCookies() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCookies.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T takeScreenshot() {
        String command = String.format("[0, %d, \"%s\", {\"id\": null, \"highlights\": null, \"full\": true}]", messageId++, Command.takeScreenshot.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T takeScreenshot(List<String> elementIds) {
        String command = String.format("[0, %d, \"%s\", {\"id\": null, \"highlights\": %s, \"full\": true}]"
        , messageId++, Command.takeScreenshot.getCommand(), elementIds.stream().collect(Collectors.joining("\", \"", "[\"", "\"]")));
        write(command);
        return read();
    }

    @Override
    public Orientation getScreenOrientation() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getScreenOrientation.getCommand());
        write(command);
        return Orientation.valueOf(read());
    }

    @Override
    public <T> T setScreenOrientation(Orientation orientation) {
        String command = String.format("[0, %d, \"%s\", {\"orientation\": \"%s\"}]", messageId++, Command.setScreenOrientation.getCommand(), orientation);
        write(command);
        return read();
    }

    @Override
    public <T> T getWindowSize() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowSize.getCommand());
        write(command);
        return read();
    }

    @Override
    public <T> T setWindowSize(String size) {
        int width = 0;
        int height = 0;
        String command = String.format("[0, %d, \"%s\", {\"width\": %d, \"height\": %d}]", messageId++, Command.setWindowSize.getCommand(), width, height);
        write(command);
        return read();
    }

    @Override
    public <T> T maximizeWindow() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.maximizeWindow.getCommand());
        write(command);
        return read();
    }    
}
