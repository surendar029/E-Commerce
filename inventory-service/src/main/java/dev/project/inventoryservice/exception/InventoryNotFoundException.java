package dev.project.inventoryservice.exception;

public class InventoryNotFoundException extends RuntimeException{
    public InventoryNotFoundException(String message){
        super(message);
    }
}
