package MRTAmain;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DateTimeStr {

	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	public String DtString() 
	{
		
    	
    	Calendar cal = Calendar.getInstance();
    	String dateString = dateFormat.format(cal.getTime());
    	return dateString;
	}
	public String DtString(String format) 
	{
		
    	dateFormat = new SimpleDateFormat(format);
    	Calendar cal = Calendar.getInstance();
    	String dateString = dateFormat.format(cal.getTime());
    	return dateString;
	}
}
