package Server;

import java.net.InetAddress;
import java.util.ArrayList;



public class ServerClient {

	public String username;
	public InetAddress address;
	public int port;
	public boolean status = false; //is Connected
	public int x, y;
	
	public ServerClient(InetAddress address, int port, String username)
	{
		this.address = address;
		this.port = port;
		this.username = username;
		status = true;
		this.x = 200;
		this.y = 200;
	}
}



