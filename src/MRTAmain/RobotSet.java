package MRTAmain;

/*public class RobotSet {
	private int robotCount, rCount ;
	private String rc[];  
	public RobotSet (int robc) 
	{
		robotCount = robc ; 
		rc = new String[robotCount];
		rCount = 0; 
	}

	public String GetRobotName (int i)
	{
		return rc[i]; 
	}
	public void SetRobotName (String name) 
	{
		rc[rCount++] = name; 
		
	}
	public int GetRobotCount ( ) 
	{
		return rCount; 
	}
	public String [] GetRobotList() 
	{
		return rc;
	}
	
}*/ 



import java.util.ArrayList;

import Enums.eRobotStatus;
import MRTAStreamManager.MRTAFileWriter;


public class RobotSet {
	
	private Robotum robots [] ;
	private int robotCount; 

	
/*	public TaskSet (int size, int id) 
	{
		
		//tasks = new Task [size];
		taskCount = 0;
		maxTaskCount = size; 
		idNumber = id; 
		
	}
*/	
	
	public String GetRobotName (int i)
	{
		return robots [i].robotName; 
	}
	
	
	
	public void SetRobotName (String name) 
	{
		Robotum r= new Robotum(); 
		r.robotName = name; 
	
        AddRobot(r);		

		  
		
	}
	

	public String [] GetRobotList() 
	{
		String [] robotList = new String [GetRobotCount()];	
		for (int i=0;i<GetRobotCount(); i++) 
			robotList[i] = robots[i].robotName; 
		
		return robotList;
				
	}
	
	
	public RobotSet (int size) 
	{
		
		robots = new Robotum [size];
		robotCount = 0;
		
	}

	public int GetRobotCount() 
	{
		return robotCount; 
	}
	
	public void AddRobot (Robotum r) 
	{
	
			robots[robotCount++] = r; 
	
	}
	public Robotum GetRobot (int index) 
	{
		return robots[index]; 
	}
	
	public Station [] GetStations() 
	{
		Station [] stations = new Station[robotCount]; 
		for (int i=0; i<robotCount; i++) 
		{
			Station s = new Station();
			Robotum r = GetRobot(i); 
			s.rect = r.rect;
			s.xLoc = r.xLoc;
			s.yLoc = r.yLoc; 
			stations[i] = s;
			
		}		
		
		return stations; 
	}
	
	public void ToFileAsStations (String templateName) 
	{
	   
		 ArrayList <String> lines = new ArrayList<String>();
		 String targetLoc =  "\\..\\..\\config\\"+ templateName+"\\Stations.txt";  
	            
	            String wStr = ""; 
	            
	            for (int i=0; i<robotCount; i++)
	            {
	            	
	            	Robotum r = GetRobot(i); 
	            	wStr ="S"+Integer.toString(i)+"_"+r.xLoc + "_"+r.yLoc; 
	                lines.add(wStr);        
	      
	            }
	     MRTAFileWriter fw = new MRTAFileWriter(); 
	     fw.WriteToFile(targetLoc, lines);
		
	}
	
	public void ToFile(String templateName) 
	{
	   
		 ArrayList <String> lines = new ArrayList<String>();
		 String targetLoc =  "\\..\\..\\config\\"+ templateName+"\\Robots.txt";  
	            
	            String wStr = ""; 
	            
	            for (int i=0; i<robotCount; i++)
	            {
	            	
	            	Robotum r = GetRobot(i); 
	            	wStr ="R"+Integer.toString(i)+"_"+r.xLoc + "_"+r.yLoc+"_0"+"_"+r.rect.x1+"_"+r.rect.y1+"_"+r.rect.x2+"_"+r.rect.y2; 
	                lines.add(wStr);        
	      
	            }
	     MRTAFileWriter fw = new MRTAFileWriter(); 
	     fw.WriteToFile(targetLoc, lines);
		
	}
}

