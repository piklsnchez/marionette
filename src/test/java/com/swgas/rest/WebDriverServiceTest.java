package com.swgas.rest;

import com.swgas.exception.NotImplementedException;
import com.swgas.marionette.Marionette;
import com.swgas.model.Cookie;
import com.swgas.model.Status;
import com.swgas.model.Timeouts;
import com.swgas.model.WebElement;
import com.swgas.util.MarionetteUtil;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
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
    private static final String CLASS    = WebDriverServiceTest.class.getName();
    private static final Logger LOG      = Logger.getLogger(CLASS);
    private static final String BASE_URI = "http://localhost:8008";
    private static final String URL      = "https://myaccountdev.swgas.com/";
    private static       Server server;
    private              String sessionId;
    
    public WebDriverServiceTest() {
    }
    
    @BeforeAll
    public static void beforeAll(){
        LOG.entering(CLASS, "berforeAll");
        try{
            server = new Server();
        } catch(Throwable e){
            LOG.throwing(CLASS, "beforeAll", e);
            throw e;
        }
        LOG.exiting(CLASS, "beforeAll", server);
    }
    
    @AfterAll
    public static void afterAll(){
        server.close();
    }
    
    @BeforeEach
    public void beforeEach() throws Exception {
        LOG.entering(CLASS, "beforeEach");
        try{
            HttpResponse<String> response = POST(getUri("newSession"), "");
            if(200 != response.statusCode()){
                Exception e = new WebApplicationException(response.body(), response.statusCode());
                LOG.throwing(CLASS, "beforeEach", e);
                throw e;
            }
            sessionId = MarionetteUtil.parseJsonObject(response.body()).getString("sessionId");
            LOG.exiting(CLASS, "beforeEach");
        } catch(Exception e){
            LOG.throwing(CLASS, "beforeEach", e);
            throw e;
        }
    }
    
    @AfterEach
    public void afterEach() {
        LOG.entering(CLASS, "afterEach");
        if(null != sessionId){
            DELETE(getUri("deleteSession", sessionId), "");
        } else {
            LOG.warning("SessionId is null");
        }
        LOG.exiting(CLASS, "afterEach");
    }
    
    private static HttpResponse<String> GET(URI uri){
        LOG.entering(CLASS, "GET", uri);
        HttpResponse<String> result = null;
        try{
            result = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(uri)
                .version(HttpClient.Version.HTTP_2)
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
                .version(HttpClient.Version.HTTP_2)
                .headers("Accept",      "application/json"
                        ,"Content-Type","application/json")
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
                .version(HttpClient.Version.HTTP_2)
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
    @Test
    public void testGetStatus() {
        LOG.entering(CLASS, "testGetStatus");
        try{
            Status status = new Status().fromJson(GET(getUri("getStatus", sessionId)).body());
            Assertions.assertTrue(null != status);
        LOG.exiting(CLASS, "testGetStatus");
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetStatus", e);
            throw e;
        }
    }

    /**
     * Test of getTimeouts method, of class WebDriverService.
     * RETURN {"implicit":0,"pageLoad":300000,"script":30000}
     */
    @Test
    public void testGetTimeouts() throws Exception {
        LOG.entering(CLASS, "testGetTimeouts");
        try{
            Timeouts timeouts = new Timeouts().fromJson(GET(getUri("getTimeouts", sessionId)).body());
            Assertions.assertTrue(null != timeouts);
            LOG.exiting(CLASS, "testGetTimeouts", timeouts);
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
                , new Timeouts(Duration.of(30, ChronoUnit.SECONDS), Duration.ZERO, Duration.ZERO).toJson()
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
            String url = URL;
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
            String url       = String.format("%sagency", URL);
            String expResult = URL;
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
            String url       = String.format("%sagency", URL);
            String expResult = URL;
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
            setUrl(URL);
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
            String result = MarionetteUtil.parseJsonObject(GET(getUri("getWindow", sessionId)).body()).getString("handle");
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
            String expResult  = MarionetteUtil.parseJsonObject(GET(getUri("getWindow", sessionId)).body()).getString("handle");
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("setWindow", sessionId), MarionetteUtil.createJson("handle", expResult)).body());
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
            setUrl(URL);
            JsonArray result = MarionetteUtil.parseJsonArray(DELETE(getUri("closeWindow", sessionId), "").body());
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
            JsonArray result  = MarionetteUtil.parseJsonArray(GET(getUri("getWindows", sessionId)).body());
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
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("setFrame", sessionId), Json.createObjectBuilder().addNull("id").build().toString()).body());
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
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("setParentFrame", sessionId), "").body());
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
            Rectangle2D result = MarionetteUtil.parseRectangle(GET(getUri("getWindowRect", sessionId)).body());
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
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("setWindowRect", sessionId)
                , Json.createObjectBuilder().add("x",0).add("y",0).add("width",100).add("height",100).build().toString()).body()
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
    @Test
    @Disabled("Not Yet Implemented")
    public void testMinimizeWindow() {
        LOG.entering(CLASS, "testMinimizeWindow");        
        try{
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("minimizeWindow", sessionId), "").body());
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
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("maximizeWindow", sessionId), "").body());
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
    @Test //@Disabled("Not Yet Implemented")
    public void testFullscreen() {
        LOG.entering(CLASS, "testFullscreen");        
        try{
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("fullscreen", sessionId), "").body());
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
            WebElement element = new WebElement().fromJson(GET(getUri("getActiveElement", sessionId)).body());
            Assertions.assertTrue(null != element);
            LOG.exiting(CLASS, "testGetActiveElement", element);
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
            setUrl(URL);
            WebElement element = new WebElement().fromJson(POST(
                getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.CSS_SELECTOR.name()).add("value", "body").build().toString()
            ).body());
            Assertions.assertTrue(null != element.getId());
            LOG.exiting(CLASS, "testFindElement", element);
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
            setUrl(URL);
            JsonArray result = MarionetteUtil.parseJsonArray(POST(
                getUri("findElements", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.CSS_SELECTOR.name()).add("value", "body").build().toString()
            ).body());
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
            setUrl(URL);
            WebElement element = new WebElement().fromJson(POST(
                getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.CSS_SELECTOR.name()).add("value", "body").build().toString()
            ).body());
            Assertions.assertTrue(!element.getId().isEmpty(), String.format("result: %s", element));
            element = new WebElement().fromJson(POST(
                getUri("findElementFromElement" ,sessionId, element.getId())
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body());
            Assertions.assertTrue(!element.getId().isEmpty());
            LOG.exiting(CLASS, "testFindElementFromElement", element);
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
            setUrl(URL);
            WebElement element = new WebElement().fromJson(POST(
                getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.CSS_SELECTOR.name()).add("value", "body").build().toString()
            ).body());
            Assertions.assertTrue(!element.getId().isEmpty(), String.format("result: %s", element));
            JsonArray elements = MarionetteUtil.parseJsonArray(POST(
                getUri("findElementsFromElement", sessionId, element.getId())
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body());
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
            setUrl(URL);
            WebElement element = new WebElement().fromJson(POST(getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body());
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("isElementSelected", sessionId, element.getId())).body());
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
            setUrl(URL);
            String elementId = new WebElement().fromJson(POST(getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body()).getId();
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("getElementAttribute", sessionId, elementId, "id")).body());
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
    @Test
    public void testGetElementProperty() {
        LOG.entering(CLASS, "testGetElementProperty");
        try{
            setUrl(URL);
            String elementId = new WebElement().fromJson(POST(getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body()).getId();
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("getElementProperty", sessionId, elementId, "id")).body());
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
    @Test
    public void testGetElementCss() {
        LOG.entering(CLASS, "testGetElementCss");
        try{
            setUrl(URL);
            String elementId = new WebElement().fromJson(POST(getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body()).getId();
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("getElementCssProperty", sessionId, elementId, "width")).body());
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
    @Test
    public void testGetElementText() {
        LOG.entering(CLASS, "testGetElementText");
        try{
            setUrl(URL);
            String elementId = new WebElement().fromJson(POST(getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body()).getId();
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("getElementText", sessionId, elementId)).body());
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
    @Test
    public void testGetElementTagName() {
        LOG.entering(CLASS, "testGetElementTagName");
        try{
            setUrl(URL);
            String elementId = new WebElement().fromJson(POST(getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body()).getId();
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("getElementTagName", sessionId, elementId)).body());
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
    @Test
    public void testGetElementRect() {
        LOG.entering(CLASS, "testGetElementRect");
        try{
            setUrl(URL);
            String elementId  = new WebElement().fromJson(POST(getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body()).getId();
            Rectangle2D result = MarionetteUtil.parseRectangle(GET(getUri("getElementRect", sessionId, elementId)).body());
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
    @Test
    public void testIsElementEnabled() {
        LOG.entering(CLASS, "testIsElementEnabled");
        try{
            setUrl(URL);
            String elementId  = new WebElement().fromJson(POST(getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body()).getId();
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("isElementEnabled", sessionId, elementId)).body());
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
    @Test
    public void testClickElement() {
        LOG.entering(CLASS, "testClickElement");
        try{
            setUrl(URL);
            String elementId  = MarionetteUtil.parseJsonArray(POST(
                getUri("findElements", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.ID.name()).add("value", "menu_myaccount").build().toString()
            ).body()).getString(1);
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("clickElement", sessionId, elementId), "").body());
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
    @Test
    public void testClearElement() {
        LOG.entering(CLASS, "testClearElement");
        try{
            setUrl(URL);
            String elementId  = new WebElement().fromJson(POST(
                getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.CSS_SELECTOR.name()).add("value", "input[name='username']").build().toString()
            ).body()).getId();
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("clearElement", sessionId, elementId), "").body());
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
    @Test
    public void testSendKeysToElement() {
        LOG.entering(CLASS, "testSendKeysToElement");
        try{
            setUrl(URL);
            String elementId  =new WebElement().fromJson(POST(
                getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.CSS_SELECTOR.name()).add("value", "input[name='username']").build().toString()
            ).body()).getId();
            JsonObject result = MarionetteUtil.parseJsonObject(POST(
                getUri("sendKeysToElement", sessionId, elementId)
                , MarionetteUtil.createJson("keys", "userName")
            ).body());
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
    @Test
    public void testGetPageSource() {
        LOG.entering(CLASS, "testGetPageSource");
        try{
            setUrl(String.format("%smaintenance/", URL));
            String result = MarionetteUtil.parseJsonObject(GET(getUri("getPageSource", sessionId)).body()).getString("source");
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
    @Test
    public void testExecuteScript() {
        LOG.entering(CLASS, "testExecuteScript");
        try{
            setUrl(URL);
            JsonObject result = MarionetteUtil.parseJsonObject(POST(
                getUri("executeScript", sessionId)
                , Json.createObjectBuilder().add("script", "return document.querySelector('#menu_myaccount')").add("args", JsonValue.EMPTY_JSON_ARRAY).build().toString()
            ).body());
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
            setUrl(URL);
            JsonObject result = MarionetteUtil.parseJsonObject(POST(
                getUri("executeScriptAsync", sessionId)
                , Json.createObjectBuilder().add("script", "setTimeout(function(){return 1;}, 500);").add("args", "[]").build().toString()
            ).body());
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
    @Test
    public void testGetCookies() {
        LOG.entering(CLASS, "testGetCookies");
        try{
            Cookie cookie = new Cookie("cookieName1", "cookieValue", "/", ".swgas.com", true, true, LocalDateTime.now().plusDays(5));
            setUrl(URL);
            POST(getUri("addCookie", sessionId), cookie.toJson());
            cookie.setName("cookieName2");
            POST(getUri("addCookie", sessionId), cookie.toJson());            
            List<Cookie> result = MarionetteUtil.parseJsonArray(GET(getUri("getCookies", sessionId)).body())
            .stream()
            .map(c -> new Cookie().fromJson(c.toString()))
            .collect(Collectors.toList());
            Assertions.assertTrue(result.stream().anyMatch(c -> Objects.equals("cookieName1", c.getName())));
            LOG.exiting(CLASS, "testGetCookies", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetCookies", e);
            throw e;
        }
    }

    /**
     * Test of getCookie method, of class WebDriverService.
     */
    @Test
    public void testGetCookie() {
        LOG.entering(CLASS, "testGetCookie");
        try{
            Cookie cookie = new Cookie("cookieName", "cookieValue", "/", ".swgas.com", false, false, LocalDateTime.now().plusDays(5));
            setUrl(URL);
            POST(getUri("addCookie", sessionId), cookie.toJson());
            Cookie result = new Cookie().fromJson(GET(getUri("getCookie", sessionId, "cookieName")).body());
            Assertions.assertTrue("cookieName".equals(result.getName()));
            DELETE(getUri("deleteCookie", sessionId, "cookieName"), "");
            LOG.info(
                GET(getUri("getCookie", sessionId, "cookieName")).body()
            );
            LOG.exiting(CLASS, "testGetCookie", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetCookie", e);
            throw e;
        }
    }

    /**
     * Test of addCookie method, of class WebDriverService.
     */
    @Test
    public void testAddCookie() {
        LOG.entering(CLASS, "testAddCookie");
        try{
            Cookie cookie = new Cookie("cookieName", "cookieValue", "/", ".swgas.com", false, false, LocalDateTime.now().plusDays(5));
            setUrl(URL);
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("addCookie", sessionId), cookie.toJson()).body());
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
    @Test
    public void testDeleteCookie() {
        LOG.entering(CLASS, "testDeleteCookie");
        try{
            setUrl(URL);
            JsonObject result = MarionetteUtil.parseJsonObject(DELETE(getUri("deleteCookie", sessionId, "cookieName"), "").body());
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
    @Test
    public void testDeleteAllCookies() {
        LOG.entering(CLASS, "testDeleteAllCookies");
        try{
            setUrl(URL);
            JsonObject result = MarionetteUtil.parseJsonObject(DELETE(getUri("deleteAllCookies", sessionId), "").body());
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
    @Test
    @Disabled(value = "Not Implimented")
    public void testPerformActions() {
        LOG.entering(CLASS, "testPerformActions");
        try{
            setUrl(URL);
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("performActions", sessionId), "").body());
            LOG.exiting(CLASS, "testPerformActions", result);
        } catch(Exception e){            
            LOG.throwing(CLASS, "testPerformActions", e);
            throw e;
        }
    }

    /**
     * Test of releaseActions method, of class WebDriverService.
     */
    @Test
    @Disabled(value = "Not Implimented")
    public void testReleaseActions() {
        LOG.entering(CLASS, "testReleaseActions");
        try{
            setUrl(URL);
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("releaseActions", sessionId), "").body());
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
    @Test
    public void testDismissAlert() {
        LOG.entering(CLASS, "testDismissAlert");
        try{
            setUrl(URL);
            POST(getUri("executeScript", sessionId)
                , Json.createObjectBuilder()
                .add("script", "window.alert('alert');")
                .add("args", JsonValue.EMPTY_JSON_ARRAY)
                .build().toString()
            );
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("dismissAlert", sessionId), "").body());
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
    @Test
    public void testAcceptAlert() {
        LOG.entering(CLASS, "testAcceptAlert");
        try{
            setUrl(URL);
            POST(getUri("executeScript", sessionId)
                , Json.createObjectBuilder()
                .add("script", "window.alert('alert');")
                .add("args", JsonValue.EMPTY_JSON_ARRAY)
                .build().toString()
            );
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("acceptAlert", sessionId), "").body());
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
    @Test
    public void testGetAlertText() {
        LOG.entering(CLASS, "testGetAlertText");
        String expected = "alert";
        try{
            setUrl(URL);
            POST(getUri("executeScript", sessionId)
                , Json.createObjectBuilder()
                .add("script", String.format("window.alert('%s');", expected))
                .add("args", JsonValue.EMPTY_JSON_ARRAY)
                .build().toString()
            );
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("getAlertText", sessionId)).body());
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
    @Test
    public void testSetAlertText() {
        LOG.entering(CLASS, "testSetAlertText");
        String expected = "trela";
        try{
            setUrl(URL);
            POST(getUri("executeScript", sessionId)
                , Json.createObjectBuilder()
                .add("script", "window.prompt('alert');")
                .add("args", JsonValue.EMPTY_JSON_ARRAY)
                .build().toString()
            );
            JsonObject result = MarionetteUtil.parseJsonObject(POST(getUri("setAlertText", sessionId), MarionetteUtil.createJson("text", expected)).body());
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
    @Test
    public void testGetScreenshot() {
        LOG.entering(CLASS, "testGetScreenshot");
        try{
            setUrl(URL);
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("getScreenshot", sessionId)).body());
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
    @Test
    public void testGetElementScreenshot() {
        LOG.entering(CLASS, "testGetElementScreenshot");
        try{
            setUrl(URL);
            String element = MarionetteUtil.parseJsonObject(POST(
                getUri("findElement", sessionId)
                , Json.createObjectBuilder().add("using", Marionette.SearchMethod.CSS_SELECTOR.name()).add("value", "#sticky-menu #menu_myaccount").build().toString()
            ).body()).getString("element");
            JsonObject result = MarionetteUtil.parseJsonObject(GET(getUri("getElementScreenshot", sessionId, element)).body());
            Assertions.assertTrue(!result.getString("screenshot","").isEmpty());
            LOG.exiting(CLASS, "testGetElementScreenshot", result);
        } catch(Exception e){
            LOG.throwing(CLASS, "testGetElementScreenshot", e);
            throw e;
        }
    }    
}