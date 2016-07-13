package com.swgas.marionette;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;

/**
 *
 * @author ocstest
 */
public class MarionetteImplTest {
    private static final String CLASS = MarionetteImplTest.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    
    public Process browser;
    private Marionette client;
    
    public MarionetteImplTest() {
    }
    
    @Before
    public void before() {
        ProcessBuilder _proc = new ProcessBuilder("/usr/bin/firefox", "-marionette");
        //_proc.inheritIO();
        try{
            browser = _proc.start();            
            BufferedReader reader = new BufferedReader(new InputStreamReader(browser.getInputStream()));
            while(reader.lines().map(line -> {LOG.info(line);return line;}).noneMatch(line -> line.contains("Listening"))){}
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
    
    @After
    public void after(){
        if(browser != null){
            client.quitApplication(Collections.singletonList("eForceQuit"));
            //browser.destroy();
        }
    }

    @Test
    public void testNewSession() throws Exception{
        LOG.entering(CLASS, "testNewSession");
        String url = "https://myaccountdev.swgas.com/";        
        Assert.assertTrue(
            MarionetteImpl.getAsync("localhost", 2828)
            .thenCompose(c -> {client = c; return client.newSession();})
            .thenCompose(s -> client.get(url))
            .thenCompose(s -> client.getCurrentUrl())
            .get(2,TimeUnit.SECONDS)
            .contains(url)
        );
        LOG.exiting(CLASS, "testNewSession");
    }

    @Test    @Ignore
    public void testGetElementAttribute() {
        System.out.println("getElementAttribute");
        String elementId = "";
        String attribute = "";
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getElementAttribute(elementId, attribute);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testClickElement() {
        System.out.println("clickElement");
        String elementId = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.clickElement(elementId);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSingleTap_String_Point() {
        System.out.println("singleTap");
        String elementId = "";
        Point point = null;
        MarionetteImpl instance = new MarionetteImpl();
        instance.singleTap(elementId, point.toString());
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSingleTap_String() {
        System.out.println("singleTap");
        String elementId = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.singleTap(elementId);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetElementText() {
        System.out.println("getElementText");
        String elementId = "";
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getElementText(elementId);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSendKeysToElement() {
        System.out.println("sendKeysToElement");
        String elementId = "";
        String text = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.sendKeysToElement(elementId, text);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testClearElement() {
        System.out.println("clearElement");
        String elementId = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.clearElement(elementId);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testIsElementSelected() {
        System.out.println("isElementSelected");
        String elementId = "";
        MarionetteImpl instance = new MarionetteImpl();
        boolean expResult = false;
        boolean result = instance.isElementSelected(elementId);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testIsElementEnabled() {
        System.out.println("isElementEnabled");
        String elementId = "";
        MarionetteImpl instance = new MarionetteImpl();
        boolean expResult = false;
        boolean result = instance.isElementEnabled(elementId);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testIsElementDisplayed() {
        System.out.println("isElementDisplayed");
        String elementId = "";
        MarionetteImpl instance = new MarionetteImpl();
        boolean expResult = false;
        boolean result = instance.isElementDisplayed(elementId);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetElementTagName() {
        System.out.println("getElementTagName");
        String elementId = "";
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getElementTagName(elementId);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetElementRectangle() {
        System.out.println("getElementRectangle");
        String elementId = "";
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getElementRectangle(elementId);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetElementValueOfCssProperty() {
        System.out.println("getElementValueOfCssProperty");
        String elementId = "";
        String property = "";
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getElementValueOfCssProperty(elementId, property);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testAcceptDialog() {
        System.out.println("acceptDialog");
        MarionetteImpl instance = new MarionetteImpl();
        instance.acceptDialog();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testDismissDialog() {
        System.out.println("dismissDialog");
        MarionetteImpl instance = new MarionetteImpl();
        instance.dismissDialog();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetTextFromDialog() {
        System.out.println("getTextFromDialog");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getTextFromDialog();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSendKeysToDialog() {
        System.out.println("sendKeysToDialog");
        String text = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.sendKeysToDialog(text);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testQuitApplication() {
        System.out.println("quitApplication");
        List<String> flags = null;
        MarionetteImpl instance = new MarionetteImpl();
        instance.quitApplication(flags);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testNewSession_String() {
        System.out.println("newSession");
        String sessionId = "";
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.newSession(sessionId).join();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSetTestName() {
        System.out.println("setTestName");
        String testName = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.setTestName(testName);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testDeleteSession() {
        System.out.println("deleteSession");
        MarionetteImpl instance = new MarionetteImpl();
        instance.deleteSession();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSetScriptTimeout() {
        System.out.println("setScriptTimeout");
        Duration timeout = null;
        MarionetteImpl instance = new MarionetteImpl();
        instance.setScriptTimeout(timeout);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSetSearchTimeout() {
        System.out.println("setSearchTimeout");
        Duration timeout = null;
        MarionetteImpl instance = new MarionetteImpl();
        instance.setSearchTimeout(timeout);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetWindowHandle() {
        System.out.println("getWindowHandle");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getWindowHandle();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetCurrentChromeWindowHandle() {
        System.out.println("getCurrentChromeWindowHandle");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getCurrentChromeWindowHandle();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetWindowPosition() {
        System.out.println("getWindowPosition");
        MarionetteImpl instance = new MarionetteImpl();
        Point expResult = null;
        Point result = instance.getWindowPosition();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSetWindowPosition() {
        System.out.println("setWindowPosition");
        Point point = null;
        MarionetteImpl instance = new MarionetteImpl();
        //instance.setWindowPosition(point);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetTitle() {
        System.out.println("getTitle");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getTitle();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetWindowHandles() {
        System.out.println("getWindowHandles");
        MarionetteImpl instance = new MarionetteImpl();
        List<String> expResult = null;
        List<String> result = instance.getWindowHandles();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetChromeWindowHandles() {
        System.out.println("getChromeWindowHandles");
        MarionetteImpl instance = new MarionetteImpl();
        List<String> expResult = null;
        List<String> result = instance.getChromeWindowHandles();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetPageSource() {
        System.out.println("getPageSource");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getPageSource();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testClose() {
        System.out.println("close");
        MarionetteImpl instance = new MarionetteImpl();
        instance.close();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testCloseChromeWindow() {
        System.out.println("closeChromeWindow");
        MarionetteImpl instance = new MarionetteImpl();
        instance.closeChromeWindow();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSetContext() {
        System.out.println("setContext");
        Marionette.Context context = null;
        MarionetteImpl instance = new MarionetteImpl();
        instance.setContext(context);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetContext() {
        System.out.println("getContext");
        MarionetteImpl instance = new MarionetteImpl();
        Marionette.Context expResult = null;
        Marionette.Context result = instance.getContext();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSwitchToWindow() {
        System.out.println("switchToWindow");
        String id = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.switchToWindow(id);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetActiveFrame() {
        System.out.println("getActiveFrame");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getActiveFrame();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSwitchToParentFrame() {
        System.out.println("switchToParentFrame");
        MarionetteImpl instance = new MarionetteImpl();
        instance.switchToParentFrame();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSwitchToFrame() {
        System.out.println("switchToFrame");
        String id = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.switchToFrame(id);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSwitchToShadowRoot() {
        System.out.println("switchToShadowRoot");
        String id = "";
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.switchToShadowRoot(id);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetCurrentUrl() {
        System.out.println("getCurrentUrl");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getCurrentUrl().join();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetWindowType() {
        System.out.println("getWindowType");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getWindowType();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGet() {
        System.out.println("get");
        String url = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.get(url);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testTimeouts() {
        System.out.println("timeouts");
        Marionette.Timeout timeout = null;
        Duration time = null;
        MarionetteImpl instance = new MarionetteImpl();
        instance.timeouts(timeout, time);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGoBack() {
        System.out.println("goBack");
        MarionetteImpl instance = new MarionetteImpl();
        instance.goBack();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGoForward() {
        System.out.println("goForward");
        MarionetteImpl instance = new MarionetteImpl();
        instance.goForward();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testRefresh() {
        System.out.println("refresh");
        MarionetteImpl instance = new MarionetteImpl();
        instance.refresh();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testExecuteJsScript() {
        System.out.println("executeJsScript");
        String script = "";
        String args = null;
        boolean async = false;
        boolean newSandbox = false;
        Duration scriptTimeout = null;
        Duration inactivityTimeout = null;
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.executeJsScript(script, args, async, newSandbox, scriptTimeout, inactivityTimeout);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testExecuteScript() {
        System.out.println("executeScript");
        String script = "";
        String args = null;
        boolean newSandbox = false;
        Duration scriptTimeout = null;
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.executeScript(script, args, newSandbox, scriptTimeout);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testExecuteAsyncScript() {
        System.out.println("executeAsyncScript");
        String script = "";
        String args = null;
        boolean newSandbox = false;
        Duration scriptTimeout = null;
        boolean debug = false;
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.executeAsyncScript(script, args, newSandbox, scriptTimeout, debug);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testFindElement() {
        System.out.println("findElement");
        Marionette.SearchMethod method = null;
        String value = "";
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.findElement(method, value);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testFindElements() {
        System.out.println("findElements");
        Marionette.SearchMethod method = null;
        String value = "";
        MarionetteImpl instance = new MarionetteImpl();
        List<String> expResult = null;
        List<String> result = instance.findElements(method, value);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetActiveElement() {
        System.out.println("getActiveElement");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getActiveElement();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testLog() {
        System.out.println("log");
        Marionette.LogLevel level = null;
        String message = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.log(level, message);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetLogs() {
        System.out.println("getLogs");
        MarionetteImpl instance = new MarionetteImpl();
        List<String> expResult = null;
        List<String> result = instance.getLogs();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testImportScript() {
        System.out.println("importScript");
        String script = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.importScript(script);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testClearImportedScripts() {
        System.out.println("clearImportedScripts");
        MarionetteImpl instance = new MarionetteImpl();
        instance.clearImportedScripts();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testAddCookie() {
        System.out.println("addCookie");
        String cookie = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.addCookie(cookie);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testDeleteAllCookies() {
        System.out.println("deleteAllCookies");
        MarionetteImpl instance = new MarionetteImpl();
        instance.deleteAllCookies();
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testDeleteCookie() {
        System.out.println("deleteCookie");
        String name = "";
        MarionetteImpl instance = new MarionetteImpl();
        instance.deleteCookie(name);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetCookies() {
        System.out.println("getCookies");
        MarionetteImpl instance = new MarionetteImpl();
        List<String> expResult = null;
        List<String> result = instance.getCookies();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test @Ignore
    public void testTakeScreenshot_0args() {
        LOG.entering(CLASS, "testTakeScreenshot_0args");
        client = new MarionetteImpl();
        String url = "https://myaccountdev.swgas.com/";
        client.newSession();
        client.get(url);
        String result = client.takeScreenshot();
        
        Assert.assertNotNull(result);
        LOG.exiting(CLASS, "testTakeScreenshot_0args", result.length());
    }

    @Test    @Ignore
    public void testTakeScreenshot_List() {
        System.out.println("takeScreenshot");
        List<String> elementIds = null;
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.takeScreenshot(elementIds);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetScreenOrientation() {
        System.out.println("getScreenOrientation");
        MarionetteImpl instance = new MarionetteImpl();
        Marionette.Orientation expResult = null;
        Marionette.Orientation result = instance.getScreenOrientation();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSetScreenOrientation() {
        System.out.println("setScreenOrientation");
        Marionette.Orientation orientation = null;
        MarionetteImpl instance = new MarionetteImpl();
        instance.setScreenOrientation(orientation);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetWindowSize() {
        System.out.println("getWindowSize");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.getWindowSize();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSetWindowSize() {
        System.out.println("setWindowSize");
        String size = "";
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.setWindowSize(size);
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testMaximizeWindow() {
        System.out.println("maximizeWindow");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        String result = instance.maximizeWindow();
        Assert.assertEquals(expResult, result);
        Assert.fail("The test case is a prototype.");
    }
    
}
