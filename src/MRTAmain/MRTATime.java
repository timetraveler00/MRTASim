package MRTAmain;
import java.util.Calendar;
import java.util.Date;


public class MRTATime {

	
	public void PrintWallWatch ()
    {
		
			Date dtt = Calendar.getInstance().getTime(); 
			
			@SuppressWarnings("deprecation")
			int lDateHours =dtt.getHours(); 
		@SuppressWarnings("deprecation")
		int lDateMinutes =dtt.getMinutes(); 
		@SuppressWarnings("deprecation")
		int lDateSeconds = dtt.getSeconds();
       	System.out.println (" *** DS =  "+lDateHours +":"+lDateMinutes+":"+lDateSeconds+" ***");
       	
    }
}
