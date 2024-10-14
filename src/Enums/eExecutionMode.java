package Enums;


public enum eExecutionMode {
	     AGILE(0),CENTRALIZED(1),TRADE(2);
	     /* 
	      * AGILE Mode: Robots bids for tasks, each robot is assigned to a task, execution starts, after all robots become idle again, new auction is performed. 
	      * CENTRALIZED Mode: Robots bids for tasks, each robot is assigned to a task, auctions are repeated until all the tasks are assigned or all the robots 
	      *                   reach to their capacity limit. Then execution starts.    
 	      *TRADE Mode: After centralized allocation, robots can transfer their tasks to any other if benefits will be won. 
 	      *
 	      *
 	      */

	    private final int value;
	    private eExecutionMode (int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

}
