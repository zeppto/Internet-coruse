//4.1.2 Signering/verifiering med asymmetriska algoritmer och nyckelpar

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.spec.X509EncodedKeySpec;  

public class VerifyHandler   
{
	public static void main(String[] args)
	{
		//paths
		String puKeyPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.2/PuKey.txt";
		String dataPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.2/Data.txt";
		String signaturePath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.2/Signature.txt";
		
		if(args.length > 2)
		{
			//getting paths
			dataPath = args[0];
			puKeyPath = args[1];
			signaturePath = args[2];
		}
		
		try
		{
			//getting public key
			byte[] bPuKey = Files.readAllBytes(Paths.get(puKeyPath));
			X509EncodedKeySpec puKeySpec = new X509EncodedKeySpec(bPuKey);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			PublicKey puKey = keyFactory.generatePublic(puKeySpec);
			
			//initializeing signature
			Signature dsa = Signature.getInstance("SHA256withDSA", "SUN");
			dsa.initVerify(puKey);
		
			//getting data to verifie
			FileInputStream inFile = new FileInputStream(dataPath);
			byte[] inBuf = new byte[1024];
			int length;
			while ((length = inFile.read(inBuf)) != -1) 
			{
				dsa.update(inBuf, 0, length);
			}
			inFile.close();
			
			//verifying data
			byte[] sign = Files.readAllBytes(Paths.get(signaturePath));
			boolean verifycation = false;
			if(sign.length == 63)
				verifycation = dsa.verify(sign);
			if(verifycation)
				System.out.println("verifyed!"); 
			else
				System.out.println("no verifyed!"); 
		}
		catch (Exception e) { e.printStackTrace();}
	}
}