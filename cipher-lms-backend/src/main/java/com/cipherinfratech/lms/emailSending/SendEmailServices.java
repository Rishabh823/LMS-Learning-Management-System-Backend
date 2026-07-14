package com.cipherinfratech.lms.emailSending;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Properties;

public class SendEmailServices {

    //Development
//	private static String from ="mayank.insystlabs@gmail.com";
//	private static String epass ="ictrqokefuawyrnx";

    //Production
    private static String from ="lms.cipherinfratech@gmail.com";
    private static String epass ="daxqkwgbtoxjgifv";


    public static Session emailDetails() {
        Properties properties = System.getProperties();
        System.out.println("Properties " + properties);

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.enable", true);
        properties.put("mail.smtp.auth", true);

        // to get the session object
        Session session = Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, epass);
            }
        });
        session.getDebug();

        return session;
    }

    public static void sendMail(String message, String subject, String to) {

        // to get the session object and mail properties
        Session session = emailDetails();

        // compose the messase

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(from);

            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

            mimeMessage.setSubject(subject);

            mimeMessage.setText(message);

            Transport.send(mimeMessage);

            // send the message
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public static void sendMailWithAttachment(String message, String subject, String to) {

        // to get the session object and mail properties
        Session session = emailDetails();

        // compose the messase



        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(from);

            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

            mimeMessage.setSubject(subject);

            mimeMessage.setText(message);

            Transport.send(mimeMessage);

            // send the message
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public static void sendMailToMultipleUserSepratly(String message, String subject, String to[]) {

        // to get the session object and mail properties
        Session session = emailDetails();

        // compose the messase

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(from);

            mimeMessage.setSubject(subject);

            mimeMessage.setText(message);
            for(String to1:to) {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to1));
                Transport.send(mimeMessage);
            }



            // send the message
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public static void sendMailToMultipleUserInOne(String message, String subject, ArrayList<String> to) {

        // to get the session object and mail properties
        Session session = emailDetails();

        // compose the messase
        MimeMessage mimeMessage = new MimeMessage(session);
        try {

            // add all to in InternetAddress
            InternetAddress[] address = new InternetAddress[to.size()];
            for (int i = 0; i < to.size(); i++) {
                address[i] = new InternetAddress(to.get(i));
            }
            mimeMessage.setFrom(from);

            mimeMessage.addRecipients(Message.RecipientType.TO, address);

            mimeMessage.setSubject(subject);

            mimeMessage.setText(message);

            Transport.send(mimeMessage);

            // send the message
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

}
