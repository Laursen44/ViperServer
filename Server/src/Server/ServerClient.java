package Server;

import java.net.InetAddress;

public class ServerClient {

	public int userID;
	private InetAddress address;
	private int port;
	private boolean status = false; //is Connected
	
	private static int userIDCounter = 0;
	
	public ServerClient(InetAddress address, int port)
	{
		userID = userIDCounter++;
		this.address = address;
		this.port =port;
		status = true;
	}
	
	public int hashCode()
	{
		return userID;
	}
	
}
