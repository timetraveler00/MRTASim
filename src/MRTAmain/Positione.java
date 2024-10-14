package MRTAmain;

public class Positione {
	double x; 
	double y; 
	

	public Positione(double xx, double yy) 
	{
		x = xx; 
		y = yy; 
	}
	
	public double distance (Positione p1) 
	{
		double dist = Math.sqrt((p1.x-this.x)*(p1.x-this.x)+(p1.y-this.y)*(p1.y-this.y)); 
		//System.out.println("actual : ["+p1.x+"-"+p1.y+"] kesen ["+this.x+"-"+this.y+"] distance : "+dist);
		return dist; 
	}
}
