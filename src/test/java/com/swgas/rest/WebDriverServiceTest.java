package com.swgas.rest;

import com.swgas.marionette.Marionette;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;
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
            new ArrayList<String>(((HashMap<String, Session>) sessionsField.get(null)).keySet())
            .forEach(s -> {
                LOG.info(String.format("deleting session: %s", s));
                instance.deleteSession(s);
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
            String result = instance.newSession();
            LOG.info(result);
            Assertions.assertTrue(null != result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testNewSession", e);
            throw e;
        }
        LOG.exiting(CLASS, "testNewSession");
    }

    /**
     * Test of deleteSession method, of class WebDriverService.
     */
    @Test
    public void testDeleteSession() {
        LOG.entering(CLASS, "testDeleteSession");
        try{
            String result = instance.deleteSession(instance.newSession());
        } catch(Exception e){
            LOG.throwing(CLASS, "testDeleteSession", e);
            throw e;
        }
        LOG.exiting(CLASS, "testDeleteSession");
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
            String result = instance.getTimeouts(instance.newSession());
            Assertions.assertTrue(null != result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetTimeouts", e);
            throw e;
        }
        LOG.exiting(CLASS, "testGetTimeouts");
    }

    /**
     * Test of setTimeouts method, of class WebDriverService.
     */
    @Test
    public void testSetTimeouts() {
        LOG.entering(CLASS, "testSetTimeouts");
        try{
            String result = instance.setTimeouts(instance.newSession(), Marionette.Timeout.SCRIPT, "PT0.01S");
            Assertions.assertTrue(null != result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSetTimeouts", e);
            throw e;
        }
        LOG.exiting(CLASS, "testSetTimeouts");
    }

    /**
     * Test of setUrl method, of class WebDriverService.
     */
    @Test
    public void testSetUrl() {
        LOG.entering(CLASS, "testSetUrl");
        try{
            String result = instance.setUrl(instance.newSession(), "https://myaccountdev.swgas.com");
            Assertions.assertTrue(null != result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSetUrl", e);
            throw e;
        }
        LOG.exiting(CLASS, "testSetUrl");
    }

    /**
     * Test of getUrl method, of class WebDriverService.
     */
    @Test
    public void testGetUrl() {
        LOG.entering(CLASS, "testGetUrl");
        try{
            String sessionId = instance.newSession();
            String url = "https://myaccountdev.swgas.com";
            instance.setUrl(sessionId, url);
            String result = instance.getUrl(sessionId);
            Assertions.assertTrue(Objects.equals(url, result));
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetUrl", e);
            throw e;
        }
        LOG.exiting(CLASS, "testGetUrl");
    }

    /**
     * Test of back method, of class WebDriverService.
     */
    @Test @Disabled
    public void testBack() {
        LOG.info("back");
        
        String expResult = "";
        String result = instance.back("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of forward method, of class WebDriverService.
     */
    @Test @Disabled
    public void testForward() {
        LOG.info("forward");
        
        String expResult = "";
        String result = instance.forward("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of refresh method, of class WebDriverService.
     */
    @Test @Disabled
    public void testRefresh() {
        LOG.info("refresh");
        
        String expResult = "";
        String result = instance.refresh("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getTitle method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetTitle() {
        LOG.info("getTitle");
        
        String expResult = "";
        String result = instance.getTitle("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getWindow method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetWindow() {
        LOG.info("getWindow");
        
        String expResult = "";
        String result = instance.getWindow("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of closeWindow method, of class WebDriverService.
     */
    @Test @Disabled
    public void testCloseWindow() {
        LOG.info("closeWindow");
        
        String expResult = "";
        String result = instance.closeWindow("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of setWindow method, of class WebDriverService.
     */
    @Test @Disabled
    public void testSetWindow() {
        LOG.info("setWindow");
        
        String expResult = "";
        String result = instance.setWindow("","");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getWindows method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetWindows() {
        LOG.info("getWindows");
        
        String expResult = "";
        String result = instance.getWindows("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of setFrame method, of class WebDriverService.
     */
    @Test @Disabled
    public void testSetFrame() {
        LOG.info("setFrame");
        
        String expResult = "";
        String result = instance.setFrame("","");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of parentFrame method, of class WebDriverService.
     */
    @Test @Disabled
    public void testParentFrame() {
        LOG.info("parentFrame");
        
        String expResult = "";
        String result = instance.parentFrame("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getWindowDimension method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetWindowDimension() {
        LOG.info("getWindowDimension");
        
        String expResult = "";
        String result = instance.getWindowDimension("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of setWindowDimension method, of class WebDriverService.
     */
    @Test @Disabled
    public void testSetWindowDimension() {
        LOG.info("setWindowDimension");
        
        String expResult = "";
        String result = instance.setWindowDimension("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of maximizeWindow method, of class WebDriverService.
     */
    @Test @Disabled
    public void testMaximizeWindow() {
        LOG.info("maximizeWindow");
        
        String expResult = "";
        String result = instance.maximizeWindow("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of minimizeWindow method, of class WebDriverService.
     */
    @Test @Disabled
    public void testMinimizeWindow() {
        LOG.info("minimizeWindow");
        
        String expResult = "";
        String result = instance.minimizeWindow("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of fullscreen method, of class WebDriverService.
     */
    @Test @Disabled
    public void testFullscreen() {
        LOG.info("fullscreen");
        
        String expResult = "";
        String result = instance.fullscreen("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getActiveElement method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetActiveElement() {
        LOG.info("getActiveElement");
        
        String expResult = "";
        String result = instance.getActiveElement("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of findElement method, of class WebDriverService.
     */
    @Test @Disabled
    public void testFindElement() {
        LOG.info("findElement");
        
        String expResult = "";
        String result = instance.findElement("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of findElements method, of class WebDriverService.
     */
    @Test @Disabled
    public void testFindElements() {
        LOG.info("findElements");
        
        String expResult = "";
        String result = instance.findElements("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of findElementFromElement method, of class WebDriverService.
     */
    @Test @Disabled
    public void testFindElementFromElement() {
        LOG.info("findElementFromElement");
        
        String expResult = "";
        String result = instance.findElementFromElement("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of findElementsFromElement method, of class WebDriverService.
     */
    @Test @Disabled
    public void testFindElementsFromElement() {
        LOG.info("findElementsFromElement");
        
        String expResult = "";
        String result = instance.findElementsFromElement("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of isElementSelected method, of class WebDriverService.
     */
    @Test @Disabled
    public void testIsElementSelected() {
        LOG.info("isElementSelected");
        
        String expResult = "";
        String result = instance.isElementSelected("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getElementAttribute method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementAttribute() {
        LOG.info("getElementAttribute");
        
        String expResult = "";
        String result = instance.getElementAttribute("");
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
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
