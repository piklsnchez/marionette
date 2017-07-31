package com.swgas.exception;

public class WebDriverException extends RuntimeException{
    public WebDriverException(){
        super();
    }
    public WebDriverException(String message){
        super(message);
    }
    public WebDriverException(String message, Throwable t){
        super(message, t);
    }
    
}
