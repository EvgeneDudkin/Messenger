package com.example.messengerpigeon.jsonServerRequests;

/**
 * Исключение если пользователь уже существует
 */
public class userAlreadyExistsException extends Exception {
    public userAlreadyExistsException() {
    }

    public userAlreadyExistsException(String msg) {
        super(msg);
    }
}
