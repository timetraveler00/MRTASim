package tsp.src.main.java.net.parasec.tsp.impl;

import Enums.ePointType;
import Enums.eTSPLoopMode;
import IndoorStructure.IndoorStructure;
import IndoorStructure.Wall;
import IndoorStructure.WallNode;
//import sun.java2d.pipe.LoopBasedPipe;

public final class Point {

    public final double x;
    public final double y;
    private boolean active = true;
    public IndoorStructure indoor; 
    ePointType pointType; 
    eTSPLoopMode loopMode; 
     public int index; 
    
    
    

    public Point(final double x, final double y,IndoorStructure is, ePointType pType, eTSPLoopMode lMode,int pIndex) {
        this.x = x;
        this.y = y;
        indoor = is; 
        pointType = pType;
        loopMode = lMode; 
        index = pIndex; 
       
    }
    
    

    /**
     * Euclidean distance.
     * tour wraps around N-1 to 0.
     */ 
    public static double distance(final Point[] points) {
        final int len = points.length; 
        System.out.println("Point->distance()  len: "+len );
	            
        double d= points[len-1].distance(points[0]);
        for(int i = 1; i < len; i++)
            d += points[i-1].distance(points[i]);
        return d;
    }

    /**
     * Euclidean distance.
     */
    public final double distance2(final Point to) {
        return Math.sqrt(_distance(to));
    }
   
    /**
     * compare 2 points.
     * no need to square when comparing.
     * http://en.wikibooks.org/wiki/Algorithms/Distance_approximations
     */ 
    public final double _distance(final Point to) {
       
    	final double dx = this.x-to.x;
        final double dy = this.y-to.y;
    	
    	
	    return (dx*dx)+(dy*dy); 
    	
    	
	
    }

     ////  ADDED BY SAVAS OZTURK
     // 2018 October 02nd  
     // Distance method of original algorithm does not take obstacles into account. 
     // Instead of calculating distances by sqrt, we insert instant values from preprocessed information 
      
      
      
       
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
    	
    	
    	
    	
    	
    	
    	 
    	
    	
   
    	
    }
     
     public final double dist(final Point to) {
     
    	 	WallNode wnr = new WallNode((int)this.x, (int)this.y);
    		WallNode wnt = new WallNode((int)to.x, (int)to.y);
    		Wall w = new Wall(wnr, wnt);
    		double distance = 0;
             
    		
    		// Ýki görev arasýna engel geliyorsa durum karýþýk
    		if (indoor.InterSection(w)) {
    			distance = indoor.ShortestofClosestsLength(wnr, wnt);
    		}
    		// arada duvar yoksa direk Euclidian
    		else {
    			distance = distance2(to);
    		}

    		
    		return distance;
    	 
     }
    
    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
    	this.active = active;
    }

    public String toString() {
        return x + " " + y;
    }
}
