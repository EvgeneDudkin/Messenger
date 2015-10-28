package com.example.egor.pigeonmes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Kirill2 on 28.10.2015.
 */
public class msgOkBox {
    /**
     * Меседжбокс с кнопкой 'ОК'
     * @param title Название бокса
     * @param msg Сообщение
     */
    public static void MsgOkBox(String title, String msg, Context context) {
        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}
