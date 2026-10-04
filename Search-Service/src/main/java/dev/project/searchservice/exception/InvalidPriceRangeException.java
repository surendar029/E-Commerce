package dev.project.searchservice.exception;

public class InvalidPriceRangeException extends RuntimeException{
    public InvalidPriceRangeException(String message){
        super(message);
    }
}
