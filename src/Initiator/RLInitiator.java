package Initiator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import Enums.eRobotStatus;
import MRTAStreamManager.*;
import MRTAmain.*;
import MRTAmain.SimManagerAgent.EvaluateMessages;
import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.wrapper.ContainerController;
import jade.content.onto.basic.Action;
import jade.core.ContainerID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.proto.AchieveREInitiator;
import jade.wrapper.AgentController;
//import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
//import sun.misc.Contended;


///  Author: Sava� �zt�rk 
//   Date: 2021 04 01 
//   This class initiates MRTASim in order to open it in Reinforcement Learning Mode


public class RLInitiator extends GuiAgent {
	
	SimParameters simPar = null; 
	ContainerController container ;
	
	public String [] createdAgents = new String[105];
	public int agentCount = 0;
	public AgentController t1 = null;
	public int robotColorIndex = 0;
	boolean restartFlag = true;
	RLInitiator thisa = this;
	int heatMapCreationTime=0;
	public int taskSetUnderTest = 0;
	public ResultSet results = new ResultSet(1000);
	
	
	EvaluateMessages evalProp = new EvaluateMessages(this, 1000);
	
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void Sleeep (int timeout) 
	{
		try{
				Thread.currentThread();
			//	do what you want to do before sleeping
				Thread.sleep(timeout);//sleep for 1000 ms
				//	do what you want to do after sleeptig
				}
			catch(InterruptedException ie){
				//		If this thread was intrrupted by nother thread 
			} finally {
		}      
	}
	
	protected void setup () 
	{
		
		System.out.println(""); 
	    System.out.println("RLInitiator Agent Started");
     	System.out.println("RLInitiator>setup() ");
    	System.out.println("");
    	
		addBehaviour(evalProp);
		Object[] args = getArguments();
		
		if (args != null) {
			simPar = (SimParameters) args[0];
			simPar.callerAgent = "RLInitiator";
			System.out.println("RLInitiator-> Received and assigned SimParamters Object " + simPar.setupSelection);
			StartAuctioneer(simPar, 0);
			StartTerrain(simPar, 0);
			StartRobotsFromFile (simPar, 0);
		} 
		else 
		{
			System.out.println("I am RLInitiator! I am killed by you, because no parameter is sent to me!");
		}
		
					
		
		
	}
	
	public void actionPerformed(ActionEvent e) 
	{
					
	/*	int sTaskCount = 0; 
		int sRobotCount = 0; 
		int terrainWidth = Integer.parseInt(tfTerrainWidth.getText()); 
		int terrainHeight = Integer.parseInt(tfTerrainHeight.getText());
	//	int taskSeed = Integer.parseInt(tfTaskSeed.getText());
		ucCapacity = Integer.parseInt(tfucCapacity.getText());
		tradeCapacity = Integer.parseInt(tftradeCapacity.getText());
		
		 
		 sTaskCount = Integer.parseInt(tf1.getText()); 
		 sRobotCount = Integer.parseInt(tf2.getText());
		 
		 expMode = 0; 
		 if (expMode == 5) 
			 CalculateRobotBoundaries(sRobotCount, sTaskCount);
		 
		 
		 StartTerrain(sTaskCount, sRobotCount);  
		 StartAuctioneer (sTaskCount, terrainWidth, terrainHeight); 
	     StartRobots(sRobotCount, terrainWidth, terrainHeight) ; 				
	*/
	} // action

	
	
	
	
	
	
	public void StartAgent (String agentName, String agentType, Object[] targs)
	{
		try {
			// create agent t1 on the same container of the creator agent
			//int stationCount = Integer.parseInt(tfStationCount.getText()) ; 
					
			
			//= (AgentContainer) getContainerController(); // get a container controller for creating new agents
			jade.core.Runtime runtime = jade.core.Runtime.instance();
			//Create a Profile, where the launch arguments are stored
			Profile profile = new ProfileImpl();
			profile.setParameter(Profile.CONTAINER_NAME, "c1");
			profile.setParameter(Profile.MAIN_HOST, "localhost");
			//container=null; 
			
			if (container==null)
			{
				container =  runtime.createAgentContainer(profile);
			      	
			}
			else 
			{
			   container = getContainerController(); 
			}
			
			createdAgents[agentCount++]  = agentName; 
			
			t1 = ((ContainerController) container).createNewAgent(agentName, agentType, targs);
			t1.start();
			System.out.println(getLocalName()+" CREATED AND STARTED NEW : "+ agentType +" NAMED " +agentName + " ON CONTAINER "+((ContainerController) container).getContainerName());

			} catch (Exception any) {
				any.printStackTrace();
              }
	 }

	public void StartTerrain (SimParameters sp, int experimentCounter) 
	{
	
			
					String name = "RLArena"+experimentCounter; 
			       
					Object[] targs = new Object[4];
					targs[0] = name;
					targs[1] = sp.callerAgent;
					targs[2] = "RLAuctioneer"+experimentCounter; 
					targs[3] = sp;  
				
					createdAgents[agentCount++]  = name;
	     			StartAgent (name, "MRTAmain.TerrainAgent", targs);
				  
					
	
	}
	

	
	
	public void StartAuctioneer (SimParameters sp, int experimentCounter) 
	{
	
			    
					String name = "RLAuctioneer"+experimentCounter;  
			       
					Object[] targs = new Object[12];
					
					// Auctioneer Name
					targs[0] = name; 
					
					// random seed
					targs[1] = 0;
					
						
					// taskCount
					targs[2] = sp.taskCount;
					
					// 
					targs[3] = sp.TERRAIN_WIDTH; 
					
					targs[4] = sp.TERRAIN_HEIGHT; 
					
					targs[5] = sp.templateSelection; 
					
					targs[6] = sp.initialTaskAssignmentMethod.getValue();
					targs[7] = sp.ucCapacity;
					targs[8] = "RLArena"+experimentCounter;
					targs[9] = sp.autoMode;
					targs[10] = sp.expMode.getValue();
					targs[11] =  sp.setupSelection; 
					
					createdAgents[agentCount++]  = name; 
					
			
					StartAgent (name, "MRTAmain.AuctioneerAgent", targs);
			
	}
	
	
	public void WaitAMinute (int delay) 
	{
		 try{
	  			Thread.currentThread();
				//	do what you want to do before sleeping
	  			Thread.sleep(delay);//sleep for 1000 ms
	  			//	do what you want to do after sleeptig
	  			}
	  		catch(InterruptedException ie){
	  			//		If this thread was intrrupted by nother thread 
	  		} finally {
			}      
	}
	
	public void StartRobotsFromFile (SimParameters sp, int experimentCounter) 
	{
		
	
		RobotReader rr = new RobotReader(sp.templateSelection); 
		
		for (int kk=0; kk<sp.robotCount; kk++) 
		{
         
			Robotum r = rr.GetRobot(kk);
			WaitAMinute(200);
			StartARobot(sp, r, experimentCounter);
			
		
		
		} // for 
	}	
	
	public void StartARobot (SimParameters sp, Robotum r, int experimentCounter) 
	{
	
	
	
	String rname = r.robotName;
	Object[] rargs = new Object[26];
	rargs[0] = rname; 
	rargs[2] = r.yLoc;
	rargs[1] = r.xLoc;
	rargs[3] = "RLAuctioneer"+experimentCounter;
	rargs[4] = (robotColorIndex++)%10;
	rargs[5] =sp.approachSelection.getValue();
	rargs[6] = sp.TERRAIN_WIDTH-10; 
	rargs[7] = sp.TERRAIN_HEIGHT-10; 
	rargs[8] = sp.templateSelection; 
	rargs[9] = sp.loopModeSelection.getValue();
	rargs[10] = sp.approachSelection.getValue();
	rargs[11] = sp.robotSpeed;
	rargs[12] = sp.stationCount; 
	rargs[13] = sp.ucCapacity; 
	rargs[14] =sp.tradeCapacity ;
	rargs[15] = "RLArena"+experimentCounter ;
	rargs[16] =r.rect.x1; 
	rargs[17] =r.rect.y1; 
	rargs[18] =r.rect.x2; 
	rargs[19] =r.rect.y2; 
	rargs[20] =sp.expMode.getValue(); 
	rargs[21] =sp.pickUpTime;
	rargs[22] =sp.localScheduleSelection.getValue();
	rargs[23] = sp.pathStyleSelection.getValue(); 
	rargs[24] = sp.autoMode; 
	rargs[25] = sp.setupSelection; 
	
			createdAgents[agentCount++]  = rname; 
			StartAgent (rname, "MRTAmain.RLRobotAgent", rargs);
			
	}
	
	public void Parametric () 
	{
		
		
	}
	
	public void Automatic () 
	{
		
		
	}
	
	public void SelectedSet () 
	{
		
		
	}
	
	public void HeatMap () 
	{
		
		
	}

	
	public boolean AllTasksCovered (int robotCount, int taskCount) 
	{

		
		 TaskReader tr = new TaskReader(simPar.templateSelection);
		 RobotReader rr = new RobotReader(simPar.templateSelection);
		 		 
		 
		for (int i=0 ; i< taskCount; i++) 
		{
		    Task t = tr.GetTask(i);
		    
		   
		    boolean coveredFlag = false; 
			for (int j=0; j<robotCount; j++) 
			{
				HRectangle rect = rr.GetRobot(j).rect; 
				if (t.xLoc>rect.x1 && t.xLoc <rect.x2 && t.yLoc >rect.y1 && t.yLoc <rect.y2) 
				{
					coveredFlag = true; 
			    }
			}
			if (coveredFlag == false) 
			{
				System.out.println(" G�rev hi�bir robot taraf�ndan kapsanm�yor : " + t.taskName);
				return false;
			}
		}
		 
		 return true; 
	}
	

	public class EvaluateMessages extends TickerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 657002871747329933L;

		public EvaluateMessages(Agent a, long interval) {
			
			super(a, interval);
			System.out.println ("RLInitiator-> Evaluate Messages Constructor ");
		}
		
		
		

		protected void onTick() {
			
			ACLMessage msg = receive();
			
			if (msg != null) {
				
				String content = msg.getContent();
				System.out.println ("RLInitiator-> A message is arrived... : " + content);
				
				String delimiter = "_";
				String[] temp;
				temp = content.split(delimiter);
			
				ACLMessage repmsg = msg.createReply();
				repmsg.setPerformative(ACLMessage.INFORM);
				
				System.out.println ("PI> Mesaj geldi..."+temp[0]);
				/*
				 *777// Robot tan�mlama mesaj� ise 01_robotName_locX_locY
				*/
				
				if (temp[0].compareTo("01") == 0) {
	                System.out.println ("PARAMETRICINITIATOR> Yeni robot ekle talebi geldi "+temp[1]);
	                Robotum r=new Robotum(); 
	                r.robotName = temp[1]; 
	                r.xLoc = Integer.parseInt(temp[2]);
	                r.yLoc = Integer.parseInt(temp[3]);
	                r.status = eRobotStatus.values()[Integer.parseInt(temp[4])];
	                r.rect = new HRectangle(0, 0, simPar.TERRAIN_WIDTH, simPar.TERRAIN_HEIGHT);
	                StartARobot(simPar, r, 0);

				} 
				
				if (temp[0].compareTo("115G") == 0) {
	                System.out.println ("SIMMANAGER> Otomatik Reset Talebi 115 geldi...");

				} 
				if (temp[0].compareTo("191") == 0) {
	                System.out.println ("PARAMETRICINITIATOR> Acil Reset Talebi 191 geldi...");
	                 addBehaviour(new ResetWaker(thisa, 100));
			        
			       
	            	   restartFlag = false; 

				} 
				
				if (temp[0].compareTo("137") == 0) {
				 
				    System.out.println ("PARAMETRICINITIATOR> Ola�an Reset Talebi 137 geldi...");
	                System.out.println ("SIMMANAGER> K�m�latif toplam geldi..."+temp[1]);
					
	                /*
	                 * remp 1 totalway
	                 * temp 2 maxway
	                 * temp 3  
	                 * 
	                 * 
	                 * 
	                 * */
	                
	                WayCalculator wc = new WayCalculator(); 
	                DateTimeStr dt = new DateTimeStr(); 
	                ExperimentResult er = new ExperimentResult(); 
	                
	                int stepCount = new ExperimentStepReader().getStep(); 
	                er.idNumber = stepCount++;
	                WriteStep(er.idNumber);

	                er.setupStr = simPar.setupSelection; 
	                er.totalWay = (int) wc.PhysicalWay((double) Integer.parseInt(temp[1]), simPar.physicalWidth, simPar.TERRAIN_WIDTH);
	                er.maxWay  = Integer.parseInt(temp[2]);
	                er.initialTime = Integer.parseInt(temp[3])/1000 + 1;
	                er.tradeTime = Integer.parseInt(temp[4]) / 1000 + 1;
	                er.schedulingTime = Integer.parseInt(temp[5]);
	                er.pickUpTime = Integer.parseInt(temp[6]);
	                er.execTime = Integer.parseInt(temp[7]);
	                er.maxRobotime = Integer.parseInt(temp[8]);
	                er.templateName = simPar.templateSelection; 
	                er.approachSelection =simPar.approachSelection; 
	                er.ucCapacity = simPar.ucCapacity ; 
	                er.initialAssignment =  simPar.initialTaskAssignmentMethod; //;simPar.initialTaskAssignmentMethod; simPar.initialTaskAssignmentMethod;
	                er.tradeCapacity = simPar.tradeCapacity;
	                er.expMode = simPar.expMode; 
	                er.heatRadius = simPar.heatDiameter ; 
	                er.heatTasks = simPar.tasksPerHeat; 
	                er.heatDist = simPar.stationProx; 
	                 
	                er.dtstr = dt.DtString();
	              
	                er.heatMapCreationTime = heatMapCreationTime ; 
	   
	               // er.stationSet = stationSet; //stSet[gStationCount-3][g_s];
	                int cTaskCount = simPar.taskCount; 
	                cTaskCount = cTaskCount==0 ? 10:cTaskCount;
	                taskSetUnderTest = taskSetUnderTest >=0 ? taskSetUnderTest:0;
	                er.taskSet =simPar.taskSet;  
	                
	                er.ToFile(simPar.templateSelection); 
	             
	           //	 FileReader fr = new FileReader(); 
			   //	 fr.WriteGeneticSet (templateSelection, gStationCount, st.stCoor , wc.PhysicalWay(st.totalWay),wc.PhysicalWay(st.maxWay), Integer.parseInt(temp[3]), Integer.parseInt(temp[4]), Integer.parseInt(temp[5]), Integer.parseInt(temp[6]), Integer.parseInt(temp[7]), Integer.parseInt(temp[8]), cTaskCount , gStepCount);
				   
	                results.AddResults(er); 
	                results.toFile(); 
	               
	               if (simPar.autoMode == 1)
	               {  
	            	   addBehaviour(new ResetWaker(thisa, 1000));
			        
			       
	            	   restartFlag = false; 
	            	   addBehaviour(new RestartWaker(thisa, 25000)); 
	               }
					
					

				} 
			} // msg != null 
			
		}// on Tick 
		
		}
		
		public class RestartWaker extends WakerBehaviour {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestartWaker(Agent a, long interval) {
		super(a, interval);
	}

	protected void handleElapsedTimeout() {

		/*StartGeneticListener sgl = new StartGeneticListener(); 
		sg l.actionPerformed(null);
		*/ 
		 restartFlag = true; 

	}
}

public class ResetWaker extends WakerBehaviour {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResetWaker(Agent a, long interval) {
		super(a, interval);
	}

	protected void handleElapsedTimeout() {

		ResetListener rs= new ResetListener(); 
		rs.actionPerformed(null);           

	}
}



class ResetListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) 
		{
			ACLMessage msg = new ACLMessage (ACLMessage.INFORM); 
					 
			for (int j=0; j< agentCount; j++) 
			{
				
				msg.addReceiver(new AID(createdAgents[j], AID.ISLOCALNAME));
				System.out.println("Parametric Initiator> Reset talebi " + createdAgents[j] + " etmenine gonderildi.  ");
			}
			
			msg.setLanguage("English");
			msg.setContent("191");
			send(msg);
			System.out.println("Parametric Initiator> Reset talebi butun etmenlere gonderildi.  ");
			doDelete();
			
		}
			
	}
		
		public void WriteStep (int stepp)
		{
	        ArrayList<String> step = new ArrayList<String>(); 
	        step.add(Integer.toString(stepp));
	        new MRTAFileWriter().WriteToFile("\\Experiments\\ExperimentCounter.txt",step);
		}





}			 	 




