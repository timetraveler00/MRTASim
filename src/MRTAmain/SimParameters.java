package MRTAmain;


import Enums.eBidValuation;
import Enums.eExecutionMode;
import Enums.eInitialTaskAssignment;
import Enums.eLocalScheduling;
import Enums.ePathStyle;
import Enums.eServiceArea;
import Enums.eTSPLoopMode;


public class SimParameters {


	public String setupSelection; 

	/*
	 * 
	 * 			setupSelector = new Choice (); 
			setupSelector.addItem("MS"); 
			setupSelector.addItem("ASP1");
			setupSelector.addItem("ASP2"); 
			setupSelector.addItem("ASP3"); 
			setupSelector.addItem("NS1");
			setupSelector.addItem("HippoBots"); 			
			setupSelector.addItem("FollowMe");
			setupSelector.addItem("FODFocus"); 
			setupSelector.addItem("Custom");
			setupSelector.addItem("Random"); 
			setupSelector.addItem("Automation");
			setupSelector.addItem("Parametric"); 
			SetupSelection ss = new SetupSelection(); 
			setupSelector.addItemListener(ss); 	

	 		setupSelector.select(0); 
	 */
	
	
	public String templateSelection;
	
	public eServiceArea expMode; 
	/*
	expModeSelector = new Choice(); 
	expModeSelector.add ("");
	expModeSelector.add ("Rectangular");
	expModeSelector.add ("2");
	expModeSelector.add ("3");
	expModeSelector.add ("Extended rectangular");
	expModeSelector.add ("Circular");
	expModeSelector.select(4); 
	expModeSelector.addItemListener(new ExpModeSelection());
    */ 
	public eExecutionMode executionModeSelection ;
	/*
	executionModeSelector.addItem("DYNAMIC OPTIMAL"); 
		executionModeSelector.addItem("OPTIMAL + TRADE-BASED"); 
		executionModeSelector.addItem("OPTIMAL + SCHEDULED"); 
		executionModeSelector.addItem("SEQUENTIAL - GROUP");
		executionModeSelector.addItem("SEQUENTIAL - DISCRETE");
	
	*/
	public eBidValuation approachSelection;
	public eBidValuation iapproachSelection;
	
	/*
	 * iapproachSelector = new Choice (); 
 		iapproachSelector.addItem("Euclidian"); 1
 		iapproachSelector.addItem("Fuzzy"); 2
 		iapproachSelector.addItem("D*Lite"); 3
 		iapproachSelector.addItem("D*Lite Fuzzy"); 4
 		iapproachSelector.addItem("RRT"); 5
 		iapproachSelector.addItem("TPM - TSP"); 6
 		iapproachSelector.addItem("RRT - TSP"); 7
 		iapproachSelector.addItem("D*L - TSP"); 8 
 		iapproachSelector.addItem("Hybrid (TPMTSP + Euclidian)") ; 9
 		iapproachSelection = 3; 
 		iapproachSelector.select(2); 
 		iapproachSelector.addItemListener(new iApproachSelection()); 		
 		SelectInitialTA(); 
	 * */
	
	public eInitialTaskAssignment initialTaskAssignmentMethod ; 
	/*
	 * initialTaskAssgnSelector = new Choice(); 
		initialTaskAssgnSelector.addItem("Random");
		initialTaskAssgnSelector.addItem("Greedy"); 
		initialTaskAssgnSelector.addItem("Optimal"); 
		initialTaskAssgnSelector.addItem("PRIM"); 
		
		initialTaskAssgnSelector.select(1); 
		initialTaskAssignmentMethod =initialTaskAssgnSelector.getItem(1); 
		initialTaskAssgnSelector.addItemListener(new InitialTASelection());
	 * */
	
	
	public eTSPLoopMode loopModeSelection ; 
	/*
	 * 
	 * loopModeSelector = new Choice (); 
 		loopModeSelector.addItem("TSP TOUR");
 		loopModeSelector.addItem("TSP PATH");
 		loopModeSelector.addItem("TSP PATH-to-STATION");
 		loopModeSelector.addItemListener(new LoopModeSelection()); 	
 		loopModeSelector.select(2); 
 		loopModeSelection = 2; 
	 */
	
	public eLocalScheduling localScheduleSelection;
	public ePathStyle pathStyleSelection; 
	public String callerAgent; 
	/*
	localSchedulingSelector = new Choice (); 
		localSchedulingSelector.addItem("TSP & TPM"); 
		localSchedulingSelector.addItem("TSP & RRT");
		localSchedulingSelector.addItem("TSP & D*-Lite ");
		localSchedulingSelector.addItem("TSP & Dijkstra ");
		//localSchedulingSelector.addItem("MST with TPM"); 
		//localSchedulingSelector.addItem("NEAREST"); 
		//localSchedulingSelector.addItem("NEXT"); 

		localScheduleSelection = 2; 
		localSchedulingSelector.addItemListener(new LocalScheduleSelection()); 		
		SelectLocalSchedule(); 
	*/
	
	public SimParameters() {
		super();
		// TODO Auto-generated constructor stub
		
		autoMode = 1;
	
	}

	public String initialTAMethod; 
	public String initialBidValuation; 
	public String tradeBidValuation; 
	public String localScheduling; 
	
	
	public int stationCount; 
	public int robotSpeed = 20;
	public int pickUpTime = 120; 
	public int taskCount; 
	
	public int robotCount;
	
	public int pickUpDuration; 
	public int ucCapacity; 
	public int tradeCapacity; 
	
	public int autoMode; 
	public int taskSetRangeStart; 
	public 	int taskSetRangeEnd;
	
    public int taskSetNumber; 
    public int stationSetNumber;
	

	public int heatDiameter=9; 
	
	String nameofTheAuctioneer; 
	
	
	public int tasksPerHeat = 7; 
	public int stationProx = 0;  
	
	
  	String terrainAgentName = "ARENA";
  	
  	public TaskSet taskSet ; 
  	
  	public int TERRAIN_WIDTH; 
  	public int TERRAIN_HEIGHT; 
  	public int physicalWidth; 
	
	
	public void setTaskSetRangeEnd(int rend)
	{
		
		taskSetRangeEnd = rend +1;
		if (autoMode == 0) 
		 {
			taskSetRangeEnd = taskSetRangeStart + 1; 
		 }
		
	}
	
	
	
    /**
	Object[] targs = new Object[23];
	
	targs[0] = name; 

	
	targs[1] = templateSelection;
	

	targs[2] = executionModeSelection;
	
	// 
	targs[3] = approachSelection; 
	
	targs[4] = "M1"; 
	
	targs[5] = tCount; 
	
	targs[6] = rCount; 
	
	targs[7] = tfTerrainWidth.getText(); 
	
	targs[8] = tfTerrainHeight.getText();
	targs[9] = iapproachSelection; 
	targs[10] = stationCount ;
	targs[11] = 0 ; 
	targs[12] = expMode ; 
	targs[13] = ucCapacity; 
	targs[14] = tradeCapacity; 
	targs[15] = g_s; 
	targs[16] = g_t; 
	targs[17] = heatDiameter ;
	targs[18] = tasksPerHeat ;
	targs[19] = stationProx ;
	
	targs[20] = setupSelection ;
	targs[21] = robotSpeed;
	targs[22] = pickUpTime; 
	*/
}
