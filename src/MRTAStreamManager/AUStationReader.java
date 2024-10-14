package MRTAStreamManager;

import MRTAmain.Station;
import MRTAmain.StationSet;
	
public class AUStationReader extends StationReader {
	
	 StationSet ss ; //  = new StationSet(size); 
	 int setSize ;
	
	public StationSet GetStationSet () 
	{
		ss = new StationSet(setSize); 
		for (int i=0; i<setSize; i++) 
		{
			Station st = GetStation(i);
			ss.stations[i] = st; 
			ss.stCoor[i][0] = st.xLoc; 
			ss.stCoor[i][1] = st.yLoc; 
		}
        return ss;  		
	}
	
	public AUStationReader(String template, int size, int index, int stepCount) {
		  
		      super(template, "\\Experiments\\", "\\StationSets\\StationSet"+size+"_"+index+".txt",stepCount);
		      setSize = size; 
		      
		      
		     
			}
			
			

		}