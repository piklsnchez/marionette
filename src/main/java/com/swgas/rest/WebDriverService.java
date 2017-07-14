package com.swgas.rest;

import com.swgas.marionette.MarionetteFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import javax.ws.rs.DELETE;
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
        try{
            ProcessBuilder procBuilder = new ProcessBuilder("firefox", "--marionette", "-P", "marionette", "--new-instance");
            Process proc = procBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while(reader.lines().map(line -> {LOG.info(line);return line;}).noneMatch(line -> line.contains("Listening"))){}
            Session session = new Session();
            session.setProc(proc);
            String sessionId = MarionetteFactory.getAsync(HOST, PORT)
            .thenCompose(c -> {session.setClient(c); return c.newSession();})
            .get(TIMEOUT, TimeUnit.SECONDS);
            session.setSessionId(sessionId);
            SESSIONS.put(sessionId, session);
            return sessionId;
            //FIXME return http error
        } catch(IOException | InterruptedException | ExecutionException | TimeoutException e){
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
                result = session.getClient().quitApplication(Collections.singletonList("eForceQuit"))
                .get(TIMEOUT, TimeUnit.SECONDS);
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
        return "??";
    }

    //Get Timeouts
    @GET
    @Path("/session/{session_id}/timeouts")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTimeouts() {
        return "??";
    }

    //Set Timeouts
    @POST
    @Path("/session/{session_id}/timeouts")
    @Produces(MediaType.APPLICATION_JSON)
    public String setTimeouts() {
        return "??";
    }

    //Go
    @POST
    @Path("/session/{session_id}/url")
    @Produces(MediaType.APPLICATION_JSON)
    public String setUrl() {
        return "??";
    }

    //Get Current URL
    @GET
    @Path("/session/{session_id}/url")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUrl() {
        return "??";
    }

    //Back
    @POST
    @Path("/session/{session_id}/back")
    @Produces(MediaType.APPLICATION_JSON)
    public String back() {
        return "??";
    }

    //Forward
    @POST
    @Path("/session/{session_id}/forward")
    @Produces(MediaType.APPLICATION_JSON)
    public String forward() {
        return "??";
    }

    //Refresh
    @POST
    @Path("/session/{session_id}/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public String refresh() {
        return "??";
    }

    //Get Title
    @GET
    @Path("/session/{session_id}/title")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTitle() {
        return "??";
    }

    //Get Window Handle
    @GET
    @Path("/session/{session_id}/window")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWindow() {
        return "??";
    }

    //Close Window
    @DELETE
    @Path("/session/{session_id}/window")
    @Produces(MediaType.APPLICATION_JSON)
    public String closeWindow() {
        return "??";
    }

    //Switch To Window
    @POST
    @Path("/session/{session_id}/window")
    @Produces(MediaType.APPLICATION_JSON)
    public String setWindow() {
        return "??";
    }

    //Get Window Handles
    @GET
    @Path("/session/{session_id}/window/handles")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWindows() {
        return "??";
    }

    //Switch To Frame
    @POST
    @Path("/session/{session_id}/frame")
    @Produces(MediaType.APPLICATION_JSON)
    public String setFrame() {
        return "??";
    }

    //Switch To Parent Frame
    @POST
    @Path("/session/{session_id}/frame/parent")
    @Produces(MediaType.APPLICATION_JSON)
    public String parentFrame() {
        return "??";
    }

    //Get Window Rect
    @GET
    @Path("/session/{session_id}/window/rect")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWindowDimension() {
        return "??";
    }

    //Set Window Rect
    @POST
    @Path("/session/{session_id}/window/rect")
    @Produces(MediaType.APPLICATION_JSON)
    public String setWindowDimension() {
        return "??";
    }

    //Maximize Window
    @POST
    @Path("/session/{session_id}/window/maximize")
    @Produces(MediaType.APPLICATION_JSON)
    public String maximizeWindow() {
        return "??";
    }

    //Minimize Window
    @POST
    @Path("/session/{session_id}/window/minimize")
    @Produces(MediaType.APPLICATION_JSON)
    public String minimizeWindow() {
        return "??";
    }

    //Fullscreen Window
    @POST
    @Path("/session/{session_id}/window/fullscreen")
    @Produces(MediaType.APPLICATION_JSON)
    public String fullscreen() {
        return "??";
    }

    //Get Active Element
    @GET
    @Path("/session/{session_id}/element/active")
    @Produces(MediaType.APPLICATION_JSON)
    public String getActiveElement() {
        return "??";
    }

    //Find Element
    @POST
    @Path("/session/{session_id}/element")
    @Produces(MediaType.APPLICATION_JSON)
    public String findElement() {
        return "??";
    }

    //Find Elements
    @POST
    @Path("/session/{session_id}/elements")
    @Produces(MediaType.APPLICATION_JSON)
    public String findElements() {
        return "??";
    }

    //Find Element From Element
    @POST
    @Path("/session/{session_id}/element/{element_id}/element")
    @Produces(MediaType.APPLICATION_JSON)
    public String findElementFromElement() {
        return "??";
    }

    //Find Elements From Element
    @POST
    @Path("/session/{session_id}/element/{element_id}/elements")
    @Produces(MediaType.APPLICATION_JSON)
    public String findElementsFromElement() {
        return "??";
    }

    //Is Element Selected
    @GET
    @Path("/session/{session_id}/element/{element_id}/selected")
    @Produces(MediaType.APPLICATION_JSON)
    public String isElementSelected() {
        return "??";
    }

    //Get Element Attribute
    @GET
    @Path("/session/{session_id}/element/{element_id}/attribute/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementAttribute() {
        return "??";
    }

    //Get Element Property
    @GET
    @Path("/session/{session_id}/element/{element_id}/property/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementProperty() {
        return "??";
    }

    //Get Element CSS Value
    @GET
    @Path("/session/{session_id}/element/{element_id}/css/{property_name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementCss() {
        return "??";
    }

    //Get Element Text
    @GET
    @Path("/session/{session_id}/element/{element_id}/text")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementText() {
        return "??";
    }

    //Get Element Tag Name
    @GET
    @Path("/session/{session_id}/element/{element_id}/name")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementTagName() {
        return "??";
    }

    //Get Element Rect
    @GET
    @Path("/session/{session_id}/element/{element_id}/rect")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementDimension() {
        return "??";
    }

    //Is Element Enabled
    @GET
    @Path("/session/{session_id}/element/{element_id}/enabled")
    @Produces(MediaType.APPLICATION_JSON)
    public String isElementEnabled() {
        return "??";
    }

    //Element Click
    @POST
    @Path("/session/{session_id}/element/{element_id}/click")
    @Produces(MediaType.APPLICATION_JSON)
    public String clickElement() {
        return "??";
    }

    //Element Clear
    @POST
    @Path("/session/{session_id}/element/{element_id}/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public String clearElement() {
        return "??";
    }

    //Element Send Keys
    @POST
    @Path("/session/{session_id}/element/{element_id}/value")
    @Produces(MediaType.APPLICATION_JSON)
    public String sendKeysToElement() {
        return "??";
    }

    //Get Page Source
    @GET
    @Path("/session/{session_id}/source")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPageSource() {
        return "??";
    }

    //Execute Script
    @POST
    @Path("/session/{session_id}/execute/sync")
    @Produces(MediaType.APPLICATION_JSON)
    public String executeScript() {
        return "??";
    }

    //Execute Async Script
    @POST
    @Path("/session/{session_id}/execute/async")
    @Produces(MediaType.APPLICATION_JSON)
    public String executeScriptAsync() {
        return "??";
    }

    //Get All Cookies
    @GET
    @Path("/session/{session_id}/cookie")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCookies() {
        return "??";
    }

    //Get Named Cookie
    @GET
    @Path("/session/{session_id}/cookie/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCookie() {
        return "??";
    }

    //Add Cookie
    @POST
    @Path("/session/{session_id}/cookie")
    @Produces(MediaType.APPLICATION_JSON)
    public String addCookie() {
        return "??";
    }

    //Delete Cookie
    @DELETE
    @Path("/session/{session_id}/cookie/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String removeCookie() {
        return "??";
    }

    //Delete All Cookies
    @DELETE
    @Path("/session/{session id)/cookie")
    @Produces(MediaType.APPLICATION_JSON)
    public String removeCookies() {
        return "??";
    }

    //Perform Actions
    @POST
    @Path("/session/{session_id}/actions")
    @Produces(MediaType.APPLICATION_JSON)
    public String performActions() {
        return "??";
    }

    //Release Actions
    @DELETE
    @Path("/session/{session_id}/actions")
    @Produces(MediaType.APPLICATION_JSON)
    public String releaseActions() {
        return "??";
    }

    //Dismiss Alert
    @POST
    @Path("/session/{session_id}/alert/dismiss")
    @Produces(MediaType.APPLICATION_JSON)
    public String dismissAlert() {
        return "??";
    }

    //Accept Alert
    @POST
    @Path("/session/{session_id}/alert/accept")
    @Produces(MediaType.APPLICATION_JSON)
    public String acceptAlert() {
        return "??";
    }

    //Get Alert Text
    @GET
    @Path("/session/{session_id}/alert/text")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAlertText() {
        return "??";
    }

    //Send Alert Text
    @POST
    @Path("/session/{session_id}/alert/text")
    @Produces(MediaType.APPLICATION_JSON)
    public String setAlertText() {
        return "??";
    }

    //Take Screenshot
    @GET
    @Path("/session/{session_id}/screenshot")
    @Produces(MediaType.APPLICATION_JSON)
    public String getScreenshot() {
        return "??";
    }

    //Take Element Screenshot
    @GET
    @Path("/session/{session_id}/element/{element_id}/screenshot")
    @Produces(MediaType.APPLICATION_JSON)
    public String getElementScreenshot() {
        return "??";
    }
}
