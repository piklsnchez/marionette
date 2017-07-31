package com.swgas.rest;

import com.swgas.marionette.Marionette;
import com.swgas.marionette.MarionetteFactory;
import com.swgas.util.MarionetteUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
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
    private static final String CLASS   = WebDriverService.class.getName();
    private static final Logger LOG     = Logger.getLogger(CLASS);
    private static final String HOST    = "localhost";
    private static final int    PORT    = 2828;
    private static final int    TIMEOUT = 20;
    private static final Map<String, Session> SESSIONS = new HashMap<>();
    
    //New Session
    @POST
    @Path("/session")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
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
            .thenApply(MarionetteUtil::toSession)
            .get(TIMEOUT, TimeUnit.SECONDS);
            session.setSessionId(sessionId);
            
            SESSIONS.put(sessionId, session);
            String result = MarionetteUtil.createJson("sessionId", sessionId);
            LOG.exiting(CLASS, "newSession", result);
            return result;
            //FIXME return http error
        } catch(IOException | InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "newSession", e);
            throw new RuntimeException(e);
        }
    }

    //Delete Session
    @DELETE
    @Path("/session/{session_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String deleteSession(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "deleteSession", sessionId);
        Session session = SESSIONS.get(sessionId);
        String result = "";
        //no such session
        if(null == session){
            RuntimeException e = new RuntimeException("No such session");
            LOG.throwing(CLASS, "deleteSession", e);
            throw e;
        }        
        if(session.getProc() != null){
            try{
                //this causes error
                /*result = session.getClient()
                .deleteSession()
                .thenApply(MarionetteUtil::parseToObject)
                .thenApply(Objects::toString)
                .get(TIMEOUT, TimeUnit.SECONDS);*/
                
                result = session.getClient()
                .quitApplication(Collections.singletonList("eForceQuit"))
                .thenApply(MarionetteUtil::toObject)
                .thenApply(Objects::toString)
                .get(TIMEOUT, TimeUnit.SECONDS);
                
                session.getProc().destroy();
                SESSIONS.remove(sessionId);
                //FIXME return http error
            } catch(InterruptedException | ExecutionException | TimeoutException e){
                LOG.throwing(CLASS, "deleteSession", e);
                Throwable t = e instanceof ExecutionException ? e.getCause() : e;                
                throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
            }
        }
        LOG.exiting(CLASS, "deleteSession", result);
        return result;
    }

    //Status
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getStatus() {
        throw new RuntimeException("Not yet implemented");
    }

    //Get Timeouts
    @GET
    @Path("/session/{session_id}/timeouts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getTimeouts(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getTimeouts", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getTimeouts()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getTimeouts", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException | IllegalArgumentException | DateTimeParseException e){
            LOG.throwing(CLASS, "getTimeouts", e);
            throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
        }
    }

    //Set Timeouts
    @POST
    @Path("/session/{session_id}/timeouts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String setTimeouts(@PathParam("session_id") String sessionId, String body){
        LOG.entering(CLASS, "setTimeouts", Stream.of(sessionId, body).toArray());
        try {
            JsonObject json = MarionetteUtil.parseJsonObject(body);
            String result = SESSIONS.get(sessionId)
            .getClient()
            .setTimeouts(Marionette.Timeout.valueOf(json.getString("timeout")), Duration.parse(json.getString("duration")))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "setTimeouts", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException | IllegalArgumentException | DateTimeParseException e){
            LOG.throwing(CLASS, "setTimeouts", e);
            throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
        }
    }

    //Go
    @POST
    @Path("/session/{session_id}/url")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String setUrl(@PathParam("session_id") String sessionId, String url) {
        LOG.entering(CLASS, "setUrl", Stream.of(sessionId, url).toArray());
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .get(MarionetteUtil.parseJsonObject(url).getString("url"))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "setTimeouts", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "setTimeouts", e);
            throw new RuntimeException(e);
        }
    }

    //Get Current URL
    @GET
    @Path("/session/{session_id}/url")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getUrl(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getUrl", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getCurrentUrl()
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(u -> MarionetteUtil.createJson("url", u))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getUrl", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getUrl", e);
            throw new RuntimeException(e);
        }
    }

    //Back
    @POST
    @Path("/session/{session_id}/back")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String back(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "back", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .goBack()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "back", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "back", e);
            throw new RuntimeException(e);
        }
    }

    //Forward
    @POST
    @Path("/session/{session_id}/forward")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String forward(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "forward", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .goForward()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "forward", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "forward", e);
            throw new RuntimeException(e);
        }
    }

    //Refresh
    @POST
    @Path("/session/{session_id}/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String refresh(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "refresh", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .refresh()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "refresh", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "refresh", e);
            throw new RuntimeException(e);
        }
    }

    //Get Title
    @GET
    @Path("/session/{session_id}/title")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getTitle(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getTitle", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getTitle()
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(v -> MarionetteUtil.createJson("title", v))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getTitle", sessionId);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getTitle", e);
            throw new RuntimeException(e);
        }
    }

    //Get Window Handle
    @GET
    @Path("/session/{session_id}/window")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getWindow(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getWindow", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getWindowHandle()
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(v -> MarionetteUtil.createJson("window", v))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getWindow", sessionId);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getWindow", e);
            throw new RuntimeException(e);
        }
    }

    //Switch To Window
    @POST
    @Path("/session/{session_id}/window")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String setWindow(@PathParam("session_id") String sessionId, String window) {
        LOG.entering(CLASS, "setWindow", Stream.of(sessionId, window).toArray());
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .switchToWindow(MarionetteUtil.parseJsonObject(window).getString("window"))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "setWindow", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "setWindow", e);
            throw new RuntimeException(e);
        }
    }

    //Close Window
    /**
     * 
     * @param sessionId
     * @return array
     */
    @DELETE
    @Path("/session/{session_id}/window")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String closeWindow(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "closeWindow", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .close()
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "closeWindow", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "closeWindow", e);
            if(e instanceof TimeoutException){
                SESSIONS.get(sessionId).getProc().destroy();
            }
            throw new RuntimeException(e);
        }
    }

    //Get Window Handles
    /**
     * 
     * @param sessionId
     * @return array
     */
    @GET
    @Path("/session/{session_id}/window/handles")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWindows(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getWindows", sessionId);
        try{            
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getWindowHandles()
            .thenApply(MarionetteUtil::toList)
            .thenApply(l -> Json.createArrayBuilder(l).build())
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getWindows", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getWindows", e);
            throw new RuntimeException(e);
        }
    }

    //Switch To Frame
    @POST
    @Path("/session/{session_id}/frame")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String setFrame(@PathParam("session_id") String sessionId, String id) {
        LOG.entering(CLASS, "setFrame", Stream.of(sessionId, id).toArray());
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .switchToFrame(MarionetteUtil.parseJsonObject(id).getString("id", null))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "setFrame", result);
            return result;
        //FIXME return http error
        } catch(Exception e){
            LOG.throwing(CLASS, "setFrame", e);
            throw new RuntimeException(e);
        }
    }

    //Switch To Parent Frame
    @POST
    @Path("/session/{session_id}/frame/parent")
    @Produces(MediaType.APPLICATION_JSON)
    public String setParentFrame(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "setParentFrame", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .switchToParentFrame()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "setParentFrame", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "setParentFrame", e);
            throw new RuntimeException(e);
        }
    }

    //Get Window Rect
    @GET
    @Path("/session/{session_id}/window/rect")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWindowRect(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getWindowRect", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getWindowRect()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getWindowRect", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getWindowRect", e);
            throw new RuntimeException(e);
        }
    }

    //Set Window Rect
    @POST
    @Path("/session/{session_id}/window/rect")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String setWindowRect(@PathParam("session_id") String sessionId, String rect) {
        LOG.entering(CLASS, "setWindowRect", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .setWindowRect(MarionetteUtil.parseRectangle(rect))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "setWindowRect", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "setWindowRect", e);
            if(e instanceof TimeoutException){
                SESSIONS.get(sessionId).getProc().destroy();
            }
            throw new RuntimeException(e);
        }
    }

    //Minimize Window
    @POST
    @Path("/session/{session_id}/window/minimize")
    @Produces(MediaType.APPLICATION_JSON)
    public String minimizeWindow(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "minimizeWindow", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .minimizeWindow()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "minimizeWindow", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "minimizeWindow", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Maximize Window
    @POST
    @Path("/session/{session_id}/window/maximize")
    @Produces(MediaType.APPLICATION_JSON)
    public String maximizeWindow(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "maximizeWindow", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .maximizeWindow()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "maximizeWindow", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "maximizeWindow", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Fullscreen Window
    @POST
    @Path("/session/{session_id}/window/fullscreen")
    @Produces(MediaType.APPLICATION_JSON)
    public String fullscreen(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "fullscreen", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .fullscreen()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "fullscreen", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "fullscreen", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get Active Element
    @GET
    @Path("/session/{session_id}/element/active")
    @Produces(MediaType.APPLICATION_JSON)
    public String getActiveElement(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getActiveElement", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getActiveElement()
            .thenApply(MarionetteUtil::toElement)
            .thenApply(e -> MarionetteUtil.createJson("element", e))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getActiveElement", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getActiveElement", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Find Element
    @POST
    @Path("/session/{session_id}/element")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String findElement(@PathParam("session_id") String sessionId, String json){
        LOG.entering(CLASS, "findElement", Stream.of(sessionId, json).toArray());
        JsonObject find = MarionetteUtil.parseJsonObject(json);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .findElement(Marionette.SearchMethod.valueOf(find.getString("using")), find.getString("value"))
            .thenApply(MarionetteUtil::toElement)
            .thenApply(e -> MarionetteUtil.createJson("element", e))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "findElement", result);
            return result;
        //FIXME return http error
        } catch(Exception e){
            LOG.throwing(CLASS, "findElement", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Find Elements
    @POST
    @Path("/session/{session_id}/elements")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String findElements(@PathParam("session_id") String sessionId, String json) {
        LOG.entering(CLASS, "findElements", Stream.of(sessionId, json).toArray());
        JsonObject find = MarionetteUtil.parseJsonObject(json);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .findElements(Marionette.SearchMethod.valueOf(find.getString("using")), find.getString("value"))
            .thenApply(MarionetteUtil::toElements)
            .thenApply(e -> Json.createArrayBuilder(e).build())
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "findElements", result);
            return result;
        //FIXME return http error
        } catch(Exception e){
            LOG.throwing(CLASS, "findElements", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Find Element From Element
    @POST
    @Path("/session/{session_id}/element/{element_id}/element")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String findElementFromElement(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId, String json) {
        LOG.entering(CLASS, "findElementFromElement", sessionId);
        JsonObject find = MarionetteUtil.parseJsonObject(json);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .findElementFromElement(Marionette.SearchMethod.valueOf(find.getString("using")), find.getString("value"), elementId)
            .thenApply(MarionetteUtil::toElement)
            .thenApply(e -> MarionetteUtil.createJson("element", e))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "findElementFromElement", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "findElementFromElement", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Find Elements From Element
    @POST
    @Path("/session/{session_id}/element/{element_id}/elements")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String findElementsFromElement(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId, String json) {
        LOG.entering(CLASS, "findElementsFromElement", sessionId);
        JsonObject find = MarionetteUtil.parseJsonObject(json);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .findElementsFromElement(Marionette.SearchMethod.valueOf(find.getString("using")), find.getString("value"), elementId)
            .thenApply(MarionetteUtil::toElements)
            .thenApply(e -> Json.createArrayBuilder(e).build())
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "findElementsFromElement", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "findElementsFromElement", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Is Element Selected
    @GET
    @Path("/session/{session_id}/element/{element_id}/selected")
    @Produces(MediaType.APPLICATION_JSON)
    public String isElementSelected(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId) {
        LOG.entering(CLASS, "isElementSelected", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .isElementSelected(elementId)
            .thenApply(MarionetteUtil::toBooleanValue)
            .thenApply(b -> Json.createObjectBuilder().add("selected", b).build())
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "isElementSelected", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "isElementSelected", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get Element Attribute
    @GET
    @Path("/session/{session_id}/element/{element_id}/attribute/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementAttribute(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId, @PathParam("name") String name) {
        LOG.entering(CLASS, "getElementAttribute", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getElementAttribute(elementId, name)
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(n -> MarionetteUtil.createJson("attribute", n))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getElementAttribute", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getElementAttribute", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get Element Property
    @GET
    @Path("/session/{session_id}/element/{element_id}/property/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementProperty(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId, @PathParam("name") String name) {
        LOG.entering(CLASS, "getElementProperty", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getElementProperty(elementId, name)
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(n -> MarionetteUtil.createJson("property", n))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getElementProperty", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getElementProperty", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get Element CSS Value
    @GET
    @Path("/session/{session_id}/element/{element_id}/css/{css_property}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementCssProperty(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId, @PathParam("css_property") String property) {
        LOG.entering(CLASS, "getElementCss", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getElementCssProperty(elementId, property)
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(n -> MarionetteUtil.createJson("css", n))
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getElementCss", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getElementCss", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get Element Text
    @GET
    @Path("/session/{session_id}/element/{element_id}/text")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementText(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId) {
        LOG.entering(CLASS, "getElementText", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getElementText(elementId)
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(n -> MarionetteUtil.createJson("text", n))
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getElementText", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getElementText", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get Element Tag Name
    @GET
    @Path("/session/{session_id}/element/{element_id}/name")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementTagName(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId) {
        LOG.entering(CLASS, "getElementTagName", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getElementTagName(elementId)
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(n -> MarionetteUtil.createJson("tag", n))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getElementTagName", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getElementTagName", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get Element Rect
    @GET
    @Path("/session/{session_id}/element/{element_id}/rect")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementRect(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId) {
        LOG.entering(CLASS, "getElementRect", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getElementRectangle(elementId)
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getElementRect", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getElementRect", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Is Element Enabled
    @GET
    @Path("/session/{session_id}/element/{element_id}/enabled")
    @Produces(MediaType.APPLICATION_JSON)
    public String isElementEnabled(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId) {
        LOG.entering(CLASS, "isElementEnabled", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .isElementEnabled(elementId)
            .thenApply(MarionetteUtil::toBooleanValue)
            .thenApply(b -> Json.createObjectBuilder().add("enabled", b).build())
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "isElementEnabled", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "isElementEnabled", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Element Click
    @POST
    @Path("/session/{session_id}/element/{element_id}/click")
    @Produces(MediaType.APPLICATION_JSON)
    public String clickElement(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId) {
        LOG.entering(CLASS, "clickElement", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .clickElement(elementId)
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "clickElement", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "clickElement", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Element Clear
    @POST
    @Path("/session/{session_id}/element/{element_id}/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public String clearElement(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId) {
        LOG.entering(CLASS, "clearElement", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .clearElement(elementId)
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "clearElement", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "clearElement", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Element Send Keys
    @POST
    @Path("/session/{session_id}/element/{element_id}/value")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String sendKeysToElement(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId, String json) {
        LOG.entering(CLASS, "sendKeysToElement", Stream.of(sessionId, elementId, json).toArray());
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .sendKeysToElement(elementId, MarionetteUtil.parseJsonObject(json).getString("keys"))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "sendKeysToElement", result);
            return result;
        //FIXME return http error
        } catch(Exception e){
            LOG.throwing(CLASS, "sendKeysToElement", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get Page Source
    @GET
    @Path("/session/{session_id}/source")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPageSource(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getPageSource", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getPageSource()
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(s -> MarionetteUtil.createJson("source", s))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getPageSource", result);
            return result;
        //FIXME return http error
        } catch(Exception e){
            LOG.throwing(CLASS, "getPageSource", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Execute Script
    @POST
    @Path("/session/{session_id}/execute/sync")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String executeScript(@PathParam("session_id") String sessionId, String json) {
        LOG.entering(CLASS, "executeScript", Stream.of(sessionId, json).toArray());
        JsonObject script = MarionetteUtil.parseJsonObject(json);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .executeScript(script.getString("script"), script.getString("args"), null, null)
            .thenApply(MarionetteUtil::toJsonValue)
            .thenApply(r -> Json.createObjectBuilder().add("return", r).build())
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "executeScript", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "executeScript", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Execute Async Script
    @POST
    @Path("/session/{session_id}/execute/async")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String executeScriptAsync(@PathParam("session_id") String sessionId, String json) {
        LOG.entering(CLASS, "executeScriptAsync", Stream.of(sessionId, json).toArray());
        JsonObject script = MarionetteUtil.parseJsonObject(json);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .executeAsyncScript(script.getString("script"), script.getString("args"), null, null, null)
            .thenApply(MarionetteUtil::toJsonValue)
            .thenApply(r -> Json.createObjectBuilder().add("return", r).build())
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "executeScriptAsync", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "executeScriptAsync", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get All Cookies
    @GET
    @Path("/session/{session_id}/cookie")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCookies(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getCookies", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getCookies()
            .thenApply(MarionetteUtil::toArray)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getCookies", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getCookies", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get Named Cookie
    @GET
    @Path("/session/{session_id}/cookie/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCookie(@PathParam("session_id") String sessionId, @PathParam("name") String name) {
        LOG.entering(CLASS, "getCookie", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getCookies()
            .thenApply(MarionetteUtil::toArray)
            .thenApply(array -> array.stream()
                .filter(cookie -> Objects.equals(name, cookie.asJsonObject().getString("name")))
                .findFirst().orElse(JsonObject.EMPTY_JSON_OBJECT)
            )
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getCookie", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getCookie", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Add Cookie
    @POST
    @Path("/session/{session_id}/cookie")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addCookie(@PathParam("session_id") String sessionId, String cookie) {
        LOG.entering(CLASS, "addCookie", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .addCookie(cookie)
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "addCookie", result);
            return result;
        //FIXME return http error
        } catch(Exception e){
            LOG.throwing(CLASS, "addCookie", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Delete Cookie
    @DELETE
    @Path("/session/{session_id}/cookie/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteCookie(@PathParam("session_id") String sessionId, @PathParam("name") String cookie) {
        LOG.entering(CLASS, "deleteCookie", Stream.of(sessionId, cookie).toArray());
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .deleteCookie(cookie)
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "deleteCookie", result);
            return result;
        //FIXME return http error
        } catch(Exception e){
            LOG.throwing(CLASS, "deleteCookie", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Delete All Cookies
    @DELETE
    @Path("/session/{session_id}/cookie")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteAllCookies(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "deleteAllCookies", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .deleteAllCookies()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "deleteAllCookies", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "deleteAllCookies", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Dismiss Alert
    @POST
    @Path("/session/{session_id}/alert/dismiss")
    @Produces(MediaType.APPLICATION_JSON)
    public String dismissAlert(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "dismissAlert", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .dismissDialog()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "dismissAlert", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "dismissAlert", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Accept Alert
    @POST
    @Path("/session/{session_id}/alert/accept")
    @Produces(MediaType.APPLICATION_JSON)
    public String acceptAlert(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "acceptAlert", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .acceptDialog()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "acceptAlert", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "acceptAlert", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Get Alert Text
    @GET
    @Path("/session/{session_id}/alert/text")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAlertText(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getAlertText", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getTextFromDialog()
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(s -> MarionetteUtil.createJson("text", s))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getAlertText", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getAlertText", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Send Alert Text
    @POST
    @Path("/session/{session_id}/alert/text")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String setAlertText(@PathParam("session_id") String sessionId, String json) {
        LOG.entering(CLASS, "setAlertText", Stream.of(sessionId, json).toArray());
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .sendKeysToDialog(MarionetteUtil.parseJsonObject(json).getString("text"))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "setAlertText", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "setAlertText", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Take Screenshot
    @GET
    @Path("/session/{session_id}/screenshot")
    @Produces(MediaType.APPLICATION_JSON)
    public String getScreenshot(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "getScreenshot", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .takeScreenshot()
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(s -> MarionetteUtil.createJson("screenshot", s))            
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getScreenshot", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getScreenshot", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Take Element Screenshot
    @GET
    @Path("/session/{session_id}/element/{element_id}/screenshot")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementScreenshot(@PathParam("session_id") String sessionId, @PathParam("element_id") String elementId) {
        LOG.entering(CLASS, "getScreenshot", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .takeScreenshot(elementId)
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(s -> MarionetteUtil.createJson("screenshot", s))            
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getScreenshot", result);
            return result;
        //FIXME return http error
        } catch(InterruptedException | ExecutionException | TimeoutException e){
            LOG.throwing(CLASS, "getScreenshot", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Perform Actions
    @POST
    @Path("/session/{session_id}/actions")
    @Produces(MediaType.APPLICATION_JSON)
    public String performActions(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "performActions", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .performActions()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "performActions", result);
            return result;
        //FIXME return http error
        } catch(Exception e){
            LOG.throwing(CLASS, "performActions", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }

    //Release Actions
    @DELETE
    @Path("/session/{session_id}/actions")
    @Produces(MediaType.APPLICATION_JSON)
    public String releaseActions(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "releaseActions", sessionId);
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .releaseActions()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "releaseActions", result);
            return result;
        //FIXME return http error
        } catch(Exception e){
            LOG.throwing(CLASS, "releaseActions", e);
            throw new RuntimeException(e instanceof ExecutionException ? e.getCause() : e);
        }
    }
}
