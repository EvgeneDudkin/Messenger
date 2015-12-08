var net = require('net'),
eventHandlers = require('./eventHandlers'),
mysqlConnection = require('./mysqlConnection');
var log4js = require('./log4js');
var logger = log4js.getLogger();
var mconnection = mysqlConnection.connection;
var dataH = eventHandlers.dataH;

var HOST = '192.168.0.101';
var PORT = 3000;

//������� ��������� �������
//������� ������ - ���������� ������� 'connection' (�� ����, ����� � ������� �������������� �����������)
var server = net.createServer(function(sock) {
    var bDATA = false;
    var stop = null;
    var length = 0;
    var str = "";
    //�����, � ��� ��� ���-�� �������������
    //console.log('CONNECTED: ' + sock.remoteAddress +':'+ sock.remotePort);
    logger.trace();
    logger.trace();
    logger.trace("=================================");
    logger.trace('CONNECTED: ' + sock.remoteAddress +':'+ sock.remotePort);
    logger.trace("=================================");
    //���������� ������ 'data'. �� ���� ������ �������� �����-�� ������
    sock.on('data', function(data) {
        if(bDATA) {
            //logger.debug(data.toString());
            str += data.toString();
            if(str.length >= length) {
                dataH(str,sock,mconnection);
            }
        }
        else {
            logger.debug(data);
            length = parseInt(data.toString());

            logger.debug(length);
            if(isNaN(length)) {
                logger.error("NAN");
                sock.destroy();
            }
        }
        bDATA = true;
    });
    /*sock.on('data', function(data) {
        dataH(data.toString(),sock,mconnection);
        bDATA = true;
    });*/

    //���������� ������� 'close'. �� ����, ����� ������ ������ ����������
    sock.on('close', function(data) {
        logger.trace("=================================");
        logger.trace('CLOSED: ' + sock.remoteAddress +' '+ sock.remotePort);
        logger.trace("=================================");
        logger.trace();
        logger.trace();
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


logger.trace('Server listening on ' + HOST +':'+ PORT);