package com.swgas.marionette;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;
import com.swgas.parser.MarionetteParser;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MarionetteImplTest {
    private static final String CLASS = MarionetteImplTest.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private static final String HOST = "localhost";
    private static final int    PORT = 2828;
    private static final int    TIMEOUT = 20;
    private static final String URL = "https://myaccountdev.swgas.com/";
    
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
    public void after() throws Exception {
        if(browser != null){
            client.quitApplication(Collections.singletonList("eForceQuit")).get(TIMEOUT, TimeUnit.SECONDS);
            //browser.destroy();
        }
    }

    @Test
    public void testNewSession() throws Exception{
        LOG.entering(CLASS, "testNewSession");
        Assert.assertTrue(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> {client = c; return client.newSession();})
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.getCurrentUrl())
            .get(TIMEOUT,TimeUnit.SECONDS)
            .contains(URL)
        );
        LOG.exiting(CLASS, "testNewSession");
    }

    @Test
    public void testGetElementAttribute() throws Exception{
        LOG.entering(CLASS, "testGetElementAttribute");
        String id = "menu_myaccount";
        String attribute = "title";
        Assert.assertTrue(
            Objects.toString(
                MarionetteFactory.getAsync(HOST, PORT)
                .thenCompose(c -> {client = c; return client.newSession();})
                .thenCompose(s -> client.get(URL))
                .thenCompose(s -> client.findElement(Marionette.SearchMethod.ID, id))
                .thenApply(MarionetteParser.ELEMENT::parseFrom)
                .thenCompose(e -> client.getElementAttribute((String)e, attribute))
                .thenApply(MarionetteParser.OBJECT::parseFrom)
                .get(TIMEOUT,TimeUnit.SECONDS)
            ,"").contains("Home"));
        LOG.exiting(CLASS, "testGetElementAttribute");
    }

    @Test
    public void testClickElement() throws Exception {
        LOG.entering(CLASS, "testClickElement");
        String css = "input[name='username']";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteParser.ELEMENT::parseFrom)
        .thenCompose(e -> client.clickElement((String)e))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testClickElement");
    }

    @Test
    public void testSingleTap_String_Point() throws Exception {
        LOG.entering(CLASS, "testSingleTap_String_Point");
        String css = "body";
        Point point = new Point(1, 1);
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteParser.ELEMENT::parseFrom)
        .thenCompose(e -> client.singleTap((String)e, (int)point.getX(), (int)point.getY()))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSingleTap_String_Point");
    }

    @Test
    public void testSingleTap_String() throws Exception {
        LOG.entering(CLASS, "testSingleTap_String");
        String css = "body";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteParser.ELEMENT::parseFrom)
        .thenCompose(e -> client.singleTap((String)e))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSingleTap_String");
    }

    @Test
    public void testGetElementText() throws Exception {
        LOG.entering(CLASS, "testGetElementText");
        String id = "menu_myaccount";
        Assert.assertTrue(
            Objects.toString(
                MarionetteFactory.getAsync(HOST, PORT)
                .thenCompose(c -> {client = c; return client.newSession();})
                .thenCompose(s -> client.get(URL))
                .thenCompose(s -> client.findElement(Marionette.SearchMethod.ID, id))
                .thenApply(MarionetteParser.ELEMENT::parseFrom)
                .exceptionally(t -> client.findElement(Marionette.SearchMethod.ID, id).thenApply(MarionetteParser.ELEMENT::parseFrom).join())
                .thenCompose(e -> client.getElementText((String)e))
                .thenApply(MarionetteParser.OBJECT::parseFrom)
                .get(TIMEOUT, TimeUnit.SECONDS)
            ,"").contains("Home")
        );
        LOG.exiting(CLASS, "testGetElementText");
    }

    @Test
    public void testSendKeysToElement() throws Exception {
        LOG.entering(CLASS, "testSendKeysToElement");
        String css = "input[name='username']";
        String text = "user";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteParser.ELEMENT::parseFrom)
        .thenCompose(e -> client.sendKeysToElement((String)e, text))
        .thenApply(MarionetteParser.OBJECT::parseFrom)
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSendKeysToElement");
    }

    @Test
    public void testClearElement() throws Exception {
       LOG.entering(CLASS, "testClearElement");
        String css = "input[name='username']";
        String text = "user";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteParser.ELEMENT::parseFrom)
        .thenCompose(e -> client.sendKeysToElement((String)e, text))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteParser.ELEMENT::parseFrom)
        .thenCompose(e -> client.clearElement((String)e))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testClearElement");
    }

    @Test
    public void testIsElementSelected() throws Exception {
        LOG.entering(CLASS, "testIsElementSelected");
        String url = URL.concat("startenergyshare");
        String css = "input[name='donation_timing'][value='otd']";
        Assert.assertFalse(
            (boolean)MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> {client = c; return client.newSession();})
            .thenCompose(s -> client.get(url))
            .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteParser.ELEMENT::parseFrom)
            .thenCompose(e -> client.isElementSelected((String)e))
            .thenApply(MarionetteParser.OBJECT::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testIsElementSelected");
    }

    @Test
    public void testIsElementEnabled() throws Exception {
        LOG.entering(CLASS, "testIsElementEnabled");
        String css = ".LoginForm button";
        Assert.assertTrue(
            (boolean)MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> {client = c; return client.newSession();})
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteParser.ELEMENT::parseFrom)
            .thenCompose(e -> client.isElementEnabled((String)e))
            .thenApply(MarionetteParser.OBJECT::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testIsElementEnabled");
    }

    @Test
    public void testIsElementDisplayed() throws Exception {
        LOG.entering(CLASS, "testIsElementDisplayed");
        String css = ".LoginForm button";
        Assert.assertTrue(
            (boolean)MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> {client = c; return client.newSession();})
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteParser.ELEMENT::parseFrom)
            .thenCompose(e -> client.isElementDisplayed((String)e))
            .thenApply(MarionetteParser.OBJECT::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testIsElementDisplayed");
    }

    @Test
    public void testGetElementTagName() throws Exception {
        LOG.entering(CLASS, "testGetElementTagName");
        String css = ".LoginForm button";
        Assert.assertEquals(
            "button"
            , MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> {client = c; return client.newSession();})
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteParser.ELEMENT::parseFrom)
            .thenCompose(e -> client.getElementTagName((String)e))
            .thenApply(MarionetteParser.OBJECT::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetElementTagName");
    }

    @Test
    public void testGetElementRectangle() throws Exception {
        LOG.entering(CLASS, "testGetElementRectangle");
        String css = ".LoginForm button";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply  (MarionetteParser.ELEMENT::parseFrom)
        .thenCompose(e -> client.getElementRectangle((String)e))
        .thenApply  (MarionetteParser.OBJECT::parseFrom)
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testGetElementRectangle");
    }

    @Test
    public void testGetElementValueOfCssProperty() throws Exception {
        LOG.entering(CLASS, "testGetElementValueOfCssProperty");
        String css = ".LoginForm button";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply  (MarionetteParser.ELEMENT::parseFrom)
        .thenCompose(e -> client.getElementValueOfCssProperty((String)e, "width"))
        .thenApply(MarionetteParser.OBJECT::parseFrom)
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testGetElementValueOfCssProperty");
    }

    @Test
    public void testAcceptDialog() throws Exception {
        LOG.entering(CLASS, "testAcceptDialog");
        String script = "window.confirm();";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.executeScript(script, "[]", false, Duration.ofSeconds(TIMEOUT)))
        .thenCompose(s -> client.acceptDialog())
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testAcceptDialog");
    }

    @Test
    public void testDismissDialog() throws Exception {
        LOG.entering(CLASS, "testDismissDialog");
        String script = "window.confirm();";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.executeScript(script, "[]", false, Duration.ofSeconds(TIMEOUT)))
        .thenCompose(s -> client.dismissDialog())
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testDismissDialog");
    }

    @Test
    public void testGetTextFromDialog() throws Exception {
        LOG.entering(CLASS, "testGetTextFromDialog");
        String script = "window.confirm('house');";
        Assert.assertEquals(
            "house"
            , MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> {client = c; return client.newSession();})
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.executeScript(script, "[]", false, Duration.ofSeconds(TIMEOUT)))
            .thenCompose(s -> client.getTextFromDialog())
            .thenApply(MarionetteParser.OBJECT::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetTextFromDialog");
    }

    @Test
    public void testSendKeysToDialog() throws Exception {
        LOG.entering(CLASS, "testSendKeysToDialog");
        String script = "return window.prompt('say house');";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.executeScript(script, "[]", false, Duration.ofSeconds(TIMEOUT)))
        .thenCompose(s -> client.sendKeysToDialog("house"))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSendKeysToDialog");
    }

    @Test
    public void testQuitApplication() throws Exception{
        LOG.entering(CLASS, "testQuitApplication");
        List<String> flags = null;
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.quitApplication(flags))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testQuitApplication");
    }

    @Test
    public void testNewSession_String() throws Exception{
        LOG.entering(CLASS, "testNewSession_String");
        String sessionId = "1234";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession(sessionId);})
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testNewSession_String");
    }

    @Test
    public void testSetTestName() throws Exception {
        LOG.entering(CLASS, "testSetTestName");
        String testName = "tester";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.setTestName(testName))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSetTestName");
    }

    @Test
    public void testDeleteSession() throws Exception{
        LOG.entering(CLASS, "testDeleteSession");
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.deleteSession())
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testDeleteSession");
    }

    @Test
    public void testSetScriptTimeout() throws Exception {
        LOG.entering(CLASS, "testSetScriptTimeout");
        Duration timeout = Duration.ofSeconds(2);
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.setScriptTimeout(timeout))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSetScriptTimeout");
    }

    @Test
    public void testSetSearchTimeout() throws Exception {
        LOG.entering(CLASS, "testSetSearchTimeout");
        Duration timeout = Duration.ofSeconds(2);
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.newSession();})
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.setSearchTimeout(timeout))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSetSearchTimeout");
    }

    @Test
    public void testGetWindowHandle() throws Exception {
        LOG.entering(CLASS, "testGetWindowHandle");
        LOG.info(
            (String)MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> {client = c; return client.newSession();})
            .thenCompose(s -> client.get(URL))
            .thenCompose(s ->client.getWindowHandle())
            .thenApply(MarionetteParser.OBJECT::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetWindowHandle");
    }

    @Test
    public void testGetCurrentChromeWindowHandle() throws Exception {
        LOG.entering(CLASS, "testGetCurrentChromeWindowHandle");
        LOG.info(
            (String)MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> {client = c; return client.newSession();})
            .thenCompose(s -> client.get(URL))
            .thenCompose(s ->client.getCurrentChromeWindowHandle())
            .thenApply(MarionetteParser.OBJECT::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetCurrentChromeWindowHandle");
    }

    @Test    @Ignore
    public void testGetWindowPosition() {
        System.out.println("getWindowPosition");
        MarionetteImpl instance = new MarionetteImpl();
        Point expResult = null;
        //Point result = instance.getWindowPosition();
        
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
        //String result = instance.getTitle();
        
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetWindowHandles() {
        System.out.println("getWindowHandles");
        MarionetteImpl instance = new MarionetteImpl();
        List<String> expResult = null;
        //List<String> result = instance.getWindowHandles();
        
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetChromeWindowHandles() {
        System.out.println("getChromeWindowHandles");
        MarionetteImpl instance = new MarionetteImpl();
        List<String> expResult = null;
       // List<String> result = instance.getChromeWindowHandles();
        
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetPageSource() {
        System.out.println("getPageSource");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        //String result = instance.getPageSource();
        
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
        //Marionette.Context result = instance.getContext();
        
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
        //String result = instance.getActiveFrame();
        
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
        //String result = instance.switchToShadowRoot(id);
        
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetCurrentUrl() {
        System.out.println("getCurrentUrl");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        //String result = instance.getCurrentUrl().join();
        
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetWindowType() {
        System.out.println("getWindowType");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        //String result = instance.getWindowType();
        
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
        //String result = instance.executeJsScript(script, args, async, newSandbox, scriptTimeout, inactivityTimeout);
        
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
        //String result = instance.executeScript(script, args, newSandbox, scriptTimeout);
        
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
        //String result = instance.executeAsyncScript(script, args, newSandbox, scriptTimeout, debug);
        
        Assert.fail("The test case is a prototype.");
    }

    @Test @Ignore
    public void testFindElement() throws Exception {
        System.out.println("findElement");
        Marionette.SearchMethod method = null;
        String value = "";
        String expResult = "";
        String result = MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> {client = c; return client.findElement(method, value);})
        .get(TIMEOUT, TimeUnit.SECONDS);
        
    }

    @Test    @Ignore
    public void testFindElements() {
        System.out.println("findElements");
        Marionette.SearchMethod method = null;
        String value = "";
        MarionetteImpl instance = new MarionetteImpl();
        List<String> expResult = null;
        //List<String> result = instance.findElements(method, value);
        
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetActiveElement() {
        System.out.println("getActiveElement");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        //String result = instance.getActiveElement();
        
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
       // List<String> result = instance.getLogs();
        
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
       // List<String> result = instance.getCookies();
        
        Assert.fail("The test case is a prototype.");
    }

    @Test @Ignore
    public void testTakeScreenshot_0args() {
        LOG.entering(CLASS, "testTakeScreenshot_0args");
        client = new MarionetteImpl();
        String url = "https://myaccountdev.swgas.com/";
        client.newSession();
        client.get(url);
        //String result = client.takeScreenshot();
        
        //Assert.assertNotNull(result);
        LOG.exiting(CLASS, "testTakeScreenshot_0args");
    }

    @Test    @Ignore
    public void testTakeScreenshot_List() {
        System.out.println("takeScreenshot");
        List<String> elementIds = null;
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        //String result = instance.takeScreenshot(elementIds);
        
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testGetScreenOrientation() {
        System.out.println("getScreenOrientation");
        MarionetteImpl instance = new MarionetteImpl();
        Marionette.Orientation expResult = null;
        //Marionette.Orientation result = instance.getScreenOrientation();
        
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
        //String result = instance.getWindowSize();
        
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testSetWindowSize() {
        System.out.println("setWindowSize");
        String size = "";
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        //String result = instance.setWindowSize(size);
        
        Assert.fail("The test case is a prototype.");
    }

    @Test    @Ignore
    public void testMaximizeWindow() {
        System.out.println("maximizeWindow");
        MarionetteImpl instance = new MarionetteImpl();
        String expResult = "";
        //String result = instance.maximizeWindow();
        
        Assert.fail("The test case is a prototype.");
    }
}
