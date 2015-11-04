var net = require('net'),
    eventHandlers = require('./eventHandlers'),
    mysqlConnection = require('./mysqlConnection');

var mconnection = mysqlConnection.connection;
var dataH = eventHandlers.dataH;


var HOST = '192.168.0.101';
var PORT = 3000;

mconnection.query('SELECT COUNT(*) from users where login="' + 'qwerty"', function(err, rows, fields) {
    if (!err)
        console.log('The solution is: ', rows[0]['COUNT(*)']);
    else
        console.log('Error while performing Query.');
});


//������� ��������� �������
//������� ������ - ���������� ������� 'connection' (�� ����, ����� � ������� �������������� �����������)
var server = net.createServer(function(sock) {
    var bDATA = false;


    //�����, � ��� ��� ���-�� �������������
    console.log('CONNECTED: ' + sock.remoteAddress +':'+ sock.remotePort);
    var stop = null;
    //���������� ������� 'data'. �� ���� ������ �������� �����-�� ������
    sock.on('data', function(data) {
        dataH(data.toString(),sock,mconnection);

        bDATA = true;
    });
    //���������� ������� 'close'. �� ����, ����� ������ ������ ����������
    sock.on('close', function(data) {
        console.log('CLOSED: ' + sock.remoteAddress +' '+ sock.remotePort);
    });


    sock.on('error', function(data) {
        console.log(8);
        sock.destroy();
    });

    //���� ����� 10 ��� ����� connection �� ������ ����� ������ ����������, ������� socket
    sock.setTimeout(10000);

    sock.on('timeout', function(data) {
        if(bDATA == false) {
            sock.destroy();
        }
    });

    function writeSt(msg) {
        sock.write(msg);
    }
});

//�������� �������������� ������ ����:����
server.listen(PORT, HOST);


console.log('Server listening on ' + HOST +':'+ PORT);