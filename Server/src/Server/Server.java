package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server
{
	private int port;
	private Thread listenThread;
	private boolean listening = false;
	private DatagramSocket socket;
	
	//server constructor
	public Server(int port)
	{
		this.port = port;
		
	}
	
	public int getPort()
	{
		return port;
	}
	
	//shoots off a separate thread that listens for client inputs
	public void start()
	{
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		} 
		//listenThread object variable is assigned to an instance of
		//the class thread and is implicitly implementing a runnable in
		//which the listen method is run. Thereafter started. 
		listening = true;
		listenThread= new Thread (() -> listen());
		listenThread.start();
	}
	public void listen()
	{
		while (listening)
		{
			
		}
	}
	
	private void process(DatagramPacket packet)
	{
		
	}
	
	public void send(byte[] data, InetAddress adress, int port)
	{
		DatagramPacket pack = new DatagramPacket(data, data.length, adress, port);
		
		try {
			socket.send(pack);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
