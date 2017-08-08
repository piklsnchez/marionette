package com.swgas.exception;

import com.swgas.model.JsonError;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UnsupportedOperationException extends WebApplicationException {
    public UnsupportedOperationException(){
        this(new JsonError("unsupported operation", "", ""));
    }
    public UnsupportedOperationException(JsonError error) {
        super(Response.status(500).entity(error).build());
    }
}
