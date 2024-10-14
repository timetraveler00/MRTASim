package MRTAmain;
import java.awt.Color;

import IndoorStructure.IndoorStructure;
import MRTAStreamManager.StationsAllReader;
import MRTAStreamManager.TaskReader;
import MRTAStreamManager.TerrainDimensionsReader;

public class HeatMap {
	
	public double heatMatrix[][] ;  
    public int tasksforEachPeak = 10; 
    public int clusterCount ;
    public int clusters [][]; 
    public TaskCluster taskClusters []; 
	public int tWidth ; 
	public int tHeight; 
	public String templateSelection; 
	public ColorSet heatColors [] ; 
	int colorCount = 0; 
	public int heatRadius = 15; //15; 
	public int allTasks; 
	public int allStations; 
	public int stationCount; 
	public Station peakStations [] = new Station [100]; 
	public int idealStationDistance; //100; 
	public int idealPeakDistance ;
	public int cNumbers = 0; 
    double maxxx ;
    double minnn ; 
    
    int xst, xen, yst, yen; 
    public IndoorStructure is ; // = IndoorStructure.getInstance(); 
    IOLogger iol = new IOLogger("HeatMap", 0);
    int nearClusters [] ;
    int nClusters =0;
    private ColorSet [] cset = new ColorSet[100]; 
    private int colorSetCount = 0; 
    TaskReader tr ;
    StationsAllReader sar ; 
    
    
	public void Yaz(String st) 
	{
		 
		System.out.println(st);
		 	 
	}
	
	public StationSet GetStationSet() 
	{
	StationSet stset = new StationSet(stationCount); 
		for (int i=0; i< stationCount; i++ )
			stset.stations[i] = peakStations[i]; 
		
		return stset; 

	}

	public HeatMap (String ts, int radius, int tasksPerHeat, int stationProximity) 
	{

		Yaz("Heatmap-HeatMap()> radius: " + radius + " tasksperheat: "+ tasksPerHeat + " station proximity: "+stationProximity);
		TerrainDimensionsReader tdr = new TerrainDimensionsReader(ts);
		tWidth = tdr.getTERRAIN_WIDTH(); 
		tHeight = tdr.getTERRAIN_HEIGHT(); 
		heatMatrix = new double [tWidth][tHeight]; 
		clusters = new int [tWidth][tHeight];
		heatColors = new ColorSet [1000000];
		taskClusters = new TaskCluster[10000000] ;
		templateSelection = ts; 
		heatRadius = radius; 
		idealPeakDistance = 50; 
		idealStationDistance = stationProximity; 
		tasksforEachPeak = tasksPerHeat; 
		
		tr = new TaskReader(templateSelection) ; 
		sar = new StationsAllReader(templateSelection); 
		allTasks = tr.TaskCount(); //fr.ReadTasks(templateSelection);  
		allStations = sar.StationCount(); //fr.ReadAllStations(templateSelection);
		ResetClusterMatrix();
		is = new IndoorStructure(ts);
		
				
		Yaz("Heatmap-HeatMap()> allStations " + allStations + " allTasks : "+ allTasks);
		
		

	}
	
	public void ResetClusterMatrix () 
	{
		for (int i=0; i<tWidth; i++) 
		{
			for (int j=0; j<tHeight; j++) 
			{
				clusters[i][j] = -1; 
			}
		}
		
	}
	
	public void ClusterRange () 
	{
		int minx = 1000; 
		int miny = 1000; 
		
		int maxx = 0; 
		int maxy = 0; 
		
		for (int i=0; i<tWidth; i++) 
		{
			for (int j=0; j<tHeight; j++) 
			{
				if (clusters[i][j] > 0 ) 
				{
					 if (i<minx) 
						 minx = i; 
					 if (j<miny)
						 miny = j; 
					 if (i>maxx) 
						 maxx = i; 
					 if (j>maxy) 
						 maxy = j; 
						 
				}
			}
		}
		
		xst = minx ; 
		xen = maxx; 
		yst = miny; 
		yen = maxy; 
	}
	
	public boolean IsNearCluster (int nearClusters[], int nc, int id)  
	{
		for (int i=0; i<nc; i++ ) 
		{
			if (nearClusters[i] == id) 
			{
				return true; 
			}
			
		}
		return false; 
	}
	
	public void MatchTasksAndClusters()
	{
		
        for (int i=0; i<allTasks; i++) 
        {
        	Task t = tr.GetTask (i); 
        	int cluster = clusters[t.xLoc][t.yLoc];
        	if (cluster>-1) 
        	{
        	t.cluster = cluster; 
        	taskClusters[TaskClusterOrder(cluster)].AddTask(t) ; 
        	Yaz("Heatmap-MatchTasksAndClusters()> Cluster ID " + cluster + " Cluster Order : " + TaskClusterOrder(cluster) +" Task : " + t.taskName);
        	} 
        }
     
		
		
	}
	
	public int MaxTasksInACluster() 
	{
		int maxx = 0; 
		for (int i=0; i<clusterCount; i++) 
		{
			TaskCluster tc = taskClusters[i]; 
			if (tc.taskCount > maxx) 
			{
				maxx = tc.taskCount; 
			}
			
		}
		
		return maxx; 
		
	}
	
	class TPInfo {
		
		public int mini = -1; 
		public int dist = 0; 
	}
	
	public TPInfo FindClosestTP(ColorSet cl, int thr) 
	{
		int minn = 10000; 
		int mini = -1; 
		for (int i=0; i<is.GetTPCount(); i++) 
		{
			int dist = (int) CalcDistance(is.GetTP(i).xLoc, is.GetTP(i).yLoc, cl.xx,  cl.yy);
			if (dist < minn && dist > thr) 
			{
				minn = dist;
				mini = i; 
			}
		}
		 TPInfo tpi = new TPInfo(); 
		 tpi.mini = mini; 
		 tpi.dist = minn; 
		return tpi; 
	}
	/*
	public int ClosestTP (ColorSet cl) 
	{
		
		fr.ReadReferencePoint(templateSelection); 
		TransitionPoint tpr = is.Closest(new TransitionPoint(fr.rp.xLoc, fr.rp.yLoc,"rp")); 
		
		TPInfo tpi = FindClosestTP(cl, 0) ; 
		while (tpi.mini >-1 && is.ShortestPath(is.GetTP(tpi.mini), tpr)==null) 
		{
		//System.out.println(tpi.mini +" " +tpi.dist);
			tpi = FindClosestTP(cl, tpi.dist) ; 
		}
		
		return tpi.mini; 
		
	}*/
	
	public Station ClosestStation (ColorSet cl, int order) 
	{
		int minn = 1000; 
		Station closest = null; 
		//TransitionPoint tp = is.ClosestInReferenceArea(new TransitionPoint(cl.xx, cl.yy,"TP"));
		
		for (int i=order; i<allStations; i++) 
			
		{
		
			Station st = sar.GetStation(i); //fr.stations[i];   
			//TransitionPoint tp = is.GetTP(ClosestTP(cl)); 
			// int dist = (int) CalcTPDistance(st.xLoc, st.yLoc, tp.xLoc,  tp.yLoc);
			int dist = (int) CalcDistance(st.xLoc, st.yLoc, cl.xx,  cl.yy); 
			if (dist < minn && dist>0 && !TooCloseToOtherStations(st.xLoc, st.yLoc)) 
			{
				closest = st; 
				minn = dist;
				Yaz("ClosestStation()  station " + st.taskName + " minn "+dist + " cl "+ cl.xx + " - "+cl.yy);
			}
	
		} 
		
		if (closest!=null && !StationExists(closest))
		{	
			Yaz("Heatmap-ClosestStation()> Closest Found " + closest.xLoc +"-"+closest.yLoc);
			return closest ; 
		}
		else 
			return null; 
	
	}
	
	public Station ClosestTPStation (ColorSet cl, int order) 
	{
		int minn = 1000; 
		Station closest = null; 
		//TransitionPoint tp = is.ClosestInReferenceArea(new TransitionPoint(cl.xx, cl.yy,"TP"));
		
		for (int i=order; i<allStations; i++) 
			
		{
		//	System.out.println("station " + i);
			Station st = sar.GetStation(i); // fr.stations[i];   
			//TransitionPoint tp = is.GetTP(ClosestTP(cl)); 
			// int dist = (int) CalcTPDistance(st.xLoc, st.yLoc, tp.xLoc,  tp.yLoc);
			int dist = (int) CalcTPDistance(st.xLoc, st.yLoc, cl.xx,  cl.yy); 
			if (dist < minn && dist>0 && !TooCloseToOtherStations(st.xLoc, st.yLoc)) 
			{
				closest = st; 
				minn = dist; 
				Yaz("ClosestTPStation station " + st.taskName + " minn "+dist + " cl : "+ cl.xx + " - "+cl.yy + " ST : " + st.xLoc + " - " + st.yLoc);
			}
	
		} 
		
		if (closest!=null && !StationExists(closest))
		{	
			Yaz("Heatmap-ClosestTPStation()> Closest Found " + closest.xLoc +"-"+closest.yLoc);
			return closest ; 
		}
		else 
			return null; 
		
		
	}
	
	public boolean StationExists (Station st) 
	{
		

		for (int i=0; i<stationCount; i++) 
		{
			if (peakStations[i].xLoc == st.xLoc && peakStations[i].yLoc== st.yLoc)
			{
				Yaz("Heatmap-StationExists()> Station in the list !!!!!!  order : " + i + "---"+ st.xLoc +"-"+st.yLoc);
				return true; 
			}
		}
	 
		Yaz("Heatmap-StationExists()> Station for the first time  " + st.xLoc +"-"+st.yLoc);
		
		return false; 
		
	}
	
	public void ClusterCenters_orig() 
	{
		
			drawHeatMap(); 
			ReviseClusters(); 
			MatchTasksAndClusters();
			ClusterBounds(); 
		for (int i=0; i<clusterCount; i++) 
		{
			double smallerThan = 100000.0; 
			
			
			if (taskClusters[i].taskCount>0) 
			{
			for (int k=0; k<taskClusters[i].taskCount/tasksforEachPeak+1; k++)
			{
				ColorSet cl = MaxInCluster(taskClusters[i], smallerThan);
				
				
				if(cl!=null)
				{
					Yaz("Heatmap-ClusterCenters()> k : " +  k  +  " smallerThan :" + smallerThan + " x : "+cl.xx + " y : "+ cl.yy + " maxx : "  + cl.maxRoboTime);
					Station st;
				
					st = ClosestTPStation(cl, 0);
					if (st==null) 
						   st= ClosestStation(cl, 0);
					
					if (st!=null)
					{
						st.rect = taskClusters[i].rect; 
						Yaz("Heatmap> taskClusters[i].rect  i :  " +  i +"_ index : "+ taskClusters[i].index +"    " +taskClusters[i].rect.x1 + "-" +  taskClusters[i].rect.y1);  
								 
						smallerThan = cl.maxRoboTime; 
						peakStations [stationCount++] = st; 
						taskClusters[i].peakPoints[taskClusters[i].peakCount++] = cl;
						taskClusters[i].peakStations[taskClusters[i].stationCount++] = st; 
					}	
					
					} 
				}
			
			}
			
				
	
		}
		
		
	}
	
	public void ClusterCenters () 
	{
		
			drawHeatMap(); 
			ReviseClusters(); 
			MatchTasksAndClusters();
			ClusterBounds(); 
		for (int i=0; i<clusterCount; i++) 
		{
			double smallerThan = 100000.0; 
			if (taskClusters[i].taskCount>0) 
			{
			for (int k=0; k<taskClusters[i].taskCount/tasksforEachPeak+1; k++)
			{
				ColorSet cl = MaxInCluster(taskClusters[i], smallerThan);
				if(cl!=null)
				{
					Yaz("Heatmap-ClusterCenters()> k : " +  k  +  " smallerThan :" + smallerThan + " x : "+cl.xx + " y : "+ cl.yy + " maxx : "  + cl.maxRoboTime);
				    cl.globalId = colorSetCount; 
					cset [colorSetCount++] = cl; 
					taskClusters[i].peakPoints[taskClusters[i].peakCount++] = cl; 
				} // cl!null
			
			} // for k
			
			} // if taskcount
		}	// for clustercount
		
		double [][] fArray = PrepareArray(); 
		GreedyAssignment ga = new GreedyAssignment(); 
		int assignments[][] = new int[allStations][colorSetCount]; 
		//assignments = ga.GreedyAssignment_yFirst(fArray, 1, allStations, colorSetCount); 
		assignments = ga.HungarianAssignment(fArray, 1, allStations, colorSetCount); 
		
		
		for (int i=0; i<clusterCount; i++) 
		{
			TaskCluster tci = taskClusters[i];
		     for (int j=0; j<tci.peakCount; j++) 
		     {
		    	 Yaz (" i : " + i + " j "+ j  + " globalid " + tci.peakPoints[j].globalId) ; 
		    	 Yaz (" i : " + i + " j "+ j  + " globalid x " + tci.peakPoints[j].xx + " y " + tci.peakPoints[j].yy ) ; 
		    	 for (int k=0; k<allStations; k++) 
		    	  
		    		 { 
		    		   
		    		      if (assignments [k][tci.peakPoints[j].globalId] == 1 )
		    		     {
		    		    	 Station st = sar.GetStation(k); // fr.stations [k]; 
		    		    	 st.rect = tci.rect; 
		    		    	 taskClusters[i].peakStations[tci.stationCount++] = st; 
		    		    	 peakStations [stationCount++] = st; 
		    		     }
		    		 }
		    		 
		    	
		    		 
		     }	
		}

	/*	
   	 for (int k=0; k<allStations; k++) 
   	  
	 { 
   		 for (int l = 0; l<colorSetCount; l++) 
   		 {
   			 System.out.print( assignments [k][l] + "   "); 
   		 }
   		 System.out.println( "  "); 
	 }
	   */
	
		
		
		
	}
	public double MaxInMatrix(double fArray[][], int xCount, int yCount) {
		double maxP = 0;
		for (int i = 0; i < xCount; i++) {

			for (int j = 0; j < yCount; j++) {
				if (maxP < fArray[i][j])
					maxP = fArray[i][j];
			}
		}

		return maxP;
	}

	public double[][] PrepareArray() {

		double [][] finalArray = new double[allStations][colorSetCount]; 
		
		for (int i = 0; i < allStations; i++) {
			for (int j = 0; j < colorSetCount; j++) 
			{
				Station st = sar.GetStation(i); 
				double dist = CalcTPDistance(st.xLoc, st.yLoc, cset[j].xx, cset[j].yy); 
				  if (dist<0 && dist >3000) 
			    dist =  CalcDistance(st.xLoc, st.yLoc, cset[j].xx, cset[j].yy);//CalcTPDistance(fr.stations[i].xLoc, fr.stations[i].yLoc, cset[j].xx, cset[j].yy); 
		       
						
	            if (dist>0 && dist <3000) 
	            	finalArray[i][j] = dist; 
	            else 
	            	finalArray[i][j] = 5000; 
			}  
		}
		double propMax = MaxInMatrix(finalArray, allStations, colorSetCount) + 5;
		for (int i = 0; i < allStations; i++) {
			for (int j = 0; j < colorSetCount; j++) 
			{
				finalArray[i][j] = propMax - finalArray[i][j]; 
	//			System.out.print (finalArray[i][j] + "     ") ; 
	
			}  
	//		Yaz ( "     ") ; 
		}
		

		
	
		return finalArray;
	}
	
	public void ClusterBounds() 
	{
		for (int i=0; i<tWidth; i++) 
		{
			for (int j=0; j<tHeight; j++) 
			{
				
				if (clusters[i][j]>0) 
				{
					int clusterID = clusters[i][j]; 
					HRectangle rt = taskClusters[TaskClusterOrder(clusterID)].rect; 
					if (rt.x1 > i) 
						rt.x1 = i; 
					if (rt.y1 > j) 
						rt.y1 = j; 
					if (rt.x2 < i) 
						rt.x2 = i; 
					if (rt.y2 < j) 
						rt.y2 = j; 
					
					taskClusters[TaskClusterOrder(clusterID)].rect = rt;  
					Yaz("Heatmap> ClusterBounds() clusterId : "+clusterID + " task order : " + TaskClusterOrder(clusterID) + " i : "+i +" j: "+j+ " rt.x1 :" + taskClusters[TaskClusterOrder(clusterID)].rect.x1 + " y1 : "+taskClusters[TaskClusterOrder(clusterID)].rect.y1 + " x2 : "+taskClusters[TaskClusterOrder(clusterID)].rect.x2 + " y2 : "+taskClusters[TaskClusterOrder(clusterID)].rect.y2);
				}
			}
		}
		
		
	}
	
	public void ResetTaskClusters() 
	{
	/*	for (int i=0; i<clusterCount; i++) 
		{
			
			taskClusters[i].index = -1;  
		}*/
		clusterCount = 0; 
	}
	
	public void ListTaskClusters() 
	{
		
		ResetTaskClusters(); 
		
		for (int i=0; i<tWidth; i++) 
		{
			for (int j=0; j<tHeight; j++) 
			{
				
				if (clusters[i][j]>0) 
				{
					int clusterID = clusters[i][j]; 
					if ( TaskClusterOrder(clusterID) < 0) 
					{
						TaskCluster tc = new TaskCluster(); 
						tc.index = clusterCount; 
						taskClusters[clusterCount++] = tc; 
						Yaz("HeatMap> Cluster created : clusterCount " + clusterCount + " index : " + tc.index) ;
			
					}
			
				}
				
			}
		} 
	
	}
	
	
	public TaskCluster MergeDecision (int xx, int yy, int nClusters, int nearClusters[]) 
	{
		TaskCluster found = null; 
		if (nClusters == 0) 
		{
			found = new TaskCluster(); 
			found.index = cNumbers; 
			clusters[xx][yy] = cNumbers; 
			taskClusters[clusterCount++] = found;
			cNumbers++; 
	//		Yaz("Heatmap> AssignCluster ("+xx+";"+yy+") index : "+found.index + " YENÝ CLUSTER ");
			
		}
		else 
		{
			/*found = new TaskCluster(); 
			found.index = clusterCount; 
			clusters[xx][yy] = clusterCount; 
			taskClusters[clusterCount++] = found;
			*/
			found = GetTaskClusterByIndex(nearClusters[0]) ; 
			for (int i=1; i<nClusters;i++) 
			{
				TaskCluster current = GetTaskClusterByIndex(nearClusters[i]) ; 
				if (found!=null && current != null && found.index != current.index) 
					
				{
					MergeClusters(found, current); 
					
				}
				
			}
		}
		
		return found; 
	}
	public void  EvaluateClusterAddition (int xxx, int yyy) 
	{
		if (heatMatrix [xxx][yyy]>0) 
		{
			int clusterNumber = clusters [xxx][yyy]; 
								
	     	if (clusterNumber < 0) 
			
		    {
	     		TaskCluster taskC = new TaskCluster(); 
	     		taskC.index = cNumbers; 
	     		clusters[xxx][yyy] = cNumbers; 
	     		taskClusters[clusterCount] = taskC; 
	     		clusterNumber = cNumbers; 
	     		clusterCount++; 
	     		cNumbers++;
			 
		    } 

	
		    if (!IsNearCluster(nearClusters, nClusters, clusterNumber)) 
	    	{
		    	nearClusters[nClusters++] = clusterNumber; 
		    }
		
		} // if 
		
		 
		
	}
	
	public boolean IsInBoundaries (int xxx, int yyy) 
	{
		return (xxx >= 0 && xxx<tWidth && yyy>=0 && yyy<tHeight); 
	}
	
	public TaskCluster AssignCluster (int xx, int yy) 
	{
		nearClusters = new int [9];
		nClusters = 0; 
		for (int i=0; i<3; i++) 
		{
			for (int j=0; j<3; j++)
			{
				int xxx= xx-1+i; 
				int yyy =yy-1+j; 
				boolean b = (i*j)!=1 ; 
				
				if ( IsInBoundaries (xxx, yyy) && b )
				{
					EvaluateClusterAddition (xxx, yyy); 

				}   // if xxx
			}  		// for j	
			
		}  // for i 
	//	Yaz("Heatmap> AssignCluster ("+xx+";"+yy+") index : "+clusterCount);
 //		Yaz("Heatmap> AssignCluster () Komþu küme sayýsý "+nClusters + " Toplam küme " + clusterCount );
		// Çevresinde hiç cluster yoksa kendisi cluster oluyor
			return MergeDecision(xx, yy, nClusters, nearClusters); 
		
	}
	
	public void MergeClusters (TaskCluster f, TaskCluster c) 
	{
		 
		int index1= f.index; 
		int index2= c.index; 
	//	Yaz("HEATMAP>MergeClusters()  Found : "+index1 + " current :  "+index2);

		for (int i=0; i<tWidth; i++) 
		{
			for (int j=0; j<tHeight; j++) 
			{
				if (clusters[i][j] == index2)  
					clusters[i][j] = index1 ; 
			}
		}
		
		int delCluster; 
		do {
			delCluster = TaskClusterOrder(index2) ; 
			if (delCluster>-1) 
			{
			for (int i = delCluster ; i<clusterCount-1 ; i++) 
				taskClusters[i] = taskClusters[i+1]; 
			     clusterCount--;
			}
		} while (delCluster>-1);
		
		 
		
	}
	
	
    public TaskCluster GetTaskClusterByIndex (int ix) 
    {
    	for (int i=0; i<clusterCount; i++) 
    	{
    		if (taskClusters[i].index == ix)
    			return taskClusters[i]; 	
    	}
    	return null; 
    } 
    
    public int TaskClusterOrder (int ix) 
    {
    	for (int i=0; i<clusterCount; i++) 
    	{
    		if (taskClusters[i].index == ix)
    			return i; 	
    	}
    	return -1; 
    	
    }
	
	public void ReviseClusters() 
	{
		
		clusterCount = 0; 
		for (int i=0; i<1000000; i++ ) 
		{
			taskClusters [i] = null; 
			
		}
		ResetClusterMatrix(); 
		Yaz("HEATMAP>ReviseClusters()  ColorCount : "+colorCount); 
		for (int i=0; i<colorCount; i++) 
		{
			heatColors[i].tc = AssignCluster (heatColors[i].xx, heatColors[i].yy); 
			
			
		}
 		
		
		
	}
	
	/*public void Cluster () 
	{
		for (int i=0; i<tWidth; i++) 
		{
			for (int j=0; j<tHeight; j++) 
			{
				
				if (clusters[i][j]<0) 
				{
					 for (int )
					
					
					
				}
		
		
			}
		}
	}*/
	
	public void ResetHeatMatrix () 
	{
		
		for (int i=0; i<tWidth; i++) 
		{
		    for (int j=0;j<tHeight; j++) 
			    heatMatrix [i][j] = 0;
		}
		
	}
	
	public int TaskHeatMatrix () 
	{
	
		for (int i=0; i<allTasks; i++) 
		{
			Task t= tr.GetTask(i);				
			AddToHeatMatrix( t.xLoc,t.yLoc, heatRadius);  
			System.out.println("Task "+i+" added to heat matrix " + t.xLoc + "-"+t.yLoc); 
			
		}
		return allTasks; 
		
	}
	
	
	
	public void AddToHeatMatrix (int xx, int yy, int power)
	{
		
		int radius = 3 * power; 
		 
		//fr.ReadReferencePoint(templateSelection); 
			
			
			for (int k=-radius; k<radius; k++) 
			{
				for (int l=-radius; l<radius; l++) 
				{
					if (xx+k>-1 && xx+k<tWidth && yy+l >-1 && yy+l <tHeight) 
					{
						
						int dist =(int) CalcDistance(xx+k, yy+l,xx, yy);
						if (!is.InterSection(xx+k, yy+l, xx, yy)) 
					//	if (!is.IsInClosedStructure(xx+k, yy+l)) 
						{	
							if (dist < radius) 
								heatMatrix [xx+k][yy+l] += radius * 2 - dist; 
						}
																									
					}
					
				}
					
				
				
			}
			
			
			
		
		
	}

	public Color GetAlphaColor(double alpha) 
	{
		Color c = null; 
		/*int tmp; 
		int r=0, g=0, b=0; 
           
		// coloring depending on the current alpha value
		if(alpha <= 255 && alpha >= 235){
		    tmp=255-alpha;
		    r=255-tmp;
		    g=tmp*12;
		}else if(alpha <= 234 && alpha >= 200){
		    tmp=234-alpha;
		    r=255-(tmp*8);
		    g=255;
		}else if(alpha <= 199 && alpha >= 150){
		    tmp=199-alpha;
		    g=255;
		    b=tmp*5;
		}else if(alpha <= 149 && alpha >= 100){
		    tmp=149-alpha;
		    g=255-(tmp*5);
		    b=255;
		}else
		    b=255;
		
	   return new Color (r,g,b);
	   */
		///////////////////////////////////////////////////////
	/*	Color c= null;
		if (alpha < 80 ) 
			c=  new Color (0, 0, alpha );
		else if (alpha>= 80 && alpha < 160 )
			c= new Color (0, alpha, 0);
		else if (alpha>= 160 )
			c= new Color (alpha,0, 0);
		return c; 
	  */
		////////////////////////////////////////////////////
		
		
		/*Color c= new Color (alpha, 0,0);
		return c;
		*/
		/////////////////////////////////////////////////////////
		
		double blue = 0; 
		double red = 0; 
		double green = 0; 
		double value= alpha; 
	//	double spread = maxxx-minnn;
		
		
		    
		    double ratio = 2 * (value-minnn) / (maxxx - minnn);
		    blue = (Math.max(0, 255*(1 - ratio)));
		    red = (Math.max(0, 255*(ratio - 1)));
		    green = 255 - blue - red;
		    c = new Color ((int)red, (int)green, (int)blue) ; 
		    return c; 
	         	
		/*
		
        if (spread>0) 
        {
			 double ratio =  (value - minnn) / spread ; 
        	//double r = alpha-MinHeatMatrix()/spread; 
			 //System.out.println ("min : " + MinHeatMatrix() + " max : "+MaxHeatMatrix() + " value : "+ value + " spread : " + spread + " ratio : " + ratio) ; 
			  
			 if (ratio<0.25) 
			 {
				 blue =1.0; 
				 green = 4*ratio; 
			 }
			 else if (ratio <0.5) 
			 {
				green = 1.0;
				blue = (1.0 + 4.0 * ( minnn - value + 0.25 * spread) / spread); 
			 }
			 else if (ratio < 0.75 )
			 {
				 green = 1.0;
				 red = (4.0 * (value -  minnn - 0.5 *spread) / spread); 
			 }
			 else 
			 {
				 red = 1; 
				 green =  (1.0 + 4.0 * ( minnn - value + 0.75 * spread) / spread); 
			 }
			 //System.out.println ("value : "+value + " red : "+red + " green : "+ green +" blue : "+ blue) ; 
			 c = new Color ((int) red*255, (int)green * 255, (int) blue *255) ; 
			 /*
			 If ratio < 0.25 Then
			        blue = 1
			        green = 4 * ratio
			    ElseIf ratio < 0.5 Then
			        green = 1
			        blue = 1 + 4 * (min - Value + 0.25 * spread) / spread
			    ElseIf ratio < 0.75 Then
			        green = 1
			        red = 4 * (Value - min - 0.5 * spread) / spread
			    Else
			        red = 1
			        green = 1 + 4 * (min - Value + 0.75 * spread) / spread

			    End If
			    heatMapColor = RGB(red * 255, green * 255, blue * 255)
			    *
			 
        }  
		 return c; 
      */
	}

	public double MaxHeatMatrix () 
	{
		double maxx = 0.0; 
		
		for (int i=0; i<tWidth; i++) 
		{
			for (int j=0; j<tHeight; j++) 
			{
				
				if (heatMatrix[i][j]>maxx) 
				{
					maxx = heatMatrix[i][j]; 
					
				}
			}
		}
		return maxx; 
	} 
	
	public boolean TooCloseToOtherPeaks (TaskCluster tc, int xx, int yy) 
	{
		
		for (int i=0; i<tc.peakCount; i++) 
		{
			if (CalcDistance(tc.peakPoints[i].xx, tc.peakPoints[i].yy, xx, yy) < idealPeakDistance) 
				return true; 
				
		}
		return false; 
	}
	
	public boolean TooCloseToOtherStations (int xx, int yy) 
	{
		
		for (int i=0; i<stationCount; i++) 
		{
			if (CalcDistance(peakStations[i].xLoc, peakStations[i].yLoc, xx, yy) < idealStationDistance) 
				return true; 
				
		}
		return false; 
	}
	
	
	public ColorSet MaxInCluster (TaskCluster tc, double smallerThan) 
	{
		double maxx = 0; 
		ColorSet cl = new ColorSet(); 
		
		for (int i=tc.rect.x1; i<tc.rect.x2; i++) 
		{
			for (int j=tc.rect.y1; j<tc.rect.y2; j++) 
			{

				if (clusters[i][j] == tc.index && heatMatrix[i][j] < smallerThan && heatMatrix[i][j]>maxx && !TooCloseToOtherPeaks(tc, i, j) ) 
				{
					maxx = heatMatrix[i][j];
					cl.xx = i; 
					cl.yy = j; 
					cl.maxRoboTime = maxx; 
					Yaz("Heatmap-MaxInCluster()> i : " +  i  +  " j : " + j + " clusterId : " + tc.index + " maxx : " + maxx + " smallerThan : " + smallerThan );
					
				}
			}
		}
		
		
		return cl; 
	}
	
	public double MinHeatMatrix () 
	{
		double minn = 1000000; 
		
		for (int i=0; i<tWidth; i++) 
		{
			for (int j=0; j<tHeight; j++) 
			{
				
				if (heatMatrix[i][j]>0 && heatMatrix[i][j]<minn) 
				{
					minn = heatMatrix[i][j]; 
					
				}
			}
		}
		return minn;  
	}
	
	public void Process (int sSize)  {
		
	    maxxx = MaxHeatMatrix();
	    minnn = MinHeatMatrix(); 
	    if (sSize > 0) 
	    {

	    
	for (int i=0; i<tWidth; i++) 
	{
		for (int j=0; j<tHeight; j++) 
		{
			
			if (heatMatrix[i][j]>0) 
			{
				
				//Yaz("i : "+i+" j : "+ j + " heatMatrix : "+heatMatrix[i][j]); 
				Color cl = GetAlphaColor(heatMatrix[i][j]); 
				ColorSet cs = new ColorSet(); 
				cs.cl = cl; 
				cs.xx = i; 
				cs.yy = j;
				//clusters[i][j] = colorCount;
				heatColors[colorCount++] = cs; 
				 
				
				
				
			} 
			
			
		}
		
	}	
	
	    }
	}
	
	public void drawHeatMap () 
	{
		Yaz("HEATMAP>drawHeatMap() "); 
		
		   ResetHeatMatrix(); 		  
   		   Process (TaskHeatMatrix());
   		    

	}
	
		
	public double CalcDistance(int xs, int ys, int xd, int yd)
	{
		
		int x_fark = xs-xd ; 
		int y_fark =  ys-yd ;
		
		return Math.sqrt( x_fark * x_fark  + y_fark*y_fark ) ;	


	}
	
	public double CalcTPDistance(int xs, int ys, int xd, int yd)
	{
		
		Task ts = new Task (xs,ys,"Tasksource");
		Task td = new Task (xd,yd,"Taskdestination");
		 
		double dist = is.ShortestofClosestsLength(ts, td); 
		if (dist>=0 && dist <3000)
		    return dist; 
		else 
			return 9999;
		
		
		
	}
	
	

}
