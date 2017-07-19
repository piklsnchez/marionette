package com.swgas.rest;

import com.swgas.marionette.Marionette;
import com.swgas.marionette.MarionetteFactory;
import com.swgas.parser.MarionetteParser;
import com.swgas.util.MarionetteUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import javax.json.Json;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ws")
public class WebDriverService {
    private static final String CLASS = WebDriverService.class.getName();
    private static final Logger LOG   = Logger.getLogger(CLASS);
    private static final String HOST  = "localhost";
    private static final int    PORT  = 2828;
    private static final int    TIMEOUT = 20;
    private static final Map<String, Session> SESSIONS = new HashMap<>();
    
    //New Session
    @POST
    @Path("/session")
    @Produces(MediaType.APPLICATION_JSON)
    public String newSession() {
        LOG.entering(CLASS, "newSession");
        try{
            ProcessBuilder procBuilder = new ProcessBuilder("firefox", "--marionette", "-P", "marionette", "--new-instance");
            Process proc = procBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while(reader.lines().map(line -> {LOG.info(line);return line;}).noneMatch(line -> line.contains("Listening"))){}
            Session session = new Session();
            session.setProc(proc);
            String sessionId = MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> {session.setClient(c); return c.newSession();})
            .thenApply(MarionetteUtil::parseToSession)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.info("done");
            session.setSessionId(sessionId);
            SESSIONS.put(sessionId, session);
            String result = Json.createObjectBuilder().add("sessionId", sessionId).build().toString();
            LOG.exiting(CLASS, "newSession", result);
            return result;
            //FIXME return http error
        //} catch(IOException | InterruptedException | ExecutionException | TimeoutException e){
        }catch(Exception e){
            LOG.throwing(CLASS, "newSession", e);
            throw new RuntimeException(e);
        }
    }

    //Delete Session
    @DELETE
    @Path("/session/{session_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteSession(@PathParam("session_id") String sessionId) {        
        Session session = SESSIONS.get(sessionId);
        String result = "";
        if(session.getProc() != null){
            try{
                result = session.getClient()
                .deleteSession()
                .thenApply(MarionetteUtil::parseToObject)
                .get(TIMEOUT, TimeUnit.SECONDS)
                .toString();                
                session.getClient().quitApplication(Collections.singletonList("eForceQuit")).get(TIMEOUT, TimeUnit.SECONDS);
                session.getProc().destroy();
                SESSIONS.remove(sessionId);
                //FIXME return http error
            } catch(InterruptedException | ExecutionException | TimeoutException e){
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    //Status
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatus() {
        throw new RuntimeException("Not yet implemented");
    }

    //Get Timeouts
    @GET
    @Path("/session/{session_id}/timeouts")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTimeouts(@PathParam("session_id") String sessionId) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .getTimeouts()
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException | IllegalArgumentException | DateTimeParseException e){            
            throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
        }
    }

    //Set Timeouts
    @POST
    @Path("/session/{session_id}/timeouts")
    @Produces(MediaType.APPLICATION_JSON)
    public String setTimeouts(@PathParam("session_id") String sessionId, @FormParam("timeout") Marionette.Timeout timeout, @FormParam("duration") String duration) {
        try {
            return SESSIONS.get(sessionId)
            .getClient()
            .setTimeouts(timeout, Duration.parse(duration))
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException | IllegalArgumentException | DateTimeParseException e){
            throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
        }
    }

    //Go
    @POST
    @Path("/session/{session_id}/url")
    @Produces(MediaType.APPLICATION_JSON)
    public String setUrl(@PathParam("session_id") String sessionId, @FormParam("url") String url) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .get(url)
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Get Current URL
    @GET
    @Path("/session/{session_id}/url")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUrl(@PathParam("session_id") String sessionId) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .getCurrentUrl()
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Back
    @POST
    @Path("/session/{session_id}/back")
    @Produces(MediaType.APPLICATION_JSON)
    public String back(@PathParam("session_id") String sessionId) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .goBack()
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Forward
    @POST
    @Path("/session/{session_id}/forward")
    @Produces(MediaType.APPLICATION_JSON)
    public String forward(@PathParam("session_id") String sessionId) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .goForward()
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Refresh
    @POST
    @Path("/session/{session_id}/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public String refresh(@PathParam("session_id") String sessionId) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .refresh()
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Get Title
    @GET
    @Path("/session/{session_id}/title")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTitle(@PathParam("session_id") String sessionId) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .getTitle()
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Get Window Handle
    @GET
    @Path("/session/{session_id}/window")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWindow(@PathParam("session_id") String sessionId) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .getWindowHandle()
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Switch To Window
    @POST
    @Path("/session/{session_id}/window")
    @Produces(MediaType.APPLICATION_JSON)
    public String setWindow(@PathParam("session_id") String sessionId, @FormParam("name") String name) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .switchToWindow(name)
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Close Window
    @DELETE
    @Path("/session/{session_id}/window")
    @Produces(MediaType.APPLICATION_JSON)
    public String closeWindow(@PathParam("session_id") String sessionId) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .closeChromeWindow()
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Get Window Handles
    @GET
    @Path("/session/{session_id}/window/handles")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWindows(@PathParam("session_id") String sessionId) {
        try{            
            return Json.createObjectBuilder().add(
                  "windows"
                , Json.createArrayBuilder(SESSIONS.get(sessionId)
                .getClient()
                .getWindowHandles()
                .thenApply(MarionetteUtil::parseToList)
                .get(TIMEOUT, TimeUnit.SECONDS))
            ).build().toString();
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Switch To Frame
    @POST
    @Path("/session/{session_id}/frame")
    @Produces(MediaType.APPLICATION_JSON)
    public String setFrame(@PathParam("session_id") String sessionId, @FormParam("id") String id) {
        try{
            return SESSIONS.get(sessionId)
            .getClient()
            .switchToFrame(id)
            .thenApply(MarionetteParser.STRING::parseFrom)
            .get(TIMEOUT, TimeUnit.SECONDS);
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    //Switch To Parent Frame
    @POST
    @Path("/session/{session_id}/frame/parent")
    @Produces(MediaType.APPLICATION_JSON)
    public String parentFrame(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Window Rect
    @GET
    @Path("/session/{session_id}/window/rect")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWindowDimension(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Set Window Rect
    @POST
    @Path("/session/{session_id}/window/rect")
    @Produces(MediaType.APPLICATION_JSON)
    public String setWindowDimension(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Maximize Window
    @POST
    @Path("/session/{session_id}/window/maximize")
    @Produces(MediaType.APPLICATION_JSON)
    public String maximizeWindow(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Minimize Window
    @POST
    @Path("/session/{session_id}/window/minimize")
    @Produces(MediaType.APPLICATION_JSON)
    public String minimizeWindow(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Fullscreen Window
    @POST
    @Path("/session/{session_id}/window/fullscreen")
    @Produces(MediaType.APPLICATION_JSON)
    public String fullscreen(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Active Element
    @GET
    @Path("/session/{session_id}/element/active")
    @Produces(MediaType.APPLICATION_JSON)
    public String getActiveElement(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Find Element
    @POST
    @Path("/session/{session_id}/element")
    @Produces(MediaType.APPLICATION_JSON)
    public String findElement(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Find Elements
    @POST
    @Path("/session/{session_id}/elements")
    @Produces(MediaType.APPLICATION_JSON)
    public String findElements(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Find Element From Element
    @POST
    @Path("/session/{session_id}/element/{element_id}/element")
    @Produces(MediaType.APPLICATION_JSON)
    public String findElementFromElement(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Find Elements From Element
    @POST
    @Path("/session/{session_id}/element/{element_id}/elements")
    @Produces(MediaType.APPLICATION_JSON)
    public String findElementsFromElement(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Is Element Selected
    @GET
    @Path("/session/{session_id}/element/{element_id}/selected")
    @Produces(MediaType.APPLICATION_JSON)
    public String isElementSelected(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Element Attribute
    @GET
    @Path("/session/{session_id}/element/{element_id}/attribute/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementAttribute(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Element Property
    @GET
    @Path("/session/{session_id}/element/{element_id}/property/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementProperty(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Element CSS Value
    @GET
    @Path("/session/{session_id}/element/{element_id}/css/{property_name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementCss(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Element Text
    @GET
    @Path("/session/{session_id}/element/{element_id}/text")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementText(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Element Tag Name
    @GET
    @Path("/session/{session_id}/element/{element_id}/name")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementTagName(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Element Rect
    @GET
    @Path("/session/{session_id}/element/{element_id}/rect")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementDimension(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Is Element Enabled
    @GET
    @Path("/session/{session_id}/element/{element_id}/enabled")
    @Produces(MediaType.APPLICATION_JSON)
    public String isElementEnabled(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Element Click
    @POST
    @Path("/session/{session_id}/element/{element_id}/click")
    @Produces(MediaType.APPLICATION_JSON)
    public String clickElement(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Element Clear
    @POST
    @Path("/session/{session_id}/element/{element_id}/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public String clearElement(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Element Send Keys
    @POST
    @Path("/session/{session_id}/element/{element_id}/value")
    @Produces(MediaType.APPLICATION_JSON)
    public String sendKeysToElement(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Page Source
    @GET
    @Path("/session/{session_id}/source")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPageSource(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Execute Script
    @POST
    @Path("/session/{session_id}/execute/sync")
    @Produces(MediaType.APPLICATION_JSON)
    public String executeScript(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Execute Async Script
    @POST
    @Path("/session/{session_id}/execute/async")
    @Produces(MediaType.APPLICATION_JSON)
    public String executeScriptAsync(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get All Cookies
    @GET
    @Path("/session/{session_id}/cookie")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCookies(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Named Cookie
    @GET
    @Path("/session/{session_id}/cookie/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCookie(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Add Cookie
    @POST
    @Path("/session/{session_id}/cookie")
    @Produces(MediaType.APPLICATION_JSON)
    public String addCookie(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Delete Cookie
    @DELETE
    @Path("/session/{session_id}/cookie/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String removeCookie(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Delete All Cookies
    @DELETE
    @Path("/session/{session id)/cookie")
    @Produces(MediaType.APPLICATION_JSON)
    public String removeCookies(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Perform Actions
    @POST
    @Path("/session/{session_id}/actions")
    @Produces(MediaType.APPLICATION_JSON)
    public String performActions(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Release Actions
    @DELETE
    @Path("/session/{session_id}/actions")
    @Produces(MediaType.APPLICATION_JSON)
    public String releaseActions(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Dismiss Alert
    @POST
    @Path("/session/{session_id}/alert/dismiss")
    @Produces(MediaType.APPLICATION_JSON)
    public String dismissAlert(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Accept Alert
    @POST
    @Path("/session/{session_id}/alert/accept")
    @Produces(MediaType.APPLICATION_JSON)
    public String acceptAlert(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Get Alert Text
    @GET
    @Path("/session/{session_id}/alert/text")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAlertText(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Send Alert Text
    @POST
    @Path("/session/{session_id}/alert/text")
    @Produces(MediaType.APPLICATION_JSON)
    public String setAlertText(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Take Screenshot
    @GET
    @Path("/session/{session_id}/screenshot")
    @Produces(MediaType.APPLICATION_JSON)
    public String getScreenshot(@PathParam("session_id") String sessionId) {
        return "??";
    }

    //Take Element Screenshot
    @GET
    @Path("/session/{session_id}/element/{element_id}/screenshot")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementScreenshot(@PathParam("session_id") String sessionId) {
        return "??";
    }
}
