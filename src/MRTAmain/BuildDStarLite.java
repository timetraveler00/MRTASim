package MRTAmain;


import java.util.List;

import DStarLiteJava.DStarLite;
import DStarLiteJava.State;
import IndoorStructure.IndoorStructure;
import IndoorStructure.Wall;
import IndoorStructure.WallNode;


public class BuildDStarLite {

	

	
	 IndoorStructure ins ;//= IndoorStructure.getInstance() ;  
	 List<State> path ;
	 DStarLite pf ;
	 int mf = 1; 
	 IOLogger iol = new IOLogger("BuildDStar", 0);
	 
		public void Yaz(String st) 
		{
			 
			iol.Yaz(st);
			 	 
		}
	/*	
		public boolean CrossTypeIntersection (IndoorStructure is, int i, int j) 
		{
			boolean retVal = false; 
			Wall w = new Wall (i-1, j-1, i+1, j+1); 
			Wall w2 = new Wall (i+1, j-1, i-1, j+1); 
			if (is.InterSection(w) || is.InterSection(w2)) 
				retVal =  true;
			return retVal;  
					 
		}*/
		public boolean CrossTypeIntersection5 (int i, int j) 
		{
			boolean retVal = false; 
			Wall w = new Wall (i-5, j-5, i+5, j+5); 
			Wall w2 = new Wall (i+5, j-5, i-5, j+5); 
			if (ins.InterSection(w) || ins.InterSection(w2)) 
				retVal =  true;
			return retVal;  
					 
		}
	 public BuildDStarLite ( IndoorStructure is, HRectangle rect) 
		{
	         
		     ins = is; //IndoorStructure.getInstance(); 
		 
			Yaz("***** DSTARLITE constructor ********");
			ins.walls.add(new Wall(new WallNode(rect.x1, rect.y1), new WallNode (rect.x2, rect.y1)));
			ins.walls.add(new Wall(new WallNode(rect.x2, rect.y1), new WallNode (rect.x2, rect.y2)));
			ins.walls.add(new Wall(new WallNode(rect.x2, rect.y2), new WallNode (rect.x1, rect.y2)));
			ins.walls.add(new Wall(new WallNode(rect.x1, rect.y2), new WallNode (rect.x1, rect.y1)));
			
			pf = new DStarLite();
			//pf.init(10,10, 20, 20);
			for (int i=1; i<ins.tW-1; i++) 
			for (int j=1; j<ins.tH-1; j++) 
			{
				
				
				if (CrossTypeIntersection5(i, j)) 
				{

							pf.updateCell(i/mf, j/mf, -1); 
							
				}
				
			}
			Yaz("***** DSTARLITE created  ********");
		}
	public BuildDStarLite (IndoorStructure is) 
	{
	    this (is, new HRectangle(1, 1, is.tW-1, is.tH-1));  
	}
	
	
	public double DStarLiteDistance (int startx, int starty, int targetx, int targety, String from, String to) 
	{
		double cumulativeDist = -1; 
		pf.updateStart(startx/mf, starty/mf); 
		pf.updateGoal(targetx/mf, targety/mf); 
		
		//Yaz("Start node: ("+startx+","+starty+")");
		//Yaz("End node: ("+targetx+","+targety+")");

		//Time the replanning
		long begin = System.currentTimeMillis();
		if (pf.replan()==true) 
		{
		
		long end = System.currentTimeMillis();

		Yaz("Time: " + (end-begin) + "ms");

		path = pf.getPath();
		int oldx = -1; 
		int oldy = -1; 
		
		for (State i : path)
		{
			if (oldx <0 && oldy<0) 
			{
				oldx = i.x*mf; 
				oldy = i.y*mf; 
			}
			
			//Yaz("x: " + i.x * mf  + " y: " + i.y * mf);
			double dist = 	CalcDistanceD (oldx , oldy, i.x*mf, i.y*mf); 		
			cumulativeDist += dist; 
			//Yaz("dist : " + dist + " cumulative : " +cumulativeDist);
					
			oldx = i.x * mf ; 
			oldy = i.y * mf ; 
			//if (cumulativeDist>5000) 
			//	break; 
			
		}
		cumulativeDist+=1; 
		
		Yaz(":");
		Yaz("............................................................................");
		
		Yaz("DStar mesafesi hesaplandý-   Time: " + (end-begin) + "ms");
		Yaz("Start node: "+from +" ("+startx+","+starty+")");
		Yaz("End node: "+to+" ("+targetx+","+targety+")");
		Yaz("DStar Mesafesi : ("+cumulativeDist+")");
		Yaz("............................................................................");
		Yaz(":");
		
		}else 
		{
			cumulativeDist = 99999; 
			long end = System.currentTimeMillis();
			Yaz("?");
			Yaz("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			Yaz("DStar mesafesi hesaplanamadý-   Time: " + (end-begin) + "ms");
			Yaz("Start node: "+from +" ("+startx+","+starty+")");
			Yaz("End node: "+to+" ("+targetx+","+targety+")");
			Yaz("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			Yaz("?");
		}
		
		return cumulativeDist; 

	}
	
	
	public DStarPath DStarLitePath (int startx, int starty, int targetx, int targety) 
	{
		double cumulativeDist = -1; 
		pf.updateStart(startx/mf, starty/mf); 
		pf.updateGoal(targetx/mf, targety/mf); 
		
		Yaz("Start node: ("+startx+","+starty+")");
		Yaz("End node: ("+targetx+","+targety+")");

		//Time the replanning
		long begin = System.currentTimeMillis();
		path = null; 
		if (pf.replan()==true) 
		{
		
		long end = System.currentTimeMillis();

		Yaz("Time: " + (end-begin) + "ms");

		path = pf.getPath();
		int oldx = -1; 
		int oldy = -1; 
		
		for (State i : path)
		{
			if (oldx <0 && oldy<0) 
			{
				oldx = i.x*mf; 
				oldy = i.y*mf; 
			}
			
			//Yaz("x: " + i.x * mf  + " y: " + i.y * mf);
			double dist = 	CalcDistanceD (oldx , oldy, i.x*mf, i.y*mf); 		
			cumulativeDist += dist; 
			//Yaz("dist : " + dist + " cumulative : " +cumulativeDist);
					
			oldx = i.x * mf ; 
			oldy = i.y * mf ; 
			
		}
		cumulativeDist+=1; 
		
		Yaz(":");
		Yaz("............................................................................");
		Yaz("DStar mesafesi hesaplandý-   Time: " + (end-begin) + "ms");
		Yaz("Start node: ("+startx+","+starty+")");
		Yaz("End node: ("+targetx+","+targety+")");
		Yaz("DStar Mesafesi : ("+cumulativeDist+")");
		Yaz("............................................................................");
		Yaz(":");
		
		}else 
		{
			cumulativeDist = 99999; 
			long end = System.currentTimeMillis();
			Yaz("?");
			Yaz("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			Yaz("DStar mesafesi hesaplanamadý-   Time: " + (end-begin) + "ms");
			Yaz("Start node: ("+startx+","+starty+")");
			Yaz("End node: ("+targetx+","+targety+")");
			Yaz("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			Yaz("?");
		}
		if (path!=null) 
		{
	     DStarPath dPath = new DStarPath();
	     dPath.dsPath = path; 
	     dPath.pathLength = cumulativeDist; 
	     return dPath; 
		}
		
		return null; 

	}
	
	private double CalcDistanceD(int xs, int ys, int xd, int yd)
	{
		
		int x_fark = xs-xd ; 
		int y_fark = ys-yd ;
		
		return Math.sqrt( x_fark * x_fark  + y_fark*y_fark ) ;	


	}
}

