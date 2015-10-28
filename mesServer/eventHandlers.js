/**
 * Created by Kirill2 on 27.10.2015.
 */

function dataHandler(data, sock) {

    var query = JSON.parse(data);
    if(query.auth != null) {
        sock.write();
    }
    else if(query.token != null) {

    }
    else {
        sock.write("Нет аутентификации и токена :(");
        sock.destroy();
    }
}

exports.dataH = dataHandler;