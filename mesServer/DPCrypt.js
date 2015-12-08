/**
 * Created by Kirill2 on 07.12.2015.
 */
 /*var key = require('./cryptoKey');
 var key1 = key.key1;
 var key2 = key.key2;
 var key3 = key.key3;
 var key4 = key.key4;*/
 var log4js = require('./log4js');
 var logger = log4js.getLogger();

 var alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

 function decryptCl(key, salt) {
   this.key1 = (Math.sqrt(key) | 0) + 1 + salt%13;
   this.key2 = (Math.sqrt(key) | 0) + 1 + this.key1%13;
   this.key3 = [];
   this.key4 = [];
   this.key3[0] = (key*37*this.key1)%this.key2;
   for (var i = 1; i < this.key2; i++) {
      this.key3[i] = (this.key3[i-1]*37*this.key1+i)%this.key2;
   };
   this.key4[0] = (key*37*this.key2)%this.key1;
   for (var i = 1; i < this.key1; i++) {
      this.key4[i] = (this.key4[i-1]*37*this.key3[i%this.key2]+i)%this.key1;
   };
   /*logger.info("keys");
   logger.debug(this.key1);
   logger.debug(this.key2);
   logger.debug(this.key3);
   logger.debug(this.key4);*/

   this.decrypt = function (str, length) {
      var chartable;
      chartable = stringToCharArrayDec(str, this.key1, this.key2, this.key3, this.key4);
      chartable = permutationTableDecFirst(chartable, this.key1, this.key2, this.key3, this.key4);
      chartable = permutationTableDecSecond(chartable, this.key1, this.key2, this.key3, this.key4);

      return charTableToString(chartable, this.key1, this.key2, this.key3, this.key4).substring(0, length);
   };
}
function encryptCl(key, salt) {
   this.key1 = (Math.sqrt(key) | 0) + 1 + salt%13;
   this.key2 = (Math.sqrt(key) | 0) + 1 + this.key1%13;
   this.key3 = [];
   this.key4 = [];
   this.key3[0] = (key*37*this.key1)%this.key2;
   for (var i = 1; i < this.key2; i++) {
      this.key3[i] = (this.key3[i-1]*37*this.key1+i)%this.key2;
   };
   this.key4[0] = (key*37*this.key2)%this.key1;
   for (var i = 1; i < this.key1; i++) {
      this.key4[i] = (this.key4[i-1]*37*this.key3[i%this.key2]+i)%this.key1;
   };

   /*logger.info("keys");
   logger.debug(this.key1);
   logger.debug(this.key2);
   logger.debug(this.key3);
   logger.debug(this.key4);*/
   this.encrypt = function (str, length) {
      var chartable;
      chartable = stringToCharArrayEnc(str, this.key1, this.key2, this.key3, this.key4);
      chartable = permutationTableEncFirst(chartable, this.key1, this.key2, this.key3, this.key4);
      chartable = permutationTableEncSecond(chartable, this.key1, this.key2, this.key3, this.key4);

      return charTableToString(chartable, this.key1, this.key2, this.key3, this.key4);
   };
}

/*function getMyKeyBitch(key, salt) {
   key1 = (Math.sqrt(key) | 0) + 1 + salt%13;
   key2 = (Math.sqrt(key) | 0) + 1 + key1%13;
   key3[0] = (key*37*key2)%key1;
   for (var i = 1; i < key1; i++) {
      key3[i] = (key3[i-1]*37*key2)%key1;
   };
   key4[0] = (key*37*key1)%key2;
   for (var i = 1; i < key2; i++) {
      key4[i] = (key4[i-1]*37*key3[i%key2])%key2;
   };
}*/

function stringToCharArrayEnc(str, key1, key2, key3, key4) {
   var buffer = [];
   for(var i = 0; i < key1; i++) {
      buffer[i] = [];
   }

   for (var i = 0; i < str.length; i++) {
      if (i == key1 * key2) {
         break;
      }
      buffer[i % key1][(i / key1)|0] = str.charAt(i);
   }

   if (str.length < key1 * key2) {
      for (var i = str.length; i < key1 * key2; ++i) {
         buffer[i % key1][(i / key1)|0] = alphabet.charAt(Math.floor(Math.random() * alphabet.length))
         //logger.debug("rnd = " + buffer[i % key1][(i / key1)|0]);
         //possible.charAt(Math.floor(Math.random() * alphabet.length))
      }
   }
   //logger.info("buffer str to char enc:");
   //logger.debug("\n"+buffer);
   return buffer;
}

// Converts string to char array
// (Decryption)
function stringToCharArrayDec(str, key1, key2, key3, key4) {
   var buffer = [];
   for(var i = 0; i < key2; i++) {
      buffer[i] = [];
   }
   for (var i = 0; i < str.length; i++) {
      if (i == key2 * key1) {
         break;
      }
      buffer[i % key2][(i / key2)|0] = str.charAt(i);
   }
   /*
    if (str.length < key1 * key2) {
    for (var i = str.length; i < key1 * key2; ++i) {
    buffer[i % key2][key1 - 1] = ' ';
    }
    }
    */

    return buffer;
 }

// Change the order of columns
// (Encrypt)
function permutationTableEncFirst(chartable, key1, key2, key3, key4) {
   for (var i = 0; i < key1; ++i) {
      for (var j = 0; j < key2; ++j) {
         var tmp = chartable[i][j];
         chartable[i][j] = chartable[i][key3[j]];
         chartable[i][key3[j]] = tmp;
      }
   }
   return chartable;
}

// Change the order of rows
// (Encrypt)
function permutationTableEncSecond(chartable, key1, key2, key3, key4) {
   for (var i = 0; i < key1; ++i) {
      var tmp = chartable[i];
      chartable[i] = chartable[key4[i]];
      chartable[key4[i]] = tmp;
   }
   return chartable;
}

// Change the order of columns back
// (Decrypt)
function permutationTableDecFirst(chartable, key1, key2, key3, key4) {
   for (var i = key1 - 1; i >= 0; --i) {
      for (var j = 0; j < key2; ++j) {
         var tmp = chartable[j][i];
         chartable[j][i] = chartable[j][key4[i]];
         chartable[j][key4[i]] = tmp;
      }
   }
   return chartable;
}

// Change the order of rows back
// (Decrypt)
function permutationTableDecSecond(chartable, key1, key2, key3, key4) {
   for (var i = key2 - 1; i >= 0; --i) {
      //var tmp = chartable[i].join("");
      var tmp = chartable[i];
      chartable[i] = chartable[key3[i]];
      chartable[key3[i]] = tmp;
   }
   return chartable;
}



// Convert char table to string
function charTableToString(char_table, key1, key2, key3, key4) {
   var buffer = "";

   for (var i = 0; i < char_table.length; i++) {
      var tmp = char_table[i].join("");
      buffer += tmp;
   }
   return buffer;
}

// Encrypt string
/*function Encrypt(str) {
   var chartable;
   chartable = stringToCharArrayEnc(str);
   chartable = permutationTableEncFirst(chartable);
   chartable = permutationTableEncSecond(chartable);

   return charTableToString(chartable);
}


// Decrypt string
function Decrypt(str, length) {
   var chartable;
   chartable = stringToCharArrayDec(str);
   chartable = permutationTableDecFirst(chartable);
   chartable = permutationTableDecSecond(chartable);

   return charTableToString(chartable).substring(0, length);
}*/

exports.encrypt = encryptCl;
exports.decrypt = decryptCl;

