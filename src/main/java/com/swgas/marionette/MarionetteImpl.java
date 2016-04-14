package com.swgas.marionette;

import com.swgas.exception.MarionetteException;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpCookie;
import java.net.Socket;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLElement;


public class MarionetteImpl implements Marionette {
    private static final String CLASS = MarionetteImpl.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String sessionId;
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
    public String getElementAttribute(HTMLElement element, String attribute) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s, \"name\": %s}]", messageId++, Command.getElementAttribute.getCommand(), element.getId(), attribute);
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public void clickElement(HTMLElement element) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s}]", messageId++, Command.clickElement.getCommand(), element.getId());
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void singleTap(HTMLElement element, Point point) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s, \"x\": %d, \"y\": %d}]", messageId++, Command.singleTap.getCommand(), element.getId(), point.x, point.y);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void singleTap(HTMLElement element) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s, \"x\": %d, \"y\": %d}]", messageId++, Command.singleTap.getCommand(), element.getId(), 0, 0);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public String getElementText(HTMLElement element) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s}]", messageId++, Command.getElementText.getCommand(), element.getId());
        out.format("%d:%s", command.length(), command);
        return read();
    }

    @Override
    public void sendKeysToElement(HTMLElement element, String text) {
        String command = String.format("[0, %d, \"%s\", {\"id\": %s, \"value\": %s}]", messageId++, Command.getElementText.getCommand(), element.getId(), text);
        out.format("%d:%s", command.length(), command);
        read();
    }

    @Override
    public void clearElement(HTMLElement element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isElementSelected(HTMLElement element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isElementEnabled(HTMLElement element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isElementDisplayed(HTMLElement element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getElementTagName(HTMLElement element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Rectangle getElementRect(HTMLElement element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getElementValueOfCssProperty(HTMLElement element, String property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void acceptDialog() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dismissDialog() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTextFromDialog() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendKeysToDialog(String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void quitApplication(List<String> flags) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object newSession(String sessionId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object newSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTestName(String testName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setScriptTimeout(Duration timeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSearchTimeout(Duration timeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getWindowHandle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCurrentChromeWindowHandle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Point getWindowPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWindowPosition(Point point) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getWindowHandles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getChromeWindowHandles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Document getPageSource() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeChromeWindow() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setContext(Context context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Context getContext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchToWindow(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HTMLElement getActiveFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchToParentFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchToFrame(HTMLElement element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object switchToShadowRoot(HTMLElement element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCurrentUrl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getWindowType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void get(String url) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void timeouts(Timeout timeout, Duration time) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void goBack() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void goForward() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object executeJsScript(String script, List<String> args, boolean async, boolean newSandbox, Duration scriptTimeout, Duration inactivityTimeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object executeScript(String script, List<String> args, boolean async, boolean newSandbox, Duration scriptTimeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object executeAsyncScript(String script, List<String> args, boolean async, boolean newSandbox, Duration scriptTimeout, boolean debug) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HTMLElement findElement(SearchMethod method, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<HTMLElement> findElements(SearchMethod method, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HTMLElement getActiveElement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void log(LogLevel level, String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getLogs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void importScript(String file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearImportedScripts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addCookie(HttpCookie cookie) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteAllCookies() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteCookie(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<HttpCookie> getCookies() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String takeScreenshot() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String takeScreenshot(List<HTMLElement> elements) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Orientation getScreenOrientation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setScreenOrientation(Orientation orientation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Rectangle getWindowSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object setWindowSize(Rectangle size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object maximizeWindow() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
