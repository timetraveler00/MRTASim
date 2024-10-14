package MRTAmain;

public class MessageBuffer {

	private int buffSize; 
	private String [] messageBuffer ;  
    
	public MessageBuffer (int size)
	{
		buffSize = size; 
		messageBuffer = new String [buffSize+1];  
	}
	
	public void NewMessage (String message) 
    {
    	for (int i=buffSize-1;i>=0; i--)
    		
    	{
    		if (messageBuffer[i]!=null)
    		 messageBuffer[i+1] = messageBuffer[i] ; 
    	}
    	messageBuffer[0] = message; 
    	
    }
	
	public String GetMessage(int index)
	{
		
		if (index>=buffSize)
			return null; 
		return messageBuffer[index];  
			 
	}
	
	
	public void SetBufferSize (int size)
	{
		buffSize = size; 
	}
	
}
