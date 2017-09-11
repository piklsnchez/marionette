package com.swgas.marionette;

import com.swgas.model.Cookie;
import com.swgas.model.Timeouts;
import com.swgas.rest.Session;
import java.awt.Point;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import com.swgas.util.MarionetteUtil;
import java.awt.geom.Rectangle2D;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.json.Json;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MarionetteImplTest {
    private static final String CLASS   = MarionetteImplTest.class.getName();
    private static final Logger LOG     = Logger.getLogger(CLASS);
    private static final int    TIMEOUT = 20;
    private static final String URL     = "https://myaccountdev.swgas.com/";
    
    private Session session;
    
    public MarionetteImplTest() {
    }
    
    @BeforeEach
    private void beforeEach() {
        LOG.entering(CLASS, "beforeEach");
        try{
            session = MarionetteFactory.createSession().get(TIMEOUT, TimeUnit.SECONDS);
            session.setSessionId(
                session.getClient().newSession()
                .thenApply(MarionetteUtil::toSession)
                .get(TIMEOUT, TimeUnit.SECONDS)
            );
        } catch(Exception e){
            LOG.throwing(CLASS, "beforeEach", e);
            if(null != session && session.getProc() != null){
                try{
                    session.getProc().destroy();
                } catch(Exception _e){
                    LOG.logp(Level.WARNING, CLASS, "beforeEach", e.getMessage(), _e);
                }
            }
            throw new RuntimeException(e);
        }
        LOG.exiting(CLASS, "beforeEach", session);
    }
    
    @AfterEach
    private void afterEach() throws Exception {        
        try(Session s = session){
            if(null != session && session.getProc() != null){
                session.getClient()
                .quitApplication(Collections.singletonList("eForceQuit"))
                .thenApply(MarionetteUtil::toObject)
                .thenApply(Objects::toString)
                .get(TIMEOUT, TimeUnit.SECONDS);

                session.getProc().destroy();
            }
        } catch(Exception e){
            LOG.throwing(CLASS, "afterEach", e);
        }
    }

    @Disabled("already run")
    @Test
    public void testNewSession() throws Exception{
        LOG.entering(CLASS, "testNewSession");
                
        LOG.exiting(CLASS, "testNewSession");
    }

    @Test
    public void testFindElements(){
        LOG.entering(CLASS, "testFindElements");
        try{
        String body = "body";
        Assertions.assertTrue(
            session.getClient().get(URL)
            .thenCompose(s -> session.getClient().findElements(Marionette.SearchMethod.CSS_SELECTOR, body))
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
            session.getClient().get(URL)
            .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.ID, id))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> session.getClient().getElementAttribute(e.getId(), attribute))
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
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> session.getClient().clickElement(e.getId()))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testClickElement");
    }

    @Test
    @Disabled
    public void testSingleTap_String_Point() throws Exception {
        LOG.entering(CLASS, "testSingleTap_String_Point");
        String css = "body";
        Point point = new Point(1, 1);
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> session.getClient().singleTap(e.getId(), (int)point.getX(), (int)point.getY()))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSingleTap_String_Point");
    }

    @Test
    @Disabled
    public void testSingleTap_String() throws Exception {
        LOG.entering(CLASS, "testSingleTap_String");
        String css = "body";
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> session.getClient().singleTap(e.getId(), 0, 0))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSingleTap_String");
    }

    @Test
    public void testGetElementText() throws Exception {
        LOG.entering(CLASS, "testGetElementText");
        String id = "menu_myaccount";
        Assertions.assertTrue(
            session.getClient().get(URL)
            .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.ID, id))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> session.getClient().getElementText(e.getId()))
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
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> session.getClient().sendKeysToElement(e.getId(), text))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSendKeysToElement");
    }

    @Test
    public void testClearElement() throws Exception {
       LOG.entering(CLASS, "testClearElement");
        String css = "input[name='username']";
        String text = "user";
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> session.getClient().sendKeysToElement(e.getId(), text))
        .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> session.getClient().clearElement(e.getId()))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testClearElement");
    }

    @Test
    public void testIsElementSelected() throws Exception {
        LOG.entering(CLASS, "testIsElementSelected");
        String css = "body";
        Assertions.assertFalse(
            session.getClient().get(URL)
            .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> session.getClient().isElementSelected(e.getId()))
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
            session.getClient().get(URL)
            .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> session.getClient().isElementEnabled(e.getId()))
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
            session.getClient().get(URL)
            .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> session.getClient().isElementDisplayed(e.getId()))
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
            , session.getClient().get(URL)
            .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
            .thenApply(MarionetteUtil::toElement)
            .thenCompose(e -> session.getClient().getElementTagName(e.getId()))
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetElementTagName");
    }

    @Test
    public void testGetElementRectangle() throws Exception {
        LOG.entering(CLASS, "testGetElementRectangle");
        String css = ".LoginForm button";
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> session.getClient().getElementRectangle(e.getId()))
        .thenApply(MarionetteUtil::toRectangle)
        .get(TIMEOUT, TimeUnit.SECONDS)
        .getBounds();
        LOG.exiting(CLASS, "testGetElementRectangle");
    }

    @Test
    public void testGetElementValueOfCssProperty() throws Exception {
        LOG.entering(CLASS, "testGetElementValueOfCssProperty");
        String css = ".LoginForm button";
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.CSS_SELECTOR, css))
        .thenApply(MarionetteUtil::toElement)
        .thenCompose(e -> session.getClient().getElementCssProperty(e.getId(), "width"))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testGetElementValueOfCssProperty");
    }

    @Test
    public void testAcceptDialog() throws Exception {
        LOG.entering(CLASS, "testAcceptDialog");
        String script = "window.confirm();";
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().executeScript(script, "[]", false, Duration.ofSeconds(TIMEOUT)))
        .thenCompose(s -> session.getClient().acceptDialog())
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testAcceptDialog");
    }

    @Test
    public void testDismissDialog() throws Exception {
        LOG.entering(CLASS, "testDismissDialog");
        String script = "window.confirm();";
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().executeScript(script, "[]", false, Duration.ofSeconds(TIMEOUT)))
        .thenCompose(s -> session.getClient().dismissDialog())
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testDismissDialog");
    }

    @Test
    public void testGetTextFromDialog() throws Exception {
        LOG.entering(CLASS, "testGetTextFromDialog");
        String script = "window.confirm('house');";
        Assertions.assertEquals(
            "house"
            , session.getClient().get(URL)
            .thenCompose(s -> session.getClient().executeScript(script, "[]", false, Duration.ofSeconds(TIMEOUT)))
            .thenCompose(s -> session.getClient().getTextFromDialog())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetTextFromDialog");
    }

    @Test
    public void testSendKeysToDialog() throws Exception {
        LOG.entering(CLASS, "testSendKeysToDialog");
        String script = "return window.prompt('say house');";
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().executeScript(script, "[]", false, Duration.ofSeconds(TIMEOUT)))
        .thenCompose(s -> session.getClient().sendKeysToDialog("house"))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSendKeysToDialog");
    }

    @Disabled("already run")
    @Test
    public void testQuitApplication() throws Exception{
        LOG.entering(CLASS, "testQuitApplication");
        List<String> flags = Collections.singletonList("eForceQuit");
        session.getClient().quitApplication(flags)
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testQuitApplication");
    }

    @Disabled("don't know")
    @Test
    public void testNewSession_String() throws Exception{
        LOG.entering(CLASS, "testNewSession_String");
        String sessionId = "1234";
        session.getClient().newSession(sessionId)
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testNewSession_String");
    }

    @Test
    public void testSetTestName() throws Exception {
        LOG.entering(CLASS, "testSetTestName");
        String testName = "tester";
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().setTestName(testName))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSetTestName");
    }

    @Disabled("already run")
    @Test
    public void testDeleteSession() throws Exception{
        LOG.entering(CLASS, "testDeleteSession");
        session.getClient().deleteSession()
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testDeleteSession");
    }

    @Test
    public void testSetScriptTimeout() throws Exception {
        LOG.entering(CLASS, "testSetScriptTimeout");
        Duration timeout = Duration.ofSeconds(2);
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().setScriptTimeout(timeout))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSetScriptTimeout");
    }

    @Test
    public void testSetSearchTimeout() throws Exception {
        LOG.entering(CLASS, "testSetSearchTimeout");
        Duration timeout = Duration.ofSeconds(2);
        session.getClient().get(URL)
        .thenCompose(s -> session.getClient().setSearchTimeout(timeout))
        .get(TIMEOUT, TimeUnit.SECONDS);
        LOG.exiting(CLASS, "testSetSearchTimeout");
    }

    @Test
    public void testGetWindowHandle() throws Exception {
        LOG.entering(CLASS, "testGetWindowHandle");
        LOG.info(
            session.getClient().get(URL)
            .thenCompose(s -> session.getClient().getWindowHandle())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetWindowHandle");
    }

    @Test
    public void testGetCurrentChromeWindowHandle() throws Exception {
        LOG.entering(CLASS, "testGetCurrentChromeWindowHandle");
        LOG.info(
            session.getClient().get(URL)
            .thenCompose(s -> session.getClient().getCurrentChromeWindowHandle())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetCurrentChromeWindowHandle");
    }
    
    @Test
    public void testTakeScreenshot_0args() throws Exception {
        LOG.entering(CLASS, "testTakeScreenshot_0args");
        LOG.info(
            session.getClient().get(URL)
            .thenCompose(s -> session.getClient().takeScreenshot())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testTakeScreenshot_0args");
    }

    @Test
    public void testGetPageSource() throws Exception {
        LOG.entering(CLASS, "testGetPageSource");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/maintenance/")
            .thenCompose(s -> session.getClient().getPageSource())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetPageSource");
    }

    @Test
    public void testGetTitle() throws Exception {
        LOG.entering(CLASS, "testGetTitle");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getTitle())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetTitle");
    }

    @Test
    public void testGetWindowHandles() throws Exception {
        LOG.entering(CLASS, "testGetWindowHandles");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getWindowHandles())
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
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getChromeWindowHandles())
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
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().close())
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
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().closeChromeWindow())
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testCloseChromeWindow");
    }

    @Test @Disabled("Just wanted to skip")
    public void testSetContext() throws Exception {
        LOG.entering(CLASS, "testSetContext");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().setContext(Marionette.Context.CONTEXT_CONTENT))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testSetContext");
    }

    @Test @Disabled("Just wanted to skip")
    public void testGetContext() throws Exception {
        LOG.entering(CLASS, "testGetContext");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getContext())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetContext");
    }

    @Test @Disabled
    public void testSwitchToWindow() throws Exception {
        LOG.entering(CLASS, "testSwitchToWindow");
        try{
            String result = session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getWindowHandles())
            .thenApply(MarionetteUtil::toArray)
            .thenApply(array -> array.getString(0))
            .thenCompose(session.getClient()::switchToWindow)
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "testSwitchToWindow", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSwitchToWindow", e);
            throw e;
        }
    }

    @Test
    public void testGetActiveFrame() throws Exception {
        LOG.entering(CLASS, "testGetActiveFrame");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getActiveFrame())
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
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().switchToParentFrame())
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
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().switchToFrame(null))
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
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().switchToShadowRoot())
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
                result = session.getClient().get(url)
                .thenCompose(s -> session.getClient().getCurrentUrl())
                .thenApply(MarionetteUtil::toStringValue)
                .get(TIMEOUT, TimeUnit.SECONDS)
                , url
            )
        );
        LOG.exiting(CLASS, "testGetCurrentUrl", result);
    }

    @Test
    public void testGetWindowType() throws Exception {
        LOG.entering(CLASS, "testGetWindowType");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getWindowType())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetWindowType");
    }

    @Test
    public void testGet() throws Exception {
        LOG.entering(CLASS, "testGet");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGet");
    }

    @Test
    public void testGetTimeouts() throws Exception {
        LOG.entering(CLASS, "testGetTimeouts");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getTimeouts())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetTimeouts");
    }

    @Test
    public void testSetTimeouts() throws Exception {
        LOG.entering(CLASS, "testSetTimeouts");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().setTimeouts(new Timeouts(Duration.ZERO, Duration.ZERO, Duration.ZERO)))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testSetTimeouts");
    }

    @Test
    public void testGoBack() throws Exception {
        LOG.entering(CLASS, "testGoBack");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().goBack())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGoBack");
    }

    @Test
    public void testGoForward() throws Exception {
        LOG.entering(CLASS, "testGoForward");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().goForward())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGoForward");
    }

    @Test
    public void testRefresh() throws Exception {
        LOG.entering(CLASS, "testRefresh");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().refresh())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testRefresh");
    }

    @Test
    public void testExecuteScript() throws Exception {
        LOG.entering(CLASS, "testExecuteScript");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().executeScript("return 'abc';", "[]", null, null))
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testExecuteScript");
    }

    @Test @Disabled("Can't get this to return")
    public void testExecuteAsyncScript() throws Exception {
        LOG.entering(CLASS, "testExecuteAsyncScript");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().executeAsyncScript("return abc;", "[]", null, Duration.ofSeconds(TIMEOUT), null))
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testExecuteAsyncScript");
    }

    @Test
    public void testFindElement() throws Exception {
        LOG.entering(CLASS, "testFindElement");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().findElement(Marionette.SearchMethod.ID, "menu_myaccount"))
            .thenApply(MarionetteUtil::toElement)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testFindElement");
    }

    @Test
    public void testGetActiveElement() throws Exception {
        LOG.entering(CLASS, "testGetActiveElement");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getActiveElement())
            .thenApply(MarionetteUtil::toElement)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetActiveElement");
    }

    @Test
    public void testAddCookie() throws Exception {
        LOG.entering(CLASS, "testAddCookie");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().addCookie(Json.createObjectBuilder()
                .add("name", "cookieName")
                .add("value", "cookieValue")
                .add("path", "/")
                .add("domain", ".swgas.com")
                .add("expires", LocalDateTime.now().plusDays(5).toEpochSecond(ZoneOffset.UTC))
                .build().toString()))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testAddCookie");
    }

    @Test
    public void testDeleteCookie() throws Exception {
        LOG.entering(CLASS, "testDeleteCookie");
        List<Cookie> cookies = MarionetteUtil.parseJsonArray(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getCookies())
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)        
            .get(TIMEOUT, TimeUnit.SECONDS)
        ).stream()
        .map(c -> new Cookie().fromJson(c.toString()))
        .collect(Collectors.toList());
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().deleteCookie(cookies.get(0).getName()))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testDeleteCookie");
    }

    @Test
    public void testDeleteAllCookies() throws Exception {
        LOG.entering(CLASS, "testDeleteAllCookies");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().deleteAllCookies())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testDeleteAllCookies");
    }

    @Test
    public void testGetCookies() throws Exception {
        LOG.entering(CLASS, "testGetCookies");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getCookies())
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetCookies");
    }

    @Test
    public void testTakeScreenshot() throws Exception {
        LOG.entering(CLASS, "testTakeScreenshot");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().takeScreenshot())
            .thenApply(MarionetteUtil::toStringValue)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testTakeScreenshot");
    }

    @Test
    public void testGetWindowRect() throws Exception {
        LOG.entering(CLASS, "testGetWindowRect");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().getWindowRect())
            .thenApply(MarionetteUtil::toRectangle)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testGetWindowRect");
    }

    @Test
    public void testSetWindowRect() throws Exception {
        LOG.entering(CLASS, "testSetWindowRect");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().setWindowRect(new Rectangle2D.Double(1.0d, 1.0d, 500.0d, 500.0d)))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testSetWindowRect");
    }

    @Test
    public void testMaximizeWindow() throws Exception {
        LOG.entering(CLASS, "testMaximizeWindow");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().maximizeWindow())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testMaximizeWindow");
    }

    @Test @Disabled("Not Yet Implemented")
    public void testFullscreen() throws Exception {
        LOG.entering(CLASS, "testFullscreen");
        LOG.info(
            session.getClient().get("https://myaccountdev.swgas.com/")
            .thenCompose(s -> session.getClient().fullscreen())
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS)
        );
        LOG.exiting(CLASS, "testFullscreen");
    }
}