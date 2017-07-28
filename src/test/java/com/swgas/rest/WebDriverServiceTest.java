package com.swgas.rest;

import com.swgas.exception.NotImplementedException;
import com.swgas.marionette.Marionette;
import com.swgas.util.MarionetteUtil;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class WebDriverServiceTest {
    private static final String CLASS = WebDriverServiceTest.class.getName();
    private static final Logger LOG   = Logger.getLogger(CLASS);
    private static final String BASE_URI = "http://localhost:8080";
    private static       Server server;
    private              String sessionId;
    private WebDriverService instance;
    
    public WebDriverServiceTest() {
    }
    
    @BeforeAll
    public static void beforeAll(){
        LOG.entering(CLASS, "berforeAll");
        server = new Server();
        LOG.entering(CLASS, "beforeAll", server);
    }
    
    @AfterAll
    public static void afterAll(){
        server.close();
    }
    
    @BeforeEach
    public void beforeEach() throws Exception {
        LOG.entering(CLASS, "beforeEach");
        try{            
            sessionId = MarionetteUtil.parseJsonObject(POST(getUri("newSession"), "").body()).getString("sessionId");
            LOG.exiting(CLASS, "beforeEach");
        } catch(Exception e){
            LOG.throwing(CLASS, "beforeEach", e);
            throw e;
        }
    }
    
    @AfterEach
    public void afterEach() {
        LOG.entering(CLASS, "afterEach");
        DELETE(getUri("deleteSession", sessionId), "");
        LOG.exiting(CLASS, "afterEach");
    }
    
    private static HttpResponse<String> GET(URI uri){
        LOG.entering(CLASS, "GET", uri);
        HttpResponse<String> result = null;
        try{
            result = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .GET().build()
                , HttpResponse.BodyHandler.asString()
            );
            LOG.finest(String.format("Status: %d%nHeaders: %s%nBody: %s", result.statusCode(), result.headers().map(), result.body()));
        } catch(IOException | InterruptedException e){
            LOG.logp(Level.WARNING, CLASS, "GET", e.toString(), e);
        }
        LOG.exiting(CLASS, "GET", result);
        return result;
    }
    
    private static HttpResponse<String> POST(URI uri, String body){
        LOG.entering(CLASS, "POST", Stream.of(uri, body).toArray());
        HttpResponse<String> result = null;
        try{
            result = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .headers("Accept","application/json","Content-Type","application/json")
                .POST(HttpRequest.BodyProcessor.fromString(body))
                .build()
                , HttpResponse.BodyHandler.asString()
            );
            LOG.finest(String.format("Status: %d%nHeaders: %s%nBody: %s", result.statusCode(), result.headers().map(), result.body()));
        } catch(IOException | InterruptedException e){
            LOG.logp(Level.WARNING, CLASS, "POST", e.toString(), e);
        }
        LOG.exiting(CLASS, "POST", result);
        return result;
    }
    
    private static HttpResponse<String> DELETE(URI uri, String body){
        LOG.entering(CLASS, "DELETE", Stream.of(uri, body).toArray());
        HttpResponse<String> result = null;
        try{
            result = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE(HttpRequest.BodyProcessor.fromString(body))
                .build()
                , HttpResponse.BodyHandler.asString());
        } catch(IOException | InterruptedException e){
            LOG.logp(Level.WARNING, CLASS, "DELETE", e.toString(), e);
        }
        LOG.exiting(CLASS, "DELETE", result);
        return result;
    }
    
    private static URI getUri(String method, Object... params){        
        return UriBuilder.fromUri(String.format("%s%s%s"
            , BASE_URI
            , WebDriverService.class.getAnnotation(Path.class).value()
            , Arrays.stream(WebDriverService.class.getDeclaredMethods()).filter(m -> Objects.equals(m.getName(), method)).findFirst().get().getAnnotation(Path.class).value()
        )).build(params);
    }
    
    private void setUrl(String url){
        POST(getUri("setUrl", sessionId), MarionetteUtil.createJson("url",url));
    }

    /**
     * Test of newSession method, of class WebDriverService.
     */
    @Test @Disabled("Only one at a time")
    public void testNewSession() {
        LOG.entering(CLASS, "testNewSession");
        try{
            String result = MarionetteUtil.parseJsonObject(POST(getUri("newSession"), "").body()).getString("session");
            LOG.exiting(CLASS, "testNewSession", result);
            DELETE(getUri("deleteSession", sessionId), "").body();
        } catch(Exception e){
            LOG.throwing(CLASS, "testNewSession", e);
            throw e;
        }
    }

    /**
     * Test of deleteSession method, of class WebDriverService.
     */
    @Test @Disabled("Only one at a time")
    public void testDeleteSession() {
        LOG.entering(CLASS, "testDeleteSession");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(POST(getUri("newSession"), "").body()).getString("session");
            String result = DELETE(getUri("deleteSession", sessionId), "").body();
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
     * RETURN {"implicit":0,"pageLoad":300000,"script":30000}
     */
    @Test
    public void testGetTimeouts() throws Exception{
        LOG.entering(CLASS, "testGetTimeouts");
        try{
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("getTimeouts", sessionId)).body());
            Assertions.assertTrue(Stream.of("implicit", "pageLoad", "script").allMatch(result::containsKey), String.format("result:(%s) should contain \"implicit\", \"pageLoad\", \"script\"", result));
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
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("setTimeouts", sessionId)
                , Json.createObjectBuilder()
                .add("timeout", Marionette.Timeout.SCRIPT.name())
                .add("duration", "PT0.01S")
                .build().toString()
            ).body());
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
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("setUrl", sessionId)
                , MarionetteUtil.createJson("url", "https://myaccountdev.swgas.com")
            ).body());
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
            String url = "https://myaccountdev.swgas.com/";
            setUrl(url);
            String result = MarionetteUtil.parseJsonObject(GET(getUri("getUrl", sessionId)).body()).getString("url");
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
            String url       = "https://myaccountdev.swgas.com/agency";
            String expResult = "https://myaccountdev.swgas.com/";
            setUrl(expResult);
            setUrl(url);
            POST(getUri("back", sessionId), "");
            String result = MarionetteUtil.parseJsonObject(GET(getUri("getUrl", sessionId)).body()).getString("url");
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
            String url       = "https://myaccountdev.swgas.com/agency";
            String expResult = "https://myaccountdev.swgas.com/";
            setUrl(url);
            setUrl(expResult);
            POST(getUri("back", sessionId), "");
            POST(getUri("forward", sessionId), "");
            String result = MarionetteUtil.parseJsonObject(GET(getUri("getUrl", sessionId)).body()).getString("url");
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
            String result = POST(getUri("refresh", sessionId), "").body();
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
            String expResult = "Southwest Gas - MyAccount Home";
            setUrl("https://myaccountdev.swgas.com/");
            String result = MarionetteUtil.parseJsonObject(GET(getUri("getTitle", sessionId)).body()).getString("title");
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
            String result    = MarionetteUtil.parseJsonObject(GET(getUri("getWindow", sessionId)).body()).getString("window");
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
            String expResult  = MarionetteUtil.parseJsonObject(GET(getUri("getWindow", sessionId)).body()).getString("window");
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("setWindow", sessionId), MarionetteUtil.createJson("window", expResult)).body());
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
    @Test @Disabled
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
        LOG.entering(CLASS, "testGetElementProperty");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.ID, "menu_myaccount")).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getElementProperty(sessionId, elementId, "id"));
            Assertions.assertTrue(Objects.equals("menu_myaccount", result.getString("property")));
            LOG.exiting(CLASS, "testGetElementProperty", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetElementProperty", e);
            throw e;
        }
    }

    /**
     * Test of getElementCss method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementCss() {
        LOG.entering(CLASS, "testGetElementCss");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.ID, "menu_myaccount")).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getElementCssProperty(sessionId, elementId, "width"));
            Assertions.assertTrue(null != result.getString("css"));
            LOG.exiting(CLASS, "testGetElementCss", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetElementCss", e);
            throw e;
        }
    }

    /**
     * Test of getElementText method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementText() {
        LOG.entering(CLASS, "testGetElementText");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.ID, "menu_myaccount")).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getElementText(sessionId, elementId));
            Assertions.assertTrue(null != result.getString("text"));
            LOG.exiting(CLASS, "testGetElementText", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetElementText", e);
            throw e;
        }
    }

    /**
     * Test of getElementTagName method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementTagName() {
        LOG.entering(CLASS, "testGetElementTagName");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.ID, "menu_myaccount")).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getElementTagName(sessionId, elementId));
            Assertions.assertTrue(null != result.getString("tag"));
            LOG.exiting(CLASS, "testGetElementTagName", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetElementTagName", e);
            throw e;
        }
    }

    /**
     * Test of getElementDimension method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementRect() {
        LOG.entering(CLASS, "testGetElementRect");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId  = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.ID, "menu_myaccount")).getString("element");
            Rectangle2D result = MarionetteUtil.parseRectangle(instance.getElementRect(sessionId, elementId));
            Assertions.assertTrue(result.getWidth() > 0.0d, String.format("width(%f) should be greater than zero", result.getWidth()));
            LOG.exiting(CLASS, "testGetElementRect", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetElementRect", e);
            throw e;
        }
    }

    /**
     * Test of isElementEnabled method, of class WebDriverService.
     */
    @Test @Disabled
    public void testIsElementEnabled() {
        LOG.entering(CLASS, "testIsElementEnabled");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId  = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.ID, "menu_myaccount")).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.isElementEnabled(sessionId, elementId));
            Assertions.assertTrue(result.getBoolean("enabled"));
            LOG.exiting(CLASS, "testIsElementEnabled", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testIsElementEnabled", e);
            throw e;
        }
    }

    /**
     * Test of clickElement method, of class WebDriverService.
     */
    @Test @Disabled
    public void testClickElement() {
        LOG.entering(CLASS, "testClickElement");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId  = MarionetteUtil.parseJsonArray(instance.findElements(sessionId, Marionette.SearchMethod.ID, "menu_myaccount")).getString(1);
            JsonObject result = MarionetteUtil.parseJsonObject(instance.clickElement(sessionId, elementId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testClickElement", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testClickElement", e);
            throw e;
        }
    }

    /**
     * Test of clearElement method, of class WebDriverService.
     */
    @Test @Disabled
    public void testClearElement() {
        LOG.entering(CLASS, "testClearElement");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId  = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.CSS_SELECTOR, "input[name='username']")).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.clearElement(sessionId, elementId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testClearElement", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testClearElement", e);
            throw e;
        }
    }

    /**
     * Test of sendKeysToElement method, of class WebDriverService.
     */
    @Test @Disabled
    public void testSendKeysToElement() {
        LOG.entering(CLASS, "testSendKeysToElement");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String elementId  = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.CSS_SELECTOR, "input[name='username']")).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.sendKeysToElement(sessionId, elementId, "userName"));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testSendKeysToElement", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSendKeysToElement", e);
            throw e;
        }
    }

    /**
     * Test of getPageSource method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetPageSource() {
        LOG.entering(CLASS, "testGetPageSource");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/maintenance/");
            String result = MarionetteUtil.parseJsonObject(instance.getPageSource(sessionId)).getString("source");
            Assertions.assertTrue(!result.isEmpty());
            LOG.exiting(CLASS, "testGetPageSource", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetPageSource", e);
            throw e;
        }
    }

    /**
     * Test of executeScript method, of class WebDriverService.
     */
    @Test @Disabled
    public void testExecuteScript() {
        LOG.entering(CLASS, "testExecuteScript");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.executeScript(sessionId, "return document.querySelector('#menu_myaccount')", "[]"));
            Assertions.assertTrue(null != result.get("return"));
            LOG.exiting(CLASS, "testExecuteScript", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testExecuteScript", e);
            throw e;
        }
    }

    /**
     * Test of executeScriptAsync method, of class WebDriverService.
     */
    @Test @Disabled("Can't get this to return")
    public void testExecuteScriptAsync() {
        LOG.entering(CLASS, "testExecuteScriptAsync");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.executeScriptAsync(sessionId, "setTimeout(function(){return 1;}, 500);", "[]"));
            Assertions.assertTrue(null != result.get("return"));
            LOG.exiting(CLASS, "testExecuteScriptAsync", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testExecuteScriptAsync", e);
            throw e;
        }
    }

    /**
     * Test of getCookies method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetCookies() {
        LOG.entering(CLASS, "testGetCookies");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonArray result = MarionetteUtil.parseJsonArray(instance.getCookies(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testGetCookies", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetCookies", e);
            throw e;
        }
    }

    /**
     * Test of getCookie method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetCookie() {
        LOG.entering(CLASS, "testGetCookie");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getCookie(sessionId, "cookieName"));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testGetCookie", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetCookie", e);
            throw e;
        }
    }

    /**
     * Test of addCookie method, of class WebDriverService.
     */
    @Test @Disabled
    public void testAddCookie() {
        LOG.entering(CLASS, "testAddCookie");
        try{
            String cookie = Json.createObjectBuilder()
            .add("name", "cookieName")
            .add("value", "cookieValue")
            .add("path", "/")
            .add("domain", ".swgas.com")
            .add("expires", LocalDateTime.now().plusDays(5).toEpochSecond(ZoneOffset.UTC))
            .build().toString();
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.addCookie(sessionId, cookie));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testAddCookie", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testAddCookie", e.getCause());
            throw e;
        }
    }

    /**
     * Test of deleteCookie method, of class WebDriverService.
     */
    @Test @Disabled
    public void testDeleteCookie() {
        LOG.entering(CLASS, "testDeleteCookie");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.deleteCookie(sessionId, "cookieName"));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testDeleteCookie", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testDeleteCookie", e);
            throw e;
        }
    }

    /**
     * Test of testDeleteAllCookies method, of class WebDriverService.
     */
    @Test @Disabled
    public void testDeleteAllCookies() {
        LOG.entering(CLASS, "testDeleteAllCookies");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.deleteAllCookies(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testDeleteAllCookies", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testDeleteAllCookies", e);
            throw e;
        }
    }

    /**
     * Test of performActions method, of class WebDriverService.
     */
    @Test @Disabled("Not Implemented")
    public void testPerformActions() {
        LOG.entering(CLASS, "testPerformActions");
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.performActions(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testPerformActions", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testPerformActions", e);
            throw e;
        }
    }

    /**
     * Test of releaseActions method, of class WebDriverService.
     */
    @Test @Disabled("Not Implemented")
    public void testReleaseActions() {
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.releaseActions(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testReleaseActions", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testReleaseActions", e);
            throw e;
        }
    }

    /**
     * Test of dismissAlert method, of class WebDriverService.
     */
    @Test @Disabled
    public void testDismissAlert() {
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            instance.executeScript(sessionId, "window.alert('alert');", "[]");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.dismissAlert(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testDismissAlert", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testDismissAlert", e);
            throw e;
        }
    }

    /**
     * Test of acceptAlert method, of class WebDriverService.
     */
    @Test @Disabled
    public void testAcceptAlert() {
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            instance.executeScript(sessionId, "window.alert('alert');", "[]");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.acceptAlert(sessionId));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testAcceptAlert", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testAcceptAlert", e);
            throw e;
        }
    }

    /**
     * Test of getAlertText method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetAlertText() {
        String expected = "alert";
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            instance.executeScript(sessionId, String.format("window.alert('%s');", expected), "[]");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getAlertText(sessionId));
            Assertions.assertTrue(Objects.equals(expected, result.getString("text")), String.format("result should be %s but was %s", expected, result.getString("text")));
            LOG.exiting(CLASS, "testGetAlertText", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetAlertText", e);
            throw e;
        }
    }

    /**
     * Test of setAlertText method, of class WebDriverService.
     */
    @Test @Disabled
    public void testSetAlertText() {
        String expected = "trela";
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            instance.executeScript(sessionId, "window.prompt('alert');", "[]");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.setAlertText(sessionId, expected));
            Assertions.assertTrue(null != result);
            LOG.exiting(CLASS, "testSetAlertText", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testSetAlertText", e);
            throw e;
        }
    }

    /**
     * Test of getScreenshot method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetScreenshot() {
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getScreenshot(sessionId));
            Assertions.assertTrue(!result.getString("screenshot","").isEmpty());
            LOG.exiting(CLASS, "testGetScreenshot", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetScreenshot", e);
            throw e;
        }
    }

    /**
     * Test of getElementScreenshot method, of class WebDriverService.
     */
    @Test @Disabled
    public void testGetElementScreenshot() {
        try{
            String sessionId = MarionetteUtil.parseJsonObject(instance.newSession()).getString("sessionId");
            instance.setUrl(sessionId, "https://myaccountdev.swgas.com/");
            String element = MarionetteUtil.parseJsonObject(instance.findElement(sessionId, Marionette.SearchMethod.CSS_SELECTOR, "#sticky-menu #menu_myaccount")).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(instance.getElementScreenshot(sessionId, element));
            Assertions.assertTrue(!result.getString("screenshot","").isEmpty());
            LOG.exiting(CLASS, "testGetElementScreenshot", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetElementScreenshot", e);
            throw e;
        }
    }    
}