package MRTAmain;

import Enums.eTaskState;
import Enums.eTradeStatus;

 public class Task 
    {
         public int xLoc;
         public int yLoc;
         public String taskName;
         public eTaskState state ;       
         public boolean mineTask;
         public int taskType = 0;
         public int completedTasks; 
         public String nearestTask ;
         public int owner; 
         public int sold; 
         public int price;
         public int creationTime; 
         public String bundleHead; 
         public String ownerName; 
         public boolean nearestFlag = false; 
         public int cluster; 
         public Station closest = null; 
         
       
         
   	  public eTradeStatus tradeStatus ; 
      public boolean traded = false;  
      public int index; 

         /**
          * TRADE STATUS
          * 0 : Nothing done yet
          * 1 : call for proposals
          * 2 : sold
          * 3 : bought 
          * 
          * 
          * /
          

         /*
          * TASK TYPE
          * 0 : Temporary
          * 1 : Charging unit1
          */
         
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
          * 
          */
         
         public int proposal; // robotAgent kullanacak. 
    	
    	 public Task() 
         {
        	 xLoc = 10; 
        	 yLoc = 10; 
        	 taskName = "taskName";
        	
        	 taskType = 0; 
        	 completedTasks = 0;
        	 creationTime = 0 ; 
        	 nearestTask = ""; 
        	 owner = -1 ;
        	 bundleHead = taskName; 

         }
         
    	 public Task(int xl, int yl, String tName) 
         {
        	 xLoc = xl; 
        	 yLoc = yl; 
        	 taskName = tName;
        	 state= eTaskState.NOT_STARTED; 

        	 taskType = 0; 
        	 completedTasks = 0; 
        	 nearestTask = ""; 
        	 owner = -1 ; 
        	 creationTime = 0 ; 
        	 bundleHead = taskName; 

         }
    	 

    	 
    	 public Task(Task t) 
         {
        	 xLoc = t.xLoc; 
        	 yLoc = t.yLoc; 
        	 taskName = t.taskName;

        	 taskType = t.taskType; 
        	 completedTasks = t.completedTasks; 
        	 nearestTask = t.nearestTask; 
        	 owner = t.owner ; 
        	 bundleHead = t.bundleHead; 

         }
    	 
    	 public Task(int xl, int yl, String tName, int tType) 
         {
        	 xLoc = xl; 
        	 yLoc = yl; 
        	 taskName = tName;

        	 taskType = tType; 
        	 completedTasks = 0; 
        	 nearestTask = "";
        	 owner = -1 ; 
        	 bundleHead = taskName; 

         }
    	 public Task(Robotum r) 
         {

        	 this (r.xLoc, r.yLoc, r.robotName, 0); 
        	 
         }
    	 
    	 
    	 public Task(int xl, int yl, String tName , int tType, int crTime) 
         {
    		 this (xl, yl, tName, tType); 
    		 creationTime = crTime;  

         }
    	  	 

    } 