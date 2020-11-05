//4.1.2 Signering/verifiering med asymmetriska algoritmer och nyckelpar

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;  

public class SignHandler  
{
	public static void main(String[] args)
	{
		//paths
		String prKeyPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.2/PrKey.txt";
		String dataPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.2/Data.txt";
		String signaturePath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.2/Signature.txt";
		
		if(args.length > 2)
		{
			//getting paths
			dataPath = args[0];
			prKeyPath = args[1];
			signaturePath = args[2];
		}
		
		try
		{
			//getting privet key
			byte[] bPrKey = Files.readAllBytes(Paths.get(prKeyPath));
			PKCS8EncodedKeySpec prKeySpec = new PKCS8EncodedKeySpec(bPrKey);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			PrivateKey prKey = keyFactory.generatePrivate(prKeySpec);
			
			//initializeing signature
			Signature dsa = Signature.getInstance("SHA256withDSA", "SUN");
			dsa.initSign(prKey);
		
			//getting data to sign
			FileInputStream inFile = new FileInputStream(dataPath);
			byte[] inBuf = new byte[1024];
			int length;
			while ((length = inFile.read(inBuf)) != -1) 
			{
				dsa.update(inBuf, 0, length);
			}
			inFile.close();
			
			//signing data
			byte[] realSig = dsa.sign();
			FileOutputStream outFile = new FileOutputStream(signaturePath);
			System.out.println(realSig.length);
			outFile.write(realSig);
			outFile.close(); 
			
			System.out.println("signature done"); 
		}
		catch (Exception e) { e.printStackTrace();}
	}
}

