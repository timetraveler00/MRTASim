package MRTAStreamManager;
	
public class FMStationReader extends StationReader {
	
	  public FMStationReader(String template, int stCount, int stepCount) {
		  
				super(template, "\\Experiments\\", "\\StationSets\\StationSet"+Integer.toString(stCount)+"_"+Integer.toString(stepCount) +".txt");
				
			}
			
			

		}