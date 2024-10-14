package MRTAStreamManager;

import java.util.ArrayList;

import MRTAmain.RobotSet;
import MRTAmain.Robotum;


public class RobotReader extends MRTAFileReader {
	
	
	protected ArrayList<Robotum> robotList = new ArrayList<Robotum>(); 
    public int step = 0; 
	
	protected void CreateRobotArray () 
    {
    	
		
		for (int i=0; i<LineCount(); i++) 
    	{
			 Robotum r = new Robotum(); 
			 r.robotName = Get (i,0); 
			 r.xLoc = Integer.parseInt(Get(i,1)); 
			 r.yLoc = Integer.parseInt(Get(i,2)); 
	
			 if (RowSize(Get(i))  == 8 && step==0) 
			 {
				 r.rect.x1 =  Integer.parseInt(Get(i,4));
				 r.rect.y1 =  Integer.parseInt(Get(i,5));
				 r.rect.x2 =  Integer.parseInt(Get(i,6));
				 r.rect.y2 =  Integer.parseInt(Get(i,7)); 
			 }
			 else 
			 {
				
				 TerrainDimensionsReader tdr = new TerrainDimensionsReader(templateName); 
				 
			     // System.out.println(" WIDTH " + TERRAIN_WIDTH + " HEIGHT " + TERRAIN_HEIGHT) ; 
				 int respRadius = 100 + step *20; 

				 	r.rect.x1 = r.xLoc-respRadius/2>=0 ? r.xLoc-respRadius/2 : 0; 
				 	r.rect.x2 = r.xLoc+respRadius/2<tdr.getTERRAIN_WIDTH() ? r.xLoc+respRadius/2 : tdr.getTERRAIN_WIDTH()-1;
				 	r.rect.y1 = r.yLoc-respRadius/2>=0 ? r.yLoc-respRadius/2 : 0;
					r.rect.y2 = r.yLoc+respRadius/2<tdr.getTERRAIN_HEIGHT() ?r.yLoc+respRadius/2 : tdr.getTERRAIN_HEIGHT()-1;
					
			 }
			  robotList.add(r); 
    	}
    }
    
    public Robotum GetRobot (int index) 
    {
    	return robotList.get(index); 
    }
    
    public ArrayList<Robotum> GetRobotList () 
    {
    	return robotList; 
    }
    
    public RobotSet  GetRobotSet () 
    {
    	RobotSet rs = new RobotSet(RobotCount()); 
    	for (int i=0; i<RobotCount();i++) 
    	{
    		rs.AddRobot(GetRobot(i));
    	}
    	return rs;
    }
    
    public int RobotCount () 
    {
    	return robotList.size(); 
    }

		public RobotReader(String template, int sstep) {
			super();
			delimiter ="_";
			folderPath = "\\config\\";
			fileName = "Robots.txt"; 
			step = sstep; 
			templateName = template; 
			
			FromFileToArray();
			CreateRobotArray(); 
			
		}
		
		public RobotReader(String template) {
			this (template, 0);
			
		}
		
		

	}
