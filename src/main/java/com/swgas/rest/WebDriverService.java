package com.swgas.rest;

import com.swgas.exception.InvalidSessionIdException;
import com.swgas.exception.MarionetteException;
import com.swgas.exception.NoSuchCookieException;
import com.swgas.exception.SessionNotCreatedException;
import com.swgas.exception.UnknownErrorException;
import com.swgas.marionette.Marionette;
import com.swgas.marionette.MarionetteFactory;
import com.swgas.model.JsonError;
import com.swgas.model.Status;
import com.swgas.model.Timeouts;
import com.swgas.util.MarionetteUtil;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ws")
public class WebDriverService {
    private static final String CLASS    = WebDriverService.class.getName();
    private static final Logger LOG      = Logger.getLogger(CLASS);
    private static final int    TIMEOUT  = 20;
    private static final Map<String, Session> SESSIONS = new HashMap<>();
    
    //New Session
    @POST
    @Path("/session")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String newSession() {
        LOG.entering(CLASS, "newSession");
        
        Session session = null;
        try{
            session = MarionetteFactory.createSession().get(TIMEOUT, TimeUnit.SECONDS);
            session.setSessionId(
                session.getClient().newSession()
                .thenApply(MarionetteUtil::toSession)
                .get(TIMEOUT, TimeUnit.SECONDS)
            );
            SESSIONS.put(session.getSessionId(), session);
            String result = MarionetteUtil.createJson("sessionId", session.getSessionId());
            LOG.exiting(CLASS, "newSession", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "newSession", e);
            if(null != session && null != session.getProc()){
                try{
                    session.getProc().destroy();
                } catch(Exception _e){
                    LOG.logp(Level.WARNING, CLASS, "newSession", e.getMessage(), _e);
                }
            }
            throw new SessionNotCreatedException(e);
        }
    }

    //Delete Session
    @DELETE
    @Path("/session/{session_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String deleteSession(@PathParam("session_id") String sessionId) {
        LOG.entering(CLASS, "deleteSession", sessionId);
        try(Session session = SESSIONS.get(sessionId)){
            String result = "";
            //no such session
            if(null == session){
                InvalidSessionIdException e = new InvalidSessionIdException(sessionId, new NullPointerException(String.format("No active session with is (%s)", sessionId)));
                LOG.throwing(CLASS, "deleteSession", e);
                throw e;
            }        
            if(session.getProc() != null){
                result = session.getClient()
                .quitApplication(Collections.singletonList("eForceQuit"))
                .thenApply(MarionetteUtil::toObject)
                .thenApply(Objects::toString)
                .get(TIMEOUT, TimeUnit.SECONDS);

                session.getProc().destroy();
                SESSIONS.remove(sessionId);
            }
            LOG.exiting(CLASS, "deleteSession", result);
            return result;
        } catch(TimeoutException e){
            LOG.throwing(CLASS, "deleteSession", e);
            throw new com.swgas.exception.TimeoutException(e);
        } catch(Exception e){
            LOG.throwing(CLASS, "deleteSession", e);
            throw new UnknownErrorException(e instanceof ExecutionException ? e.getCause() : e);                
        }
    }

    //Status
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getStatus() {
        LOG.entering(CLASS, "status");
        try{
            boolean ready = true;
            String message = SESSIONS.keySet().stream().reduce("", (a,b ) -> String.format("%s\n%s", a, b));
            return new Status(ready, message).toJson();
        } catch(Exception e){
            LOG.throwing(CLASS, "status", e);
            throw new UnknownErrorException(e);
        }
    }

    /**
     * NONCONFORMAT: A string representation of this duration using ISO-8601 seconds based representation, such as PT8H6M12.345S.
     * @param sessionId
     * @return 
     */
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
            .thenApply(t -> new Timeouts(
                  Duration.of(t.getInt("script",   0), ChronoUnit.MILLIS)
                , Duration.of(t.getInt("pageLoad", 0), ChronoUnit.MILLIS)
                , Duration.of(t.getInt("impicit",  0), ChronoUnit.MILLIS)
            ).toJson())
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getTimeouts", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "getTimeouts", e);
            throw MarionetteUtil.castException(e);
        }
    }

    /**
     * NONCONFORMANT: A string representation of this duration using ISO-8601 seconds based representation, such as PT8H6M12.345S.
     * @param sessionId
     * @param timeouts
     * @return 
     */
    @POST
    @Path("/session/{session_id}/timeouts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String setTimeouts(@PathParam("session_id") String sessionId, String timeouts){
        LOG.entering(CLASS, "setTimeouts", Stream.of(sessionId, timeouts).toArray());
        try {
            String result = SESSIONS.get(sessionId)
            .getClient()
            .setTimeouts(new Timeouts().fromJson(timeouts))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "setTimeouts", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "setTimeouts", e);
            throw MarionetteUtil.castException(e);
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
            //TODO: user prompt
            String result = SESSIONS.get(sessionId)
            .getClient()
            .get(MarionetteUtil.parseJsonObject(url).getString("url"))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "setUrl", result);
            return result;
            //TODO: invalid argument
        } catch(Exception e){
            LOG.throwing(CLASS, "setUrl", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getUrl", e);
            throw MarionetteUtil.castException(e);
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
            //TODO: handle user prompts
            String result = SESSIONS.get(sessionId)
            .getClient()
            .goBack()
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "back", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "back", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "forward", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "refresh", e);
            throw MarionetteUtil.castException(e);
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
            //TODO: handle user prompts
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getTitle()
            .thenApply(MarionetteUtil::toStringValue)
            .thenApply(v -> MarionetteUtil.createJson("title", v))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getTitle", sessionId);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "getTitle", e);
            throw MarionetteUtil.castException(e);
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
            .thenApply(v -> MarionetteUtil.createJson("handle", v))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getWindow", sessionId);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "getWindow", e);
            throw MarionetteUtil.castException(e);
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
            .switchToWindow(MarionetteUtil.parseJsonObject(window).getString("handle"))
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "setWindow", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "setWindow", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "closeWindow", e);
            WebApplicationException _e = MarionetteUtil.castException(e);
            if(e instanceof TimeoutException){
                SESSIONS.get(sessionId).getProc().destroy();
            }
            throw _e;
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getWindows", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "setFrame", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "setParentFrame", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getWindowRect", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "setWindowRect", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "maximizeWindow", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "minimizeWindow", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "fullscreen", e);
            throw MarionetteUtil.castException(e);
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
            .thenApply(e -> MarionetteUtil.createJson("element", e.getId()))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getActiveElement", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "getActiveElement", e);
            throw MarionetteUtil.castException(e);
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
            .thenApply(e -> MarionetteUtil.createJson("element", e.getId()))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "findElement", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "findElement", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "findElements", e);
            throw MarionetteUtil.castException(e);
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
            .thenApply(e -> MarionetteUtil.createJson("element", e.getId()))
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "findElementFromElement", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "findElementFromElement", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "findElementsFromElement", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "isElementSelected", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getElementAttribute", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getElementProperty", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getElementCss", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getElementText", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getElementTagName", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getElementRect", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "isElementEnabled", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "clickElement", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "clearElement", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "sendKeysToElement", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getPageSource", e);
            throw MarionetteUtil.castException(e);
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
            .executeScript(script.getString("script"), script.getJsonArray("args").toString(), null, null)
            .thenApply(MarionetteUtil::toJsonValue)
            .thenApply(r -> Json.createObjectBuilder().add("return", r).build())
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "executeScript", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "executeScript", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "executeScriptAsync", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getCookies", e);
            throw MarionetteUtil.castException(e);
        }
    }

    //Get Named Cookie
    @GET
    @Path("/session/{session_id}/cookie/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCookie(@PathParam("session_id") String sessionId, @PathParam("name") String name) {
        LOG.entering(CLASS, "getCookie", Stream.of(sessionId, name).toArray());
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .getCookies()
            .thenApply(MarionetteUtil::toArray)
            .thenApply(array -> array.stream().peek(c -> LOG.info(Objects.toString(c)))
                .filter(cookie -> Objects.equals(name, cookie.asJsonObject().getString("name")))
                .findFirst().orElseThrow(()-> new MarionetteException(new JsonError("no such cookie", String.format("cookie named \"%s\" does not exist", name), "")))
            )
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "getCookie", result);
            return Response.ok(result).build();
        } catch(Exception e){
            WebApplicationException ex = MarionetteUtil.castException(e);
            LOG.throwing(CLASS, "getCookie", ex);
            throw ex;
        }
    }

    //Add Cookie
    @POST
    @Path("/session/{session_id}/cookie")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addCookie(@PathParam("session_id") String sessionId, String cookie) {
        LOG.entering(CLASS, "addCookie", Stream.of(sessionId, cookie).toArray());
        try{
            String result = SESSIONS.get(sessionId)
            .getClient()
            .addCookie(cookie)
            .thenApply(MarionetteUtil::toObject)
            .thenApply(Objects::toString)
            .get(TIMEOUT, TimeUnit.SECONDS);
            LOG.exiting(CLASS, "addCookie", result);
            return result;
        } catch(Exception e){
            LOG.throwing(CLASS, "addCookie", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "deleteCookie", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "deleteAllCookies", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "performActions", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "releaseActions", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "dismissAlert", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "acceptAlert", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getAlertText", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "setAlertText", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getScreenshot", e);
            throw MarionetteUtil.castException(e);
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
        } catch(Exception e){
            LOG.throwing(CLASS, "getScreenshot", e);
            throw MarionetteUtil.castException(e);
        }
    }
}
