package com.chat;

/**
 * Created by user on 1/24/2018.
 */

class NotificationData {
    static final String TEXT="TEXT";

    private String title;
    private String image;
    private String text;
    private String sound;
    private int id;

    NotificationData(String title, String image, String text, String sound, int id){
        this.title=title;
        this.image=image;
        this.text=text;
        this.sound=sound;
        this.id=id;
    }

    String getImage(){
        return image;
    }

    void setImage(String image){
        this.image=image;
    }

    String getTitle(){
        return title;
    }

    void setTitle(String title){
        this.title=title;
    }

    String getText(){
        return text;
    }

    void setText(String text){
        this.text=text;
    }

    String getSound(){
        return sound;
    }

    void setSound(String sound){
        this.sound=sound;
    }

    int getId(){
        return id;
    }

    void setId(int id){
        this.id=id;
    }
}
