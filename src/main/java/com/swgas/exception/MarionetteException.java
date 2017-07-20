package com.swgas.exception;

/**
 *
 * @author ocstest
 */
public class MarionetteException extends RuntimeException{
    public MarionetteException(){super();}
    public MarionetteException(Throwable e){super(e);}
    public MarionetteException(String message){super(message);}
    public MarionetteException(String message, Throwable t){super(message, t);}
}
