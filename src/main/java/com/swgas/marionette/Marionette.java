package com.swgas.marionette;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Marionette {
    public enum Context{
        CONTEXT_CHROME, CONTEXT_CONTENT
    }
    public enum Timeout{
        SEARCH, SCRIPT, PAGE
    }
    public enum SearchMethod{
          ELEMENT       ("ele=")
        , ID            ("")
        , NAME          ("name=")
        , CLASS_NAME    ("class=")
        , TAG_NAME      ("tag=")
        , CSS_SELECTOR  ("css=")
        , LINK          ("link=")
        , PARTIAL_LINK  ("link~=")
        , XPATH         ("")
        , ANON          ("anon=")
        , ANON_ATTRIBUTE("anonAttr=");

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
    public CompletableFuture<String> getElementText(String element);
    public CompletableFuture<String> sendKeysToElement(String element, String arrayOfChars);
    public CompletableFuture<String> clearElement(String element);
    public CompletableFuture<Boolean> isElementSelected(String element);
    public CompletableFuture<Boolean> isElementEnabled(String element);
    public CompletableFuture<Boolean> isElementDisplayed(String element);
    public CompletableFuture<String> getElementTagName(String element);
    public CompletableFuture<Rectangle2D> getElementRectangle(String element);
    public CompletableFuture<String> getElementValueOfCssProperty(String element, String property);
    default CompletableFuture<String> actionChain(List<Object> actionChain, String id){return null;}
    default CompletableFuture<String> multiAction(List<Object> multiActions){return null;}
    public CompletableFuture<String> acceptDialog();
    public CompletableFuture<String> dismissDialog();
    public CompletableFuture<String> getTextFromDialog();
    public CompletableFuture<String> sendKeysToDialog(String text);
    public CompletableFuture<String> quitApplication(List<String> flags);
    public CompletableFuture<String> newSession(String sessionId);
    public CompletableFuture<String> newSession();
    public CompletableFuture<String> setTestName(String testName);
    public CompletableFuture<String> deleteSession();
    public CompletableFuture<String> setScriptTimeout(Duration timeout);
    public CompletableFuture<String> setSearchTimeout(Duration timeout);
    public CompletableFuture<String> getWindowHandle();
    public CompletableFuture<String> getCurrentChromeWindowHandle();
    public CompletableFuture<Point2D> getWindowPosition();
    public CompletableFuture<String> setWindowPosition(Point2D point);
    public CompletableFuture<String> getTitle();
    public CompletableFuture<List<String>> getWindowHandles();
    public CompletableFuture<List<String>> getChromeWindowHandles();
    public CompletableFuture<String> getPageSource();
    public CompletableFuture<String> close();
    public CompletableFuture<String> closeChromeWindow();
    public CompletableFuture<String> setContext(Context context);
    public CompletableFuture<Context> getContext();
    public CompletableFuture<String> switchToWindow(String id);
    public CompletableFuture<String> getActiveFrame();
    public CompletableFuture<String> switchToParentFrame();
    public CompletableFuture<String> switchToFrame(String element);
    public CompletableFuture<String> switchToShadowRoot(String element);
    public CompletableFuture<String> getCurrentUrl();
    public CompletableFuture<String> getWindowType();
    public CompletableFuture<String> get(String url);
    public CompletableFuture<String> timeouts(Timeout timeout, Duration time);
    public CompletableFuture<String> goBack();
    public CompletableFuture<String> goForward();
    public CompletableFuture<String> refresh();
    public CompletableFuture<String> executeJsScript(String script, String args, boolean async, boolean newSandbox, Duration scriptTimeout, Duration inactivityTimeout);
    public CompletableFuture<String> executeScript(String script, String args, boolean newSandbox, Duration scriptTimeout);
    public CompletableFuture<String> executeAsyncScript(String script, String args, boolean newSandbox, Duration scriptTimeout, boolean debug);
    public CompletableFuture<String> findElement(SearchMethod method, String value);
    public CompletableFuture<List<String>> findElements(SearchMethod method, String value);
    public CompletableFuture<String> getActiveElement();
    public CompletableFuture<String> log(LogLevel level, String message);
    public CompletableFuture<List<String>> getLogs();
    public CompletableFuture<String> importScript(String script);
    public CompletableFuture<String> clearImportedScripts();
    public CompletableFuture<String> addCookie(String cookie);
    public CompletableFuture<String> deleteAllCookies();
    public CompletableFuture<String> deleteCookie(String name);
    public CompletableFuture<List<String>> getCookies();
    public CompletableFuture<String> takeScreenshot();
    public CompletableFuture<String> takeScreenshot(List<String> elements);
    public CompletableFuture<Orientation> getScreenOrientation();
    public CompletableFuture<String> setScreenOrientation(Orientation orientation);
    public CompletableFuture<Dimension2D> getWindowSize();
    public CompletableFuture<String> setWindowSize(Dimension2D size);
    public CompletableFuture<String> maximizeWindow();
}
