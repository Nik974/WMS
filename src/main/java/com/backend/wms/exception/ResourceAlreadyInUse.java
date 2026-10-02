package com.backend.wms.exception;

public class ResourceAlreadyInUse extends RuntimeException {
    public ResourceAlreadyInUse(String message) {
        super(message);
    }
    public static ResourceAlreadyInUse of(String entityName, String fieldName, String fieldValue) {
        return new ResourceAlreadyInUse(
                entityName + " with " + fieldName + " " + fieldValue + " is already in use");
    }
}
