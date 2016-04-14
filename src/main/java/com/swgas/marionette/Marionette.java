package com.swgas.marionette;

import java.awt.Point;
import java.awt.Rectangle;
import java.net.HttpCookie;
import java.time.Duration;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLElement;

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
    public String getElementAttribute(HTMLElement element, String name);
    public void clickElement(HTMLElement element);
    public void singleTap(HTMLElement element, Point point);
    public void singleTap(HTMLElement element);
    public String getElementText(HTMLElement element);
    public void sendKeysToElement(HTMLElement element, String text);
    public void clearElement(HTMLElement element);
    public boolean isElementSelected(HTMLElement element);
    public boolean isElementEnabled(HTMLElement element);
    public boolean isElementDisplayed(HTMLElement element);
    public String getElementTagName(HTMLElement element);
    public Rectangle getElementRect(HTMLElement element);
    public String getElementValueOfCssProperty(HTMLElement element, String property);
    default public Object actionChain(List<Object> actionChain, String id){return null;}
    default public void multiAction(List<Object> multiActions){}
    public void acceptDialog();
    public void dismissDialog();
    public String getTextFromDialog();
    public void sendKeysToDialog(String text);
    public void quitApplication(List<String> flags);
    public Object newSession(String sessionId);
    public Object newSession();
    public void setTestName(String testName);
    public void deleteSession();
    public void setScriptTimeout(Duration timeout);
    public void setSearchTimeout(Duration timeout);
    public String getWindowHandle();
    public String getCurrentChromeWindowHandle();
    public Point getWindowPosition();
    public void setWindowPosition(Point point);
    public String getTitle();
    public List<String> getWindowHandles();
    public List<String> getChromeWindowHandles();
    public Document getPageSource();
    public void close();
    public void closeChromeWindow();
    public void setContext(Context context);
    public Context getContext();
    public void switchToWindow(String id);
    public HTMLElement getActiveFrame();
    public void switchToParentFrame();
    public void switchToFrame(HTMLElement element);
    public Object switchToShadowRoot(HTMLElement element);
    public String getCurrentUrl();
    public Object getWindowType();
    public void get(String url);
    public void timeouts(Timeout timeout, Duration time);
    public void goBack();
    public void goForward();
    public void refresh();
    public Object executeJsScript(String script, List<String> args, boolean async, boolean newSandbox, Duration scriptTimeout, Duration inactivityTimeout);
    public Object executeScript(String script, List<String> args, boolean async, boolean newSandbox, Duration scriptTimeout);
    public Object executeAsyncScript(String script, List<String> args, boolean async, boolean newSandbox, Duration scriptTimeout, boolean debug);
    public HTMLElement findElement(SearchMethod method, String value);
    public List<HTMLElement> findElements(SearchMethod method, String value);
    public HTMLElement getActiveElement();
    public void log(LogLevel level, String message);
    public List<String> getLogs();
    public void importScript(String file);
    public void clearImportedScripts();
    public void addCookie(HttpCookie cookie);
    public void deleteAllCookies();
    public void deleteCookie(String name);
    public List<HttpCookie> getCookies();
    public String takeScreenshot();
    public String takeScreenshot(List<HTMLElement> elements);
    public Orientation getScreenOrientation();
    public void setScreenOrientation(Orientation orientation);
    public Rectangle getWindowSize();
    public Object setWindowSize(Rectangle size);
    public Object maximizeWindow();
}
