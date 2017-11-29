package com.swgas.exception;

import com.swgas.model.JsonError;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class NoSuchCookieException extends WebApplicationException {
    private static final String CLASS = NoSuchCookieException.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    
    public NoSuchCookieException(JsonError error){
        super(Response.status(404).entity(error).build());
        LOG.info(String.format("%s: <init>: %s", CLASS, this));
    }
}
