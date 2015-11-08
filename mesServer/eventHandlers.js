/**
 * Created by Kirill2 on 27.10.2015.
 */
var md5 = require('md5'),
    getToken = require('./token.js').createToken;

function dataHandler(data, sock, conect) {
    data = data.toString();
    for (var i = 0; i < data.length; i++) {
        if (data[i] == '{') {
            data = data.substr(i);
            break;
        }
    }
    console.log(data);
    try {
        var query = JSON.parse(data);
    }
    catch (err) {
        sock.write("Error 1");
        sock.destroy();
        return;
    }
    console.log(query);
    if (query.auth != null) {
        authRequest(sock, conect, query);
    }
    //Если регистрация
    else if (query.reg != null) {
        regRequest(sock, conect, query);
    }
    else if (query.friends != null) {
        friendsRequest(sock, conect, query);
    }
    else if (query.token != null) {
    }
    else {
        var answer = {response: "Error 2"};
        sock.write(JSON.stringify(answer));
        sock.destroy();
    }
}

/**
 * Запрос авторизации
 * @param sock сокет
 * @param conect mysql connect
 * @param query запрос
 */
function authRequest(sock, conect, query) {
    var answer = {response: "", token: ""};
    if (query.auth.login == null || query.auth.pass == null) {
        answer.response = "Error a8";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }
    //Ищем в базе логин/пасс
    conect.query('SELECT id from users where login="' + query.auth.login + '" and pass="' + md5(query.auth.pass) + '"', function (err, rows, fields) {
        //Нет ошибки
        if (err) {
            answer.response = "Error a4";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        if (rows.length != 1) {
            answer.response = "Error a3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        var flag = false;
        var i = 0;
        while (!flag && i++ <= 10) {
            var token = "";
            getToken(token);
            //Проверка на то, что такое токен уже есть. todo: НАДО ЛИ?!?!
            conect.query('SELECT id from tokens where token="' + token + '"', function (err2, rows2, fields2) {
                //Проверка на ошибку
                if (err2) {
                    answer.response = "Error a6";
                    sock.write(JSON.stringify(answer));
                    sock.destroy();
                    return;
                }
                //Проверяем что такого токена нет
                if (rows.length === 0) {
                    answer.response = "OK";
                    answer.token = token;
                    //Вставляем токен в базу
                    conect.query('INSERT into tokens (userId, token) values (' + rows[0]['id'] + ', "' + token + '")', function (err3, rows3, fields3) {
                        //Проверка ошибки
                        if (!err3) {
                            sock.write(JSON.stringify(answer));
                            sock.destroy();
                            return;
                        }
                        else {
                            answer.response = "Error a7";
                            sock.write(JSON.stringify(answer));
                            sock.destroy();
                            return;
                        }
                    });
                }
            });
        }
        //Ошибки ошибочки ошибулички
        answer.response = "Error a5";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    });
}

function regRequest(sock, conect, query) {
    var answer = {response: "", token: ""};
    //Проверка на наличие логина и паса
    if (query.reg.login == null || query.reg.pass == null) {
        answer.response = "Error r5";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }

    //Проверяем, етсь ли у нас такой уже в базе
    conect.query('SELECT COUNT(*) from users where login="' + query.reg.login + '"', function (err, rows, fields) {
        //Ошибки нет
        if (err) {
            answer.response = "Error r4";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //Если есть такой
        if (rows[0]['COUNT(*)'] === 1) {
            //Ошибка
            answer.response = "Error r3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //Если нету такого
        //Вставляем в базу
        conect.query('INSERT INTO users (login, pass) VALUES ("' + query.reg.login + '", "' + md5(query.reg.pass) + '")', function (err2, rows2, fields2) {
            //ошибка
            if (err2) {
                answer.response = "Error r6";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //Смотрим id
            conect.query('SELECT id from users where login="' + query.auth.login + '" and pass="' + md5(query.auth.pass) + '"', function (err3, rows3, fields3) {
                if (err3) {
                    answer.response = "Error r7";
                    sock.write(JSON.stringify(answer));
                    sock.destroy();
                    return;
                }

                var flag = false;
                var i = 0;
                while (!flag && i++ <= 10) {
                    var token = "";
                    getToken(token);
                    //Проверка на то, что такое токен уже есть. todo: НАДО ЛИ?!?!
                    conect.query('SELECT id from tokens where token="' + token + '"', function (err2, rows2, fields2) {
                        //Проверка на ошибку
                        if (err2) {
                            answer.response = "Error r9";
                            sock.write(JSON.stringify(answer));
                            sock.destroy();
                            return;
                        }
                        //Проверяем что такого токена нет
                        if (rows.length === 0) {
                            answer.response = "OK";
                            answer.token = token;
                            //Вставляем токен в базу
                            conect.query('INSERT into tokens (userId, token) values (' + rows3[0]['id'] + ', "' + token + '")', function (err3, rows3, fields3) {
                                //Проверка ошибки
                                if (!err3) {
                                    sock.write(JSON.stringify(answer));
                                    sock.destroy();
                                    return;
                                }
                                else {
                                    answer.response = "Error r8";
                                    sock.write(JSON.stringify(answer));
                                    sock.destroy();
                                    return;
                                }
                            });
                        }
                    });
                }
                //Ошибки ошибочки ошибулички
                answer.response = "Error r5";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;

            });
        });
    });
}

function friendsRequest(sock, conect, query) {
    var answer = {response: "", friends: []};
    if(query.friends.token == null) {
        answer.response = "Error fl1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }
    var id;
    conect.query('SELECT id from tokens where token="'+ query.friends.token +'"' + query.friends.login + '") ', function (err, rows, fields) {
        if(err) {
            answer.response = "Error fl2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        if(rows.length === 0) {
            answer.response = "Error fl3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        id = rows[0]['id'];
    });

    conect.query('select id, login from users where exists(select * from friendrequests where (idSender=id and idRecipient='+ id +') or (idSender=4 and idRecipient='+ id +'))'+ query.friends.token +'"' + query.friends.login + '") ', function (err, rows, fields) {
        if(err) {
            answer.response = "Error fl4";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        var friendsList = [];
        for (var i = 0; i < rows.length; i++) {
            friendsList[i] = {login: rows[i]['login'], id: rows[i]['id']};
        }
        answer.response = "OK";
        answer.friends = friendsList;
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    });
}


exports.dataH = dataHandler;