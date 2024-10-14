
package IndoorStructure;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import Djkstra.DijkstraAlgorithm;
import Djkstra.Edge;
import Djkstra.Graph1;
import Djkstra.Vertex;
import MRTAStreamManager.ShortestMapReader;
import MRTAStreamManager.StationReader;
import MRTAStreamManager.TerrainDimensionsReader;
import MRTAStreamManager.TransitionPointReader;
import MRTAStreamManager.WallReader;
import MRTAmain.HRectangle;
import MRTAmain.Robotum;
import MRTAmain.Station;
import MRTAmain.Task;



    



public class IndoorStructure {


 /*    private static IndoorStructure instance;
         private IndoorStructure(){}
    
    //static block initialization for exception handling
    static{
        try{
            instance = new IndoorStructure();
        }catch(Exception e){
            throw new RuntimeException("Exception occured in creating IndoorStructure singleton instance");
        }
    }
    
    public static IndoorStructure getInstance(){
        return instance;
    }
     
*/
	
	  public ArrayList<Wall> walls;
	  public ArrayList<Station> stations;
	  public ArrayList<TransitionPoint> tpoints;
	  public ArrayList<Vertex> nodes;
	  public ArrayList<Edge> edges;
	  public String tmpltSelect ; 
      public int tW, tH; 
	  public int SP[][] ; 
	  public int physicalWidth; 
	  public int stationCount; 
	  public HRectangle rect;
	
      public void getCallerMethod ()  
	  {
			
			  final StackTraceElement[] ste = Thread.currentThread().getStackTrace(); 
			  
			  System.out.println("");
			  System.out.print("IndoorStructure-> "); 
			  for (int j=2;j<ste.length;j++)
				  System.out.print(ste[j].getMethodName()+"--"); 
			  System.out.println("");
			   
      }
	  
	  public void Yaz(String st) 
      {
			 
		System.out.println(st);
			 	 
	  }
	  
	  public IndoorStructure(String templateSelection, HRectangle rct) 
	  {
		   this (templateSelection); 
		   rect = rct; 
		  
	  }
	  
	  public void ClearIndoorStructure () 
	  {
		  getCallerMethod ();
		  for (int i=0; i<walls.size();i++)
		  {
			  
		  }
			  walls.clear(); 
			  stations.clear(); 
			  tpoints.clear(); 
			  edges.clear(); 
		  }
	  
	  
	public IndoorStructure(String templateSelection) 
	{
		
		getCallerMethod ();
		walls = new ArrayList<Wall>(); 
		stations = new ArrayList<Station>(); 
		tpoints = new ArrayList<TransitionPoint>();
		nodes =  new ArrayList<Vertex>();
		edges = new ArrayList<Edge>(); 
		tmpltSelect = templateSelection;
	    
        TerrainDimensionsReader tdr = new TerrainDimensionsReader(tmpltSelect);
        tW = tdr.getTERRAIN_WIDTH();  
        tH = tdr.getTERRAIN_HEIGHT(); 
        physicalWidth = tdr.getPhysicalWidth();
        //System.out.println ("Indoor Structure> tw "+tW + " th "+tH + " physical "+physicalWidth);
        
        
        if (tmpltSelect.compareTo("Empty")==0) 
        {
        
        	SetWall(new Wall(new WallNode(0,0),new WallNode(tW, 0))); 
        	SetWall(new Wall(new WallNode(tW, 0),new WallNode(tW, tH)));
        	SetWall(new Wall(new WallNode(0, tH),new WallNode(tW, tH)));
        	SetWall(new Wall(new WallNode(0,0),new WallNode(0, tH))); 
        }
        else 
        {        	
        	SetWall(new Wall(new WallNode(0,0),new WallNode(tW, 0))); 
        	SetWall(new Wall(new WallNode(tW, 0),new WallNode(tW, tH)));
        	SetWall(new Wall(new WallNode(0, tH),new WallNode(tW, tH)));
        	SetWall(new Wall(new WallNode(0,0),new WallNode(0, tH))); 
        	
        	WallReader wr = new WallReader(templateSelection); 
        	int wallCount = wr.WallCount(); 
        	//System.out.println ("Indoor Structure> wallCount "+ wallCount);
        	for (int i=0; i<wallCount ; i++) 
        	{
        		SetWall(wr.GetWall(i)); 
        	}
        }
        
        
        
        TransitionPointReader tpr = new TransitionPointReader(tmpltSelect);
        	int tpC = tpr.TransitionPointCount();
        	//System.out.println ("Indoor Structure> tp count "+ tpC);
        	for (int i=0; i<tpC ; i++) 
        	{
        		SetTP(tpr.GetTransitionPoint(i));   
        	}
        	
        	
        	// Eðer alan boþsa ve hiç TP yoksa 
        	// en az bir dummy TP eklememiz gerekiyor. 
        	if (tpC == 0) 
        	{
        		int i=0;
        		while (InterSection(tW/2+i,tH/2+i))
        		{
        			i++;
        		}
        		SetTP (new TransitionPoint(tW/2+i, tH/2+i, "TP")); 
        	}
        	// End of dummy TP addition
        	System.out.println ("Indoor Structure> station read" + stationCount);
        	
        	
        	StationReader sr = new StationReader(tmpltSelect); 
        	stationCount = sr.StationCount();
        	//System.out.println ("Indoor Structure> station count "+ stationCount);
        	for (int i=0; i<stationCount; i++) 
        	{
        		stations.add(sr.GetStation(i)); 
        	}
        	
		    // Lane haritasýnýn otomatik çýkarýlmasý için 
        	// Dosyadan okumaya gerek yok
        	GenerateNodeMap_Auto();  
        	ShortestPathsMap(); 
		    rect = new HRectangle(0, 0, tW, tH);
		    
	}
	
	public void AddTP (WallNode wn, String tpName) 
	{
		//getCallerMethod ();
		if (!InterSection(wn) && GetTP(wn.xLoc, wn.yLoc)== null && IsIn(wn))
        {
        	SetTP(new TransitionPoint(wn.xLoc, wn.yLoc, tpName));
        }
	}
	
	
	public void AddTPSet  (Wall walli, int i) 
	{
		//getCallerMethod ();
		WallNode wn = new WallNode(walli.tpStart1.xLoc, walli.tpStart1.yLoc);
	    AddTP (wn, "TP"+Integer.toString(i)+"-1") ; 
	
		wn = new WallNode(walli.tpStart2.xLoc, walli.tpStart2.yLoc); 

		 AddTP (wn, "TP"+Integer.toString(i)+"-2") ; 

        wn = new WallNode(walli.tpEnd1.xLoc, walli.tpEnd1.yLoc); 
        AddTP (wn, "TP"+Integer.toString(i)+"-3") ; 

        wn = new WallNode(walli.tpEnd2.xLoc, walli.tpEnd2.yLoc); 

        AddTP (wn, "TP"+Integer.toString(i)+"-4") ; 
        /* wn = new WallNode(walli.tpStart3.xLoc, walli.tpStart3.yLoc); 

       AddTP (wn, "TP"+Integer.toString(i)+"-5") ; 
        wn = new WallNode(walli.tpEnd3.xLoc, walli.tpEnd3.yLoc); 

        AddTP (wn, "TP"+Integer.toString(i)+"-6") ; 
        wn = new WallNode(walli.tpStart4.xLoc, walli.tpStart4.yLoc); 

        AddTP (wn, "TP"+Integer.toString(i)+"-7") ; 
        wn = new WallNode(walli.tpEnd4.xLoc, walli.tpEnd4.yLoc); 

        AddTP (wn, "TP"+Integer.toString(i)+"-8") ;
        */
		
	}
	
	
	public void BuildTransitions () 
	{
		getCallerMethod ();
		nodes = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();  
		tpoints = new ArrayList<TransitionPoint>();
		for (int i=0; i<GetWallCount(); i++) 
		{
			Wall walli = GetWall(i); 
			walli.CalculateGuards(); 
            AddTPSet (walli, i); 
		}
		
	}
	
	public void ShortestPathsMap() 
	{

		getCallerMethod ();  
		ToString();
		ShortestMapReader smr = new ShortestMapReader(tmpltSelect, GetTPCount());
		  int tpCount = smr.CreateShortestMapArray();  // fr.ReadShortestMap(tmpltSelect, GetTPCount());
		  if (tpCount != GetTPCount())
		  Yaz("IndoorStructure> ShortestPathsMap TP Count uyumsuzluðu "); 
			  
			  SP  = smr.GetShortestPathMap();
		  Yaz("ShortestPathsMap : " + tpCount); 
		  /*for (int i=0; i<tpCount; i++)
		  {  for (int j=0; j<tpCount ; j++) 
			  {
				 SP[i][j] = fr.shortestPathMatrix[i][j];  
				 //Yaz(SP[i][j]+"_"); 
				  
			  }
		      //Yaz(""); 
		  }*/
			  
		  
		  
	}
	
	public Edge GetEdge(int index)
	{
		return edges.get(index); 
		
	}
	
	public void RemoveTP(TransitionPoint tp) 
	{
		getCallerMethod ();  
		int tpIndex = TPIndex(tp.tpname); 
		tpoints.remove(tpIndex) ; 
		nodes.remove(tpIndex); 
		GenerateNodeMap_Auto();  
		
		
	}
	
	public void ToString () 
	{
		
		Iterator<Wall> it= walls.iterator();
		int counter = 0; 
		int cumLength = 0; 

        while(it.hasNext())
        {
           Wall w = (Wall) it.next();  
           cumLength += w.GetLength(); 
           counter++; 
        	
          
        }
        Yaz(" Duvar sayýsý :"+ Integer.toString(counter)) ;
        Yaz(" Uzunluk :"+ Integer.toString(cumLength)) ;
        Yaz(" TP sayýsý :"+ Integer.toString(GetTPCount())) ;
        ListTPs();
        Yaz(" Lane sayýsý :"+ Integer.toString(edges.size())) ;
	
	}
	
	public Wall GetWall(int index) 
	{
		
		return walls.get(index); 
	}
	
	public Station GetStation(int index) 
	{
		
		return stations.get(index); 
	}
	
	public TransitionPoint GetTP(int index) 
	{
		
		return tpoints.get(index); 
	}
	
	public Edge GetLane (int index) 
	{
		return edges.get(index); 
	}
	
	public void SetWall(Wall w) 
	{
		
		walls.add(w) ; 
	}
	
	public TransitionPoint GetTP(int x, int y)
	{
		for (int i=0; i<GetTPCount(); i++) 
			{
			    TransitionPoint tp = GetTP(i); 
			    if (tp.xLoc == x && tp.yLoc == y) 
			    	return tp; 
			}
		return null; 
	}
	public void SetTP(TransitionPoint tp) 
	{
		
		int i= tpoints.size() ; 
		
		
		tpoints.add(tp) ; 
		
        Vertex location = new Vertex("Node_"+i , tp.tpname,  tp, false);
	      nodes.add(location);
	}
	public int GetWallCount () 
	{
		return walls.size(); 
		
	}
	public int GetStationCount () 
	{
		return stations.size(); 
		
	}
	public int GetLaneCount() 
	{
		return edges.size(); 
	}
	
	public int GetTPCount () 
	{
		return tpoints.size(); 
		
	}
	public boolean InterSection (int x1, int y1, int x2, int y2)
	
	{
		WallNode wn1 = new WallNode(x1, y1);
		WallNode wn2 = new WallNode(x2, y2); 
				
		return InterSection (new Wall(wn1, wn2)); 
	}
	
	public boolean InterSection (int x, int y)
	
	{
		getCallerMethod();
		WallNode wn = new WallNode(x, y); 
				
		return InterSection (wn); 
	}
	public boolean InterSection (Robotum r) 
	{
		//Yaz("GetWallCount() : " + Integer.toString(GetWallCount()));
		WallNode wn = new WallNode(r.xLoc, r.yLoc); 
				
		return InterSection (wn); 
	}
	
	public boolean InterSection (Task t) 
	{
		//Yaz("GetWallCount() : " + Integer.toString(GetWallCount()));
		WallNode wn = new WallNode(t.xLoc, t.yLoc); 
				
		return InterSection (wn); 
	}
	
	public boolean InterSection (WallNode w) 
	{
		//Yaz("GetWallCount() : " + Integer.toString(GetWallCount())); 
		Wall ww = new Wall (w,w); 
		return InterSection(ww); 
	}
	
	public boolean InterSection (Wall w) 
	{
		//getCallerMethod();
		//Yaz("GetWallCount() : " + Integer.toString(GetWallCount())); 
		for (int i=0; i<GetWallCount(); i++) 
		{
			Wall wm = GetWall(i); 
			if (wm.Intersection(w)) 
			//if (wm.Surroundings(w))
			{
				return true; 
			}
		}
		
		return false; 
	}
	
	
	// fAST VER:
	public LinkedList<Vertex> ShortestofClosests(Task from, Task to) 
	{
		getCallerMethod ();
		int minDist = 100000; 
		
		LinkedList<Vertex> ret =  null; 
		long begin  = System.currentTimeMillis();
		TransitionPoint tf = new TransitionPoint(from);  
		TransitionPoint tg = new TransitionPoint(to);  

		TransitionPoint tpp = null;  
		TransitionPoint trr = null; 
		 
				
			for (int i=0; i<GetTPCount(); i++) 
			{
				TransitionPoint tp = GetTP(i); 
				
				Wall m = new Wall ( new WallNode (tf.xLoc, tf.yLoc), new WallNode (tp.xLoc, tp.yLoc)); 
				Yaz ("ShortestofClosests - tp " + i);
				if (!this.InterSection(m) ) 
				{
					for (int j=0; j<GetTPCount(); j++) 
					{
						
						
						TransitionPoint tr = GetTP(j); 	
						
						Wall n = new Wall ( new WallNode (tg.xLoc, tg.yLoc), new WallNode (tr.xLoc, tr.yLoc)); 
						if (!this.InterSection(n) ) 
						{
							 int tptf = tp.GetDistance(tf) ; 
							 int trtg = tr.GetDistance(tg); 
							 // int betw = ShortestPathLength(tp, tr);
							 int betw = ShortestPathLengthFF(tp, tr);
							 int total; 
							
							 if (betw<0) 
							 {
								 total = 100000; 
							 }
							 else 
							 {
								 total = tptf + trtg + betw; 
							 }
							 
							  //Yaz("???from : " + from.taskName + " to : "+to.taskName +" tp : "+tp.tpname + " tr : "+tr.tpname + " tf : "+tf.tpname + " tg : "+tg.tpname+ " tptf : "+tptf+" trtg: "+trtg+"between: "+betw+" total  :"+total);
							 if (total < minDist && total>=0)
							 {
								 minDist = total; 
								 tpp = tp; 
								 trr = tr; 
								// ret =  ShortestPath(tp,  tr); 
							 } 
							
						}
						
					}

				}
			}
		if (tpp!=null && trr!=null) 
		{
			// Yaz("MÝNDÝST :"+minDist);
			ret =  ShortestPath(tpp,  trr); 
			long end = System.currentTimeMillis();
		//	Yaz("IndoorStructure>ShortestofClosests() from : " + from.taskName + " xloc : " +from.xLoc + " yloc : "+from.yLoc +  "  to : " + to.taskName + " xloc : " +to.xLoc + " yloc : "+to.yLoc + " dist : "+ minDist+" time :" + (end-begin)); 
			return ret;
		}
		else 
		{
			return null;
		 } 
		
	}

	//////// ORIGINAL 
	public LinkedList<Vertex> ShortestofClosests111(Task from, Task to) 
	{
		getCallerMethod ();
		int minDist = 100000; 
		
		LinkedList<Vertex> ret =  null; 
		long begin  = System.currentTimeMillis();
		// Yaz("ShortestofClosest") ; 
		TransitionPoint tf = new TransitionPoint(from);  
		TransitionPoint tg = new TransitionPoint(to);  

		
		for (int i=0; i<GetTPCount(); i++) 
		{
			TransitionPoint tp = GetTP(i); 
			
			Wall m = new Wall ( new WallNode (tf.xLoc, tf.yLoc), new WallNode (tp.xLoc, tp.yLoc)); 

			if (!this.InterSection(m) ) 
			{
				for (int j=0; j<GetTPCount(); j++) 
				{
					
				
					TransitionPoint tr = GetTP(j); 	
					
					Wall n = new Wall ( new WallNode (tg.xLoc, tg.yLoc), new WallNode (tr.xLoc, tr.yLoc)); 
					if (!this.InterSection(n) ) 
					{
						 int tptf = tp.GetDistance(tf) ; 
						 int trtg = tr.GetDistance(tg); 
						 // int betw = ShortestPathLength(tp, tr);
						 int betw = ShortestPathLengthFF(tp, tr);
						 int total; 
						 if (betw<0) 
						 {
							 total = 100000; 
						 }
						 else 
						 {
							 total = tptf + trtg + betw; 
						 }
						 
						 // Yaz("???from : " + from.taskName + " to : "+to.taskName +" tp : "+tp.name + " tr : "+tr.name + " tf : "+tf.name + " tg : "+tg.name+ " tptf : "+tptf+" trtg: "+trtg+"between: "+betw+" total  :"+total);
						 if (total < minDist && total>=0)
						 {
							 minDist = total; 
							 ret =  ShortestPath(tp,  tr); 
						 } 
						
					}
					
				}

			}
		}
		long end = System.currentTimeMillis();
		Yaz("IndoorStructure>ShortestofClosests() from : " + from.taskName + " xloc : " +from.xLoc + " yloc : "+from.yLoc +  "  to : " + to.taskName + " xloc : " +to.xLoc + " yloc : "+to.yLoc + " dist : "+ minDist+" time :" + (end-begin));
		return ret; 
		
	}
	
	
	//////// ORIGINAL 
	public int ShortestofClosestsLength111(Task from, Task to) 
	{
		//getCallerMethod ();
		int minDist = 100000; 
		
		long begin  = System.currentTimeMillis();
		TransitionPoint tf = new TransitionPoint(from);  
		TransitionPoint tg = new TransitionPoint(to);  

		for (int i=0; i<GetTPCount(); i++) 
		{
			TransitionPoint tp = GetTP(i); 
			
			Wall m = new Wall ( new WallNode (tf.xLoc, tf.yLoc), new WallNode (tp.xLoc, tp.yLoc)); 
			
			if (!this.InterSection(m) ) 
			{
				for (int j=0; j<GetTPCount(); j++) 
				{
					TransitionPoint tr = GetTP(j); 	
					
					Wall n = new Wall ( new WallNode (tg.xLoc, tg.yLoc), new WallNode (tr.xLoc, tr.yLoc)); 
					if (!this.InterSection(n) ) 
					{
						 int tptf = tp.GetDistance(tf) ; 
						 int trtg = tr.GetDistance(tg); 
						  int betw = ShortestPathLengthFF(tp, tr); 
						 //int betw = ShortestPathLength(tp, tr); 
						 int total; 
						 if (betw< 0) 
						 {
							 total = 100000; 
						 }
						 else 
						 {
							 total = tptf + trtg + betw; 
						 }
						 // Yaz(tp.name+"-"+tf.name+"..."+tr.name+"-"+tg.name+" total " + total );
						 if (total < minDist && total>=0)
						 {
							 minDist = total; 
							// ret =  ShortestPath(tp,  tr); 
						 } 
						
					}
				}

			}
		}
		// Yaz("MÝNDÝST :"+minDist);
		long end = System.currentTimeMillis();
		//Yaz("IndoorStructure>ShortestofClosestsLength() from : " + from.taskName + " xloc : " +from.xLoc + " yloc : "+from.yLoc +  "  to : " + to.taskName + " xloc : " +to.xLoc + " yloc : "+to.yLoc + " dist : "+ minDist+" time :" + (end-begin)); 
		return minDist; 
		
	}
	public int ShortestofClosestsLength(WallNode fromw, WallNode tow) 
	{
		
		return ShortestofClosestsLength(new Task(fromw.xLoc,fromw.yLoc,"from"), new Task(tow.xLoc, tow.yLoc,"to"));
	}
	
	public int ShortestofClosestsLength(Task from, Task to) 
	{
		// getCallerMethod ();
		int minDist = 100000; 
		//Yaz("IndoorStructure>ShortestofClosestsLength() from : " + from.taskName + " xloc : " +from.xLoc + " yloc : "+from.yLoc +  "  to : " + to.taskName + " xloc : " +to.xLoc + " yloc : "+to.yLoc + " dist : "+ minDist); 
		long begin  = System.currentTimeMillis();
		TransitionPoint tf = new TransitionPoint(from);  
		TransitionPoint tg = new TransitionPoint(to);  
		for (int i=0; i<GetTPCount(); i++) 
		{
			TransitionPoint tp = GetTP(i); 
			
			Wall m = new Wall ( new WallNode (tf.xLoc, tf.yLoc), new WallNode (tp.xLoc, tp.yLoc)); 
			
			if (!this.InterSection(m) ) 
			{
				for (int j=0; j<GetTPCount(); j++) 
				{
					TransitionPoint tr = GetTP(j); 	
					
					Wall n = new Wall ( new WallNode (tg.xLoc, tg.yLoc), new WallNode (tr.xLoc, tr.yLoc)); 
					if (!this.InterSection(n) ) 
					{
						 int tptf = tp.GetDistance(tf) ; 
						 int trtg = tr.GetDistance(tg); 
						  int betw = ShortestPathLengthFF(tp, tr); 
						 //int betw = ShortestPathLength(tp, tr); 
					//	  Yaz("tptf : " + tptf + " trtg : " +trtg+ " betw : "+betw +  "  i : " + i + " j : " +j ); 
						 int total; 
						 if (betw< 0) 
						 {
							 total = 100000; 
						 }
						 else 
						 {
							 total = tptf + trtg + betw; 
						 }
						 // Yaz(tp.name+"-"+tf.name+"..."+tr.name+"-"+tg.name+" total " + total );
						 if (total < minDist && total>=0)
						 {
							 minDist = total; 
						 } 
		
					}
				}

			}
		}
		/*if (minDist<0 && minDist == 100000)
			minDist = 99999; 
*/
		// Yaz("MÝNDÝST :"+minDist);
		long end = System.currentTimeMillis();
	//	Yaz("IndoorStructure>ShortestofClosestsLength() from : " + from.taskName + " xloc : " +from.xLoc + " yloc : "+from.yLoc +  "  to : " + to.taskName + " xloc : " +to.xLoc + " yloc : "+to.yLoc + " dist : "+ minDist+" time :" + (end-begin)); 
		return minDist;
	}
	
/*
	public boolean IsInClosedStructure (int x, int y) 
	{
	     int sl = ShortestofClosestsLength(new Task(x, y, "t1"), new Task (refPoint.xLoc, refPoint.yLoc, "t2"));
	     if (sl>0 && sl<10000) 
	    	 return true; 
	     return false; 
	}
	*/
	
	public TransitionPoint Closest(TransitionPoint tm) 
	{
		getCallerMethod ();
		int minDist = 100000; 
		TransitionPoint closest = null; 
		long begin = System.currentTimeMillis();
		int tpco = GetTPCount(); 
		
		if (tpco%10000==0)
		      Yaz("TPCOUNT" + Integer.toString(GetTPCount())) ; 
		for (int i=0; i<GetTPCount(); i++) 
		{
			TransitionPoint tp = GetTP(i); 
			
			Wall m = new Wall ( new WallNode (tm.xLoc, tm.yLoc), new WallNode (tp.xLoc, tp.yLoc)); 
	
			if (!this.InterSection(m) ) 
			{
				int dist = tp.GetDistance(tm); 
				if (dist < minDist&&dist>0) {
				//Yaz("dist < minDist && !this.InterSection(m) þartý saðlandý. dist " + dist + " mindist " +minDist + " "+tp.xLoc + " "+ tp.yLoc) ; 
				minDist = dist;  
				closest = tp; 
				}
			}
		}
		
		long end = System.currentTimeMillis();
		//Yaz("IndoorStructure>Closest() tm : " + tm.tpname + " xloc : " +tm.xLoc + " yloc : "+tm.yLoc + " closest : " + closest.tpname + " xloc : " +closest.xLoc + " yloc : "+closest.yLoc+ " time :" + (end-begin)); 
		return closest; 
		
	}
	
	public TransitionPoint Closest(TransitionPoint tm, TransitionPoint prev) 
	{
	//	getCallerMethod ();
		if (prev== null) 
		{
		
		int minDist = 100000; 
		TransitionPoint closest = null; 
		long begin = System.currentTimeMillis();
		int tpco = GetTPCount(); 
		
		//if (tpco%10000==0)
		      Yaz("TPCOUNT" + Integer.toString(GetTPCount())) ; 
		for (int i=0; i<GetTPCount(); i++) 
		{
			TransitionPoint tp = GetTP(i); 
			
			Wall m = new Wall ( new WallNode (tm.xLoc, tm.yLoc), new WallNode (tp.xLoc, tp.yLoc)); 
	
			if (!this.InterSection(m) ) 
			{
				int dist = tp.GetDistance(tm); 
				if (dist < minDist&&dist>0) {
				Yaz("dist < minDist && !this.InterSection(m) þartý saðlandý. dist " + dist + " mindist " +minDist + " "+tp.xLoc + " "+ tp.yLoc) ; 
				minDist = dist;  
				closest = tp; 
				}
			}
		}
		
		long end = System.currentTimeMillis();
		//Yaz("IndoorStructure>Closest() tm : " + tm.tpname + " xloc : " +tm.xLoc + " yloc : "+tm.yLoc + " closest : " + closest.tpname + " xloc : " +closest.xLoc + " yloc : "+closest.yLoc+ " time :" + (end-begin));
		
		return closest;
		} else
			return prev;
		
	}
	
	public void ListTPs() 
	{
		for (int i = 0; i < GetTPCount(); i++) {
		      
	          //Vertex location = new Vertex("Node_" + i, GetTP(i).name,  GetTP(i), false);
		      Yaz("DJKSTRA  :   i = "+Integer.toString(i) + " TPname = " + GetTP(i).tpname + " TPloc [" + Integer.toString(GetTP(i).xLoc)+";"+Integer.toString(GetTP(i).yLoc)+"]"); 
		    }
	}
	
	  public void addLane(String laneId, int sourceLocNo, int destLocNo,
		      int duration) {
		    Edge lane = new Edge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
		    edges.add(lane);
		  }
	
	  
	  public void LaneStructure  () 
		{
		   
		
		  getCallerMethod (); 
		  edges = new ArrayList<Edge>(); 
		  for (int i=0; i<GetTPCount(); i++)
		  { 
			  TransitionPoint tpi = GetTP(i); 
		  
			WallNode wi = new WallNode(tpi.xLoc, tpi.yLoc); 	
			for (int j=0; j<i; j++)
			{
				TransitionPoint tpj = GetTP(j); 
				WallNode wj = new WallNode(tpj.xLoc, tpj.yLoc); 	 
				Wall ij = new Wall (wi,wj); 
				if (i!=j && !InterSection(ij))
				{
					int dist = (int) CalcDistance(tpi.xLoc, tpi.yLoc, tpj.xLoc, tpj.yLoc); 
					addLane("Lane_"+Integer.toString(i)+"_"+Integer.toString(j), i, j, dist); 
					addLane("Lane_"+Integer.toString(j)+"_"+Integer.toString(i), j, i, dist); 
				}
			}	

		  }	
 
		}
	  
	  public void GenerateNodeMap_Auto () 
		{
		   
		  getCallerMethod ();
		  BuildTransitions(); 
		  
		   LaneStructure(); 
   
		}
			public double CalcDistance(int xs, int ys, int xd, int yd)
			{
				
				int x_fark = xs-xd ; 
				int y_fark =  ys-yd ;
				
				return Math.sqrt( x_fark * x_fark  + y_fark*y_fark ) ;	


			}
	
	public int TPIndex (String tpName) 
	{
		for (int i=0; i<GetTPCount(); i++) 
		{
			if (GetTP(i).tpname.compareTo(tpName) == 0)
				return i; 
		}
		return -1; 
		
	}
	
	public LinkedList<Vertex> ShortestPath (TransitionPoint from, TransitionPoint to)
	{
		getCallerMethod ();
		long begin = System.currentTimeMillis();
		    // toString(); 
		LinkedList<Vertex> path = null; 
		    if (from.tpname.compareTo(to.tpname)==0)
		    {
		    	 path = new LinkedList<Vertex>(); 
		    	path.add(new Vertex(from.tpname, from.tpname,  from, false));
		    
		    }
		    else 
		    {
		
		     Graph1 Graph1 = new Graph1(nodes, edges);
		    DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(Graph1);
		    //Yaz(from.name + "-"+ to.name); 
		    dijkstra.execute(nodes.get(TPIndex(from.tpname)));
		    path = dijkstra.getPath(nodes.get(TPIndex(to.tpname)));
		   System.out.println("shortest path : from : "+from.tpname + " to : "+to.tpname+" length : " + dijkstra.cumu);
		    
		    for (Vertex vertex : path) {
		        System.out.print(vertex+"  ");
		        
		      } 
		    Yaz("  ");
		   
		    }
		   		long end = System.currentTimeMillis();
		    Yaz("IndoorStructure>ShortestPath() from : " + from.tpname + " xloc : " +from.xLoc + " yloc : "+from.yLoc +" to : " + to.tpname + " xloc : " +to.xLoc + " yloc : "+to.yLoc + "  time :" + (end-begin)); 
		    
		    return path; 
		
	}
	
	public int ShortestPathLengthFF (TransitionPoint from, TransitionPoint to)
	{
		//long begin = System.currentTimeMillis();
		
		//getCallerMethod ();
	    int fr = TPIndex(from.tpname) ; 
	    int t = TPIndex(to.tpname) ; 
	    
	    
		//long end = System.currentTimeMillis();

//		Yaz("IndoorStructure>ShortestPathLengthFF() from : " + from.name + " xloc : " +from.xLoc + " yloc : "+from.yLoc +  "  to : " + to.name + " xloc : " +to.xLoc + " yloc : "+to.yLoc + " dist : "+ SP[fr][t]);
		return SP[fr][t];
		
	} 
	
	public int ShortestPathLength (TransitionPoint from, TransitionPoint to)
	{
		 
	    getCallerMethod();
		if (from.tpname.compareTo(to.tpname)==0)
	    {
	        return 0; 
	    
	    }
	    else 
	    {
	
		   
		Graph1 Graph1 = new Graph1(nodes, edges);
		    DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(Graph1);
		    dijkstra.execute(nodes.get(TPIndex(from.tpname)));
		    dijkstra.getPath(nodes.get(TPIndex(to.tpname)));

		    return dijkstra.cumu;  
	    }
	    
		
	} 
	public boolean IsIn (int x, int y) 
	{
		boolean retVal = false; 
		if (x>0 & y>0 & x<tW & y<tH) 
			retVal =  true; 
		return retVal;
	}
	public boolean IsIn (TransitionPoint tp) 
	{
	     return IsIn (tp.xLoc, tp.yLoc); 	
	}
	public boolean IsIn(WallNode wn) 
	{
		return IsIn(wn.xLoc, wn.yLoc); 
	}
	
		

}
