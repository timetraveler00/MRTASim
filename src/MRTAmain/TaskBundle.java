package MRTAmain;

import Enums.eTaskState;
import Enums.eTradeStatus;



public class TaskBundle {
	
    /*
     * TASK STATE 
     * 0: Idle
     * 1: Processing
     * 2:  Completed
     * 3: Canceled
     * 4: Charging (CU) 
     * 5: Disabled (CU) 
     * 6: Bought
     * 7: Sold (robot task)  
     * 9: Not activated
     * 10 : Offered
     */  
	  public eTaskState state ;          
	  public eTradeStatus tradeStatus ; 
      public boolean traded = false;  
      public int price;
      public int owner;
      public int taskCount = 0; 
      public int bundleIndex = 0; // for coloring purposes at terrain
      public boolean offered = false;  
  public int cluster ; 
      
      public int proposal; // robotAgent kullanacak. 

    public Task ls [] ;    
    		
      
  	public TaskBundle ()
  	{
  		ls = new Task[100]; 
  		taskCount = 0; 
  		state = eTaskState.NOT_STARTED; 
  		tradeStatus = eTradeStatus.OWNERLESS; 
  		traded = false ; 
  		owner = -1; 
  		offered = false; 
  		
  	}
  	public void AddTask(Task t)
  	{
  		ls[taskCount++] = t; 
  	}
  	public void RemoveTask (Task t) 
  	{
  		int tIndex = TaskIndex(t.taskName) ; 
  		for (int i=tIndex; i<taskCount-1; i++) 
  		{
  			ls[i] = ls[i+1] ;  
  					 
  		}
  		taskCount--; 
  		
  	}
  	public void RemoveTask(int index)
  	{
  		 
  		for (int i=index; i<taskCount-1; i++) 
  		{
  			ls[i] = ls[i+1] ;  
  					 
  		}
  		taskCount--; 
  	}
  	public void ListTasks ()
  	{
  		
  		for (int i=0; i<taskCount; i++) 
  		{
  			System.out.println("TaskName ( :"+ Integer.toString(i) + " ) : " +ls[i].taskName);
  		}
  	}
  	public Task GetTask(int index) 
  	{
  		return ls[index];
  	}
  	
  	public Task GetTask(String tName) 
  	{
  		return ls[TaskIndex(tName)];
  	}
  	
  	public int taskCount () 
  	{
  		return taskCount;
  	}
  	public int TaskIndex (String tName) 
  	{
  		for (int i=0; i<taskCount; i++) 
  		{
  			if (ls[i].taskName.compareTo(tName) == 0) 
  			    {
  				    return i; 
  			    }
  		}
  		return -1; 
  	}
  	public void SetTask (int index, Task t) 
  	{
  		ls[index] = t  ; 
  	}
  	
  	public void AddTask (int index, Task t) 
  	{
  		ls[index] = t  ;
  		taskCount++;
  	}
  	
  	
  	public int CalculateCost () 
  	{
  		//return (int) LocalPathLength()+ taskCount* 10;
  		
  		return 0; 
  		
  	}
	


}
