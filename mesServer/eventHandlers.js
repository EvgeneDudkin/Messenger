	/**
 * Created by Kirill2 on 27.10.2015.
 */
var md5 = require('md5');

function dataHandler(data,sock, conect) {
    data = data.toString();
    for(var i = 0; i < data.length;i++) {
        if(data[i] == '{') {
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
        conect.query('SELECT COUNT(*) from users where login="' + query.auth.login + '" and pass="' + md5(query.auth.pass) + '"', function (err, rows, fields) {
            if (!err) {
                if (rows[0]['COUNT(*)'] === 1) {
                    sock.write("OK");
					sock.destroy();
                }
                else {
                    sock.write("Error 3");
                    sock.destroy();
                }
            }
            else {
                sock.write("Error 4");
                sock.destroy();
            }
        });
    }
    //Если регистрация
    else if (query.reg != null) {
        //Проверка на наличие логина и паса
        if(query.reg.login != null && query.reg.pass != null) {
            //Проверяем, етсь ли у нас такой уже в базе
        conect.query('SELECT COUNT(*) from users where login="' + query.reg.login + '"', function (err, rows, fields) {
            //Ошибки нет
            if (!err) {
                //Если есть такой
                if (rows[0]['COUNT(*)'] === 1) {
                    //Ошибка
                    sock.write("Error 3");
                    sock.destroy();
                }
                //Если нету такого
                else {
                    //Вставляем в базу
                    conect.query('INSERT INTO users (login, pass) VALUES ("'+ query.reg.login+ '", "' + md5(query.reg.pass) + '")', function (err, rows, fields) {
                        //Нет ошибки то все ок
                        if(!err) {
                            sock.write("OK");
							sock.destroy();
                        }
                        //Есть ошибка - ошибка
                        else {
                            sock.write("Error 6");
                            sock.destroy();
                        }
                    });
                }
            }
            //Есть ошибка - ошибка
            else {
                sock.write("Error 4");
                sock.destroy();
            }
        });

        }
        //Если чего то нет
        else {
            sock.write("Error 5");
            sock.destroy();
        }
    }
	else if (query.friends != null)  {
			conect.query('SELECT sender, recipient from friendsrequest where statuc=true and (sender="' + query.friends.login + '" OR recipient="'+query.friends.login + '") ', function (err, rows, fields) { 
				//console.log(rows[0]["sender"]);
				//console.log(rows.length);
				var friendsList = [];
				for(var i = 0; i < rows.length; i++) {
					if(rows[i]["sender"] == query.friends.login) {
						friendsList[i] = rows[i]["recipient"];
					}
					else {
						friendsList[i] = rows[i]["sender"];
					}
				}
				var qwe = {friends:friendsList}
				//var a = "" + qwe;
				sock.write(JSON.stringify(qwe));
				sock.destroy();
			});
			
	}
    else if (query.token != null) {
    }
    else {
        sock.write("Error 2");
        sock.destroy();
    }
}

exports.dataH = dataHandler;