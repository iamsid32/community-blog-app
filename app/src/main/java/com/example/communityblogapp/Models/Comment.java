package com.example.communityblogapp.Models;

import com.google.firebase.database.ServerValue;

public class Comment {
    private String uid,uimg,uname,content,key;
    private Object timestamp;

    public Comment() {
    }

    public Comment(String uid, String uimg, String uname, String content) {
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.content = content;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Comment(String uid, String uimg, String uname, String content, Object timestamp) {
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
