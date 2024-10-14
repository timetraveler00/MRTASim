package Initiator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Enums.eRobotStatus;
import Initiator.ParametricInitiator.EvaluateMessages;
import Initiator.ParametricInitiator.ResetListener;
import Initiator.ParametricInitiator.ResetWaker;
import Initiator.ParametricInitiator.RestartWaker;
import MRTAStreamManager.ExperimentStepReader;
import MRTAStreamManager.TaskSetReader;
import MRTAmain.DateTimeStr;
import MRTAmain.ExperimentResult;
import MRTAmain.HRectangle;
import MRTAmain.ResultSet;
import MRTAmain.Robotum;
import MRTAmain.SimParameters;
import MRTAmain.WayCalculator;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class TaskSetInitiator extends ParametricInitiator{
	
	int experimentCounter = 0; 
	int cTaskCount = 0; 
	
	TaskSetInitiator thisa = this;
	//EvaluateMessages1 evalPropT = new EvaluateMessages1(thisa, 1000);
	ListenToMe ltm = new ListenToMe();
	/*
	SimParameters simPar = null; 
	ContainerController container ;
	
	public String [] createdAgents = new String[105];
	public int agentCount = 0;
	public AgentController t1 = null;
	public int robotColorIndex = 0;
	boolean restartFlag = true;
	int heatMapCreationTime=0;
	public int taskSetUnderTest = 0;
	public ResultSet results = new ResultSet(1000);
	*/
	public class PerformWaker extends WakerBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public PerformWaker(Agent a, long interval) {
			super(thisa, interval);
		}

		protected void handleElapsedTimeout() {

			ltm.start();
			//ltm.actionPerformed(null);

		}
	}
	protected void setup () 
	{
		 addBehaviour(evalProp);
		 
		System.out.println(""); 
		System.out.println("TaskSetInitiator Agent Started");
		System.out.println("TaskSetInitiator>setup() ");
		System.out.println("");

		Object[] args = getArguments();
		if (args != null) {
			simPar = (SimParameters) args[0];
			System.out.println("Received initialTaskAssignmentMethod: " + simPar.initialTaskAssignmentMethod);
		}
		
		simPar.callerAgent = this.getLocalName();	
		// Is preprocessing needed?
		/*StartAuctioneer(simPar, 0);
		StartTerrain(simPar, 0);
		StartRobotsFromFile (simPar, 0);
		*/
		// perform();
		
		 addBehaviour(new PerformWaker(thisa, 10));
		
		//ltm.actionPerformed(null);
		// Configuring robot/station locations
		
				
		// Heatmap - extended rectangular 
		// Selected Set  - circular/all
		// Manual Configuration - circular/all 
		// Random locations   
		
		
		
	}
	public void WriteTempTasks (int stepCount) 
	{
		System.out.println("TaskSetInitiator->WriteTempTasks()->Task Count  : "+cTaskCount + " step : "+stepCount);
		TaskSetReader tsr = new TaskSetReader(simPar.templateSelection, simPar.taskCount, stepCount);
		simPar.taskSet = tsr.GetTaskSet();
		//tSet[cTaskCount-1][stepCount] = tsr.GetTaskSet();
		tsr.GetTaskSet().ToFile(simPar.templateSelection);
		//fr.WriteTasks(templateSelection, cTaskCount, tsr.GetTaskSet()); 
				//tSet[cTaskCount/10-1][stepCount].ToFile(templateSelection) ; 
		
	}
	class ListenToMe extends Thread
	{
	
		public void run () 
		{

		//int terrainWidth = Integer.parseInt(tfTerrainWidth.getText()); 
		//int terrainHeight = Integer.parseInt(tfTerrainHeight.getText());
	//	 int taskSeed = Integer.parseInt(tfTaskSeed.getText());
		
		System.out.println("TaskSetInitiator>perform() ");

	     restartFlag = true;
		 int gStepCount = simPar.taskSetRangeStart; 
		 int rEnd = simPar.taskSetRangeEnd +1;
		 //simPar.callerAgent = "TaskSetInitiator"; 
		 /*if (simPar.autoMode == 0) 
		 {
			 rEnd = gStepCount + 1; 
		 }*/
		 
			// TASK COUNT 
 		cTaskCount = simPar.taskCount; // Integer.parseInt(numberofTasks.getSelectedItem()); // Integer.parseInt(tf1.getText()); 
		 int wticker =0; 
		 while (gStepCount < rEnd) 
         {
      	     System.out.println("TaskSetInitiator>perform()->while -- gStepCount is less than rEnd so we are in the while block " + gStepCount + " < " +rEnd );
      	     System.out.println("TaskSetInitiator>perform()->while -- restartFlag " + restartFlag );
			 
			 while (restartFlag == false) 
			  	{
				 System.out.println("TaskSetInitiator>perform()->while -- restartFlag " + restartFlag + " WHILE TICKER : " +wticker++);	
				 try{
			  			Thread.currentThread();
						//	do what you want to do before sleeping
			  			Thread.sleep(5000);//sleep for 1000 ms
			  			//	do what you want to do after sleeptig
			  			}
			  		catch(InterruptedException ie){
			  			//		If this thread was intrrupted by nother thread 
			  		} finally {
					}      
	  
			  	}
	
				experimentCounter++; 

				// G�revler Experiments klas�r�nden al�n�p config dizinine yaz�l�yor        		
				int g_t = gStepCount;  
    	 		WriteTempTasks (g_t); 
    	 		//simPar.stationCount; 
        		/* is = new IndoorStructure(templateSelection);
    	 		 FileReader fr= new FileReader(); 
    			 cTaskCount = g_t = fr.WriteTasksUniform(templateSelection, is, 40); 
    	 		*/
    	/* 		int gStationCount = simPar.stationCount; //Integer.parseInt(tfStationCount.getText());
    	 		int gRobotCount = simPar.robotCount; // Integer.parseInt(tf2.getText());
 
    	 		
     			 approachSelection = approachSelector.getSelectedIndex()+1; //approaches [m_i]; 
             	 iapproachSelection = approachSelector.getSelectedIndex()+1;  // approaches [m_i];
    	 		  	 
    	 		  	 
    	 		 
		 		 itam = initialTaskAssgnSelector.getSelectedItem(); 
		 		 
		 		ucCapacity = Integer.parseInt(tfucCapacity.getText()); //ucCapacityMax [c_i];
		  	    tradeCapacity = Integer.parseInt(tftradeCapacity.getText()); // tradeCapacityMax [c_j] ; 
        		hmct = 0;
		  	    
		  	    switch (expMode) 
	           {
	                
	                // fodfocus veya custom s�cakl�k
	                case 4: 
	                	
	                	long begin = System.currentTimeMillis();
        				// �stasyonlar heatmap'e g�re otomatik olarak belirlenip ayarlan�yor.
        				g_s = 0; 
    	 
        		       		
        				heatDiameter = (chHeatDiameter.getSelectedIndex()*2+7) - 2;  
        				// (Ba�lang�� i�in 2 eksi�ini vermek gerekiyor.)
        				do {
        			
        					heatDiameter+=4; 
        					WriteHeatStations(); 
        				} while (!AllTasksCoveredByHeat()) ;
        		 
		 
        				long end = System.currentTimeMillis();
        				hmct = (int) (end-begin)/1000 ; 
	                	
	                	Mesaj ("F O D F O C U S ");
	                	
	                	/*
	                	g_s = 0; 
	                	heatDiameter = chHeatDiameter.getSelectedIndex()*2+7; 
	                	*
	                break;
	                
	                // hippobots
	                case 5: 
	                	 
	                    g_s = 20; 
	                    WriteTempStations(g_s);
	                     Mesaj ("H I P P O B O T S "); 
	                   
	                break;
	                
	                
	                // follow me
	                case 1: 
	                    
	                     g_s = 21; 
	                	 WriteTempStations_FM(g_s);
	                	 
	                	 Mesaj ("F O L L O W M E  ");
	                	 
	                break; 
	                
	                
	                // Custom
	                // Daha sonra custom i�in d�zenlemeyap�lacak
	                /*case 3: 
	                	
	                  	Mesaj ("C U S T O M  "); 
	                	setupStr = "Custom";
	                	
	                	
	                	
	                	
	                break; *  
	                //default : 
	                	
	                	
	                break; 
	           }
	*/

				
					  	agentCount = 0;
					  	 
						
					  	StartAuctioneer(simPar, 0);
					  	StartTerrain(simPar, 0);
						StartRobotsFromFile (simPar, 0);
					  	
			  	//StartTerrain(cTaskCount, gStationCount, gRobotCount);  
			  	//StartAuctioneer (taskSeed, cTaskCount); 
			  	//StartRobots(gRobotCount) ;
		        gStepCount++; 	 
		        restartFlag = false; 
		        
		        System.out.println("TaskSetInitiator>perform()->while -- gStepCount: " + gStepCount + " rEnd: " +rEnd );
		
	   
         }	
		 System.out.println("TaskSetInitiator>perform()->out of while -- gStepCount is not less than rEnd so we are NOT in the while block " + gStepCount + " <> " +rEnd );
	
	} // action

		}

	/*
	public class EvaluateMessages1 extends TickerBehaviour {

		/**
		 * 
		 * 
		 *
		int ticker = 0; 
		
		private static final long serialVersionUID = 657002871747329933L;

		public EvaluateMessages1(Agent a, long interval) {
			
			super(a, interval);
			System.out.println ("TaskSetInitiator>EvaluateMessages1 constructor..." );
			
		}
    	protected void onTick() {
			
			System.out.println ("TaskSetInitiator>onTick() ..." + ticker++);
			
			ACLMessage msg = receive();
			
			if (msg != null) {

				System.out.println ("TaskSetInitiator> Mesaj geldi...");
				// Mesaj�n ba�l���na g�re s�n�fland�rma
				String content = msg.getContent();
				String delimiter = "_";
				String[] temp;
				temp = content.split(delimiter);
			
				ACLMessage repmsg = msg.createReply();
				repmsg.setPerformative(ACLMessage.INFORM);
				
				System.out.println ("PI> Mesaj geldi..."+temp[0]);
				/*
				 *777// Robot tan�mlama mesaj� ise 01_robotName_locX_locY
				/
				
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
	                 addBehaviour(new ResetWaker(thisa, 000));
			        
			       
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
	                 * *
	                
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

	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		
	}*/

}
