//3.3.1 RMI på klientsidan

import java.rmi.*;
import java.io.*; 
import java.util.*; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client 
{
	public static void main(String[] args)
	{
		String host = "atlas.dsv.su.se";
		int xLim = 500, yLim = 500, rMax = 70;
		Vector ballsRaw = null;
		
		if(args.length > 0)
		{
			//getting host 
			host = args[0];
		}
		
		//createing gui
		JFrame frame = new JFrame("");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(xLim, yLim);
		frame.setLocation(300,200);
		
		//to get Key input
		JTextField textLisener = new JTextField();

		//display panel
		JPanel panel = new JPanel();
		
		//conenct to panel 
		frame.add(textLisener);
		frame.getContentPane().add(panel);
		
		frame.setVisible(true);
	
	
		try
		{
			//getting remoteServer
			String url = "rmi://" + host + "/";
			RemoteServer  remoteServer = (RemoteServer)Naming.lookup(url + "server");

			//addBall and pauseBalls when keyPressed 
			textLisener.addKeyListener(new KeyListener()
			{
				@Override
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyChar() == '+') 
					{
						try 
						{
							remoteServer.addBall();
						} catch(RemoteException re)
						{
							System.out.println("Exception generated: " + re.getMessage());
						}
					}
					if (e.getKeyChar() == 'p') 
					{
						try
						{
							remoteServer.pauseBalls();
						} catch(RemoteException re)
						{
							System.out.println("Exception generated: " + re.getMessage());
						}
					}
				}
				@Override
				public void keyTyped(KeyEvent e){}

				@Override
				public void keyReleased(KeyEvent e){}
			});

			while(true)
			{
				//temp vector for getting balls from remoteServer without overiding old balls
				Vector ballsRawTmp = null;
				try 
				{
					ballsRawTmp = remoteServer.getBalls();
				} catch(RemoteException re) 
				{
					System.out.println("Exception generated: " + re.getMessage());
				}
				  
				Graphics g = panel.getGraphics();
				
				//removal of balls
				g.setColor(panel.getBackground());
				if(ballsRaw != null)
				{
					for(int i = 0; i < ballsRaw.size(); i = i + 3) 
					{
						int x = (Integer)ballsRaw.elementAt(i);
						int y = (Integer)ballsRaw.elementAt(i + 1);
						int r = (Integer)ballsRaw.elementAt(i + 2);
						g.fillOval(x, y - 30, r, r);
					}
				}

				// draw new balls
				ballsRaw = ballsRawTmp;	  
				for(int i = 0; i < ballsRaw.size(); i = i + 3)
				{
					int x = (Integer)ballsRaw.elementAt(i);
					int y = (Integer)ballsRaw.elementAt(i + 1);
					int r = (Integer)ballsRaw.elementAt(i + 2);
					
					int colorShift = r * 255 / rMax; 
					if(colorShift > 255) 
						colorShift = 255; if(colorShift < 0) colorShift = 0;
					g.setColor(new Color(0, colorShift, colorShift));
					g.fillOval(x, y - 30, r, r);
				}
				
				//counting balls
				frame.setTitle("Antal bollar: " + ballsRaw.size() / 3);

				
				try { Thread.sleep(30); } catch(Exception e) {}
			}		
		}
		catch (Exception e) { e.printStackTrace();}
	}
}


