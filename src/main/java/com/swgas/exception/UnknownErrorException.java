package com.swgas.exception;

import com.swgas.model.JsonError;
import java.util.Arrays;
import java.util.Objects;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UnknownErrorException extends WebApplicationException {
    public UnknownErrorException(Throwable t){
        super(t.getMessage(), t, Response.status(500).entity(
            new JsonError(
                "unknown error"
                , t.getMessage()
                , Arrays.stream(t.getStackTrace()).map(Objects::toString).reduce("", (a, b) -> String.format("%s\n%s", a, b))
            )
        ).build());
    }
}
