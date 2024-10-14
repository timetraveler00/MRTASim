package MRTAStreamManager;

import java.util.ArrayList;

import IndoorStructure.Wall;
import IndoorStructure.WallNode;




public class WallReader extends MRTAFileReader {
	
	
	protected ArrayList<Wall> wallList = new ArrayList<Wall>(); 
    protected void CreateWallArray () 
    {
    	for (int i=0; i<lines.size(); i++) 
    	{
    		
    		
    		Wall w = new Wall (new WallNode(Integer.parseInt(Get(i,0)), Integer.parseInt(Get(i,1))), new WallNode(Integer.parseInt(Get(i,2)), Integer.parseInt(Get(i,3)))); 
    		wallList.add (w); 
 
    	}
    }
    
    public Wall GetWall (int index) 
    {
    	return wallList.get(index); 
    }
    
    public int WallCount () 
    {
    	return wallList.size(); 
    }

		public WallReader(String template) {
			super();
			delimiter ="_";
			folderPath = "\\config\\";
			fileName = "Walls.txt"; 
			templateName = template; 
									
			FromFileToArray();
			CreateWallArray(); 
			
		}
		
		

	}
