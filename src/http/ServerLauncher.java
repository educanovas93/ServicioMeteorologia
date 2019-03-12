package http;

import java.util.Scanner;

public class ServerLauncher {

	
	
	public static void main(String[] args) throws InterruptedException {
		String mode;
		Scanner scan = new Scanner(System.in);
		System.out.println("En que modo quieres utilizar el servidor ECO o WEB");
		mode = scan.nextLine();
		Servidor server = new Servidor(8080, false, mode);
		Servidor secureServer = new Servidor(4430,true,mode);
		server.start();
		secureServer.start();
	}
}
