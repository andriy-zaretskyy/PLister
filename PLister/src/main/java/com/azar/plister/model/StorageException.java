package com.azar.plister.model;

/**
 * Created by azar on 12/7/13.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
