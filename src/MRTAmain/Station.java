package MRTAmain;


public class Station extends Task {


	  public HRectangle rect = new HRectangle(0,0,0,0);  
	
	public Station () 
	{
		System.out.println("Station created. "); 
		rect = new HRectangle(0,0,0,0); 
	}
	
	 public Station(int xl, int yl, String sName) 
     {
    	 this();
		 xLoc = xl; 
    	 yLoc = yl; 
    	 taskName = sName;
    	  

     }
}
