package mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ClienteMail {

	
	
	
	public static void main(String[] args) throws MessagingException {
		String smtpServer = "smtp.gmail.com";
		String cliente ="clienteppc2018@gmail.com";
		String serverDistribucion = "serverppc2018@gmail.com";
		
		
		Properties p = new Properties();
		p.setProperty("mail.smtp.host", smtpServer);
		p.setProperty("mail.smtp.starttls.enable", "true");
		p.setProperty("mail.smtp.port", "587");
		p.setProperty("mail.smtp.auth", "true");
		//creamos la sesion
		Session session = Session.getDefaultInstance(p,null);
		
		//creamos mensaje
		MimeMessage message = new MimeMessage(session);
		message.setContent("peticion","text/plain");
		//message.setText("peticion");
		
		message.setSubject("Peticion");
		message.setFrom(new InternetAddress(cliente));
		message.setSender(new InternetAddress(serverDistribucion));
		message.setRecipient(Message.RecipientType.TO,new InternetAddress(serverDistribucion));
		
		//nos conectamos al servidor SMTP
		
		Transport t = session.getTransport("smtp");
		t.connect(smtpServer,cliente,"clienteppc");
		t.sendMessage(message, message.getAllRecipients());
		System.out.println("Mensaje enviado");
		
		
		
		
	}
	
}
