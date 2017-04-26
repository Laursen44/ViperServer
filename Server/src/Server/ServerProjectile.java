package Server;

import java.util.ArrayList;

public class ServerProjectile 
{
	public float x, y, a; 
	public int t;
	public String username;
	public static ArrayList<ServerProjectile> bullets = new ArrayList<ServerProjectile>();
	
	public ServerProjectile(float x, float y, float a, int t, String username)
	{
		this.x = x;
		this.y = y;
		this.a = a;
		this.t = t;
		this.username = username;
	}	
	
	public void addBullet(ServerProjectile p)
	{
		bullets.add(p);
	}
	
	public void removeBullet(ServerProjectile p)
	{
		bullets.remove(p);
	}
}
