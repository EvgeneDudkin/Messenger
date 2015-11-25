package com.example.messengerpigeon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aleksandr on 25.11.2015.
 */
public class LoginPasswordValidator {
    private Pattern symbolsPatternPassword, symbolsPatternLogin;
    private Matcher matcher;

    /**
     * ^(            # Начало группы
     * [A-Za-zА-Яа-я0-9] #Латинские буквы, кириллица, цифры
     * )$            # Конец группы
     */
    private static final String SYMBOLS_PATTERN_LOGIN = "^([A-Za-zА-Яа-я0-9]{4,14})$";
    private static final String SYMBOLS_PATTERN_PASSWORD = "^([a-zA-Z0-9]{4,14})$";


    public LoginPasswordValidator() {
        symbolsPatternPassword = Pattern.compile(SYMBOLS_PATTERN_PASSWORD);
        symbolsPatternLogin = Pattern.compile(SYMBOLS_PATTERN_LOGIN);
    }

    /**
     * Проверка пароля на существование в нем определенных символов.
     *
     * @param password пароль для валидации
     * @return true если пароль валидный, false - пароль не валидный
     */
    public boolean validatePassword(final String password) {
        matcher = symbolsPatternPassword.matcher(password);
        return matcher.matches();
    }

    /**
     * Проверка логина на существование в нем определенных символов.
     *
     * @param login пароль для валидации
     * @return true если пароль валидный, false - пароль не валидный
     */
    public boolean validateLogin(final String login) {
        matcher = symbolsPatternLogin.matcher(login);
        return matcher.matches();
    }
}
