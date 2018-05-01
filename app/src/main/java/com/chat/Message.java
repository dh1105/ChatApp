package com.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 2/13/2018.
 */

public class Message {

    private String fromName, message;
    private boolean fromMe;
    private String date;
    private String time;
    private String to;
    private String type;

    public Message() {
    }

    public Message(String fromName, String message, String date, String time, String to, String type) {
        this.fromName = fromName;
        this.message = message;
        this.date = date;
        this.time=time;
        this.to = to;
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean fromMe() {
        return fromMe;
    }

    public void setSelf(boolean fromMe) {
        this.fromMe = fromMe;
    }

    public void setDate(String date) { this.date = date; }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time=time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //    public String getDate() {
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
//
//        return sdf.format(date);
//
//    }
//
//    public String getTime() {
//
//        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
//
//        return sdf.format(date);
//
//    }
}
