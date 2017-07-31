package com.swgas.exception;

import com.swgas.model.JsonError;
import java.util.Arrays;
import java.util.Objects;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class InvalidSessionIdException extends WebApplicationException {
    public InvalidSessionIdException(String sessionId, Throwable t){
        super(t.getMessage(), t, Response.status(404).entity(
            new JsonError(
                "invalid session id"
                , t.getMessage()
                , Arrays.stream(t.getStackTrace()).map(Objects::toString).reduce("", (a, b) -> String.format("%s\n%s", a, b))
            )).build()
        );
    }
}
