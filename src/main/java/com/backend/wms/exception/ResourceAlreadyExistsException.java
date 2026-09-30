package com.backend.wms.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    public static ResourceAlreadyExistsException of(String entityName, String fieldName, String fieldValue) {
        return new ResourceAlreadyExistsException(
                entityName + " with " + fieldName + " " + fieldValue + " already exists");
    }
}
