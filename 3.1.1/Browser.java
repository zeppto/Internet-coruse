
import java.net.*;
import java.io.*; 
import java.util.*; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Browser 
{
	public static void main(String[] args)
	{
		/*StringBuffer buffer = new StringBuffer(); 
		String line = "";
		URL url = new URL(urlString);
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		while((line = br.readLine()) != null) {
			buffer.append(line + "\n");
		}
		String text = buf.toString();*/
		
		String urlString = "https://people.dsv.su.se/~pierre/i/i.cgi?href=aa_public/home/main.txt";
		
		if(args.length > 0)
		{
			//getting host 
			urlString = args[0];
		}
		try
		{
			InputStream is = new URL(urlString).openStream();
			String text = new Scanner(is).useDelimiter("\\A").next();
			System.out.println(text);
		}
		catch (Exception e) { e.printStackTrace();}
	
	}
}


