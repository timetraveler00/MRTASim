package MRTAmain;
import jade.core.Agent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

//import com.sun.prism.impl.ps.BaseShaderContext.SpecialShaderType;

import DStarLiteJava.State;
import Djkstra.Edge;
import Djkstra.Vertex;
import Enums.eBidValuation;
import Enums.eExecutionMode;
import Enums.eExecutionPhase;
import Enums.eServiceArea;
import Enums.eTaskState;
import Enums.eTradeStatus;
import Enums.eTradingProgress;
import IndoorStructure.IndoorStructure;
import IndoorStructure.TransitionPoint;
import IndoorStructure.Wall;
import IndoorStructure.WallNode;

import java.awt.image.BufferStrategy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;













import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.core.behaviours.*; 
import jade.lang.acl.ACLMessage;
import jade.core.AID; 



public class TerrainAgent extends GuiAgent{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8800681737316485660L;

	PaintArena f; 

	Robotum[] robots= new Robotum[100];
	TaskBundle[] taskBundles= new TaskBundle[100000];  

	public int chargerCount = 0; 
	public int robotCount = 0;
	public int taskBundleCount = 0;
	public int simTaskCount = 0; 
	public int simRobotCount = 0; 
	
	public String trades[][]= new String [1000][3]; 
	public int tradeCount = 0;  
	
	JRadioButtonMenuItem x1MenuItem, x10MenuItem, x40MenuItem; 
	 ButtonGroup buttonGroup; 

	
	public int simSeedValue = 3;
	  
	ListenNReply listen = new ListenNReply (this, 10);
	JPanel pnl, leftpanel, pnl2, pnl3 ;  
	TextField tf1 ;
	TextField tf2 ;
	TextField tf3 ; 
	Button b1,b2,b3,b4,bScreenShot, bLogandClose ;
	MessageBuffer mb; 
	
	TextField rtf1 ;
	TextField rtf2 ;
	TextField rtf3 ; 
	Button rb1;
	

	public int TERRAIN_WIDTH ; 
	public int TERRAIN_HEIGHT ;  
	public int GUI_WIDTH ;
	public int GUI_HEIGHT;
	public int HOR_OFFSET = 60; 
	public int VER_OFFSET = 100;
	private eExecutionMode EXECUTION_MODE;  
	private eBidValuation TRADE_APPROACH ; 
	private eBidValuation ITRADE_APPROACH ;
	public int RUNNING = 0; 
	public int totalCost = 0;
	public int simSpeed = 40;
	public String setupSelection ; 
	/*LogFrame logFrame;
	TradeLogFrame tradeLogFrame; 
	TaskListFrame taskListFrame;*/ 
	//MessageBufferFrame messageBufferFrame ; 
	
	public int cumulativePaths[] = new int[100] ; 
 	
	Random randomGenerator = new Random(); 
	String templateSelection; 
	
	public IndoorStructure is;
	
	
	Choice cb1, cb2;
	int wMin = 1000000; 
	int wMax = 0;
	int latestTrade = -1; 
	public Trade [] tradeList = new Trade[100];  
	String elapsedTime1 = "0";
	String elapsedTime2 = "0";
	String elapsedTime3 = "0";
	private eExecutionPhase executionPhase ; 
	int auctionsMade = 0; 
	int stationCount = 1; 
	String terrain = "ARENA"; 
	String auctioneer ="M1"; 
	public int autoMode = 0; 
	HeatMap hm ; 
	private eServiceArea expMode = eServiceArea.ALL_TERRAIN; 
	int ucCapacity  = 6; 
	int tradeCapacity = 10; 
	int g_t = 0; 
	int g_s = 0; 
	JCheckBox cbGeneral, cbHeatMap, cbRobotPath, cbMap, cbGrid, cbStations, cbRobots, cbTasks, cbShortInformation, cbDetails, cbBorders, cbTPs;  
	JPanel pCheckBoxGroup; 
	JPanel pLogInfo; 
	double speed = 20.0;
	Color [] colorPalette = {Color.red, Color.blue,  Color.darkGray, Color.cyan, Color.orange, Color.black,  Color.magenta,   Color.yellow,  Color.red, Color.cyan,  Color.green, Color.orange, Color.blue, Color.magenta, Color.yellow, Color.black,Color.red, Color.blue,  Color.magenta, Color.black, Color.orange, Color.cyan,  Color.green,   Color.yellow,  Color.red, Color.cyan,  Color.green, Color.orange, Color.blue, Color.magenta, Color.yellow, Color.black}; 
	//Color [] colorPalette = {Color.red, Color.red,  Color.red, Color.red, Color.red, Color.red,  Color.red,   Color.red,  Color.red, Color.red,  Color.red,Color.red, Color.red,  Color.red, Color.red, Color.red, Color.red,  Color.red,   Color.red,  Color.red, Color.red,  Color.red,Color.red, Color.red,  Color.red, Color.red, Color.red, Color.red,  Color.red,   Color.red,  Color.red, Color.red,  Color.red, Color.red, Color.red, Color.red, Color.red, Color.red}; 
	WayCalculator wc;
	int heatRadius, tasksPerHeat, stationProximity, heatDiameter; 
	int ver_start ; 
	 int hor_start ; 
	
	Task rrtStart, rrtTarget; 
	
	 private MRTATime mrt = new MRTATime(); 
	 Agent thisa = this;
	 
	

	
	IOLogger iol ; 
	
	public String[] execution_modes = {"DYNAMIC OPTIMAL","OPTIMAL + TRADE-BASED","CENTRALIZED","TRADEBASED"};
	int  hmct = 0;
	
	SimParameters sp;
	String callerAgent; 
	
	
	public void getCallerMethod ()  
	{
		
		  final StackTraceElement[] ste = Thread.currentThread().getStackTrace(); 
		  
		  System.out.println("");
		  System.out.println("TerrainAgent-> "+ste[2].getMethodName()+"--"+ste[3].getMethodName()+"--"+ste[4].getMethodName()+"--"+ste[5].getMethodName()+"--");
		  System.out.println("");
		   
	}
    

		
	ActionListener actionListener = new ActionListener() {
	      public void actionPerformed(ActionEvent actionEvent) {
	        AbstractButton aButton = (AbstractButton) actionEvent
	            .getSource();
	        boolean selected = aButton.getModel().isSelected();
	        if (actionEvent.getActionCommand().compareTo("1X")==0) 
	        {
	        	simSpeed = 1; 
	        }
	        else if (actionEvent.getActionCommand().compareTo("10X")==0) 
	        {
	        	simSpeed = 10;
	        }
	        else if (actionEvent.getActionCommand().compareTo("40X")==0) 
	        {
	        	simSpeed = 40;
	        }
	        

	        
	        Yaz(actionEvent.getActionCommand()
	            + " - selected? " + selected);
	      }
	    };
	
		protected void takeDown () {
			
			//taskListFrame.dispose();
		/*	tradeLogFrame.dispose();
			logFrame.dispose();*/
			// f.dispose(); 
		
			/*
			try{
				  //do what you want to do before sleeping
				  Thread.currentThread().sleep(3000);//sleep for 1000 ms
				  //do what you want to do after sleeptig
				}
				catch(InterruptedException ie){
				//If this thread was intrrupted by nother thread 
				}
			tradeLogFrame.dispose();
			try{
				  //do what you want to do before sleeping
				  Thread.currentThread().sleep(3000);//sleep for 1000 ms
				  //do what you want to do after sleeptig
				}
				catch(InterruptedException ie){
				//If this thread was intrrupted by nother thread 
				}
			taskListFrame.dispose();*/
			  
		}
		
		public void Yaz(String st) 
		{
			 
			//getCallerMethod();
			iol.Yaz(st);
			 	 
		}

		ActionListener onUpActionListener = new ActionListener() {
		      public void actionPerformed(ActionEvent actionEvent) {
		        if (ver_start>10) 
		        {
		        	ver_start-=10;
		         f.drawStuff();
		        }
		      }
		    };
		    
		    
		    ActionListener oUpActionListener = new ActionListener() {
			      public void actionPerformed(ActionEvent actionEvent) {
			        if (VER_OFFSET>10) 
			        {
			        	VER_OFFSET-=10;
			         f.drawStuff();
			        }
			      }
			    };
			    
				ActionListener oDownActionListener = new ActionListener() {
				      public void actionPerformed(ActionEvent actionEvent) {
				        if (VER_OFFSET<200) 
				        {
				        	VER_OFFSET+=10;
				             f.drawStuff();
				        }
				      }
				    };
				    
					ActionListener oRightActionListener = new ActionListener() {
					      public void actionPerformed(ActionEvent actionEvent) {
					        if (HOR_OFFSET<200) 
					        {
					        	HOR_OFFSET+=10;
					            f.drawStuff();
					        } 
					      }
					    };
					
					    ActionListener oLeftActionListener = new ActionListener() {
						      public void actionPerformed(ActionEvent actionEvent) {
						        if (HOR_OFFSET>10) 
						        {
						        	HOR_OFFSET-=10;
						            f.drawStuff();
						        } 
						      }
						    };
							ActionListener onDownActionListener = new ActionListener() {
							      public void actionPerformed(ActionEvent actionEvent) {
							        if (ver_start<TERRAIN_HEIGHT-200) 
							        {
							        	ver_start+=10;
							             f.drawStuff();
							        }
							      }
							    };
							    ActionListener onLeftActionListener = new ActionListener() {
								      public void actionPerformed(ActionEvent actionEvent) {
								        if (hor_start>10) 
								        {
								        	hor_start-=10;
								            f.drawStuff();
								        } 
								      }
								    };
									ActionListener onRightActionListener = new ActionListener() {
									      public void actionPerformed(ActionEvent actionEvent) {
									        if (hor_start<TERRAIN_WIDTH+400) 
									        {
									        	hor_start+=10;
									            f.drawStuff();
									        } 
									      }
									    };
    public void ArgumentParse(Object args[]) 
    {
    	getCallerMethod();
    	  	
    	
    	if (args != null) 
      	{
    		terrain = args[0].toString(); //sp.templateSelection;
    		callerAgent = args[1].toString();
    		auctioneer = args[2].toString();
    		sp = (SimParameters) args [3]; 
    		
    		
 	    	templateSelection =  sp.templateSelection; //args[1].toString();
 	    	EXECUTION_MODE = sp.executionModeSelection;  //  [Integer.parseInt(args[2].toString())];
 	    	TRADE_APPROACH =  sp.approachSelection;  // eBidValuation.values()  [Integer.parseInt(args[3].toString())];
 	    	ITRADE_APPROACH =  sp.iapproachSelection; //eBidValuation.values()  [Integer.parseInt(args[9].toString())];
 	    	simTaskCount = sp.taskCount; //Integer.parseInt(args[5].toString());
 	    	simRobotCount = sp.robotCount; //Integer.parseInt(args[6].toString());
 	    	TERRAIN_WIDTH = sp.TERRAIN_WIDTH; //Integer.parseInt(args[7].toString());
 	    	TERRAIN_HEIGHT = sp.TERRAIN_HEIGHT; // Integer.parseInt(args[8].toString());
 	    	
 	    	stationCount  = sp.stationCount; // Integer.parseInt(args[10].toString());
 	    	autoMode = sp.autoMode; // Integer.parseInt(args[11].toString());
 	    	expMode =  sp.expMode; //eServiceArea.values() [Integer.parseInt(args[12].toString())];
 	    	ucCapacity = sp.ucCapacity; // Integer.parseInt(args[13].toString());
 	    	tradeCapacity = sp.tradeCapacity; // Integer.parseInt(args[14].toString());
 			g_s = sp.stationSetNumber; //Integer.parseInt(args[15].toString());
 		    g_t =  sp.taskSetNumber; // Integer.parseInt(args[16].toString());
 		    heatRadius = sp.heatDiameter ; // Integer.parseInt(args[17].toString());
 		    tasksPerHeat = sp.tasksPerHeat; // Integer.parseInt(args[18].toString());
 		    stationProximity = sp.stationProx; // Integer.parseInt(args[19].toString());
 		    setupSelection = sp.setupSelection; // args[20].toString(); 
 		    speed = sp.robotSpeed; // Integer.parseInt(args[21].toString());
 		    
 		    
 		    ver_start = 400; 
 			hor_start = TERRAIN_WIDTH + HOR_OFFSET ; 
 		    
 		   Yaz ("----------------------------------------------------------------------TERRAIN : "+terrain ); 	
 		   
 		
    		
    		
    		/*
    		terrain = args[0].toString();
 	    	templateSelection =  args[1].toString();
 	    	EXECUTION_MODE = eExecutionMode.values()  [Integer.parseInt(args[2].toString())];
 	    	TRADE_APPROACH =  eBidValuation.values()  [Integer.parseInt(args[3].toString())];
 	    	auctioneer = args[4].toString();
 	    	simTaskCount = Integer.parseInt(args[5].toString());
 	    	simRobotCount = Integer.parseInt(args[6].toString());
 	    	TERRAIN_WIDTH = Integer.parseInt(args[7].toString());
 	    	TERRAIN_HEIGHT = Integer.parseInt(args[8].toString());
 	    	ITRADE_APPROACH =  eBidValuation.values()  [Integer.parseInt(args[9].toString())];
 	    	stationCount  = Integer.parseInt(args[10].toString());
 	    	autoMode = Integer.parseInt(args[11].toString());
 	    	expMode =  eServiceArea.values() [Integer.parseInt(args[12].toString())];
 	    	ucCapacity = Integer.parseInt(args[13].toString());
 	    	tradeCapacity = Integer.parseInt(args[14].toString());
 			g_s = Integer.parseInt(args[15].toString());
 		    g_t =  Integer.parseInt(args[16].toString());
 		    heatRadius = Integer.parseInt(args[17].toString());
 		    tasksPerHeat = Integer.parseInt(args[18].toString());
 		    stationProximity = Integer.parseInt(args[19].toString());
 		    setupSelection = args[20].toString(); 
 		    speed = Integer.parseInt(args[21].toString());
 		    
 		  ver_start = 400; 
 			hor_start = TERRAIN_WIDTH + HOR_OFFSET ; 
 		    
 		   Yaz ("----------------------------------------------------------------------TERRAIN : "+terrain ); 	
 		   for (int i=0; i<22; i++) 
 		   {
 			   Yaz ("arguman ["+i+"] "+args[i].toString());
 		   }
 		   */ 
 	    	
 		    
      	}else 
      	{
      	    Yaz ("TERRAIN : where are the args?" ); 	
      	}
    	
    	Yaz ("----------------------------------------------------------------------TERRAIN : "+terrain ); 	
    }
    public void CheckBoxInitialization () 
    {
		/*cbGeneral = new JCheckBox("Robot Coverage",false); 
		cbTasks = new JCheckBox("Tasks",true);
		cbRobots = new JCheckBox("Robots",false);
		cbRobotPath= new JCheckBox("Robot History",true);
		cbGrid = new JCheckBox("Grid",true);
		cbMap = new JCheckBox("Satellite Image",true);
		cbBorders = new JCheckBox("Borders",true);
		cbTPs = new JCheckBox("Transition Points",false);
		cbStations = new JCheckBox("Stations",false);
		cbShortInformation = new JCheckBox("General Info",false);
		cbDetails = new JCheckBox("Details",false);
		cbHeatMap = new JCheckBox("Heat Map",true);
		*/
		cbGeneral = new JCheckBox("Robot Coverage",false); 
		cbTasks = new JCheckBox("Tasks",true);
		cbRobots = new JCheckBox("Robots",true);
		cbRobotPath= new JCheckBox("Robot History",true);
		cbGrid = new JCheckBox("Grid",false);
		cbMap = new JCheckBox("Satellite Image",true);
		cbBorders = new JCheckBox("Borders",true);
		cbTPs = new JCheckBox("Transition Points",false);
		cbStations = new JCheckBox("Stations",true);
		cbShortInformation = new JCheckBox("General Info",true);
		cbDetails = new JCheckBox("Details",true);
		cbHeatMap = new JCheckBox("Heat Map",true);
    }
    public void CheckBoxListeners() 
    {
    	CheckBoxListener cbl = new CheckBoxListener(); 
		cbGeneral.addActionListener(cbl); 		
		cbTasks.addActionListener(cbl);
		cbRobots.addActionListener(cbl);
		cbRobotPath.addActionListener(cbl);
		cbGrid.addActionListener(cbl);
		cbMap.addActionListener(cbl);
		cbBorders.addActionListener(cbl);
		cbTPs.addActionListener(cbl);
		cbStations.addActionListener(cbl);
		cbShortInformation.addActionListener(cbl);
		cbDetails.addActionListener(cbl);
		cbHeatMap.addActionListener(cbl); 		
		pCheckBoxGroup.setLayout(new GridLayout(0,6) );
		pCheckBoxGroup.add(cbGeneral); 
		pCheckBoxGroup.add(cbTasks); 
		pCheckBoxGroup.add(cbRobots);
		pCheckBoxGroup.add(cbRobotPath);
		pCheckBoxGroup.add(cbGrid);
		pCheckBoxGroup.add(cbMap);
		pCheckBoxGroup.add(cbBorders);
		pCheckBoxGroup.add(cbTPs);
		pCheckBoxGroup.add(cbStations);
		pCheckBoxGroup.add(cbShortInformation);
		pCheckBoxGroup.add(cbDetails);
		pCheckBoxGroup.add(cbHeatMap); 
    }
    public void SimSpeedButtonGroup() 
    {
    	   buttonGroup = new ButtonGroup();
		   

 		  x1MenuItem = new JRadioButtonMenuItem("1X");
 		  x1MenuItem.addActionListener(actionListener);
           buttonGroup.add(x1MenuItem);
 		   

 		  x10MenuItem = new JRadioButtonMenuItem("10X");
 		  x10MenuItem.addActionListener(actionListener);
 		  buttonGroup.add(x10MenuItem);
 		
 		  x40MenuItem = new JRadioButtonMenuItem("40X",true);
 		  x40MenuItem.addActionListener(actionListener);
 		  buttonGroup.add(x40MenuItem);
 		  
 			Panel panel3 = new Panel(); 
 			panel3.setLayout(new GridLayout(0,1));
 			panel3.setBackground(Color.GREEN);

 			panel3.add(x1MenuItem); 
 			panel3.add(x10MenuItem); 
 			panel3.add(x40MenuItem); 
 			pnl.add(panel3);
 			
 			
    }
    public void ShiftingButtons () 
    {
    	Button oUp = new Button ("Up ^"); 
		Button oDown = new Button ("Down |"); 
		Button oRight = new Button ("Right >");
		Button oLeft = new Button ("< Left ");
		
		
		oUp.addActionListener(oUpActionListener); 
		oDown.addActionListener(oDownActionListener); 
		oRight.addActionListener(oRightActionListener);
        oLeft.addActionListener(oLeftActionListener);
		
		Button onUp = new Button ("Up ^"); 
		Button onDown = new Button ("Down |"); 
		Button onRight = new Button ("Right >");
		Button onLeft = new Button ("< Left ");
		
		
		onUp.addActionListener(onUpActionListener); 
		
	
		
		onDown.addActionListener(onDownActionListener); 
		
	
		
		onRight.addActionListener(onRightActionListener);
		
		
		
		onLeft.addActionListener(onLeftActionListener);
		
		pnl.add(new Button(" ")); 
		pnl.add(oLeft); 
		pnl.add(oUp); 
		pnl.add(oDown); 
		pnl.add(oRight); 
		pnl.add(onLeft); 
		pnl.add(onUp); 
		pnl.add(onDown); 
		pnl.add(onRight); 
    }
    public void PanelInitialization () 
    {
    	getCallerMethod();
    	Button b= new Button("A R E N A"); 

    	pnl = new JPanel();
    	pnl.setPreferredSize(new Dimension(400, 300));
		pnl2 = new JPanel();
		pnl3 = new JPanel();
		tf1 = new TextField("T12"); 
		tf2 = new TextField("200");
		tf3 = new TextField("100");
		b1 = new Button("Add Task");
		rtf1 = new TextField("R21"); 
		rtf2 = new TextField("200");
		rtf3 = new TextField("100");
		rb1 = new Button("Add Robot");
		b2 = new Button("Start Simulation");
		b3 = new Button("Pause Simulation");  
		b4 = new Button("Stop Simulation");
		bLogandClose = new Button("Log&Close"); 
		bScreenShot = new Button ("Screen Capture"); 
		pnl.setLayout(new GridLayout(0,4));

		pnl.add(tf1);
		pnl.add(tf2); 
		pnl.add(tf3);
		pnl.add(b1);
		pnl.add(rtf1);
		pnl.add(rtf2); 
		pnl.add(rtf3);
		pnl.add(rb1);

		pnl.add(b2); 
		pnl.add(b3);
		
		pnl.add(b4);
		
		pnl.add(b);

		pnl.add(bLogandClose);
		pnl.add(bScreenShot); 
		
	    
		JPanel pp1, pp2,pp3, pp4,pp5, pp6,pp7,pp8,pp9,pp10; 
		
		leftpanel =new JPanel();
		leftpanel.setBackground(Color.BLUE);
		leftpanel.setLayout(new GridLayout(0,1));
		pnl.setBackground(Color.CYAN);
		pp1=new JPanel();
	    pp2=new JPanel();
	    pp3=new JPanel();
	    pp4=new JPanel();
	    pp5=new JPanel();
	    pp6=new JPanel();
	    pp7=new JPanel();
	    pp8=new JPanel();
	    pp9=new JPanel();
	    pp10=new JPanel();
	    leftpanel.add(pnl);
	    leftpanel.add(pp1);
	    leftpanel.add(pp2);
	    if (TERRAIN_HEIGHT>400) leftpanel.add(pp3);
	    if (TERRAIN_HEIGHT>600)  leftpanel.add(pp4);
	    if (TERRAIN_HEIGHT>800)  leftpanel.add(pp5);
	    if (TERRAIN_HEIGHT>1000)  leftpanel.add(pp6);
	  //  leftpanel.add(pp6);
	  //  leftpanel.add(pp7);
	   // leftpanel.add(pp8);
	   // leftpanel.add(pp9);
	   // leftpanel.add(pp10);
		
		
		//pnl2.setBounds(HOR_OFFSET, VER_OFFSET, 1200, 900);
		//pnl2.add(b1);
		
		//pnl3.setBounds(HOR_OFFSET, VER_OFFSET, 1200, 900);
		//pnl3.add(b1);
		
		
		ButtonActionListener al = new ButtonActionListener(); 
		b.addActionListener(al); 
    }
    public void PanelListeners() 
    {
    	getCallerMethod();
    	AddTaskListener at1 = new AddTaskListener();
    	AddRobotListener rt1 = new AddRobotListener(); 
		StartSimListener sts1 = new StartSimListener();
		PauseSimListener ps1 = new PauseSimListener();
		StopSimListener  sos1 = new StopSimListener();
		ScreenCapture sc1 = new ScreenCapture();
		SaveAndCloseListener lac = new SaveAndCloseListener();   
		
		
		b1.addActionListener(at1); 
		rb1.addActionListener(rt1);
		b2.addActionListener(sts1);
		b3.addActionListener(ps1);
		b4.addActionListener(sos1);
		bLogandClose.addActionListener(lac); 
		bScreenShot.addActionListener(sc1); 
    }
    public void HeatMapInitialization () 
    {
		if (expMode == eServiceArea.EXTENDEDRECTANGULAR) 
		{
			long begin = System.currentTimeMillis();
			hm = new HeatMap(templateSelection, heatRadius, tasksPerHeat, stationProximity); 
			//hm.drawHeatMap(); 
			//hm.ReviseClusters(); 
			hm.ClusterCenters();
			long end = System.currentTimeMillis();
			hmct = (int) (end-begin)/1000 ; 
	
		}
    }
    public void InitializePaintArena ()
    {
    	getCallerMethod();
    	f = new PaintArena(); 
		System.out.println("GUI_WIDTH: "+ GUI_WIDTH + " GUI_HEIGHT: "+GUI_HEIGHT);
		f.setTitle("MRTASim"); 
     	f.setBounds(0,0,GUI_WIDTH,GUI_HEIGHT);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		//f.setUndecorated(true);
	
		//f.add(b,BorderLayout.NORTH);
		f.add(leftpanel,BorderLayout.LINE_END); 
		f.add(pCheckBoxGroup,BorderLayout.PAGE_START);
		//f.add(pnl2,BorderLayout.LINE_START);
		//f.add(pnl3,BorderLayout.CENTER);
		//f.add(pLogInfo,BorderLayout.EAST); 
		

		 
		f.setVisible (true);
		f.createBufferStrategy(2); 
				f.CreateIndoorStructure(); 
    }
	protected void setup () {

      getCallerMethod();
      iol = new IOLogger("TerrainAgent", autoMode);
    	Object[] args = getArguments(); 
	    ArgumentParse (args);
	            
	    Yaz ("setup ---------------------------------------------------------TERRAIN : "+terrain ); 	
		mb = new MessageBuffer(20); 
		wc = new WayCalculator(); 
		Yaz("TERRAIN_WIDTH : "+TERRAIN_WIDTH);
		Yaz("TERRAIN_HEIGHT : "+TERRAIN_HEIGHT);
	    GUI_WIDTH = TERRAIN_WIDTH + HOR_OFFSET*2 +450; 
		//GUI_HEIGHT = TERRAIN_HEIGHT < 500 ? TERRAIN_HEIGHT + 160 + VER_OFFSET*2 : 980;
	    GUI_HEIGHT = TERRAIN_HEIGHT + VER_OFFSET*2 + 100;
			
		
		pLogInfo = new JPanel(); 
		pCheckBoxGroup = new JPanel(); 	
        PanelInitialization(); 
        PanelListeners(); 
		
		CheckBoxInitialization () ; 
		CheckBoxListeners(); 
		
		
		SimSpeedButtonGroup(); 
		ShiftingButtons(); 
		 
		InitializePaintArena();  	
 

		addBehaviour(listen);
	 
        addBehaviour(new RefreshTicker(this, 500)); 

		
		//is.ToString(); 
		Yaz("----------------------------------------------------------");
		// f.BuildDStarLiteX(363, 276, 250, 225); //traderbots r1-t7
		//f.BuildDStarLiteX(99, 312, 875,330); // iZM�R
		//f.BuildDStarLiteX(99, 312, 1249, 686);
		// f.BuildDStarLite(80, 260, 140,540);
		 //f.BuildDStarLite(99, 312, 1249, 686);"
		// f.BuildDStarLite(99, 312, 875, 330);
		HeatMapInitialization();
        f.drawStuff();
		// f.BuildRRT_BiDirect(420,420,230,220); // maze2
        //f.BuildRRT_BiDirect(350,260, 250,220); // traderbots
        //f.BuildRRT(350,260, 220,170); // traderbots

        //TakeScreenShot();
		// f.BuildRRT_AHL(); 
		//f.BuildRRT_Malatya(); 
		// f.BuildRRT_Izmir();
		//f.BuildRRT_temp7();
		//System.err.println("RRT bitti...");
		//is.GenerateNodeMap_Auto(); 
		//FileReader fr = new FileReader(); 
		//fr.WriteTransitionPoints(templateSelection, is);
	   // fr.WriteRoadMap(templateSelection, is);

        
		
		//f.drawStuff(); 
	 
	    is.ToString(); 
	
	}
	
	public void ResetTaskBundles() 
	{
		for (int i=0; i<taskBundleCount; i++) 
		{
			taskBundles [i] = null; 		
		}
		taskBundleCount = 0; 
		
			}
	
	public void TakeScreenShot ()  
	{
		try {
			    
			f.setExtendedState(JFrame.NORMAL);
			f.setAlwaysOnTop(true);
			f.requestFocus();
			f.setAlwaysOnTop(false);
			
			/*f.setAlwaysOnTop(true); 	
			    f.toFront(); 
			    
			    f.repaint();
				f.setAlwaysOnTop(true);*/ 
			     f.drawStuff(); 
				
				File file = new File(".");
				String fileName = "_"; 		
				fileName = file.getAbsolutePath() + "\\..\\..\\Experiments\\"+templateSelection+"\\ScreenShots\\"+ templateSelection +"_"+EXECUTION_MODE+"_"+Integer.toString(simTaskCount)+"T_"+Integer.toString(simRobotCount)+"R_"+Integer.toString(totalCost)+"-"+Integer.toString(randomGenerator.nextInt(999))+".jpeg";
				//BufferedImage screenCapture = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				BufferedImage screenCapture = new Robot().createScreenCapture(new Rectangle(0, 0 , GUI_WIDTH, GUI_HEIGHT));	
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
	
	
	public String GetDirectory() {
        String filePath = "C:\\MRTAFolder.txt"; // File path
        String line;
        String pth = ""; 
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                // Split the line by "_" and print each parameter
                String[] parameters = line.split("_");
                
                for (String parameter : parameters) {
                    System.out.println(parameter);
                }
                System.out.println("-----"); // Separator for each line
                pth = parameters[0] + parameters[1];
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return pth;
    }
	public void TakeScreenShot_Exp ()  
	{
		try {
			
			f.setAlwaysOnTop(true); 	
		    f.toFront(); 
		    
		    f.repaint();
			f.setAlwaysOnTop(true);
			f.drawStuff(); 
		    File file = new File(".");
			String fileName = "_"; 		
			//fileName = fileName + Integer.toString(simTaskCount) +"T_"+Integer.toString(simRobotCount) +"R_Seed_"+Integer.toString(simSeedValue);
			//fileName = fileName + "_"+ execution_modes[EXECUTION_MODE] +"_"+trade_approaches[TRADE_APPROACH]+"_"+Integer.toString(totalCost)+"_"+Integer.toString(randomGenerator.nextInt(999))+".jpeg"; 	
			//fileName = templateSelection +"_"+EXECUTION_MODE+"_"+Integer.toString(simTaskCount)+"T_"+Integer.toString(simRobotCount)+"R_"+Integer.toString(randomGenerator.nextInt(999))+".jpeg";
			fileName = GetDirectory() + "\\Experiments\\"+ templateSelection +"\\"+EXECUTION_MODE+"_"+Integer.toString(simTaskCount)+"T_"+Integer.toString(simRobotCount)+"_"+Integer.toString(totalCost)+"R_"+Integer.toString(randomGenerator.nextInt(999))+".jpeg";
			//BufferedImage screenCapture = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

			BufferedImage screenCapture = new Robot().createScreenCapture(new Rectangle(0, 0 , GUI_WIDTH, GUI_HEIGHT));
			//BufferedImage screenCapture = new Robot().createScreenCapture(new Rectangle(0,0,TERRAIN_WIDTH+HOR_OFFSET*2,TERRAIN_HEIGHT+VER_OFFSET*2));
			
			
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


	protected void onGuiEvent (GuiEvent ev) 
	{

	}

	public class LogFrame extends JFrame 
	{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 492802524745736162L;

		Graphics g = null;
		
		private int logCoordinates [][] = new int [100][2] ; 
		private int logCount = 0; 
		String logs [] = new String [100];  
		/*public void paint (Graphics g)
		{
		//	 g.drawString ("sdfsdfsdfsdfsdf", 10,100);     
		}*/
		
		public void Dispose () 
		{
			if (g!=null)
			    g.dispose(); 
		}
		public void Reset() 
		{
			logCount = 0; 
		}
 		public void Show() 
		{
		
			
			BufferStrategy bf = this.getBufferStrategy();
			
		
			try 
			{
			     g = bf.getDrawGraphics(); 	
			     g.setFont(new Font("arial",Font.PLAIN,10));
			     Rectangle bounds= this.getBounds();
			     Color r= g.getColor(); 
			     g.setColor(Color.white);
			     g.fillRect(0, 0, bounds.width,bounds.height);
			     g.setColor(r); 
			     for (int i=0; i<logCount ;i++ ) 
			    	 g.drawString(logs[i], logCoordinates[i][0], logCoordinates[i][1]); 
			      
			    
			  }
			finally 
			{
				if (g!=null)
				    g.dispose(); 
			}
			
			bf.show(); 
			Toolkit.getDefaultToolkit().sync(); 
			
			 
		}
 		
 		public void writeSomething(int x, int y, String msg) 
 		{
 			  logCoordinates [logCount][0] = x;   
 			  logCoordinates [logCount][1] = y;
 			  logs [logCount++] = msg;
 			 
 		}
		
	}
	
	public class TradeLogFrame extends LogFrame
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1720381804675732769L;
		
	
		
	}
	
	public class TaskListFrame extends LogFrame 
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 304898650961140506L;
		
		
	
		
	}
	
	public class PaintArena extends JFrame 
	{
		
		 /**
		 * 
		 */
		private static final long serialVersionUID = 6447614454181784759L;
		int c=0,delta=20;
		 //int maxX=500,maxY=500;
		   int maxC = 9999999; 
		  int [] x = new int [1000000];
		  int [] y = new int [100000];

		  int [] from = new int [1000000];
		  int cCount = 0; 
		  
			 int c2=0,delta2=20;
			 //int maxX=500,maxY=500;
			   int maxC2 = 9999999; 
			  int [] x2 = new int [1000000];
			  int [] y2 = new int [1000000];

			  int [] from2 = new int [1000000]; 
			  int cCount2 = 0; 
			  private BufferedImage robotIcon;
			  private BufferedImage imageMain;
			  private BufferedImage imageStation ;
			  
			  int rx,ry,rx2,ry2;  
			    double d,RRTmin;
				
			    double d2,RRTmin2;
			    int n=0, n2=0;
			  
			
			  
			  

				 int tour =0; 
				 List<State> path ;
				 int mf  =1  ; 
				 int pfMatrix [][] = new int [2000][1500];


		public PaintArena () 
		{
		  getCallerMethod();
			Yaz("PaintArena>CONSTRUCTOR for "+templateSelection);
		    File f = new File(".");  
		    String str = "image_other.jpg"; 
		    
	/*
		    if (expMode == eServiceArea.EXTENDEDRECTANGULAR)
		    	str = "image_FODFocus.jpg"; 
			  
		    else if (expMode == eServiceArea.CIRCULAR)
		    	str = "image_HippoBots.jpg";
		    
		    else if (expMode == eServiceArea.RECTANGULAR)
		    	str = "image_FollowMe.jpg"; 
		    else 
     			 str = "image_other.jpg" ;
     			 */
		    str = "postcar3.jpg";

	       try {                
	 		  imageMain = ImageIO.read(new File(GetDirectory() + "\\config\\"+ templateSelection +"\\"+"background.jpg"));
			       } catch (IOException ex) {
		            // handle exception...
		       }

             try {                
		          
				  imageStation = ImageIO.read(new File(f.getAbsolutePath() +"\\..\\icons\\postoffice.jpg"));
		       } catch (IOException ex) {
		            // handle exception...
		       }
             
             try {                
				  robotIcon = ImageIO.read(new File(f.getAbsolutePath() + "\\..\\icons\\" +str));
		       } catch (IOException ex) {
		            // handle exception...
		       }
		}
		public boolean hit(int x,int y) {
			   
			return is.InterSection(new Wall(new WallNode(x-4, y-4),new WallNode(x+4, y+4))); 

			  }
		public boolean linehit(int xs,int ys, int xe, int ye) {
			   
			return is.InterSection(new Wall(new WallNode(xs, ys),new WallNode(xe, ye))); 

			  }
		
		
		
		public int[] Sort (int [] arr, int length)
		{
			int temp = 0; 
			for (int i=0; i<length; i++) 
			{
				for (int j=i; j<length; j++) 
				{
					if (arr[j]<arr[i]) 
					{
						temp = arr[i]; 
						arr[i] = arr[j]; 
						arr[j] = temp; 
						
					}
				}
				
			}
			
			return arr; 
		}
		
		
		public int GetTPIndex (TransitionPoint [] tl, int count, String tpName) 
		{
			
			for (int i=0; i<count; i++) 
			{
				if (tl[i].tpname.compareTo(tpName) == 0) 
					return i; 
			}
			
			return -1; 
		}
		
		public String GetTPName (TransitionPoint [] tl, int count,  int index) 
		{
			for (int i=0; i<count; i++) 
			{
				if (i == index) 
					return tl[i].tpname; 
			}
			return ""; 
		}
		
		public boolean VisibilityObtained(TransitionPoint [] tl, int count1, TransitionPoint [] tl2, int count2, TransitionPoint [] TP3, int tot) 
		{
			
			
			for (int i=0; i<count1; i++) 
			{
				TransitionPoint tp1 = tl[i]; 
				WallNode wi = new WallNode(tp1.xLoc, tp1.yLoc) ;
				
				for (int j=0; j<count2; j++) 
				{
					TransitionPoint tp2 = tl2[j];
					
					WallNode wj = new WallNode(tp2.xLoc, tp2.yLoc) ;
					
					Wall ij = new Wall (wi, wj); 
					
					if (!is.InterSection(ij)) 
					{
						
						   int mesafe2 = (int) CalcDistance(tp1.xLoc, tp1.yLoc, tp2.xLoc, tp2.yLoc); 
				          int qn = GetTPIndex(TP3, tot+1, tp1.tpname); 
				          int qc = GetTPIndex(TP3, tot+1, tp2.tpname);   
						  is.addLane("x", qn, qc, mesafe2);
				          is.addLane("x", qc, qn, mesafe2);
						
						return true; 
					}
					
				}
				
				
			}
			
			return false;  
			
		}
		
		public void RandomStartPoints () 
		{
		      do {
		          rx=(int)(Math.random()*TERRAIN_WIDTH);
		          ry=(int)(Math.random()*TERRAIN_HEIGHT);
		          rx2=(int)(Math.random()*TERRAIN_WIDTH);
		          ry2=(int)(Math.random()*TERRAIN_HEIGHT);
		        } while(hit(rx,ry) && hit(rx2,ry2));
		}
		
		public void StepSize1 () 
		{
			// System.out.println("stepsize1 G�R��� d  RRTmin "+RRTmin + " n "+n );
			for (int i=0;i<c;i++) {
		          d=Math.sqrt((x[i]-rx)*(x[i]-rx)+(y[i]-ry)*(y[i]-ry));
		         // System.out.println("stepsize1 d "+d + " RRTmin "+RRTmin + " n "+n + " i "+i + " c "+c + " rx "+rx+" ry "+ry+" x[i]"+x[i]+" y[i] "+y[i]);
		          if (RRTmin==-1 || d<RRTmin) {
		            RRTmin=d; n=i;
		          }
		        }
		}
		public void StepSize2() 
		{
	        for (int i=0;i<c2;i++) {
		          d2=Math.sqrt((x2[i]-rx2)*(x2[i]-rx2)+(y2[i]-ry2)*(y2[i]-ry2));
		          if (RRTmin2==-1 || d2<RRTmin2) {
		            RRTmin2=d2; n2=i;
		          }
		        }
		}
		public void BuildRRT_BiDirect(int startx, int starty, int goalx, int goaly) 
		{
			 
			is.nodes = new ArrayList<Vertex>();
			is.edges = new ArrayList<Edge>();  
			is.tpoints = new ArrayList<TransitionPoint>();
			 x[0]= startx; y[0]=starty;
			 x2[0]= goalx; y2[0]=goaly;
			 int tot = 0; 
			 TransitionPoint [] TP1 = new TransitionPoint [10000] ; 
			 TransitionPoint [] TP2 = new TransitionPoint [10000] ; 
			 TransitionPoint [] TP3 = new TransitionPoint [10000] ; 
			 TransitionPoint tp1 =  new TransitionPoint(x[c], y[c], "TPc"+Integer.toString(c)); 
			 TransitionPoint tp2 =  new TransitionPoint(x2[c2], y2[c2], "TP2c"+Integer.toString(c2)); 
			 is.SetTP(tp1);
			 is.SetTP(tp2);
			 TP1[0] = tp1; 
			 TP2[0] = tp2; 
			 TP3[tot++] = tp1; 
			 TP3[tot++] = tp2; 
		
			 c= 1;  
			 c2=1; 
			 
			 

			 Graphics2D g = (Graphics2D) this.getGraphics(); 
			 int color;
			 int color2;
			 // while (c<maxC) 
			 while (!VisibilityObtained(TP1, c, TP2, c2, TP3, tot))
			 {
				 Yaz("RRT while");

			      
			         RandomStartPoints(); 

			        g.setColor(Color.yellow);
			        
			        // den knoten mit der geringsten distanz zum punkt suchen
			        RRTmin=-1;
			        RRTmin2=-1; 

			        

			        d=RRTmin; 
			        d2=RRTmin2; 
			        StepSize1(); 
			        StepSize2(); 
				 
			        // maximal mit der entfernung delta springen
			        if (delta<d) d=delta;
			        
			        if (delta2<d2) d2=delta2;

			        // neuen knoten berechnen
			        x[c]=x[n]+(int)(d*(rx-x[n])/RRTmin);
			        y[c]=y[n]+(int)(d*(ry-y[n])/RRTmin);
			        
			        // neuen knoten berechnen
			        x2[c2]=x2[n2]+(int)(d2*(rx2-x2[n2])/RRTmin2);
			        y2[c2]=y2[n2]+(int)(d2*(ry2-y2[n2])/RRTmin2);

			        // das gr�n etwas dunkler machen um so weiter der baum fortgeschritten ist
			        color=255-120*c/maxC;
			        color2=255-120*c2/maxC2;

			        // nochmal pr�fen ob der neue knoten nicht in ein hindernis f�llt
			        if (!linehit(x[n], y[n], x[c],y[c])) {
			        	 Yaz("RRT linehit");
			        	Color r = g.getColor(); 
			        	g.setColor(new Color(color/2,color/3,color));
			          g.drawLine(x[n]+HOR_OFFSET,y[n]+VER_OFFSET,x[c]+HOR_OFFSET,y[c]+VER_OFFSET);
			          g.setColor(r); 
			          from [c] = n;
			          TransitionPoint tpc = new TransitionPoint(x[c], y[c], "TPc"+Integer.toString(c)); 
			          is.SetTP(tpc);
			          TP1[c] = tpc; 
			          TP3[tot++] = tpc; 
			          int mesafe = (int) CalcDistance(x[n], y[n], x[c],y[c]); 
			          	Yaz("RRT : n : " + Integer.toString(n) +" c : "+ Integer.toString(c) + " From ["+Integer.toString(x[n]) +";"+ Integer.toString(y[n])+"]  - To ["+Integer.toString(x[c])+";"+Integer.toString(y[c]) + "] mesafe : "+ Integer.toString(mesafe)); 
			          
			          String nName = GetTPName (TP1, c, n); 	
			          String cName = GetTPName (TP1, c+1, c); 
			          int qn = GetTPIndex(TP3, tot, nName); 
			          int qc = GetTPIndex(TP3, tot, cName); 
			          is.addLane("x", qn, qc, mesafe);
			          is.addLane("x", qc, qn, mesafe);
			          c++;
			        }
			        
			        if (!linehit(x2[n2], y2[n2], x2[c2],y2[c2])) {
			        	 Yaz("RRT linehit 2");
			        	 Color r = g.getColor(); 
			        	 // g.setColor(new Color(color2/2,color2,color2/2));
			        	 g.setColor(new Color(color2,color2/2,color2/2));

			          g.drawLine(x2[n2]+HOR_OFFSET,y2[n2]+VER_OFFSET,x2[c2]+HOR_OFFSET,y2[c2]+VER_OFFSET);
			          g.setColor(r); 
			          from2 [c2] = n2;
			          TransitionPoint tpc2 = new TransitionPoint(x2[c2], y2[c2], "TP2c"+Integer.toString(c2)); 
			          is.SetTP(tpc2);
			          TP2[c2] = tpc2; 
			          TP3[tot++] = tpc2; 
			          int mesafe2 = (int) CalcDistance(x2[n2], y2[n2], x2[c2],y2[c2]); 
			          	Yaz("RRT : n2 : " + Integer.toString(n2) +" c2 : "+ Integer.toString(c2) + " From2 ["+Integer.toString(x2[n2]) +";"+ Integer.toString(y2[n2])+"]  - To2 ["+Integer.toString(x2[c2])+";"+Integer.toString(y2[c2]) + "] mesafe2 : "+ Integer.toString(mesafe2)); 
				         
			          	String nName = GetTPName (TP2, c2, n2); 	
				          String cName = GetTPName (TP2, c2+1, c2); 
				          int qn = GetTPIndex(TP3, tot, nName); 
				          int qc = GetTPIndex(TP3, tot, cName); 
				          is.addLane("x", qn, qc, mesafe2);
				          is.addLane("x", qc, qn, mesafe2);

			          c2++;
			        }
			        
			        
			        cCount = c;
			        cCount2 = c2;
			        
			    
					  
			 }
			 is.ListTPs(); 
				
			 try{
				  //do what you want to do before sleeping
				  Thread.currentThread().sleep(27000);//sleep for 1000 ms
				  //do what you want to do after sleeptig
				}
				catch(InterruptedException ie){
				//If this thread was intrrupted by nother thread 
				}
			
			 g.dispose();
		}
		/*
		public void BuildDStarLiteX(int startx, int starty, int targetx, int targety) 
		{
		   
			Yaz("***** DSTARLITE ********");
			DStarLite pf = new DStarLite();
			//pf.init(startx,starty, targetx,targety);
			for (int i=1; i<TERRAIN_WIDTH-1; i++) 
			for (int j=1; j<TERRAIN_HEIGHT-1; j++) 
			{
				Wall w = new Wall (i-2, j-2, i+2, j+2); 
				Wall w2 = new Wall (i+2, j-2, i-2, j+2); 
				Wall w3 = new Wall (i+2, j+2, i-2, j-2); 
				Wall w4 = new Wall (i-2, j+2, i+2, j-2); 
				if (is.InterSection(w) || is.InterSection(w2)|| is.InterSection(w3)|| is.InterSection(w4)) 
				{

						{ int k= 1; int l= 1; 
							pf.updateCell(i/mf-1+k, j/mf-1+l, -1);
							pfMatrix[i/mf][j/mf] = -1; 
						}
 				}
			}

			Yaz("Start node: ("+startx+","+starty+")");
			Yaz("End node: ("+targetx+","+targety+")");

			//Time the replanning
			long begin = System.currentTimeMillis();
		
			pf.updateStart(startx/mf, starty/mf);
			pf.updateGoal(targetx/mf, targety/mf);
			Yaz("Nodes updated ");
			pf.replan();
		
			long end = System.currentTimeMillis();

			Yaz("Time: " + (end-begin) + "ms");

			path = pf.getPath();
			for (State i : path)
			{
				Yaz("x: " + i.x*mf + " y: " + i.y*mf);
			}
			
		}
		
		public void BuildDStarLite(int startx, int starty, int targetx, int targety) 
		{
		
			Yaz("***** DSTARLITE ********");
			DStarLite pf = new DStarLite();
			//pf.init(startx,starty, targetx,targety);
			for (int i=0; i<TERRAIN_WIDTH; i++) 
			for (int j=0; j<TERRAIN_HEIGHT; j++) 
			{
				
				//WallNode wn = new WallNode(i, j); 
				Wall w = new Wall (i-1, j-1, i+1, j+1); 
				if (is.InterSection(w)) 
				{
					//for (int k=0; k<3; k++) 
					//	for (int l=0; l<3; l++) 
							pf.updateCell(i, j, -1); 
 				}
				
			}
	       
			//pf.updateCell(125, 125, -1); 
			//pf.updateCell(126, 126, -1); 
			Yaz("Start node: ("+startx+","+starty+")");
			Yaz("End node: ("+targetx+","+targety+")");

			//Time the replanning
			long begin = System.currentTimeMillis();
			
			pf.updateStart(startx, starty);
			pf.updateGoal(targetx, targety);
			pf.replan();
			pf.updateStart(500, 320);
			pf.updateGoal(800, 320);
			pf.replan();
			long end = System.currentTimeMillis();

			Yaz("Time: " + (end-begin) + "ms");

			path = pf.getPath();
			for (State i : path)
			{
				Yaz("x: " + i.x + " y: " + i.y);
			}

			
		}
		
*/
		
		public void BuildRRT(int startx, int starty, int goalx, int goaly) 
		{
			 
			c=0;
			is.nodes = new ArrayList<Vertex>();
			is.edges = new ArrayList<Edge>();  
			is.tpoints = new ArrayList<TransitionPoint>();
			 x[0]= startx; y[0]=starty;
			 is.SetTP(new TransitionPoint(x[c], y[c], "TP"+Integer.toString(c)));
			  
			 
			
			 c= 1;  
			 
		//	    int rx,ry,n=0;
			//    double d;
			 Graphics2D g = (Graphics2D) this.getGraphics(); 
			 int color;
			while (c < maxC) 
			 {
				 Yaz("RRT while");
			      do {
			          rx=(int)(Math.random()*TERRAIN_WIDTH);
			          ry=(int)(Math.random()*TERRAIN_HEIGHT);
			        } while(hit(rx,ry));

			        g.setColor(Color.yellow);
			        
			        // den knoten mit der geringsten distanz zum punkt suchen
			       
			        RRTmin=-1.0;
			        //System.out.println("stepsize1 �NCES� RRTmin "+RRTmin + " n "+n + " i 0"+"" + " c "+c + " rx "+rx+" ry "+ry+" x[i]"+x[0]+" y[i] "+y[0]);
			        d=RRTmin; 
			        StepSize1(); 
			        
				 
			        // maximal mit der entfernung delta springen
			        if (delta<d) d=delta;

			        // neuen knoten berechnen
			        x[c]=x[n]+(int)(d*(rx-x[n])/RRTmin);
			        y[c]=y[n]+(int)(d*(ry-y[n])/RRTmin);

			        // das gr�n etwas dunkler machen um so weiter der baum fortgeschritten ist
			        color=255-120*c/maxC;

			        // nochmal pr�fen ob der neue knoten nicht in ein hindernis f�llt
			        if (!linehit(x[n], y[n], x[c],y[c])) {
			        	// Yaz("RRT linehit");
			        	g.setColor(new Color(color/2,color,color/2));
			        	g.setStroke(new BasicStroke(1));
			          g.drawLine(x[n]+HOR_OFFSET,y[n]+VER_OFFSET,x[c]+HOR_OFFSET,y[c]+VER_OFFSET);
			          
			          from [c] = n;
			          
			          is.SetTP(new TransitionPoint(x[c], y[c], "TP"+Integer.toString(c)));
			          int mesafe = (int) CalcDistance(x[n], y[n], x[c],y[c]); 
			        //  Yaz("RRT : n : " + Integer.toString(n) +" c : "+ Integer.toString(c) + " From ["+Integer.toString(x[n]) +";"+ Integer.toString(y[n])+"]  - To ["+Integer.toString(x[c])+";"+Integer.toString(y[c]) + "] mesafe : "+ Integer.toString(mesafe)); 
			          
			          	
			          is.addLane("x", n, c, mesafe);
			          is.addLane("x", c, n, mesafe);
			          c++;
			        }
			        cCount = c;
					 TransitionPoint TP_closest_robot = is.Closest(new TransitionPoint(new Task(startx, starty,"Start")));
					 TransitionPoint TP_closest_task = is.Closest(new TransitionPoint(new Task(goalx, goaly,"End")));	
					 if (TP_closest_robot!=null && TP_closest_task!= null) 
					 {
						
						 c = maxC; 
						 
					 }
					  
			 }
			 is.ListTPs(); 
			 try{
				  //do what you want to do before sleeping
				  Thread.currentThread().sleep(2000);//sleep for 1000 ms
				  //do what you want to do after sleeptig
				}
				catch(InterruptedException ie){
				//If this thread was intrrupted by nother thread 
				}
			
			 
			g.dispose();
			
			
			
			
			
		}
		
		public void BuildRRT_Malatya() 
		{
		
			//BuildRRT(100, 310); 
			
		}
		public void BuildRRT_Izmir() 
		{
			
			 //BuildRRT(40, 95); 
					
		}
		public void BuildRRT_AHL() 
		{
			
			 //BuildRRT(830, 130); 
					
		}
		public void BuildRRT_temp7() 
		{
			
			 //BuildRRT(420,420); 
					
		}
		public void drawTree (Graphics2D g) 
		{
	        
			
			int nn = 0;  
			 g.setStroke(new BasicStroke(4));
			for (int j=0; j<cCount ; j++) {
				
				 // int  color=255-120*j/cCount;
				
				  nn = from[j]; 
				  // g.setColor(new Color(color/2,color,color/2));
				  g.setColor(Color.yellow);
		          g.drawLine(x[nn]+HOR_OFFSET,y[nn]+VER_OFFSET,x[j]+HOR_OFFSET,y[j]+VER_OFFSET);
		  
		        }
			
			int nn2 = 0; 
			 g.setStroke(new BasicStroke(3));
			for (int j=0; j<cCount2 ; j++) {
				
				  int  color=255-120*j/cCount2;
				  nn2 = from2[j]; 
				  g.setColor(new Color(color/2,color/2,color));
		          g.drawLine(x2[nn2]+HOR_OFFSET,y2[nn2]+VER_OFFSET,x2[j]+HOR_OFFSET,y2[j]+VER_OFFSET);
		  
		        }
			
		}
		public double CalcDistance(int xs, int ys, int xd, int yd)
		{
			
			int x_fark = xs-xd ; 
			int y_fark =  ys-yd ;
			
			return Math.sqrt( x_fark * x_fark  + y_fark*y_fark ) ;	


		}
		public void drawRobots(Graphics2D g)
		{
			
			
			
			//Color [] colorPalette = {Color.BLACK};
			g.setFont(new Font("arial",Font.BOLD,12));
			Color r= g.getColor(); 
			g.setStroke(new BasicStroke(2));
			for (int i=0; i<robotCount; i++) 
				{
				

				    g.setColor(colorPalette[i%robotCount]);  
					
					
					g.drawString(robots[i].robotName , robots[i].xLoc+HOR_OFFSET+ 15, robots[i].yLoc+ VER_OFFSET);
					
					g.setColor(Color.blue);
					g.fillOval(robots[i].xLoc+HOR_OFFSET-4, robots[i].yLoc+ VER_OFFSET-4, 8, 8);
					
					g.setColor(Color.yellow);
					
					drawRoundSquare(robots[i].PathX(0) + HOR_OFFSET-2, robots[i].PathY(0)-2+ VER_OFFSET, 4, 4, g); 
					
					System.out.println(robots[i].numberOfSectors + "-" + robots[i].heading);
					int firstx=0, firsty = 0, prevx = 0, prevy = 0; 
					for (int j=0; j<robots[i].numberOfSectors; j++) 
					{
						int cx = robots[i].xLoc+HOR_OFFSET; 
						int cy = robots[i].yLoc+VER_OFFSET; 
						double angle = ((j * 360.0) / (robots[i].numberOfSectors * 1.0)) * (Math.PI / 180.0); 
						
						double tx = cx + robots[i].meanProximities[j] * 1.0 * Math.cos(angle) ;
						double ty = cy + robots[i].meanProximities[j] * 1.0 * Math.sin(angle); 
						//System.out.println(angle + " - " + robots[i].meanProximities[j] + " - "+ cx + " - " + cy + " - " + tx + " - " + ty);
						g.setColor(Color.pink);
						//g.fillArc((int) (cx-robots[i].meanProximities[j]), (int) (cy-robots[i].meanProximities[j]), (int) robots[i].meanProximities[j]*2, (int) robots[i].meanProximities[j]*2,(int) ((j * 360.0) / (robots[i].numberOfSectors * 1.0))+180 , (int) (360.0/robots[i].numberOfSectors) );
						g.setColor(Color.cyan);
						//g.drawArc((int) (cx-robots[i].meanProximities[j]), (int) (cy-robots[i].meanProximities[j]), (int) robots[i].meanProximities[j]*2, (int) robots[i].meanProximities[j]*2,(int) ((j * 360.0) / (robots[i].numberOfSectors * 1.0))+180 , (int) (360.0/robots[i].numberOfSectors) );
						if ( j==0) 
						{
							firstx = (int) tx; 
							firsty = (int) ty; 
							prevx =  (int) tx; 
							prevy = (int) ty; 
						}
						else if (j==robots[i].numberOfSectors-1) 
						{
							g.drawLine((int) tx, (int) ty, firstx, firsty); 
							g.drawLine((int) tx, (int) ty, prevx, prevy); 
						}
						else 
						{
							
							g.drawLine((int) tx, (int) ty, prevx, prevy); 
						    prevx = (int) tx; 
						    prevy = (int) ty; 
						    
						}
					}
					
			
					
				}	
			g.setColor(r); 		
			
		}
		public void drawRobotPaths(Graphics2D g)
		{
			//Stroke drawingStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, new float[]{10},0);
		/*	final  float dash1[] = {21.0f, 9.f};
			Stroke drawingStroke = new BasicStroke(2.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f);
			g.setStroke(drawingStroke); 
			*/
		    
		   
		  //  g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
		   //     BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
		    
			Color r= g.getColor();
			int prevX = 0, prevY = 0;  
			  int interval = 10; 
			  
			 int dashInterval [] = {7};  
			  
			for (int i=0; i<robotCount; i++) 
			{
				g.setColor(colorPalette[i%robotCount]);
				for (int j=1; j<robots[i].PathCounter();j++) 
				{
					//g.drawLine(robots[i].PathX(j-1)+ HOR_OFFSET, robots[i].PathY(j-1)+ VER_OFFSET, robots[i].PathX(j)+ HOR_OFFSET, robots[i].PathY(j)+ VER_OFFSET);
					if (CalcDistance(robots[i].PathX(j), robots[i].PathY(j), prevX, prevY)> interval) 
					{
						// g.fillRect( robots[i].PathX(j)+ HOR_OFFSET, robots[i].PathY(j)+ VER_OFFSET, 4, 4);
						switch (i%robotCount) 
						{
						case 0: 
							 g.fillRect( robots[i].PathX(j)+ HOR_OFFSET-2, robots[i].PathY(j)+ VER_OFFSET-2, 4,4);
						break; 
						case 1: 
							 g.fillRoundRect( robots[i].PathX(j)+ HOR_OFFSET-2, robots[i].PathY(j)+ VER_OFFSET-2, 4,4,2,2); 
							break; 
						case 2: 
							 g.fillOval( robots[i].PathX(j)+ HOR_OFFSET-2, robots[i].PathY(j)+ VER_OFFSET-2, 4,4);
						break; 
						case 3:
							g.drawRect( robots[i].PathX(j)+ HOR_OFFSET-2, robots[i].PathY(j)+ VER_OFFSET-2, 4,4);
						break; 
						default : 
							
							 g.drawOval( robots[i].PathX(j)+ HOR_OFFSET-2, robots[i].PathY(j)+ VER_OFFSET-2, 4,4);
						
						}
						prevX = robots[i].PathX(j); 
						prevY = robots[i].PathY(j);
						interval = dashInterval[j%dashInterval.length]; 
					} 
				}
			}
			
			//g.drawRect(20, 20, 200, 200); 
			g.setColor(r); 	
		}
		
		public void drawRoundSquare (int x, int y, int radius, int roundNess,Graphics2D g) 
		{
			g.drawRoundRect (x-radius, y-radius, radius*2, radius*2, roundNess, roundNess) ; 			
		}
		public void fillRoundSquare (int x, int y, int radius, int roundNess,Graphics2D g) 
		{
			g.fillRoundRect (x-radius, y-radius, radius*2, radius*2, roundNess, roundNess) ; 			
		}		
		public void drawCircle (int x, int y, int radius,Graphics2D g) 
		{
			g.drawOval(x-radius, y-radius, radius*2, radius*2) ; 
		}
		
		public void fillCircle (int x, int y, int radius, Graphics2D g) 
		{
			g.fillOval(x-radius, y-radius, radius*2, radius*2) ; 
		}
		
		public void drawTasks(Graphics2D  g)
		{
			
			Color r;
			Color s; 
		    //Color [] colorPalette = {Color.red, Color.blue, Color.green, Color.magenta,  Color.orange, Color.pink, Color.lightGray, Color.yellow, Color.darkGray};  
			//Color [] colorPalette = {Color.red, Color.blue, Color.magenta, Color.green, Color.orange, Color.pink, Color.lightGray, Color.yellow, Color.darkGray,Color.red, Color.blue, Color.magenta, Color.green, Color.orange, Color.pink, Color.lightGray, Color.yellow, Color.darkGray,Color.red, Color.blue, Color.magenta, Color.green, Color.orange, Color.pink, Color.lightGray, Color.yellow, Color.darkGray,Color.red, Color.blue, Color.magenta, Color.green, Color.orange, Color.pink, Color.lightGray, Color.yellow, Color.darkGray}; 

		    g.setStroke(new BasicStroke(2));
		
			
			for (int i=0; i<taskBundleCount; i++) 
			{
		        TaskBundle tb = taskBundles[i];
		        
		        if (tb!=null && tb.state != eTaskState.NOT_ASSIGNED) 
		        {
		        	mb.NewMessage(tb.GetTask(0).taskName + " - "+tb.state);
				for (int j=0; j<tb.taskCount(); j++) 
				{
				     Task t = tb.GetTask(j) ;  
				     
				     
				     if (t.owner == -1) 
				     {
				    	 s = Color.red; 
				     }
				     else 
				     {
				    	 s = colorPalette[t.owner%robotCount];
				     }
				     
				    // g.drawLine(t.xLoc-tk+HOR_OFFSET, t.yLoc-tk+VER_OFFSET, t.xLoc+tk+HOR_OFFSET, t.yLoc+tk+VER_OFFSET); 
				     //g.drawLine(t.xLoc+tk+HOR_OFFSET, t.yLoc-tk+VER_OFFSET, t.xLoc-tk+HOR_OFFSET, t.yLoc+tk+VER_OFFSET); 
				     
				     
				     	  if(t.state == eTaskState.NOT_STARTED)
				     	  {
				     		//g.drawOval(t.xLoc-7 + HOR_OFFSET, t.yLoc-7 + VER_OFFSET, 12, 12);
				     		drawCircle(t.xLoc+ HOR_OFFSET, t.yLoc+ VER_OFFSET, 7, g); 	
				     		 r= g.getColor(); 
				     		  g.setColor(s); 
				     		 drawCircle(t.xLoc+ HOR_OFFSET, t.yLoc+ VER_OFFSET, 10, g); 	
				     		drawCircle(t.xLoc+ HOR_OFFSET, t.yLoc+ VER_OFFSET, 7, g); 	
				     		fillCircle(t.xLoc+ HOR_OFFSET, t.yLoc+ VER_OFFSET, 4, g); 	
				     		  //fillCircle(t.xLoc+ HOR_OFFSET, t.yLoc+ VER_OFFSET, 8, g); 
				     		 g.setColor(r);
				     	  }
				        
				     	  else if(t.state == eTaskState.PROCESSING)
				     	  {
				     		  r= g.getColor(); 
				     		  g.setColor(s); 
				     		  //g.drawOval(t.xLoc-7+ HOR_OFFSET, t.yLoc-7+ VER_OFFSET, 12, 12);
				     		  fillCircle(t.xLoc+ HOR_OFFSET, t.yLoc+ VER_OFFSET, 5, g); 
				     		  g.setColor(Color.yellow);
					     		// g.setStroke(new BasicStroke(2));
					     		 //g.drawOval(t.xLoc-7 + HOR_OFFSET, t.yLoc-7 + VER_OFFSET, 12, 12);
					     		 drawCircle(t.xLoc + HOR_OFFSET,t.yLoc + VER_OFFSET, 7, g);
				     		  g.setColor(r); 
				     		  
				     	  } 
				     	
				     	  
				     	 else if(t.state == eTaskState.COMPLETED)
				     	  { 
				     		  r= g.getColor(); 
				     		  g.setColor(s); 
				     		  
				     		  //g.fillOval(t.xLoc-7+ HOR_OFFSET, t.yLoc-7+ VER_OFFSET, 12, 12);
				     		 fillCircle(t.xLoc+ HOR_OFFSET, t.yLoc+ VER_OFFSET, 3, g); 
				     		  g.setColor(Color.yellow);
				     		// g.setStroke(new BasicStroke(2));
				     		 //g.drawOval(t.xLoc-7 + HOR_OFFSET, t.yLoc-7 + VER_OFFSET, 12, 12);
				     		 drawCircle(t.xLoc + HOR_OFFSET,t.yLoc + VER_OFFSET, 5, g);
				     		  g.setColor(r); 
					   
							
				     	  }
				     	 else if(t.state == eTaskState.CONFIRMED)
				     	  {
				     		  r= g.getColor(); 
				     		  g.setColor(s); 
				     		  
				     		  //g.fillOval(t.xLoc-7+ HOR_OFFSET, t.yLoc-7+ VER_OFFSET, 12, 12);
				     		 fillCircle(t.xLoc+ HOR_OFFSET, t.yLoc+ VER_OFFSET, 3, g); 
				     		  g.setColor(Color.yellow);
				     		// g.setStroke(new BasicStroke(2));
				     		 //g.drawOval(t.xLoc-7 + HOR_OFFSET, t.yLoc-7 + VER_OFFSET, 12, 12);
				     		 drawCircle(t.xLoc + HOR_OFFSET,t.yLoc + VER_OFFSET, 5, g);
				     		  g.setColor(r); 
				     		  
				     	  }  
				     	 else 
				     	 {
						//   g.drawOval(t.xLoc-7 + HOR_OFFSET, t.yLoc-7 + VER_OFFSET, 12, 12); 
				        }
				     	  
				     r= g.getColor(); 
				     g.setColor(s); 	
				     g.setFont(new Font("arial",Font.PLAIN,12));
						g.drawString(t.taskName , t.xLoc+ HOR_OFFSET - 5 , t.yLoc-9+ VER_OFFSET);
						g.setColor(r); 	
				}  // j
		        }

			}  // i
			
		}
		
		
		
			
		public void CreateIndoorStructure () 
		{
			
			getCallerMethod();
			is = new IndoorStructure(templateSelection);

    	
		} 
		

		public void drawWall (Graphics2D g, Wall walle) 
		{
   		  Color r= g.getColor(); 
   		  g.setColor(Color.DARK_GRAY); 
   		     		  
   		  
   		 /* g.setStroke(new BasicStroke(3));
   		  g.drawLine(walle.startNode.xLoc + HOR_OFFSET, walle.startNode.yLoc+VER_OFFSET, walle.endNode.xLoc+HOR_OFFSET, walle.endNode.yLoc+VER_OFFSET);
   	 	  */ g.setColor(Color.black); 
   		 g.setStroke(new BasicStroke(2));
  		  g.drawLine(walle.startNode.xLoc + HOR_OFFSET, walle.startNode.yLoc+VER_OFFSET, walle.endNode.xLoc+HOR_OFFSET, walle.endNode.yLoc+VER_OFFSET); 
   		  //g.drawRect(walle.startNode.xLoc + HOR_OFFSET, walle.startNode.yLoc+VER_OFFSET-3, walle.endNode.xLoc-walle.startNode.xLoc, 6);
   		
   		  g.setColor(r); 			
			
		}
		
		public void drawEdges (Graphics2D g) 
		{
   		  Color r= g.getColor(); 
   		  g.setColor(Color.orange); 
   		     		  
   		 for (int i=0; i<is.GetLaneCount(); i++)
   		 {
   			 Edge e = is.GetLane(i); 
   			 Vertex s = e.getSource(); 
   			 Vertex d =  e.getDestination(); 
   			 TransitionPoint tps = s.tp; 
   			 TransitionPoint tpd = d.tp;
   			 Wall w = new Wall(new WallNode(tps.xLoc, tps.yLoc),new WallNode(tpd.xLoc, tpd.yLoc)); 
   			 if (!is.InterSection(w))
   			   g.drawLine( tps.xLoc + HOR_OFFSET, tps.yLoc + VER_OFFSET, tpd.xLoc+HOR_OFFSET, tpd.yLoc+VER_OFFSET);
      		  
   			 
   		 }
   		  
   		
   		  
   		  //g.drawRect(walle.startNode.xLoc + HOR_OFFSET, walle.startNode.yLoc+VER_OFFSET-3, walle.endNode.xLoc-walle.startNode.xLoc, 6);
   		
   		  g.setColor(r); 			
			
		}
		
		public void drawLanes (Graphics2D g) 
		{
			
			g.setColor(Color.yellow); 
			for (int i=0; i<is.edges.size(); i++) 
			{
			
				Edge e = is.GetEdge(i); 
				TransitionPoint ti = e.getSource().tp; 
				TransitionPoint tj = e.getDestination().tp; 
				g.drawLine(ti.xLoc, ti.yLoc, tj.xLoc, tj.yLoc) ; 
				
			}
			
		}
		
		public void ShowMessages (Graphics2D g) 
		{
			g.setColor(Color.WHITE); 
			g.fillRect(HOR_OFFSET, TERRAIN_HEIGHT + VER_OFFSET +10 , 500, 300);
			g.setColor(Color.BLACK); 
			for (int i=0; i<15; i++) 
		   {
			  if (mb.GetMessage(i)!=null)
			   g.drawString(mb.GetMessage(i), HOR_OFFSET, TERRAIN_HEIGHT + VER_OFFSET + 20 + i*20); 
		   }
			
		}
		
		public void drawTransitionPoint (Graphics2D g,  TransitionPoint tp) 
		{
   		  Color r= g.getColor(); 
   		  g.setColor(Color.red); 
   		     		  
   		  
   		  g.setStroke(new BasicStroke(1));
   		  g.fillRoundRect(tp.xLoc+HOR_OFFSET-4, tp.yLoc+VER_OFFSET-4, 8, 8, 4,4);
   		  //fillRoundSquare(tp.xLoc+HOR_OFFSET, tp.yLoc+VER_OFFSET, 2, 1, g) ; 
   		  //g.drawString(tp.name, tp.xLoc+5+HOR_OFFSET , tp.yLoc+5+VER_OFFSET);
   		  g.setColor(r); 			
			
		}
		

		
		public void drawWalls (Graphics2D g) 
		{
	
			
			for (int i=0; i<is.GetWallCount(); i++) 
			{
				drawWall (g, is.GetWall(i)); 
				
			}
			
			
			
			
			
		}
		
		public void drawTPs(Graphics2D g) 
		{
	
			drawEdges (g) ;
			for (int i=0; i<is.GetTPCount(); i++) 
			{
				drawTransitionPoint (g, is.GetTP(i)); 
				
			} 
			
			
			
			
		}
		public void wayMinMax () 
		{
			wMin = 100000; 
			wMax = 0; 
			for (int i=0; i<tradeCount; i++) 
			{
			    if (cumulativePaths[i]>0) 
			    {
			    	 if (cumulativePaths[i]<wMin)
			    		 wMin = cumulativePaths[i]; 
			    	 if (cumulativePaths[i]>wMax)
			    		 wMax = cumulativePaths[i]; 
			    }
			    
			}
		}
		
		public void drawCumulativePaths (Graphics2D g) 
		{
			wayMinMax(); 
			double multiplier = (double) (wMax-wMin) / 100.0; 
		//	double normfirst = (double) (cumulativePaths[0]-wMin) / multiplier; 
            
			 g.setStroke(new BasicStroke(3));
			 g.drawString(Integer.toString(cumulativePaths[0]), TERRAIN_WIDTH-100, TERRAIN_HEIGHT+VER_OFFSET +70);  
			 g.drawString(Integer.toString(wMax), 10, TERRAIN_HEIGHT+VER_OFFSET +75);
			 g.drawString(Integer.toString(wMin), 10, TERRAIN_HEIGHT+VER_OFFSET +175);  

			for (int i=1; i<tradeCount; i++) 
			{
			    g.drawString(Integer.toString(cumulativePaths[i]), TERRAIN_WIDTH-100, TERRAIN_HEIGHT+VER_OFFSET+i*20+70);  
				if (cumulativePaths[i]>0) 
			    {
			    	double normprev = (double) (cumulativePaths[i-1]-wMin) / multiplier; 
			    	double normcur = (double) (cumulativePaths[i]-wMin) / multiplier; 
			    	//Yaz("NORMCUR	"+Double.toString(normcur)); 
			    	
			    	 g.drawLine(HOR_OFFSET+ 60 + i*5, TERRAIN_HEIGHT + VER_OFFSET +55  , HOR_OFFSET+ 60 + i*5 ,  TERRAIN_HEIGHT + VER_OFFSET + 60);
			    	 g.drawLine(HOR_OFFSET+ 60 + i*5, TERRAIN_HEIGHT + VER_OFFSET +170 - (int) normprev , HOR_OFFSET+ 60 + (i+1)*5 ,  TERRAIN_HEIGHT + VER_OFFSET +170 - (int) normcur);
			        

			    }
			    Color cc = g.getColor(); 
			    g.setColor(Color.GRAY);
     	    g.setColor(cc);
			}
			
			
			
			
		}
		
		public void drawGrid (Graphics2D    g) 
		{
			 
			// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.6)) ; 
			 Color r= g.getColor(); 
		     g.setColor(Color.darkGray);
		     Stroke drawingStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5},0);
				g.setStroke(drawingStroke); 
			 for (int i=0; i<TERRAIN_HEIGHT/100+1; i++) 
			 {
				 g.drawLine(0+HOR_OFFSET, i*100+VER_OFFSET, TERRAIN_WIDTH + HOR_OFFSET , i*100+VER_OFFSET); 
				 
				 
			 }
			 
			 for (int i=0; i<TERRAIN_WIDTH/100+1; i++) 
			 {
				 g.drawLine(i*100 + HOR_OFFSET ,  VER_OFFSET,   i*100 + HOR_OFFSET, TERRAIN_HEIGHT + VER_OFFSET); 
				 String legend = Integer.toString(i*100); 
				 g.drawString(legend, i*100 + HOR_OFFSET  ,  VER_OFFSET + 50) ; 
				 
			 }

			//g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1)) ; 
			
	
			g.setColor(r); 
		}
		
		 public void drawPFM(Graphics2D g)
		 { 
			
			 Color r= g.getColor(); 
		     g.setColor(Color.BLACK);
		     //g.setStroke(new BasicStroke(3));
		 
		     
		     for (int i=0; i<2000; i++)
		    	 for (int j=0; j<1500; j++) 
		    	 {
		    		 if (pfMatrix[i][j]==-1)
		    		     g.drawString(".", i+HOR_OFFSET, j+VER_OFFSET); 
		    	 }  
		    	
			 g.setColor(r); 
		 }

		public void drawDSTARX(Graphics2D g)
		 {
			
			 Color r= g.getColor(); 
		     g.setColor(Color.red);
		     g.setStroke(new BasicStroke(5));
		     int oldx = -1 ;
		     int oldy = -1; 
			 for (State i : path)
				{
				if 	(oldx<0 && oldy<0)
				{
					oldx = i.x*mf; 
					oldy = i.y*mf; 
				}
				 //Yaz("x: " + i.x * mf + " y: " + i.y *mf);
					g.drawLine(oldx+HOR_OFFSET, oldy+VER_OFFSET, i.x * mf+HOR_OFFSET, i.y*mf+VER_OFFSET); 
					oldx = i.x*mf; 
					oldy = i.y*mf; 
				}
			 g.setColor(r); 
		 }
		 public void drawDSTAR(Graphics2D g)
		 {
			 Color r= g.getColor(); 
		     g.setColor(Color.yellow);
		     //g.setStroke(new BasicStroke(3));
		     int oldx = -1 ;
		     int oldy = -1; 
			 for (State i : path)
				{
				if 	(oldx<0 && oldy<0)
				{
					oldx = i.x; 
					oldy = i.y; 
				}
				 Yaz("x: " + i.x + " y: " + i.y);
					g.drawLine(oldx+HOR_OFFSET, oldy+VER_OFFSET, i.x, i.y); 
					oldx = i.x; 
					oldy = i.y; 
				}
			 g.setColor(r); 
		 }
		
		public void drawStations (Graphics2D    g) 
		{
					
			Color first= g.getColor(); 
		     //g.setColor(Color.orange);
			
			for (int i=0; i<is.stationCount; i++) 
			{
				
				Station st = is.GetStation(i);  
			    g.setColor(Color.orange);
				 g.drawImage(imageStation, st.xLoc + HOR_OFFSET, st.yLoc+ VER_OFFSET - 30, null);
			  //  g.fill3DRect(st.xLoc + HOR_OFFSET-20, st.yLoc+ VER_OFFSET-20, 40, 40, true); 
			    g.setColor(Color.RED);
			  drawCircle(st.xLoc + HOR_OFFSET, st.yLoc+ VER_OFFSET, 3, g);
			    //  g.setFont(new Font("arial",Font.BOLD,12));
			 //   g.drawString("S", st.xLoc + HOR_OFFSET -3 ,st.yLoc+ VER_OFFSET + 3); 
			    
			}
			/*for (int i=stationCount; i<is.GetStationCount(); i++) 
			{
				
				Station st = is.GetStation(i);  
			    g.setColor(Color.LIGHT_GRAY);
				// g.drawImage(imageStation, st.xLoc + HOR_OFFSET, st.yLoc+ VER_OFFSET, null);
			    g.fill3DRect(st.xLoc + HOR_OFFSET-15, st.yLoc+ VER_OFFSET-15, 30, 30, true); 
			    g.setColor(Color.RED);
			    g.setFont(new Font("arial",Font.BOLD,12));
			    g.drawString("D", st.xLoc + HOR_OFFSET -3 ,st.yLoc+ VER_OFFSET + 3); 
			    
			}*/
			g.setColor(first); 
		}
		public void drawBackGround (Graphics2D    g) 
		{
			
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.6)) ; 
			g.drawImage(imageMain, HOR_OFFSET, VER_OFFSET, null);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1)) ; 

			
		}
		public void drawHeatMap (Graphics2D    g) 
		{
		
			            
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1)) ; 
			 if (expMode == eServiceArea.EXTENDEDRECTANGULAR ) 
				{
				
					for (int k=0;k<hm.colorCount;k++) 
					{
						 //imageHeat.setRGB( hm.heatColors[k].xx, hm.heatColors[k].yy, hm.heatColors[k].cl.getRGB());
						 g.setColor(new Color (hm.heatColors[k].cl.getRGB()));
					     fillCircle(HOR_OFFSET+hm.heatColors[k].xx, VER_OFFSET + hm.heatColors[k].yy, 1, g)   ;
					}
					/*for (int ii=0; ii<TERRAIN_WIDTH; ii+=50) 
					{
						for (int jj=0;jj<TERRAIN_HEIGHT; jj+=50) 
					
						{
							if(hm.clusters[ii][jj]>=0) 
								g.drawString("" +hm.clusters[ii][jj], ii+HOR_OFFSET, jj+VER_OFFSET); 
						
						}
					
					}*/
					 /* hm.ClusterRange(); 
				        
				        CropImageFilter KesilecekAlan=new CropImageFilter( hm.xst, hm.yst, hm.xen-hm.xst, hm.yen-hm.yst);
				        
				        FilteredImageSource KesilmisHali=new FilteredImageSource(imageHeat.getSource(), KesilecekAlan);

				       Image KesilmisResim=createImage(KesilmisHali);
					
					g.drawImage(KesilmisResim, HOR_OFFSET + hm.xst, VER_OFFSET + hm.yst, null); */
			 g.setColor(Color.white); 
			 for (int i=0; i<hm.clusterCount; i++ ) 
			 {
				 for (int j=0; j<hm.taskClusters[i].peakCount;j++) 
				 {
					 f.fillCircle(hm.taskClusters[i].peakPoints[j].xx + HOR_OFFSET, hm.taskClusters[i].peakPoints[j].yy+VER_OFFSET, 7, g); 
					 
					// Yaz ("peak " + hm.taskClusters[i].peakPoints[j].xx + " - " + hm.taskClusters[i].peakPoints[j].yy);
				 }
			 }
				}
			
		}
		
		public void drawRobotBoundaries (Graphics2D    g) 
		{
			Color r = g.getColor(); 
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.2)) ; 
			for (int i=0; i<robotCount; i++ )
			{
				g.setColor(colorPalette[i%robotCount]);
				g.setStroke(new BasicStroke(2)); 
				if (expMode == eServiceArea.CIRCULAR)
				{
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.2)) ; 
					g.fillOval(robots[i].rect.x1+HOR_OFFSET, robots[i].rect.y1+VER_OFFSET, (robots[i].rect.x2-robots[i].rect.x1), (robots[i].rect.y2-robots[i].rect.y1));	
					Stroke drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9},0);
					g.setStroke(drawingStroke); 
					g.setColor(Color.BLACK);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1)) ; 
					g.drawOval(robots[i].rect.x1+HOR_OFFSET, robots[i].rect.y1+VER_OFFSET, (robots[i].rect.x2-robots[i].rect.x1), (robots[i].rect.y2-robots[i].rect.y1));	
				}
				/*	else if (expMode == 4 )  {
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.2)) ; 
					g.fillRect(robots[i].rect.x1+HOR_OFFSET, robots[i].rect.y1+VER_OFFSET, (robots[i].rect.x2-robots[i].rect.x1), (robots[i].rect.y2-robots[i].rect.y1));
					Stroke drawingStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9},0);
					g.setStroke(drawingStroke); 
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.1)) ; 
					g.drawRect(robots[i].outerRect.x1+HOR_OFFSET, robots[i].outerRect.y1+VER_OFFSET, (robots[i].outerRect.x2-robots[i].outerRect.x1), (robots[i].outerRect.y2-robots[i].outerRect.y1));
					g.setStroke(new BasicStroke(2)); 
				}*/ 		
				if (expMode ==  eServiceArea.EXTENDEDRECTANGULAR)
				{
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.2)) ; 
					g.fillRect(robots[i].rect.x1+HOR_OFFSET, robots[i].rect.y1+VER_OFFSET, (robots[i].rect.x2-robots[i].rect.x1), (robots[i].rect.y2-robots[i].rect.y1));	
					Stroke drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9},0);
					g.setStroke(drawingStroke); 
					g.setColor(Color.BLACK);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1)) ; 
					g.drawRect(robots[i].rect.x1+HOR_OFFSET, robots[i].rect.y1+VER_OFFSET, (robots[i].rect.x2-robots[i].rect.x1), (robots[i].rect.y2-robots[i].rect.y1));	
					//g.drawRect(robots[i].outerRect.x1+HOR_OFFSET, robots[i].outerRect.y1+VER_OFFSET, (robots[i].outerRect.x2-robots[i].outerRect.x1), (robots[i].outerRect.y2-robots[i].outerRect.y1));	
				}		
				else if (expMode ==  eServiceArea.RECTANGULAR)
					g.drawRect(robots[i].rect.x1+HOR_OFFSET, robots[i].rect.y1+VER_OFFSET, (robots[i].rect.x2-robots[i].rect.x1), (robots[i].rect.y2-robots[i].rect.y1));
			
				else if (expMode ==  eServiceArea.ALL_TERRAIN)
					g.drawRect(HOR_OFFSET,VER_OFFSET,TERRAIN_WIDTH, TERRAIN_HEIGHT);				
				
		
			}
			g.setColor(r);
			g.setStroke(new BasicStroke(1)); 
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1)) ; 
		}	
		
		public void drawRRTShortestPath (Graphics2D g) 
		{
			 TransitionPoint TP_closest_robot = is.Closest(new TransitionPoint(new Task(370, 270,"Start")));
			 TransitionPoint TP_closest_task = is.Closest(new TransitionPoint(new Task(230, 180,"End")));	
			 if (TP_closest_robot != null && TP_closest_task!= null )
			 {
			 
			 Yaz("RRT : Start : " + TP_closest_robot.tpname+ " End : "+ TP_closest_task.tpname); 
			 LinkedList<Vertex> robot_to_task = is.ShortestPath(TP_closest_robot, TP_closest_task); 
			 
			 g.setColor(Color.red); 
			// g.setStroke(new BasicStroke(1));
			 
			 int lsx = 370; int lsy = 270; 
			 int c1 = 0 ; 
			 if (robot_to_task != null && robot_to_task.size()>0) 
				   for (Vertex vertex:robot_to_task) 
				   {

					   g.drawLine(lsx+HOR_OFFSET,lsy+VER_OFFSET,vertex.tp.xLoc+HOR_OFFSET,vertex.tp.yLoc+VER_OFFSET); 
					   Yaz("RRT :  VERTEX " + Integer.toString(c1++) + " X : " + Integer.toString(vertex.tp.xLoc) + " Y : " + Integer.toString(vertex.tp.yLoc) );
					   lsx = vertex.tp.xLoc; 
					   lsy = vertex.tp.yLoc; 

				   }
			 
			 }
		}
		
		
		public void drawShortInfo (Graphics2D g) 
		{
			 g.setFont(new Font("arial",Font.BOLD,14));
	    	 g.setColor(Color.LIGHT_GRAY); 
	    	 g.fillRect( hor_start + 20, ver_start , 250, robotCount * 20+100);
	    	 g.setColor(Color.blue);
	    	 int totalWay = 0;
			 
			double spCof = speed*1000/3600; 
			int j;
			
			int maxTotal = 0; 
			for (j=0; j<robotCount; j++) 
			{
				double physicalWay = wc.PhysicalWay(robots[j].cumulativeWay, is.physicalWidth, is.tW);  
				totalWay += physicalWay;
				 
				double estTravTime = physicalWay / spCof; 
				int pickUpTime = robots[j].pickUpTime;
				robots[j].execTime = (int) estTravTime; 
				int totalTime = (int) (estTravTime) + pickUpTime + (int) robots[j].schTime; 
						
				g.drawString(robots[j].robotName + " - "+ Integer.toString( (int) physicalWay)+ " - " + Integer.toString((int) estTravTime)+ " - " +  Integer.toString((int) pickUpTime) +
						     " - " + 
						     Integer.toString((int)  robots[j].schTime) 
						     +
						     " - " + 
						     Integer.toString( totalTime) 
						     , hor_start+ 30 , ver_start + 60 + (j+1)*20  );
				if (totalTime>maxTotal) 
					maxTotal = totalTime; 
				
			}
			
			int initTime = Integer.parseInt(elapsedTime1)/1000; 
			int tradeTime = Integer.parseInt(elapsedTime2)/1000;
            int preTime  = hmct + initTime + tradeTime + maxTotal;    
			
			String approaches[] = {"Euclidian", "Fuzzy", "DStarLite", "DStarLiteFuzzy","RRT", "TPM - TSP"};
			g.drawString("Total Way taken =  "+ Integer.toString(totalWay),  hor_start+ 30, ver_start + 40 );
			g.drawString("t = "+ Integer.toString(hmct) + " + "+ Integer.toString(initTime)+ " + "+ Integer.toString(tradeTime) + " + " +Integer.toString(maxTotal)+" = "+ Integer.toString(preTime),  hor_start+ 30, ver_start +  60 );
			
			g.drawString(TRADE_APPROACH +" (" + ucCapacity +"-"+tradeCapacity+")" + "[TS:"+g_t+" SS:"+g_s+"]" ,  hor_start+ 30 , ver_start+20);
		}
		
		public void drawDetails (Graphics2D g) 
		{
			 int j;  
			 int totalWay = 0; 
			   String exStr [] = { "SIM. NOT STARTED",
	  		             "SIM. STARTED ", 
	  		             "AUCTION STARTED - END OF TOUR 09 ", 
	  		 			 "AUCTION OK, TRADING STARTED - 10",
	  		 			 "TRADING OK, SCHEDULING STARTED", 
	  		 			 "SCHEDULING OK, EXECUTION STARTED", 
	  		 			 "EXECUTING DONE"
	                     }; 
			 
			 g.setColor(Color.LIGHT_GRAY); 
	    	 g.fillRect(hor_start + 280 , ver_start  , 200, (latestTrade+1)*20 + 100 );
	    	 g.setColor(Color.blue);  
		    		for (j=0; j<robotCount; j++) 
				{
					double physicalWay = wc.PhysicalWay(robots[j].cumulativeWay, is.physicalWidth, is.tW);  
					totalWay += physicalWay;
					
				}
	    	 
	    	 //    logFrame.Reset() ;
			//	tradeLogFrame.Reset(); 
			//	taskListFrame.Reset(); 
				/*
				g.setFont(new Font("arial",Font.PLAIN,20));
				Color rr= g.getColor();
				g.setColor(Color.darkGray);
				g.drawString("Total Path Taken        : ", 10   , TERRAIN_HEIGHT + 20 + VER_OFFSET);
				//logFrame.writeSomething(10, 50, "Total Path Taken        : "); 
				
				g.drawString("Initial TA Duration     : ", 10   , TERRAIN_HEIGHT + 50 + VER_OFFSET);
				//logFrame.writeSomething(10, 80, "Initial TA Duration     : ");
				g.drawString("Trade Duration     : ", 10,  TERRAIN_HEIGHT + 80 + VER_OFFSET);
				//logFrame.writeSomething(10, 110, "Trade Duration          : ");
				g.drawString("TOTAL Duration          : ", 10,  TERRAIN_HEIGHT + 110 + VER_OFFSET);
				//logFrame.writeSomething(10, 140, "Scheduling Duration          : ");
				//logFrame.writeSomething(10, 170, "TOTAL Duration          : ");
				//g.drawString("EXECUTION_PHASE         : ",  510 , TERRAIN_HEIGHT + 20  + VER_OFFSET ) ;
				
				
				g.drawString("AUCTIONS MADE           : ",  10 , TERRAIN_HEIGHT + 140 + VER_OFFSET ) ;
				//logFrame.writeSomething(10, 200, "AUCTIONS MADE           : ");
				g.drawString("TERRAIN          : ", 510,  TERRAIN_HEIGHT + 50 + VER_OFFSET);
				//logFrame.writeSomething(10, 230, "TERRAIN          : ");
				g.drawString("INITIAL TRADE APPR.     : ", 510,  TERRAIN_HEIGHT + 80 + VER_OFFSET);
				//logFrame.writeSomething(10, 260, "INITIAL TRADE APPR.     : ");
				g.drawString("TRADE APPR.     : ", 510,  TERRAIN_HEIGHT + 110 + VER_OFFSET);
				//logFrame.writeSomething(10, 290, "TRADE APPR.     : ");
				g.drawString("EXECUTION_MODE     : ", 510,  TERRAIN_HEIGHT + 140 + VER_OFFSET);
				//g.drawString("EXECUTION_PHASE    : ", 510,  TERRAIN_HEIGHT + 170 + VER_OFFSET);
				//logFrame.writeSomething(10, 320, "EXECUTION_MODE     : ");
				
				//logFrame.writeSomething(10, 350, "EXECUTION_PHASE    : ");
				
				
				////////////////////////////////////
				
				g.setColor(Color.red);
	
				//logFrame.writeSomething(300, 50, Integer.toString((int)((double)(totalWay *1.0) *(double) 3850.0/(double) (TERRAIN_WIDTH*1.0)))); 
				g.drawString(elapsedTime1, 300   , TERRAIN_HEIGHT + 50 + VER_OFFSET);
				//logFrame.writeSomething(300, 80, elapsedTime1);
				g.drawString(elapsedTime2, 300, TERRAIN_HEIGHT + 80 + VER_OFFSET);
				//logFrame.writeSomething(300, 110, elapsedTime2);
				//logFrame.writeSomething(300, 140, elapsedTime3);
				
				g.drawString(Integer.toString(Integer.parseInt(elapsedTime1)+Integer.parseInt(elapsedTime2)), 300, TERRAIN_HEIGHT + 110 + VER_OFFSET);
				//logFrame.writeSomething(300, 170, Integer.toString(Integer.parseInt(elapsedTime1)+Integer.parseInt(elapsedTime2)+Integer.parseInt(elapsedTime3)));
				 g.drawString(""+auctionsMade, 300, TERRAIN_HEIGHT + 140 + VER_OFFSET);
				//logFrame.writeSomething(300, 200, auctionsMade);
				

				
				g.drawString(templateSelection, 800, TERRAIN_HEIGHT + 50 + VER_OFFSET);
				//logFrame.writeSomething(300, 230, templateSelection);
				g.drawString(""+ITRADE_APPROACH, 800, TERRAIN_HEIGHT + 80 + VER_OFFSET);
				//logFrame.writeSomething(300, 260, ""+ITRADE_APPROACH);
				g.drawString(""+TRADE_APPROACH, 800, TERRAIN_HEIGHT +110 + VER_OFFSET);
				//logFrame.writeSomething(300, 290, ""+TRADE_APPROACH);
				g.drawString(""+EXECUTION_MODE, 800, TERRAIN_HEIGHT +140 + VER_OFFSET);
				//logFrame.writeSomething(300, 320, ""+EXECUTION_MODE);
				
				g.drawString(executionPhase +"-"+exStr[executionPhase], 510, TERRAIN_HEIGHT + 20 + VER_OFFSET);
				//logFrame.writeSomething(300, 350, executionPhase +"-"+exStr);
				
		     	g.setColor(rr); 
				*/
		    		g.drawString(executionPhase +"-"+executionPhase, 20, TERRAIN_HEIGHT + 20 + VER_OFFSET);
		    		
				g.setFont(new Font("arial",Font.PLAIN,10));
				totalCost = totalWay; 
				++j; 
				j = 1;  
				//g.drawString("TRADES", TERRAIN_WIDTH -300 + HOR_OFFSET , 10 + TERRAIN_HEIGHT +VER_OFFSET   );
			//	 tradeLogFrame.writeSomething(10, 40, "TRADES"); 
				int successfulTrades = 0; 
				g.drawString("Trade Count : " +Integer.toString(latestTrade) + " Successful : "+ successfulTrades ,hor_start+ 290 + (j/40) * 100, (j%40) *10 +ver_start);
				for (int i=0; i<latestTrade+1; i++) 
				{
					Trade trd = tradeList[i];
					if (trd != null) 
					{
					   ++j; 
						g.drawString(trd.TBundle+ "  :  "+ trd.Seller + "   >   "+  trd.Buyer +"      ", hor_start+ 290 + (j/40) * 100, (j%40) *10  +ver_start);
					   //tradeLogFrame.writeSomething(10, 115 +i*15, trd.TBundle+ "  :  "+ trd.Seller + "   >   "+  trd.Buyer +"      "); 
					   if (trd.status == eTradingProgress.SUCCESS) 
					   {
						   successfulTrades++; 
					   }
					}				 
				}
				
	
		
	
		
				// tradeLogFrame.writeSomething(10, 65,"Trade Count : " +Integer.toString(latestTrade) );
			    // tradeLogFrame.writeSomething(10, 90, "Successful : "+ successfulTrades ); 
				 
			/*	j=1;
				// g.drawString("TASK OWNERS", TERRAIN_WIDTH - 700 +HOR_OFFSET, 10 +  TERRAIN_HEIGHT + VER_OFFSET );
			//	taskListFrame.writeSomething(10, 40, "TASK OWNERS"); 
				for (int i=0; i<taskBundleCount; i++)
				{
					Task t = taskBundles[i].GetTask(0); 
					String ownerName = t.ownerName; 
					++j; 
					//g.drawString(taskBundles[i].GetTask(0).taskName+" - "+ownerName, TERRAIN_WIDTH - 700 + HOR_OFFSET + (j/15) * 60 ,(j%15)*10 +  TERRAIN_HEIGHT + VER_OFFSET +10    );
		//			taskListFrame.writeSomething(10, 60 + i*20,taskBundles[i].GetTask(0).taskName+" - "+ownerName ); 
				}
				
				*/
			
				//logFrame.Show(); 
				//tradeLogFrame.Show();
			//	taskListFrame.Show(); 
	  
	    	
			
		}
		
		public void drawStuff() 
		{
	
			BufferStrategy bf = this.getBufferStrategy();
			Graphics g = null; 
			 Graphics2D g2 = null; 
			
			try 
			{
			     g = bf.getDrawGraphics(); 	
			     g.setFont(new Font("arial",Font.PLAIN,10));
			     Rectangle bounds= f.getBounds();
			     Color r= g.getColor(); 
			     g.setColor(Color.white);
			     g.fillRect(0, 0, bounds.width,bounds.height);
			     g.setColor(r); 
			    
			      g2 = (Graphics2D) g; 
			     
			     // HESAPLAMALAR 

				
				// HESAPLAR
			     
				     if (cbMap.isSelected()) 
				     {
				    	 drawBackGround(g2);
				     } 
			     if (cbHeatMap.isSelected())
			     {
			    	 drawHeatMap(g2);
			     } 

 			     if (cbGeneral.isSelected())
 			     {
 			         drawRobotBoundaries((Graphics2D)    g); 
 			     }
			   
			     if (cbTPs.isSelected())
			     {
			    	 drawTPs(g2); 
			     }
			     if (cbBorders.isSelected())
			     {
			    	 drawWalls(g2); 
			     }

			     if (cbStations.isSelected())
			     {
			    	//if (expMode!=eServiceArea.RECTANGULAR)
			    	 drawStations ((Graphics2D)    g); 
			     }
			     if (cbTasks.isSelected())
			     {
			    	 drawTasks(g2);
			     }		 
			     if (cbGrid.isSelected())
			     {
			    	 drawGrid(g2);
			     }		
			     if (cbGrid.isSelected())
			     {
			    	 drawGrid(g2);
			     }		 

			     if (cbRobots.isSelected()) 
			     {
			    		drawRobots((Graphics2D)  g);
			     }
			     if (cbRobotPath.isSelected()) 
			     {
			    		drawRobotPaths((Graphics2D)  g);
			     }
			     if (cbShortInformation.isSelected()) 
			     {
			    	
			    	 drawShortInfo((Graphics2D)  g);
			    	
			    	 
			     }

			     if (cbDetails.isSelected())
			     {
			    		
				     
			    	 drawDetails((Graphics2D)  g);
			    	 
			     }
						
			}
			finally 
			{
				//g2.dispose();
				
				//g.dispose(); 
				
			//	Yaz(terrain+"> Grafikler �izdirilemiyor."); 
			}
			
			bf.show(); 
			Toolkit.getDefaultToolkit().sync(); 
			
			
			/////////////////////////////////////
			///           DRAWSTUFF ��PL��� 
			
			// drawChargers(g);
			 //drawTree((Graphics2D) g); 
			 
			// drawRRTShortestPath((Graphics2D) g);
			// drawDSTARX((Graphics2D) g); 
			// drawPFM((Graphics2D) g); 
		
			
 	/*	for (int k=0;k<colorCount;k++) 
		{
		    g.fillOval(heatColors[k].xx+HOR_OFFSET, heatColors[k].yy+VER_OFFSET, 20, 20)		;
		    g.drawString(""+heatColors[k].maxRoboTime, heatColors[k].xx, heatColors[k].yy); 

		} 
		*/
		// g.drawString(""+heatColors[0].maxRoboTime, 200,200);
		}
		
		public void paint (Graphics g) 
		{

			
			
		
			
		}
		
	}
	// END OF FRAME CLASS
	
	
	
	
	
	public void AddUpdateTask( String temp[])
	{
		 // Normal task ise 
		// Yaz("arena> Task update  : " + temp[1]);
		if (Integer.parseInt(temp[5]) == 0) 
		  {
			  int tbIndex = TaskBundleIndex(temp[1]);
			  if (tbIndex>-1) 
			  {
				  int tIndex = taskBundles[tbIndex].TaskIndex(temp[1]); 
				  
				  if (tIndex>-1)
				  {
					  
					  Task tmp = taskBundles[tbIndex].GetTask(tIndex); 
					  tmp.xLoc = Integer.parseInt(temp[2]); 
					  tmp.yLoc = Integer.parseInt(temp[3]);
					  mb.NewMessage("72 "+tmp.taskName+"-"+temp[4]); 
					  tmp.state = eTaskState.values() [Integer.parseInt(temp[4])];
					  taskBundles[tbIndex].state = tmp.state; 
					  tmp.taskType = Integer.parseInt(temp[5]);
					  //tmp.index = Integer.parseInt(temp[8]);
					  taskBundles[tbIndex].SetTask(tIndex, tmp);
					  tmp.owner = RobotIndex(temp[6]); 
					  if (tmp.owner>-1)
						  tmp.ownerName = robots[RobotIndex(temp[6])].robotName; 
					  else 
						  tmp.ownerName = "AUC"; 
				  }   
				  
				  
			  }
			  else 
		      {
				  TaskBundle tb = new TaskBundle(); 
				  Task newTask = new Task();
				  newTask.taskName = temp[1];  
				  newTask.xLoc = Integer.parseInt(temp[2]); 
				  newTask.yLoc = Integer.parseInt(temp[3]);
				  mb.NewMessage("72 "+newTask.taskName+"-"+temp[4]); 
				  newTask.state = eTaskState.values() [Integer.parseInt(temp[4])];
				  newTask.taskType = Integer.parseInt(temp[5]);
				  newTask.owner = RobotIndex(temp[6]); 
				  if (newTask.owner>-1)
					  newTask.ownerName = robots[RobotIndex(temp[6])].robotName; 
				  else 
					  newTask.ownerName = "AUC"; 
				  tb.AddTask(newTask); 
				  
				  //tIndex = taskCount; 
				  taskBundles[taskBundleCount] = tb;
				  taskBundles[taskBundleCount++].state = newTask.state; 
			  
		  }
		  
		  } 
		  
		 

	}
	
	public void AddUpdateRobot(String []temp)
	{
		  int rIndex = RobotIndex(temp[1]); 
		  //Yaz("arena > 71 > schtime "+ temp[10]);
           
		  
		  if (rIndex>=0)
		  {
			  
			  robots[rIndex].xLoc = Integer.parseInt(temp[2]); 
			  robots[rIndex].yLoc = Integer.parseInt(temp[3]);
			  robots[rIndex].addToPath(); 
			  robots[rIndex].cumulativeWay = Integer.parseInt(temp[4]);
			  robots[rIndex].energyLevel = Integer.parseInt(temp[5]);
			  robots[rIndex].completedTasks = Integer.parseInt(temp[6]);
			  robots[rIndex].chargeCount = Integer.parseInt(temp[7]);
			  robots[rIndex].colorIndex = Integer.parseInt(temp[8]);
			  robots[rIndex].schTime = Integer.parseInt(temp[10]);
			  robots[rIndex].pickUpTime = Integer.parseInt(temp[11]);
			  robots[rIndex].execTime = Integer.parseInt(temp[12]);
			  robots[rIndex].waitingTasks = Integer.parseInt(temp[13]);
			  robots[rIndex].rect.x1 = Integer.parseInt(temp[14]);
			  robots[rIndex].rect.y1 = Integer.parseInt(temp[15]);
			   robots[rIndex].rect.x2 = Integer.parseInt(temp[16]);
			    robots[rIndex].rect.y2 = Integer.parseInt(temp[17]);
			   robots[rIndex].outerRect.x1 = Integer.parseInt(temp[18]);
			  robots[rIndex].outerRect.y1 = Integer.parseInt(temp[19]);
			   robots[rIndex].outerRect.x2 = Integer.parseInt(temp[20]);
			    robots[rIndex].outerRect.y2 = Integer.parseInt(temp[21]);
			    /*
			    robots[rIndex].heading = Double.parseDouble(temp[22]);  
				  robots[rIndex].numberOfSectors = Integer.parseInt(temp[23]); 
				  robots[rIndex].meanProximities = new double[robots[rIndex].numberOfSectors]; 
				  for (int i=0; i<robots[rIndex].numberOfSectors; i++) 
				  {
					  robots[rIndex].meanProximities[i] = Double.parseDouble(temp[24+i]); 
			//		   System.out.println ("terrain temp 24 + i " +  temp[24+i] + " - " + robots[rIndex].meanProximities[i] );
				  }
			  */
		  }
		  else 
		  {
			  Robotum newRobot = new Robotum(temp[1]); 
			  
			  newRobot.xLoc = Integer.parseInt(temp[2]); 
			  newRobot.yLoc = Integer.parseInt(temp[3]);
			  newRobot.addToPath();
			  newRobot.cumulativeWay = Integer.parseInt(temp[4]);
			  newRobot.energyLevel = Integer.parseInt(temp[5]);
			  newRobot.completedTasks = Integer.parseInt(temp[6]);
			  newRobot.chargeCount = Integer.parseInt(temp[7]);
			  newRobot.colorIndex = Integer.parseInt(temp[8]);
			  newRobot.schTime = Integer.parseInt(temp[10]);
			  newRobot.pickUpTime = Integer.parseInt(temp[11]);
			  newRobot.execTime = Integer.parseInt(temp[12]);
			  newRobot.waitingTasks = Integer.parseInt(temp[13]);
			  newRobot.rect.x1 = Integer.parseInt(temp[14]);
			  newRobot.rect.y1 = Integer.parseInt(temp[15]);
			  newRobot.rect.x2 = Integer.parseInt(temp[16]);
			  newRobot.rect.y2 = Integer.parseInt(temp[17]);
			  newRobot.outerRect.x1 = Integer.parseInt(temp[18]);
			  newRobot.outerRect.y1 = Integer.parseInt(temp[19]);
			  newRobot.outerRect.x2 = Integer.parseInt(temp[20]);
			  newRobot.outerRect.y2 = Integer.parseInt(temp[21]);
			 /* newRobot.heading = Double.parseDouble(temp[22]); 
			  newRobot.numberOfSectors = Integer.parseInt(temp[23]); 
			  newRobot.meanProximities = new double[newRobot.numberOfSectors]; 
			  for (int i=0; i<newRobot.numberOfSectors; i++) 
			  {
				
				  newRobot.meanProximities[i] = Double.parseDouble(temp[24+i]); 
			//	  System.out.println ("terrain temp 24 + i " +  temp[24+i] + " - " + newRobot.meanProximities[i] );
			  }*/
			  
			  
			  rIndex = robotCount;
			  robots[robotCount++] = newRobot; 
			  //f.SendIndoorStructure(temp[1]); 
			  
			//  Yaz(terrain+">"+ newRobot.robotName);
			  
		  }
		  //Yaz(terrain+"> Robot update  : " + robots[rIndex].robotName);
		
		  
		  // Otomatik modda t�m robotlar yerle�ti�inde otomatik ba�latma
		  if (robotCount == simRobotCount && RUNNING == 0 && autoMode == 1)
		  {
			 addBehaviour(new Starter (thisa, 5000)); 
		  }		

		 
	}
	
	public class Starter extends WakerBehaviour {

		private static final long serialVersionUID = 1L;

		public Starter(Agent a, long interval) {
			super(a, interval);
		}

		protected void handleElapsedTimeout() {

			 StartSimListener sl = new StartSimListener(); 
			  sl.actionPerformed(null); 

		}
	}
	      
	  	public int  RobotIndex (String robotName)
	  	{
	  		
	  		
	  		for (int i=0; i<robotCount; i++) 
	  		{
	  			String rbtName = robots[i].robotName; 
	  			if (rbtName.compareTo(robotName) == 0) 
	  			{
	  				return i; 
	  			}
	  		}
	  		return -1; 
	  	}
	  	
	  	public int  TaskBundleIndex (String taskName)
	  	{
		
	  		int retVal = -1;  
	  		for (int i=0; i<taskBundleCount; i++) 
	  		{
	  		    TaskBundle tb = taskBundles[i]; 
	  		    if (tb != null )
	  		    {
	  			for (int j=0; j<tb.taskCount(); j++) 
	  			{
	  			    Task t = tb.GetTask(j);
	  				String tskName = t.taskName; 
	  			    if (tskName.compareTo(taskName) == 0) 
	  			    {
	  				    retVal =  i; 
	  			    }
	  			}
	  		    }
	  		}
	  		return retVal; 
	  	}
	  	
	  	public int  TaskIndex (String taskName)
	  	{
		    int bundleIndex = TaskBundleIndex (taskName); 
		    if (bundleIndex > -1) 
		    {
		    	TaskBundle tb = taskBundles[bundleIndex] ; 
		    	return tb.TaskIndex(taskName); 
		    }
            return -1; 
	  		
	  		
	  	}
	  	
	  	
	  	
 
	  	class CheckBoxListener implements ActionListener {
			
			public void actionPerformed(ActionEvent e) 
			{
				f.drawStuff(); 
							    
			}
			
		}
	class ButtonActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) 
		{
			  
			ACLMessage msg = new ACLMessage (ACLMessage.INFORM); 
			AID aid = new AID(auctioneer, AID.ISLOCALNAME); 
			AID aid2 = new AID("M2", AID.ISLOCALNAME);
					 
			msg.addReceiver(aid);
			msg.addReceiver(aid2);
			for (int j=0; j<robotCount; j++) 
			{
				
				msg.addReceiver(new AID(robots[j].robotName, AID.ISLOCALNAME));
			}
			
			msg.setLanguage("English");
			msg.setContent("91");
			send(msg);
			Yaz(terrain+">Mesaj kay�t talebi gonderildi.  ");
		    
		}
		
		
	}

	class ScreenCapture implements ActionListener {
		
		public void actionPerformed(ActionEvent e) 
		{
			
			TakeScreenShot();

		}
		
		
	}
	public double MaxWay () 
	{
		double mWay = 0;
	    int j; 
		for (j=0; j<robotCount; j++) 
		{
			if (mWay < robots[j].cumulativeWay) 
				mWay = robots[j].cumulativeWay; 
			
			
		}
		return mWay; 
	}
	
		
	public int MaxTime () 
	{
		int mTime = 0;
	    int j; 
		for (j=0; j<robotCount; j++) 
		{
			int totalT= robots[j].schTime + robots[j].execTime + robots[j].pickUpTime;  
			
			
			if (mTime < totalT) 
				mTime = totalT; 
			
			
		}
		return mTime; 
	}
	
	public int InitialDuration () 
	{
		
		return Integer.parseInt(elapsedTime1); 
	}
	public int TradeDuration () 
	{
		return Integer.parseInt(elapsedTime2); 
	}
	
	
	
	public void SendCumulatives() 
	{
		TakeScreenShot_Exp(); 
		ACLMessage msg = new ACLMessage (ACLMessage.INFORM); 
		AID aid = new AID(callerAgent, AID.ISLOCALNAME); 
		
				 
		msg.addReceiver(aid);
		
		int totalWay = 0;
		int j; 
		int maxsch = 0; 
		int maxpick =0; 
		int maxexec = 0; 
		for (j=0; j<robotCount; j++) 
		{
			totalWay += robots[j].cumulativeWay;
			
			if (robots[j].schTime > maxsch )
				maxsch  = robots[j].schTime; 
			if (robots[j].pickUpTime > maxpick )
				maxpick  = robots[j].pickUpTime; 
			if (robots[j].execTime> maxexec )
				maxexec  = robots[j].execTime; 
			
		}
		msg.setLanguage("English");
		msg.setContent("137_"+totalWay+"_"+(int) MaxWay()+"_"+InitialDuration()+"_"+TradeDuration()+"_"+maxsch+"_"+maxpick+"_"+maxexec+"_"+MaxTime());
		send(msg);
		Yaz(terrain+"> K�m�latif toplam " + callerAgent + " a g�nderildi.  "+totalWay);
		
		
	}
	
	class StartSimListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) 
		{
						
			ACLMessage msg = new ACLMessage (ACLMessage.INFORM); 
			AID aid = new AID(auctioneer, AID.ISLOCALNAME); 
			//AID aid2 = new AID("M2", AID.ISLOCALNAME);
			msg.addReceiver(aid);
			//msg.addReceiver(aid2);
			msg.setLanguage("English");
			msg.setContent("61_"+Integer.toString(EXECUTION_MODE.getValue())+"_"+Integer.toString(TRADE_APPROACH.getValue())+"_"+Integer.toString(simSpeed));
			send(msg);
			Yaz(terrain+">****************************************************  ");
			Yaz(terrain+">Simulasyon Baslatma talebi (61), MUZAYEDECI'ye gonderildi.  ");
			Yaz(terrain+">�al��ma Modu :  " + EXECUTION_MODE );
			Yaz(terrain+">PAZARLIK YAKLA�IMI :  " + TRADE_APPROACH );
			Yaz(terrain+">S�M�LASYON HIZI :  " + Integer.toString(simSpeed) );
			Yaz(terrain+">****************************************************  ");
			RUNNING = 1; 
			
			
			//TakeScreenShot();
           
		
			
		    
		}
		
		
	}
	
	class PauseSimListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) 
		{
						
			ACLMessage msg = new ACLMessage (ACLMessage.INFORM); 
			AID aid = new AID(auctioneer, AID.ISLOCALNAME); 
			AID aid2 = new AID("M2", AID.ISLOCALNAME); 
			msg.addReceiver(aid);
			msg.addReceiver(aid2);
			msg.setLanguage("English");
			msg.setContent("62");
			send(msg);
			Yaz(terrain+">Simulasyon Duraklatma talebi, MUZAYEDECIYE gonderildi.  ");
			
		}
		
		
	}
  class StopSimListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) 
		{
						
			ACLMessage msg = new ACLMessage (ACLMessage.INFORM); 
			AID aid = new AID(auctioneer, AID.ISLOCALNAME);
			AID aid2 = new AID("M2", AID.ISLOCALNAME); 
			msg.addReceiver(aid);
			msg.addReceiver(aid2);
			msg.setLanguage("English");
			msg.setContent("63");
			send(msg);
			Yaz(terrain+">Simulasyon Durdurma talebi, MUZAYEDECIYE gonderildi.  ");
			
		    
		}
		
		
	}
	
	class AddTaskListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) 
		{
			
			// Burada istisna ve s�n�r kontrol� yap�lmal�d�r.
			// Simdilik kullan�c�n�n do�ru girdi�i varsay�lacakt�r. 
		
			ACLMessage msg = new ACLMessage (ACLMessage.INFORM); 
			AID aid = new AID(auctioneer, AID.ISLOCALNAME);
//			AID aid2 = new AID("M2", AID.ISLOCALNAME); 
			msg.addReceiver(aid);
//			msg.addReceiver(aid2);
			msg.setLanguage("English");
			msg.setContent("81_"+tf1.getText()+"_"+tf2.getText()+"_"+tf3.getText());
			send(msg);
			Yaz(terrain+">Yeni Gorev talebi, MUZAYEDECIYE gonderildi.  ");
		    
		}
		
		
	}
	
class AddRobotListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) 
		{
			// Burada istisna ve s�n�r kontrol� yap�lmal�d�r.
			// Simdilik kullan�c�n�n do�ru girdi�i varsay�lacakt�r. 
		
			ACLMessage msg = new ACLMessage (ACLMessage.INFORM); 
			AID aid = new AID("ParametricInitiator", AID.ISLOCALNAME);
			AID aid2 = new AID(auctioneer, AID.ISLOCALNAME);
			msg.addReceiver(aid);
			msg.addReceiver(aid2);
			msg.setLanguage("English");
			msg.setContent("01_"+rtf1.getText()+"_"+rtf2.getText()+"_"+rtf3.getText()+"_0");
			send(msg);
			Yaz(terrain+">Yeni Robot Olusturma talebi, ParameterInitiator a ve Auctioneer a gonderildi.  ");
		}
	}
	
public void PrintWallWatch() {
		
		mrt.PrintWallWatch(); 

	}
	
		class SaveAndCloseListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) 
		{
			
			SendCumulatives(); 
		    
		}
		
		
	}
	  public class RefreshTicker extends TickerBehaviour {

    	  /**
		 * 
		 */
		private static final long serialVersionUID = 584155733334479024L;

		public RefreshTicker (Agent a, long interval)
    	  {
    	    	super(a,interval);     	
    	  }
    	  
    	  protected void onTick() 
    	  {
    		  f.drawStuff(); 
    		  
    	  }
	  } 
	

    public class ListenNReply extends TickerBehaviour {

    	  /**
		 * 
		 */
		private static final long serialVersionUID = -6190586566576828846L;

		public ListenNReply (Agent a, long interval)
    	  {
    	    	super(a,interval);     	
    	  }
    	  
	      public void EndOfATradeMessageArrived(String temp[]) 
		  {
				 if (latestTrade == -1 )
				 {
					 latestTrade++; 
					 tradeList[latestTrade] = new Trade(); 
				 }
				 else if (tradeList[latestTrade].TBundle.compareTo(temp[5])!=0)
				 {
					 latestTrade++; 
					 tradeList[latestTrade] = new Trade();
				 }
				
				
				 tradeList[latestTrade].Seller = temp[3]; 
				 tradeList[latestTrade].TBundle = temp[5]; 
				// tradeList[latestTrade].TBundle = temp[5]; 
				 
				 
	      	   if (TaskBundleIndex(temp[6])<0)
	      	   {
	      		   tradeList[latestTrade].status = eTradingProgress.FAILURE; 
	      		   tradeList[latestTrade].Buyer = "-"; 
	      	    }
	      	   else 
	      	   {
	      		 tradeList[latestTrade].status = eTradingProgress.SUCCESS; 
	      		tradeList[latestTrade].Buyer = temp[4]; 
	      	   }
		  }
		  
	      public void TaskSoldMessageArrived(String temp[])
	      {
	    	  trades[tradeCount][0] = temp[1]; 					 
				 trades[tradeCount][1] = temp[2];
				 trades[tradeCount++][2] = temp[3];
				 
				 int tIndex = TaskBundleIndex(temp[3]); 
				 taskBundles[tIndex].state = eTaskState.CONFIRMED; 
				 for (int i=0; i<taskBundles[tIndex].taskCount; i++)
				 {
					 Task t = taskBundles[tIndex].GetTask(i);
					 t.state=eTaskState.CONFIRMED;
					 t.owner  =RobotIndex(temp[1]); 
					 t.ownerName = temp[1]; 
					taskBundles[tIndex].SetTask(i, t);  
					 
				 }

	      }
	      
	      public void ParseMessage (String temp[]) 
	      {
	    	  
				 if (temp[0].compareTo("71") == 0 ) 
				 {
					 AddUpdateRobot(temp);

				 }
				 
				 else if (temp[0].compareTo("06") == 0 ) 
				 {
					 AddUpdateTask(temp);

				 }
				 // G�rev update
				 else if (temp[0].compareTo("72") == 0 ) 
				 {
					 AddUpdateTask(temp);
				 }
				   
				 // trade
				 else if (temp[0].compareTo("73") == 0 ) 
				 { 
					TaskSoldMessageArrived(temp);
				 }
				 else if (temp[0].compareTo("114") == 0 ) 
				 { 
		      	 	EndOfATradeMessageArrived(temp);
				 }
				 
				 else if (temp[0].compareTo("115T") == 0 ) 
				 { 
					
					 					 
					 latestTrade++  ; 
					 tradeList[latestTrade] = new Trade(); 
					 tradeList[latestTrade].Seller = "new" ;
					 tradeList[latestTrade].TBundle = "new"; 
					 tradeList[latestTrade].status = eTradingProgress.IDLE; 
         	      	 tradeList[latestTrade].Buyer = "-"; 
					  
			 
				 }
				
				 else if (temp[0].compareTo("118") == 0 ) 
				 { 
				      
				     elapsedTime1 = temp[1];
				     elapsedTime2 = temp[2]; 
				     elapsedTime3 = temp[3]; 
				   
				      
				 }
				 else if (temp[0].compareTo("120") == 0 ) 
				 { 
				      
				     executionPhase  = eExecutionPhase.values()[Integer.parseInt(temp[1])];
				     auctionsMade =  Integer.parseInt(temp[2]); 
				   
				      
				 }
				 
				 else if (temp[0].compareTo("136") == 0 ) 
				 { 
				      SendCumulatives(); 
				   
				      
				 }

				 
				 else if (temp[0].compareTo("191") == 0 ) 
				 {
					 f.setVisible(false);
					 System.out.println (" KEND�M� S�L�YORUM ");
					 doDelete();
				  //	taskListFrame.dispose();
					/*tradeLogFrame.setVisible(false);
					logFrame.setVisible(false);
					 */
					/*for (int i=0;i<tradeCount; i++)
					{
						trades[i] = null; 
						
					}*/
				  // is = null; 
					 
					 
					//if (f!=null) 
					//	
					
				     
				     
				 }
				 else if (temp[0].compareTo("181") == 0 ) 
				 {
				      
					System.out.println("181 rrt talebi geldi");
					 f.maxC = Integer.parseInt(temp[1]);  
					 f.x = new int[f.maxC]; 
					 f.y= new int[f.maxC]; 
					 f.from = new int[f.maxC]; 
					 f.delta = Integer.parseInt(temp[2]);
					 f.BuildRRT(420,420,230,220);
					
				 }
				 
				 else if (temp[0].compareTo("1001") == 0 ) 
				 {
				      
					System.out.println("1001 ldr verisi geldi");
					
					  int rIndex = RobotIndex(temp[1]); 
					  //Yaz("arena > 71 > schtime "+ temp[10]);
			           
					 
					  if (rIndex>=0)
					  {
						  robots[rIndex].heading = Integer.parseInt(temp[2]); 
						  robots[rIndex].numberOfSectors = Integer.parseInt(temp[3]); 
						  for (int i=0; i<robots[rIndex].numberOfSectors; i++) 
						  {
							  robots[rIndex].meanProximities[i] = Integer.parseInt(temp[4+i]); 
						  }
						  
					  }
					  else 
					  {
						  // doNothing wait 
					  }
					
				 }


	      }
	      
    	  protected void onTick() 
    	  {
			ACLMessage msg = receive(); 
			if (msg != null) 
			{
				 String content = msg.getContent();  
				 String delimiter = "_"; 
				 String [] temp; 
				 temp = content.split(delimiter);
				// int localCounter = 0; 
				 // Robot update
				 
				 ParseMessage(temp); 

		            if (f!=null)     
						 f.drawStuff(); 
		    }
			
	    	  else 
	    	  {
	    		  block(); 
	    	  }
			
			//f."f();
		}



		
	}


}


