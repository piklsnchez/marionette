package com.swgas.exception;

import com.swgas.model.JsonError;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class NoSuchElementException extends WebApplicationException {
    public NoSuchElementException(JsonError error){
        super(Response.status(404).entity(error).build());
    }
}
