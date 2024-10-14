package MRTAmain;

public class IOLogger {
	
	public String sourceEntity; 
	private int autoMode = 0; 
	public IOLogger (String source, int autom) 
	{
		sourceEntity = source; 
		autoMode = autom; 
	}

	 public void Mesaj (String st) 
		{
			
		 
		 if (autoMode == 0) 
		 {
		     System.out.println("*");
			 System.out.println("******************");
			 System.out.println("**************************************");
			 System.out.println("*************************************************************************");
			 System.out.println(sourceEntity+"> " + st);
			 System.out.println("*************************************************************************");
			 System.out.println("**************************************");
			 System.out.println("******************");
			 System.out.println("*");
		 } 
		}
		
		public void KisaMesaj (String st) 
		{
			 
		/*	 if (autoMode == 0) 
			 {
				 	System.out.println(sourceEntity+"> " + st);
			 }*/ 	 
		}
		public void Yaz(String st) 
		{
			 
		//	if (autoMode == 0)  
				System.out.println(st);
			 	 
		}
	
}
