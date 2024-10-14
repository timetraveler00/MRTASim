package MRTAmain;

import Enums.eTradingProgress;

public class Trade {
	public String Seller; 
	public String Buyer; 
	public String TBundle; 
    public int price; 
    public int bids[] = new int [100];
    public String bidders[] = new String[100]; 
    
    
    
    /*
	 * status 
	 * 0 : not created
	 * 1 : announced
	 * 2 : completed, unsuccessful
	 * 3: completed, successful
	 *  
	 * */
	public eTradingProgress status;

}
