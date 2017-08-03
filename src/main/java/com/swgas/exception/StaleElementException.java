package com.swgas.exception;

import com.swgas.model.JsonError;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class StaleElementException extends WebApplicationException {
    public StaleElementException(JsonError error){
        super(Response.status(400).entity(error).build());
    }
}
