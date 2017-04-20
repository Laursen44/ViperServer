package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

import Serialization.Type;
import Serialization.VPDatabase;
import Serialization.VPField;
import Serialization.VPObject;

public class Server
{
	private int port;
	private Thread listenThread;
	private boolean listening = false;
	private DatagramSocket socket;
	//Sets a value that will be used at the maximum size for datapackets received. 
	private final int MAX_PACKET_SIZE = 1024;
	private byte[] receivedDataBuffer = new byte [MAX_PACKET_SIZE *10]; 
	private Set<ServerClient> clients = new HashSet<ServerClient>();

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
		
		System.out.println("started server on port 8586!");
		
		//listenThread object variable is assigned to an instance of
		//the class thread and is implicitly implementing a runnable in
		//which the listen method is run. Thereafter started. 
		listening = true;
		listenThread= new Thread (() -> listen(), "ViperProjectServer-ListenThread" );
		listenThread.start();
		System.out.println("Server is Listening");
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
		InetAddress address = pack.getAddress();
		int port = pack.getPort();
		dump(pack);
		if (new String(data,0,4).equals("VPDB"))
		{
			VPDatabase database = VPDatabase.Deserialize(data);
			process(database);		
		}
		else if (data[0] ==0x40 && data[1] ==0x40)
		{
			switch (data[2])
			{
			case 0x01:
				clients.add(new ServerClient(pack.getAddress(), pack.getPort()));
				break;
			case 2:
				//timeout packet
				break;
			}
		}
	}
	
	private void process (VPDatabase database) 
	{
		System.out.println("received database!");
		dump(database);
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
	
	private void dump(DatagramPacket pack)
	{
		byte[] data = pack.getData();
		InetAddress address = pack.getAddress();
		int port = pack.getPort();
		
		System.out.println("-------------------");
		System.out.println("PACKET:");
		System.out.println("\t"+address.getHostAddress()+":"+port);
		System.out.println();
		System.out.println("\tContents:");
		System.out.println("\t\t");
		
		for (int i = 0; i < pack.getLength(); i++)
		{
			System.out.printf("%x", data[i]);
			if ((i+1 ) % 16 == 0)
				System.out.print("\n\t\t");
		}
		System.out.println("-------------------");
	}
	
	private void dump(VPDatabase database)
	{
		System.out.println("----------------------------------");
		System.out.println("VPDatabase");
		System.out.println("----------------------------------");
		System.out.println("Name: " + database.getName());
		System.out.println("Size: " + database.getSize());
		System.out.println("Object count: " + database.objects.size());
		System.out.println();
		for(VPObject object : database.objects)
		{
			System.out.println("\tObject:");
			System.out.println("\tName: " + object.getName());
			System.out.println("\tSize: " +object.getSize());
			System.out.println("\tField Count: " + object.fields.size());
			for (VPField field : object.fields)
			{
				System.out.println("\t\tField: ");
				System.out.println("\t\tName: " + field.getName());
				System.out.println("\t\tSize: " + field.getSize());
				String data ="";
				switch (field.type)
				{
				case Type.BYTE:
					data += field.getByte();
					break;
				case Type.SHORT:
					data += field.getShort();
					break;
				case Type.CHAR:
					data += field.getChar();
					break;
				case Type.INT:
					data += field.getInt();
					break;
				case Type.LONG:
					data += field.getLong();
					break;
				case Type.FLOAT:
					data += field.getFloat();
					break;
				case Type.DOUBLE:
					data += field.getDouble();
					break;
				case Type.BOOLEAN:
					data += field.getBoolean();
					break;
				}
				System.out.println("data: " +data);
			}
		}
		
		System.out.println("---------------------------------");
		
	}
}
