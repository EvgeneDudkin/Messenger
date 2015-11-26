package com.example.messengerpigeon.History;

/**
 * Created by Пользователь on 25.11.2015.
 */
public class History_Item {
    private String name_sender;
    private String message_one;
    private String time_send;

    public History_Item(String name_sender,String mess,String time) {
        super();
        this.name_sender=name_sender;
        this.message_one=mess;
        this.time_send=time;
    }
    public String getName_sender(){
        return name_sender;
    }
    public void  setName_sender(String name) {this.name_sender = name;}

    public String getMessage_one(){
        return message_one;
    }
    public void  setMessage_one(String mess) {this.message_one = mess;}

    public String getTime_send()
    {
        return  time_send;
    }
    public void setTime_send(String time)
    {
        this.time_send=time;
    }

}
