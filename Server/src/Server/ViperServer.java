package Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ViperServer 
{
	
	public static void main (String[] args)
	{
		System.out.println("Muggle Strugle");
		Server server = new Server(2205);
		server.start();
		
		InetAddress address = null;
		try {
			address = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		int port = 1337;
		server.send(new byte[]{1,2,3}, address, port);
	}
}