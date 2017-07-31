package com.swgas.exception;

import com.swgas.model.JsonError;
import java.util.Arrays;
import java.util.Objects;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class TimeoutException extends WebApplicationException {
    public TimeoutException(Throwable t){
        super(t.getMessage(), t, Response.status(408).entity(
            new JsonError(
                "timeout"
                , t.getMessage()
                , Arrays.stream(t.getStackTrace()).map(Objects::toString).reduce("", (a, b) -> String.format("%s\n%s", a, b))
            )
        ).build());
    }
}
