package epicheck.utils;

import javafx.application.Platform;
import javafx.concurrent.Task;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by jean on 10/27/16.
 */
public class MailUtils {

    private static String username = "jean.barriere@epitech.eu";
    private static String password = "owbl(s:?";
    private static String maillist;

    public static void send(ApiRequest.StringListener ret, String subject, String content, ArrayList<String> emails) {
        new Thread(() -> {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "outlook.office365.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            maillist = "";
            for(int i = 0; i < emails.size(); i++) {
                maillist += emails.get(i) + (i + 1 < emails.size() ? ", " : "");
            }

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(maillist));
                message.setSubject(subject);
                message.setText(content);
                Transport.send(message);
                ret.onComplete("email send");
            } catch (MessagingException e) {
                ret.onFailure("an error occured");
            }
        }).start();
    }
}
