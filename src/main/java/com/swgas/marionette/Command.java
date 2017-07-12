package com.swgas.marionette;

/**
 *
 * @author ocstest
 */
public enum Command {
      getElementAttribute         ("getElementAttribute")
    , clickElement                ("clickElement")
    , singleTap                   ("singleTap")
    , getElementText              ("getElementText")
    , sendKeysToElement           ("sendKeysToElement")
    , clearElement                ("clearElement")
    , isElementSelected           ("isElementSelected")
    , isElementEnabled            ("isElementEnabled")
    , isElementDisplayed          ("isElementDisplayed")
    , getElementTagName           ("getElementTagName")
    , getElementRect              ("getElementRect")
    , getElementValueOfCssProperty("getElementValueOfCssProperty")
    , actionChain                 ("actionChain")
    , multiAction                 ("multiAction")
    , acceptDialog                ("acceptDialog")
    , dismissDialog               ("dismissDialog")
    , getTextFromDialog           ("getTextFromDialog")
    , sendKeysToDialog            ("sendKeysToDialog")
    , quitApplication             ("quitApplication")
    , newSession                  ("newSession")
    , setTestName                 ("setTestName")
    , deleteSession               ("deleteSession")
    , setScriptTimeout            ("setScriptTimeout")
    , setSearchTimeout            ("setSearchTimeout")
    , getWindowHandle             ("getWindowHandle")
    , getCurrentChromeWindowHandle("getCurrentChromeWindowHandle")
    , getWindowPosition           ("getWindowPosition")
    , setWindowPosition           ("setWindowPosition")
    , getTitle                    ("getTitle")
    , getWindowHandles            ("getWindowHandles")
    , getChromeWindowHandles      ("getChromeWindowHandles")
    , getPageSource               ("getPageSource")
    , close                       ("close")
    , closeChromeWindow           ("closeChromeWindow")
    , setContext                  ("setContext")
    , getContext                  ("getContext")
    , switchToWindow              ("switchToWindow")
    , getActiveFrame              ("getActiveFrame")
    , switchToParentFrame         ("switchToParentFrame")
    , switchToFrame               ("switchToFrame")
    , switchToShadowRoot          ("switchToShadowRoot")
    , getCurrentUrl               ("getCurrentUrl")
    , getWindowType               ("getWindowType")
    , get                         ("get")
    , timeouts                    ("timeouts")
    , goBack                      ("goBack")
    , goForward                   ("goForward")
    , refresh                     ("refresh")
    , executeJsScript             ("executeJSScript")
    , executeScript               ("executeScript")
    , executeAsyncScript          ("executeAsyncScript")
    , findElement                 ("findElement")
    , findElements                ("findElements")
    , getActiveElement            ("getActiveElement")
    , log                         ("log")
    , getLogs                     ("getLogs")
    , importScript                ("importScript")
    , clearImportedScripts        ("clearImportedScripts")
    , addCookie                   ("addCookie")
    , deleteAllCookies            ("deleteAllCookies")
    , deleteCookie                ("deleteCookie")
    , getCookies                  ("getCookies")
    , takeScreenshot              ("takeScreenshot")
    , getScreenOrientation        ("getScreenOrientation")
    , setScreenOrientation        ("setScreenOrientation")
    , getWindowSize               ("getWindowSize")
    , setWindowSize               ("setWindowSize")
    , maximizeWindow              ("maximizeWindow");
    private final String command;
    Command(String command){
        this.command = command;
    }
    public String getCommand(){
        return command;
    }
}
