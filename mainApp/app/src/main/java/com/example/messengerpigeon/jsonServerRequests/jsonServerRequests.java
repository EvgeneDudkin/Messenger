package com.example.messengerpigeon.jsonServerRequests; /**
 * Created by Kirill2 on 15.11.2015.
 */

import org.json.*;
import com.example.messengerpigeon.miniClasses.dialog;
import com.example.messengerpigeon.miniClasses.friend;

import java.util.Objects;

/**
 * Родительский класс всех запросов
 */
public class jsonServerRequests {
    /**
     * Строка "ответа" сервера. Точнее это строка, которая обозначет реакцию сервера на наш запрос.
     */
    protected String response = "";
    /**
     * Строка запроса
     */
    protected String strRequest = "";


    protected String strProtectedRequest = "";

    protected JSONObject jsonProtectedRequest = null;




    /**
     * JSON объект запроса
     */
    protected JSONObject jsonRequest = null;

    /**
     * (виртуальный метод)
     * Обработчик ответа сервера
     * @param input Строка, которую вернул сервер
     */
    protected void responseHandler( String input) {}

    /**
     * Геттер получения строки запроса
     * @return строка запроса
     */
    public String get_Request() {
        return strRequest;
    }

    /**
     * Геттер получения JSON объекта запроса
     * @return JSON объект запроса
     */
    public JSONObject get_Request_json() {
        return jsonRequest;
    }

    /**
     * Родительский метод обработки ошибок
     * Если ошибка будет обработана, будет вызвавно исключение
     * TODO: обработка неизвестных ошибок
     * @throws Exception
     */
    protected void errorHandler() throws Exception {
        switch (response) {
            case "":
                throw new Exception("response not found");
            case "j1":
                throw new Exception("event handler :(");
        }
    }

}

