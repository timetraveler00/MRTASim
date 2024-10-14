package MRTAmain;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Djkstra.Vertex;
import Enums.ePointType;
import Enums.eTSPLoopMode;
import IndoorStructure.IndoorStructure;
import IndoorStructure.Wall;
import IndoorStructure.WallNode;
import MRTAmain.SimManagerAgent.LoopModeSelection;
import tsp.src.main.java.net.parasec.tsp.DumpPoints;
import tsp.src.main.java.net.parasec.tsp.TSP;
import tsp.src.main.java.net.parasec.tsp.TSPSolver;
import tsp.src.main.java.net.parasec.tsp.impl.Point;



public class TSPMinPath {

	  // TSPMinGreedy result ;
	   int counter = 0; 
	   IndoorStructure is; 
	   //int points[][] = new int [1000][3];
	   //Point points[] ;  
	   eTSPLoopMode lpMode; 
	   int INFNTY = 99999;
	   int BIGNUMBER = 9999; 
	   List<Integer> tour; 
	   double tourCost; 
	   int tourSize; 
	   // dynamically hold the instances
       ArrayList<Point> pointList = new ArrayList<Point>();
      
	   
	 /*  
	   {
		   
		  

	        // fill it with a random number between 0 and 100
	        int elements = new Random().nextInt(100);  
	        for( int i = 0 ; i < elements ; i++ ) {
	            list.add( new xClass() );
	        }

	        // convert it to array
	        xClass [] array = list.toArray( new xClass[ list.size() ] );


	        System.out.println( "size of array = " + array.length );
	   }
	   */
	   
	  /* public void prep() 
	   {
		   final TSP tsp = new TSPSolver();
			
			// error = distance found.
			final long l = System.currentTimeMillis();
			final double error = tsp.solve(points);
			
			DumpPoints.dump(points,""/*, args[1]*);

			System.out.printf("tour length = %.4f\toptimisation time = %.2f seconds.\n", error, (System.currentTimeMillis() - l)/1000d);	
		   
	   }
	   */
	   
	   public TSPMinPath (IndoorStructure ist, eTSPLoopMode loopMode) 
	   {
		   is = ist; 
		   // result = new TSPMinGreedy(is, loopMode);
		   //result = new TSPMinGreedy(is, loopMode);
		   counter = 0; 
		   lpMode = loopMode; 
	   }
	   
	   public TSPMinPath (TSPMinPath tm) 
	   {
		   this.is = tm.is; 
		   this.lpMode = tm.lpMode; 
		   this.counter = 0; 
		   
		   pointList = new ArrayList<Point>();
		   for (int i=0; i<tm.PointCount();i++) 
			   this.pointList.add(tm.pointList.get(i));
		   
		   this.tour = new ArrayList<Integer> () ;
		   
		   for (int i=0; i<tm.tour.size(); i++) 
			   this.tour.add((tm.tour.get(i)));
		   
		   this.tourCost = tm.tourCost; 
		   this.tourSize = tm.tourSize; 
	   }
	   
	   public void AddPoint (int xLoc, int yLoc,ePointType pointType) 
	   {
		   //myTsp.addPoint((double) xLoc, (double) yLoc); 
		 /*  points[counter][0] = xLoc; 
		   points[counter][1] = yLoc;
		   
		   points[counter][2] = 0; 
		   counter++;*/ 
		   
		   System.out.println("TSPMinPath-> Point Added xloc" + xLoc + " yloc: "+yLoc + " pointType: "+pointType + " index: "+counter);
		   pointList.add( new Point(xLoc, yLoc,is,pointType,lpMode,counter++) );
		   
		   //points[counter] = new Point(xLoc, yLoc,is,pointType,lpMode,counter++);
		   
	   }
	   
	   public void AddTask (Task t) 
	   {
           AddPoint (t.xLoc, t.yLoc,ePointType.TASK);  
	   }
	   ; 
	   public void AddRobot (Robotum r) 
	   {
           AddPoint (r.xLoc, r.yLoc,ePointType.ROBOT);  
	   }
	   
	
/*
	   
	   public double CalcDistance(int from, int to)
	   {
	   	
	 
              return  points[from].distance(points[to]);

	   }
	*/
	
	
	   public int PointCount() 
	   {
		   return counter; 
	   }
	   
	   ////////////////////////////////     YENÝ TSP ALGORÝTMASINDA DEÐÝÞECEKLER 
	   
	   public void ResetDistances ()
	   {
		
		   /// BAKILACAK 
		   // 2 EKÝM
		   /*  
		   // System.out.println("counter : " + counter );
		   result = new TSPMinGreedy(counter, lpMode); 		  
			
		   result.setMaxPath(10000);
			
			for(int i = 0; i < counter; i++){
				for(int j = 0; j < counter; j++){
					

						
						result.setDistance(i, j, 999);


				}
			}
			*/
	   }
	   
/*	   public void SetDistance (int from, int to, int dist)
	   {
		   
		   //System.out.println("from : " + from + " to : "+to +" dist : "+dist);

		   result.setDistance(from, to, dist);
		   
	   }
	*/   
	   public double[][] createDistanceMatrix() 
	   {
		   
		   double distanceMatrix[][] = new double[pointList.size()][pointList.size()];
		   for (int i=0;i<pointList.size();i++) 
		   {
			   Point pi = pointList.get(i);
			   for (int j=0; j<pointList.size();j++) 
			   {
			   
				   Point pj = pointList.get(j);
				   
				   if (i==j ) 
				   {
					   distanceMatrix[i][j] = BIGNUMBER;
				   }
				   else if (j==0 && (lpMode == eTSPLoopMode.TSP_PATH || lpMode == eTSPLoopMode.TSP_PATHTOSTATION)) 
				   {
					   distanceMatrix[i][j] = BIGNUMBER;
				   }
				   else if (i==pointList.size()-1 && lpMode == eTSPLoopMode.TSP_PATHTOSTATION)
				   {
					   distanceMatrix[i][j] = BIGNUMBER;
				   }
				   else if (i==0 && j==pointList.size()-1 && lpMode == eTSPLoopMode.TSP_PATHTOSTATION )
				   {
					   distanceMatrix[i][j] = BIGNUMBER;
				   }
				   else 
				   {
					   distanceMatrix[i][j] = dist (pi,pj);
				   }
				   
				     
				   	
				   	
			   }		   
				   	
		   }
		   return distanceMatrix; 
	   }
	   public final double dist(final Point from,final Point to) {
		     
   	 	WallNode wnr = new WallNode((int)from.x, (int)from.y);
   		WallNode wnt = new WallNode((int)to.x, (int)to.y);
   		Wall w = new Wall(wnr, wnt);
   		double distance = 0;
            
   		
   		// Ýki görev arasýna engel geliyorsa durum karýþýk
   		if (is.InterSection(w)) {
   			distance = is.ShortestofClosestsLength(wnr, wnt);
   		}
   		// arada duvar yoksa direk Euclidian
   		else {
   			distance = distance2(from,to);
   		}

   		
   		return distance;
   	 
    }
	   
	   /**
	     * Euclidean distance.
	     */
	    public final double distance2(final Point from,final Point to) {
	        return Math.sqrt(_distance(from,to));
	    }
	   
	    /**
	     * compare 2 points.
	     * no need to square when comparing.
	     * http://en.wikibooks.org/wiki/Algorithms/Distance_approximations
	     */ 
	    public final double _distance(final Point from,final Point to) {
	       
	    	final double dx = from.x-to.x;
	        final double dy = from.y-to.y;
	    	
	    	
		    return (dx*dx)+(dy*dy); 
	    	
	    	
		
	    }
	   
	   /*
	   public final double distance(final Point to) {
		   
	    	
	    	ePointType ownType = this.pointType; 
	    	ePointType toType = to.pointType; 
	    	
	    	//return dist(to); 
	    	
	    	if (ownType==ePointType.ROBOT && toType == ePointType.TASK) 
	    	{
	    	     return dist(to); 
	    	}
	    	else if (ownType==ePointType.TASK && toType == ePointType.ROBOT && loopMode==eTSPLoopMode.TSP_TOUR) 
	    	{
	    	     return dist(to); 
	    	}
	    	else if (ownType==ePointType.TASK && toType == ePointType.ROBOT) 
	    	{
	    		return 9999;
	    	}
	    	else if (ownType==ePointType.TASK && toType == ePointType.TASK) 
	    	{
	    		return dist(to); 
	    	}
	    	else if (ownType==ePointType.ROBOT && toType == ePointType.ROBOT) 
	    	{
	    		return 9999; 
	    	}
	    	else if (ownType==ePointType.ROBOT && toType == ePointType.STATION) 
	    	{
	    		return 9999; 
	    	}
	    	else if (ownType==ePointType.STATION && toType == ePointType.ROBOT) 
	    	{
	    		return 9999; 
	    	}
	    	else if (ownType==ePointType.TASK && toType == ePointType.STATION) 
	    	{
	    		return dist(to); 
	    	}
	    	else if (ownType==ePointType.STATION && toType == ePointType.TASK) 
	    	{
	    		return 9999; 
	    	}
	    	
	    	return 9999; 
	    	
	  	
	    }*/
	   public double TSPPathLength () 
	   {
		  
		   double distanceMatrix[][] = createDistanceMatrix();
		   TspDynamicProgrammingIterative tsp = new TspDynamicProgrammingIterative(0,distanceMatrix);
		   tour = tsp.getTour(); 
		   
		   // Prints: [0, 3, 2, 4, 1, 5, 0]
		    System.out.println("Tour: " + tour);
            tourSize = (lpMode==eTSPLoopMode.TSP_TOUR) ? tour.size() : tour.size() - 1 ;  
		    
		    tourCost= (lpMode==eTSPLoopMode.TSP_TOUR) ?  tsp.getTourCost() : tsp.getTourCost()-BIGNUMBER;
            
            //tourCost = tsp.getTourCost(); 
		    // Print: 42.0
		    System.out.println("Tour cost: " + tourCost);
		    
		    
		    
		    
		   //return tsp.getTourCost();
		    return tourCost;
		   
		   /* final TSP tsp = new TSPSolver();
			
			// error = distance found.
			final long l = System.currentTimeMillis();
			
			points = pointList.toArray(new Point[ pointList.size() ]);
			final double error = tsp.solve(points);
			
			return (int) error;
			
			 */
		   
		   //  Calculate(); 
	       // System.out.println("min solution ....");
		//	int min = result.findMinSolution();
		   //	System.out.println("min solution 2...."+min);
			
	   }
	   /*
	   public LinkedList<Integer> Calculate () 
	   {

			// System.out.println("tsp hesaplama baþladý....");
			//read the n x n matrix of distances
			for(int i = 0; i < counter; i++){
				for(int j = 0; j < counter; j++){
					
					if (i==j )
					{
						
						result.setDistance(i, j, 0);
					}
					else if ( (lpMode==eTSPLoopMode.TSP_PATH || lpMode==eTSPLoopMode.TSP_PATHTOSTATION)   && j==0)
					{
						result.setDistance(i, j, 0);
					}
		
				}
			}
			/*System.out.println("Final matrix");
			for(int i = 0; i < counter; i++){
				for(int j = 0; j < counter; j++){
					System.out.print(result.distances[i][j]+" ");
					
				}
				System.out.println("");
			}
			
			
			
			System.out.println("tsp hesaplama bitti....");
			*
			return (LinkedList<Integer>) result.minPathList;

	   }*/
	   
//////////////////////////////////////////////////////////////////////// END OF DEÐÝÞECEKLER 
	   
	   
	   
	   /*   public void AddPointList (LinkedList<Vertex> pointss) 
	   {
		   for (Vertex vertex: pointss ) 
		   {
			 //  myTsp.addPoint(vertex.tp.xLoc, vertex.tp.yLoc);
			   points[counter][0] = (int) vertex.tp.xLoc; 
			   points[counter][1] = (int) vertex.tp.yLoc;
			   counter++; 
		   }
		   
	   } */ 
	   
	   public void List()
	   {
		/*   int From; int To; 

			System.out.println("--before------");
			for (int i=0; i<counter; i++) 
			{
				System.out.println("Point["+Integer.toString(i)+"] = "+Integer.toString(points[i][0])+" , "+Integer.toString(points[i][1])+ "  "+Integer.toString(points[i][2]));
		
			}
		   Calculate(); 
		   System.out.println("--after------");
			From = 0; 
			To = myTsp.To(From);
			for (int i=0; i<counter; i++) 
			{
				System.out.println("Point["+Integer.toString(i)+"] = "+Integer.toString(points[From][0])+" , "+Integer.toString(points[From][1])+ "  "+Integer.toString(points[From][2]));
				 To = myTsp.To(To); From = myTsp.From(To);
			}
			System.out.println("--the end------");
			
			*/
			
			
	   }
	   
	   /*public void RedundancyCheck() {
	   
       for (int i=0; i<counter ; i++) 
       {
    	
    	   for (int j=i+1; j<counter; j++) 
    	   {
    		   if (points[i][2] == 0 && points[j][2] == 0 &&  points[i][0] == points[j][0] && points[i][1] == points[j][1]) 
    				   
    		   {
    					   points[j][2]  = 1;
    			
    		   }
    		   
    	   }
//    	   myTsp.addPoint(points[i][0],points[i][1] ); 
    	   
  

    	   
       }
       for (int i=0; i<counter ; i++) 
       {System.out.println(i+"-"+points[i][2]);
	   
       }
   } 
   
   */
	   
	
	
	
}
