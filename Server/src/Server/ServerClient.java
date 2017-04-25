package Server;

import java.net.InetAddress;
import java.util.ArrayList;



public class ServerClient {

	public String username;
	public InetAddress address;
	public int port;
	public boolean status = false; //is Connected
	public ArrayList<Projectile> bullets = new ArrayList<Projectile>();
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
	
	public void addBullet(Projectile p)
	{
		bullets.add(p);
	}
	
	public void removeBullet(Projectile p)
	{
		bullets.remove(p);
	}
}

class Projectile 
{
	public int x, y; 
	public float xdir, ydir;
	public int newB;
	public String username;
	
	public Projectile(int x, int y, float xdir, float ydir, String username)
	{
		this.x = x;
		this.y = y;
		this.xdir = xdir;
		this.ydir = ydir;
		this.username = username;
		this.newB = 1;
	}
}


