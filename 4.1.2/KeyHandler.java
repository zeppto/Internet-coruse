//4.1.2 Signering/verifiering med asymmetriska algoritmer och nyckelpar

import java.security.*;
import java.io.FileOutputStream;
import java.io.IOException;

public class KeyHandler 
{
	public static void main(String[] args)
	{
		//paths
		String prKeyPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.2/PrKey.txt";
		String puKeyPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.2/PuKey.txt";
		
		if(args.length > 1)
		{
			//getting paths 
			prKeyPath = args[0];
			puKeyPath = args[1];
		}
		
		KeyPairGenerator keyGen = null;
		try
		{
			//getting key generator
			keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
			System.out.println("creating generator"); 
		}
		catch (Exception e) { e.printStackTrace();}
		
		//generating key pair
		KeyPair pair = keyGen.generateKeyPair();
		System.out.println("generating key pair"); 
		PrivateKey pr = pair.getPrivate();
		PublicKey pu = pair.getPublic();
		
		byte[] prKey = pr.getEncoded();
		byte[] puKey = pu.getEncoded();
		
		try
		{	
			//save key to file
			FileOutputStream outFile = new FileOutputStream(prKeyPath);
			outFile.write(prKey);
			outFile.close(); 
			
			//save key to file
			outFile = new FileOutputStream(puKeyPath);
			outFile.write(puKey);
			outFile.close(); 
		}
		catch (IOException e) { e.printStackTrace();}
		System.out.println("saved key pair in two folders"); 
	}
}



