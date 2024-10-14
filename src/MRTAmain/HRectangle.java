package MRTAmain;

	public class HRectangle {
		
		public int x1 = 2000; 
		public int y1 = 2000; 
		public int x2 = 0; 
		public int y2 = 0; 
		
		public HRectangle (int xx1, int yy1, int xx2, int yy2) 
		{
			
			x1 = xx1; 
			y1 = yy1; 
			x2 = xx2; 
			y2 = yy2; 
			
		}
		
		public boolean Contains ( Task t) 
		{
			boolean retVal = false; 
			if (t.xLoc > x1 && t.xLoc < x2 && t.yLoc > y1 && t.yLoc <y2)
				retVal =  true;
			
			return retVal; 
		}
		

	}