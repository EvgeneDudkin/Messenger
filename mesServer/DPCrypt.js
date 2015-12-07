/**
 * Created by Kirill2 on 07.12.2015.
 */
var key = require('./cryptoKey');
var key1 = key.key1;
var key2 = key.key2;
var key3 = key.key3;

function stringToCharArrayEnc(str) {
   var buffer = new Array[key1];
   for(var i = 0; i < key1; i++) {
      buffer[i] = new Array[key2];
   }

   for (var i = 0; i < str.length(); i++) {
      if (i == key1 * key2) {
         break;
      }
      buffer[i % key1][i / key1] = str.charAt(i);
   }

   if (str.length() < key1 * key2) {
      for (var i = str.length(); i < key1 * key2; ++i) {
         buffer[i % key1][key2 - 1] = ' ';
      }
   }

   return buffer;
}

// Converts string to char array
// (Decryption)
function stringToCharArrayDec(str) {
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

   if (str.length < key1 * key2) {
      for (var i = str.length; i < key1 * key2; ++i) {
         buffer[i % key2][key1 - 1] = ' ';
      }
   }

   return buffer;
}

// Change the order of columns
// (Encrypt)
function permutationTableEnc(chartable) {
   for (var i = 0; i < chartable.length; ++i) {
      var row = chartable[i];
      for (var j = 0; j < key3.length; ++j) {
         var tmp = chartable[i][j];
         chartable[i][j] = chartable[i][key3[j]];
         chartable[i][key3[j]] = tmp;
      }
   }
   return chartable;
}

// Change the order of rows
// (Decrypt)
function permutationTableDec(chartable) {
   for (var i = key3.length - 1; i >= 0; --i) {
      var tmp = chartable[i].join("");
      //chartable[i] = chartable[key3[i]].join("").split('');
      chartable[i] = chartable[key3[i]];
      chartable[key3[i]] = tmp.split('');
   }
   return chartable;
}


// Convert char table to string
function charTableToString(char_table) {
   var buffer = "";

   for (var i = 0; i < char_table.length; i++) {
      var tmp = char_table[i].join("");
      console.log(tmp);
      buffer += tmp;
   }
   return buffer;
}

// Encrypt string
function Encrypt(str) {
   var chartable;
   chartable = stringToCharArrayEnc(str);
   chartable = permutationTableEnc(chartable);

   return charTableToString(chartable);
}


// Decrypt string
function Decrypt(str) {
   var chartable;
   //console.log(chartable);
   chartable = stringToCharArrayDec(str);
   //console.log(chartable);
   chartable = permutationTableDec(chartable);
   //console.log(chartable);

   var i = key1 - 1;
   while (chartable[key2 - 1][i % key1] == ' ') {
      chartable[key2 - 1][i % key1] = '\u0000';
      i--;
      if (i == -1)
         break;
   }
   //console.log(chartable);

   return charTableToString(chartable);
}

exports.encrypt = Encrypt;
exports.decrypt = Decrypt;

