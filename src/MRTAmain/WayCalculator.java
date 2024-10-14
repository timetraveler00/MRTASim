package MRTAmain;
import IndoorStructure.IndoorStructure;


public class WayCalculator {

	
	
		
	public WayCalculator() {
		// TODO Auto-generated constructor stub
		
		 
	} 
	
	public double PhysicalWay (double plWay, double physicalWidth, double terrainWidth)  
	{
		double pixelWay; 
		
		if (plWay>0) 
	    {
	    	pixelWay = plWay;  
	    	double scale = (double) ((double) (physicalWidth * 1.0)) / ((double) (terrainWidth * 1.0) );
	    	double way = (double) (scale * (double) (pixelWay * 1.0));
	         return way ;
	         
	    } 
	    else return 0; 
	     
	}
	
	 
	
}
