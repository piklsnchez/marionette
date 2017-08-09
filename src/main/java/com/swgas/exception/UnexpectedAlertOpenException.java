package com.swgas.exception;

import com.swgas.model.JsonError;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UnexpectedAlertOpenException extends WebApplicationException {
    public UnexpectedAlertOpenException(JsonError error){
        super(Response.status(500).entity(error).build());
    }
}
