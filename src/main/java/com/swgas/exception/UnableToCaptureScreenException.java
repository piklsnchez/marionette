package com.swgas.exception;

import com.swgas.model.JsonError;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UnableToCaptureScreenException extends WebApplicationException{
    public UnableToCaptureScreenException(JsonError error){
        super(Response.status(500).entity(error).build());
    }
}
