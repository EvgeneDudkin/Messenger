/**
 * Created by Kirill2 on 27.10.2015.
 */
var crypto = require('crypto');
var md5 = require('md5'),
    getToken = require('./token.js').createToken;
var encrypt = require('./DPcrypt').encrypt;
var decrypt = require('./DPcrypt').decrypt;

function dataHandler(data, sock, conect) {
    data = data.toString();
    console.log(data);
    for (var i = 0; i < data.length; i++) {
        if (data[i] == '{') {
            data = data.substr(i);
            break;
        }
    }
    try {
        var query = JSON.parse(data);
        if(query.query != null) {
            console.log(query.query);
            var str = decrypt(query.query).trim();
            for (var i = str.length-1; i >= 0 ; i--) {
                if (str[i] == '}') {
                    str = str.substr(0, i+1);
                    break;
                }
            }
            console.log(str);
            query = JSON.parse(str);
        }
    }
    catch (err) {
        console.log(err);
        sock.write("Error 1");
        sock.destroy();
        return;
    }
    console.log(query);
    //�����������
    if (query.auth != null) {
        authRequest(sock, conect, query);
    }
    //���� �����������
    else if (query.reg != null) {
        regRequest(sock, conect, query);
    }
    //������ �� ������ ������
    else if (query.friendsL != null) {
        friendsListRequest(sock, conect, query);
    }
    //������ �� ����� ������
    else if (query.friendsS != null) {
        friendsSearchRequest(sock, conect, query);
    }
    //������ �� ������ ������
    else if (query.friendRequest != null) {
        friendRequest(sock, conect, query);
    }
    //������ �� ������ ��������
    else if (query.dialogsL != null) {
        dialogsListRequest(sock, conect, query);
    }
    //������ �� �������� �������
    else if (query.dialogC != null) {
        dialogCreateRequest(sock, conect, query);
    }
    //������ �� �������� ���������
    else if (query.sendMsg != null) {
        sendMsgRequest(sock, conect, query);
    }
    //������ �� ������ ��������
    else if (query.lastNmsg != null) {
        lastNmsgRequest(sock, conect, query);
    }
	else if (query.userInfo != null) {
		getUserInfo(sock, conect, query);
	}
    else {
        var answer = {response: "Error 2"};
        sock.write(JSON.stringify(answer));
        sock.destroy();
    }
}

/**
 * ������ �����������
 * @param sock �����
 * @param conect mysql connect
 * @param query ������
 */
function authRequest(sock, conect, query) {
    var answer = {response: "", token: "", dialogs: []};
    if (query.auth.login == null || query.auth.pass == null) {
        answer.response = "Error a8";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }
    //���� � ���� �����/����
    conect.query('SELECT id from users where login="' + query.auth.login + '" and pass="' + md5(query.auth.pass) + '"', function (err, rows, fields) {
        //��� ������
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
        //�������� �� ��, ��� ����� ����� ��� ����. todo: ���� ��?!?!
        conect.query('SELECT COUNT(*) from tokens where token="' + token + '"', function (err2, rows2) {
            //�������� �� ������
            if (err2) {
                answer.response = "Error a6";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //��������� ��� ������ ������ ���
            if (rows2[0]['COUNT(*)'] === 0) {
                answer.response = "OK";
                answer.token = token;
                //��������� ����� � ����
                conect.query('INSERT into tokens (userId, token) values(' + rows[0]['id'] + ', "' + token + '")', function (err3) {
                    //�������� ������
                    if (!err3) {
                        conect.query('SELECT login, dialogs.id, dialogs.name from dialogs, users, userdialog where '+
' dialogId = dialogs.id and userId = users.id and ' +
' exists(select * from userdialog where dialogId = dialogs.id and userId = '+rows[0]['id']+') and not (users.id = '+rows[0]['id']+')', function (err4, rows4) {
                            if (err4) {
								console.log(err4);
                                answer.response = "Error adl4";
                                sock.write(JSON.stringify(answer));
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
        //������ �������� ����������
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
    //�������� �� ������� ������ � ����
    if (query.reg.login == null || query.reg.pass == null) {
        answer.response = "Error r5";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }


    //���������, ���� �� � ��� ����� ��� � ����
    conect.query('SELECT * from users where login="' + query.reg.login + '"', function (err, rows) {
        //������ ���
        if (err) {
            answer.response = "Error r4";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //console.log(rows[0]);
        //���� ���� �����
        if (rows.length === 1) {
            //������
            answer.response = "Error r3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //���� ���� ������
        //��������� � ����
        conect.query('INSERT INTO users (login, pass, firstName, lastName) VALUES ("' + query.reg.login + '", "' + md5(query.reg.pass) + '"' +
            ', "' + query.reg.firstName + '", "' + query.reg.lastName + '")', function (err2, rows2) {
            //������
            if (err2) {
                answer.response = "Error r6";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }

            var token = crypto.randomBytes(32).toString('hex');
            //�������� �� ��, ��� ����� ����� ��� ����. todo: ���� ��?!?!
            conect.query('SELECT COUNT(*) from tokens where token="' + token + '"', function (err4, rows4) {
                //�������� �� ������
                if (err4) {
                    answer.response = "Error r9";
                    sock.write(JSON.stringify(answer));
                    sock.destroy();
                    return;
                }
                //��������� ��� ������ ������ ���
                if (rows4[0]['COUNT(*)'] === 0) {
                    answer.response = "OK";
                    answer.token = token;
                    //��������� ����� � ����
                    conect.query('INSERT into tokens (userId, token) values (' + rows2.insertId + ', "' + token + '")', function (err5) {
                        //�������� ������
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
             //������ �������� ����������
             answer.response = "Error r5";
             sock.write(JSON.stringify(answer));
             sock.destroy();
             return;*/

        });
    });
}


//������ �� ������ ������
function friendsListRequest(sock, conect, query) {
    var answer = {response: "", friends: []};
    //� ������� ������ ���� �����
    if (query.friendsL.token == null) {
        answer.response = "Error fl1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }
    //�������� �� ������ ������� ������ id
    conect.query('SELECT userId from tokens where token="' + query.friendsL.token + '"', function (err, rows) {
        var id;
        //�������� �� ������
        if (err) {
            answer.response = "Error fl2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //��������, ��� ����� ����� ������ ����
        if (rows.length === 0) {
            answer.response = "Error fl3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        id = rows[0]['userId'];
        //�������� ���� ������ �� ��
        conect.query('call proc_friendList('+id+')', function (err2, rows2) {
            //�������� �� ������
            if (err2) {
                answer.response = "Error fl4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //��������� JSON ������ ������
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

    conect.query('SELECT userId from tokens where token="' + query.friendRequest.token + '"', function (err, rows) {
        var id;
        //�������� �� ������
        if (err) {
            answer.response = "Error fr2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //��������, ��� ����� ����� ������ ����
        if (rows.length === 0) {
            answer.response = "Error fr3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        id = rows[0]['userId'];

        conect.query('SELECT reqStatus from friendrequests where idSender=' + query.friendRequest.idRecipient + ' and idRecipient=' + id, function (err3, rows3) {
            if (err) {
                answer.response = "Error fr5";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }

            if (rows3.length != 0) {
                if (rows3[0]['reqStatus'] == 0) {
                    conect.query('UPDATE friendrequests set reqStatus=1 where idSender=' + query.friendRequest.idRecipient + ' and idRecipient=' + id, function (err4) {
                        if(err4) {
                            answer.response = "Error fr6";
                            sock.write(JSON.stringify(answer));
                            sock.destroy();
                            return;
                        }
                        answer.response = "OK";
                        answer.idRecipient = query.friendRequest.idRecipient;
                        sock.write(JSON.stringify(answer));
                        sock.destroy();
                    });
                }
                else {
                    answer.response = "OK";
                    answer.idRecipient = query.friendRequest.idRecipient;
                    sock.write(JSON.stringify(answer));
                    sock.destroy();
                }
            }
            else {
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
            }
        });


    });
}

//������ ��������
function dialogsListRequest(sock, conect, query) {
    var answer = {response: "", dialogs: []};
    //��������
    if (query.dialogsL.token == null) {
        answer.response = "Error dl1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
    }
    //
    conect.query('SELECT userId from tokens where token="' + query.friendsL.token + '"', function (err, rows) {
        //�������� �� ������
        if (err) {
            answer.response = "Error dl2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //��������, ��� ����� ����� ������ ����
        if (rows.length === 0) {
            answer.response = "Error dl3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }

        //TODO: exists VS where VS join ?!??!?!
        //���������� ���� ������� ��������
        conect.query('SELECT login, dialogs.id, dialogs.name from dialogs, users, userdialog where '+
'dialogId = dialogs.id and userId = users.id and' +
'exists(select * from userdialog where dialogId = dialogs.id and userId = '+id+') and not (users.id = '+id+')', function (err2, rows2) {
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
                    name: rows2[i]['name'],
					login: rows2[i]['login']
                };
            }
            answer.response = "OK";
            answer.dialogs = dialogsList;
            sock.write(JSON.stringify(answer));
            sock.destroy();
					
					});
            
        });
    }

//�������� �������
function dialogCreateRequest(sock, conect, query) {
    var answer = {response: "", dialog: null};
    if (query.dialogC.token == null || query.dialogC.idRecipient == null || query.dialogC.dialogName == null) {
        answer.response = "Error dc1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }

    conect.query('SELECT userId from tokens where token="' + query.friendsL.token + '"', function (err, rows) {
        //�������� �� ������
        if (err) {
            answer.response = "Error dc2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //��������, ��� ����� ����� ������ ����
        if (rows.length === 0) {
            answer.response = "Error dc3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }

        //�������� ��� userId � idRecipient ������
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
            //������� ������
            conect.query('INSERT INTO dialogs (name) values ("' + query.dialogC.dialogName + '")', function (err2, rows2) {
                if (err2) {
                    answer.response = "Error dc4";
                    sock.write(JSON.stringify(answer));
                    sock.destroy();
                    return;
                }
                //��������� ������� �����
                conect.query('INSERT INTO userdialog (userId, dialogId) values (' + rows[0]['userId'] + ', ' + rows2.insertId + ')', function (err3) {
                    if (err3) {
                        answer.response = "Error dc5";
                        sock.write(JSON.stringify(answer));
                        sock.destroy();
                        return;
                    }
                    //��������� ������� �����
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
    //�������� �� ������ ������� ������ id
    conect.query('SELECT userId from tokens where token="' + query.friendsS.token + '"', function (err, rows) {
        var id;
        //�������� �� ������
        if (err) {
            answer.response = "Error fs2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //��������, ��� ����� ����� ������ ����
        if (rows.length === 0) {
            answer.response = "Error fs3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        id = rows[0]['userId'];
        //�������� ���� ������ �� ��
		/*' +
            'and not exists(select * from friendrequests where reqStatus=1 and (idSender=id and idRecipient=' + id + ') ' +
            'or (idRecipient=id and idSender=' + id + ')))*/
        conect.query('call proc_friendSearch('+id+', "'+query.friendsS.searchPattern+'")', function (err2, rows2) {
            //�������� �� ������
            if (err2) {
                answer.response = "Error fs4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //��������� JSON ������ ������
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
            sock.write(JSON.stringify(answer));
            sock.destroy();
        });
    });
}

function lastNmsgRequest(sock, conect, query) {
    var answer = {response: "", messages: []};
    if (query.lastNmsg.token == null || query.lastNmsg.dialogId == null || query.lastNmsg.messageCount == null) {
        answer.response = "Error ln1";
        console.log("Error ln1");
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }

	

    conect.query('SELECT userId from tokens where token="' + query.lastNmsg.token + '"', function (err, rows) {
        var id;
        //�������� �� ������
        if (err) {
            console.log("Error ln2");
            answer.response = "Error ln2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //��������, ��� ����� ����� ������ ����
        if (rows.length === 0) {
            console.log("Error ln3");
            answer.response = "Error ln3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        id = rows[0]['userId'];
        //�������� ���� ������ �� ��
		if(query.lastNmsg.dateStart != null) {
        conect.query('SELECT messages.id as id, senderId, login, msg, datatime FROM messages join users on senderId=users.id where dialogId=' +
            query.lastNmsg.dialogId + ' and datatime >  order by messages.id desc   limit ' + query.lastNmsg.messageCount, function (err2, rows2) {
            //�������� �� ������
            if (err2) {
                console.log("Error ln4");
                answer.response = "Error ln4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //��������� JSON ������ ������
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
            console.log(answer);
            sock.write(JSON.stringify(answer));
            sock.destroy();
        });
		}
		else if(query.lastNmsg.idStart != null) {
          conect.query('SELECT messages.id as id, senderId, login, msg, datatime FROM messages join users on senderId=users.id where dialogId=' +
             query.lastNmsg.dialogId + ' and messages.id < '+query.lastNmsg.idStart+'  order by messages.id desc   limit ' + query.lastNmsg.messageCount, function (err2, rows2) {
              //�������� �� ������
              if (err2) {
                  console.log("Error ln4");
                  answer.response = "Error ln4";
                  sock.write(JSON.stringify(answer));
                  sock.destroy();
                  return;
              }
              //��������� JSON ������ ������
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
              console.log(answer);
              sock.write(JSON.stringify(answer));
              sock.destroy();
          });
		}
		else {
        conect.query('SELECT messages.id as id, senderId, login, msg, datatime FROM messages join users on senderId=users.id where dialogId=' +
            query.lastNmsg.dialogId + ' order by messages.id desc   limit ' + query.lastNmsg.messageCount, function (err2, rows2) {
            //�������� �� ������
            if (err2) {
                console.log("Error ln4");
                answer.response = "Error ln4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //��������� JSON ������ ������
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
            console.log(answer);
            sock.write(JSON.stringify(answer));
            sock.destroy();
        });
		}
    });

}

function sendMsgRequest(sock, conect, query) {
    var answer = {response: ""};
    if (query.sendMsg.token == null || query.sendMsg.dialogId == null || query.sendMsg.msg == null) {
        answer.response = "Error sm1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
    }

    conect.query('SELECT userId from tokens where token="' + query.sendMsg.token + '"', function (err, rows) {
        var id;
        //�������� �� ������
        if (err) {
            console.log("Error sm2");
            answer.response = "Error sm2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //��������, ��� ����� ����� ������ ����
        if (rows.length === 0) {
            console.log("Error sm3");
            answer.response = "Error sm3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        id = rows[0]['userId'];
        //�������� ���� ������ �� ��
        conect.query('INSERT INTO messages (dialogId, senderId, msg) values ('+query.sendMsg.dialogId+','+id+',"'+query.sendMsg.msg+'")', function (err2, rows2) {
            //�������� �� ������
            if (err2) {
                console.log("Error sm4");
                answer.response = "Error sm4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }

            answer.response = "OK";
            console.log(answer);
            sock.write(JSON.stringify(answer));
            sock.destroy();
        });
    });

}

function getUserInfo(sock, conect, query) {
	var answer = {response: "", user: null};
	if(query.userInfo.token == null) {
        answer.response = "Error ui1";
        sock.write(JSON.stringify(answer));
        sock.destroy();
        return;
	}
	
	conect.query('SELECT userId from tokens where token="' + query.sendMsg.token + '"', function (err, rows) {
        var id;
        //�������� �� ������
        if (err) {
            answer.response = "Error ui2";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        //��������, ��� ����� ����� ������ ����
        if (rows.length === 0) {
            answer.response = "Error ui3";
            sock.write(JSON.stringify(answer));
            sock.destroy();
            return;
        }
        id = rows[0]['userId'];
        //�������� ���� ������ �� ��
        conect.query('select login, firstName, lastName from users where id = ' + id, function (err2, rows2) {
            //�������� �� ������
            if (err2) {
                answer.response = "Error ui4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }

            answer.response = "OK";
			answer.user = {id: id, username: rows2['login'], fName: rows2['firstName'], lName: rows2['lastName']}
            sock.write(JSON.stringify(answer));
            sock.destroy();
        });
    });
}

exports.dataH = dataHandler;