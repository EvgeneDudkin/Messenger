package com.example.messengerpigeon.Messages_Item;

/**
 * Created by Пользователь on 20.11.2015.
 */
public class Messages_Item {
    private String NameFriend;
    private int resIcon;
    private String messDialog;

    public Messages_Item(String title, int resIcon, String dialog) {
        super();
        this.NameFriend=title;
        this.messDialog=dialog;
        this.resIcon=resIcon;
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
    public void setMessDialog(String dial)
    {
        this.messDialog=dial;
    }

}