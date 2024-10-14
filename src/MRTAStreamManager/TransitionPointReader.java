package MRTAStreamManager;

import java.util.ArrayList;

import IndoorStructure.TransitionPoint;
import MRTAmain.Task;






public class TransitionPointReader extends MRTAFileReader {
	
	
	protected ArrayList<TransitionPoint> transitionPointList = new ArrayList<TransitionPoint>(); 
    protected void CreateTransitionPointArray () 
    {
    	for (int i=0; i<lines.size(); i++) 
    	{
    		
    		TransitionPoint tp = new TransitionPoint(new Task (Integer.parseInt(Get(i,1)), Integer.parseInt(Get(i,2)), Get(i,0))); 
    		// 
    		transitionPointList.add (tp); 
 
    	}
    }
    
    public TransitionPoint GetTransitionPoint (int index) 
    {
    	return transitionPointList.get(index); 
    }
    
    public int TransitionPointCount () 
    {
    	return transitionPointList.size(); 
    }

		public TransitionPointReader(String template) {
			super();
			delimiter ="_";
			folderPath = "\\config\\";
			fileName = "TransitionPoints.txt"; 
			templateName = template; 
									
			FromFileToArray();
			CreateTransitionPointArray(); 
			
		}
		
		

	}