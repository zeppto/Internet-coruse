//4.1.1 Kryptering/dekryptering med symmetriska algoritmer

import java.util.*; 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
 
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class KeyHandler
{
	public static void main (String[] args)
	{
		//the path to the key file
		String keyPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.1/Key.txt";
		if(args.length > 0)
		{
			//get the key file
			keyPath = args[0];
		}
		byte[] key = new byte[1]; 
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
			//save key to file
			FileOutputStream outFile = new FileOutputStream(keyPath);
			outFile.write(key);
			outFile.close(); 
		}
		catch (IOException e) { e.printStackTrace();}
	}	
}