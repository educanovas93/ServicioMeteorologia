package mail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

public class ServidorMail extends Thread {
	
	
	
	
@Override
public void run() {
		String smtpServer = "smtp.gmail.com";
		String serverDistribucion = "serverppc2018@gmail.com";
		
		
		Properties p = new Properties();
		p.setProperty("mail.smtp.host", smtpServer);
		p.setProperty("mail.smtp.starttls.enable", "true");
		p.setProperty("mail.smtp.port", "587");
		p.setProperty("mail.smtp.auth", "true");
		//creamos la sesion
		Session session = Session.getDefaultInstance(p,null);
		
		
		try {
			while (true) {
			Store store = session.getStore("imaps");
			store.connect("imap.gmail.com",serverDistribucion,"serverppc");
			Folder folder = store.getFolder("inbox");
			folder.open(Folder.READ_WRITE);
			
				Message mensajes[] = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN),false));
				
				System.out.println("Hay "+mensajes.length+" mensajes no leidos");
				for (Message m : mensajes) {
					folder.getMessage(m.getMessageNumber()).getContent();
					m.getContent();
					
					if(m.getSubject().equalsIgnoreCase("Peticion")) {
						
						System.out.println("Mensaje de petición encontrado,enviando datos...");
						Session nuevaSession = Session.getDefaultInstance(p,null);
						MimeMessage response = new MimeMessage(nuevaSession);

						String content = new Scanner(new File("distribucion/dist.html")).useDelimiter("\\Z").next();
						response.setContent(content,"text/html");
						
						response.setSubject("Datos");
						response.setFrom(new InternetAddress(serverDistribucion));
						response.setSender(new InternetAddress(serverDistribucion));
						response.setRecipients(Message.RecipientType.TO, m.getFrom());
						//nos conectamos al servidor SMTP
						
						Transport t = nuevaSession.getTransport("smtp");
						t.connect(smtpServer,serverDistribucion,"serverppc");
						t.sendMessage(response,response.getAllRecipients());
						System.out.println("Datos del meteorología enviados");
						
						
						
						//t.close();
						//folder.close(true);
						//store.close();
					}
				}
				folder.close(false);
				store.close();
				
				Thread.sleep(1000);
			}
		} catch (MessagingException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}


}
