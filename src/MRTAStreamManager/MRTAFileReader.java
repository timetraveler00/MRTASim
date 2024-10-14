package MRTAStreamManager;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MRTAFileReader {

	// Be carful to update for every update
	public String rootDirectory = "sdfsfD:\\EWS2\\";
	public String version = "sdfsMRTA40";
	public String absolutePath = rootDirectory + version; 
	public String logDirectory ;
	
	protected String folderPath; 
	protected String templateName; 
	protected String fileName;  
	protected String delimiter; 
	protected String fullPath; 
		
	 
	protected  ArrayList <String> lineStrings = new ArrayList<String>();
	protected  ArrayList <ArrayList<String>> lines = new ArrayList<ArrayList<String>>();
	
	


public void GetDirectory() {
	        String filePath = "C:\\MRTAFolder.txt"; // File path
	        String line;

	        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	            while ((line = br.readLine()) != null) {
	                // Split the line by "_" and print each parameter
	                String[] parameters = line.split("_");
	                
	                for (String parameter : parameters) {
	                    System.out.println(parameter);
	                }
	                System.out.println("-----"); // Separator for each line
	                rootDirectory = parameters[0];
	                version = parameters[1];
	                logDirectory = parameters[2]; 
	                absolutePath = rootDirectory + version; 
	                // System.out.println(absolutePath);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }


	
	public int RowSize (ArrayList<String> mRow)
	{
		int rowSize = (int) mRow.size(); 
		if (rowSize <0 || rowSize>32767) 
			return 0; 
		return rowSize; 
		
	}

	
	
	protected String Get (int row,int col)
	{
		if ( RowSize(lines.get(row)) >  col)
     		return lines.get(row).get(col);
		else return null; 
	}
	
	public int LineCount() 
	{
		int retVal = (int) lines.size();
		if (retVal<0 || retVal>32767)
 		   retVal =  0;
		return retVal;
		
	}
	
	protected ArrayList <String> Get (int row)
	{
		return lines.get(row); 		
	}
	
	public void FromFileToArray () 
	{
		GetDirectory();
		//System.out.println(rootDirectory + " -  " + version) ; 
		FileInputStream fstream = null;
		try
		{
		File f = new File(".");
		//System.out.println(f.getAbsolutePath() );
		
		//System.out.println(f.getAbsolutePath() + folderPath + templateName + fileName);
		//fstream = new FileInputStream(f.getAbsolutePath() + folderPath + templateName +"\\" + fileName );
		fstream = new FileInputStream(absolutePath + folderPath + templateName +"\\" + fileName );
		DataInputStream   in = new DataInputStream(fstream);
		InputStreamReader isr = new InputStreamReader(in); 
		BufferedReader  br = new BufferedReader(isr);
		String strLine; 
		   while ((strLine = br.readLine()) != null)   {

			         String [] temp; 
					 temp = strLine.split(delimiter);
					 ArrayList <String> lineStrings = new ArrayList<String>();
					 for (int i=0; i<temp.length; i++) 
			         {
			        	 lineStrings.add(temp[i]); 
			         }
					 
					 lines.add(lineStrings);

		   };
		   
		  fstream.close();
		  fstream = null ; 
	}   
	catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	 
 }catch (Exception e){//Catch exception if any
System.err.println("Read Error: " + e.getMessage());
}
finally 
{
    fstream = StreamHelper.cleanClose (fstream); 
}
		
		
	}
	
	
	
	
}
