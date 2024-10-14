package IndoorStructure;


import java.awt.geom.Line2D;

import MRTAmain.Task;

public class Wall {

	public WallNode startNode; 
	public WallNode endNode; 
	public TransitionPoint tpStart1; 
	public TransitionPoint tpEnd1; 
	public TransitionPoint tpStart2; 
	public TransitionPoint tpEnd2; 
	public TransitionPoint tpStart3; 
	public TransitionPoint tpEnd3; 
	public TransitionPoint tpStart4; 
	public TransitionPoint tpEnd4; 
	
	public Wall(WallNode str, WallNode end) 
	{
		startNode = str; 
		endNode = end; 
		
	}
	
	public Wall(Task t1, Task t2) 
	{
		startNode = new WallNode(t1.xLoc, t1.yLoc); 
		endNode = new WallNode(t2.xLoc, t2.yLoc);  
		
	}
	public Wall(int x1, int y1, int x2, int y2) 
	{
		startNode = new WallNode(x1, y1); 
		endNode = new WallNode(x2, y2);  
		
	}
	
	public int GetLength() 
	{
		int x_fark = startNode.xLoc-endNode.xLoc; 
		int y_fark = startNode.yLoc-endNode.yLoc; 
		
		return (int) Math.sqrt( Math.pow(x_fark, 2) +  Math.pow(y_fark, 2)) ; 
		
		
		
		 
	}
	
	public boolean Surroundings (Wall walle) 
	{
		int radius = 5; //(int) robotStep; 
		
		int sx= walle.startNode.xLoc;
		int sy= walle.startNode.yLoc;
		int ex= walle.endNode.xLoc;
		int ey= walle.endNode.yLoc;
		Wall walla = new Wall (new WallNode(sx-radius,sy-radius),new WallNode(sx+radius,sy-radius));
		Wall wallb = new Wall (new WallNode(sx-radius,sy-radius),new WallNode(ex-radius,ey+radius));
		Wall wallc = new Wall (new WallNode(ex-radius,ey+radius),new WallNode(ex+radius,ey+radius));
		Wall walld = new Wall (new WallNode(ex+radius,ey+radius),new WallNode(sx+radius,sy-radius));
		//System.out.println ("sx " + sx + " sy" + " ex "+ ex + " ey " + ey ); 
		 if (Intersection(walla) || Intersection(wallb) || Intersection(wallc) || Intersection(walld))
		 {
			 System.out.println ("wall sx " + sx + " sy " + sy +" ex "+ ex + " ey " + ey  +" start x " +startNode.xLoc + " start y "  +startNode.yLoc+ " end x "+  endNode.xLoc + " end y " +  endNode.yLoc); 
			 return true;
		 }
		/*if (is_main.InterSection(walla)) 
			retVal = retVal+rw; 
		if (is_main.InterSection(wallb)) 
			retVal = retVal +rw*10; 
		if (is_main.InterSection(wallc)) 
			retVal = retVal -rw; 
		if (is_main.InterSection(walld)) 
			retVal = retVal -rw*10; 
		*/
		
		 else return false; 
	}
	public boolean Intersection (Wall walle) 
	{
	
	    Line2D.Double l1 = new Line2D.Double() ; 
	    l1.setLine(startNode.xLoc, startNode.yLoc, endNode.xLoc, endNode.yLoc);
	    
	   
	    Line2D.Double l2 = new Line2D.Double() ;
	    l2.setLine(walle.startNode.xLoc, walle.startNode.yLoc, walle.endNode.xLoc, walle.endNode.yLoc);
	    

	    Line2D.Double l2a = new Line2D.Double() ;
	    l2a.setLine(walle.startNode.xLoc, walle.startNode.yLoc-5, walle.endNode.xLoc, walle.endNode.yLoc-5);
	    
	    Line2D.Double l2b = new Line2D.Double() ;
	    l2b.setLine(walle.startNode.xLoc, walle.startNode.yLoc+5, walle.endNode.xLoc, walle.endNode.yLoc+5);
	    
		
	    Line2D.Double l3a = new Line2D.Double() ;
	    l3a.setLine(walle.startNode.xLoc+5, walle.startNode.yLoc, walle.endNode.xLoc+5, walle.endNode.yLoc);
	    
	    Line2D.Double l3b = new Line2D.Double() ;
	    l3b.setLine(walle.startNode.xLoc-5, walle.startNode.yLoc, walle.endNode.xLoc-5, walle.endNode.yLoc);
	    
	    /*if (l1.intersectsLine(l2)) 
		{
	        System.out.println(Integer.toString(startNode.xLoc) + "-"+ Integer.toString(startNode.yLoc) + "-"+Integer.toString(endNode.xLoc) + "-"+Integer.toString(endNode.yLoc)  ); 
		    System.out.println(Integer.toString(walle.startNode.xLoc) + "-"+ Integer.toString(walle.startNode.yLoc) + "-"+Integer.toString(walle.endNode.xLoc) + "-"+Integer.toString(walle.endNode.yLoc)  );
			System.out.println("kesiyor"); 
	    
		}*/
	    return l1.intersectsLine(l2);
		//return l1.intersectsLine(l2)||l1.intersectsLine(l2a)||l1.intersectsLine(l2b)||l1.intersectsLine(l3a)||l1.intersectsLine(l3b); 
	}
	
	public void CalculateGuards () 
	{
		int tx = startNode.xLoc; 
	    int ty = startNode.yLoc; 
	    int rx = endNode.xLoc ;
	    int ry = endNode.yLoc ; 

   		 //double h = 5.0;
	     int margin = 10;  
         double ydiff = ty - ry  > 0.0 ?  1.0 :  -1.0;
         double xdiff = tx - rx > 0.0 ?  1.0 :  -1.0;
         double angle;
         if (tx - rx ==0)
         {    if ( ty - ry > 0)
                angle = 90.0;
         else if (ry-ty > 0 )
        	 angle = 270.0; 
            else if  ( ry - ty > 0 )
                angle = 180.0;
            else
                angle = 0.0;

           // angle =  //atan (abs(task->GetY()-GetY())) * 180 / PI;
         }
         else
             angle = Math.atan (Math.abs( ty -ry)*1.0 / Math.abs(tx-rx)*1.0) * 180.0 / Math.PI;
       //  return (int) angle; 
         //double xx = h * Math.cos(angle*Math.PI/180) ;
         //double yy = h * Math.sin (angle*Math.PI/180) ;
         double xx = margin * Math.cos(angle*Math.PI/180) ;
         double yy = margin * Math.sin (angle*Math.PI/180) ;

         double fx = Math.round(xx * xdiff); 
         double fy = Math.round(yy * ydiff);  
         
         //double instantWay  = Math.sqrt(fx*fx+fy*fy);
     	tpStart1 = new TransitionPoint(new Task(startNode.xLoc+(int)fx, startNode.yLoc+(int)fy, "tpstart"));
     	tpEnd1 = new TransitionPoint(new Task(endNode.xLoc+(int)fx, endNode.yLoc+(int)fy, "tpend"));
     	tpStart2= new TransitionPoint(new Task(startNode.xLoc-(int)fx, startNode.yLoc-(int)fy, "tpstart"));
     	tpEnd2 = new TransitionPoint(new Task(endNode.xLoc-(int)fx, endNode.yLoc-(int)fy, "tpend"));
         
     
	
	}
	
	public void CalculateGuards3 () 
	{
		int tx = startNode.xLoc; 
	    int ty = startNode.yLoc; 
	    int rx = endNode.xLoc ;
	    int ry = endNode.yLoc ; 

   		 //double h = 5.0;
	     int margin = 10;  
         double ydiff = ty - ry  > 0.0 ?  1.0 :  -1.0;
         double xdiff = tx - rx > 0.0 ?  1.0 :  -1.0;
         double angle;
         if (tx - rx ==0)
         {    if ( ty - ry > 0)
                angle = 90.0;
         else if (ry-ty > 0 )
        	 angle = 270.0; 
            else if  ( ry - ty > 0 )
                angle = 180.0;
            else
                angle = 0.0;

           // angle =  //atan (abs(task->GetY()-GetY())) * 180 / PI;
         }
         else
             angle = Math.atan (Math.abs( ty -ry)*1.0 / Math.abs(tx-rx)*1.0) * 180.0 / Math.PI;
       //  return (int) angle; 
         //double xx = h * Math.cos(angle*Math.PI/180) ;
         //double yy = h * Math.sin (angle*Math.PI/180) ;
         double xx = margin * Math.cos(angle*Math.PI/180) ;
         double yy = margin * Math.sin (angle*Math.PI/180) ;
         double xx2 = margin * Math.cos((angle+90.0)*Math.PI/180) ;
         double yy2 = margin * Math.sin ((angle+90.0)*Math.PI/180) ;
         double xx3 = margin * Math.cos((angle+270.0)*Math.PI/180) ;
         double yy3 = margin * Math.sin ((angle+270.0)*Math.PI/180) ;

         double fx = Math.round(xx * xdiff); 
         double fy = Math.round(yy * ydiff);  
         double fx2 = Math.round(xx2 * xdiff); 
         double fy2 = Math.round(yy2 * ydiff);  
         double fx3 = Math.round(xx3 * xdiff); 
         double fy3 = Math.round(yy3 * ydiff);  
         
         //double instantWay  = Math.sqrt(fx*fx+fy*fy);
     	tpStart1 = new TransitionPoint(new Task(startNode.xLoc+(int)fx, startNode.yLoc+(int)fy, "tpstart"));
     	tpEnd1 = new TransitionPoint(new Task(endNode.xLoc+(int)fx, endNode.yLoc+(int)fy, "tpend"));
     	tpStart2= new TransitionPoint(new Task(startNode.xLoc-(int)fx, startNode.yLoc-(int)fy, "tpstart"));
     	tpEnd2 = new TransitionPoint(new Task(endNode.xLoc-(int)fx, endNode.yLoc-(int)fy, "tpend"));
     	
     	tpStart3= new TransitionPoint(new Task(startNode.xLoc-(int)fx, startNode.yLoc+(int)fy, "tpstart"));
     	tpEnd3 = new TransitionPoint(new Task(endNode.xLoc-(int)fx, endNode.yLoc+(int)fy, "tpend"));
     	tpStart4= new TransitionPoint(new Task(startNode.xLoc+(int)fx, startNode.yLoc-(int)fy, "tpstart"));
     	tpEnd4 = new TransitionPoint(new Task(endNode.xLoc+(int)fx, endNode.yLoc-(int)fy, "tpend"));
     	
     	
     	//tpStart3= new TransitionPoint(new Task(startNode.xLoc-(int)fx2, startNode.yLoc-(int)fy2, "tpstart"));
     	//tpEnd3 = new TransitionPoint(new Task(endNode.xLoc-(int)fx2, endNode.yLoc-(int)fy2, "tpend"));
     	//tpStart4= new TransitionPoint(new Task(startNode.xLoc+(int)fx3, startNode.yLoc+(int)fy3, "tpstart"));
     	//tpEnd4 = new TransitionPoint(new Task(endNode.xLoc+(int)fx3, endNode.yLoc+(int)fy3, "tpend"));
         System.out.println ("angle " + angle + " fx2 "+fx2 + " fy2 "+ fy2 );
     
	
	}

	
	public boolean Intersection (Line2D.Double walle) 
	{
	    Line2D.Double l1 = new Line2D.Double() ; 
	    l1.setLine(startNode.xLoc, startNode.yLoc, endNode.xLoc, endNode.yLoc);
	    
	    	    
		return l1.intersectsLine(walle); 
	}
	
	
	
}
