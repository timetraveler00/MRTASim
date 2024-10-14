package MRTAmain;

import java.util.ArrayList;


import MRTAStreamManager.MRTAFileWriter;


public class TaskSet {
	
	private Task tasks [] ;
	private int taskCount; 
	private int maxTaskCount;
	private int idNumber ; 
	private int tIndex [] ;
	
/*	public TaskSet (int size, int id) 
	{
		
		//tasks = new Task [size];
		taskCount = 0;
		maxTaskCount = size; 
		idNumber = id; 
		
	}
*/	
	public TaskSet (int size, int id) 
	{
		
		tasks = new Task [size];
		taskCount = 0;
		maxTaskCount = size; 
		idNumber = id; 
		tIndex = new int[size]; 
		
		
	}
	public int GetIDNumber() 
	{
		return idNumber; 
	}
	public int GetTaskCount() 
	{
		return taskCount; 
	}
	
	public void AddTask (Task t) 
	{
		if (taskCount<maxTaskCount) 
		{
			tasks[taskCount++] = t; 
		}
		else 
		{
			System.out.println("TASKSET> Set k�mesi doldu, yeni �ye kabul edilmiyor : "+taskCount); 
		}
	}
	public Task GetTask (int index) 
	{
		return tasks[index]; 
	}
	
	public void SetTIndex (int indice, int value) 
	{
		tIndex[indice] = value; 
	}
	public int  GetTIndex (int indice) 
	{
		return tIndex[indice]; 
	}
	
	
	public void ToFile(String templateName) 
	{
	     String targetLoc =  "\\config\\"+ templateName+"\\Tasks.txt"; 
		 toFile (targetLoc);      
	     
	}
	public void ToFileAsStations(String templateName) 
	{
	    
		 String targetLoc =  "\\config\\"+ templateName+"\\Stations.txt"; 
		 toFile (targetLoc);      
	     
	}
	
	public void toFile(String targetLoc) 
	{
	   
		 ArrayList <String> lines = new ArrayList<String>();
	            
	            String wStr = ""; 
	            for (int i=0; i<taskCount; i++)
	            {
	            	
	            	Task t = GetTask(i); 
	            	wStr = "T"+Integer.toString(i)+"_"+t.xLoc + "_"+t.yLoc+"_"+t.creationTime;
	                lines.add(wStr);        
	      
	            }
	     MRTAFileWriter fw = new MRTAFileWriter(); 
	     fw.WriteToFile(targetLoc, lines);
		
	}
	
	public void ToExpFile(String templateName) 
	{
		 String targetLoc =  "\\Experiments\\"+ templateName+"\\TaskSets\\TaskSet"+taskCount +"_"+ idNumber +".txt"; 
		 toFile (targetLoc);      
		
	}
	
}
