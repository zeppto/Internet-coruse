//2.1.1 Stream sockets på klientsidan

import java.io.*;  
import java.net.*;  

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Client
{
	public static void main (String[] args)
	{
		//host and port
		String host = " 127.0.0.1";
		int port = 2000;
		if(args.length > 0)
		{
			System.out.println("Host: "+args[0]);
			host = args[0];
		}			
		else	
		{	
			System.out.println("Host: "+ "atlas.dsv.su.se");
		}			
		
		if(args.length > 1)
		{
			System.out.println("Port: "+args[1]);
			port = Integer.parseInt(args[1]);
		}			
		else	
		{
			System.out.println("Port: "+ "9494"); 
		}
		
		//create read thread
		ChattThread read = new ChattThread(host, port);
		
	}
}

//thread for gui elements
class GUIClass extends Thread 
{
	boolean programOpen = true;
	String massage = "";
	JTextArea textArea;
	PrintWriter out;
	
	public GUIClass() 
	{
		//createing gui
		JFrame frame = new JFrame("Chatt");
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(450, 400);
		frame.setLocation(300,200);
		//to close nicely
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() 
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) 
			{
				if(programOpen)
				{
					out.println("*...left the chatt*");
					programOpen = false;
				}
				else
				{
					System.exit(0);
				}
			}
		});
		
		//creat botttom panal
		JPanel botttomPanel = new JPanel();
		JTextField textField = new JTextField(20);
		JButton send = new JButton("Send");
		botttomPanel.add(textField);
		botttomPanel.add(send);
		
		//text display
		JPanel middlePanel = new JPanel();
		textArea = new JTextArea(18, 37);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		middlePanel.add(scroll);
		textArea.setEditable(false);
		
		//conenct to panel
		frame.getContentPane().add(BorderLayout.SOUTH, botttomPanel);  
		frame.getContentPane().add(BorderLayout.CENTER, middlePanel);

		frame.setVisible(true);
		
		
		//send massage
		textField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{;	
				//send text
				String massage = textField.getText();				
				textField.setText("");
				out.println(massage);
			}
		});
		send.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//send text
				String massage = textField.getText();				
				textField.setText("");
				out.println(massage);
			}
		});
		
		start();
	}
	
	public void uppdateText(String msg)
	{
		textArea.append("	" + msg + "\n");
	}
	
	public boolean timeToclose()
	{
		return programOpen;
	}
}

class ChattThread extends Thread 
{
	boolean alive = true;
	String host = "127.0.0.1"; 
	int port = 2000;
	GUIClass gui;
	
	public ChattThread(String in_host, int in_port) 
	{
		host = in_host;
		port = in_port;
		//GUI 
		gui = new GUIClass();
		start();
	}
	
	public void run() 
	{
		while(alive)
		{
			try
			{    
				Socket s = new Socket(host,port);  
				String hostAd = s.getInetAddress().getHostName();
				System.out.println("host addres " + hostAd);
				PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream() , "ISO-8859-1"), true);
				BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()));
				BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in));
				
				gui.out = out;

				//reading from server
				String userInput;
				while(alive)
				{
					userInput = in.readLine();
					gui.uppdateText(userInput);
					
					if(!gui.timeToclose())
					{
						gui.uppdateText(userInput);
						alive = false;
						s.close();
						System.exit(0);
					}
				}
			}
			catch(Exception e)
			{
				System.out.println(e);	
				alive = false;
				System.exit(0);
			}
		}
	}
}

