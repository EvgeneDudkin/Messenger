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
        conect.query('INSERT INTO users (login, pass) VALUES ("' + query.reg.login + '", "' + md5(query.reg.pass) + '")', function (err2, rows2) {
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
        conect.query('select * from users where ' +
            'exists(select * from friendrequests where reqStatus=1 and (idSender=id and idRecipient=' + id + ') ' +
            'or (idRecipient=id and idSender=' + id + '))', function (err2, rows2) {
            //�������� �� ������
            if (err2) {
                answer.response = "Error fl4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //��������� JSON ������ ������
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

        //���� ��� ��, ����� ��������� ������. ������� friendrequests ���� ��������� ������� ����������,
        // �� ���� ���� � ������� ��� ���� 1, 4 (�� ���� user 1 ������ ������ �� ������ user 4)
        // �� �������� 1, 4 ��� 4, 1 ��� ������ � ��.(������ ������ ���� �����������)
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

        //TODO: exists VS just where VS join ?!??!?!
        //���������� ���� ������� ��������
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
    conect.query('SELECT userId from tokens where token="' + query.friendsL.token + '"', function (err, rows) {
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
        conect.query('select id, login, firstName, lastName from users where (login REGEXP "'+query.friendsS.searchPattern+'") = 1 ' +
            'and not exists(select * from friendrequests where reqStatus=1 and (idSender=id and idRecipient=' + id + ') ' +
            'or (idRecipient=id and idSender=' + id + ')))', function (err2, rows2) {
            //�������� �� ������
            if (err2) {
                answer.response = "Error fs4";
                sock.write(JSON.stringify(answer));
                sock.destroy();
                return;
            }
            //��������� JSON ������ ������
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