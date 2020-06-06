package com.distri.mdlware.exception;

public class InvalidFormatException extends RuntimeException{


    public InvalidFormatException(String message){

        super(message);
    }

    public InvalidFormatException(String message, Exception e){
        super(message, e);
    }

    public InvalidFormatException(Exception e){
        super(e);
    }
}
