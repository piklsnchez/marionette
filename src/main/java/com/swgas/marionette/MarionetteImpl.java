package com.swgas.marionette;

import com.swgas.exception.MarionetteException;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MarionetteImpl implements Marionette {
    private static final String CLASS = MarionetteImpl.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int messageId = 0;
    
    public MarionetteImpl(){
        this("localhost", 2828);
    }
    public MarionetteImpl(String host, int port){
        try{
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            LOG.info(read());
        } catch(IOException e){
            throw new MarionetteException(e);
        }
    }
    @Override
    public void finalize() throws Throwable{        
        super.finalize();
        socket.close();
    }
    
    private String read(){
        LOG.entering(CLASS, "read");
        String result = "";
        try{
            int i = 0;
            char[] buf = new char[555];
            while ((buf[i++] = (char) in.read()) != ':') {
                //read into buffer
            }
            i = in.read(buf, 0, Integer.parseInt(new String(buf, 0, --i), 10));
            result = new String(buf, 0, i);
        } catch(IOException e){
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        LOG.exiting(CLASS, "read", result);
        return result;
    }

    @Override
    public String getElementAttribute(String elementId, String attribute) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s, \"name\": %s}]", messageId++, Command.getElementAttribute.getCommand(), elementId, attribute);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public void clickElement(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s}]", messageId++, Command.clickElement.getCommand(), elementId);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void singleTap(String elementId, Point point) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s, \"x\": %d, \"y\": %d}]", messageId++, Command.singleTap.getCommand(), elementId, point.x, point.y);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void singleTap(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s, \"x\": %d, \"y\": %d}]", messageId++, Command.singleTap.getCommand(), elementId, 0, 0);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public String getElementText(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s}]", messageId++, Command.getElementText.getCommand(), elementId);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public void sendKeysToElement(String elementId, String text) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s, \"value\": %s}]", messageId++, Command.sendKeysToElement.getCommand(), elementId, text);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void clearElement(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s}]", messageId++, Command.clearElement.getCommand(), elementId);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public boolean isElementSelected(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s}]", messageId++, Command.isElementSelected.getCommand(), elementId);
        out.format("%d:%s", command.length(), command);
        return Boolean.parseBoolean(read());
    }

    @Override
    public boolean isElementEnabled(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s}]", messageId++, Command.isElementEnabled.getCommand(), elementId);
        out.format("%d:%s", command.length(), command);
        return Boolean.parseBoolean(read());
    }

    @Override
    public boolean isElementDisplayed(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s}]", messageId++, Command.isElementDisplayed.getCommand(), elementId);
        out.format("%d:%s", command.length(), command);
        return Boolean.parseBoolean(read());
    }

    @Override
    public String getElementTagName(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s}]", messageId++, Command.getElementTagName.getCommand(), elementId);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String getElementRectangle(String elementId) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s}]", messageId++, Command.getElementRect.getCommand(), elementId);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String getElementValueOfCssProperty(String elementId, String property) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s, \"propertyName\": %s}]", messageId++, Command.getElementValueOfCssProperty.getCommand(), elementId, property);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public void acceptDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.acceptDialog.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void dismissDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.dismissDialog.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public String getTextFromDialog() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getTextFromDialog.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public void sendKeysToDialog(String text) {
        String command = String.format("[0, %d, \"%s\", {\"value\": %s}]", messageId++, Command.sendKeysToDialog.getCommand(), text);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void quitApplication(List<String> flags) {
        String command = String.format("[0, %d, \"%s\", {\"flags\": %s}]", messageId++, Command.quitApplication.getCommand(), flags.stream().collect(Collectors.joining("\", \"", "[\"", "\"]")));
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public String newSession(String sessionId) {
        String command = String.format("[0, %d, \"%s\", {\"capabilities\": null, \"sessionId\": \"%s\"}]", messageId++, Command.newSession.getCommand(), sessionId);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String newSession() {
        String command = String.format("[0, %d, \"%s\", {\"capabilities\": null, \"sessionId\": null}]", messageId++, Command.newSession.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public void setTestName(String testName) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\"}]", messageId++, Command.setTestName.getCommand(), testName);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void deleteSession() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.deleteSession.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void setScriptTimeout(Duration timeout) {
        String command = String.format("[0, %d, \"%s\", {\"ms\": %d}]", messageId++, Command.setScriptTimeout.getCommand(), timeout.toMillis());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void setSearchTimeout(Duration timeout) {
        String command = String.format("[0, %d, \"%s\", {\"ms\": %d}]", messageId++, Command.setSearchTimeout.getCommand(), timeout.toMillis());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public String getWindowHandle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowHandle.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String getCurrentChromeWindowHandle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCurrentChromeWindowHandle.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public Point getWindowPosition() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowPosition.getCommand());
        out.format("%d:%s", command.length(), command);
        String result = read();
        return new Point();
    }

    @Override
    public void setWindowPosition(Point point) {
        String command = String.format("[0, %d, \"%s\", {\"x\": %d, \"y\": %d}]", messageId++, Command.setWindowPosition.getCommand(), point.x, point.y);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public String getTitle() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getTitle.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public List<String> getWindowHandles() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowHandles.getCommand());
        out.format("%d:%s", command.length(), command);
        return Stream.of(read()).collect(Collectors.toList());
    }

    @Override
    public List<String> getChromeWindowHandles() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getChromeWindowHandles.getCommand());
        out.format("%d:%s", command.length(), command);
        return Stream.of(read()).collect(Collectors.toList());
    }

    @Override
    public String getPageSource() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getPageSource.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public void close() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.close.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void closeChromeWindow() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.closeChromeWindow.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void setContext(Context context) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\"}]", messageId++, Command.setContext.getCommand(), context);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public Context getContext() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getContext.getCommand());
        out.format("%d:%s", command.length(), command);
        return Context.valueOf(read());
    }

    @Override
    public void switchToWindow(String id) {
        String command = String.format("[0, %d, \"%s\", {\"name\": \"%s\"}]", messageId++, Command.switchToWindow.getCommand(), id);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public String getActiveFrame() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getActiveFrame.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
        
    }

    @Override
    public void switchToParentFrame() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.switchToParentFrame.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void switchToFrame(String id) {
        String command = String.format("[0, %d, \"%s\", {\"focus\": \"true\", \"id\": \"%s\"}]", messageId++, Command.switchToFrame.getCommand(), id);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public String switchToShadowRoot(String id) {
        String command = String.format("[0, %d, \"%s\", {\"id\": \"%s\"}]", messageId++, Command.switchToShadowRoot.getCommand(), id);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String getCurrentUrl() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCurrentUrl.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String getWindowType() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowType.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public void get(String url) {
        String command = String.format("[0, %d, \"%s\", {\"url\": \"%s\"}]", messageId++, Command.get.getCommand(), url);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void timeouts(Timeout timeout, Duration time) {
        String command = String.format("[0, %d, \"%s\", {\"type\": \"%s\", \"ms\", %d}]", messageId++, Command.get.getCommand(), timeout, time.toMillis());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void goBack() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.goBack.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void goForward() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.goForward.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void refresh() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.refresh.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public String executeJsScript(String script, List<String> args, boolean async, boolean newSandbox, Duration scriptTimeout, Duration inactivityTimeout) {
        String command = String.format("[0, %d, \"%s\", {\"script\": %s, \"args\": %s, \"async\": %s, \"newSandbox\": %s, \"scriptTimeout\": %d, \"inactivityTimeout\": %d, \"filename\": null, \"line\": null}]"
        , messageId++, Command.executeJsScript.getCommand(), script, args.stream().collect(Collectors.joining("\", \"", "[\"", "\"]")), async, newSandbox, scriptTimeout.toMillis(), inactivityTimeout.toMillis());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String executeScript(String script, List<String> args, boolean newSandbox, Duration scriptTimeout) {
        String command = String.format("[0, %d, \"%s\", {\"script\": %s, \"args\": %s, \"newSandbox\": %s, \"sandbox\": null, \"scriptTimeout\": %d, \"filename\": null, \"line\": null}]"
        , messageId++, Command.executeScript.getCommand(), script, args.stream().collect(Collectors.joining("\", \"", "[\"", "\"]")), newSandbox, scriptTimeout.toMillis());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String executeAsyncScript(String script, List<String> args, boolean newSandbox, Duration scriptTimeout, boolean debug) {
        String command = String.format("[0, %d, \"%s\", {\"script\": %s, \"args\": %s, \"newSandbox\": %s, \"sandbox\": null, \"scriptTimeout\": %d, \"line\": null, \"filename\": null, \"debug_script\": %s}]"
        , messageId++, Command.executeAsyncScript.getCommand(), script, args.stream().collect(Collectors.joining("\", \"", "[\"", "\"]")), newSandbox, scriptTimeout.toMillis(), debug);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String findElement(SearchMethod method, String value) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\", \"using\": \"%s\"}]", messageId++, Command.findElement.getCommand(), method, value);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public List<String> findElements(SearchMethod method, String value) {
        String command = String.format("[0, %d, \"%s\", {\"value\": \"%s\", \"using\": \"%s\"}]", messageId++, Command.findElements.getCommand(), method, value);
        out.format("%d:%s", command.length(), command);
        return Stream.of(read()).collect(Collectors.toList());
    }

    @Override
    public String getActiveElement() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getActiveElement.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public void log(LogLevel level, String message) {
        String command = String.format("[0, %d, \"%s\", {\"level\": \"%s\", \"value\": \"%s\"}]", messageId++, Command.log.getCommand(), level, message);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public List<String> getLogs() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getLogs.getCommand());
        out.format("%d:%s", command.length(), command);
        return Stream.of(read()).collect(Collectors.toList());
    }

    @Override
    public void importScript(String script) {
        String command = String.format("[0, %d, \"%s\", {\"script\": \"%s\"}]", messageId++, Command.importScript.getCommand(), script);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void clearImportedScripts() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.clearImportedScripts.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void addCookie(String cookie) {
        String command = String.format("[0, %d, \"%s\", {\"cookie\": \"%s\"}]", messageId++, Command.addCookie.getCommand(), cookie);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void deleteAllCookies() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.deleteAllCookies.getCommand());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void deleteCookie(String name) {
        String command = String.format("[0, %d, \"%s\", {\"name\": \"%s\"}]", messageId++, Command.deleteCookie.getCommand(), name);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public List<String> getCookies() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getCookies.getCommand());
        out.format("%d:%s", command.length(), command);
        return Stream.of(read()).collect(Collectors.toList());
    }

    @Override
    public String takeScreenshot() {
        String command = String.format("[0, %d, \"%s\", {\"id\": null, \"highlights\": null, \"full\": true}]", messageId++, Command.takeScreenshot.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String takeScreenshot(List<String> elementIds) {
        String command = String.format("[0, %d, \"%s\", {\"id\": null, \"highlights\": %s, \"full\": true}]"
        , messageId++, Command.takeScreenshot.getCommand(), elementIds.stream().collect(Collectors.joining("\", \"", "[\"", "\"]")));
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public Orientation getScreenOrientation() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getScreenOrientation.getCommand());
        out.format("%d:%s", command.length(), command);
        return Orientation.valueOf(read());
    }

    @Override
    public void setScreenOrientation(Orientation orientation) {
        String command = String.format("[0, %d, \"%s\", {\"orientation\": \"%s\"}]", messageId++, Command.setScreenOrientation.getCommand(), orientation);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public String getWindowSize() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.getWindowSize.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String setWindowSize(String size) {
        int width = 0;
        int height = 0;
        String command = String.format("[0, %d, \"%s\", {\"width\": %d, \"height\": %d}]", messageId++, Command.setWindowSize.getCommand(), width, height);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public String maximizeWindow() {
        String command = String.format("[0, %d, \"%s\", {}]", messageId++, Command.maximizeWindow.getCommand());
        out.format("%d:%s", command.length(), command);
        return read();
    }
    
}
