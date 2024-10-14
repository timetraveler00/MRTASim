package MRTAStreamManager;

import MRTAmain.Task;
import MRTAmain.TaskSet;
public class TaskSetReader extends TaskReader {

	TaskSet ts ; 
	int setSize; 
	int setIndex;
	 
	
	public TaskSet GetTaskSet () 
	{
		ts = new TaskSet(setSize, setIndex); 
		for (int i=0; i<setSize; i++) 
		{
			Task t = GetTask(i);
			ts.AddTask(t);
		}
	    return ts;  		
	}
	public TaskSetReader (String template, int size, int index)  
	{
		super(template, "\\Experiments\\", "\\TaskSets\\TaskSet"+size+"_"+index+".txt");
		setSize = size;
		setIndex = index; 
		
	}
}
