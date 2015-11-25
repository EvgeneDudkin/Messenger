package com.example.messengerpigeon.jsonServerRequests;

/**
 * Исключение отсутствия пользователя в БД
 */
public class userNotFoundException extends Exception {
    public userNotFoundException() {
    }

    public userNotFoundException(String msg) {
        super(msg);
    }
}
