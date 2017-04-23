package Server;

public class ViperServer 
{
	public static void main (String[] args)
	{
		System.out.println("Server running");
		Server server = new Server(5030);
		server.start();
	}
}
