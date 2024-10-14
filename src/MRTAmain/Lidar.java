package MRTAmain;

import IndoorStructure.IndoorStructure;
import IndoorStructure.Wall;

public final class Lidar {
	
	public double range = 18.0;
	public int numberofSectors = 24;
	private double sectorSlide = 360.0/ numberofSectors; 
	
	public int windowSize =3; 
	
	public double [] proximityVector = new double [numberofSectors+ (windowSize/2)*2];
	public double [] meanProximities = new double [numberofSectors ];
	IndoorStructure localMap ; 
	Robotum myCarrier ;
	
	
	public Lidar(IndoorStructure localMap, Robotum myCarrier) {
		super();
		this.localMap = localMap;
		this.myCarrier = myCarrier;
	} 
	
	public void scan (Robotum r) 
	{
		myCarrier = r; 
		
		for (int i=0; i<numberofSectors+(windowSize/2)*2; i++) 
		{
			proximityVector[i] = 0; 
		}
		for (int i=0; i<numberofSectors; i++) 
		{
			proximityVector[i+ (windowSize/2)] = checkProximity(i); 
			
	//		System.out.println("****      " + i +  ". proximity : " + proximityVector[i]);
		}
	/*	for (int i=0; i<numberofSectors/2; i++) 
		{
			
			if (proximityVector[i+ (windowSize/2) ] <= range*0.5 && proximityVector[i+ (windowSize/2) + numberofSectors/2] <= range*0.5)
			{
			   // no change 			    
			}    
			else if (proximityVector[i+ (windowSize/2) ] <= range*0.5 && proximityVector[i+ (windowSize/2) + numberofSectors/2] > range*0.5)
			{
				proximityVector[i+ (windowSize/2) + numberofSectors/2] +=  proximityVector[i+ (windowSize/2) ];    
			}   
			else if (proximityVector[i+ (windowSize/2) + numberofSectors/2 ] <= range*0.5 && proximityVector[i+ (windowSize/2) ] > range*0.5)
			{
				proximityVector[i+ (windowSize/2) ] +=  proximityVector[i+ (windowSize/2) + numberofSectors/2];    
			} 
			else if (proximityVector[i+ (windowSize/2) + numberofSectors/2 ] > range*0.5 && proximityVector[i+ (windowSize/2) ] > range*0.5)
			{
				proximityVector[i+ (windowSize/2) ] +=  proximityVector[i+ (windowSize/2) + numberofSectors/2];    
				proximityVector[i+ (windowSize/2) + numberofSectors/2] +=  proximityVector[i+ (windowSize/2) ];    
			} 
			else 
			{
				//no change
			}
			
			
	//		System.out.println("****      " + i +  ". proximity : " + proximityVector[i]);
		}
		
		*/
		for (int i=0; i<windowSize/2; i++) 
		{
			proximityVector[i] = proximityVector[numberofSectors-(windowSize/2)+i]; 
			proximityVector[numberofSectors+windowSize/2+i] = proximityVector[(windowSize/2)+i]; 
		}
		/*for (int i=0; i<numberofSectors+(windowSize/2)*2; i++) 
		{
			System.out.println("****      " + i +  ". VECTOR : " + proximityVector[i]);
		}*/
		
		for (int i=0; i<numberofSectors; i++) 
		{
			int windowSum = 0; 
			for (int j=0;j<windowSize; j++) 
			{		
			   windowSum+= proximityVector[i+j]; 
			} 
			meanProximities[i] = windowSum/windowSize*1.0; 

		//	System.out.println("****      " + i +  ". MEAN : " + meanProximities[i]);
		}
		
	}
	
	public double checkProximity (int order) 
	{
		double freeRange = range; 
		double face = (order*360.0)/(numberofSectors*1.0);  
		double t = (face) * Math.PI/180.0;
		
		while (freeRange > 0.0) 
		{
			
			 int x = (int) Math.round(myCarrier.xLoc + freeRange * Math.cos(t));
	         int y = (int) Math.round(myCarrier.yLoc + freeRange * Math.sin(t));
			 
			Wall w = new Wall (myCarrier.xLoc, myCarrier.yLoc, x, y);
			//System.out.println();
			// haritada bir engelle kesiþme varsa range'i kademeli olarak azalt 
			//System.out.println("face: "+face + " t(radian): " + t + " x: "+x + " y: "+y + " freeRange: " + freeRange);
			
			if (localMap.InterSection(w)) 
			{
				freeRange= freeRange  - 1.0;
			}
			else 
				break; 
				
		}
		return (double) freeRange;
	}
	
	
	
	

}
