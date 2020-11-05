//4.1.1 Kryptering/dekryptering med symmetriska algoritmer

import java.util.*; 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.FileInputStream;
 
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class EncryptHandler
{
	public static void main (String[] args)
	{
		//paths to the key, plaintext and encryptedtext
		String dataPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.1/Data.txt";
		String keyPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.1/Key.txt";
		String encryptedDataPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.1/EncryptedData.txt";
		
		if(args.length > 2)
		{
			//get paths to the key, plaintext and encryptedtext
			dataPath = args[0];
			keyPath = args[1];
			encryptedDataPath = args[2];
		}

		try
		{		
			//get key from file
			byte[] key = Files.readAllBytes(Paths.get(keyPath));
			SecretKeySpec sKey = new SecretKeySpec(key, "AES");
			
			//getting encryption cipher
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, sKey);			
			
			//encrypting plaintext and saving it to file
			FileInputStream inFile = new FileInputStream(dataPath);
			FileOutputStream outFile = new FileOutputStream(encryptedDataPath);
			int length;
			byte[] inBuf = new byte[1024]; 
			while((length = inFile.read(inBuf)) != -1) 
			{
				byte[] outBuf = cipher.update(inBuf, 0, length);
                if ( outBuf != null ) 
					outFile.write(outBuf);
            }
			byte[] outBuf = cipher.doFinal();
            if ( outBuf != null ) 
			{
				outFile.write(outBuf);
			}
			outFile.close(); 
			inFile.close(); 
			
		}
		catch (Exception e) { e.printStackTrace();}
		

	}	
}