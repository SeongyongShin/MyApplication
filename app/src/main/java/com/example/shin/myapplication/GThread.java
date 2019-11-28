package com.example.shin.myapplication;

import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class GThread extends Thread {
    private String id;
    private String pw;
    private ArrayList<File> imgFile;
    private MailVO vo;
    private String msg = "";
    private boolean isTrue = false;
    public GThread(String id, String pw,ArrayList<File> imgFile, MailVO vo) {
        this.id = id;
        this.pw = pw;
        this.imgFile = imgFile;
        this.vo = vo;
    }

    @Override
    public void run() {
        synchronized (this) {
            GMailSender gMailSender = new GMailSender(id, pw);
            try {
                gMailSender.sendMail(imgFile, vo);
            } catch (SendFailedException e) {
                msg = "받는 사람 이메일 형식이 잘못되었습니다.";
            } catch (MessagingException e) {
                msg = "인터넷 연결을 확인해주십시오";
            } catch (Exception e) {
                msg = "사진을 먼저 찍어주세요.";
            }
            isTrue = true;
            notify();
        }
    }
    public boolean isTrue(){
        return isTrue;
    }
    public String getMsg(){
        return msg;
    }
}
