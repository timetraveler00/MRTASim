
/////////////// SIMMANAGER 34 

package MRTAmain;
import jade.core.Agent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Choice;
import java.awt.AWTException;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.core.behaviours.*; 

import java.util.*; 

import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.AID;
import Enums.eBidValuation;
import Enums.eComponentAccessibility;
import Enums.eComponentVisibility;
import Enums.eExecutionMode;
import Enums.eInitialTaskAssignment;
import Enums.eLocalScheduling;
import Enums.ePathStyle;
import Enums.eServiceArea;
import Enums.eTSPLoopMode;
import IndoorStructure.IndoorStructure;

//import Initiator.StartGeneticListener;
import MRTAStreamManager.AUStationReader;
import MRTAStreamManager.ExperimentStepReader;
import MRTAStreamManager.FMStationReader;
import MRTAStreamManager.MRTAFileReader;
import MRTAStreamManager.MRTAFileWriter;
import MRTAStreamManager.RobotReader;
import MRTAStreamManager.StationReader;
import MRTAStreamManager.StationsAllReader;
import MRTAStreamManager.TaskReader;
import MRTAStreamManager.TaskSetReader;
import MRTAStreamManager.TasksAllReader;
import MRTAStreamManager.TerrainDimensionsReader;

/*
 * MRTA-FOD Clearance Simulation
 * SIMMANAGER Agent 
 * Aim: Starting, killing and managing other agents with several parameters
 * Savas Ozturk
 * 2013-2014
 * 
 * */

public class SimManagerAgent extends GuiAgent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4559402516761232489L;
	
	MRTAFileReader mfr = new MRTAFileReader(); 
	
	
	SimParameters sp ; 
	JFrame f; 
	JPanel pnl, pnl2 ; 
	TextField tf1 ;
	TextField tf2 ;
	//TextField tfTaskSeed ; 
	TextField tfTerrainWidth ;
	TextField tfTerrainHeight ;
	TextField tfTSPLoopMode;
	TextField tfRRTIterCount; 
	TextField tfRRTDelta; 
	TextField tfStationCount; 
	TextField tfucCapacity; 
	TextField tftradeCapacity ; 
	//TextField tfTaskSetNumber ;
	TextField tfStationSetNumber ;
	
	SimManagerAgent thisa = this; 
	Button b1,b2,b3,b4 ;
	Button btnKillAgents;
	Button btnRRT; 
	//public int cTaskCount = 0; 

	//public int TERRAIN_WIDTH = 700; 
	//public int TERRAIN_HEIGHT =500;  

	
	//private AgentController t1 = null;
	
	String [] createdAgents = new String[105]; 
	int agentCount; 

	EvaluateMessages evalProp = new EvaluateMessages(this, 10);
	
	Choice templateSelector; 
	Choice setTemplateSelector;
	Choice setupSelector; 
	Choice numberofTasks;
	Choice expModeSelector;  
	
	Choice chHeatDiameter; 
//	String templateSelection; 
	//int setupSelection;
	
	
	// �retilecek task setleri i�in kullan�lacak template
	String setTemplateSelection; 
	TextField tfSampleCount;
	
	Choice executionModeSelector; 
	Choice initialTaskAssgnSelector; 
	int executionModeSelection; 
	Choice approachSelector; 
	//intsp.approachSelection; 
	Choice iapproachSelector; 
	//int sp.approachSelection ; 
	Choice loopModeSelector; 
	//int loopModeSelection = 0; 
	
	Choice localSchedulingSelector;
	
	Choice pathStyleSelector; 
	
	//int localScheduleSelection;  
	public Random randomGenerator = new Random(); 
	public ResultSet results = new ResultSet(1000);  
	Checkbox cbAutomated = new Checkbox("Automation", false); 
	
	int gStepCount = 0; 
 
//	int gStationCount = 5; 
	//int gRobotCount = 5;
	public IndoorStructure is;
	
	//public int ucCapacity = 4;
	//public int tradeCapacity = 6;
	 
	//int robotsPerStation = 1; 
	
	boolean restartFlag = true; 
	
	public StationSet stationSet  = new StationSet(1); //stSet [][] = new StationSet [5][gSampleCount];
	// 3 ila 7 robot i�in gSampleCount kadar
	
//	public TaskSet tSet [][] = new TaskSet [100][gSampleCount];
	// 10 ila 50 task i�in gSampleCount kadar 

	public int g_s = 0, g_t = 0; 
//	 HeatMap hm ; 
	 int hmct; 
 
	// int expMode = 0; 
	 IOLogger iol  = new IOLogger("SimManagerAgent", 0); 
	 int experimentCounter = 0; 
	 
	 JSlider rangeStart , rangeEnd;
	 
		static final int FPS_MIN = 0;
		static final int FPS_MAX = 9;
		static final int FPS_INIT = 1;    //initial frames per second
		//public int taskSetRangeStart = 0; 
		//public int taskSetRangeEnd = 0;
		
		//public int autoMode = 0; 
		//public int robotSpeed  = 20; 
		//public int pickUpTime  = 120;
		
		
		SetupSelect setsel = new SetupSelect(); 
//      String setupStr ; 
     Label lStationCount = new Label("Number of Stations");
	 Label lStationSetNumber = new Label("Station Set Number"); 
	 Label lHeatDiameter = new Label ("Heat Diameter (m.)");
	 Label lTaskSetRangeEnd = new Label ("TaskSet Range End"); 
	 Label lTaskSetRangeStart = new Label ("TaskSet Range Start"); 
	 Label lRobotSpeed = new Label ("Max.Speed km/h ");  
	 TextField tfRobotSpeed = new TextField("20");
	 Label lPickUpTime = new Label ("PickUp Duration s.");  
	 TextField tfPickUpTime = new TextField("120");
	 JTabbedPane tabbedPane; 
	 JPanel panel1 = new JPanel(); 
	 JPanel panel2 = new JPanel(); 
	JPanel panel3 = new JPanel(); 
	JPanel panel4 = new JPanel(); 
	//String initiatorAgentName ="ParametricInitiator"; 
	 
	 
	 
	 /*
	  * 0 : parametric
	  * 1 : automation
	  * 2 : random
	  * 3 : selected set  
	  * 4 : heat  
	  * */
	 
	 
 /// 
	public AgentController t1 = null;
	
	public void StartAgent (String agentName, String agentType, Object[] targs)
	{
		try {
			// create agent t1 on the same container of the creator agent
			//int stationCount = Integer.parseInt(tfStationCount.getText()) ; 
					
			AgentContainer container = (AgentContainer) getContainerController(); // get a container controller for creating new agents
			
			createdAgents[agentCount++]  = agentName; 
			
			t1 = ((ContainerController) container).createNewAgent(agentName, agentType, targs);
			t1.start();
			System.out.println(getLocalName()+" CREATED AND STARTED NEW : "+ agentType +" NAMED " +agentName + " ON CONTAINER "+((ContainerController) container).getContainerName());
			

			} catch (Exception any) {
				any.printStackTrace();
              }
	 }
	
	
	
	/*
	 * processStage  = 
	 * 0: istasyon say�s�n�n se�ilmesi 
	 * 1:  �stasyon seti belirlenmesi 
	 * 2:  Task say�s�n�n se�ilmesi 
	 * 3 : Task seti belirlenmesi 
	 * 4 : Y�ntem belirlenmesi  
	 * 
	 * */
	 public void Mesaj (String st) 
		{
			 Yaz("*");
			 Yaz("******************");
			 Yaz("**************************************");
			 Yaz("*************************************************************************");
			 Yaz("SIMMANAGER> " + st);
			 Yaz("*************************************************************************");
			 Yaz("**************************************");
			 Yaz("******************");
			 Yaz("*");
			 
		}
		
		public void KisaMesaj (String st) 
		{
			 
			 Yaz("SIMMANAGER> " + st);
			 	 
		}
		public void Yaz(String st) 
		{
			 
			iol.Yaz(st);
			 	 
		}
		
		 public void SelectSetup() {
			 
				
			 sp.setupSelection = setupSelector.getSelectedItem(); 
			 
			 
		 }
		 
		 class  SetupSelection implements ItemListener {
			
			 public void itemStateChanged (final ItemEvent ie) 
			 {
				 
				 SelectSetup(); 
				 setsel.itemStateChange(); 
				 
			 }
			 
			 
		 }
		
	public void ExperimentParameters() 
	{
		KisaMesaj(" Terrain : "+ sp.templateSelection);
		KisaMesaj(" Task Count : "+sp.taskCount);
        KisaMesaj(" Station Count : "+sp.stationCount);
        KisaMesaj(" Task Set : " + g_t );
        KisaMesaj(" Station Set :"+ g_s );
        KisaMesaj(" Initial Allocation Approach: "+ sp.initialTaskAssignmentMethod );
        KisaMesaj(" Initial Bidding Approach:"+sp.approachSelection);
        KisaMesaj(" Trade Bidding Approach:"+  sp.approachSelection);
        KisaMesaj(" Initial Robot Capacity:"+  sp.ucCapacity);
        KisaMesaj(" Trade Robot Capacity:"+  sp.tradeCapacity);
	}
	 public void GenerateTaskSets (int gSampleCount, int size) 
		{
		  
			
			TasksAllReader tar = new TasksAllReader(setTemplateSelection); 
			int tCount = tar.TaskCount(); 
			
			for (int k=0; k<gSampleCount; k++) 
			{  
				TaskSet ts = new TaskSet(size, k); 
				
			    int i= 0; 	
			 	while (i<size)
			 	{
			 		int stIndex = randomGenerator.nextInt(tCount);
			 		
			 		boolean existFlag = false; 
					for (int j=0; j<i; j++) 
					{
						if  (ts.GetTIndex(j) == stIndex )
						{
							existFlag = true; 
						}
					}
					if (!existFlag) 
					{
			 		
						KisaMesaj ("stIndex " + stIndex + " size "+size +" k "+ k+ " i "+i); 
						// 	ayn� task� vermemesi de kontrol edilmeli
						Task ttt = tar.GetTask(stIndex);
						ttt.index = stIndex; 	
								
						//fr.tasks[stIndex].index = stIndex;  
						 
						ts.AddTask(ttt) ;
						ts.SetTIndex(i, stIndex); 
						i++; 
					}
				}
			
			 	ts.ToExpFile(setTemplateSelection) ; 
			 			
	     	}		
			
			
		}
				
		public void GenerateStationSet (int gSampleCount, int size) 
		{
			
			int stationProximity = 75;
			 StationSet allstations = new StationsAllReader(sp.templateSelection).GetStationSet(); 
			int stCount = allstations.setSize; 
			//IndoorStructure issg = new IndoorStructure(setTemplateSelection);
			
			
			for (int k=0; k<gSampleCount; k++) 
			{
			int i=0; 
			 StationSet ss = new StationSet(size);
			 ss.SetID(k); 
		
			while (i<size)  
	     	{
				int stIndex = randomGenerator.nextInt(stCount); 
				boolean existFlag = false; 
				for (int j=0; j<i; j++) 
				{
					if (ss.stIndex[j] == stIndex )
					{
						existFlag = true; 
					}
				}
				
				if (!existFlag) 
				{
				
			//	TransitionPoint t1 = new TransitionPoint(fr.stations[stIndex].xLoc, fr.stations[stIndex].yLoc,"tp1");
			//	TransitionPoint tc1 = issg.Closest(t1);
			//	TransitionPoint tc2 = issg.Closest( new TransitionPoint(is.refPoint.xLoc, is.refPoint.yLoc, "tp2")); 
				
				Yaz("SIMMANAGER> �stasyon setleri olu�turuluyor ... Set : " + k + " �stasyon : " +i);
			//	Yaz("SIMMANAGER>  " + t1.toString() ); 
			
			//	if (tc1!= null && tc2 != null && is.ShortestPathLengthFF(tc1,tc2) >0 && is.ShortestPathLengthFF(tc1,tc2) <5000 )
			//			{
				Station randomst = allstations.stations[stIndex];	
				boolean proximityFlag = false; 
					for (int j=0; j<i; j++) 
					{
						//TransitionPoint t3 = new TransitionPoint(fr.stations[stSet[k].stIndex[j]].xLoc, fr.stations[stSet[k].stIndex[j]].yLoc,"tp3");
						//TransitionPoint tc3 = is.Closest(t3);
						Station temp = allstations.stations[ss.stIndex[j]];
						
						if ( CalcDistance(temp.xLoc, temp.yLoc, randomst.xLoc, randomst.yLoc) < stationProximity)
						    proximityFlag = true; 
					}
					if (! proximityFlag) 
					{
					
					      
				
				//	Yaz("SIMMANAGER> : is.ShortestPathLengthFF(tc1,tc2) > " + is.ShortestPathLengthFF(tc1,tc2));         
					ss.stCoor [i][0] = randomst.xLoc;	
					ss.stCoor [i][1] = randomst.yLoc; 
					ss.stIndex[i] = stIndex; 
					 i++; 
					}
					
			//		}
					
				} //e exist 
			} // while 
				//stSet[size-3][k] = ss;
				ss.ToFile(setTemplateSelection); 
				
			
			}
			
		  
		}
		
		public double CalcDistance(int xs, int ys, int xd, int yd)
		{
			
			int x_fark = xs-xd ; 
			int y_fark =  ys-yd ;
			
			return Math.sqrt( x_fark * x_fark  + y_fark*y_fark ) ;	


		}
		
		
		public void SetupSettings() 
		{
			setupSelector = new Choice (); 
			setupSelector.addItem("Parametric"); 
			setupSelector.addItem("OptimalTPM");
			setupSelector.addItem("Automation");
			setupSelector.addItem("HeatMap");
			setupSelector.addItem("RL");
			
			/*setupSelector.addItem("ASP1");
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
			*/
			SetupSelection ss = new SetupSelection(); 
			setupSelector.addItemListener(ss);
			 	

	 		setupSelector.select(4); 
	 		
	 		
		}	
	
	public void ExecutionModeSettings() 
	{
		executionModeSelector = new Choice (); 
 		executionModeSelector.addItem("AGILE"); 
 		executionModeSelector.addItem("CENTRALIZED"); 
 		executionModeSelector.addItem("TRADE"); 
 		sp.executionModeSelection = eExecutionMode.CENTRALIZED ;  
 		executionModeSelector.select(1); 
 		executionModeSelector.addItemListener(new ExecutionModeSelection()); 	
	}
	
	public void test() 
	{ 
		int x = 5; 
		int y = 3; 
		
		// If Block A : if x is bigger than y 
		if ( x > y ) 
		{
			System.out.println(x + " is bigger than " + y );
		}
		// If Block B : if x is lower than y 
		else if ( x > y ) 
		{
			System.out.println(x + " is lower than " + y );
		}
		// Else Block C : if x is equal to y 
		else 
		{
			System.out.println(x + " is equal to " + y );
		}
		
	}
	
	
	public void TemplateSettings () 
	{
		
		
		templateSelector = new Choice (); 
		//File f = new File(".");
		//System.out.print("absolute path: "+f.getAbsolutePath()); 
		System.out.println("absolute path: "+mfr.absolutePath);
		String path =  mfr.absolutePath +"\\config\\" ;
		 System.out.println("path: "+path); 
		String folders; 
		File folder = new File(path);
		File[] listofFolders = folder.listFiles(); 
		if (listofFolders!=null)
		{
			for (int i=0; i<listofFolders.length; i++) 
			{
				folders = listofFolders[i].getName(); 
				templateSelector.addItem(folders); 
			}
		 
		
			templateSelector.addItemListener(new TemplateSelection()); 	
			templateSelector.select(1); 
			sp.templateSelection = templateSelector.getItem(1); 
			SelectTemplate(); 
		}
		
	}
	
	public void TemplateSettings_SetGeneration () 
	{
		setTemplateSelector = new Choice (); 
		//File f = new File(".");
		
		String path = mfr.absolutePath +"\\config\\" ;
		System.out.println (mfr.absolutePath + " - " + path);  
		String folders; 
		File folder = new File(path);
		File[] listofFolders = folder.listFiles(); 
		if (listofFolders!=null) 
		{
			for (int i=0; i<listofFolders.length; i++) 
			{
				folders = listofFolders[i].getName(); 
				setTemplateSelector.addItem(folders); 
			}
		 		
			setTemplateSelector.addItemListener(new TemplateSelection_SetGeneration()); 	
			setTemplateSelector.select(0); 
			setTemplateSelection = setTemplateSelector.getItem(0); 
			SelectTemplate_SetGeneration();
		}
	
		
	}
	public void LoopModeSettings () 
	{
		loopModeSelector = new Choice (); 
 		loopModeSelector.addItem("TSP TOUR");
 		loopModeSelector.addItem("TSP PATH");
 		loopModeSelector.addItem("TSP PATH-to-STATION");
 		loopModeSelector.addItemListener(new LoopModeSelection()); 	
 		loopModeSelector.select(1); 
 		sp.loopModeSelection = eTSPLoopMode.TSP_PATH; 
	}
	
	public void InitialTaskSettings() 
	{
		initialTaskAssgnSelector = new Choice(); 
		initialTaskAssgnSelector.addItem("Random");
		initialTaskAssgnSelector.addItem("Greedy"); 
		initialTaskAssgnSelector.addItem("Optimal"); 
		initialTaskAssgnSelector.addItem("PRIM"); 
		
		initialTaskAssgnSelector.select(1); 
		sp.initialTaskAssignmentMethod =  eInitialTaskAssignment.GREEDY;// initialTaskAssgnSelector.getSelectedIndex(); // initialTaskAssgnSelector.getItem(1);
		initialTaskAssgnSelector.addItemListener(new InitialTASelection());
		
	}
	
		
	public void InitialApproachSettings() 
	{
		iapproachSelector = new Choice (); 
 		iapproachSelector.addItem("Euclidian(TPM)"); 
 		iapproachSelector.addItem("Fuzzy"); 
 		iapproachSelector.addItem("D*Lite"); 
 		iapproachSelector.addItem("D*Lite Fuzzy");
 		iapproachSelector.addItem("RRT");
 		iapproachSelector.addItem("TPM - TSP");
 		iapproachSelector.addItem("RRT - TSP");
 		iapproachSelector.addItem("D*L - TSP");
 		iapproachSelector.addItem("Hybrid (TPMTSP + Euclidian)");
 		sp.approachSelection = eBidValuation.DSTARLITE; //3; 
 		iapproachSelector.select(2); 
 		iapproachSelector.addItemListener(new IApproachSelection()); 		
 		SelectInitialTA(); 
	}
	
	public void TradeApproachSettings() 
	{
		approachSelector = new Choice (); 
 		approachSelector.addItem("Euclidian(TPM)"); 
 		approachSelector.addItem("Fuzzy"); 
 		approachSelector.addItem("D*Lite"); 
 		approachSelector.addItem("D*Lite Fuzzy");
 		approachSelector.addItem("RRT");
 		approachSelector.addItem("TPM - TSP");
 		approachSelector.addItem("RRT - TSP");
 		approachSelector.addItem("D*L - TSP");
 		approachSelector.addItem("Hybrid (TPMTSP + Euclidian)");
 		sp.approachSelection = eBidValuation.DSTARLITE; //3; 
 		approachSelector.select(2); 
 		approachSelector.addItemListener(new ApproachSelection()); 		
	}
	
	public void SchedulingSettings() 
	{
		localSchedulingSelector = new Choice (); 
 		localSchedulingSelector.addItem("TSP"); 
 		localSchedulingSelector.addItem("Nearest");
 		localSchedulingSelector.addItem("By Name");
 		localSchedulingSelector.addItem("Random");
 		//localSchedulingSelector.addItem("MST with TPM"); 
 		//localSchedulingSelector.addItem("NEAREST"); 
 		//localSchedulingSelector.addItem("NEXT"); 

 		sp.localScheduleSelection = eLocalScheduling.TSP; 
 		localSchedulingSelector.addItemListener(new LocalScheduleSelection()); 		
 		SelectLocalSchedule(); 
	} 
	
	public void DistCalcSettings() 
	{
		pathStyleSelector = new Choice (); 
		pathStyleSelector.addItem("Bresenham"); 
		pathStyleSelector.addItem("D*-Lite");
		pathStyleSelector.addItem("RRT");
		pathStyleSelector.addItem("Djkstra");
 		//localSchedulingSelector.addItem("MST with TPM"); 
 		//localSchedulingSelector.addItem("NEAREST"); 
 		//localSchedulingSelector.addItem("NEXT"); 

 		sp.pathStyleSelection = ePathStyle.DJKSTRA; 
 		pathStyleSelector.addItemListener(new DistanceCalculationSelection());		
 		SelectDistanceCalculationMethod(); 
	} 
	
	class SliderListenerStart implements ChangeListener {
	    public void stateChanged(ChangeEvent e) {
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	            sp.taskSetRangeStart = (int)source.getValue();
	            
	            if (rangeEnd.getValue() < sp.taskSetRangeStart) 
	            {
	                rangeEnd.setValue(sp.taskSetRangeStart); 
	            }
	           
	        }    
	    }

		
	}
	class SliderListenerEnd implements ChangeListener {
	    public void stateChanged(ChangeEvent e) {
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	            sp.taskSetRangeEnd = (int)source.getValue();
	            if (rangeStart.getValue() > sp.taskSetRangeEnd) 
	            {
	                rangeStart.setValue(sp.taskSetRangeEnd); 
	            }
	        }    
	    }

		
	}
	
	
	public void GUIPrep () 
	{
		tf1 = new TextField(); 
		tf2 = new TextField(); 
		tfTerrainWidth = new TextField(); 
		tfTerrainHeight = new TextField();
		tf1.setText("10"); 
		tf2.setText("2"); 
		tfTerrainHeight.setText("300"); 
		tfTerrainWidth.setText("400"); 
		//tfTaskSeed = new TextField(); 
		//tfTaskSeed.setText("0"); 
		tfTSPLoopMode = new TextField(); 
		tfTSPLoopMode.setText("1"); 
		tfRRTIterCount = new TextField();
		tfRRTIterCount.setText("1000"); 
		tfRRTDelta = new TextField();
		tfRRTDelta.setText ("3"); 
		tfStationCount = new TextField();
		tfStationCount.setText ("3"); 
		tfucCapacity = new TextField();
		tfucCapacity.setText ("100"); 
		tftradeCapacity = new TextField();
		tftradeCapacity.setText ("100"); 
		//tfTaskSetNumber = new TextField(); 
     	//tfTaskSetNumber.setText("0"); 
		tfStationSetNumber = new TextField(); 
		tfStationSetNumber.setText("0");
		 
		numberofTasks = new Choice(); 
		numberofTasks.setBounds(100,100, 75,75);  
		numberofTasks.add("1");
		numberofTasks.add("2");
		numberofTasks.add("3");
		numberofTasks.add("4");
		numberofTasks.add("5");
		numberofTasks.add("6");
		numberofTasks.add("7");
		numberofTasks.add("8");
		numberofTasks.add("9");
		numberofTasks.add("10");
		numberofTasks.add("15");
		numberofTasks.add("16");
		numberofTasks.add("20");
		numberofTasks.add("25");
		numberofTasks.add("29");
		numberofTasks.add("30");
		numberofTasks.add("35");
		numberofTasks.add("36");
		numberofTasks.add("37");
		numberofTasks.add("38");
		numberofTasks.add("40");
		numberofTasks.add("45");
		numberofTasks.add("50");
		numberofTasks.select (2); 
		
		
		expModeSelector = new Choice(); 
		expModeSelector.add ("ALL_TERRAIN");
		expModeSelector.add ("RECTANGULAR");
		expModeSelector.add ("EXTENDEDRECTANGULAR");
		expModeSelector.add ("CIRCULAR");
		expModeSelector.select(0); 
		expModeSelector.addItemListener(new ExpModeSelection());
		

	}
	public void TabbedPane () 
	{
		tabbedPane = new JTabbedPane(); 

		tabbedPane.addTab("Simulation Setup",null,panel1);
		tabbedPane.addTab("Reset Simulation",null,panel2);
		tabbedPane.addTab("Reserved",null,panel3);
		tabbedPane.addTab("Experiment Sets",null,panel4);
	}
	

public void SetSimParameters() 
{
	restartFlag = true; // 
	
	
	//////////////////////////////////////
	
	//SimParameters sp = new SimParameters();
		
	// Sim. otomatik olacaksa 1 
	sp.autoMode = SelectAutoMode(); 
	gStepCount = sp.taskSetRangeStart; 
	//sp.setTaskSetRangeEnd(rangeEnd.getValue());
	
    // G�rev say�s� aray�zden al�n�yor. 
    // 	G�rev seti ise �nceden olu�turulmu� setler aras�ndan 
    sp.taskCount = Integer.parseInt(numberofTasks.getSelectedItem()); // Integer.parseInt(tf1.getText());
	System.out.println("sp.taskcount: "+sp.taskCount);
	
		sp.stationCount = Integer.parseInt(tfStationCount.getText());
		sp.robotCount = Integer.parseInt(tf2.getText());
    	    
		 sp.approachSelection = eBidValuation.values() [approachSelector.getSelectedIndex()]; //approaches [m_i]; 
     	 sp.iapproachSelection = eBidValuation.values() [iapproachSelector.getSelectedIndex()];   // approaches [m_i];    
    	  
 		 sp.initialTaskAssignmentMethod =  eInitialTaskAssignment.values() [initialTaskAssgnSelector.getSelectedIndex()];//initialTaskAssgnSelector.getSelectedItem();
 		 
	 	 sp.ucCapacity = Integer.parseInt(tfucCapacity.getText()); //ucCapacityMax [c_i];
	  	sp.tradeCapacity = Integer.parseInt(tftradeCapacity.getText()); // tradeCapacityMax [c_j] ;
	  	
	  	
	  	sp.setupSelection = setupSelector.getSelectedItem();
        sp.templateSelection = templateSelector.getSelectedItem(); 
		
	  sp.heatDiameter = Integer.parseInt(chHeatDiameter.getSelectedItem());
	  
	  sp.expMode = eServiceArea.values() [expModeSelector.getSelectedIndex()];
	  sp.robotSpeed = Integer.parseInt(tfRobotSpeed.getText());
	  sp.executionModeSelection = eExecutionMode.values()[executionModeSelector.getSelectedIndex()]; 
	  sp.loopModeSelection = eTSPLoopMode.values() [loopModeSelector.getSelectedIndex()]; 
	  sp.localScheduleSelection = eLocalScheduling.values() [localSchedulingSelector.getSelectedIndex()] ;
	  sp.pathStyleSelection = ePathStyle.values()[pathStyleSelector.getSelectedIndex()];
	  sp.templateSelection = templateSelector.getSelectedItem(); 
	  
	  sp.pickUpTime = Integer.parseInt(tfPickUpTime.getText());
	//  sp.callerAgent = initiatorAgentName; 
	  
	  
	
}

public void SetSomeParameters ()
{
	sp.robotSpeed = Integer.parseInt(tfRobotSpeed.getText());
	sp.pickUpTime = Integer.parseInt(tfPickUpTime.getText());
}

	
/*public void TakeScreenShot (String str)  
{
	try {
		    
		f.setExtendedState(JFrame.NORMAL);
		f.setAlwaysOnTop(true);
		f.requestFocus();
		f.setAlwaysOnTop(false);
		
		/*f.setAlwaysOnTop(true); 	
		    f.toFront(); 
		    
		    f.repaint();
			f.setAlwaysOnTop(true);* 
		    
			
			File file = new File(".");
			String fileName = "_"; 		
			fileName = file.getAbsolutePath() + "\\..\\..\\Experiments\\Results_All\\"+ str+".jpeg";
			//BufferedImage screenCapture = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			BufferedImage screenCapture = new Robot().createScreenCapture(new Rectangle(0, 0 , f.getWidth(), f.getHeight()));	
			File target = new File (fileName);
			ImageIO.write(screenCapture, "jpeg", target); 
		} 
	catch (IOException ae)
	{
		
	}
	catch (AWTException AA)
	{
					
	}
	
}

*/

	public void Panel1() 
	{
		panel1.add(new Label("MRTA TYPE"));
		panel1.add(new Label(""));
		
		// FODFocus, HippoBots ve FollowMe aras�ndan se�im yap�lmas�n� sa�lar.
		SetupSettings(); 
		panel1.add(new Label("Setup Type")); 
 		panel1.add(setupSelector);
		
		TemplateSettings(); 
		panel1.add(new Label("Sim.Template")); 
 		panel1.add(templateSelector);
 		
 		panel1.add(new Label ("Comm. Area")); 
 		panel1.add(expModeSelector);
		
		ExecutionModeSettings();
 		panel1.add(new Label("Execution Mode")); 
 		panel1.add(executionModeSelector);
		
 		panel1.add(new Label("TSP Loop Mode")); 
 		
 		LoopModeSettings(); 
 	    panel1.add(loopModeSelector); 

		panel1.add(new Label("ASSINGMENT PREFERENCES"));
		panel1.add(new Label(""));
			
		InitialTaskSettings(); 
		panel1.add(new Label("Initial Task Assignment")); 
		panel1.add(initialTaskAssgnSelector);
 		
		InitialApproachSettings(); 
 		panel1.add(new Label("Bid Valuation Method For Initial A.")); 
 		panel1.add(iapproachSelector);
	
		TradeApproachSettings(); 
 		panel1.add(new Label("Bid Valuation Method For Trades")); 
 		panel1.add(approachSelector);
 	 		
		SchedulingSettings();
 		panel1.add(new Label("Local Scheduling Approach")); 
 		panel1.add(localSchedulingSelector);
 		
 		DistCalcSettings();
 		panel1.add(new Label("Path Style"));
 		panel1.add(pathStyleSelector);

		panel1.add(new Label("PARAMETERS"));
		panel1.add(new Label(""));
		
 		panel1.add(new Label("Number of Tasks"));
 		//Panel not = new Panel(); 
 		//not.add(tf1); 
 		//not.add(numberofTasks);
 		//panel1.add(not);
 		panel1.add(numberofTasks);
 		panel1.add(new Label("Number of Robots")); 
 		panel1.add(tf2);
 		panel1.add(lStationCount); 
 		panel1.add(tfStationCount);
 		panel1.add (lRobotSpeed); 
 		panel1.add(tfRobotSpeed); 
 		panel1.add(lPickUpTime); 
 		panel1.add(tfPickUpTime); 
  		
 		panel1.add(new Label("Robot Capacity (min)")); 
 		panel1.add(tfucCapacity);
 		panel1.add(new Label("Robot Capacity (max)")); 
 		panel1.add(tftradeCapacity);
 		
 		panel1.add(new Label("SELECTED SET PARAMETERS"));
		panel1.add(new Label(""));
 		//panel1.add(new Label("Task Set Number  ")); 
 		//panel1.add(tfTaskSetNumber); 
		chHeatDiameter = new Choice();
		chHeatDiameter.add("10"); 
 		chHeatDiameter.add("20");
 		chHeatDiameter.add("30"); 
 		chHeatDiameter.add("40");
 		chHeatDiameter.add("50");
 		chHeatDiameter.add("60");
 		chHeatDiameter.add("70");
		chHeatDiameter.add("100"); 
 		chHeatDiameter.add("200");
 		chHeatDiameter.add("300"); 
 		chHeatDiameter.add("400");
 		chHeatDiameter.add("500");
 		chHeatDiameter.add("600");
 		chHeatDiameter.add("700");
 		chHeatDiameter.select(2); 
 		
	 		panel1.add(lHeatDiameter);
	 		panel1.add(chHeatDiameter);
 		
 		panel1.add(new Label (""));
 		
 		//ExpModeSel (); 
 	
 		panel1.add(cbAutomated);
 		cbAutomated.addItemListener(new AutoListener()); 
 		panel1.add(lTaskSetRangeStart);
 		
 		rangeStart = new JSlider(JSlider.HORIZONTAL,
                FPS_MIN, FPS_MAX, FPS_INIT);
 		rangeStart.setValue(1);
rangeStart.addChangeListener(new SliderListenerStart());

//Turn on labels at major tick marks.
rangeStart.setMajorTickSpacing(1);
rangeStart.setMinorTickSpacing(1);
//framesPerSecond.setPaintTicks(true);
rangeStart.setPaintLabels(true);

 		rangeEnd = new JSlider(JSlider.HORIZONTAL,
                FPS_MIN, FPS_MAX, FPS_INIT);
                rangeEnd.setValue(10); 
rangeEnd.addChangeListener(new SliderListenerEnd());

//Turn on labels at major tick marks.
rangeEnd.setMajorTickSpacing(1);
rangeEnd.setMinorTickSpacing(1);
//framesPerSecond.setPaintTicks(true);
rangeEnd.setPaintLabels(true);
 		panel1.add(rangeStart);
	panel1.add(lTaskSetRangeEnd);
 		 		panel1.add(rangeEnd);
 		 		
 		 			panel1.add(lStationSetNumber); 
 		panel1.add(tfStationSetNumber);
 		 		

 		SelectAutoMode(); 		
 	    panel1.add(new Label("")); 
 	    
 	    
 	    
		//JButton button1 = new JButton("START PARAMETRIC");
		// button1.addActionListener(new StartTerrainListener()); 
		
		//JButton button2 = new JButton("START AUTOMATION ");
	//	button2.addActionListener(new StartGeneticListener()); 

		//JButton button3 = new JButton("START RANDOM EXPERIMENTS");
		//button3.addActionListener(new StartRandomExperiments()); 
		
		//JButton button4 = new JButton("S T A R T  S I M U L A T I O N");
		//button4.addActionListener(new StartSelectedSetListener()); 
		
		JButton button5 = new JButton("S T A R T  ");
		button5.addActionListener(new StartHeatMapListener()); 
		
	//	panel1.add(button2); 
	//	panel1.add(button1); 
	//	panel1.add(button3); 
	//	panel1.add(button4); 
		panel1.add(button5); 
		
		setsel.itemStateChange();
	}
	
	class StartHeatMapListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{
			

			SetSimParameters();
			Object[] targs = new Object[1];
			
			targs[0] = sp;  	
			
			System.out.println("Sent initialTaskAssignmentMethod: " + sp.initialTaskAssignmentMethod);
			
			if (sp.setupSelection.compareTo("Automation")==0) 
			{
				//initiatorAgentName ="TaskSetInitiator";
				StartAgent ("TaskSetInitiator", "Initiator.TaskSetInitiator", targs);
				//StartAgent ("ParametricInitiator", "Initiator.ParametricInitiator", targs);
			}
			else if (sp.setupSelection.compareTo("Parametric")==0)			
			{
				//initiatorAgentName ="ParametricInitiator";
				StartAgent ("ParametricInitiator", "Initiator.ParametricInitiator", targs);
			}
			else if (sp.setupSelection.compareTo("HeatMap")==0) 
			{
				StartAgent ("HeatMapInitiator", "Initiator.HeatMapInitiator", targs);
			}
			else if (sp.setupSelection.compareTo("RL")==0) 
			{
				StartAgent ("RLInitiator", "Initiator.RLInitiator", targs);
			}
			
		}
		
	}
	
	class StartGeneticListener implements ActionListener {
		public void actionPerformed(ActionEvent e) 
		{
			
		}
		
	}
	public void Panel2() 
	{
		btnKillAgents = new Button("Reset"); 
		btnKillAgents.addActionListener(new ResetListener()); 
		panel2.add(btnKillAgents);
	}
	public void Panel3() 
	{
		btnRRT = new Button ("RRT") ;
        btnRRT.addActionListener(new RRTListener()); 
        panel3.setLayout(new GridLayout(0,2)); 
        panel3.add(new Label("RRT Delta")); 
        panel3.add(tfRRTDelta); 
        panel3.add(new Label("Max. Iterations")); 
        panel3.add(tfRRTIterCount); 
        panel3.add(new Label("")); 
        panel3.add(btnRRT); 
	}
	public void Panel4() 
	{
		   Button btnSetGeneration = new Button("Generate Experimental Sets"); 
	        btnSetGeneration.addActionListener(new SetGenerationListener()); 
	            
	        
	        TemplateSettings_SetGeneration(); 
	        
	        panel4.add(new Label("Select a Terrain "));
	        panel4.add(setTemplateSelector) ;
	        
	        panel4.add(new Label("Sample Count "));
	        tfSampleCount = new TextField();
	        tfSampleCount.setText("20"); 
	        panel4.add(tfSampleCount) ;
	        panel4.add (btnSetGeneration);
	}
	
	protected void setup () {
	
		
		System.out.println("SimManagerAgent has started");
		mfr.GetDirectory();
		PrintStream fileStream;
		try {
			
			DateTimeStr dstr = new DateTimeStr();
			
			fileStream = new PrintStream(mfr.logDirectory +"MRTA "+ dstr.DtString()+".txt");
			System.setOut(fileStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		sp = new SimParameters(); 
		GUIPrep(); 
		
		f = new JFrame(); 
		f.setTitle("MRTASim");
		panel1.setLayout(new GridLayout(0,2)); 
		TabbedPane(); 
	
		f.add(tabbedPane);  
		Panel1(); 
	    Panel2(); 
	    Panel3(); 
	    Panel4(); 
  		f.setBounds(0,0,500,700);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		f.setVisible (true);
		f.createBufferStrategy(2); 
	     addBehaviour(evalProp);
	
	}
	
class ResetListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) 
		{
			ACLMessage msg = new ACLMessage (ACLMessage.INFORM); 
			msg.addReceiver(new AID("ParametricInitiator", AID.ISLOCALNAME));
			msg.setLanguage("English");
			msg.setContent("191");
			send(msg);
			Yaz("SIMMANAGER > Reset talebi ParametricInitiator'a gonderildi.  ");
			
		}
			
	}

class RRTListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e) 
	{
		ACLMessage msg = new ACLMessage (ACLMessage.INFORM); 
				 
		msg.addReceiver(new AID("OARENA0", AID.ISLOCALNAME));
		
		
		msg.setLanguage("English");
		msg.setContent("181_"+tfRRTIterCount.getText()+"_"+tfRRTDelta.getText());
		send(msg);
		Yaz("SIMMANAGER > RRT talebi Terrain Agent'a (ARENA) gonderildi.  ");
			
	}
	
}

class SetGenerationListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e) 
	{
		
		Yaz("SIMMANAGER > Yeni deney setleri �retiliyor. : "+ setTemplateSelection);
		int gSampleCount = Integer.parseInt(tfSampleCount.getText());
		
		
		 for (int i=1; i<8; i++) 
    	 {
    		 GenerateStationSet(gSampleCount, i);		
			 Mesaj("�stasyon setleri olu�turuldu ...");
       	 }
    	
    	 for (int i=10; i<40; i+=10)
    	 {
    	     GenerateTaskSets(gSampleCount, i);  	 
    	     Mesaj("Task  setleri olu�turuldu ...");
    	 }
	}
}


	


public class EvaluateMessages extends TickerBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 657002871747329933L;

	public EvaluateMessages(Agent a, long interval) {
		super(a, interval);
	}
	
	
	

	protected void onTick() {
		
		ACLMessage msg = receive();
		//System.out.println ("SIMMANAGER> Mesaj geldi...");
		if (msg != null) {

			// Mesaj�n ba�l���na g�re s�n�fland�rma
			String content = msg.getContent();
			String delimiter = "_";
			String[] temp;
			temp = content.split(delimiter);
		
			ACLMessage repmsg = msg.createReply();
			repmsg.setPerformative(ACLMessage.INFORM);
			//System.out.println ("SIMMANAGER> Mesaj geldi..."+temp[0]);
			/*
			 *777// Robot tan�mlama mesaj� ise 01_robotName_locX_locY
			*/
			if (temp[0].compareTo("115G") == 0) {
                System.out.println ("SIMMANAGER> Otomatik Reset Talebi 115 geldi...");

			} 
			if (temp[0].compareTo("191") == 0) {
                System.out.println ("SIMMANAGER> Otomatik Reset Talebi 115 geldi...");

			} 
			
			
		} // msg != null 
		
	}
	

	
	       
}	






	
	
	public boolean AllTasksCovered (int robotCount, int taskCount) 
	{
		//FileReader fr = new FileReader(); 
		TaskReader tr = new TaskReader(sp.templateSelection);
		RobotReader rr= new RobotReader(sp.templateSelection); 
		 //fr.ReadTasks(templateSelection);
		 
		
		for (int i=0 ; i< taskCount; i++) 
		{
		    Task t = tr.GetTask(i);
		    
		   
		    boolean coveredFlag = false; 
			for (int j=0; j<robotCount; j++) 
			{
				Robotum r= rr.GetRobot(j); 
				HRectangle rect = r.rect; 
				if (t.xLoc>rect.x1 && t.xLoc <rect.x2 && t.yLoc >rect.y1 && t.yLoc <rect.y2) 
				{
					coveredFlag = true; 
			    }
			}
			if (coveredFlag == false) 
			{
				Yaz(" G�rev hi�bir robot taraf�ndan kapsanm�yor : " + t.taskName);
				return false;
			}
		}
		 
		 return true; 
	}
	
	public void CalculateRobotBoundaries (int robotCount, int taskCount) 
	{
	    
   		//Yaz(" istasyon : " + gStationCount +" setsize : " + st.setSize);
		
    	//FileReader fr = new FileReader();
		 
		
    	int step = 0; 
    	
    	do { 
    		//fr.ReadRobots(templateSelection, step++);
    		RobotReader rr = new RobotReader(sp.templateSelection, step++);
    		rr.GetRobotSet().ToFileAsStations(sp.templateSelection);
    		rr.GetRobotSet().ToFile(sp.templateSelection);
    		 
    		Yaz(" step : "+step);
    	} while (!AllTasksCovered(robotCount, taskCount)) ; 
    	
		
		 
		//stSet [gStationCount-3][stepCount] = st;  
		//st.ToFile(templateSelection) ; 
		//stationSet = st;
		
/*		}
		
		else 
		{
			StationSet st = LoadStationSet (gStationCount, stepCount);// stSet[gStationCount-3][stepCount]; 
			FileReader fr = new FileReader(); 
			fr.WriteStations(templateSelection, gStationCount, st.stCoor); 
			fr.WriteRobots(templateSelection, gStationCount, st.stCoor); 
			//stSet [gStationCount-3][stepCount] = st;  
			//st.ToFile(templateSelection) ; 
			stationSet = st; 
		}
	*/
	}
	

class ApproachSelection implements ItemListener {
	 public void itemStateChanged (final ItemEvent ie) 
	 {
		 
		sp.approachSelection  = eBidValuation.values() [approachSelector.getSelectedIndex()] ;
	 
	 }
	 
}



	class ExpModeSelection implements ItemListener {
		 public void itemStateChanged (final ItemEvent ie) 
		 {
			 
			 
		   //  ExpModeSel(); 
		 }
		 
		 
	}
 

public int SelectAutoMode () 
{
	 
	int autoMode; 
	if (cbAutomated.getState() == true) 
	 {
		 autoMode = 1; 
	 }
	 else 
	 {
		 autoMode = 0; 
	 }
	 HideorViewRangeEnd();  
	 return autoMode;

}

public void HideorViewRangeEnd () 
{
	 if (sp.autoMode == 1) 
	 {
		 lTaskSetRangeEnd.setVisible(true); 
		 rangeEnd.setVisible(true); 
	 }
	 else
	 {
		 lTaskSetRangeEnd.setVisible(false); 
		 rangeEnd.setVisible(false);
	 }
}

class AutoListener implements ItemListener {
	 public void itemStateChanged (final ItemEvent ie) 
	 {
		 SelectAutoMode();
	
	 
	 }
	 
	 
}
public void SelectLocalSchedule () 
{
	
	 sp.localScheduleSelection  = eLocalScheduling.values() [localSchedulingSelector.getSelectedIndex()];
}
class LocalScheduleSelection implements ItemListener {
	 public void itemStateChanged (final ItemEvent ie) 
	 {
		 
		SelectLocalSchedule(); 
		 

	 
	 }
	 
	 
}

public void SelectDistanceCalculationMethod () 
{
	
	 sp.pathStyleSelection  = ePathStyle.values() [pathStyleSelector.getSelectedIndex()];
}
class DistanceCalculationSelection implements ItemListener {
	 public void itemStateChanged (final ItemEvent ie) 
	 {
		 
		 SelectDistanceCalculationMethod(); 
		 

	 
	 }
	 
	 
}

class IApproachSelection implements ItemListener {
	 public void itemStateChanged (final ItemEvent ie) 
	 {
		 
		 sp.iapproachSelection  = eBidValuation.values() [iapproachSelector.getSelectedIndex()];
	 
	 }
	 
	 
}

class InitialTASelection implements ItemListener {
	 public void itemStateChanged (final ItemEvent ie) 
	 {
		 
		 SelectInitialTA(); 
	
	 }
	 	 
}

public void SelectInitialTA() 
{
	 sp.initialTaskAssignmentMethod  = eInitialTaskAssignment.values() [initialTaskAssgnSelector.getSelectedIndex()];
	 if (initialTaskAssgnSelector.getSelectedIndex() == 0) 
	 {
		iapproachSelector.setVisible(false); 
	 }
	 else 
		 iapproachSelector.setVisible(true); 	
}


class LoopModeSelection implements ItemListener {
	 public void itemStateChanged (final ItemEvent ie) 
	 {
		  
		 sp.loopModeSelection  = eTSPLoopMode.values() [loopModeSelector.getSelectedIndex()];
		 //if (loopModeSelection < 2) 
		 //	 tfStationCount.setText("0"); 
		// SelectTemplate();
		 
	 }
}
	 class TemplateSelection implements ItemListener {
		 public void itemStateChanged (final ItemEvent ie) 
		 {
			 
		    SelectTemplate(); 
		 
		 }
		 
		 
	 }
	 class TemplateSelection_SetGeneration implements ItemListener {
		 public void itemStateChanged (final ItemEvent ie) 
		 {
			 
		    SelectTemplate_SetGeneration(); 
		 
		 }
		 
		 
	 }
	 
	 public void SelectTemplate() 
	 {
		 sp.templateSelection  = templateSelector.getItem(templateSelector.getSelectedIndex());
		 TerrainDimensionsReader tdr = new TerrainDimensionsReader(sp.templateSelection);
		 sp.TERRAIN_WIDTH = tdr.getTERRAIN_WIDTH(); 
		 sp.TERRAIN_HEIGHT = tdr.getTERRAIN_HEIGHT();
		 sp.physicalWidth=tdr.getPhysicalWidth();
		 tfTerrainWidth.setText(Integer.toString(sp.TERRAIN_WIDTH)); 
		 tfTerrainHeight.setText(Integer.toString(sp.TERRAIN_HEIGHT)); 
		 tf1.setText(Integer.toString(new TaskReader(sp.templateSelection).TaskCount()));
		 tf2.setText(Integer.toString(new RobotReader(sp.templateSelection).RobotCount()));
		 if (sp.loopModeSelection == eTSPLoopMode.TSP_PATHTOSTATION) 
			 tfStationCount.setText(Integer.toString(new StationReader(sp.templateSelection).StationCount())); 
			  
		 else 	 
			 tfStationCount.setText("0");
		 
		 
		 if (sp.templateSelection.compareTo("Empty")  == 0) 
		 {
			 tfTerrainHeight.setVisible(true);
			 tfTerrainWidth.setVisible(true); 
			// tfTaskSeed.setVisible(true); 
			 
		 }
		 else 
		 {
			 tfTerrainHeight.setVisible(false);
			 tfTerrainWidth.setVisible(false); 
			// tfTaskSeed.setVisible(false); 
		 }
	 } 
	 
	 public void SelectTemplate_SetGeneration() 
	 {
		 setTemplateSelection  = setTemplateSelector.getItem(setTemplateSelector.getSelectedIndex());
		 
	 } 
	 
	 class SetupSelect 
	 {
		
		 private void ExecutionModeComponent (eExecutionMode value, eComponentAccessibility accessibility, eComponentVisibility visibility) 
		 {
			 
	     		sp.executionModeSelection = value ;  
	     		executionModeSelector.select(sp.executionModeSelection.getValue()); 
	     		executionModeSelector.setVisible(visibility.getValue()); 
	     		executionModeSelector.setEnabled(accessibility.getValue());
		}
		 
		 private void ServiceAreaComponent (eServiceArea value, eComponentAccessibility accessibility, eComponentVisibility visibility) 
		 {
			 
	     		sp.expMode = value;  
	     		expModeSelector.select(sp.expMode.getValue()); 
	     		expModeSelector.setVisible(visibility.getValue()); 
	     		expModeSelector.setEnabled(accessibility.getValue());
		}
		 private void TSPLoopModeComponent (eTSPLoopMode value, eComponentAccessibility accessibility, eComponentVisibility visibility) 
		 {
			 
	     		sp.loopModeSelection = value ;  
	     		loopModeSelector.select(sp.loopModeSelection.getValue()); 
	     		loopModeSelector.setVisible(visibility.getValue()); 
	     		loopModeSelector.setEnabled(accessibility.getValue());
		}
		 
		 private void InitialTaskAssignmentComponent (eInitialTaskAssignment value, eComponentAccessibility accessibility, eComponentVisibility visibility) 
		 {
			 
	     		sp.initialTaskAssignmentMethod = value ;  
	     		initialTaskAssgnSelector.select(sp.initialTaskAssignmentMethod.getValue()); 
	     		initialTaskAssgnSelector.setVisible(visibility.getValue()); 
	     		initialTaskAssgnSelector.setEnabled(accessibility.getValue());
		}
		 
		 private void InitialBidValuationComponent (eBidValuation value, eComponentAccessibility accessibility, eComponentVisibility visibility) 
		 {
			 
	     		sp.iapproachSelection = value;  
	     		iapproachSelector.select(sp.iapproachSelection.getValue() ); 
	     		iapproachSelector.setVisible(visibility.getValue()); 
	     		iapproachSelector.setEnabled(accessibility.getValue());
		}
		 private void TradeBidValuationComponent (eBidValuation value, eComponentAccessibility accessibility, eComponentVisibility visibility) 
		 {
			 
	     		sp.approachSelection = value ;  
	     		approachSelector.select(sp.approachSelection.getValue()); 
	     		approachSelector.setVisible(visibility.getValue()); 
	     		approachSelector.setEnabled(accessibility.getValue());
		}
		 
		 private void LocalSchedulingSelectorComponent (eLocalScheduling value, eComponentAccessibility accessibility, eComponentVisibility visibility) 
		 {
			 
	     		sp.localScheduleSelection = value ;  
	     		localSchedulingSelector.select(sp.localScheduleSelection.getValue()); 
	     		localSchedulingSelector.setVisible(visibility.getValue()); 
	     		localSchedulingSelector.setEnabled(accessibility.getValue());
		}
		 
		 private void PathStyleComponent (ePathStyle value, eComponentAccessibility accessibility, eComponentVisibility visibility) 
		 {
			 
	     		sp.pathStyleSelection = value ;  
	     		pathStyleSelector.select(sp.pathStyleSelection.getValue()); 
	     		pathStyleSelector.setVisible(visibility.getValue()); 
	     		pathStyleSelector.setEnabled(accessibility.getValue());
		}
		 
		 
		private void NumberofTasksTextFieldComponent(String content, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.taskCount = Integer.parseInt(content);
			   tf1.setText(content);
			   tf1.setVisible(visibility.getValue());
			   tf1.setEnabled(accessibility.getValue());
			   
		}
		private void NumberofTasksChoiceComponent(String content, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.taskCount = Integer.parseInt(content);
			   numberofTasks.select(content);
			   numberofTasks.setVisible(visibility.getValue());
			   numberofTasks.setEnabled(accessibility.getValue());
			   
		}
		
		private void NumberofRobotsComponent(String content, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.robotCount = Integer.parseInt(content);
			   tf2.setText(content);
			   tf2.setVisible(visibility.getValue());
			   tf2.setEnabled(accessibility.getValue());
			  
			   
		}
		private void NumberofStationsComponent(String content, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.stationCount = Integer.parseInt(content);
			   tfStationCount.setText(content);
			   tfStationCount.setVisible(visibility.getValue());
			   tfStationCount.setEnabled(accessibility.getValue());
			   lStationCount.setVisible(visibility.getValue());
			   lStationCount.setEnabled(accessibility.getValue());
			   
		}
		private void RobotSpeedComponent(String content, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.robotSpeed = Integer.parseInt(content);
			   tfRobotSpeed.setText(content);
			   tfRobotSpeed.setVisible(visibility.getValue());
			   tfRobotSpeed.setEnabled(accessibility.getValue());
			   lRobotSpeed.setVisible(visibility.getValue());
               lRobotSpeed.setEnabled(accessibility.getValue());
		}
		
		private void PickupDurationComponent(String content, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.pickUpDuration = Integer.parseInt(content);
			   tfPickUpTime.setText(content);
			   tfPickUpTime.setVisible(visibility.getValue());
			   tfPickUpTime.setEnabled(accessibility.getValue());
			   lPickUpTime.setVisible(visibility.getValue());
			   lPickUpTime.setEnabled(accessibility.getValue());
		}
		private void StationSetSelectComponent(String content, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.stationSetNumber = Integer.parseInt(content);
			   tfStationSetNumber.setText(content);
			   tfStationSetNumber.setVisible(visibility.getValue());
			   tfStationSetNumber.setEnabled(accessibility.getValue());
			   lStationSetNumber.setVisible(visibility.getValue());
			   lStationSetNumber.setEnabled(accessibility.getValue());
		}
		private void RobotCapacityComponent(String min, String max, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.ucCapacity = Integer.parseInt(min);
			   sp.tradeCapacity = Integer.parseInt(max);
			   tfucCapacity.setText(min);
			   tftradeCapacity.setText(max);
			   tfucCapacity.setVisible(visibility.getValue());
			   tfucCapacity.setEnabled(accessibility.getValue());
			   tftradeCapacity.setVisible(visibility.getValue());
			   tftradeCapacity.setEnabled(accessibility.getValue());
			   

			   
		}
		
		private void HeatDiameterComponent(String content, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.heatDiameter = Integer.parseInt(content);
			   chHeatDiameter.select((sp.heatDiameter-300)/100);
			   chHeatDiameter.setVisible(visibility.getValue());
			   chHeatDiameter.setEnabled(accessibility.getValue());
			   lHeatDiameter.setVisible(visibility.getValue());
			   lHeatDiameter.setEnabled(accessibility.getValue());
		}		
		
		
		private void RangeStartComponent(int content, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.taskSetRangeStart = content;
			   rangeStart.setValue(content);
			   rangeStart.setVisible(visibility.getValue());
			   rangeStart.setEnabled(accessibility.getValue());
			   lTaskSetRangeStart.setVisible(visibility.getValue());
			   lTaskSetRangeStart.setEnabled(accessibility.getValue());
		}	
		
		private void RangeEndComponent(int content, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   sp.taskSetRangeEnd = content;
			   rangeEnd.setValue(content);
			   rangeEnd.setVisible(visibility.getValue());
			   rangeEnd.setEnabled(accessibility.getValue());
			   lTaskSetRangeEnd.setVisible(visibility.getValue());
			   lTaskSetRangeEnd.setEnabled(accessibility.getValue());
			   
		}	
		
		
		private void AutomateComponent(boolean selection, eComponentAccessibility accessibility, eComponentVisibility visibility)
		{
			   cbAutomated.setState(selection);   
			   cbAutomated.setVisible(visibility.getValue());
			   cbAutomated.setEnabled(accessibility.getValue());
		}	
		

		 public void itemStateChange () 
		 {
			 
			 sp.setupSelection  = setupSelector.getSelectedItem();
		
			 if (sp.setupSelection.compareTo("Parametric")==0 )
			 {
     			 // User Defined - Parametric  
				 ExecutionModeComponent(eExecutionMode.TRADE, eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 ServiceAreaComponent(eServiceArea.RECTANGULAR, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE); 
				 TSPLoopModeComponent(eTSPLoopMode.TSP_TOUR, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 InitialTaskAssignmentComponent( eInitialTaskAssignment.OPTIMAL, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 InitialBidValuationComponent(eBidValuation.DSTARLITE, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 TradeBidValuationComponent(eBidValuation.DSTARLITE, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 LocalSchedulingSelectorComponent(eLocalScheduling.TSP, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);	
				 PathStyleComponent(ePathStyle.BRESENHAM, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 NumberofTasksChoiceComponent("37", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE );
				 NumberofRobotsComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 NumberofStationsComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 RobotSpeedComponent("20", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 PickupDurationComponent("120", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 RobotCapacityComponent("40","40", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 HeatDiameterComponent("700",eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 				 
				 AutomateComponent(false,  eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 RangeStartComponent(0, eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 RangeEndComponent(0, eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 StationSetSelectComponent("0",  eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 NumberofTasksTextFieldComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN );
				 
				 
			 }
			 if (sp.setupSelection.compareTo("OptimalTPM")==0 )
			 {
     			 // User Defined - Parametric  
				 ExecutionModeComponent(eExecutionMode.TRADE, eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 ServiceAreaComponent(eServiceArea.RECTANGULAR, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE); 
				 TSPLoopModeComponent(eTSPLoopMode.TSP_PATH, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 InitialTaskAssignmentComponent( eInitialTaskAssignment.OPTIMAL, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 InitialBidValuationComponent(eBidValuation.TMPTSP, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 TradeBidValuationComponent(eBidValuation.TMPTSP, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 LocalSchedulingSelectorComponent(eLocalScheduling.TSP, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 PathStyleComponent(ePathStyle.BRESENHAM, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 NumberofTasksChoiceComponent("30", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE );
				 NumberofRobotsComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 NumberofStationsComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 RobotSpeedComponent("20", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 PickupDurationComponent("120", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 RobotCapacityComponent("40","40", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 HeatDiameterComponent("700",eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 				 
				 AutomateComponent(false,  eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 RangeStartComponent(0, eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 RangeEndComponent(0, eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 StationSetSelectComponent("0",  eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 NumberofTasksTextFieldComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN );
				 
				 
			 }
			 if (sp.setupSelection.compareTo("RL")==0)
			 {
     			 // User Defined - Parametric  
				 ExecutionModeComponent(eExecutionMode.TRADE, eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 ServiceAreaComponent(eServiceArea.RECTANGULAR, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE); 
				 TSPLoopModeComponent(eTSPLoopMode.TSP_PATH, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 InitialTaskAssignmentComponent( eInitialTaskAssignment.OPTIMAL, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 InitialBidValuationComponent(eBidValuation.TMPTSP, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 TradeBidValuationComponent(eBidValuation.TMPTSP, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 LocalSchedulingSelectorComponent(eLocalScheduling.TSP, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 PathStyleComponent(ePathStyle.BRESENHAM, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 NumberofTasksChoiceComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE );
				 NumberofRobotsComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 NumberofStationsComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 RobotSpeedComponent("5", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 PickupDurationComponent("120", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 RobotCapacityComponent("40","40", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 HeatDiameterComponent("700",eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 				 
				 AutomateComponent(false,  eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 RangeStartComponent(0, eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 RangeEndComponent(0, eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 StationSetSelectComponent("0",  eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 NumberofTasksTextFieldComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN );
				 
				 
			 }
			 if (sp.setupSelection.compareTo("Automation")==0 || sp.setupSelection.compareTo("HeatMap")==0)
			 {
     			 // User Defined - Parametric  
				 
				 
				 ExecutionModeComponent(eExecutionMode.CENTRALIZED, eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 ServiceAreaComponent(eServiceArea.EXTENDEDRECTANGULAR, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE); 
				 TSPLoopModeComponent(eTSPLoopMode.TSP_PATHTOSTATION, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 InitialTaskAssignmentComponent( eInitialTaskAssignment.OPTIMAL, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 InitialBidValuationComponent(eBidValuation.TMPTSP, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 TradeBidValuationComponent(eBidValuation.TMPTSP, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 LocalSchedulingSelectorComponent(eLocalScheduling.TSP, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 PathStyleComponent(ePathStyle.DJKSTRA, eComponentAccessibility.ENABLED,eComponentVisibility.VISIBLE);
				 NumberofTasksChoiceComponent("10", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE );
				 NumberofRobotsComponent("8", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 NumberofStationsComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 RobotSpeedComponent("20", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 PickupDurationComponent("120", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 RobotCapacityComponent("40","40", eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 HeatDiameterComponent("300",eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 				 
				 AutomateComponent(true,  eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 RangeStartComponent(0, eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 RangeEndComponent(1, eComponentAccessibility.ENABLED, eComponentVisibility.VISIBLE);
				 StationSetSelectComponent("0",  eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN);
				 NumberofTasksTextFieldComponent("1", eComponentAccessibility.ENABLED, eComponentVisibility.HIDDEN );
				 
				 
				 
			 }
		
			 
	 /*
			 // ASP2
			        case 2: 
			    	   
 	    	     		DisableEMS(2); // centralized
 	    	     		DisableExMS(4); // exp Mode extended rectangular
			     		DisableIAS(3);  // d*
			     		DisableAS(3);    // d*
			     		DisableAssS(1);   // greedy 
			    	    DisableLMS(2);    // TSP Path to station
			    	    Disabletf1 (); 
			    	    DisableTSC ();  
			    	    DisableRC(); 
			    	    DisableCapacity ("10"); 
			    	    DisableDiameter(); 
			    	    DisableStationSetNumber();  
			    	    DisableLSS(2); // tsp & d*
			    	     
			    	    
			    	   
			    	 //   templateSelector.select("Ataturk");
			    	    SelectTemplate(); 
			    	    tfRobotSpeed.setText("20"); 
			    	    tfPickUpTime.setText ("120"); 
			    	    DisableRS();  
			    	   
			    	    
			    	break;
			    	
			    	// ASP3
			        case 3: 
			        case 4: 
			    	   
 	    	     		DisableEMS(2); // centralized
 	    	     		DisableExMS(4); // exp Mode extended rectangular
			     		DisableIAS(3);  // d*
			     		DisableAS(3);    // d*
			     		DisableAssS(2);   // OPT�MAL
			    	    DisableLMS(2);    // TSP Path to station
			    	    Disabletf1 (); 
			    	    DisableTSC ();  
			    	    DisableRC(); 
			    	    DisableCapacity ("10"); 
			    	    DisableDiameter(); 
			    	    DisableStationSetNumber();  
			    	    DisableLSS(2); // tsp & d*
			    	     
			    	    
			    	   
			    	 //   templateSelector.select("Ataturk");
			    	    SelectTemplate(); 
			    	    tfRobotSpeed.setText("20"); 
			    	    tfPickUpTime.setText ("120"); 
			    	    DisableRS();  
			    	   
			    	    
			    	break;
			    	
			        // ASP1
			        case 1: 
 	    	     		
			        	DisableEMS(2); // CENTRALIZED
			        	DisableExMS(5); // sp.expMode
			     		DisableIAS(3);  // dstar
			     		DisableAS(3);    // dstar
			     		DisableAssS(1);   // greedy   
			    	    DisableLMS(2);    // TSP Path to station
			    	    Disabletf1 ();   // number of tasks choice 
			    	    EnableTSC ();    // stationCount
			    	    EnableRC();   // ROBOT count
			    	    DisableCapacity ("100"); 
			    	    DisableStationSetNumber();    
			    	    HideDiameter();
			    	    DisableLSS(2);
			    	    tfRobotSpeed.setText("20"); 
			    	    tfPickUpTime.setText ("120");
			    	 
			    	   DisableRS();  
			    	    
			    	    
			       break;
			       
			       			        // Follow Me
			        case 0: 
 	    	     		
			        	DisableEMS(2); // centralized
			        	DisableExMS(1); // expMode 1
			        	DisableIAS(3);  // dstar
			     		DisableAS(3);    // dstar
			     		DisableAssS(1);   // greedy   
			    	    DisableLMS(1);    // TSP Path to station
			    	    Disabletf1 ();   // number of tasks choice 
			    	    HideTSC ();    // stationCount
			    	    EnableRC();   // ROBOT count
			    	    DisableCapacity ("100"); 
			    	    DisableStationSetNumber();  
			    	    HideDiameter();
			    	    DisableLSS(2);
			    	    tfRobotSpeed.setText("70"); 
			    	    tfPickUpTime.setText ("120");
			    	    
			    	    DisableRS();  
			       break;	
			       
			        // Custom
			        case 44: 
 	    	     		
			        	EnableEMS(2); // SEQUENT�AL
			        	EnableExMS(5); // expMode
			        	EnableIAS(3);  // dstar
			     		EnableAS(3);    // dstar
			     		EnableAssS(1);   // greedy   
			    	    EnableLMS(1);    // TSP Path to station
			    	    Disabletf1 ();   // number of tasks choice 
			    	    EnableTSC ();    // stationCount
			    	    EnableRC();   // ROBOT count
			    	    EnableCapacity (); 
			    	    ViewDiameter();
			    	    EnableStationSetNumber(); 
			    	    EnableLSS(0);
			    	    
			    	    EnableRS(); 
			    	    
			    	    
			       break;			       
			        // Custom
			        case 45: 
	    	     		
			        	EnableEMS(2); // SEQUENT�AL
			        	EnableExMS(5); // expMode
			        	EnableIAS(3);  // dstar
			     		EnableAS(3);    // dstar
			     		EnableAssS(1);   // greedy   
			    	    EnableLMS(1);    // TSP Path to station
			    	    Enabletf1 ();   // number of tasks choice 
			    	    EnableTSC ();    // stationCount
			    	    EnableRC();   // ROBOT count
			    	    EnableCapacity (); 
			    	    ViewDiameter();
			    	    EnableStationSetNumber();  
			    	
			    	//    HideRS(); 
			    	    
			       break;	
			       
			        // Custom
			        case 6: 
	    	     		
			        	EnableEMS(2); // SEQUENT�AL
			        	EnableExMS(5); // expMode
			        	EnableIAS(3);  // dstar
			     		EnableAS(3);    // dstar
			     		EnableAssS(1);   // greedy   
			    	    EnableLMS(1);    // TSP Path to station
			    	    Enabletf1 ();   // number of tasks choice 
			    	    EnableTSC ();    // stationCount
			    	    EnableRC();   // ROBOT count
			    	    EnableCapacity (); 
			    	    ViewDiameter();
			    	    EnableStationSetNumber();  
			    	    tfRobotSpeed.setText("20"); 
			    	//    HideRS(); 
			    	    
			       break;	
			       
			      			       
			        // Custom
			        case 7: 
	    	     		
			        	EnableEMS(2); // SEQUENT�AL
			        	EnableExMS(5); // expMode
			        	EnableIAS(3);  // dstar
			     		EnableAS(3);    // dstar
			     		EnableAssS(1);   // greedy   
			    	    EnableLMS(1);    // TSP Path to station
			    	    Enabletf1 ();   // number of tasks choice 
			    	    EnableTSC ();    // stationCount
			    	    EnableRC();   // ROBOT count
			    	    EnableCapacity (); 
			    	    ViewDiameter();
			    	    EnableStationSetNumber();  
			    	    tfRobotSpeed.setText("20"); 
			    	    EnableLSS(0);
			    	  //  HideRS(); 
			    	    
			       break;	
			       
			       */
			
			// setupStr = setupSelector.getSelectedItem(); 
			 
			// ExpModeSel(); 
		      
		 }
		 
	 }
	 
	
	
	 
	 class  ExecutionModeSelection implements ItemListener {
		 public void itemStateChanged (final ItemEvent ie) 
		 {
			 
			 sp.executionModeSelection  = eExecutionMode.values() [executionModeSelector.getSelectedIndex()];

		 }
		 
		 
	 }
		protected void onGuiEvent (GuiEvent ev) 
		{

		}

  


}


 