package IndoorStructure;
import MRTAmain.Task;



public class TransitionPoint {

	public int xLoc; 
	public int yLoc; 
	public String tpname; 
	
	public TransitionPoint (int xl, int yl, String n) 
	{
		xLoc = xl; 
		yLoc = yl; 
		tpname = n; 
		
	}
	public TransitionPoint (WallNode wn, String n) 
	{
		xLoc = wn.xLoc; 
		yLoc = wn.yLoc; 
		tpname = n; 
		
	}
	
	public TransitionPoint (Task t) 
	{
		xLoc = t.xLoc; 
		yLoc = t.yLoc;
		tpname = t.taskName; 
		
	}
	public String toString () 
	{
		return "("+tpname + " , " + xLoc + " , "+yLoc+" )"; 		
	}
	
	public int GetDistance(TransitionPoint tm) 
	{
		int x_fark = xLoc-tm.xLoc; 
		int y_fark = yLoc-tm.yLoc; 
		/*System.out.println(this.name + "_" + tm.name + " xloc " +  Integer.toString(xLoc) + " tm.xloc " +  Integer.toString(tm.xLoc) ) ; 
		System.out.println(this.name + "_" + tm.name + " yloc " +  Integer.toString(yLoc) + " tm.yloc " +  Integer.toString(tm.yLoc) ) ; 
		System.out.println(this.name + "_" + tm.name + " x_fark " +  Integer.toString(x_fark)) ; 
		System.out.println(this.name + "_" + tm.name + " x_fark " +  Integer.toString(x_fark)) ; 
		
		System.out.println(this.name + "_" + tm.name + " y_fark " +  Integer.toString(y_fark)) ; 
		*/
		return (int) Math.sqrt( Math.pow(x_fark, 2) +  Math.pow(y_fark, 2)) ; 
		
		
		
		 
	}
}