import java.io.*;
import java.net.*;

public class smtpServer {
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(25);
			System.out.println("SMTP server is ready");
			while (true) {
				Socket s = ss.accept();
				new Thread(new smtpServerSession(s)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}