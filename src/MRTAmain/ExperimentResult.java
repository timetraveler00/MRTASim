package MRTAmain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import Enums.eBidValuation;
import Enums.eInitialTaskAssignment;
import Enums.eServiceArea;
import MRTAStreamManager.MRTAFileWriter;


public class ExperimentResult {

	public TaskSet taskSet ; 
	public StationSet stationSet; 
	
	public eBidValuation approachSelection; 
	public eBidValuation iapproachSelection;
	public String dtstr = ""; 
	public int heatMode = 0; 
	
	
	public int totalWay ;
	public int maxWay ;
	public int initialTime; 
	public int tradeTime; 
	public int schedulingTime; 
	public int pickUpTime; 
	public int execTime;
	public int maxRobotime; 
	public int totalTime;
	public int idNumber;
	public int heatMapCreationTime; 
	public String templateName; 
	public int ucCapacity; 
	public int tradeCapacity; 
	public eServiceArea expMode ; 
	public int heatRadius ; 
	public int heatTasks; 
	public int heatDist; 
	public int autonomyMode = 3; 
	public String setupStr ;
	
	public eInitialTaskAssignment initialAssignment ; 
	public String approaches[] = {"Euclidian", "Fuzzy", "DStarLite", "DStarLiteFuzzy","RRT", "TPM - TSP"};
	public String assignmentMethods[] = {"Random", "Greedy","Optimal","PRIM"};
	  

	public int TotalTime () 
	{
		totalTime = heatMapCreationTime + initialTime + tradeTime + maxRobotime; 
		return   totalTime; 
		
	}
	
	public String ExperimentMetrics () 
	{
	
	   if (stationSet== null) 
	   {
		   stationSet = new StationSet(1);
	   }
	   if (taskSet == null)
	   {
		   taskSet = new TaskSet(1, 0);
	   }
	
		System.out.println("idNumber: "+idNumber);
		System.out.println("dtstr: "+dtstr);
		System.out.println("setupStr: "+setupStr);
		System.out.println("templateName: "+templateName);
		System.out.println("autonomyMode: "+autonomyMode);
		System.out.println("approach: "+approachSelection);
		System.out.println("initialAssignment: "+initialAssignment);
		System.out.println("expMode: "+expMode);
		System.out.println("heatRadius: "+heatRadius);
		System.out.println("heatTasks: "+heatTasks);
		System.out.println("heatDist: "+heatDist);
		System.out.println("ucCapacity: "+ucCapacity);
		System.out.println("tradeCapacity: "+tradeCapacity);
		//System.out.println("stationSet.idNumber: "+stationSet.idNumber);
		System.out.println("stationSet.setSize: "+stationSet.setSize);
		System.out.println("taskSet.GetIDNumber(): "+taskSet.GetIDNumber());
		System.out.println("taskSet.GetTaskCount(): "+taskSet.GetTaskCount());
		System.out.println("totalWay: "+totalWay);
		System.out.println("maxWay: "+maxWay);
		System.out.println("heatMapCreationTime: "+heatMapCreationTime);
		System.out.println("initialTime: "+initialTime);
		System.out.println("tradeTime: "+tradeTime);
		System.out.println("schedulingTime: "+schedulingTime);
		System.out.println("pickUpTime: "+pickUpTime);
		System.out.println("execTime: "+execTime);
		System.out.println("maxRobotime: "+maxRobotime);
		System.out.println("TotalTime(): "+TotalTime());
		
		
		return idNumber+";"+ 
		       dtstr+";"+
		       setupStr+";"+
		       templateName+";"+
		       autonomyMode+";"+
		       approachSelection+";"+
		       initialAssignment+";"+
		       expMode +";" +
		       heatRadius+";"+
		       heatTasks+";"+
		       heatDist+";"+
		       ucCapacity+"--"+
		       tradeCapacity+";"+
		       /*stationSet.idNumber*/ "0"+ ";" +
		       stationSet.setSize+"S;"+ 
		       taskSet.GetIDNumber()+";"+
		       taskSet.GetTaskCount()+"T;"+
   
		       totalWay+";"+
		       maxWay+";"+
		       heatMapCreationTime+";"+
		       initialTime+";"+
		       tradeTime+";"+
		       schedulingTime+";"+
		       pickUpTime+";"+
		       execTime+";"+
		       maxRobotime+";"+
		       TotalTime();  
		
	}
	
	public BufferedWriter WriteLine (BufferedWriter bw, String str) throws IOException 
	{
    	 
		bw.write(str); 
		 bw.newLine();
		 return bw; 
	}
	
	public void ToFile (String templateName) 
	{
		
	  
		  DateTimeStr dt = new DateTimeStr(); 
	  	            
	            ArrayList<String> strList = new ArrayList<String>(); 
	            strList.add("---------------------------SUMMARY---------------------------------------------------");
	            strList.add(ExperimentMetrics());  
	            strList.add("---------------------------STATIONS---------------------------------------------------");
	            for (int i=0; i<stationSet.setSize  ; i++)
	            {
	            	strList.add( "S"+Integer.toString(i)+";"+stationSet.stCoor[i][0] + ";"+stationSet.stCoor[i][1]);
	            }
	            strList.add("---------------------------TASKS---------------------------------------------------");
	            for (int i=0; i<taskSet.GetTaskCount() ; i++)
	            {
	            	Task t = taskSet.GetTask(i);
	            	strList.add( "T"+Integer.toString(i)+";"+t.xLoc+";"+t.yLoc);
	            }
	            strList.add("-------------------------------------------------------------------------------------");
	            
	            String target = "\\Experiments\\"+ templateName+"\\Results_All\\"+ dt.DtString()+"_"+approachSelection+"_"+stationSet.setSize+"S_"+taskSet.GetTaskCount()+"T_"+totalWay+"_"+maxWay+"_"+heatMapCreationTime+"_"+initialTime+"_"+tradeTime+"_"+schedulingTime+"_"+pickUpTime+"_"+execTime+"_"+maxRobotime+"_"+TotalTime()+".txt"; 
	            
	            new MRTAFileWriter().WriteToFile(target, strList);
	            
	            		
	}
	
}
