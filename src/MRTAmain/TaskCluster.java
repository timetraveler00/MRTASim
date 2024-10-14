package MRTAmain;



public class TaskCluster {

	public int taskCount; 
	public int stationCount =0; 
	public int peakCount = 0 ; 
	public ColorSet peakPoints[] = new ColorSet [20]; 
	public Station peakStations [] = new Station[20]; 
	public int index ; 
	public Task tasks [] = new Task [100] ; 
    public HRectangle rect = new HRectangle(9999,9999,0,0); 
    

    
	public void AddTask (Task t) 
	{
		tasks [taskCount++] = t; 
	}
	
	

}
