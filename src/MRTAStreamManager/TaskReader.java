package MRTAStreamManager;

import java.util.ArrayList;

import MRTAmain.Task;


public class TaskReader extends MRTAFileReader {
	
	
	protected ArrayList<Task> taskList = new ArrayList<Task>(); 
    protected void CreateTaskArray () 
    {
    	for (int i=0; i<lines.size(); i++) 
    	{
    		int st = 0; 
    		if ( Get(i,3)!=null )
    		     st = Integer.parseInt(Get(i,3)) ; 
    		
    		      Task t = new Task (Integer.parseInt(Get(i, 1)), Integer.parseInt(Get(i, 2)), Get (i,0), 0, st);
    		      taskList.add(t);   		
 
    	}
    }
    
    public Task GetTask (int index) 
    {
    	return taskList.get(index); 
    }
    
    public int TaskCount () 
    {
    	return taskList.size(); 
    }

		public TaskReader(String template) {

			
			this (template, "Tasks.txt") ;
		}
		
		public TaskReader(String template, String filename) {
			
			this (template, "\\config\\",filename) ;
		}
		
		public TaskReader(String template, String folderpath, String filename) {
			super();
			delimiter ="_";
			folderPath = folderpath;
			fileName = filename; 
			templateName = template; 
			
			System.out.println (folderpath + " - " + filename + " - " + template);
			
			FromFileToArray();
			CreateTaskArray(); 
			
		}
		
		

	}
