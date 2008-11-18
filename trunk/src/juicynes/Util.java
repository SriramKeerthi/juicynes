package juicynes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Util
{

	public static int[] bytesToInts(byte[] bytes)
	{
		int[] ints = new int[bytes.length];
		for(int i=0; i < bytes.length; i++)
		{
			ints[i] = (bytes[i]&0xFF);
		}
		
		return ints;
	}
	
	public static int[] bytesFromFile(String filename) throws IOException
	{
		File file = new File(filename);
		FileInputStream fis = new FileInputStream(file);
		
		byte[] bytes = new byte[(int)file.length()];
		fis.read(bytes);
		
		return bytesToInts(bytes);
	}
}
