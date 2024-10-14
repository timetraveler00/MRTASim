package MRTAmain;

import java.util.ArrayList;

import MRTAStreamManager.MRTAFileWriter;

public class StationSet 
	{
		public int setSize ;
		public int stCoor [][] ;
		public int totalWay ;
		public int maxWay ;
		public int stIndex [];
		public int initialTime; 
		public int tradeTime; 
		public int schedulingTime; 
		public int pickUpTime; 
		public int execTime;
		public int maxRobotime; 
		public int totalTime;
		public int idNumber; 
		public Station stations[]; 
				 
		
		/*public StationSet() 
		{
			setSize = 5; 
			stCoor = new int [setSize][2];
			totalWay =0; 
			maxWay = 0; 
			stIndex = new int[setSize];
			
			
		}*/
		
		public StationSet(int stsz) 
		{
			setSize = stsz;
			stCoor = new int [setSize][2];
			totalWay =0;
			maxWay = 0;
			stIndex = new int[setSize]; 
			idNumber = 0; 
			stations = new Station [setSize]; 
		}
		
		public void SetID (int idn) 
		{
			idNumber = idn; 
		}
		
		public int TotalTime () 
		{
			totalTime = initialTime + tradeTime + maxRobotime; 
			return   totalTime; 
			
		}
		
		public void ToFileAsRobots(String templateName) 
		{
		   
			 ArrayList <String> lines = new ArrayList<String>();
			 String targetLoc =  "\\config\\"+ templateName+"\\Robots.txt";  
		            
		            String wStr = ""; 
		            
		            for (int i=0; i<setSize; i++)
		            {
		            	
		            	Station s = stations[i];  
		            	wStr ="R"+Integer.toString(i)+"_"+s.xLoc + "_"+s.yLoc+"_0"+"_"+s.rect.x1+"_"+s.rect.y1+"_"+s.rect.x2+"_"+s.rect.y2; 
		                lines.add(wStr);        
		      
		            }
		     MRTAFileWriter fw = new MRTAFileWriter(); 
		     fw.WriteToFile(targetLoc, lines);
			
		}
		public void ToFileAsStations(String templateName) 
		{
		   
			 ArrayList <String> lines = new ArrayList<String>();
			 String targetLoc =  "\\config\\"+ templateName+"\\Stations.txt";  
		            
		            String wStr = ""; 
		            
		            for (int i=0; i<setSize; i++)
		            {
		            	
		            	Station s = stations[i];  
		            	wStr ="S"+Integer.toString(i)+"_"+s.xLoc + "_"+s.yLoc; 
		                lines.add(wStr);        
		      
		            }
		     MRTAFileWriter fw = new MRTAFileWriter(); 
		     fw.WriteToFile(targetLoc, lines);
			
		}
		
		public void ToFile(String templateName) 
		{
		     
	            ArrayList<String> strList = new ArrayList<String>(); 
	            
	            for (int i=0; i<setSize; i++)
	            {
	            	
	            	strList.add("S"+Integer.toString(i)+"_"+ stCoor[i][0] + "_"+ stCoor[i][1]);  
	      
	            }
	            
	            
	            String target =  "\\Experiments\\"+ templateName+"\\StationSets\\StationSet"+setSize +"_"+ idNumber +".txt" ;
	            
	            new MRTAFileWriter().WriteToFile(target, strList);
			
		}
		
	}