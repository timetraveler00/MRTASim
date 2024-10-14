package MRTAStreamManager;
	import java.util.ArrayList;

import MRTAmain.HRectangle;
import MRTAmain.Station;
import MRTAmain.StationSet;
public class StationReader extends MRTAFileReader {
	
	    int stepCount = 0; 	
	
		protected ArrayList<Station> stationList = new ArrayList<Station>(); 
	    protected void CreateStationArray () 
	    {
            
	    	
	    	for (int i=0; i<LineCount(); i++) 
	    	{
	    		
	    		Station st = new Station (Integer.parseInt(Get(i, 1)), Integer.parseInt(Get(i, 2)), Get (i,0));
                //System.out.println("i   : " + i );   
	    		// Follow me mode 
	    		// elle eklenmi� istasyon olup olmad��� kontrol ediliyor. 
	    		// Sadece en sonuncu node kontrol ediliyor. 
	    		if (Get(i,7)!=null) 
	    		{
	    			HRectangle rect1 = new HRectangle (Integer.parseInt(Get(i,4)), Integer.parseInt(Get(i,5)), Integer.parseInt(Get(i,6)), Integer.parseInt(Get(i,7)));
	    			st.rect = rect1;
	    		}
	    		else 
	    		{
	    			 int respRadius =  100 + stepCount * 40; 
	    			int xmin =  st.xLoc-respRadius/2; //ss.stations[stationCount].xLoc-respRadius/2>=0 ? ss.stations[stationCount].xLoc-respRadius/2 : 0; 
					int xmax = st.xLoc+respRadius/2 ;//ss.stations[stationCount].xLoc+respRadius/2<TERRAIN_WIDTH ? ss.stations[stationCount].xLoc+respRadius/2 : TERRAIN_WIDTH-10;
					int ymin = st.yLoc-respRadius/2 ; //ss.stations[stationCount].yLoc-respRadius/2>=0 ? ss.stations[stationCount].yLoc-respRadius/2 : 0;
					int ymax = st.yLoc+respRadius/2;//ss.stations[stationCount].yLoc+respRadius/2<TERRAIN_HEIGHT ? ss.stations[stationCount].yLoc+respRadius/2 : TERRAIN_HEIGHT-10;
					
					HRectangle rect1   = new HRectangle (xmin, ymin, xmax, ymax);
					st.rect = rect1;
	
	    		}
	    		
	    		stationList.add(st);   		
	 
	    	}
	    }
	    
	    public Station GetStation (int index) 
	    {
	    	return stationList.get(index); 
	    }
	    
	    public int StationCount () 
	    {
	    	return stationList.size(); 
	    }
	    
	    public Station[] GetStationList () 
	    {
	    	Station [] stations = new Station[StationCount()]; 
	    	for (int i=0; i<StationCount(); i++) 
	    		stations[i] = GetStation(i); 
	    	return stations; 
	    }
	    
	    public StationSet GetStationSet () 
	    {
	    	StationSet stations = new StationSet (StationCount());
	    	
	    
	    	for (int i=0; i<StationCount(); i++)
	    	{
	    		Station s = GetStation(i);
	    		stations.stations[i] = s; 
	    		stations.stCoor[i][0] = s.xLoc; 
	    		stations.stCoor[i][1] = s.yLoc;
	    		
	    		
	    	}
	    	return stations; 
	    }

		// Generic StationReader	
	    public StationReader(String template) {
				
				this(template,"Stations.txt"); 
				
			}
			
			// StationsAllReader
			public StationReader(String template, String fileName) {
			
				
				this (template, "\\config\\", fileName); 
				
			}
			
			// FMStationReader
			public StationReader(String template, String folderpath, String filename) {
				 
				this (template, folderpath, filename, 0); 
				
			}
			
			// AUStattionReader
			public StationReader(String template, String folderpath, String filename, int step) {
				
				super();
				delimiter ="_";
				folderPath = folderpath;
				fileName = filename; 
				templateName = template; 
				stepCount = step; 
				
				
				FromFileToArray();
				CreateStationArray(); 
				
			}

		}
