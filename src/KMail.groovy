/**
 * Created by carlos on 7/9/16.
 */

// https://mvnrepository.com/artifact/javax.mail/mail
//@Grapes(
//        @Grab(group='javax.mail', module='mail', version='1.4.7')
//)

import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class KMail {

    def user
    def password
    def Dest
    def Subject
    def Msg
    def Resultado

    Properties mailServerProperties;
    Session getMailSession;
    MimeMessage generateMailMessage;


    boolean send() {

        try {
            // Step1
//            println("\n 1st ===> setup Mail Server Properties..");
            mailServerProperties = System.getProperties();
            mailServerProperties.put("mail.smtp.port", "587");
            mailServerProperties.put("mail.smtp.auth", "true");
            mailServerProperties.put("mail.smtp.starttls.enable", "true");
//            println("Mail Server Properties have been setup successfully..");

            // Step2
//            println("\n\n 2nd ===> get Mail Session..");
            getMailSession = Session.getDefaultInstance(mailServerProperties, null);
            generateMailMessage = new MimeMessage(getMailSession);
            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(Dest));
            //  generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@gmail.com"));
            generateMailMessage.setSubject(Subject);
            generateMailMessage.setContent(Msg, "text/html");
//            System.out.println("Mail Session has been created successfully..");

            // Step3
//            println("\n\n 3rd ===> Get Session and Send mail");
            Transport transport = getMailSession.getTransport("smtp");

            // Enter your correct gmail UserID and Password
            // if you have 2FA enabled then provide App Specific Password
            transport.connect("smtp.gmail.com", user, password);
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
            transport.close();
            Resultado = 'File Sent'
            return true
        }
        catch (ex) {
            Resultado = ex.message
            return false
        }
    }
}

