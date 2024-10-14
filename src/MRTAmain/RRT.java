package MRTAmain;


import java.util.ArrayList;

import Djkstra.Edge;
import Djkstra.Vertex;
import IndoorStructure.IndoorStructure;
import IndoorStructure.TransitionPoint;
import IndoorStructure.Wall;
import IndoorStructure.WallNode;



public class RRT {
	
	
	int c=0,delta=20;



	  private int TWIDTH; 
	  private int THEIGHT; 
	  private int MAX_ITERATIONS; 
	  private IndoorStructure is = null; 
	  
	  

		   int maxC = 100; 
		  int [] x;
		  int [] y ;

		  int [] from ; 
		  int cCount = 0; 
	  
		 int c2=0,delta2=5;
		 //int maxX=500,maxY=500;
		   int maxC2 = 100; 
		  int [] x2 ;
		  int [] y2 ;

		  int [] from2 ; 
		  int cCount2 = 0;
		  
		   int rx,ry,n=0;
		    double d,min;
		    int rx2,ry2,n2=0;
		    double d2,min2;
	  
	public RRT (int dlt, int maxIter, IndoorStructure ist) 
	{
		TWIDTH = ist.tW; 
		THEIGHT = ist.tH; 
		delta = dlt; 
		MAX_ITERATIONS = maxIter; 
		is = ist; 
		x = new int [MAX_ITERATIONS];
		y = new int [MAX_ITERATIONS];

		x2 = new int [MAX_ITERATIONS];
		y2 = new int [MAX_ITERATIONS];
		
		from = new int [MAX_ITERATIONS];
		from2 = new int [MAX_ITERATIONS];
		
	
	}
	public IndoorStructure GetIndoorStructure ()
	{
		return is; 
	}
	  
	
	
	
	public boolean hit(int x,int y) { 
		return is.InterSection(new Wall(new WallNode(x-4, y-4),new WallNode(x+4, y+4))); 
		/*boolean hit=false;
		    for (int b=0;b<boxes;b++) {
		      if (x>bx[b] && y>by[b] && x<bx[b]+bdx[b] && y<by[b]+bdy[b]) hit=true;
		    }
		    return hit;*/
		  }
	public boolean linehit(int xs,int ys, int xe, int ye) {
		   
		return is.InterSection(new Wall(new WallNode(xs, ys),new WallNode(xe, ye))); 
		/*boolean hit=false;
		    for (int b=0;b<boxes;b++) {
		      if (x>bx[b] && y>by[b] && x<bx[b]+bdx[b] && y<by[b]+bdy[b]) hit=true;
		    }
		    return hit;*/
		  }
	
	public double CalcDistance(int xs, int ys, int xd, int yd)
	{
		
		int x_fark = xs-xd ; 
		int y_fark =  ys-yd ;
		
		return Math.sqrt( x_fark * x_fark  + y_fark*y_fark ) ;	


	}
	public int CalcRRT(Task start, Task end) 
	{
		return CalcRRT(new TransitionPoint(start), new TransitionPoint(end) ); 		
	} 
	public IndoorStructure RRTPath(Task start, Task end) 
	{
		return RRTPath(new TransitionPoint(start), new TransitionPoint(end) ); 		
	} 
	public IndoorStructure BuildRRT_BiDirect(Task start, Task end) 
	{
		return BuildRRT_BiDirect(new TransitionPoint(start), new TransitionPoint(end) ); 		
	} 
	public int CalcRRT(int x1, int y1, int x2, int y2) 
	{
		Task start = new Task (x1, y1, "start"); 
		Task end = new Task(x2,y2,"end");
		return CalcRRT(new TransitionPoint(start), new TransitionPoint(end) ); 		
	}
	
	public int CalcRRT(Robotum r, Task t) 
	{
		Task start = new Task (r.xLoc, r.yLoc, "start"); 
		Task end = t;
		return CalcRRT(new TransitionPoint(start), new TransitionPoint(end) ); 		
	}	
	
	public int CalcRRT(TransitionPoint start, TransitionPoint end) 
	{
			
		int result = 5000; 
		if (!is.InterSection(new Wall(new WallNode(start.xLoc, start.yLoc), new WallNode(end.xLoc, end.yLoc)))) 
		{
			result = (int) CalcDistance(start.xLoc, start.yLoc, end.xLoc, end.yLoc); 
		}
		
		is.nodes = new ArrayList<Vertex>();
		is.edges = new ArrayList<Edge>();  
		is.tpoints = new ArrayList<TransitionPoint>();
		x = new int [MAX_ITERATIONS]; 
		y = new int [MAX_ITERATIONS]; 
		from = new int [MAX_ITERATIONS]; 
		System.out.println("delta : " + Integer.toString(delta)+ " max iter : "+Integer.toString(MAX_ITERATIONS)); 
		TransitionPoint TP_closest_start = null;
		TransitionPoint TP_closest_end = null ;	
		
	//	 x[0]= TWIDTH/2; y[0]=THEIGHT/2;
	//	System.out.println("RRT :000"); 
int qw=0; 
		do {
 //   x[0]= TWIDTH/2-qw;
   // y[0]=THEIGHT/2-qw;
			x[0] = start.xLoc+qw ; 
			y[0] = start.yLoc-qw ; 
     qw++; 
} while(hit(x[0],y[0]));
//System.out.println("RRT :xxx"); 
		 is.SetTP(new TransitionPoint(x[c], y[c], "TP"+Integer.toString(c)));
		
		
		 c= 1;  
		 
		  //  int rx,ry,n=0;
		   // double d,min;

		 while (c<MAX_ITERATIONS) 
		 {
			// System.out.println("RRT :111"); 
		      do {
		          rx=(int)(Math.random()*TWIDTH);
		          ry=(int)(Math.random()*THEIGHT);
		        } while(hit(rx,ry));

		      //System.out.println("RRT :222"); 
		        
		        // den knoten mit der geringsten distanz zum punkt suchen
		        min=-1;
		    /*    for (int i=0;i<c;i++) {
		          d=Math.sqrt((x[i]-rx)*(x[i]-rx)+(y[i]-ry)*(y[i]-ry));
		          if (min==-1 || d<min) {
		            min=d; n=i;
		          }
		        }*/
		        d=min; 
		        StepSize1(); 
		        
		    //    System.out.println("RRT :333"); 
			 
		        // maximal mit der entfernung delta springen
		        if (delta<d) d=delta;

		        // neuen knoten berechnen
		        x[c]=x[n]+(int)(d*(rx-x[n])/min);
		        y[c]=y[n]+(int)(d*(ry-y[n])/min);

		     //   System.out.println("RRT :444"); 
		        // nochmal prüfen ob der neue knoten nicht in ein hindernis fällt
		        if (!linehit(x[n], y[n], x[c],y[c])) {
		          
		       // 	 System.out.println("RRT :555"); 
		        	from [c] = n;
		          
		          is.SetTP(new TransitionPoint(x[c], y[c], "TP"+Integer.toString(c)));
		          int mesafe = (int) CalcDistance(x[n], y[n], x[c],y[c]); 
		          //	System.out.println("RRT : n : " + Integer.toString(n) +" c : "+ Integer.toString(c) + " From ["+Integer.toString(x[n]) +";"+ Integer.toString(y[n])+"]  - To ["+Integer.toString(x[c])+";"+Integer.toString(y[c]) + "] mesafe : "+ Integer.toString(mesafe)); 
		          is.addLane("x", n, c, mesafe);
		          is.addLane("x", c, n, mesafe);
		          c++;
		        }
		        cCount = c;
				 
		        //System.out.println("RRT :666"); 
		         // durdurma
		         TP_closest_start = is.Closest(start,TP_closest_start);
				 TP_closest_end = is.Closest(end,TP_closest_end);	
				 
				 if (TP_closest_start!=null && TP_closest_end!= null) 
				 {
					// System.out.println("RRT :777"); 
				//	 System.out.println("RRT :777"); 
				//	 System.out.println("RRT CALC vertices count : "+is.GetTPCount()); 
				//	 System.out.println("RRT CALC edges count : "+is.GetLaneCount()); 
					result =  is.ShortestPathLength(TP_closest_start, TP_closest_end)+ 
							(int) CalcDistance(start.xLoc, start.yLoc, TP_closest_start.xLoc, TP_closest_start.yLoc) +  
							(int) CalcDistance(end.xLoc, end.yLoc, TP_closest_end.xLoc, TP_closest_end.yLoc);  
					 c = MAX_ITERATIONS; 
					 
				 }
				// System.out.println("RRT :888"); 

		 }
		 System.out.println("RRT :999"); 
		 System.out.println("RRT CALC vertices count : "+is.GetTPCount()); 
		 System.out.println("RRT CALC edges count : "+is.GetLaneCount()); 
		 return result; 
		 
		
		
	}
	
	public IndoorStructure RRTPath(TransitionPoint start, TransitionPoint end) 
	{
			
		IndoorStructure retIS = null; 
		if (is.InterSection(new Wall(new WallNode(start.xLoc, start.yLoc), new WallNode(end.xLoc, end.yLoc)))) 
		{
			
		is.nodes = new ArrayList<Vertex>();
		is.edges = new ArrayList<Edge>();  
		is.tpoints = new ArrayList<TransitionPoint>();
		x = new int [MAX_ITERATIONS]; 
		y = new int [MAX_ITERATIONS]; 
		from = new int [MAX_ITERATIONS]; 
		System.out.println("delta : " + Integer.toString(delta)+ " max iter : "+Integer.toString(MAX_ITERATIONS)); 
		TransitionPoint TP_closest_start = null;
		TransitionPoint TP_closest_end = null ;	
		
	//	 x[0]= TWIDTH/2; y[0]=THEIGHT/2;
	//	System.out.println("RRT :000"); 
int qw=0; 
		do {
 //   x[0]= TWIDTH/2-qw;
   // y[0]=THEIGHT/2-qw;
			x[0] = start.xLoc+qw ; 
			y[0] = start.yLoc-qw ; 
     qw++; 
} while(hit(x[0],y[0]));
//System.out.println("RRT :xxx"); 
		 is.SetTP(new TransitionPoint(x[c], y[c], "TP"+Integer.toString(c)));
	
		
		 c= 1;  
		 
		  //  int rx,ry,n=0;
		  //  double d,min;

		 while (c<MAX_ITERATIONS) 
		 {
			// System.out.println("RRT :111"); 
		      do {
		          rx=(int)(Math.random()*TWIDTH);
		          ry=(int)(Math.random()*THEIGHT);
		        } while(hit(rx,ry));

		      //System.out.println("RRT :222"); 
		        
		        // den knoten mit der geringsten distanz zum punkt suchen
		        min=-1;
		      /*  for (int i=0;i<c;i++) {
		          d=Math.sqrt((x[i]-rx)*(x[i]-rx)+(y[i]-ry)*(y[i]-ry));
		          if (min==-1 || d<min) {
		            min=d; n=i;
		          }
		        }*/ 
		        d=min; 
		        StepSize1();
		        
		       
		       // System.out.println("RRT :333"); 
			 
		        // maximal mit der entfernung delta springen
		        if (delta<d) d=delta;

		        // neuen knoten berechnen
		        x[c]=x[n]+(int)(d*(rx-x[n])/min);
		        y[c]=y[n]+(int)(d*(ry-y[n])/min);

		    //    System.out.println("RRT :444"); 
		        // nochmal prüfen ob der neue knoten nicht in ein hindernis fällt
		        if (!linehit(x[n], y[n], x[c],y[c])) {
		          
		        //	 System.out.println("RRT :555"); 
		        	from [c] = n;
		          
		          is.SetTP(new TransitionPoint(x[c], y[c], "TP"+Integer.toString(c)));
		          int mesafe = (int) CalcDistance(x[n], y[n], x[c],y[c]); 
		          //	System.out.println("RRT : n : " + Integer.toString(n) +" c : "+ Integer.toString(c) + " From ["+Integer.toString(x[n]) +";"+ Integer.toString(y[n])+"]  - To ["+Integer.toString(x[c])+";"+Integer.toString(y[c]) + "] mesafe : "+ Integer.toString(mesafe)); 
		          is.addLane("x", n, c, mesafe);
		          is.addLane("x", c, n, mesafe);
		          c++;
		        }
		        cCount = c;
				 
		     //  System.out.println("RRT :666 ... c: "+c); 
		         // durdurma
		         TP_closest_start = is.Closest(start);
				 TP_closest_end = is.Closest(end);	
				 if (TP_closest_start!=null) {System.out.println ("Closest start> "+TP_closest_start.xLoc+" "+TP_closest_start.yLoc+" "+TP_closest_start.tpname);}
				 if (TP_closest_end!=null) {System.out.println ("Closest end> "+TP_closest_end.xLoc+" "+TP_closest_end.yLoc+" "+TP_closest_end.tpname);}
				 if (TP_closest_start!=null && TP_closest_end!= null) 
				 {
					// System.out.println("RRT :777"); 
					// System.out.println("RRT :777"); 
					// System.out.println("RRT CALC vertices count : "+is.GetTPCount()); 
					// System.out.println("RRT CALC edges count : "+is.GetLaneCount()); 
					 //is.LaneStructure(); 
					 retIS =  is; 
				//	return is.ShortestPathLength(TP_closest_start, TP_closest_end)+ 
						//	(int) CalcDistance(start.xLoc, start.yLoc, TP_closest_start.xLoc, TP_closest_start.yLoc) +  
					//		(int) CalcDistance(end.xLoc, end.yLoc, TP_closest_end.xLoc, TP_closest_end.yLoc);  
					 c = MAX_ITERATIONS; 
					 
				 }
				// System.out.println("RRT :888"); 

		 }
		 System.out.println("RRT :999"); 
		 System.out.println("RRT CALC vertices count : "+is.GetTPCount()); 
		 System.out.println("RRT CALC edges count : "+is.GetLaneCount()); 
		// return 3000; 
		}
		 return retIS;
		  
		
		
	}
	

	
	public int GetTPIndex (TransitionPoint [] tl, int count, String tpName) 
	{
		for (int i=0; i<count; i++) 
		{
			if (tl[i].tpname.compareTo(tpName) == 0) 
				return i; 
		}
		
		return -1; 
	}
	public String GetTPName (TransitionPoint [] tl, int count,  int index) 
	{
		for (int i=0; i<count; i++) 
		{
			if (i == index) 
				return tl[i].tpname; 
		}
		return ""; 
	}
	
	public boolean VisibilityObtained(TransitionPoint [] tl, int count1, TransitionPoint [] tl2, int count2, TransitionPoint [] TP3, int tot) 
	{
		
		
		for (int i=0; i<count1; i++) 
		{
			TransitionPoint tp1 = tl[i]; 
			WallNode wi = new WallNode(tp1.xLoc, tp1.yLoc) ;
			
			for (int j=0; j<count2; j++) 
			{
				TransitionPoint tp2 = tl2[j];
				
				WallNode wj = new WallNode(tp2.xLoc, tp2.yLoc) ;
				
				Wall ij = new Wall (wi, wj); 
				
				if (!is.InterSection(ij) && CalcDistance(tp1.xLoc, tp1.yLoc, tp2.xLoc, tp2.yLoc)<=delta) 
				{
					
					   int mesafe2 = (int) CalcDistance(tp1.xLoc, tp1.yLoc, tp2.xLoc, tp2.yLoc); 
			          int qn = GetTPIndex(TP3, tot+1, tp1.tpname); 
			          int qc = GetTPIndex(TP3, tot+1, tp2.tpname);   
					  is.addLane("x", qn, qc, mesafe2);
			          is.addLane("x", qc, qn, mesafe2);
					
					return true; 
				}
				
			}
			
			
		}
		
		return false;  
		
	}
	
	public void RandomStartPoints () 
	{
	      do {
	          rx=(int)(Math.random()*TWIDTH);
	          ry=(int)(Math.random()*THEIGHT);
	          rx2=(int)(Math.random()*TWIDTH);
	          ry2=(int)(Math.random()*THEIGHT);
	        } while(hit(rx,ry) && hit(rx2,ry2));
	}
	
	public void StepSize1 () 
	{
        for (int i=0;i<c;i++) {
	          d=Math.sqrt((x[i]-rx)*(x[i]-rx)+(y[i]-ry)*(y[i]-ry));
	          if (min==-1 || d<min) {
	            min=d; n=i;
	          }
	        }
	}
	public void StepSize2() 
	{
        for (int i=0;i<c2;i++) {
	          d2=Math.sqrt((x2[i]-rx2)*(x2[i]-rx2)+(y2[i]-ry2)*(y2[i]-ry2));
	          if (min2==-1 || d2<min2) {
	            min2=d2; n2=i;
	          }
	        }
	}
	public IndoorStructure BuildRRT_BiDirect(TransitionPoint start, TransitionPoint end) 
	{
		 
		is.nodes = new ArrayList<Vertex>();
		is.edges = new ArrayList<Edge>();  
		is.tpoints = new ArrayList<TransitionPoint>();
		 x[0]= start.xLoc; y[0]=start.yLoc;
		 x2[0]= end.xLoc;  y2[0]=end.yLoc;
		 int tot = 0; 
		 TransitionPoint [] TP1 = new TransitionPoint [MAX_ITERATIONS] ; 
		 TransitionPoint [] TP2 = new TransitionPoint [MAX_ITERATIONS] ; 
		 TransitionPoint [] TP3 = new TransitionPoint [MAX_ITERATIONS] ; 
		 TransitionPoint tp1 =  new TransitionPoint(x[c], y[c], "TPc"+Integer.toString(c)); 
		 TransitionPoint tp2 =  new TransitionPoint(x2[c2], y2[c2], "TP2c"+Integer.toString(c2)); 
		 is.SetTP(tp1);
		 is.SetTP(tp2);
		 TP1[0] = tp1; 
		 TP2[0] = tp2; 
		 TP3[tot++] = tp1; 
		 TP3[tot++] = tp2; 
		 
		 
	
		

		 
		 c= 1;  
		 c2=1; 
		 
		 
		
	//	 int color;
	//	 int color2;
		 // while (c<maxC) 
		 while (!VisibilityObtained(TP1, c, TP2, c2, TP3, tot))
		 {
			 System.out.println("RRT while");
		    
		        RandomStartPoints();
		        
		        // den knoten mit der geringsten distanz zum punkt suchen
		        min=-1;
		        min2=-1; 
		      StepSize1(); 
		      StepSize2(); 
		        d=min; 
		        d2=min2; 
			 
		        // maximal mit der entfernung delta springen
		        if (delta<d) d=delta;
		        
		        if (delta2<d2) d2=delta2;

		        // neuen knoten berechnen
		        x[c]=x[n]+(int)(d*(rx-x[n])/min);
		        y[c]=y[n]+(int)(d*(ry-y[n])/min);
		        
		        // neuen knoten berechnen
		        x2[c2]=x2[n2]+(int)(d2*(rx2-x2[n2])/min2);
		        y2[c2]=y2[n2]+(int)(d2*(ry2-y2[n2])/min2);

		        // das grün etwas dunkler machen um so weiter der baum fortgeschritten ist
		       // color=255-120*c/maxC;
		       // color2=255-120*c2/maxC2;

		        // nochmal prüfen ob der neue knoten nicht in ein hindernis fällt
		        if (!linehit(x[n], y[n], x[c],y[c])) {
		        	 System.out.println("RRT linehit");
		      //  	g.setColor(new Color(color/2,color,color/2));
		       //   g.drawLine(x[n]+HOR_OFFSET,y[n]+VER_OFFSET,x[c]+HOR_OFFSET,y[c]+VER_OFFSET);
		          
		          from [c] = n;
		          TransitionPoint tpc = new TransitionPoint(x[c], y[c], "TPc"+Integer.toString(c)); 
		          is.SetTP(tpc);
		          TP1[c] = tpc; 
		          TP3[tot++] = tpc; 
		          int mesafe = (int) CalcDistance(x[n], y[n], x[c],y[c]); 
		          	System.out.println("RRT : n : " + Integer.toString(n) +" c : "+ Integer.toString(c) + " From ["+Integer.toString(x[n]) +";"+ Integer.toString(y[n])+"]  - To ["+Integer.toString(x[c])+";"+Integer.toString(y[c]) + "] mesafe : "+ Integer.toString(mesafe)); 
		          
		          String nName = GetTPName (TP1, c, n); 	
		          String cName = GetTPName (TP1, c+1, c); 
		          int qn = GetTPIndex(TP3, tot, nName); 
		          int qc = GetTPIndex(TP3, tot, cName); 
		          is.addLane("x", qn, qc, mesafe);
		          is.addLane("x", qc, qn, mesafe);
		          c++;
		        }
		        
		        if (!linehit(x2[n2], y2[n2], x2[c2],y2[c2])) {
		        	 System.out.println("RRT linehit 2");
		 //       	g.setColor(new Color(color2/2,color2,color2/2));
		  //        g.drawLine(x2[n2]+HOR_OFFSET,y2[n2]+VER_OFFSET,x2[c2]+HOR_OFFSET,y2[c2]+VER_OFFSET);
		          
		          from2 [c2] = n2;
		          TransitionPoint tpc2 = new TransitionPoint(x2[c2], y2[c2], "TP2c"+Integer.toString(c2)); 
		          is.SetTP(tpc2);
		          TP2[c2] = tpc2; 
		          TP3[tot++] = tpc2; 
		          int mesafe2 = (int) CalcDistance(x2[n2], y2[n2], x2[c2],y2[c2]); 
		          	System.out.println("RRT : n2 : " + Integer.toString(n2) +" c2 : "+ Integer.toString(c2) + " From2 ["+Integer.toString(x2[n2]) +";"+ Integer.toString(y2[n2])+"]  - To2 ["+Integer.toString(x2[c2])+";"+Integer.toString(y2[c2]) + "] mesafe2 : "+ Integer.toString(mesafe2)); 
			         
		          	String nName = GetTPName (TP2, c2, n2); 	
			          String cName = GetTPName (TP2, c2+1, c2); 
			          int qn = GetTPIndex(TP3, tot, nName); 
			          int qc = GetTPIndex(TP3, tot, cName); 
			          is.addLane("x", qn, qc, mesafe2);
			          is.addLane("x", qc, qn, mesafe2);
		       //   is.addLane("x2",n2, c2, mesafe2);
		       //   is.addLane("x2", c2, n2, mesafe2);
		          c2++;
		        }
		        
		        
		        cCount = c;
		        cCount2 = c2;
		        
		       // if (VisibilityObtained(TP1, c, TP2, c2)) 
		        
		        
				 /*TransitionPoint TP_closest_robot = is.Closest(new TransitionPoint(new Task(370, 280,"Start")));
				 TransitionPoint TP_closest_task = is.Closest(new TransitionPoint(new Task(180, 280,"End")));	
				 if (TP_closest_robot!=null && TP_closest_task!= null)*/  
				/* {
					
					 c = maxC; 
					 
				 }*/
				  
		 }
		 is.ListTPs(); 
		 
		return is;
		
		
		
		
		
		
	}
	
}
