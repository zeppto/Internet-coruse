//2.1.2 Stream sockets på serversidan

import java.util.*; 
import java.io.*;  
import java.net.*;  

public class Server
{
	public static void main (String[] args)
	{
		int port = 2000;		
		
		if(args.length > 0)
		{
			port = Integer.parseInt(args[0]);
		}
		
		System.out.println("Port: "+ Integer.toString(port));
		
		//starting a server therd
		MyServerSocket myServerSocket = new MyServerSocket(port);
	}
}

class MyServerSocket 
{
	//client list and massage list
	List<MyClientSocket> clientList = Collections.synchronizedList(new ArrayList<MyClientSocket>()); 
	List<String> messages = Collections.synchronizedList(new ArrayList<String>()); 
	
	public MyServerSocket(int port) 
	{
		try
		{
			//creating a message therd
			MessageHandeler msgHandeler = new MessageHandeler();
			
			//starting server
			ServerSocket server = new ServerSocket(port);
			String host = server.getInetAddress().getLocalHost().getHostName();
			System.out.println("host addres: " + host);
			
			while(true)
			{
				//accapting clients and adding them to list
				clientList.add(new MyClientSocket(server.accept()));
			}
		}
		catch(Exception e)
		{
			System.out.println(e);	
		}
	}	
	
	class MyClientSocket extends Thread 
	{	
		public Socket s;
		public PrintWriter writer;
		
		public MyClientSocket(Socket socket) 
		{
			s = socket;
			
			//sendeing out that a conectin has happand
			String conection = "Client: " + s.getRemoteSocketAddress().toString() + ": Conected";
			System.out.println(conection);
			messages.add(conection);
					
			start();
		}
		
		public void run() 
		{
			try
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
				writer = new PrintWriter(s.getOutputStream(), true);
				
				String msg;
				while((msg = reader.readLine()) != null)
				{
					//sending messages that has been resived  					
					messages.add(msg);
					System.out.println("Client: " + s.getRemoteSocketAddress().toString() + " Broadcast: " + msg);
				}
				
				//closing therd and conection to socket
				reader.close();
				writer.close();
				s.close();
			}
			catch(Exception e)
			{
				System.out.println(e);	
			}
			//sending disconetion massage
			String disconection = "Client: " + s.getRemoteSocketAddress() + ": Disconected";
			System.out.println(disconection);
			messages.add(disconection);
			//removing client from client list
			s = null;
			synchronized(clientList) 
			{ 
				Iterator<MyClientSocket> it = clientList.iterator(); 
	  
				while (it.hasNext()) 
				{
					//remove disconected threds
					MyClientSocket client = it.next();
					if(client.s == null)
						it.remove(); 							
				}
			} 
		}
	}
	
	class MessageHandeler extends Thread 
	{			
		public MessageHandeler() 
		{	
			start();
		}
		
		public void run() 
		{
			while(true)
			{
				synchronized(messages) 
				{ 
					synchronized(clientList) 
					{
						Iterator<String> msgIt = messages.iterator(); 
						
						//iterate through messages
						while (msgIt.hasNext()) 
						{
							String msg = msgIt.next();
							Iterator<MyClientSocket> ClientIt = clientList.iterator(); 
							//iterate through clients for each messages
							while (ClientIt.hasNext()) 
							{
								//sending messages to clients
								MyClientSocket client = ClientIt.next();
								client.writer.println(msg);
							}
							msgIt.remove();
						}
					}
				} 
			}
		}
	}
}


