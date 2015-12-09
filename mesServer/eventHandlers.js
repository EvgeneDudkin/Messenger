/**
 * Created by Kirill2 on 27.10.2015.
 */
 //\[.*\]\s\[DEBUG\]\s\[default\]
 var crypto = require('crypto');
 var md5 = require('md5'),
 getToken = require('./token.js').createToken;
 var encrypt = require('./DPcrypt').encrypt;
 var decrypt = require('./DPcrypt').decrypt;

 var log4js = require('./log4js');
 var logger = log4js.getLogger('files');
 var logconsole = log4js.getLogger('cons');

 function writeAndDestroy(sock, str) {
   logger.info("OUTPUT before encrypt:")
   logger.debug(str);
  // logger.debug(JSON.parse(str));
   try {
      var resp = JSON.parse(str);
      logconsole.info("output");
      if(resp.response == "OK") {
         logconsole.info(resp.response);
      }
      else {
         logconsole.error(resp.response);
      }
   }
   catch (err) {
      logconsole.error(err);
   }
   var lngth = str.length;
   var salt = Math.floor(Math.random() * (16127 + 1));
   var encStr = new encrypt(lngth, salt);
   str = encStr.encrypt(str);

   //logger.info("OUTPUT after encrypt:")
   //logger.debug(str);

   var res = {query: str, key: lngth, salt: salt};
   sock.write(JSON.stringify(res));

   logger.info("OUTPUT sock.write:")
   logger.debug(res);

   //sock.write(str);
   sock.destroy();
}

function dataHandler(data, sock, conect) {
   data = data.toString();
   logger.info("INPUT: data:")
   logger.debug(data);
   for (var i = 0; i < data.length; i++) {
      if (data[i] == '{') {
         data = data.substr(i);
         break;
      }
   }
   try {
      var query = JSON.parse(data);
      if (query.query != null && query.key != null) {
         //logger.info("INPUT data befor decrypt:")
         //logger.debug(query.query);
         //console.log(query.query);
         var decStr = new decrypt(query.key, query.salt);

         var str = decStr.decrypt(query.query, query.key).trim();
         for (var i = str.length - 1; i >= 0; i--) {
            if (str[i] == '}') {
               str = str.substr(0, i + 1);
               break;
            }
         }
         //logger.info("INPUT data after decrypt:")
         //logger.debug(str);
         //console.log(str);
         query = JSON.parse(str);
      }
   }
   catch (err) {
      logger.error(err);
      //console.log(err);
      var answer = {response: "Error 1"};
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
      return;
   }
   logger.info("INPUT after JSON parse:")
   logger.debug(query);
   //console.log(query);
   //Авторизация
   logconsole.info("INPUT:");
   if (query.auth != null) {
      logconsole.info("auth");
      authRequest(sock, conect, query);
   }
   //Если регистрация
   else if (query.reg != null) {
      logconsole.info("reg");
      regRequest(sock, conect, query);
   }
   //Запрос на список друзей
   else if (query.friendsL != null) {
      logconsole.info("firendsL");
      friendsListRequest(sock, conect, query);
   }
   else if (query.onlyFriendsL != null) {
      logconsole.info("onlyFriendsL");

   }
   //Запрос на поиск друзей
   else if (query.friendsS != null) {
      logconsole.info("friendsS");
      friendsSearchRequest(sock, conect, query);
   }
   //Запрос на запрос дружбы
   else if (query.friendRequest != null) {
      logconsole.info("friendRequest");
      friendRequest(sock, conect, query);
   }
   //Запрос на список диалогов
   else if (query.dialogsL != null) {
      logconsole.info("dialogsL");
      dialogsListRequest(sock, conect, query);
   }
   //Запрос на создание диалога
   else if (query.dialogC != null) {
      logconsole.info("dialogC");
      dialogCreateRequest(sock, conect, query);
   }
   //Запрос на отправку сообщения
   else if (query.sendMsg != null) {
      logconsole.info("sendMsg");
      sendMsgRequest(sock, conect, query);
   }
   //Запрос на список диалогов
   else if (query.lastNmsg != null) {
      logconsole.info("lastNmsg");
      lastNmsgRequest(sock, conect, query);
   }
   else if (query.userInfo != null) {
      logconsole.info("userInfo");
      getUserInfo(sock, conect, query);
   }
   else {
      logconsole.error("Erorr 2");
      var answer = {response: "Error 2"};
      writeAndDestroy(sock, JSON.stringify(answer));
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
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
      return;
   }
   //Ищем в базе логин/пасс
   conect.query('SELECT id from users where login="' + query.auth.login + '" and pass="' + md5(query.auth.pass) + '"', function (err, rows, fields) {
      //Нет ошибки
      if (err) {
         answer.response = "Error a4";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      if (rows.length != 1) {
         answer.response = "Error a3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      var token = crypto.randomBytes(32).toString('hex');
      //Проверка на то, что такое токен уже есть. todo: НАДО ЛИ?!?!
      conect.query('SELECT COUNT(*) from tokens where token="' + token + '"', function (err2, rows2) {
         //Проверка на ошибку
         if (err2) {
            answer.response = "Error a6";
            writeAndDestroy(sock, JSON.stringify(answer));
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
               if (!err3) {
                  conect.query('SELECT login, dialogs.id, dialogs.name from dialogs, users, userdialog where ' +
                     ' dialogId = dialogs.id and userId = users.id and ' +
                     ' exists(select * from userdialog where dialogId = dialogs.id and userId = ' + rows[0]['id'] + ') and not (users.id = ' + rows[0]['id'] + ')', function (err4, rows4) {
                        if (err4) {
                           //console.log(err4);
                           answer.response = "Error adl4";
                           writeAndDestroy(sock, JSON.stringify(answer));
                           sock.destroy();
                           return;
                        }
                        var dialogsList = [];
                        for (var i = 0; i < rows4.length; i++) {
                           dialogsList[i] = {
                              id: rows4[i]['id'],
                              name: rows4[i]['name'],
                              login: rows4[i]['login']
                           };
                        }
                        answer.response = "OK";
                        answer.dialogs = dialogsList;
                        writeAndDestroy(sock, JSON.stringify(answer));
                        sock.destroy();
                     });
}
else {
   answer.response = "Error a7";
   answer.token = "";
   writeAndDestroy(sock, JSON.stringify(answer));
   sock.destroy();
}
});
}
else {
   answer.response = "Error a8";
   answer.token = "";
   writeAndDestroy(sock, JSON.stringify(answer));
   sock.destroy();
}
});
      //
      //Ошибки ошибочки ошибулички
      /*if(i == 11) {
       answer.response = "Error a5";
       writeAndDestroy(sock, JSON.stringify(answer));
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
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
      return;
   }


   //Проверяем, етсь ли у нас такой уже в базе
   conect.query('SELECT * from users where login="' + query.reg.login + '"', function (err, rows) {
      //Ошибки нет
      if (err) {
         answer.response = "Error r4";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //console.log(rows[0]);
      //Если есть такой
      if (rows.length === 1) {
         //Ошибка
         answer.response = "Error r3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //Если нету такого
      //Вставляем в базу
      conect.query('INSERT INTO users (login, pass, firstName, lastName) VALUES ("' + query.reg.login + '", "' + md5(query.reg.pass) + '"' +
         ', "' + query.reg.firstName + '", "' + query.reg.lastName + '")', function (err2, rows2) {
         //ошибка
         if (err2) {
            answer.response = "Error r6";
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
            return;
         }

         var token = crypto.randomBytes(32).toString('hex');
         //Проверка на то, что такое токен уже есть. todo: НАДО ЛИ?!?!
         conect.query('SELECT COUNT(*) from tokens where token="' + token + '"', function (err4, rows4) {
            //Проверка на ошибку
            if (err4) {
               answer.response = "Error r9";
               writeAndDestroy(sock, JSON.stringify(answer));
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
                     writeAndDestroy(sock, JSON.stringify(answer));
                     sock.destroy();
                  }
                  else {
                     answer.response = "Error r8";
                     answer.token = "";
                     writeAndDestroy(sock, JSON.stringify(answer));
                     sock.destroy();
                  }
               });
            }
            else {
               answer.response = "Error r9";
               answer.token = "";
               writeAndDestroy(sock, JSON.stringify(answer));
               sock.destroy();
            }
         });
         /*}
          //Ошибки ошибочки ошибулички
          answer.response = "Error r5";
          writeAndDestroy(sock, JSON.stringify(answer));
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
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
      return;
   }
   //Выбираем из списка токенов нужный id
   conect.query('SELECT userId from tokens where token="' + query.friendsL.token + '"', function (err, rows) {
      var id;
      //Проверка на ошибку
      if (err) {
         answer.response = "Error fl2";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //Проверка, что такой токен вообще есть
      if (rows.length === 0) {
         answer.response = "Error fl3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      id = rows[0]['userId'];
      //Выбираем всех друзей из БД
      if (query.friendsL.req != null) {
         if (query.friendsL.req == 0) {
            conect.query('	select login, id, firstName, lastName from users join friendrequests on ' +
               '((idSender = id and idRecipient = ' + id + ') or (idRecipient = id and idSender = ' + id + ')) and reqStatus = 1'
               , function (err2, rows2) {
                  //console.log('1213');
                  if (err2) {
                     answer.response = "Error fl5";
                     writeAndDestroy(sock, JSON.stringify(answer));
                     sock.destroy();
                     return;
                  }
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
                  writeAndDestroy(sock, JSON.stringify(answer));
                  sock.destroy();

               });
}
else if (query.friendsL.req == 1) {
   conect.query('	select login, idSender as id, firstName, lastName from users join friendrequests on ' +
      'idRecipient = id and idSender = ' + id + ' and reqStatus = 0'
      , function (err2, rows2) {
         if (err2) {
            answer.response = "Error fl6";
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
            return;
         }
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
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();

      });
}
else if (query.friendsL.req == -1) {
   conect.query('select login, idSender as id, firstName, lastName from users join friendrequests on ' +
      'idSender = id and idRecipient = ' + id + ' and reqStatus = 0'
      , function (err2, rows2) {
         if (err2) {
            answer.response = "Error fl7";
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
            return;
         }
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
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();

      });
}
else {
   if (err2) {
      answer.response = "Error fl8";
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
      return;
   }
}
}
else
   conect.query('call proc_friendList(' + id + ')', function (err2, rows2) {
            //Проверка на ошибку
            if (err2) {
               answer.response = "Error fl4";
               writeAndDestroy(sock, JSON.stringify(answer));
               sock.destroy();
               return;
            }
            //Формируем JSON список друзей
            var friendsList = [];
            for (var i = 0; i < rows2[0].length; i++) {
               friendsList[i] = {
                  login: rows2[0][i]['login'],
                  id: rows2[0][i]['id'],
                  firstName: rows2[0][i]['firstName'],
                  lastName: rows2[0][i]['lastName'],
                  reqStatus: rows2[0][i]['req']
               };
            }
            answer.response = "OK";
            answer.friends = friendsList;
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
         });


});
}

function friendRequest(sock, conect, query) {
   var answer = {response: "", idRecipient: null};
   if (query.friendRequest.token == null || query.friendRequest.idRecipient == null) {
      answer.response = "Error fr1";
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
   }

   conect.query('SELECT userId from tokens where token="' + query.friendRequest.token + '"', function (err, rows) {
      var id;
      //Проверка на ошибку
      if (err) {
         answer.response = "Error fr2";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //Проверка, что такой токен вообще есть
      if (rows.length === 0) {
         answer.response = "Error fr3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      id = rows[0]['userId'];

      conect.query('SELECT reqStatus from friendrequests where idSender=' + query.friendRequest.idRecipient + ' and idRecipient=' + id, function (err3, rows3) {
         if (err) {
            answer.response = "Error fr5";
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
            return;
         }

         if (rows3.length != 0) {
            if (rows3[0]['reqStatus'] == 0) {
               conect.query('UPDATE friendrequests set reqStatus=1 where idSender=' + query.friendRequest.idRecipient + ' and idRecipient=' + id, function (err4) {
                  if (err4) {
                     answer.response = "Error fr6";
                     writeAndDestroy(sock, JSON.stringify(answer));
                     sock.destroy();
                     return;
                  }
                  answer.response = "OK";
                  answer.idRecipient = query.friendRequest.idRecipient;
                  writeAndDestroy(sock, JSON.stringify(answer));
                  sock.destroy();
               });
            }
            else {
               answer.response = "OK";
               answer.idRecipient = query.friendRequest.idRecipient;
               writeAndDestroy(sock, JSON.stringify(answer));
               sock.destroy();
            }
         }
         else {
            conect.query('INSERT INTO friendrequests (idSender, idRecipient) VALUES (' + id + ', ' + query.friendRequest.idRecipient + ')', function (err2, rows2) {
               if (err2) {
                  answer.response = "Error fr4";
                  writeAndDestroy(sock, JSON.stringify(answer));
                  sock.destroy();
               }
               else {
                  answer.response = "OK";
                  answer.idRecipient = query.friendRequest.idRecipient;
                  writeAndDestroy(sock, JSON.stringify(answer));
                  sock.destroy();
               }
            });
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
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
   }
   //
   conect.query('SELECT userId from tokens where token="' + query.dialogsL.token + '"', function (err, rows) {
      //Проверка на ошибку
      if (err) {
         answer.response = "Error dl2";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //Проверка, что такой токен вообще есть
      if (rows.length === 0) {
         answer.response = "Error dl3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      var id = rows[0]['userId'];
      //TODO: exists VS where VS join ?!??!?!
      //Собственно сама выборка диалогов
      conect.query('SELECT login, dialogs.id, dialogs.name, dialogs.lastUpdate, dialogs.lastLogin , dialogs.lastMsg from dialogs, users, userdialog where ' +
         ' dialogId = dialogs.id and userId = users.id and ' +
         ' exists(select * from userdialog where dialogId = dialogs.id and userId = ' + id + ') and not (users.id = ' +id + ')', function (err2, rows2) {
            if (err2){

               answer.response = "Error dl4";
               writeAndDestroy(sock, JSON.stringify(answer));
               sock.destroy();
               return;
            }
            var dialogsList = [];
            for (var i = 0; i < rows2.length; i++) {
               dialogsList[i] = {
                  id: rows2[i]['id'],
                  name: rows2[i]['name'],
                  login: rows2[i]['login'],
                  date: rows2[i]['lastUpdate'].toString().substring(0, 33),
                  lastLogin: rows2[i]['lastLogin'],
                  msg: rows2[i]['lastMsg']== null ? null : rows2[i]['lastMsg'].substring(0, 30)
               };
            }
            answer.response = "OK";
            answer.dialogs = dialogsList;
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();

         });

});
}

//Создание диалога
function dialogCreateRequest(sock, conect, query) {
   var answer = {response: "", dialog: null};
   if (query.dialogC.token == null || query.dialogC.idRecipient == null || query.dialogC.dialogName == null) {
      answer.response = "Error dc1";
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
      return;
   }

   conect.query('SELECT userId from tokens where token="' + query.dialogC.token + '"', function (err, rows) {
      //Проверка на ошибку
      if (err) {
         answer.response = "Error dc2";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //Проверка, что такой токен вообще есть
      if (rows.length === 0) {
         answer.response = "Error dc3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }

      //Проверка что userId и idRecipient друзья
      /*conect.query('SELECT reqStatus FROM friendRequests WHERE ' +
       'idSender=' + query.dialogC.idRecipient + ' and idRecipient=' + rows[0]['iserId'] + ') or (idRecipient=' + query.dialogC.idRecipient + ' and idSender=' + rows[0]['iserId'], function (err5, rows5) {
       if (err5) {
       answer.response = "Error dc7";
       writeAndDestroy(sock, JSON.stringify(answer));
       sock.destroy();
       return;
       }
       if (rows.length === 0 || rows[0]['reqStatus'] === 0) {
       answer.response = "Error dc8";
       writeAndDestroy(sock, JSON.stringify(answer));
       sock.destroy();
       return;
    }*/
      //Создаем диалог
      conect.query('INSERT INTO dialogs (name) values ("' + query.dialogC.dialogName + '")', function (err2, rows2) {
         if (err2) {
            answer.response = "Error dc4";
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
            return;
         }
         //Добавляем первого юзера
         conect.query('INSERT INTO userdialog (userId, dialogId) values (' + rows[0]['userId'] + ', ' + rows2.insertId + ')', function (err3) {
            if (err3) {
               answer.response = "Error dc5";
               writeAndDestroy(sock, JSON.stringify(answer));
               sock.destroy();
               return;
            }
            //Добавляем второго юзера
            conect.query('INSERT INTO userdialog (userId, dialogId) values (' + query.dialogC.idRecipient + ', ' + rows2.insertId + ')', function (err4) {
               if (err4) {
                  answer.response = "Error dc6";
                  writeAndDestroy(sock, JSON.stringify(answer));
                  sock.destroy();
                  return;
               }
               answer.response = "OK";
               answer.dialog = {id: rows2.insertId, name: query.dialogC.dialogName};
               writeAndDestroy(sock, JSON.stringify(answer));
               sock.destroy();
            });
         });

      });
});
   //});
}

function friendsSearchRequest(sock, conect, query) {
   var answer = {response: "", friends: []};
   if (query.friendsS.token == null || query.friendsS.searchPattern == null) {
      answer.response = "Error fs1";
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
      return;
   }
   //Выбираем из списка токенов нужный id
   conect.query('SELECT userId from tokens where token="' + query.friendsS.token + '"', function (err, rows) {
      var id;
      //Проверка на ошибку
      if (err) {
         answer.response = "Error fs2";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //Проверка, что такой токен вообще есть
      if (rows.length === 0) {
         answer.response = "Error fs3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      id = rows[0]['userId'];
      //Выбираем всех друзей из БД
      /*' +
       'and not exists(select * from friendrequests where reqStatus=1 and (idSender=id and idRecipient=' + id + ') ' +
          'or (idRecipient=id and idSender=' + id + ')))*/
   conect.query('call proc_friendSearch(' + id + ', "' + query.friendsS.searchPattern + '")', function (err2, rows2) {
         //Проверка на ошибку
         if (err2) {
            answer.response = "Error fs4";
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
            return;
         }
         //Формируем JSON список друзей
         var friendsList = [];
         for (var i = 0; i < rows2[0].length; i++) {
            friendsList[i] = {
               login: rows2[0][i]['login'],
               id: rows2[0][i]['id'],
               firstName: rows2[0][i]['firstName'],
               lastName: rows2[0][i]['lastName'],
               reqStatus: rows2[0][i]['req']
            };
         }
         answer.response = "OK";
         answer.friends = friendsList;
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
      });
});
}

function lastNmsgRequest(sock, conect, query) {
   var answer = {response: "", messages: []};
   if (query.lastNmsg.token == null || query.lastNmsg.dialogId == null || query.lastNmsg.messageCount == null) {
      answer.response = "Error ln1";
      //console.log("Error ln1");
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
      return;
   }


   conect.query('SELECT userId from tokens where token="' + query.lastNmsg.token + '"', function (err, rows) {
      var id;
      //Проверка на ошибку
      if (err) {
         //console.log("Error ln2");
         answer.response = "Error ln2";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //Проверка, что такой токен вообще есть
      if (rows.length === 0) {
         //console.log("Error ln3");
         answer.response = "Error ln3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      id = rows[0]['userId'];
      //Выбираем всех друзей из БД
      if (query.lastNmsg.dateStart != null) {
         conect.query('SELECT messages.id as id, senderId, login, msg, datatime FROM messages join users on senderId=users.id where dialogId=' +
            query.lastNmsg.dialogId + ' and datatime >  order by messages.id desc   limit ' + query.lastNmsg.messageCount, function (err2, rows2) {
            //Проверка на ошибку
            if (err2) {
               //console.log("Error ln4");
               answer.response = "Error ln4";
               writeAndDestroy(sock, JSON.stringify(answer));
               sock.destroy();
               return;
            }
            //Формируем JSON список друзей
            var messagesList = [];
            for (var i = 0; i < rows2.length; i++) {
               var datestr = rows2[i]['datatime'].toString().substring(0, 33);
               //console.log(typeof datestr);
               messagesList[i] = {
                  id: rows2[i]['id'],
                  login: rows2[i]['login'],
                  senderId: rows2[i]['senderId'],
                  text: rows2[i]['msg'],
                  datatime: datestr
               };
            }

            answer.response = "OK";
            answer.messages = messagesList;
            //console.log(answer);
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
         });
}
else if (query.lastNmsg.idStart != null) {
   conect.query('SELECT messages.id as id, senderId, login, msg, datatime FROM messages join users on senderId=users.id where dialogId=' +
      query.lastNmsg.dialogId + ' and messages.id < ' + query.lastNmsg.idStart + '  order by messages.id desc   limit ' + query.lastNmsg.messageCount, function (err2, rows2) {
            //Проверка на ошибку
            if (err2) {
               //console.log("Error ln4");
               answer.response = "Error ln4";
               writeAndDestroy(sock, JSON.stringify(answer));
               sock.destroy();
               return;
            }
            //Формируем JSON список друзей
            var messagesList = [];
            for (var i = 0; i < rows2.length; i++) {
               var datestr = rows2[i]['datatime'].toString().substring(0, 33);
               //console.log(typeof datestr);
               messagesList[i] = {
                  id: rows2[i]['id'],
                  login: rows2[i]['login'],
                  senderId: rows2[i]['senderId'],
                  text: rows2[i]['msg'],
                  datatime: datestr
               };
            }

            answer.response = "OK";
            answer.messages = messagesList;
            //console.log(answer);
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
         });
}
else {
   conect.query('SELECT messages.id as id, senderId, login, msg, datatime FROM messages join users on senderId=users.id where dialogId=' +
      query.lastNmsg.dialogId + ' order by messages.id desc   limit ' + query.lastNmsg.messageCount, function (err2, rows2) {
            //Проверка на ошибку
            if (err2) {
               console.log("Error ln4");
               answer.response = "Error ln4";
               writeAndDestroy(sock, JSON.stringify(answer));
               sock.destroy();
               return;
            }
            //Формируем JSON список друзей
            var messagesList = [];
            for (var i = 0; i < rows2.length; i++) {
               var datestr = rows2[i]['datatime'].toString().substring(0, 33);
               //console.log(typeof datestr);
               messagesList[i] = {
                  id: rows2[i]['id'],
                  login: rows2[i]['login'],
                  senderId: rows2[i]['senderId'],
                  text: rows2[i]['msg'],
                  datatime: datestr
               };
            }

            answer.response = "OK";
            answer.messages = messagesList;
            //console.log(answer);
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
         });
}
});

}

function sendMsgRequest(sock, conect, query) {
   var answer = {response: ""};
   if (query.sendMsg.token == null || query.sendMsg.dialogId == null || query.sendMsg.msg == null) {
      answer.response = "Error sm1";
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
      return;
   }

   conect.query('SELECT userId from tokens where token="' + query.sendMsg.token + '"', function (err, rows) {
      var id;
      //Проверка на ошибку
      if (err) {
         //console.log("Error sm2");
         answer.response = "Error sm2";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //Проверка, что такой токен вообще есть
      if (rows.length === 0) {
         //console.log("Error sm3");
         answer.response = "Error sm3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      id = rows[0]['userId'];
      //Выбираем всех друзей из БД
      conect.query('INSERT INTO messages (dialogId, senderId, msg) values (' + query.sendMsg.dialogId + ',' + id + ',"' + query.sendMsg.msg + '")', function (err2, rows2) {
         //Проверка на ошибку
         if (err2) {
            //console.log("Error sm4");
            answer.response = "Error sm4";
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
            return;
         }

         answer.response = "OK";
         //console.log(answer);
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
      });
   });

}

function getUserInfo(sock, conect, query) {
   var answer = {response: "", user: null};
   if (query.userInfo.token == null) {
      answer.response = "Error ui1";
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
      return;
   }

   conect.query('SELECT userId from tokens where token="' + query.sendMsg.token + '"', function (err, rows) {
      var id;
      //Проверка на ошибку
      if (err) {
         answer.response = "Error ui2";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //Проверка, что такой токен вообще есть
      if (rows.length === 0) {
         answer.response = "Error ui3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      id = rows[0]['userId'];
      //Выбираем всех друзей из БД
      conect.query('select login, firstName, lastName from users where id = ' + id, function (err2, rows2) {
         //Проверка на ошибку
         if (err2) {
            answer.response = "Error ui4";
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();
            return;
         }

         answer.response = "OK";
         answer.user = {id: id, username: rows2['login'], fName: rows2['firstName'], lName: rows2['lastName']}
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
      });
   });
}

function dialogsLPE(sock, conect, query) {

   var answer = {response: "", dialogs: []};
   //Проверка
   if (query.dialogsLPE.token == null) {
      answer.response = "Error dlp1";
      writeAndDestroy(sock, JSON.stringify(answer));
      sock.destroy();
   }
   //
   conect.query('SELECT userId from tokens where token="' + query.dialogsLPE.token + '"', function (err, rows) {
      //Проверка на ошибку
      if (err) {
         answer.response = "Error dlp2";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      //Проверка, что такой токен вообще есть
      if (rows.length === 0) {
         answer.response = "Error dlp3";
         writeAndDestroy(sock, JSON.stringify(answer));
         sock.destroy();
         return;
      }
      var id = rows[0]['userId'];
      //TODO: exists VS where VS join ?!??!?!
      //Собственно сама выборка диалогов
      conect.query('SELECT login, dialogs.id, dialogs.name, dialogs.lastUpdate, userdialog.publicKey1, userdialog.publicKey2 from dialogs, users, userdialog where ' +
         ' dialogId = dialogs.id and userId = users.id and ' +
         ' exists(select * from userdialog where dialogId = dialogs.id and userId = ' + id + ') and not (users.id = ' +id + ') and dialogs.protected = 1', function (err2, rows2) {
            if (err2){

               answer.response = "Error dlp4";
               writeAndDestroy(sock, JSON.stringify(answer));
               sock.destroy();
               return;
            }
            var dialogsList = [];
            for (var i = 0; i < rows2.length; i++) {
               dialogsList[i] = {
                  id: rows2[i]['id'],
                  name: rows2[i]['name'],
                  login: rows2[i]['login'],
                  publicKey1: rows2[i]['publicKey1'],
                  publicKey2: rows2[i]['publicKey2'],
                  date: rows2[i]['lastUpdate'].toString().substring(0, 33)
               };
            }
            answer.response = "OK";
            answer.dialogs = dialogsList;
            writeAndDestroy(sock, JSON.stringify(answer));
            sock.destroy();

         });

   });
}


exports.dataH = dataHandler;