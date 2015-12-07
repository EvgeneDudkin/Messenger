var net = require('net'),
    eventHandlers = require('./eventHandlers'),
    mysqlConnection = require('./mysqlConnection');

var mconnection = mysqlConnection.connection;
var dataH = eventHandlers.dataH;


var HOST = '172.20.205.88';
var PORT = 3001;

//������� ��������� �������
//������� ������ - ���������� ������� 'connection' (�� ����, ����� � ������� �������������� �����������)
var server = net.createServer(function(sock) {
    var bDATA = false;
    var stop = null;
    var length = 0;
    var str = "";

    //�����, � ��� ��� ���-�� �������������
    console.log('CONNECTED: ' + sock.remoteAddress +':'+ sock.remotePort);
    //���������� ������ 'data'. �� ���� ������ �������� �����-�� ������
    sock.on('data', function(data) {
        if(bDATA) {
            console.log(data.toString());
            str += data.toString();
            if(str.length >= length) {
                dataH(str,sock,mconnection);
            }
        }
        else {
            console.log(data);
            length = parseInt(data.toString());
            console.log(length);
        }
        bDATA = true;
    });
    /*sock.on('data', function(data) {
                dataH(data.toString(),sock,mconnection);
        bDATA = true;
    });*/

    //���������� ������� 'close'. �� ����, ����� ������ ������ ����������
    sock.on('close', function(data) {
        console.log('CLOSED: ' + sock.remoteAddress +' '+ sock.remotePort);
    });


    sock.on('error', function(data) {
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