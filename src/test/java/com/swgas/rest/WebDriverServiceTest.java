package com.swgas.rest;

import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class WebDriverServiceTest {
    private static final String CLASS = WebDriverServiceTest.class.getName();
    private static final Logger LOG   = Logger.getLogger(CLASS);
    
    public WebDriverServiceTest() {
    }

    /**
     * Test of newSession method, of class WebDriverService.
     */
    @Test
    public void testNewSession() {
        LOG.entering(CLASS, "testNewSession");
        WebDriverService instance = new WebDriverService();
        String result = instance.newSession();
        LOG.info(result);
        Assertions.assertTrue(null != result);
        
        instance.deleteSession(result);
        LOG.exiting(CLASS, "testNewSession");
    }

    /**
     * Test of deleteSession method, of class WebDriverService.
     */
    @Test
    public void testDeleteSession() {
        LOG.entering(CLASS, "testDeleteSession");
        String sessionId = "123";
        WebDriverService instance = new WebDriverService();
        try{
            String result = instance.deleteSession(sessionId);
            LOG.info(result);
        } catch(Exception e){
            LOG.fine(e.toString());
        }
        LOG.exiting(CLASS, "testDeleteSession");
    }

    /**
     * Test of getStatus method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetStatus() {
        LOG.info("getStatus");
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getStatus();
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getTimeouts method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetTimeouts() {
        LOG.info("getTimeouts");
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getTimeouts();
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of setTimeouts method, of class WebDriverService.
     */
    @Test @Disabled
    public void testSetTimeouts() {
        LOG.info("setTimeouts");
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.setTimeouts();
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of setUrl method, of class WebDriverService.
     */
    @Test @Disabled
    public void testSetUrl() {
        LOG.info("setUrl");
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.setUrl();
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of getUrl method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetUrl() {
        LOG.info("getUrl");
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getUrl();
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }

    /**
     * Test of back method, of class WebDriverService.
     */
    @Test @Disabled
    public void testBack() {
        LOG.info("back");
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.back();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.forward();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.refresh();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getTitle();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getWindow();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.closeWindow();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.setWindow();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getWindows();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.setFrame();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.parentFrame();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getWindowDimension();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.setWindowDimension();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.maximizeWindow();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.minimizeWindow();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.fullscreen();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getActiveElement();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.findElement();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.findElements();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.findElementFromElement();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.findElementsFromElement();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.isElementSelected();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getElementAttribute();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getElementProperty();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getElementCss();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getElementText();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getElementTagName();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getElementDimension();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.isElementEnabled();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.clickElement();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.clearElement();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.sendKeysToElement();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getPageSource();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.executeScript();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.executeScriptAsync();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getCookies();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getCookie();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.addCookie();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.removeCookie();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.removeCookies();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.performActions();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.releaseActions();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.dismissAlert();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.acceptAlert();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getAlertText();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.setAlertText();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getScreenshot();
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
        WebDriverService instance = new WebDriverService();
        String expResult = "";
        String result = instance.getElementScreenshot();
        Assertions.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.fail("The test case is a prototype.");
    }
    
}
