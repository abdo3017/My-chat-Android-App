package com.example.mychat.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    String Email;
    String id;
    String ImageUrl;
    String Username;
    String Password;
    String Status;
    String LastMessage;
    String DateLastMessage;
    String MinuteLastMessage;
    String HourLastMessage;
    String ImageLastMessage;



    public User(User user) {
        this.id = user.id;
        this.DateLastMessage = user.DateLastMessage;
        this.HourLastMessage = user.HourLastMessage;
        this.MinuteLastMessage = user.MinuteLastMessage;
        Email = user.Email;
        ImageUrl = user.ImageUrl;
        Username = user.Username;
        Password = user.Password;
        Status = user.Status;
        LastMessage = user.LastMessage;
        ImageLastMessage =  user.ImageLastMessage;

    }

    public User(String email, String id, String imageUrl, String username, String password
            ,String status,String lastMessage
            ,String hourLastMessage
            , String dateLastMessage
            , String minuteLastMessage
            ,String imageLastMessage) {
        this.id = id;
        this.DateLastMessage = dateLastMessage;
        this.HourLastMessage = hourLastMessage;
        this.MinuteLastMessage = minuteLastMessage;
        Email = email;
        ImageUrl = imageUrl;
        Username = username;
        Password = password;
        Status = status;
        LastMessage = lastMessage;
        ImageLastMessage =  imageLastMessage;

    }


    public String getDateLastMessage() {
        return DateLastMessage;
    }

    public void setDateLastMessage(String dateLastMessage) {
        DateLastMessage = dateLastMessage;
    }

    public String getMinuteLastMessage() {
        return MinuteLastMessage;
    }

    public void setMinuteLastMessage(String minuteLastMessage) {
        MinuteLastMessage = minuteLastMessage;
    }

    public String getHourLastMessage() {
        return HourLastMessage;
    }

    public void setHourLastMessage(String hourLastMessage) {
        HourLastMessage = hourLastMessage;
    }

    public String getImageLastMessage() {
        return ImageLastMessage;
    }

    public void setImageLastMessage(String imageLastMessage) {
        ImageLastMessage = imageLastMessage;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public User() {
    }
    public void setStatus(String status) {
        Status = status;
    }

    public String getStatus() {
        return Status;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPassword() {
        return Password;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return Username;
    }

    public String getEmail() {
        return Email;
    }

    public String getImageUrl() {
        return ImageUrl;
    }
}
