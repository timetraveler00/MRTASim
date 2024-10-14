package MRTAStreamManager;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class StreamHelper {

	public static FileInputStream cleanClose(FileInputStream fstream) 
	{
		if (fstream!= null) 
		{
			try {
				fstream.close();
			}
			catch (Exception e) 
			{
				//do sth
			}
		}
		return null; 
	}
	
	public static FileOutputStream cleanClose(FileOutputStream fstream) 
	{
		if (fstream!= null) 
		{
			try {
				fstream.close();
			}
			catch (Exception e) 
			{
				//do sth
			}
		}
		return null; 
	}
	
	public static BufferedWriter cleanClose(BufferedWriter fstream) 
	{
		if (fstream!= null) 
		{
			try {
				fstream.close();
			}
			catch (Exception e) 
			{
				//do sth
			}
		}
		return null; 
	}
	
}
