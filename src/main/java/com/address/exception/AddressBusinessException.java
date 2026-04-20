package com.address.exception;

public class AddressBusinessException extends RuntimeException {

    public AddressBusinessException(String message) {
        super(message);
    }

    public AddressBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
