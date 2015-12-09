package com.example.messengerpigeon.Messages_Item;

import java.util.Date;

/**
 * Created by Пользователь on 20.11.2015.
 */
public class Messages_Item {
    private String NameFriend;
    private int resIcon;
    private String messDialog;
    private String date;

    public Messages_Item(String title, int resIcon, String dialog) {
        super();
        this.NameFriend=title;
        this.messDialog=dialog;
        this.resIcon=resIcon;
    }
    public Messages_Item(String title, int resIcon, String dialog, String _date) {
        super();
        this.NameFriend=title;
        this.messDialog=dialog;
        this.resIcon=resIcon;
        this.date=_date;
    }

    public String getNameFriend(){
        return NameFriend;
    }

    public void  setNameFriend(String title) {
        this.NameFriend = title;
    }

    public int getResIcon(){
        return  resIcon;
    }

    public void setResIcon(int resIcon){
        this.resIcon=resIcon;
    }

    public String getMessDialog()
    {
        return  messDialog;
    }

    public String getDate(){
        return date;
    }
    public void setMessDialog(String dial)
    {
        this.messDialog=dial;
    }

}
