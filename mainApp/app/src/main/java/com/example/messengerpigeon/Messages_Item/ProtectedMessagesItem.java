package com.example.messengerpigeon.Messages_Item;

/**
 * Created by Пользователь on 09.12.2015.
 */
public class ProtectedMessagesItem {
    private String NameFriend;
    private int resIcon;
    private String messDialog;
    private  String Status;

    public ProtectedMessagesItem(String title, int resIcon, String dialog, String status) {
        super();
        this.NameFriend=title;
        this.messDialog=dialog;
        this.resIcon=resIcon;
        this.Status=status;
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
    public String getStatus()
    {
        return Status;
    }
    public void setStatus(String dial)
    {
        this.Status=dial;
    }
}
