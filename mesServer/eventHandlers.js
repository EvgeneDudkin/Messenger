/**
 * Created by Kirill2 on 27.10.2015.
 */
var md5 = require('md5');

function dataHandler(data,sock, conect) {
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
        conect.query('SELECT COUNT(*) from users where login="' + query.auth.login + '" and pass="' + md5(query.auth.pass) + '"', function (err, rows, fields) {
            if (!err) {
                if (rows[0]['COUNT(*)'] === 1) {
                    sock.write("OK");
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
    //���� �����������
    else if (query.reg != null) {
        //�������� �� ������� ������ � ����
        if (query.reg.login != null && query.reg.pass != null) {
            //���������, ���� �� � ��� ����� ��� � ����
            conect.query('SELECT COUNT(*) from users where login="' + query.reg.login + '"', function (err, rows, fields) {
                //������ ���
                if (!err) {
                    //���� ���� �����
                    if (rows[0]['COUNT(*)'] === 1) {
                        //������
                        sock.write("Error 3");
                        sock.destroy();
                    }
                    //���� ���� ������
                    else {
                        //��������� � ����
                        conect.query('INSERT INTO users (login, pass) VALUES ("' + query.reg.login + '", "' + md5(query.reg.pass) + '")', function (err, rows, fields) {
                            //��� ������ �� ��� ��
                            if (!err) {
                                sock.write("OK");
                            }
                            //���� ������ - ������
                            else {
                                sock.write("Error 6");
                                sock.destroy();
                            }
                        });
                    }
                }
                //���� ������ - ������
                else {
                    sock.write("Error 4");
                    sock.destroy();
                }
            });

        }
        //���� ���� �� ���
        else {
            sock.write("Error 5");
            sock.destroy();
        }
    }
    else if (query.token != null) {
    }
    else {
        sock.write("Error 2");
        sock.destroy();
    }
}

exports.dataH = dataHandler;