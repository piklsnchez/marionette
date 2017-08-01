package com.swgas.marionette;

import com.swgas.model.Timeouts;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.json.JsonArray;

public interface Marionette {
    public enum Context{
        CONTEXT_CHROME, CONTEXT_CONTENT
    }
    public enum Timeout{
        SEARCH, SCRIPT, PAGE_LOAD;
        @Override
        public String toString(){
            return name().toLowerCase().replace("_", " ").replace("search", "implicit");
        }
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
    public static final String WEBELEMENT_KEY     = "ELEMENT";
    public static final String W3C_WEBELEMENT_KEY = "element-6066-11e4-a52e-4f735466cecf";
    
    public CompletableFuture<JsonArray>  getElementAttribute         (String element, String attribute);
    public CompletableFuture<JsonArray>  getElementProperty          (String element, String property);
    public CompletableFuture<JsonArray>  clickElement                (String element);
    public CompletableFuture<JsonArray>  getElementText              (String element);
    public CompletableFuture<JsonArray>  sendKeysToElement           (String element, String arrayOfChars);
    public CompletableFuture<JsonArray>  clearElement                (String element);
    public CompletableFuture<JsonArray>  isElementSelected           (String element);
    public CompletableFuture<JsonArray>  isElementEnabled            (String element);
    public CompletableFuture<JsonArray>  isElementDisplayed          (String element);
    public CompletableFuture<JsonArray>  getElementTagName           (String element);
    public CompletableFuture<JsonArray>  getElementRectangle         (String element);
    public CompletableFuture<JsonArray>  getElementCssProperty       (String element, String property);
    public CompletableFuture<JsonArray>  singleTap                   (String element, int x, int y);
    default CompletableFuture<JsonArray> actionChain                 (List<Object> actionChain, String id){return null;}
    default CompletableFuture<JsonArray> multiAction                 (List<Object> multiActions){return null;}
    public CompletableFuture<JsonArray>  acceptDialog                ();
    public CompletableFuture<JsonArray>  dismissDialog               ();
    public CompletableFuture<JsonArray>  getTextFromDialog           ();
    public CompletableFuture<JsonArray>  sendKeysToDialog            (String text);
    public CompletableFuture<JsonArray>  quitApplication             (List<String> flags);
    public CompletableFuture<JsonArray>  newSession                  (String sessionId);
    public CompletableFuture<JsonArray>  newSession                  ();
    public CompletableFuture<JsonArray>  setTestName                 (String testName);
    public CompletableFuture<JsonArray>  deleteSession               ();
    public CompletableFuture<JsonArray>  setScriptTimeout            (Duration timeout);
    public CompletableFuture<JsonArray>  setSearchTimeout            (Duration timeout);
    public CompletableFuture<JsonArray>  getWindowHandle             ();
    public CompletableFuture<JsonArray>  getCurrentChromeWindowHandle();
    public CompletableFuture<JsonArray>  getTitle                    ();
    public CompletableFuture<JsonArray>  getWindowHandles            ();
    public CompletableFuture<JsonArray>  getChromeWindowHandles      ();
    public CompletableFuture<JsonArray>  getPageSource               ();
    public CompletableFuture<JsonArray>  close                       ();
    public CompletableFuture<JsonArray>  closeChromeWindow           ();
    public CompletableFuture<JsonArray>  setContext                  (Context context);
    public CompletableFuture<JsonArray>  getContext                  ();
    public CompletableFuture<JsonArray>  switchToWindow              (String id);
    public CompletableFuture<JsonArray>  getActiveFrame              ();
    public CompletableFuture<JsonArray>  switchToParentFrame         ();
    public CompletableFuture<JsonArray>  switchToFrame               ();
    public CompletableFuture<JsonArray>  switchToFrame               (String id);
    public CompletableFuture<JsonArray>  switchToFrame               (int id);
    public CompletableFuture<JsonArray>  switchToShadowRoot          ();
    public CompletableFuture<JsonArray>  switchToShadowRoot          (String element);
    public CompletableFuture<JsonArray>  getCurrentUrl               ();
    public CompletableFuture<JsonArray>  getWindowType               ();
    public CompletableFuture<JsonArray>  get                         (String url);
    public CompletableFuture<JsonArray>  setTimeouts                 (Timeouts timeouts);
    public CompletableFuture<JsonArray>  getTimeouts                 ();
    public CompletableFuture<JsonArray>  goBack                      ();
    public CompletableFuture<JsonArray>  goForward                   ();
    public CompletableFuture<JsonArray>  refresh                     ();
    public CompletableFuture<JsonArray>  executeJsScript             (String script, String args, Boolean async, Boolean newSandbox, Duration scriptTimeout, Duration inactivityTimeout);
    public CompletableFuture<JsonArray>  executeScript               (String script, String args, Boolean newSandbox, Duration scriptTimeout);
    public CompletableFuture<JsonArray>  executeAsyncScript          (String script, String args, Boolean newSandbox, Duration scriptTimeout, Boolean debug);
    public CompletableFuture<JsonArray>  findElement                 (SearchMethod method, String value);
    public CompletableFuture<JsonArray>  findElements                (SearchMethod method, String value);
    public CompletableFuture<JsonArray>  findElementFromElement      (SearchMethod method, String value, String elementId);
    public CompletableFuture<JsonArray>  findElementsFromElement     (SearchMethod method, String value, String elementId);
    public CompletableFuture<JsonArray>  getActiveElement            ();
    public CompletableFuture<JsonArray>  log                         (LogLevel level, String message);
    public CompletableFuture<JsonArray>  getLogs                     ();
    public CompletableFuture<JsonArray>  addCookie                   (String cookie);
    public CompletableFuture<JsonArray>  deleteAllCookies            ();
    public CompletableFuture<JsonArray>  deleteCookie                (String name);
    public CompletableFuture<JsonArray>  getCookies                  ();
    public CompletableFuture<JsonArray>  takeScreenshot              ();
    public CompletableFuture<JsonArray>  takeScreenshot              (String element);
    public CompletableFuture<JsonArray>  getScreenOrientation        ();
    public CompletableFuture<JsonArray>  setScreenOrientation        (Orientation orientation);
    public CompletableFuture<JsonArray>  getWindowRect               ();
    public CompletableFuture<JsonArray>  setWindowRect               (Rectangle2D size);
    public CompletableFuture<JsonArray>  minimizeWindow              ();
    public CompletableFuture<JsonArray>  maximizeWindow              ();
    public CompletableFuture<JsonArray>  fullscreen                  ();
    public CompletableFuture<JsonArray>  performActions              ();
    public CompletableFuture<JsonArray>  releaseActions              ();
}
