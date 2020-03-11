package com.example.mychat.Model;

public class Message {



    private String Msg;
    private String ImageMessage;
    private String Hour;
    private String minute;
    private String Date;
    public static int DocName;
    private String Sender_ID;
    private String Reciver_ID;



    public String getDate() {
        return Date;
    }

    public String getHour() {
        return Hour;
    }

    public void setHour(String hour) {
        Hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public void setDate(String date) {
        Date = date;
    }
    public String getSender_ID() {
        return Sender_ID;
    }

    public void setSender_ID(String sender_ID) {
        Sender_ID = sender_ID;
    }

    public String getReciver_ID() {
        return Reciver_ID;
    }

    public void setReciver_ID(String reciver_ID) {
        Reciver_ID = reciver_ID;
    }

    public Message()
    {

    }

    public Message(String date,String hour,String minute,String msg
            , int docName, String Sender, String Reciver, String imageMessage) {

        Date = date;
        this.Hour = hour;
        this.minute = minute;
        Msg = msg;
        DocName = docName;
        Sender_ID = Sender;
        Reciver_ID = Reciver;
        ImageMessage = imageMessage;
    }


    public String getImageMessage() {
        return ImageMessage;
    }

    public void setImageMessage(String imageMessage) {
        ImageMessage = imageMessage;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public int getDocName() {
        return DocName;
    }

    public void setDocName(int docName) {
        DocName = docName;
    }
}
