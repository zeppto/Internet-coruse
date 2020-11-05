//2.2.1 Datagram sockets med unicast

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class Draw 
{	
	public static void main(String[] args) 
	{	
		//getting host and ports
		int serverPort = 2002;
		int clientPort = 2002;
		String host = "localhost";

		if(args.length > 2)
		{
			serverPort = Integer.parseInt(args[0]);
			host = args[1];
			clientPort = Integer.parseInt(args[2]);
		}
		
		//creating client, paper and server
		DrawClient client = new DrawClient(clientPort, host);
		Paper p = new Paper(client);
		DrawServer server = new DrawServer(serverPort, p);
		
		//createing gui
		JFrame frame = new JFrame("Whiteboard");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 640);
		frame.setVisible(true);
		frame.getContentPane().add(p, BorderLayout.CENTER);
	}	
}


class Paper extends JPanel 
{
	private HashSet<Point> hs = new HashSet<Point>();
	private DrawClient client;
	
	public Paper(DrawClient client) 
	{
		this.client = client;
		setBackground(Color.white);
		addMouseListener(new L1());
		addMouseMotionListener(new L2());
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.black);
		synchronized(hs) 
		{ 
			Iterator i = hs.iterator();
			while(i.hasNext()) 
			{
				Point p = (Point)i.next();
				g.fillOval(p.x, p.y, 4, 4);
			}
		}
	}
	
	public void retrieveDrawing(String msg)
	{
		synchronized(hs) 
		{ 
			Point p;
			if((p = stringToPoint(msg)) != null)
			{
				hs.add(p);
				repaint();
			}
		}
	}

	private void addPoint(Point p)
	{
		synchronized(hs) 
		{ 
			client.sendDrawing(pointToString(p));
			hs.add(p);
			repaint();
		}
	}
	
	private String pointToString (Point p)
	{
		return p.x + " " + p.y + " ";
	}
	
	private Point stringToPoint (String msg)
	{
		Point p = null;
		String[] xy = msg.split(" ");
		if (xy.length > 1)
		{
			p = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])); 
		}
		return p;
	}

	class L1 extends MouseAdapter
	{
		public void mousePressed(MouseEvent me) 
		{
			addPoint(me.getPoint());
		}
	}

	class L2 extends MouseMotionAdapter 
	{
		public void mouseDragged(MouseEvent me) 
		{
			addPoint(me.getPoint());
		}
	}
}

//client
class DrawClient 
{
	boolean active = true;
	DatagramSocket socket;
	InetAddress address;
	
	byte[] buf;
	int port;
	
	
	public DrawClient(int port, String host) 
	{
		this.port = port;
		try
		{
			socket = new DatagramSocket();
		}
		catch(SocketException ie) {}
		try
		{
			address = InetAddress.getByName(host);
		}
		catch(UnknownHostException ie) {}	
	}
	
	public void sendDrawing(String msg)
	{
		try
		{
			buf = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
			socket.send(packet);
		}
		catch(IOException e) {}
	}
	
	public void close()
	{
		socket.close();
	}
}

//server
class DrawServer extends Thread
{
	boolean active = true;
	DatagramSocket socket;
	byte[] buf = new byte[10];
	
	Paper paper;
	
	public DrawServer( int port, Paper p)
	{
		this.paper = p;
		try
		{
			socket = new DatagramSocket(port);
			start();
		}
		catch(SocketException ie) {}
	}
	
	public void run()
	{
		while (active)
		{
			try
			{
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				packet = new DatagramPacket(buf, buf.length, address, port);
				String received = new String(packet.getData(), 0, packet.getLength());
				paper.retrieveDrawing(received);
				if(received.equals("end"))
				{
					active = false;
					continue;
				}
				socket.send(packet);
			}
			catch(IOException e) {}
		}
		socket.close();
	}
}
