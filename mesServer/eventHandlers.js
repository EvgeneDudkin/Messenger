/**
 * Created by Kirill2 on 27.10.2015.
 */
var crypto = require('crypto');
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
    //Авторизация
    if (query.auth != null) {
        authRequest(sock, conect, query);
    }
    //Если регистрация
    else if (query.reg != null) {
        regRequest(sock, conect, query);
    }
    //Запрос на список друзей
    else if (query.friendsL != null) {
        friendsListRequest(sock, conect, query);
    }
    //Запрос на поиск друзей
    else if (query.friendsS != null) {
        friendsSearchRequest(sock, conect, query);
    }
    //Запрос на запрос дружбы
    else if (query.friendRequest != null) {
        friendRequest(sock, conect, query);
    }
    //Запрос на список диалогов
    else if (query.dialogsL != null) {
        dialogsListRequest(sock, conect, query);
    }
    //Запрос на создание диалога
    else if (query.dialogC != null) {
        dialogCreateRequest(sock, conect, query);
    }
    //Запрос на отправку сообщения
    else if (query.sendMsg != null) {
        sendMsgRequest(sock, conect, query);
    }
    //Запрос на список диалогов
    else if (query.lastNmsg != null) {
        lastNmsgRequest(sock, conect, query);
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
    var answer = {response: "", token: "", dialogs: []};
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
        var token = crypto.randomBytes(32).toString('hex');
        //Проверка на то, что такое токен уже есть. todo: НАДО ЛИ?!?!
        conect.query('SELECT COUNT(*) from tokens where token="' + token + '"', function (err2, rows2) {
            //Проверка на ошибку
            if (err2) {
                answer.response = "Error a6";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //Проверяем что такого токена нет
            if (rows2[0]['COUNT(*)'] === 0) {
                answer.response = "OK";
                answer.token = token;
                //Вставляем токен в базу
                conect.query('INSERT into tokens (userId, token) values(' + rows[0]['id'] + ', "' + token + '")', function (err3) {
                    //Проверка ошибки
                    if (!err3) {/*
                        conect.query('select * from users where ' +
                            'exists(select * from friendrequests where reqStatus=1 and ' +
                            '(idSender=id and idRecipient=' + rows[0]['id'] + ') or (idRecipient=id and idSender=' + rows[0]['id'] + '))', function (err4, rows4) {
                            if (err4) {
                                answer.response = "Error afl4";
                                sock.write(JSON.stringify(answer));
                                sock.destroy();
                                return;
                            }
                            var friendsList = [];
                            for (var i = 0; i < rows4.length; i++) {
                                friendsList[i] = {
                                    login: rows4[i]['login'],
                                    id: rows4[i]['id'],
                                    firstName: rows4[i]['firstName'],
                                    lastName: rows4[i]['lastName']
                                };
                            }
                            answer.response = "OK";
                            answer.friends = friendsList;
                            sock.write(JSON.stringify(answer));
                            sock.destroy();
                        });
                        */
                        conect.query('select id, name from dialogs left join userdialog on dialogs.id = userdialog.dialogId where userId = ' + rows[0]['id'], function (err4, rows4) {
                            if (err4) {
                                answer.response = "Error adl4";
                                sock.write(JSON.stringify(answer));
                                sock.destroy();
                                return;
                            }
                            var dialogsList = [];
                            for (var i = 0; i < rows4.length; i++) {
                                dialogsList[i] = {
                                    id: rows4[i]['id'],
                                    name: rows4[i]['name']
                                };
                            }
                            answer.response = "OK";
                            answer.dialogs = dialogsList;
                            sock.write(JSON.stringify(answer));
                            sock.destroy();
                        });
                    }
                    else {
                        answer.response = "Error a7";
                        answer.token = "";
                        sock.write(JSON.stringify(answer));
                        sock.destroy();
                    }
                });
            }
            else {
                answer.response = "Error a8";
                answer.token = "";
                sock.write(JSON.stringify(answer));
                sock.destroy();
            }
        });
        //
        //Ошибки ошибочки ошибулички
        /*if(i == 11) {
         answer.response = "Error a5";
         sock.write(JSON.stringify(answer));
         sock.destroy();
         return;
         }*/
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
    conect.query('SELECT * from users where login="' + query.reg.login + '"', function (err, rows) {
        //Ошибки нет
        if (err) {
            answer.response = "Error r4";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //console.log(rows[0]);
        //Если есть такой
        if (rows.length === 1) {
            //Ошибка
            answer.response = "Error r3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //Если нету такого
        //Вставляем в базу
        conect.query('INSERT INTO users (login, pass) VALUES ("' + query.reg.login + '", "' + md5(query.reg.pass) + '")', function (err2, rows2) {
            //ошибка
            if (err2) {
                answer.response = "Error r6";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }

            var token = crypto.randomBytes(32).toString('hex');
            //Проверка на то, что такое токен уже есть. todo: НАДО ЛИ?!?!
            conect.query('SELECT COUNT(*) from tokens where token="' + token + '"', function (err4, rows4) {
                //Проверка на ошибку
                if (err4) {
                    answer.response = "Error r9";
                    sock.write(JSON.stringify(answer));
                    sock.destroy();
                    return;
                }
                //Проверяем что такого токена нет
                if (rows4[0]['COUNT(*)'] === 0) {
                    answer.response = "OK";
                    answer.token = token;
                    //Вставляем токен в базу
                    conect.query('INSERT into tokens (userId, token) values (' + rows2.insertId + ', "' + token + '")', function (err5) {
                        //Проверка ошибки
                        if (!err5) {
                            sock.write(JSON.stringify(answer));
                            sock.destroy();
                        }
                        else {
                            answer.response = "Error r8";
                            answer.token = "";
                            sock.write(JSON.stringify(answer));
                            sock.destroy();
                        }
                    });
                }
                else {
                    answer.response = "Error r9";
                    answer.token = "";
                    sock.write(JSON.stringify(answer));
                    sock.destroy();
                }
            });
            /*}
             //Ошибки ошибочки ошибулички
             answer.response = "Error r5";
             sock.write(JSON.stringify(answer));
             sock.destroy();
             return;*/

        });
    });
}


//Запрос на список друзей
function friendsListRequest(sock, conect, query) {
    var answer = {response: "", friends: []};
    //В запросе должен быть токен
    if (query.friendsL.token == null) {
        answer.response = "Error fl1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }
    //Выбираем из списка токенов нужный id
    conect.query('SELECT userId from tokens where token="' + query.friendsL.token + '"', function (err, rows) {
        var id;
        //Проверка на ошибку
        if (err) {
            answer.response = "Error fl2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //Проверка, что такой токен вообще есть
        if (rows.length === 0) {
            answer.response = "Error fl3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        id = rows[0]['userId'];
        //Выбираем всех друзей из БД
        conect.query('select * from users where ' +
            'exists(select * from friendrequests where reqStatus=1 and (idSender=id and idRecipient=' + id + ') ' +
            'or (idRecipient=id and idSender=' + id + '))', function (err2, rows2) {
            //Проверка на ошибку
            if (err2) {
                answer.response = "Error fl4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //Формируем JSON список друзей
            var friendsList = [];
            for (var i = 0; i < rows2.length; i++) {
                friendsList[i] = {
                    login: rows2[i]['login'],
                    id: rows2[i]['id'],
                    firstName: rows2[i]['firstName'],
                    lastName: rows2[i]['lastName']
                };
            }
            answer.response = "OK";
            answer.friends = friendsList;
            sock.write(JSON.stringify(answer));
            sock.destroy();
        });
    });

}

function friendRequest(sock, conect, query) {
    var answer = {response: "", idRecipient: null};
    if (query.friendRequest.token == null || query.friendRequest.idRecipient == null) {
        answer.response = "Error fr1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
    }

    conect.query('SELECT userId from tokens where token="' + query.friendsL.token + '"', function (err, rows) {
        var id;
        //Проверка на ошибку
        if (err) {
            answer.response = "Error fr2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //Проверка, что такой токен вообще есть
        if (rows.length === 0) {
            answer.response = "Error fr3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }

        //Если все ОК, тогда вставляем запись. Таблица friendrequests сама проверяет наличие дублекатов,
        // то есть если в таблице уже есть 1, 4 (то есть user 1 сделал запрос на дружбу user 4)
        // то добавить 1, 4 или 4, 1 уже нельзя в БД.(выдаст ошибку если попробовать)
        id = rows[0]['userId'];
        conect.query('INSERT INTO friendrequests (idSender, idRecipient) VALUES (' + id + ', ' + query.friendRequest.idRecipient + ')', function (err2, rows2) {
            if (err2) {
                answer.response = "Error fr4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
            }
            else {
                answer.response = "OK";
                answer.idRecipient = query.friendRequest.idRecipient;
                sock.write(JSON.stringify(answer));
                sock.destroy();
            }
        });
    });
}

//Список диалогов
function dialogsListRequest(sock, conect, query) {
    var answer = {response: "", dialogs: []};
    //Проверка
    if (query.dialogsL.token == null) {
        answer.response = "Error dl1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
    }
    //
    conect.query('SELECT userId from tokens where token="' + query.friendsL.token + '"', function (err, rows) {
        //Проверка на ошибку
        if (err) {
            answer.response = "Error dl2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //Проверка, что такой токен вообще есть
        if (rows.length === 0) {
            answer.response = "Error dl3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }

        //TODO: exists VS just where VS join ?!??!?!
        //Собственно сама выборка диалогов
            conect.query('SELECT * FROM dialogs where exists(SELECT * FROM userdialog where userId = ' + rows[0]['userId'] + ' and dialogId = id)', function (err2, rows2) {
                if (err2) {
                answer.response = "Error dl4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            var dialogsList = [];
            for (var i = 0; i < rows2.length; i++) {
                dialogsList[i] = {
                    id: rows2[i]['id'],
                    name: rows2[i]['name']
                };
            }
            answer.response = "OK";
            answer.dialogs = dialogsList;
            sock.write(JSON.stringify(answer));
            sock.destroy();
        });
    });
}

//Создание диалога
function dialogCreateRequest(sock, conect, query) {
    var answer = {response: "", dialog: null};
    if (query.dialogC.token == null || query.dialogC.idRecipient == null || query.dialogC.dialogName == null) {
        answer.response = "Error dc1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }

    conect.query('SELECT userId from tokens where token="' + query.friendsL.token + '"', function (err, rows) {
        //Проверка на ошибку
        if (err) {
            answer.response = "Error dc2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //Проверка, что такой токен вообще есть
        if (rows.length === 0) {
            answer.response = "Error dc3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }

        //Проверка что userId и idRecipient друзья
        conect.query('SELECT reqStatus FROM friendRequests WHERE ' +
            'idSender=' + query.dialogC.idRecipient + ' and idRecipient=' + rows[0]['iserId'] + ') or (idRecipient=' + query.dialogC.idRecipient + ' and idSender=' + rows[0]['iserId'], function (err5, rows5) {
            if (err5) {
                answer.response = "Error dc7";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            if (rows.length === 0 || rows[0]['reqStatus'] === 0) {
                answer.response = "Error dc8";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //Создаем диалог
            conect.query('INSERT INTO dialogs (name) values ("' + query.dialogC.dialogName + '")', function (err2, rows2) {
                if (err2) {
                    answer.response = "Error dc4";
                    sock.write(JSON.stringify(answer));
                    sock.destroy();
                    return;
                }
                //Добавляем первого юзера
                conect.query('INSERT INTO userdialog (userId, dialogId) values (' + rows[0]['userId'] + ', ' + rows2.insertId + ')', function (err3) {
                    if (err3) {
                        answer.response = "Error dc5";
                        sock.write(JSON.stringify(answer));
                        sock.destroy();
                        return;
                    }
                    //Добавляем второго юзера
                    conect.query('INSERT INTO userdialog (userId, dialogId) values (' + query.dialogC.idRecipient + ', ' + rows2.insertId + ')', function (err4) {
                        if (err4) {
                            answer.response = "Error dc6";
                            sock.write(JSON.stringify(answer));
                            sock.destroy();
                            return;
                        }
                        answer.response = "OK";
                        answer.dialog = {id: rows2.insertId, name: query.dialogC.dialogName};
                        sock.write(JSON.stringify(answer));
                        sock.destroy();
                    });
                });

            });
        });
    });
}

function friendsSearchRequest(sock, conect, query) {
    var answer = {response: "", friends: []};
    if (query.friendsS.token == null || query.friendsS.searchPattern == null) {
        answer.response = "Error fs1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }
    //Выбираем из списка токенов нужный id
    conect.query('SELECT userId from tokens where token="' + query.friendsL.token + '"', function (err, rows) {
        var id;
        //Проверка на ошибку
        if (err) {
            answer.response = "Error fs2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //Проверка, что такой токен вообще есть
        if (rows.length === 0) {
            answer.response = "Error fs3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        id = rows[0]['userId'];
        //Выбираем всех друзей из БД
        conect.query('select id, login, firstName, lastName from users where (login REGEXP "'+query.friendsS.searchPattern+'") = 1 ' +
            'and not exists(select * from friendrequests where reqStatus=1 and (idSender=id and idRecipient=' + id + ') ' +
            'or (idRecipient=id and idSender=' + id + ')))', function (err2, rows2) {
            //Проверка на ошибку
            if (err2) {
                answer.response = "Error fs4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //Формируем JSON список друзей
            var friendsList = [];
            for (var i = 0; i < rows2.length; i++) {
                friendsList[i] = {
                    login: rows2[i]['login'],
                    id: rows2[i]['id'],
                    firstName: rows2[i]['firstName'],
                    lastName: rows2[i]['lastName']
                };
            }
            answer.response = "OK";
            answer.friends = friendsList;
            sock.write(JSON.stringify(answer));
            sock.destroy();
        });
    });
}

exports.dataH = dataHandler;