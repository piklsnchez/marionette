package com.swgas.marionette;

import java.time.Duration;
import java.util.List;

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
        ID, NAME, CLASS_NAME, TAG_NAME, CSS, LINK, PARTIAL_LINK, XPATH, ANON, ANON_ATTRIBUTE
    }
    public enum LogLevel{
        ERROR, INFO, DEBUG
    }
    public enum Orientation{
        PORTRAIT_PRIMARY, LANDSCAPE_PRIMARY, PORTRAIT_SECONDARY, LANDSCAPE_SECONDARY
    }
    public <T> T getElementAttribute(String element, String name);
    public <T> T clickElement(String element);
    public <T> T singleTap(String element, String point);
    public <T> T singleTap(String element);
    public <T> T getElementText(String element);
    public <T> T sendKeysToElement(String element, String text);
    public <T> T clearElement(String element);
    public boolean isElementSelected(String element);
    public boolean isElementEnabled(String element);
    public boolean isElementDisplayed(String element);
    public <T> T getElementTagName(String element);
    public <T> T getElementRectangle(String element);
    public <T> T getElementValueOfCssProperty(String element, String property);
    default public Object actionChain(List<Object> actionChain, String id){return null;}
    default public void multiAction(List<Object> multiActions){}
    public <T> T acceptDialog();
    public <T> T dismissDialog();
    public <T> T getTextFromDialog();
    public <T> T sendKeysToDialog(String text);
    public <T> T quitApplication(List<String> flags);
    public <T> T newSession(String sessionId);
    public <T> T newSession();
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
    public Context getContext();
    public <T> T switchToWindow(String id);
    public <T> T getActiveFrame();
    public <T> T switchToParentFrame();
    public <T> T switchToFrame(String element);
    public <T> T switchToShadowRoot(String element);
    public <T> T getCurrentUrl();
    public <T> T getWindowType();
    public <T> T get(String url);
    public <T> T timeouts(Timeout timeout, Duration time);
    public <T> T goBack();
    public <T> T goForward();
    public <T> T refresh();
    public <T> T executeJsScript(String script, List<String> args, boolean async, boolean newSandbox, Duration scriptTimeout, Duration inactivityTimeout);
    public <T> T executeScript(String script, List<String> args, boolean newSandbox, Duration scriptTimeout);
    public <T> T executeAsyncScript(String script, List<String> args, boolean newSandbox, Duration scriptTimeout, boolean debug);
    public <T> T findElement(SearchMethod method, String value);
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
    public Orientation getScreenOrientation();
    public <T> T setScreenOrientation(Orientation orientation);
    public <T> T getWindowSize();
    public <T> T setWindowSize(String size);
    public <T> T maximizeWindow();
}
