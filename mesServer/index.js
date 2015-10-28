var net = require('net'),
    eventHandlers = require('./eventHandler');

var HOST = '192.168.0.101';
var PORT = 3000;
var sockets = [];

//������� ��������� �������
//������� ������ - ���������� ������� 'connection' (�� ����, ����� � ������� �������������� �����������)
var server = net.createServer(function(sock) {

    var bDATA = false;

    sockets.push(sock);

    //�����, � ��� ��� ���-�� �������������
    console.log('CONNECTED: ' + sock.remoteAddress +':'+ sock.remotePort);

    //���������� ������� 'data'. �� ���� ������ �������� �����-�� ������
    sock.on('data', function(data) {
        eventHandlers.dataH(data, sock);
        bDATA = true;
    });

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
});

//�������� �������������� ������ ����:����
server.listen(PORT, HOST);


console.log('Server listening on ' + HOST +':'+ PORT);