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
import com.swgas.parser.SessionParser;
import com.swgas.util.MarionetteUtil;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MarionetteImplTest {
    private static final String CLASS   = MarionetteImplTest.class.getName();
    private static final Logger LOG     = Logger.getLogger(CLASS);
    private static final String HOST    = "localhost";
    private static final int    PORT    = 2828;
    private static final int    TIMEOUT = 20;
    private static final String URL     = "https://myaccountdev.swgas.com/";
    
    public Process browser;
    private Marionette client;
    
    public MarionetteImplTest() {
    }
    
    @BeforeEach
    private void beforeEach() {
        ProcessBuilder _proc = new ProcessBuilder("firefox", "--marionette", "-P", "marionette", "--new-instance");
        //_proc.inheritIO();
        try{
            browser = _proc.start();            
            BufferedReader reader = new BufferedReader(new InputStreamReader(browser.getInputStream()));
            while(reader.lines().map(line -> {LOG.info(line);return line;}).noneMatch(line -> line.contains("Listening"))){}
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
    
    @AfterEach
    private void afterEach() throws Exception {
        if(true || browser != null){
            client.quitApplication(Collections.singletonList("eForceQuit"))
            .get(TIMEOUT, TimeUnit.SECONDS);
            try{
                browser.destroy();
            } catch(Exception e){}
        }
    }

    @Test
    public void testNewSession() throws Exception{
        LOG.entering(CLASS, "testNewSession");
        
        String sessionId = MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
        .thenApply(s -> {LOG.info(""+s); return SessionParser.parseFrom(s);})
        .get(TIMEOUT,TimeUnit.SECONDS);
        
        LOG.exiting(CLASS, "testNewSession", sessionId);
    }

    @Test
    public void testFindElements(){
        LOG.entering(CLASS, "testFindElements");
        try{
        String body = "body";
        Assertions.assertTrue(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.findElements(Marionette.SearchMethod.CSS_SELECTOR, body))
            .thenApply(MarionetteUtil::toList)
            .thenApply(a -> a.stream().map(obj -> Objects.toString(obj, "")).collect(Collectors.toList()))
            .get(TIMEOUT,TimeUnit.SECONDS)
            .size() == 1);
        //List<String> result = instance.findElements(method, value);
        } catch(ExecutionException | InterruptedException | TimeoutException e){
            LOG.throwing(CLASS, "testFindElements", e);
        }
        LOG.exiting(CLASS, "testFindElements");
    }

    @Test
    public void testGetElementAttribute() throws Exception{
        LOG.entering(CLASS, "testGetElementAttribute");
        String id = "menu_myaccount";
        String attribute = "title";
        Assertions.assertTrue(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.findElement(Marionette.SearchMethod.ID, id))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> client.getElementAttribute(e, attribute))
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT,TimeUnit.SECONDS)
            .contains("Home")
        );
        LOG.exiting(CLASS, "testGetElementAttribute");
    }

    @Test
    public void testClickElement() throws Exception {
        LOG.entering(CLASS, "testClickElement");
        String css = "input[name='username']";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> client.clickElement(e))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testClickElement");
    }

    @Test
    @Disabled
    public void testSingleTap_String_Point() throws Exception {
        LOG.entering(CLASS, "testSingleTap_String_Point");
        String css = "body";
        Point point = new Point(1, 1);
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> client.singleTap(e, (int)point.getX(), (int)point.getY()))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSingleTap_String_Point");
    }

    @Test
    @Disabled
    public void testSingleTap_String() throws Exception {
        LOG.entering(CLASS, "testSingleTap_String");
        String css = "body";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> client.singleTap(e, 0, 0))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSingleTap_String");
    }

    @Test
    public void testGetElementText() throws Exception {
        LOG.entering(CLASS, "testGetElementText");
        String id = "menu_myaccount";
        Assertions.assertTrue(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.findElement(Marionette.SearchMethod.ID, id))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> client.getElementText(e))
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
            .contains("Home")
        );
        LOG.exiting(CLASS, "testGetElementText");
    }

    @Test
    public void testSendKeysToElement() throws Exception {
        LOG.entering(CLASS, "testSendKeysToElement");
        String css = "input[name='username']";
        String text = "user";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> client.sendKeysToElement(e, text))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSendKeysToElement");
    }

    @Test
    public void testClearElement() throws Exception {
       LOG.entering(CLASS, "testClearElement");
        String css = "input[name='username']";
        String text = "user";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> client.sendKeysToElement(e, text))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> client.clearElement(e))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testClearElement");
    }

    @Test
    public void testIsElementSelected() throws Exception {
        LOG.entering(CLASS, "testIsElementSelected");
        String css = "body";
        Assertions.assertFalse(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> client.isElementSelected(e))
            .thenApply(MarionetteUtil::toBooleanValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testIsElementSelected");
    }

    @Test
    public void testIsElementEnabled() throws Exception {
        LOG.entering(CLASS, "testIsElementEnabled");
        String css = ".LoginForm button";
        Assertions.assertTrue(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> client.isElementEnabled(e))
            .thenApply(MarionetteUtil::toBooleanValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testIsElementEnabled");
    }

    @Test
    public void testIsElementDisplayed() throws Exception {
        LOG.entering(CLASS, "testIsElementDisplayed");
        String css = ".LoginForm button";
        Assertions.assertTrue(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> client.isElementDisplayed(e))
            .thenApply(MarionetteUtil::toBooleanValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testIsElementDisplayed");
    }

    @Test
    public void testGetElementTagName() throws Exception {
        LOG.entering(CLASS, "testGetElementTagName");
        String css = ".LoginForm button";
        Assertions.assertEquals(
            "button"
            , MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> client.getElementTagName(e))
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetElementTagName");
    }

    @Test
    public void testGetElementRectangle() throws Exception {
        LOG.entering(CLASS, "testGetElementRectangle");
        String css = ".LoginForm button";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> client.getElementRectangle(e))
        .thenApply(MarionetteUtil::toRectangle)
        .get(TIMEOUT, TimeUnit.SECONDS)
        .getBounds();
        LOG.exiting(CLASS, "testGetElementRectangle");
    }

    @Test
    public void testGetElementValueOfCssProperty() throws Exception {
        LOG.entering(CLASS, "testGetElementValueOfCssProperty");
        String css = ".LoginForm button";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> client.getElementCssProperty(e, "width"))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testGetElementValueOfCssProperty");
    }

    @Test
    public void testAcceptDialog() throws Exception {
        LOG.entering(CLASS, "testAcceptDialog");
        String script = "window.confirm();";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
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
        .thenCompose(c -> (client = c).newSession())
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
        Assertions.assertEquals(
            "house"
            , MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.executeScript(script, "[]", false, Duration.ofSeconds(TIMEOUT)))
            .thenCompose(s -> client.getTextFromDialog())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetTextFromDialog");
    }

    @Test
    public void testSendKeysToDialog() throws Exception {
        LOG.entering(CLASS, "testSendKeysToDialog");
        String script = "return window.prompt('say house');";
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.executeScript(script, "[]", false, Duration.ofSeconds(TIMEOUT)))
        .thenCompose(s -> client.sendKeysToDialog("house"))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSendKeysToDialog");
    }

    @Test
    public void testQuitApplication() throws Exception{
        LOG.entering(CLASS, "testQuitApplication");
        List<String> flags = Collections.singletonList("eForceQuit");
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
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
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.setTestName(testName))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSetTestName");
    }

    @Test
    public void testDeleteSession() throws Exception{
        LOG.entering(CLASS, "testDeleteSession");
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.deleteSession())
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testDeleteSession");
    }

    @Test
    public void testSetScriptTimeout() throws Exception {
        LOG.entering(CLASS, "testSetScriptTimeout");
        Duration timeout = Duration.ofSeconds(2);
        MarionetteFactory.getAsync(HOST, PORT)
        .thenCompose(c -> (client = c).newSession())
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
        .thenCompose(c -> (client = c).newSession())
        .thenCompose(s -> client.get(URL))
        .thenCompose(s -> client.setSearchTimeout(timeout))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSetSearchTimeout");
    }

    @Test
    public void testGetWindowHandle() throws Exception {
        LOG.entering(CLASS, "testGetWindowHandle");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s ->client.getWindowHandle())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetWindowHandle");
    }

    @Test
    public void testGetCurrentChromeWindowHandle() throws Exception {
        LOG.entering(CLASS, "testGetCurrentChromeWindowHandle");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.getCurrentChromeWindowHandle())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetCurrentChromeWindowHandle");
    }
    
    @Test
    public void testTakeScreenshot_0args() throws Exception {
        LOG.entering(CLASS, "testTakeScreenshot_0args");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get(URL))
            .thenCompose(s -> client.takeScreenshot())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testTakeScreenshot_0args");
    }

    @Test
    public void testGetPageSource() throws Exception {
        LOG.entering(CLASS, "testGetPageSource");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/maintenance/"))
            .thenCompose(s -> client.getPageSource())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetPageSource");
    }

    @Test
    public void testGetTitle() throws Exception {
        LOG.entering(CLASS, "testGetTitle");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getTitle())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetTitle");
    }

    @Test
    public void testGetWindowHandles() throws Exception {
        LOG.entering(CLASS, "testGetWindowHandles");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getWindowHandles())
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetWindowHandles");
    }

    @Test
    public void testGetChromeWindowHandles() throws Exception {
        LOG.entering(CLASS, "testGetChromeWindowHandles");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getChromeWindowHandles())
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetChromeWindowHandles");
    }

    @Test
    public void testClose() throws Exception {
        LOG.entering(CLASS, "testClose");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.close())
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testClose");
    }

    @Test
    public void testCloseChromeWindow() throws Exception {
        LOG.entering(CLASS, "testCloseChromeWindow");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.closeChromeWindow())
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testCloseChromeWindow");
    }

    @Test
    public void testSetContext() throws Exception {
        LOG.entering(CLASS, "testSetContext");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.setContext(Marionette.Context.CONTEXT_CHROME))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testSetContext");
    }

    @Test
    public void testGetContext() throws Exception {
        LOG.entering(CLASS, "testGetContext");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getContext())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetContext");
    }

    @Test
    public void testSwitchToWindow() throws Exception {
        LOG.entering(CLASS, "testSwitchToWindow");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getWindowHandles())
            .thenApply(MarionetteUtil::toArray)
            .thenApply(array -> array.getString(0))
            .thenCompose(client::switchToWindow)
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testSwitchToWindow");
    }

    @Test
    public void testGetActiveFrame() throws Exception {
        LOG.entering(CLASS, "testGetActiveFrame");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getActiveFrame())
            .thenApply(MarionetteUtil::toJsonValue)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetActiveFrame");
    }

    @Test
    public void testSwitchToParentFrame() throws Exception {
        LOG.entering(CLASS, "testSwitchToParentFrame");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.switchToParentFrame())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testSwitchToParentFrame");
    }

    @Test
    public void testSwitchToFrame() throws Exception {
        LOG.entering(CLASS, "testSwitchToFrame");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.switchToFrame(0))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testSwitchToFrame");
    }

    @Test
    public void testSwitchToShadowRoot() throws Exception {
        LOG.entering(CLASS, "testSwitchToShadowRoot");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.switchToShadowRoot())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testSwitchToShadowRoot");
    }

    @Test
    public void testGetCurrentUrl() throws Exception {
        LOG.entering(CLASS, "testGetCurrentUrl");
        String url = "https://myaccountdev.swgas.com/";
        String result;
        Assertions.assertTrue(
            Objects.equals(
                (result = MarionetteFactory.getAsync(HOST, PORT)
                .thenCompose(c -> (client = c).newSession())
                .thenCompose(s -> client.get(url))
                .thenCompose(s -> client.getCurrentUrl())
                .thenApply(MarionetteUtil::toStringValue)
                .get(TIMEOUT, TimeUnit.SECONDS))
                , url
            )
        );
        LOG.exiting(CLASS, "testGetCurrentUrl", result);
    }

    @Test
    public void testGetWindowType() throws Exception {
        LOG.entering(CLASS, "testGetWindowType");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getWindowType())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetWindowType");
    }

    @Test
    public void testGet()throws Exception {
        LOG.entering(CLASS, "testGet");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGet");
    }

    @Test
    public void testTimeouts() throws Exception {
        LOG.entering(CLASS, "testTimeouts");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.timeouts)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testTimeouts");
    }

    @Test    @Ignore
    public void testGoBack() throws Exception {
        LOG.entering(CLASS, "testGoBack");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.goBack)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGoBack");
    }

    @Test    @Ignore
    public void testGoForward() throws Exception {
        LOG.entering(CLASS, "testGoForward");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.goForward)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGoForward");
    }

    @Test    @Ignore
    public void testRefresh() throws Exception {
        LOG.entering(CLASS, "testRefresh");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.refresh)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testRefresh");
    }

    @Test    @Ignore
    public void testExecuteJsScript() throws Exception {
        LOG.entering(CLASS, "testExecuteJsScript");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.executeJsScript)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testExecuteJsScript");
    }

    @Test    @Ignore
    public void testExecuteScript() throws Exception {
        LOG.entering(CLASS, "testExecuteScript");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.executeScript)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testExecuteScript");
    }

    @Test    @Ignore
    public void testExecuteAsyncScript() throws Exception {
        LOG.entering(CLASS, "testExecuteAsyncScript");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.executeAsyncScript)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testExecuteAsyncScript");
    }

    @Test @Ignore
    public void testFindElement() throws Exception {
        LOG.entering(CLASS, "testFindElement");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.findElement)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testFindElement");
    }

    @Test    @Ignore
    public void testGetActiveElement() throws Exception {
        LOG.entering(CLASS, "testGetActiveElement");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getActiveElement)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetActiveElement");
    }

    @Test    @Ignore
    public void testLog() throws Exception {
        LOG.entering(CLASS, "testLog");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.log)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testLog");
    }

    @Test    @Ignore
    public void testGetLogs() throws Exception {
        LOG.entering(CLASS, "testGetLogs");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getLogs)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetLogs");
    }

    @Test    @Ignore
    public void testImportScript() throws Exception {
        LOG.entering(CLASS, "testImportScript");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.importScript)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testImportScript");
    }

    @Test    @Ignore
    public void testClearImportedScripts() throws Exception {
        LOG.entering(CLASS, "testClearImportedScripts");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.clearImportedScripts)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testClearImportedScripts");
    }

    @Test    @Ignore
    public void testAddCookie() throws Exception {
        LOG.entering(CLASS, "testAddCookie");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.addCookie)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testAddCookie");
    }

    @Test    @Ignore
    public void testDeleteAllCookies() throws Exception {
        LOG.entering(CLASS, "testDeleteAllCookies");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.deleteAllCookies)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testDeleteAllCookies");
    }

    @Test    @Ignore
    public void testDeleteCookie() throws Exception {
        LOG.entering(CLASS, "testDeleteCookie");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.deleteCookie)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testDeleteCookie");
    }

    @Test    @Ignore
    public void testGetCookies() throws Exception {
        LOG.entering(CLASS, "testGetCookies");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getCookies)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetCookies");
    }

    @Test    @Ignore
    public void testTakeScreenshot_List() throws Exception {
        LOG.entering(CLASS, "testTakeScreenshot_List");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.takeScreenshot_List)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testTakeScreenshot_List");
    }

    @Test    @Ignore
    public void testGetScreenOrientation() throws Exception {
        LOG.entering(CLASS, "testGetScreenOrientation");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getScreenOrientation)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetScreenOrientation");
    }

    @Test    @Ignore
    public void testSetScreenOrientation() throws Exception {
        LOG.entering(CLASS, "testSetScreenOrientation");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.setScreenOrientation)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testSetScreenOrientation");
    }

    @Test    @Ignore
    public void testGetWindowSize() throws Exception {
        LOG.entering(CLASS, "testGetWindowSize");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.getWindowSize)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetWindowSize");
    }

    @Test    @Ignore
    public void testSetWindowSize() throws Exception {
        LOG.entering(CLASS, "testSetWindowSize");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.setWindowSize)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testSetWindowSize");
    }

    @Test    @Ignore
    public void testMaximizeWindow() throws Exception {
        LOG.entering(CLASS, "testMaximizeWindow");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.maximizeWindow)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testMaximizeWindow");
    }

    @Test    @Ignore
    public void testFullscreen() throws Exception {
        LOG.entering(CLASS, "testFullscreen");
        LOG.info(
            MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> (client = c).newSession())
            .thenCompose(s -> client.get("https://myaccountdev.swgas.com/"))
            .thenCompose(s -> client.fullscreen)
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testFullscreen");
    }
}