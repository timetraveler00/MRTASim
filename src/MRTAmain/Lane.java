package MRTAmain;

public class Lane {
	public String laneName; 
	public int startPoint; 
	public int endPoint; 
	public int distance; 
	
	public Lane (String lName, int sPoint, int ePoint, int dist) 
	{
		laneName = "Edge_"+Integer.toString(sPoint)+"_"+Integer.toString(ePoint);
		startPoint = sPoint; 
		endPoint = ePoint; 
		distance = dist; 
	}
	
}
