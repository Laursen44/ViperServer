package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
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
	private ArrayList<ServerClient> clients = new ArrayList<ServerClient>();

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
		
		System.out.println("started server on port 5030!");
		
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
		
		if (new String(data,0,4).equals("VPDB"))
		{
			VPDatabase database = VPDatabase.Deserialize(data);
			process(database, pack);		
		}
	}
	
	private void process (VPDatabase database, DatagramPacket pack) 
	{
		System.out.println("received database!");
		
		if(database.getName().equals("Connection"))
		{
			// DO: create a server client!
			String username = "";
			for(VPObject object : database.objects)
			{
				username = object.getName();
			}
			boolean isConnected = false;
			for (int i = 0; i < clients.size(); i++)
			{
				if(clients.get(i).username.equals(username))
				{
					isConnected = true;
				}
			}
			if(!isConnected)
			{
				clients.add(new ServerClient(pack.getAddress(), pack.getPort(), username));
			}
		}
		
		if(database.getName().equals("Update"))
		{
			// DO: check user-name against list of server clients
			// if equal update its data!
			for (VPObject object : database.objects)
			{
				for (int i = 0; i < clients.size(); i++)
				{
					if(object.getName().equals(clients.get(i).username))
					{
						for (VPField field : object.fields)
						{
							if(field.getName().equals("x"))
							{
								clients.get(i).x = field.getInt();
							}
							
							if(field.getName().equals("y"))
							{
								clients.get(i).y = field.getInt();
							}
						}
					} 
				}
			}
		}
		
		if(database.getName().equals("Projectile"))
		{
			for (VPObject object : database.objects)
			{
				String username = object.getName();
				float x= 0, y = 0, a = 0;
				int t = 0;
				
				for (VPField field : object.fields)
				{
					if (field.getName().equals("x")) x = field.getFloat();
					if (field.getName().equals("y")) y = field.getFloat();
					if (field.getName().equals("a")) a = field.getFloat();
					if (field.getName().equals("t")) t = field.getInt();
				}
				
				ServerProjectile.bullets.add(new ServerProjectile(x, y, a, t, username));
			}
		}
		
		if(database.getName().equals("Dead"))
		{
			// Do: check user-name agianst list of server clients
			// if equal remove that one.
			for (VPObject object : database.objects)
			{
				for (int i = 0; i < clients.size(); i++)
				{
					if(object.getName().equals(clients.get(i)))
					{
						clients.remove(i);
					}
				}
			}
		}
		
		// sends and updates clients with new information!
		updateClients();
		
		// prints content in console for debugging.
		//dump(database);
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
	
	public void send(VPDatabase database, InetAddress address, int port)
	{
		byte[] data = new byte[database.getSize()];
		database.getBytes(data,0);
		send(data, address, port);
	}
	
	public void updateClients()
	{
		playerUpdate();
		projectileUpdate();
	}
	
	public void playerUpdate()
	{
		VPDatabase database = new VPDatabase("PlayerPos");
		
		for (int i = 0; i < clients.size(); i++)
		{
			VPObject object = new VPObject(clients.get(i).username);
			
			VPField xcord = VPField.Integer("x", clients.get(i).x);
			VPField ycord = VPField.Integer("y", clients.get(i).y);
			
			object.addField(xcord);
			object.addField(ycord);
			
			database.addObject(object);
		}
		
		for (int i = 0; i < clients.size(); i++)
		{
			send(database, clients.get(i).address, clients.get(i).port );
			System.out.println("Players updated!");
		}
	}
	
	public void projectileUpdate()
	{
		VPDatabase database = new VPDatabase("ProjectileBlue");
		
		for (ServerProjectile bullet : ServerProjectile.bullets)
		{ 
			VPObject object = new VPObject(bullet.username);
			VPField x = VPField.Float("x", bullet.x);
			VPField y = VPField.Float("y", bullet.y);
			VPField a = VPField.Float("a", bullet.a);
			VPField t = VPField.Integer("t", bullet.t);
			object.addField(x);
			object.addField(y);
			object.addField(a);
			object.addField(t);
			database.addObject(object);
		}
		
		for (ServerClient client : clients)
		{
			send(database, client.address, client.port );
			System.out.println("ProjectileBlue sent");
			ServerProjectile.bullets.clear();
		}
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
				System.out.println("\t\tSize: " + field.getInt());
		System.out.println("---------------------------------");
		
			}
		}
	}
}