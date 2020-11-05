
import java.util.*; 
import java.io.*;  
import java.net.*;  
import java.sql.*;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class GBook
{ 
    public static void main(String args[]) 
    { 
		String computer = "atlas.dsv.su.se";
		String db_name = "db_20232153";
		String url = "jdbc:mysql://" + computer + "/" + db_name;
		String username = "usr_20232153";
		String password = "232153";
		
        try
        { 
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			Connection dbConnection = DriverManager.getConnection(url, username, password);
			
			GUIClass gui = new GUIClass();
			
			Statement stmt = dbConnection.createStatement(); 
              
            // select to get info from gestBook
            String q1 = "select * from gestBook"; 
            ResultSet rs = stmt.executeQuery(q1); 
            while(rs.next())
            {
				gui.uppdateText("Name: " + rs.getString(1));
				gui.uppdateText("E-mail: " + rs.getString(2) + "   Homepage: " + rs.getString(3));
				gui.uppdateText("comments: " + rs.getString(4));
				gui.uppdateText("");
            }
              
            dbConnection.close(); 
		}
		catch(Exception e) 
        { 
            System.out.println(e); 
        } 
    } 
}



//thread for gui elements
class GUIClass extends Thread 
{
	String massage = "";
	JTextArea textArea;
	
	public GUIClass() 
	{
		//createing gui
		JFrame frame = new JFrame("guestbook");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(450, 400);
		frame.setLocation(300,200);
		
		//creat top panal
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

		//name panel
		JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField nameTextField = new JTextField(20);
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setPreferredSize(new Dimension(70, 15));
		namePanel.add(nameLabel);
		namePanel.add(nameTextField);
		
		//email panel
		JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField emailTextField = new JTextField(20);
		JLabel emailLabel = new JLabel("Email:");
		emailLabel.setPreferredSize(new Dimension(70, 15));
		emailPanel.add(emailLabel);
		emailPanel.add(emailTextField);
		
		//homepage panel
		JPanel homepagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField homepageTextField = new JTextField(20);
		JLabel homepageLabel = new JLabel("Homepage:");
		homepageLabel.setPreferredSize(new Dimension(70, 15));
		homepagePanel.add(homepageLabel);
		homepagePanel.add(homepageTextField);
		
		//comments panel
		JPanel commentsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton send = new JButton("Send");
		JTextField commentsTextField = new JTextField(20);
		JLabel commentsLabel = new JLabel("Comments:");
		commentsLabel.setPreferredSize(new Dimension(70, 15));
		commentsPanel.add(commentsLabel);
		commentsPanel.add(commentsTextField);
		commentsPanel.add(send);
		
		//add to topPanel
		topPanel.add(namePanel);
		topPanel.add(emailPanel);
		topPanel.add(homepagePanel);
		topPanel.add(commentsPanel);
		
		//text display
		JPanel middlePanel = new JPanel();
		textArea = new JTextArea(18, 37);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		middlePanel.add(scroll);
		textArea.setEditable(false);
		
		//conenct to panel
		frame.getContentPane().add(BorderLayout.NORTH, topPanel);  
		frame.getContentPane().add(BorderLayout.CENTER, middlePanel);

		frame.setVisible(true);
		
		
		//send massage
		send.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String name = nameTextField.getText();				
				nameTextField.setText("");
				String email = emailTextField.getText();				
				emailTextField.setText("");
				String homepage = homepageTextField.getText();				
				homepageTextField.setText("");
				String comments = commentsTextField.getText();				
				commentsTextField.setText("");
				
				
				try
				{
					Connection dbConnection = 
					DriverManager.getConnection("jdbc:mysql://atlas.dsv.su.se/db_20232153", "usr_20232153", "232153");
					
					Statement stmt = dbConnection.createStatement();
					
					//censuring input
					name = censurHtml( name, "(censur)" );
					email = censurHtml( email, "(censur)" );
					homepage = censurHtml( homepage, "(censur)" );
					comments = censurHtml( comments, "(censur)" );
					
					 //inserting data in database 
					String q1 = "insert into gestBook values('" +name+ "', '" +email+  
										  "', '" +homepage+ "', '" +comments+ "')"; 
					stmt.executeUpdate(q1); 
					
					//displaying user input
					uppdateText("Name: " + name);
					uppdateText( "E-mail: " + email + "   Homepage: " + homepage);
					uppdateText("comments: " + comments);
					uppdateText("");					

				}
				catch(Exception ex) 
				{ 
					System.out.println(ex); 
				} 
			}
		});
	}
	
	public void uppdateText(String msg)
	{
		textArea.append(msg + "\n");
	}

	String censurHtml( String str, String censored )
	{
		return str.replaceAll( "<.*?>", censored )
		.replaceAll( "<",  ""   )
		.replaceAll( ">",  ""   );   
	}
}




 