package com.example.admin.authui.model;

public class Chat {

    private String name;
    private String message;
    private String uid;
    private boolean important;
    private String uidUser;

    public Chat(){}

    public Chat(String uid, String name, String message, boolean isImportant, String userUid) {
        this.name = name;
        this.message = message;
        this.uid = uid;
        this.important = isImportant;
        this.uidUser = userUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean importante) {
        important = importante;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }
}
