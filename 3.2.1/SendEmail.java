//3.2.1 Epost-sändning

import java.util.*;  
import javax.mail.*;  
import javax.mail.internet.*;  
import javax.activation.*;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;  
  
public class SendEmail
{
	public static void main(String[] args)
	{
		String serverHost = "";
		String serverPort = "465";
		String username = "";
		String password = "";
		String senderEmail = "";
		String resiverEmail = "";
		String msgHeder = "";
		String msgText = "";
		
		//createing gui
		JFrame frame = new JFrame("E-mail");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(650, 660);
		frame.setLocation(300,200);
		
		//creat top panal
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

		//server panel
		JPanel serverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField serverTextField = new JTextField(47);
		JLabel serverLabel = new JLabel("Server:");
		serverLabel.setPreferredSize(new Dimension(90, 15));
		serverPanel.add(serverLabel);
		serverPanel.add(serverTextField);
		
		//username panel
		JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField usernameTextField = new JTextField(47);
		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setPreferredSize(new Dimension(90, 15));
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameTextField);
		
		//password panel
		JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField passwordTextField = new JPasswordField(47);
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setPreferredSize(new Dimension(90, 15));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordTextField);
		
		//from panel
		JPanel fromPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField fromTextField = new JTextField(47);
		JLabel fromLabel = new JLabel("From:");
		fromLabel.setPreferredSize(new Dimension(90, 15));
		fromPanel.add(fromLabel);
		fromPanel.add(fromTextField);
		
		//to panel
		JPanel toPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField toTextField = new JTextField(47);
		JLabel toLabel = new JLabel("To:");
		toLabel.setPreferredSize(new Dimension(90, 15));
		toPanel.add(toLabel);
		toPanel.add(toTextField);
		
		//subject panel
		JPanel subjectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField subjectTextField = new JTextField(47);
		JLabel subjectLabel = new JLabel("Subject:");
		subjectLabel.setPreferredSize(new Dimension(90, 15));
		subjectPanel.add(subjectLabel);
		subjectPanel.add(subjectTextField);
		
		//add to topPanel
		topPanel.add(serverPanel);
		topPanel.add(usernamePanel);
		topPanel.add(passwordPanel);
		topPanel.add(fromPanel);
		topPanel.add(toPanel);
		topPanel.add(subjectPanel);

		//text display
		JPanel middlePanel = new JPanel();
		JTextArea textArea = new JTextArea(23, 55);
		textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		middlePanel.add(scroll);
		textArea.setEditable(true);
		
		//sendButton panel
		JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton send = new JButton("Send");
		send.setPreferredSize(new Dimension(620, 40));
		sendPanel.add(send);
		
		//conenct to panel
		frame.getContentPane().add(BorderLayout.NORTH, topPanel);  
		frame.getContentPane().add(BorderLayout.CENTER, middlePanel);
		frame.getContentPane().add(BorderLayout.SOUTH, sendPanel);

		frame.setVisible(true);
		
		
		//send email
		send.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String serverHost = serverTextField.getText();				
				String username = usernameTextField.getText();
				String password = passwordTextField.getText();				
				String senderEmail = fromTextField.getText();				
				String resiverEmail = toTextField.getText();				
				String msgHeder = subjectTextField.getText();	
				String msgText = textArea.getText();
				
				Properties properties = new Properties();
				properties.put("mail.smtp.host", serverHost);
				properties.put("mail.smtp.socketFactory.port", serverPort);    
				properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");        
				properties.put("mail.smtp.port", serverPort);
				properties.put("mail.smtp.auth", "true");
				
				Session session = Session.getDefaultInstance(properties,new javax.mail.Authenticator() 
				{  
					protected PasswordAuthentication getPasswordAuthentication()
					{  
						return new PasswordAuthentication(username,password);  
					}  
				});  
				
				//Compose the message  
				try
				{  
					MimeMessage message = new MimeMessage(session);  
					message.setFrom(new InternetAddress(senderEmail));  
					message.addRecipient(Message.RecipientType.TO,new InternetAddress(resiverEmail));  
					message.setSubject(msgHeder);  
					message.setText(msgText);  
					   
					//send the message  
					Transport.send(message);  
				  
					System.out.println("successfully sent the message!");  
			   
				 }
				 catch (MessagingException me) {me.printStackTrace();}  
			}
		});
	}
}





 