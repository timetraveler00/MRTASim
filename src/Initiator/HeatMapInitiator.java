package Initiator;



//import Initiator.TaskSetInitiator.ListenToMe;
import Initiator.TaskSetInitiator.PerformWaker;
import MRTAStreamManager.RobotReader;
import MRTAStreamManager.TaskReader;
import MRTAStreamManager.TaskSetReader;
import MRTAmain.HRectangle;
import MRTAmain.SimParameters;
import MRTAmain.StationSet;
import MRTAmain.Task;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;



public class HeatMapInitiator extends TaskSetInitiator {

	
	 MRTAmain.HeatMap hm ; 
	 int hmct; 
	 int robotsPerStation = 1; 
	 HeatMapInitiator thisa = this;
		//EvaluateMessages1 evalPropT = new EvaluateMessages1(thisa, 1000);
		ListenToMe ltm = new ListenToMe();

	 	   
	 
	 int gStationCount = 5;
	 int gRobotCount = 5; 
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
		System.out.println("HeatMapInitiator Agent Started");
		System.out.println("HeatMapInitiator>setup() ");
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
		System.out.println("TaskSetInitiator->WriteTempTasks()->TaskCount : "+cTaskCount + " step : "+stepCount);
		TaskSetReader tsr = new TaskSetReader(simPar.templateSelection, simPar.taskCount, stepCount);
		simPar.taskSet = tsr.GetTaskSet();
		//tSet[cTaskCount-1][stepCount] = tsr.GetTaskSet();
		tsr.GetTaskSet().ToFile(simPar.templateSelection);
		//fr.WriteTasks(templateSelection, cTaskCount, tsr.GetTaskSet()); 
				//tSet[cTaskCount/10-1][stepCount].ToFile(templateSelection) ; 
		
	}
	
	class ListenToMe extends Thread
	{
	    
		public void WriteHeatStations () 
		{
	        hm = new MRTAmain.HeatMap(simPar.templateSelection, simPar.heatDiameter, simPar.tasksPerHeat, simPar.stationProx); 
			hm.ClusterCenters(); 
			gStationCount = hm.stationCount;
		//	tfStationCount.setText(Integer.toString(gStationCount));
			gRobotCount = gStationCount*robotsPerStation; 
			//tf2.setText(Integer.toString(gRobotCount));
			
			
			StationSet stSet = hm.GetStationSet(); 
			stSet.ToFileAsStations(simPar.templateSelection);
			stSet.ToFileAsRobots(simPar.templateSelection);
			simPar.robotCount = stSet.setSize; 
			simPar.stationCount = stSet.setSize;
			 
			 
			
		//?????    stationSet = CreateStationSet(hm.peakStations, gStationCount);  

		 
		}
		
		public boolean AllTasksCoveredByHeat () 
		{
			
				
			 TaskReader tr = new TaskReader (simPar.templateSelection); 
			 RobotReader rr = new RobotReader (simPar.templateSelection); 
			 
			 
			System.out.println("alltaskscoveredbyheat()");
			int tCount = tr.TaskCount(); 
			int rCount = rr.RobotCount(); 
			
			for (int i=0 ; i< tCount; i++) 
			{
			    Task t = tr.GetTask(i);
			    
		
			    boolean coveredFlag = false; 
				for (int j=0; j<rCount; j++) 
				{
					HRectangle rect = rr.GetRobot(j).rect; 
					//int dist = (int) CalcDistance(t.xLoc, t.yLoc, fr.robots[j].xLoc, fr.robots[j].yLoc);
					//int radius = (rect.x2-rect.x1)<=(rect.y2-rect.y1)?(rect.x2-rect.x1)/2:(rect.y2-rect.y1)/2; 
					
					//Ellipse2D.Double circle = new Ellipse2D.Double(rect.x1, rect.y1, (int) Math.abs(rect.x2-rect.x1), (int) Math.abs(rect.y2-rect.y1));
					
					
					//if (dist<= radius)
					if (t.xLoc>rect.x1 && t.xLoc <rect.x2 && t.yLoc >rect.y1 && t.yLoc <rect.y2) 
					//if (circle.contains(t.xLoc, t.yLoc))
					{
						coveredFlag = true; 
						
					}
					
				}
				
				if (coveredFlag == false) 
				{
					System.out.println(" G�rev hi�bir robot taraf�ndan kapsanm�yor (HEAT): " + t.taskName);
					return false;
				}
			}
			 
			 return true; 
		}
		
		public void run () 
		{

		//int terrainWidth = Integer.parseInt(tfTerrainWidth.getText()); 
		//int terrainHeight = Integer.parseInt(tfTerrainHeight.getText());
	//	 int taskSeed = Integer.parseInt(tfTaskSeed.getText());
		
		System.out.println("HeatMapInitiator>perform() ");

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
      	     System.out.println("HeatMapInitiator>perform()->while -- gStepCount is less than rEnd so we are in the while block " + gStepCount + " < " +rEnd );
      	     System.out.println("HeatMapInitiator>perform()->while -- restartFlag " + restartFlag );
			 
			 while (restartFlag == false) 
			  	{
				 System.out.println("HeatMapInitiator>perform()->while -- restartFlag " + restartFlag + " WH�LE T�CKER : " +wticker++);	
				 /*try{
			  			Thread.currentThread();
						//	do what you want to do before sleeping
			  			Thread.sleep(5000);//sleep for 1000 ms
			  			//	do what you want to do after sleeptig
			  			}
			  		catch(InterruptedException ie){
			  			//		If this thread was intrrupted by nother thread 
			  		} finally {
					}      
	              */ 
				 WaitAMinute(5000);
			  	}
	
				experimentCounter++; 

				// G�revler Experiments klas�r�nden al�n�p config dizinine yaz�l�yor        		
				int g_t = gStepCount;  
    	 		WriteTempTasks (g_t); 
    	 		
    	 		long begin = System.currentTimeMillis();
    			// �stasyonlar heatmap'e g�re otomatik olarak belirlenip ayarlan�yor.
    		//	g_s = 0; 

    	       		
    			int heatDiameter = simPar.heatDiameter; //(simPar.heatDiameter*2+7) - 2;  
    			// (Ba�lang�� i�in 2 eksi�ini vermek gerekiyor.)
    			do {
    		
    				heatDiameter+=20; 
    				WriteHeatStations(); 
    			} while (!AllTasksCoveredByHeat()) ;
    	 

    			long end = System.currentTimeMillis();
    			hmct = (int) (end-begin)/1000 ; 
    	    	
    	    	//Mesaj ("F O D F O C U S ");
    	    	
    	    	
    	    //	g_s = 0; 
    	    	// heatDiameter = simPar.heatDiameter;//simPar.heatDiameter * 2+7;  


				
					  	agentCount = 0;
					  	 
					  	 WaitAMinute(2000);
					  	
					  	StartAuctioneer(simPar, 0);
					  	 WaitAMinute(1000);
					  	 
					  	StartTerrain(simPar, 0);
					  	 WaitAMinute(1000);
					  	
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
	
	public void CreateHeatMap () 
	{
		
	}
	}
}
