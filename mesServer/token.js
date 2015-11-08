/**
 * Created by Kirill2 on 08.11.2015.
 */
var md5 = require('crypto');
var TOKEN_LENGTH = 32;

exports.createToken = function(callback) {
    callback = crypto.randomBytes(TOKEN_LENGTH).toString('hex');
};