package com.swgas.rest;

import com.swgas.exception.NotImplementedException;
import com.swgas.marionette.Marionette;
import com.swgas.marionette.MarionetteImpl;
import com.swgas.util.MarionetteUtil;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class WebDriverServiceTest {
    private static final String CLASS = WebDriverServiceTest.class.getName();
    private static final Logger LOG   = Logger.getLogger(CLASS);
    
    private WebDriverService instance;
    
    public WebDriverServiceTest() {
    }
    
    @BeforeEach
    public void beforeEach(){
        LOG.entering(CLASS, "beforeEach");
        instance = new WebDriverService();
        LOG.exiting(CLASS, "beforeEach");
    }
    
    @AfterEach
    public void afterEach() {
        LOG.entering(CLASS, "afterEach");
        try{
            Field sessionsField = instance.getClass().getDeclaredField("SESSIONS");
            sessionsField.setAccessible(true);
            Map<String, Session> sessions = (HashMap<String, Session>) sessionsField.get(null);
            new ArrayList<>(sessions.keySet())
            .forEach(s -> {
                try{
                    JsonObject result = MarionetteUtil.parseJsonObject(instance.deleteSession(s));
                    LOG.info(String.format("deleting session: %s result: %s", s, result));
                } catch(Exception e){
                    ((MarionetteImpl)sessions.get(s).getClient()).shutdown();
                    return;
                }
            });
        } catch(Exception e){            
            LOG.throwing(CLASS, "afterEach", e);
            
        }
        LOG.exiting(CLASS, "afterEach");
    }

    /**
     * Test of newSession method, of class WebDriverService.
     */
    @Test
    public void testNewSession() {
        LOG.entering(CLASS, "testNewSession");
        try{
            JsonObject result = MarionetteUtil.parseJsonObject(instance.newSession());
            Assertions.assertTrue(null != result, "result should not be null");
            LOG.exiting(CLASS, "testNewSession", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testNewSession", e);
            throw e;
        }
    }

    /**
     * Test of deleteSession method, of class WebDriverService.
     */
    @Test
    public void testDeleteSession() {
        LOG.entering(CLASS, "testDeleteSession");
        try{
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.deleteSession(sessionId));
            LOG.exiting(CLASS, "testDeleteSession", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testDeleteSession", e);
            throw e;
        }
    }

    /**
     * Test of getStatus method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetStatus() {
        LOG.entering(CLASS, "testGetStatus");
        String expResult = "";
        String result = instance.getStatus();
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
        LOG.exiting(CLASS, "testGetStatus");
    }

    /**
     * Test of getTimeouts method, of class WebDriverService.
     */
    @Test
    public void testGetTimeouts() {
        LOG.entering(CLASS, "testGetTimeouts");
        try{
            String session    = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getTimeouts(session));
            Assertions.assertTrue(null != result, "result should not be null");
            LOG.exiting(CLASS, "testGetTimeouts", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetTimeouts", e);
            throw e;
        }
    }

    /**
     * Test of setTimeouts method, of class WebDriverService.
     */
    @Test
    public void testSetTimeouts() {
        LOG.entering(CLASS, "testSetTimeouts");
        try{
            String session = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.setTimeouts(session, Marionette.Timeout.SCRIPT, "PT0.01S"));
            Assertions.assertTrue(null != result, "result should not be null");
            LOG.exiting(CLASS, "testSetTimeouts", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSetTimeouts", e);
            throw e;
        }
    }

    /**
     * Test of setUrl method, of class WebDriverService.
     */
    @Test
    public void testSetUrl() {
        LOG.entering(CLASS, "testSetUrl");
        try{
            String session = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.setUrl(session, "https://myaccountdev.swgas.com"));
            Assertions.assertTrue(null != result, "result should not be null");
            LOG.exiting(CLASS, "testSetUrl", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSetUrl", e);
            throw e;
        }
    }

    /**
     * Test of getUrl method, of class WebDriverService.
     */
    @Test
    public void testGetUrl() {
        LOG.entering(CLASS, "testGetUrl");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            String url = "https://myaccountdev.swgas.com/";
            instance.setUrl(sessionId, url);
            String result = MarionetteUtil.parseJsonObject(instance.getUrl(sessionId)).getString("url");
            Assertions.assertTrue(Objects.equals(url, result), String.format("\"%s\" should match \"%s\"", result, url));
            LOG.exiting(CLASS, "testGetUrl", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetUrl", e);
            throw e;
        }
    }

    /**
     * Test of back method, of class WebDriverService.
     */
    @Test
    public void testBack() {
        LOG.entering(CLASS, "testBack");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            String url       = "https://myaccountdev.swgas.com/agency";
            String expResult = "https://myaccountdev.swgas.com/";
            instance.setUrl(sessionId, expResult);
            instance.setUrl(sessionId, url);
            LOG.info(instance.back(sessionId));
            String result = MarionetteUtil.parseJsonObject(instance.getUrl(sessionId)).getString("url");
            Assertions.assertTrue(Objects.equals(expResult, result), String.format("%s should match %s", result, expResult));
            LOG.exiting(CLASS, "testBack", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testBack", e);
            throw e;
        }
    }

    /**
     * Test of forward method, of class WebDriverService.
     */
    @Test
    public void testForward() {
        LOG.entering(CLASS, "testForward");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            String url       = "https://myaccountdev.swgas.com/agency";
            String expResult = "https://myaccountdev.swgas.com/";
            instance.setUrl(sessionId, url);
            instance.setUrl(sessionId, expResult);
            instance.back(sessionId);
            LOG.info(instance.forward(sessionId));
            String result = MarionetteUtil.parseJsonObject(instance.getUrl(sessionId)).getString("url");
            Assertions.assertTrue(Objects.equals(expResult, result), String.format("%s should match %s", result, expResult));
            LOG.exiting(CLASS, "testForward", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testForward", e);
            throw e;
        }
    }

    /**
     * Test of refresh method, of class WebDriverService.
     */
    @Test
    public void testRefresh() {
        LOG.entering(CLASS, "testRefresh");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            String result    = instance.refresh(sessionId);
            Assertions.assertTrue(null != result, "result should not be null");
            LOG.exiting(CLASS, "testRefresh", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testRefresh", e);
            throw e;
        }
    }

    /**
     * Test of getTitle method, of class WebDriverService.
     */
    @Test
    public void testGetTitle() {
        LOG.entering(CLASS, "testGetTitle");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            String expResult = "Southwest Gas - MyAccount Home";
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String result = MarionetteUtil.parseJsonObject(instance.getTitle(sessionId)).getString("title");
            Assertions.assertTrue(Objects.equals(result, expResult), String.format("%s should match %s", result, expResult));
            LOG.exiting(CLASS, "testGetTitle", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetTitle", e);
            throw e;
        }
    }

    /**
     * Test of getWindow method, of class WebDriverService.
     */
    @Test
    public void testGetWindow() {
        LOG.entering(CLASS, "testGetWindow");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            String result    = MarionetteUtil.parseJsonObject(instance.getWindow(sessionId)).getString("window");
            Assertions.assertTrue(null != result, "result should not be null");
            LOG.exiting(CLASS, "testGetWindow", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetWindow", e);
            throw e;
        }
    }

    /**
     * Test of setWindow method, of class WebDriverService.
     */
    @Test
    public void testSetWindow() {
        LOG.entering(CLASS, "testSetWindow");
        try{
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            String expResult  = MarionetteUtil.parseJsonObject(instance.getWindow(sessionId)).getString("window");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.setWindow(sessionId, expResult));
            Assertions.assertTrue(null != result, "result should not be null");
            LOG.exiting(CLASS, "testSetWindow", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSetWindow", e);
            throw e;
        }
    }

    /**
     * Test of closeWindow method, of class WebDriverService.
     * webservice returns array
     */
    @Test
    public void testCloseWindow() {
        LOG.entering(CLASS, "testCloseWindow");
        try{
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String result = instance.closeWindow(sessionId);
            Assertions.assertTrue(null != result, "result should not be null");
            LOG.exiting(CLASS, "testCloseWindow", result);            
        } catch(Exception e){
            LOG.throwing(CLASS, "testCloseWindow", e);
            throw e;
        }
    }

    /**
     * Test of getWindows method, of class WebDriverService.
     * webservice returns array
     */
    @Test
    public void testGetWindows() {
        LOG.entering(CLASS, "testGetWindows");
        try{
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonArray result  = MarionetteUtil.parseJsonArray(instance.getWindows(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testGetWindows", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetWindows", e);
            throw e;
        }
    }

    /**
     * Test of setFrame method, of class WebDriverService.
     */
    @Test
    public void testSetFrame() {
        LOG.entering(CLASS, "testSetFrame");
        try{
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.setFrame(sessionId, null));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testSetFrame", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSetFrame", e);
            throw e;
        }
    }

    /**
     * Test of parentFrame method, of class WebDriverService.
     */
    @Test
    public void testParentFrame() {
        LOG.entering(CLASS, "testParentFrame");
        try{        
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.setParentFrame(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testSetFrame", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSetFrame", e);
            throw e;
        }
    }

    /**
     * Test of getWindowRect method, of class WebDriverService.
     */
    @Test
    public void testGetWindowRect() {
        LOG.entering(CLASS, "testGetWindowRect");
        try{        
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            Rectangle2D result = MarionetteUtil.parseRectangle(instance.getWindowRect(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testGetWindowRect", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetWindowRect", e);
            throw e;
        }
    }

    /**
     * Test of setWindowRect method, of class WebDriverService.
     */
    @Test
    public void testSetWindowRect() {
        LOG.entering(CLASS, "testSetWindowRect");
        try{
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.setWindowRect(sessionId
                , Json.createObjectBuilder().add("x",0).add("y",0).add("width",100).add("height",100).build().toString())
            );
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testSetWindowRect", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSetWindowRect", e);
            throw e;
        }
    }

    /**
     * Test of minimizeWindow method, of class WebDriverService.
     * Not Implemented in Marionette
     */
    @Test @Disabled("Not Yet Implemented")
    public void testMinimizeWindow() {
        LOG.entering(CLASS, "testMinimizeWindow");        
        try{
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.minimizeWindow(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testMinimizeWindow", result);
        } catch(NotImplementedException e){            
            LOG.exiting(CLASS, "testMinimizeWindow");
        } catch(Exception e){
            LOG.throwing(CLASS, "testMinimizeWindow", e);
            throw e;
        }
    }

    /**
     * Test of maximizeWindow method, of class WebDriverService.
     */
    @Test
    public void testMaximizeWindow() {
        LOG.entering(CLASS, "testMaximizeWindow");        
        try{
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.maximizeWindow(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testMaximizeWindow", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testMaximizeWindow", e);
            throw e;
        }
    }

    /**
     * Test of fullscreen method, of class WebDriverService.
     */
    @Test @Disabled("Not Yet Implemented")
    public void testFullscreen() {
        LOG.entering(CLASS, "testFullscreen");        
        try{
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.fullscreen(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testFullscreen", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testFullscreen", e);
            throw e;
        }
    }

    /**
     * Test of getActiveElement method, of class WebDriverService.
     */
    @Test
    public void testGetActiveElement() {
        LOG.entering(CLASS, "testGetActiveElement");
        try{
            String sessionId  = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getActiveElement(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testGetActiveElement", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetActiveElement", e);
            throw e;
        }
    }

    /**
     * Test of findElement method, of class WebDriverService.
     */
    @Test
    public void testFindElement() {
        LOG.entering(CLASS, "testFindElement");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.CSS_SELECTOR, "body"));
            Assertions.assertTrue(!result.getString("element").isEmpty());
            LOG.exiting(CLASS, "testFindElement", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testFindElement", e);
            throw e;
        }
    }

    /**
     * Test of findElements method, of class WebDriverService.
     */
    @Test
    public void testFindElements() {
        LOG.entering(CLASS, "testFindElements");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonArray result = MarionetteUtil.parseJsonArray(instance.findElements(sessionId, Marionette.SearchMethod.CSS_SELECTOR, "body"));
            Assertions.assertTrue(!result.getString(0).isEmpty());
            LOG.exiting(CLASS, "testFindElements", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testFindElements", e);
            throw e;
        }
    }

    /**
     * Test of findElementFromElement method, of class WebDriverService.
     */
    @Test
    public void testFindElementFromElement() {
        LOG.entering(CLASS, "testFindElementFromElement");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.CSS_SELECTOR, "body"));
            Assertions.assertTrue(!result.getString("element").isEmpty());
            result = MarionetteUtil.parseJsonObject(instance.findElementFromElement(sessionId, result.getString("element"), Marionette.SearchMethod.ID, "menu_myaccount"));
            Assertions.assertTrue(!result.getString("element").isEmpty());
            LOG.exiting(CLASS, "testFindElementFromElement", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testFindElementFromElement", e);
            throw e;
        }
    }

    /**
     * Test of findElementsFromElement method, of class WebDriverService.
     */
    @Test
    public void testFindElementsFromElement() {
        LOG.entering(CLASS, "testFindElementsFromElement");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.CSS_SELECTOR, "body"));
            Assertions.assertTrue(!result.getString("element").isEmpty());
            JsonArray elements = MarionetteUtil.parseJsonArray(instance.findElementsFromElement(sessionId, result.getString("element"), Marionette.SearchMethod.ID, "menu_myaccount"));
            Assertions.assertTrue(!elements.getString(0).isEmpty());
            LOG.exiting(CLASS, "testFindElementsFromElement", elements);
        } catch(Exception e){
            LOG.throwing(CLASS, "testFindElementsFromElement", e);
            throw e;
        }
    }

    /**
     * Test of isElementSelected method, of class WebDriverService.
     */
    @Test
    public void testIsElementSelected() {
        LOG.entering(CLASS, "testIsElementSelected");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.ID, "menu_myaccount")).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.isElementSelected(sessionId, elementId));
            Assertions.assertTrue(!result.getBoolean("selected"));
            LOG.exiting(CLASS, "testIsElementSelected", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testIsElementSelected", e);
            throw e;
        }
    }

    /**
     * Test of getElementAttribute method, of class WebDriverService.
     */
    @Test
    public void testGetElementAttribute() {
        LOG.entering(CLASS, "testGetElementAttribute");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.ID, "menu_myaccount")).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getElementAttribute(sessionId, elementId, "id"));
            Assertions.assertTrue(Objects.equals("menu_myaccount", result.getString("attribute")));
            LOG.exiting(CLASS, "testGetElementAttribute", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetElementAttribute", e);
            throw e;
        }
    }

    /**
     * Test of getElementProperty method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementProperty() {
        LOG.info("getElementProperty");
        
        String expResult = "";
        String result = instance.getElementProperty("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getElementCss method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementCss() {
        LOG.info("getElementCss");
        
        String expResult = "";
        String result = instance.getElementCss("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getElementText method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementText() {
        LOG.info("getElementText");
        
        String expResult = "";
        String result = instance.getElementText("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getElementTagName method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementTagName() {
        LOG.info("getElementTagName");
        
        String expResult = "";
        String result = instance.getElementTagName("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getElementDimension method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementDimension() {
        LOG.info("getElementDimension");
        
        String expResult = "";
        String result = instance.getElementDimension("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of isElementEnabled method, of class WebDriverService.
     */
    @Test @Disabled
    public void testIsElementEnabled() {
        LOG.info("isElementEnabled");
        
        String expResult = "";
        String result = instance.isElementEnabled("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of clickElement method, of class WebDriverService.
     */
    @Test @Disabled
    public void testClickElement() {
        LOG.info("clickElement");
        
        String expResult = "";
        String result = instance.clickElement("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of clearElement method, of class WebDriverService.
     */
    @Test @Disabled
    public void testClearElement() {
        LOG.info("clearElement");
        
        String expResult = "";
        String result = instance.clearElement("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of sendKeysToElement method, of class WebDriverService.
     */
    @Test @Disabled
    public void testSendKeysToElement() {
        LOG.info("sendKeysToElement");
        
        String expResult = "";
        String result = instance.sendKeysToElement("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getPageSource method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetPageSource() {
        LOG.info("getPageSource");
        
        String expResult = "";
        String result = instance.getPageSource("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of executeScript method, of class WebDriverService.
     */
    @Test @Disabled
    public void testExecuteScript() {
        LOG.info("executeScript");
        
        String expResult = "";
        String result = instance.executeScript("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of executeScriptAsync method, of class WebDriverService.
     */
    @Test @Disabled
    public void testExecuteScriptAsync() {
        LOG.info("executeScriptAsync");
        
        String expResult = "";
        String result = instance.executeScriptAsync("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getCookies method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetCookies() {
        LOG.info("getCookies");
        
        String expResult = "";
        String result = instance.getCookies("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getCookie method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetCookie() {
        LOG.info("getCookie");
        
        String expResult = "";
        String result = instance.getCookie("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of addCookie method, of class WebDriverService.
     */
    @Test @Disabled
    public void testAddCookie() {
        LOG.info("addCookie");
        
        String expResult = "";
        String result = instance.addCookie("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of removeCookie method, of class WebDriverService.
     */
    @Test @Disabled
    public void testRemoveCookie() {
        LOG.info("removeCookie");
        
        String expResult = "";
        String result = instance.removeCookie("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of removeCookies method, of class WebDriverService.
     */
    @Test @Disabled
    public void testRemoveCookies() {
        LOG.info("removeCookies");
        
        String expResult = "";
        String result = instance.removeCookies("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of performActions method, of class WebDriverService.
     */
    @Test @Disabled
    public void testPerformActions() {
        LOG.info("performActions");
        
        String expResult = "";
        String result = instance.performActions("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of releaseActions method, of class WebDriverService.
     */
    @Test @Disabled
    public void testReleaseActions() {
        LOG.info("releaseActions");
        
        String expResult = "";
        String result = instance.releaseActions("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of dismissAlert method, of class WebDriverService.
     */
    @Test @Disabled
    public void testDismissAlert() {
        LOG.info("dismissAlert");
        
        String expResult = "";
        String result = instance.dismissAlert("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of acceptAlert method, of class WebDriverService.
     */
    @Test @Disabled
    public void testAcceptAlert() {
        LOG.info("acceptAlert");
        
        String expResult = "";
        String result = instance.acceptAlert("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getAlertText method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetAlertText() {
        LOG.info("getAlertText");
        
        String expResult = "";
        String result = instance.getAlertText("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of setAlertText method, of class WebDriverService.
     */
    @Test @Disabled
    public void testSetAlertText() {
        LOG.info("setAlertText");
        
        String expResult = "";
        String result = instance.setAlertText("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getScreenshot method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetScreenshot() {
        LOG.info("getScreenshot");
        
        String expResult = "";
        String result = instance.getScreenshot("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getElementScreenshot method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementScreenshot() {
        LOG.info("getElementScreenshot");
        
        String expResult = "";
        String result = instance.getElementScreenshot("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }
    
}
