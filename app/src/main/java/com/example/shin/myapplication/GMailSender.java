package com.example.shin.myapplication;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMailSender extends javax.mail.Authenticator
{
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

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

    public synchronized void sendMail(String subject, String body, String sender, String recipients,File attachment)
            throws Exception
    {

//        MimeMessage message = new MimeMessage(session);
//        message.setContent("<h1>This is a test</h1>",
//                "text/html");
//        DataHandler handler = new DataHandler(
//                new ByteArrayDataSource(body.getBytes(), "text/html"));
//        message.setContent("<h1>This is a test</h1>",
//                "text/html");
//
//        message.setContent("<h1>This is a test</h1>"
//                        + "<img src=\""+mm+"\">"+ "<img src="+mm+">",
//                "text/html");
//                DataHandler handler = new DataHandler(
//                new ByteArrayDataSource(body.getBytes(), "text/plain"));
//
//        message.setSender(new InternetAddress(sender));
//        message.setSubject(subject);
//        if(attachment!=null){
//            MimeBodyPart mbp1 = new MimeBodyPart();
//            mbp1.setText(body);
//            MimeBodyPart mbp2 = new MimeBodyPart();
//            FileDataSource fds = new FileDataSource(attachment);
//            mbp2.setDataHandler(new DataHandler(fds));
//            mbp2.setFileName(fds.getName());
//            Multipart mp = new MimeMultipart();
//            mp.addBodyPart(mbp1);
//            mp.addBodyPart(mbp2);
//            message.setContent(mp);
//        }
//        //message.setDataHandler(handler);
//        if (recipients.indexOf(',') > 0)
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
//        else
//            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
//        try {
//            Transport.send(message);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);
        if(attachment!=null){
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText("asdf");
            MimeBodyPart mbp2 = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(attachment);
            mbp2.setDataHandler(new DataHandler(fds));
            mbp2.setFileName("test12");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            mp.addBodyPart(mbp2);
            message.setContent(mp);
        }
        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
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

