package com.example.shin.myapplication;
import android.app.AlertDialog;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.PasswordAuthentication;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class GMailSender extends javax.mail.Authenticator
{
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;
    private MailVO vo;
    private AlertDialog ad;
    public GMailSender(String user, String password)
    {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected javax.mail.PasswordAuthentication getPasswordAuthentication()
    {
        return new javax.mail.PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(ArrayList<File> attachment,MailVO vo1)
            throws Exception
    {
        this.vo = vo1;
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
        MimeMessage message = new MimeMessage(session);
        //DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/html"));
        //message.setSender(new InternetAddress(new String(vo.getSender().getBytes(Charset.defaultCharset()), "UTF-8"),vo.getSender()));

        message.setSubject(vo.getSubject());
        message.setFrom(new InternetAddress("asdf@asdf.asdf",vo.getSender()));
        //message.setDataHandler(handler);
        if(attachment!=null){
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(vo.getContent());
            ArrayList<MimeBodyPart> mbp = new ArrayList<>();

            for(int i=0;i<attachment.size();i++) {
                MimeBodyPart mbp2 = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(attachment.get(i));
                mbp2.setDataHandler(new DataHandler(fds));
                mbp2.setFileName(MimeUtility.encodeText(vo.getFileName())+(i+1)+".jpg");
                mbp.add(mbp2);
            }

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            for(int i=0;i<attachment.size();i++) {
                mp.addBodyPart(mbp.get(i));
            }
            message.setContent(mp);
        }
        try{
        if (vo.getRecipent().indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(vo.getRecipent()));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(vo.getRecipent()));
        }catch (Exception e){
            e.printStackTrace();
        }
        Transport.send(message);
    }

    public class ByteArrayDataSource implements DataSource
    {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type)
        {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data)
        {
            super();
            this.data = data;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public String getContentType()
        {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException
        {
            return new ByteArrayInputStream(data);
        }

        public String getName()
        {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException
        {
            throw new IOException("Not Supported");
        }
    }
}

