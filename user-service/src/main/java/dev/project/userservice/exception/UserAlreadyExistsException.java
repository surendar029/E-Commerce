package dev.project.userservice.exception;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException (String message){
        super(message);
    }
}
