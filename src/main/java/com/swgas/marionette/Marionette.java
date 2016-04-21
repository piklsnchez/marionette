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
    public String getElementAttribute(String element, String name);
    public void clickElement(String element);
    public void singleTap(String element, String point);
    public void singleTap(String element);
    public String getElementText(String element);
    public void sendKeysToElement(String element, String text);
    public void clearElement(String element);
    public boolean isElementSelected(String element);
    public boolean isElementEnabled(String element);
    public boolean isElementDisplayed(String element);
    public String getElementTagName(String element);
    public String getElementRectangle(String element);
    public String getElementValueOfCssProperty(String element, String property);
    default public Object actionChain(List<Object> actionChain, String id){return null;}
    default public void multiAction(List<Object> multiActions){}
    public void acceptDialog();
    public void dismissDialog();
    public String getTextFromDialog();
    public void sendKeysToDialog(String text);
    public void quitApplication(List<String> flags);
    public String newSession(String sessionId);
    public String newSession();
    public void setTestName(String testName);
    public void deleteSession();
    public void setScriptTimeout(Duration timeout);
    public void setSearchTimeout(Duration timeout);
    public String getWindowHandle();
    public String getCurrentChromeWindowHandle();
    public String getWindowPosition();
    public void setWindowPosition(String point);
    public String getTitle();
    public List<String> getWindowHandles();
    public List<String> getChromeWindowHandles();
    public String getPageSource();
    public void close();
    public void closeChromeWindow();
    public void setContext(Context context);
    public Context getContext();
    public void switchToWindow(String id);
    public String getActiveFrame();
    public void switchToParentFrame();
    public void switchToFrame(String element);
    public String switchToShadowRoot(String element);
    public String getCurrentUrl();
    public String getWindowType();
    public void get(String url);
    public void timeouts(Timeout timeout, Duration time);
    public void goBack();
    public void goForward();
    public void refresh();
    public String executeJsScript(String script, List<String> args, boolean async, boolean newSandbox, Duration scriptTimeout, Duration inactivityTimeout);
    public String executeScript(String script, List<String> args, boolean newSandbox, Duration scriptTimeout);
    public String executeAsyncScript(String script, List<String> args, boolean newSandbox, Duration scriptTimeout, boolean debug);
    public String findElement(SearchMethod method, String value);
    public List<String> findElements(SearchMethod method, String value);
    public String getActiveElement();
    public void log(LogLevel level, String message);
    public List<String> getLogs();
    public void importScript(String script);
    public void clearImportedScripts();
    public void addCookie(String cookie);
    public void deleteAllCookies();
    public void deleteCookie(String name);
    public List<String> getCookies();
    public String takeScreenshot();
    public String takeScreenshot(List<String> elements);
    public Orientation getScreenOrientation();
    public void setScreenOrientation(Orientation orientation);
    public String getWindowSize();
    public String setWindowSize(String size);
    public String maximizeWindow();
}
