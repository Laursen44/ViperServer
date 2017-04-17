package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import Serialization.VPDatabase;

public class Server
{
	private int port;
	private Thread listenThread;
	private boolean listening = false;
	private DatagramSocket socket;
	//Sets a value that will be used at the maximum size for datapackets received. 
	private final int MAX_PACKET_SIZE = 1024;

	private byte[] receivedDataBuffer = new byte [MAX_PACKET_SIZE *10]; 

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
		listenThread= new Thread (() -> listen(), "ViperProjectServer-ListenThread" );
		listenThread.start();
	}
	public void listen()
	{
		while (listening)
		{
			DatagramPacket  pack = new DatagramPacket(receivedDataBuffer, MAX_PACKET_SIZE);
			try
			{
				socket.receive(pack);
			}  
			catch (IOException e) {
				e.printStackTrace();
			}
			process(pack);
		}
	}

	private void process(DatagramPacket pack)
	{
		byte[] data = pack.getData();
		if (new String(data,0,4).equals("VPDB"))
		{
			VPDatabase database = VPDatabase.Deserialize(data);
			process(database);		
		}
		else
		{
			switch (data[0])
			{
			case 1:
				//connection packet
				break;
			case 2:
				//timeout packet
				break;
			}
		}
	}
	
	private void process (VPDatabase database) 
	{
		
	}

	public void send(byte[] data, InetAddress address, int port)
	{
		assert(socket.isConnected());
		DatagramPacket pack = new DatagramPacket(data, data.length, address, port);

		try {
			socket.send(pack);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
