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

public class DecryptHandler 
{
	public static void main (String[] args)
	{
		//paths to the key, encryptedtext and decryptedtext
		String encryptedDataPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.1/EncryptedData.txt";
		String keyPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.1/Key.txt";
		String decryptetDataPath = "C:/Users/Emma/Documents/distans-kurs-folder/4.1.1/DecryptetData.txt";
		
		if(args.length > 2)
		{
			//get paths to the key, encryptedtext and decryptedtext
			encryptedDataPath = args[0];
			keyPath = args[1];
			decryptetDataPath = args[2];
		}

		try
		{		
			//get key from file
			byte[] key = Files.readAllBytes(Paths.get(keyPath));
			SecretKeySpec sKey = new SecretKeySpec(key, "AES");
			
			//getting encryption cipher
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, sKey);
			
			//decrypting encryptedtext and saving it to file
			FileInputStream inFile = new FileInputStream(encryptedDataPath);
			FileOutputStream outFile = new FileOutputStream(decryptetDataPath);
			int length;
			byte[] inBuf = new byte[1024]; 
			while((length = inFile.read(inBuf)) != -1) 
			{
				byte[] outBuf = cipher.update(inBuf, 0, length);
                if ( outBuf != null ) 
					outFile.write(outBuf);
            }
			byte[] obuf = cipher.doFinal();
            if ( obuf != null ) 
				outFile.write(obuf);
			outFile.close(); 
			inFile.close(); 
		}
		catch (Exception e) { e.printStackTrace();}
		

	}	
}