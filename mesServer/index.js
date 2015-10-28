var net = require('net'),
    eventHandlers = require('./eventHandler');

var HOST = '192.168.0.101';
var PORT = 3000;
var sockets = [];

//Создаем экземпляр сервера
//Функция внутри - обработчик события 'connection' (то есть, когда к серверу осуществляется подключение)
var server = net.createServer(function(sock) {

    var bDATA = false;

    sockets.push(sock);

    //Отчет, о том что кто-то приконектился
    console.log('CONNECTED: ' + sock.remoteAddress +':'+ sock.remotePort);

    //Обработчик события 'data'. То есть клиент передает какие-то данные
    sock.on('data', function(data) {
        eventHandlers.dataH(data, sock);
        bDATA = true;
    });

    //Обработчик события 'close'. То есть, когда клиент закрыл соединение
    sock.on('close', function(data) {
        console.log('CLOSED: ' + sock.remoteAddress +' '+ sock.remotePort);
    });


    sock.on('error', function(data) {
        sock.destroy();
    });

    //Если после 10 сек после connection не пришла какая нибудь информация, удаляем socket
    sock.setTimeout(10000);

    sock.on('timeout', function(data) {
        if(bDATA == false) {
            sock.destroy();
        }
    });
});

//Начинаем прослушитьвать данный хост:порт
server.listen(PORT, HOST);


console.log('Server listening on ' + HOST +':'+ PORT);