package com.travellerguide.traveller_guide_api.interfaces.rest.error;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
