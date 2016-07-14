package com.swgas.marionette;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author ocstest
 */
public interface Marionette {
    public enum Context{
        CONTEXT_CHROME, CONTEXT_CONTENT
    }
    public enum Timeout{
        SEARCH, SCRIPT, PAGE
    }
    public enum SearchMethod{
        ID(""), NAME("name="), CLASS_NAME("class="), TAG_NAME("tag="), CSS_SELECTOR("css="), LINK("link="), PARTIAL_LINK("link~="), XPATH(""), ANON("anon="), ANON_ATTRIBUTE("anonAttr=");

        private String prefix;
        private SearchMethod(String prefix) {
            this.prefix = prefix;
        }
        public int len(){
            return prefix.length();
        }        
        @Override
        public String toString(){
            return name().toLowerCase().replace("_", " ");
        }
    }
    public enum LogLevel{
        ERROR, INFO, DEBUG
    }
    public enum Orientation{
        PORTRAIT_PRIMARY, LANDSCAPE_PRIMARY, PORTRAIT_SECONDARY, LANDSCAPE_SECONDARY
    }
    public static final String WEBELEMENT_KEY = "ELEMENT";
    public static final String W3C_WEBELEMENT_KEY = "element-6066-11e4-a52e-4f735466cecf";
    public CompletableFuture<String> getElementAttribute(String element, String name);
    public CompletableFuture<String> clickElement(String element);
    public CompletableFuture<String> singleTap(String element, int x, int y);
    public <T> T singleTap(String element);
    public <T> T getElementText(String element);
    public <T> T sendKeysToElement(String element, String text);
    public <T> T clearElement(String element);
    public <T> T isElementSelected(String element);
    public <T> T isElementEnabled(String element);
    public <T> T isElementDisplayed(String element);
    public <T> T getElementTagName(String element);
    public <T> T getElementRectangle(String element);
    public <T> T getElementValueOfCssProperty(String element, String property);
    default <T> T actionChain(List<Object> actionChain, String id){return (T)null;}
    default <T> T multiAction(List<Object> multiActions){return (T)null;}
    public <T> T acceptDialog();
    public <T> T dismissDialog();
    public <T> T getTextFromDialog();
    public <T> T sendKeysToDialog(String text);
    public CompletableFuture<String> quitApplication(List<String> flags);
    public CompletableFuture<String> newSession(String sessionId);
    public CompletableFuture<String> newSession();
    public <T> T setTestName(String testName);
    public <T> T deleteSession();
    public <T> T setScriptTimeout(Duration timeout);
    public <T> T setSearchTimeout(Duration timeout);
    public <T> T getWindowHandle();
    public <T> T getCurrentChromeWindowHandle();
    public <T> T getWindowPosition();
    public <T> T setWindowPosition(String point);
    public <T> T getTitle();
    public <T> T getWindowHandles();
    public <T> T getChromeWindowHandles();
    public <T> T getPageSource();
    public <T> T close();
    public <T> T closeChromeWindow();
    public <T> T setContext(Context context);
    public <T> T getContext();
    public <T> T switchToWindow(String id);
    public <T> T getActiveFrame();
    public <T> T switchToParentFrame();
    public <T> T switchToFrame(String element);
    public <T> T switchToShadowRoot(String element);
    public CompletableFuture<String> getCurrentUrl();
    public <T> T getWindowType();
    public CompletableFuture<String> get(String url);
    public <T> T timeouts(Timeout timeout, Duration time);
    public <T> T goBack();
    public <T> T goForward();
    public <T> T refresh();
    public <T> T executeJsScript(String script, String args, boolean async, boolean newSandbox, Duration scriptTimeout, Duration inactivityTimeout);
    public <T> T executeScript(String script, String args, boolean newSandbox, Duration scriptTimeout);
    public <T> T executeAsyncScript(String script, String args, boolean newSandbox, Duration scriptTimeout, boolean debug);
    public CompletableFuture<String> findElement(SearchMethod method, String value);
    public <T> T findElements(SearchMethod method, String value);
    public <T> T getActiveElement();
    public <T> T log(LogLevel level, String message);
    public <T> T getLogs();
    public <T> T importScript(String script);
    public <T> T clearImportedScripts();
    public <T> T addCookie(String cookie);
    public <T> T deleteAllCookies();
    public <T> T deleteCookie(String name);
    public <T> T getCookies();
    public <T> T takeScreenshot();
    public <T> T takeScreenshot(List<String> elements);
    public <T> T getScreenOrientation();
    public <T> T setScreenOrientation(Orientation orientation);
    public <T> T getWindowSize();
    public <T> T setWindowSize(String size);
    public <T> T maximizeWindow();
}
