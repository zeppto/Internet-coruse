//Gesällprov

import java.io.*;  
import java.net.*;  
import java.util.*; 
import java.sql.*;
import java.util.Base64;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class Gesallprov
{
	public static void main (String[] args)
	{
		//host and port
		String host = "atlas.dsv.su.se";
		int port = 9494;
		if(args.length > 0)
		{
			host = args[0];
		}					
		if(args.length > 1)
		{
			port = Integer.parseInt(args[1]);
		}			

		System.out.println("Host: "+ host);			
		System.out.println("Port: "+ port); 
		
		try
		{ 
			Class.forName("com.mysql.jdbc.Driver").newInstance(); 
		}
		catch(Exception e) 
        { 
            System.out.println(e); 
        }
		
		//create read thread
		ChattThread read = new ChattThread(host, port);
		
	}
}

class Account
{
	//for database
	String computer = "atlas.dsv.su.se";
	String db_name = "db_20232153";
	String url = "jdbc:mysql://" + computer + "/" + db_name;
	String dbUsername = "usr_20232153";
	String dbPassword = "232153";
	
	String username;
	String password;
	String key;
	String friends;
	String friendCode;
	
	public Account()
	{
	}
	
	public Account(String username, String password, String key,
		String friend, String friendCode)
	{
		this.username = username;
		this.password = password;
		this.key = key;
		this.friends = friend;
		this.friendCode = friendCode;
	}
	
	public void update(String username, String password, String key,
		String friend, String friendCode)
	{
		this.username = username;
		this.password = password;
		this.key = key;
		this.friends = friend;
		this.friendCode = friendCode;
	}
	
	public boolean login(String username, String password)
	{
		this.username = username;
		this.password = password;

		boolean exist = false;
	
		try
        { 
			Connection dbConnection = DriverManager.getConnection(url, dbUsername, dbPassword);
			Statement stmt = dbConnection.createStatement(); 
              
            //search for matching username and password in the database 
            String q1 = "select * from ChattAccounts"; 
            ResultSet rs = stmt.executeQuery(q1); 
            while(rs.next())
            {
				if(username.equals(rs.getString(1)) && password.equals(rs.getString(2)))
				{
					exist = true;
					//getting userinfo 
					username = rs.getString(1);
					password = rs.getString(2);
					key = rs.getString(3);
					friends = rs.getString(4);
					friendCode = rs.getString(5);
					break;
				}
            }
            stmt.close();
            dbConnection.close(); 
		}
		catch(Exception e) 
        { 
            System.out.println(e); 
        }
		return exist;
	}
	//printing out users name, friendCode and friends 
	public String friendListInit()
	{
		return "Welcome: " + username + "!" + "\n" + "Your friend code: " + friendCode + "\n\n" + "Friends: \n" + friends.replace(" ","\n");
	}
	//if there is a user with the enterd friendCode they become friends 
	public String findFriend(String friend)
	{
		boolean exist = false;
		String newFriendName = "";
		String newFriendsList = "";
		try
        { 
			Connection dbConnection = DriverManager.getConnection(url, dbUsername, dbPassword);			
			Statement stmt = dbConnection.createStatement(); 
              	  
			//checking if friend exist
			String q1 = "select * from ChattAccounts"; 
            ResultSet rs = stmt.executeQuery(q1); 
            while(rs.next())
            {
				if(friend.equals(rs.getString(5)))
				{
					exist = true;
					newFriendName = rs.getString(1);
					newFriendsList = rs.getString(4);
					
					String[] myFriends = friends.split(" ");
					for(int i = 0; myFriends.length < i; i++)
					{
						if(myFriends[0].equals(newFriendName))
						{
							exist = false;
						}
					}
					break;
				}
            }  
			  
			//if user exist both friends get their new friends added to their friendlists
			if(exist)
			{	
				String newFriend = friends + newFriendName + " ";
				String newFriendUpdate = newFriendsList + username + " ";
				  
				String q2 ="Update ChattAccounts set Friend='"+newFriend+"' where Username='"+username+"'";
				stmt.executeUpdate(q2); 
				
				String q3 ="Update ChattAccounts set Friend='"+newFriendUpdate+"' where Username='"+newFriendName+"'";
				stmt.executeUpdate(q3); 
				exist = false;
			}
			stmt.close(); 
            dbConnection.close(); 
		}
		catch(Exception e) { System.out.println(e); }
		
		return newFriendName;
	}
	//when friends send messages getKey is used to get the friends key from the database
	public byte[] getKey(String msgUsername)
	{
		byte[] key = null;
		
		try
		{
			Connection dbConnection = DriverManager.getConnection(url, dbUsername, dbPassword);			
			Statement stmt = dbConnection.createStatement(); 
			
			//update the friendlist for users that have been added as friends by others  
			String q1 = "select * from ChattAccounts"; 
			ResultSet rs = stmt.executeQuery(q1); 
			while(rs.next())
			{
				if(username.equals(rs.getString(1)))
				{
					friends = rs.getString(4);
				}
			}
			
			//searching for the user in friendlist and if found searching for it in database
			//to get the users key
			String[] myFriends = friends.split(" ");
			for(int i = 0; myFriends.length > i; i++)
			{
				if(myFriends[i].equals(msgUsername))
				{ 
					rs = stmt.executeQuery(q1); 
					while(rs.next())
					{
						if(msgUsername.equals(rs.getString(1)))
						{
							String tempKey = rs.getString(3);
							key = tempKey.getBytes();
						}
					}
				}
			}
			stmt.close();
			dbConnection.close(); 
		}
		catch(Exception e) {System.out.println(e);}
		
		return key;		
	}
}

//thread for gui elements
class GUIClass extends Thread 
{
	boolean programOpen = true;
	String message = "";
	JTextArea textArea;
	JTextArea friendTextArea;
	PrintWriter out;
	MessageHandler magHandler = new MessageHandler();
	Account account = new Account();
	
	public GUIClass() 
	{
		//createing gui
		JFrame frame = new JFrame("Chatt");
		
		//prevent dialog from closing
		frame.setVisible(true);
		
		//login gui
		JDialog dialog = new JDialog(frame, "Login", true);  
        dialog.setLayout( new FlowLayout());  
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new java.awt.event.WindowAdapter() 
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) 
			{
				System.exit(0);
			}
		});
		
		
		//username panel
		JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField usernameTextField = new JTextField(22);
		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setPreferredSize(new Dimension(90, 15));
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameTextField);
		
		//password panel
		JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField passwordTextField = new JPasswordField(22);
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setPreferredSize(new Dimension(90, 15));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordTextField);
		
		//login button
		JPanel loginPanel = new JPanel();
		JLabel loginLabel = new JLabel ("   ");
        JButton loginButton = new JButton ("login"); 
		//when the login botttom is pressed account.login trys to find the user
		//if not found a error is displayed and the user needs to try ones more
        loginButton.addActionListener ( new ActionListener()  
        {  
            public void actionPerformed( ActionEvent e )  
            {  
				if(account.login(usernameTextField.getText(), passwordTextField.getText()))
				{
					dialog.setVisible(false);
				}
				else
				{
					passwordTextField.setText("");	
					loginLabel.setText("Error: username or password wrong");
				}				
            }  
        }); 
		loginPanel.add(loginLabel);
		loginPanel.add(loginButton);
		
		//create account panal
		JPanel accountPanel = new JPanel();
		JLabel accountLabel = new JLabel ("Click to create a new accout");
        JButton accountButton = new JButton ("new");
		//creates a new dialog box where the user can enter their 
		//a new username and password to cr a new account
        accountButton.addActionListener ( new ActionListener()  
        {  
            public void actionPerformed( ActionEvent e )  
            {  
				JDialog accountDialog = new JDialog(frame, "create account", true);
				accountDialog.setLayout( new FlowLayout() ); 				
		
				//username panel
				JPanel newUsernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				JTextField newUsernameTextField = new JTextField(22);
				JLabel newUsernameLabel = new JLabel("Username:");
				newUsernameLabel.setPreferredSize(new Dimension(110, 15));
				newUsernamePanel.add(newUsernameLabel);
				newUsernamePanel.add(newUsernameTextField);
				
				//password panel
				JPanel newPassword1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				JTextField newPassword1TextField = new JPasswordField(22);
				JLabel newPassword1Label = new JLabel("Password:");
				newPassword1Label.setPreferredSize(new Dimension(110, 15));
				newPassword1Panel.add(newPassword1Label);
				newPassword1Panel.add(newPassword1TextField);
				
				//password panel
				JPanel newPassword2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				JTextField newPassword2TextField = new JPasswordField(22);
				JLabel newPassword2Label = new JLabel("Repeat Password:");
				newPassword2Label.setPreferredSize(new Dimension(110, 15));
				newPassword2Panel.add(newPassword2Label);
				newPassword2Panel.add(newPassword2TextField);
				
				//create
				JPanel createPanel = new JPanel();
				JLabel errorLabel = new JLabel ("");
				JButton createButton = new JButton ("create"); 	
				createButton.setPreferredSize(new Dimension(200, 30));	
				//try to make a new account if the button is pressed
				createButton.addActionListener ( new ActionListener()  
				{  
					public void actionPerformed( ActionEvent e )  
					{  
						//getting username and password
						String username = newUsernameTextField.getText();
						String password1 = newPassword1TextField.getText();	
						String password2 = newPassword2TextField.getText();
						//checking the username and password if the are acceptable
						CreateAccount myAccount = new CreateAccount(username, password1, password2);
						if(myAccount.checkPassword())
						{
							if(myAccount.checkUsername())
							{	
								if(myAccount.CreateNewAccount())
									accountDialog.setVisible(false);
							}
							else
							{
								newPassword1TextField.setText("");	
								newPassword2TextField.setText("");
								errorLabel.setText("Error: username already exist");
							}
						}
						else
						{
							newPassword1TextField.setText("");	
							newPassword2TextField.setText("");
							errorLabel.setText("Error: password not matching or password not longer than 5");
						}
					}  
				}); 
				createPanel.add(createButton);
				
				accountDialog.add(newUsernamePanel);
				accountDialog.add(newPassword1Panel); 
				accountDialog.add(newPassword2Panel);
				accountDialog.add(createPanel); 				
				accountDialog.add(errorLabel);
				
				accountDialog.setSize(400,250); 
				accountDialog.setLocation(400,300);		
				accountDialog.setVisible(true); 
            }  
        }); 
		accountPanel.add(accountLabel);
		accountPanel.add(accountButton);
		
		dialog.add(usernamePanel);
		dialog.add(passwordPanel);  
		dialog.add(loginPanel); 
		dialog.add(new JLabel ("____________________________________________"));	
        dialog.add(accountPanel);      
        dialog.setSize(400,250); 
		dialog.setLocation(400,300);		
        dialog.setVisible(true);

		//setting up message window		
		frame.setSize(650, 460);
		frame.setLocation(300,200);
		//to close window nicely
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() 
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) 
			{
				if(programOpen)
				{
					out.println(account.username +" *left the chatt*");
					programOpen = false;
				}
				else
				{
					System.exit(0);
				}
			}
		});
		
		//creat search friend panal
		JPanel fSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JTextField fSearchTextField = new JTextField(12);
		JButton fSearchButton = new JButton("search friend");
		fSearchPanel.add(fSearchButton);
		fSearchPanel.add(fSearchTextField);
		
		//creat botttom panal
		JPanel botttomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField textField = new JTextField(26);
		JButton send = new JButton("Send");
		botttomPanel.add(textField);
		botttomPanel.add(send);
		botttomPanel.add(fSearchPanel);
		
		//friend list
		JPanel friendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		friendTextArea = new JTextArea(22, 16);
		JScrollPane scrollF = new JScrollPane(friendTextArea);
		scrollF.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		friendPanel.add(scrollF);
		friendTextArea.setEditable(false);
		
		//text display
		JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		textArea = new JTextArea(22, 36);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		middlePanel.add(scroll);
		middlePanel.add(friendPanel);
		textArea.setEditable(false);
		
		
		//conenct to panel
		frame.getContentPane().add(BorderLayout.SOUTH, botttomPanel);  
		frame.getContentPane().add(BorderLayout.CENTER, middlePanel);

		
		frame.setVisible(true);
		
		//print friendlist
		uppdateFriendText(account.friendListInit());
		
		
		//send message
		textField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{;	
				String message = textField.getText();				
				textField.setText("");
				//sending text to be encrypted and sent
				out.println(magHandler.encryptMessage(message, account));
			}
		});
		send.addActionListener(new ActionListener()
		{
			@Override
			
			public void actionPerformed(ActionEvent e) 
			{
				String message = textField.getText();				
				textField.setText("");
				//sending text to be encrypted and sent
				out.println(magHandler.encryptMessage(message, account)); 
			}
		});
		
		//search for new friend using friendCode and printing the success
		fSearchButton.addActionListener(new ActionListener()
		{
			@Override
			
			public void actionPerformed(ActionEvent e) 
			{
				String message = fSearchTextField.getText();				
				fSearchTextField.setText("");
				String newFriend = account.findFriend(message);
				//if the user successfuly became friends text of success gets printed
				if(newFriend != "")
				{
					uppdateFriendText(newFriend);
					out.println(magHandler.encryptMessage(account.username+" befriend "+newFriend, account)); 
				}
			}
		});
		
		start();
	}
	
	//updates message window
	public void uppdateText(String msg)
	{
		textArea.append(magHandler.decryptMessage(msg, account) + "\n");
	}
	
	//updates friends window
	public void uppdateFriendText(String msg)
	{
		friendTextArea.append(msg + "\n");
	}
	
	public boolean timeToclose()
	{
		return programOpen;
	}
}

//handles the client part of sending and resiving messages 
class ChattThread extends Thread 
{
	boolean alive = true;
	String host = "atlas.dsv.su.se";
	int port = 9494;
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
				PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream() , "ISO-8859-1"), true);
				BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()));
				BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in));
				
				//making gui able to send messages
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

//to create new accounts in the database
class CreateAccount
{
	String computer = "atlas.dsv.su.se";
	String db_name = "db_20232153";
	String url = "jdbc:mysql://" + computer + "/" + db_name;
	String dbUsername = "usr_20232153";
	String dbPassword = "232153";
	
	String username;
	String password1;
	String password2;
	
	public CreateAccount(String username, String password1, String password2)
	{
		this.username = username;
		this.password1 = password1;
		this.password2 = password2;
	}
	//checks if the passwords are the same and longer then 5
	public boolean checkPassword()
	{	
		return password1.equals(password2) && password1.length() > 5;
	}
	//checking if username already exist
	public boolean checkUsername()
	{	
		boolean usernameExist = false;
	
		try
        { 
			Connection dbConnection = DriverManager.getConnection(url, dbUsername, dbPassword);
			
			Statement stmt = dbConnection.createStatement(); 
              
            // select to get info from ChattAccounts
            String q1 = "select * from ChattAccounts"; 
            ResultSet rs = stmt.executeQuery(q1); 
            while(rs.next())
            {
				if(username.equals(rs.getString(1)))
				{
					usernameExist = true;
					break;
				}
            }
            
			stmt.close();
            dbConnection.close(); 
		}
		catch(Exception e) 
        { 
            System.out.println(e); 
        }
	
		return !usernameExist && username.length() > 1;
	}
	
	//creates new account i database
	public boolean CreateNewAccount()
	{
		boolean newAccountCreated = false;
		
		byte[] key = null;
		String msgKey = "";
		String friendList = "";
		String friendCode = "";
		
		//creates a key for the new user
		try
		{
			//generate key
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256); 
			SecretKey secretKey = keyGen.generateKey();
			key = secretKey.getEncoded();
		}
		catch (NoSuchAlgorithmException e) { e.printStackTrace();}
		
		
		try
		{

		Connection dbConnection = DriverManager.getConnection(url, dbUsername, dbPassword);
			
		Statement stmt = dbConnection.createStatement(); 
		
		//generates a new random friendCode for the user
		while(!newAccountCreated)
		{
			newAccountCreated = true;
			
			Random rand = new Random();
			friendCode = "";
			for(int i = 0; i < 8; i++)
				friendCode += Integer.toString(rand.nextInt(10));
			
			// select to get info from ChattAccounts
			String q1 = "select * from ChattAccounts"; 
			ResultSet rs = stmt.executeQuery(q1); 
			while(rs.next())
			{
				if(friendCode.equals(rs.getString(5)))
				{
					System.out.println("new account!");
					newAccountCreated = false;
					break;
				}
			}
		}				
			//inserting new account in database 
			String q1 = "insert into ChattAccounts values('" +username+ "', '" +password1+  
								  "', '" +key+ "', '" +friendList+ "', '" +friendCode+ "')"; 
			stmt.executeUpdate(q1); 
			String q2 ="Update ChattAccounts set MsgKey=? where Username='"+username+"'";
			PreparedStatement pstmt = dbConnection.prepareStatement(q2);
			pstmt.setBytes(1, key);
			pstmt.executeUpdate();
			pstmt.close();
			stmt.close();
			dbConnection.close();
			
			//displaying user input
			System.out.println("new account cerated!");

		}
		catch(Exception ex) 
		{ 
			System.out.println(ex); 
		} 
		
		return true;
	}
}


class MessageHandler 
{	
	public MessageHandler() 
	{
	}
	//encrypts messages before they sends
	public String encryptMessage(String msg, Account user)
	{
		String encrypted = "";
		if(msg.length() > 0)
		{
			try
			{		
				//getting key
				byte[] key = user.key.getBytes();
				SecretKeySpec sKey = new SecretKeySpec(key, "AES");
				
				//getting encryption cipher
				Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, sKey);			
				
				//encrypts message
				byte[] msgIn = msg.getBytes();
				byte[] msgOut = cipher.update(msgIn, 0, msgIn.length);
				byte[] temp = cipher.doFinal();
				
				byte[] result = new byte[msgOut.length + temp.length]; 
				System.arraycopy(msgOut, 0, result, 0, msgOut.length); 
				System.arraycopy(temp, 0, result, msgOut.length, temp.length); 
				
				encrypted = Base64.getEncoder().encodeToString(result);
			}
			catch (Exception e) { e.printStackTrace();}
		}
		return user.username + ": " + encrypted;
	}
	//encrypts messages 
	public String decryptMessage(String msg, Account user)
	{
		String decrypted = msg;
		boolean decryptedMsg = false;
		String username = "";
		byte[] key = null;
		if(msg.length() > 0)
		{
			try
			{
				//check if it uses the users key
				String[] messageSplit = msg.split(":");
				if(messageSplit[0].equals(user.username))
				{
					decryptedMsg = true;
					key = user.key.getBytes();
					username = user.username;
				}
				else
				{	
					//checks if it is sent from a friend and 
					//then gets the friends key to open the message
					key = user.getKey(messageSplit[0]);
					if(key != null)
					{
						decryptedMsg = true;
						username = messageSplit[0];
					}
				}
				
				//if message have a known sender the message gets de
				if(decryptedMsg)
				{
					//removes the senders name before decrypting
					String testp = msg.substring(username.length() + 2, msg.length());
					
					SecretKeySpec sKey = new SecretKeySpec(key, "AES");
					
					//getting decryption cipher
					Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
					cipher.init(Cipher.DECRYPT_MODE, sKey);
					
					//decrypting message
					byte[] msgIn = Base64.getDecoder().decode(testp.getBytes());
					byte[] msgOut = cipher.update(msgIn, 0, msgIn.length);
					byte[] temp = cipher.doFinal();
					
					byte[] myResult = new byte[msgOut.length + temp.length]; 
					System.arraycopy(msgOut, 0, myResult, 0, msgOut.length); 
					System.arraycopy(temp, 0, myResult, msgOut.length, temp.length);
					
					decrypted = new String(myResult);
					decrypted = username + ": " + decrypted;
				}				
			}
			catch (Exception e) { e.printStackTrace();}
		}
		return decrypted;
	}
	
}





