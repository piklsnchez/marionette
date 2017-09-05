package com.swgas.marionette;

/**
 *
 * @author ocstest
 */
public enum Command {
      getElementAttribute         ("getElementAttribute")
      //new
    , getElementProperty          ("getElementProperty")
    , getElementTagName           ("getElementTagName")
    , getElementRect              ("getElementRect")
    , getElementValueOfCssProperty("getElementValueOfCssProperty")
    , getElementText              ("getElementText")
    , clickElement                ("clickElement")
    , sendKeysToElement           ("sendKeysToElement")
    , clearElement                ("clearElement")
    , isElementSelected           ("isElementSelected")
    , isElementEnabled            ("isElementEnabled")
    , isElementDisplayed          ("isElementDisplayed")
    , singleTap                   ("singleTap")
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
    //new
    , setTimeouts                 ("setTimeouts")
    , getTimeouts                 ("getTimeouts")
    , setScriptTimeout            ("setScriptTimeout")
    , setSearchTimeout            ("setSearchTimeout")
    , getWindowHandle             ("getWindowHandle")
    , getCurrentChromeWindowHandle("getCurrentChromeWindowHandle")
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
    //, timeouts                    ("timeouts")
    , goBack                      ("goBack")
    , goForward                   ("goForward")
    , refresh                     ("refresh")
    , executeScript               ("executeScript")
    , executeAsyncScript          ("executeAsyncScript")
    , findElement                 ("findElement")
    , findElements                ("findElements")
    , getActiveElement            ("getActiveElement")
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
    , getWindowRect               ("getWindowRect")
    , setWindowRect               ("setWindowRect")
    , minimizeWindow              ("minimizeWindow")
    , maximizeWindow              ("maximizeWindow")
    , fullscreen                  ("fullscreen");
    private final String command;
    Command(String command){
        this.command = command;
    }
    public String getCommand(){
        return command;
    }
}
