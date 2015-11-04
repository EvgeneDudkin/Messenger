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


//Создаем экземпляр сервера
//Функция внутри - обработчик события 'connection' (то есть, когда к серверу осуществляется подключение)
var server = net.createServer(function(sock) {
    var bDATA = false;


    //Отчет, о том что кто-то приконектился
    console.log('CONNECTED: ' + sock.remoteAddress +':'+ sock.remotePort);
    var stop = null;
    //Обработчик события 'data'. То есть клиент передает какие-то данные
    sock.on('data', function(data) {
        dataH(data.toString(),sock,mconnection);

        bDATA = true;
    });
    //Обработчик события 'close'. То есть, когда клиент закрыл соединение
    sock.on('close', function(data) {
        console.log('CLOSED: ' + sock.remoteAddress +' '+ sock.remotePort);
    });


    sock.on('error', function(data) {
        console.log(8);
        sock.destroy();
    });

    //Если после 10 сек после connection не пришла какая нибудь информация, удаляем socket
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

//Начинаем прослушитьвать данный хост:порт
server.listen(PORT, HOST);


console.log('Server listening on ' + HOST +':'+ PORT);