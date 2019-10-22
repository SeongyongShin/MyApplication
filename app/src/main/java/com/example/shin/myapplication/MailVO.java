package com.example.shin.myapplication;

public class MailVO {
    private String sender = "";
    private String recipent = "";
    private String subject = "";
    private String content = "";
    private String fileName = "";
    public MailVO(){}
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipent() {
        return recipent;
    }

    public void setRecipent(String recipent) {
        this.recipent = recipent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAll() {
        return getSender()+"\n"+getRecipent()+"\n"+getSubject()+"\n"+getFileName()+"\n"+getContent()+"\n";
    }
}
