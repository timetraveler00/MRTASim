package MRTAmain;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.sazonov.fuzzy.engine.EvaluationException;
import org.sazonov.fuzzy.engine.Factory;
import org.sazonov.fuzzy.engine.FuzzyState;
import org.sazonov.fuzzy.engine.LinguisticVariable;
import org.sazonov.fuzzy.engine.NoRulesFiredException;
import org.sazonov.fuzzy.engine.RuleBlock;


public class SazanovFuzzy {

	FuzzyState fuzzyState = null;
    RuleBlock fuzzyValuationRules = null;
    RuleBlock fuzzyPricingRules = null;
    LinguisticVariable TaskCount = null;
     LinguisticVariable CurrentPath = null;
     LinguisticVariable Inclusion = null;
     LinguisticVariable PriceRating = null;
     LinguisticVariable BidValue = null;
     Factory factory = Factory.getFactory();
     
     File f; 
     FileInputStream fstream; 
     DataInputStream in; 
     BufferedReader br ; 
     
	public void InitializeFuzzyMemberships() throws EvaluationException
	{
            fuzzyState = factory.makeFuzzyState();
	        TaskCount = factory.makeLinguisticVariable("TaskCount");
	        TaskCount.add(factory.makeMemberFunction("toofew", -1, -1, 0, 2 ));
	        TaskCount.add(factory.makeMemberFunction("few", 1, 2, 3, 4 ));
	        TaskCount.add(factory.makeMemberFunction("medium", 3, 4, 5, 6));
	        TaskCount.add(factory.makeMemberFunction("many", 5, 6, 8, 9));
	        TaskCount.add(factory.makeMemberFunction("toomany", 8, 9 , 100, 100 ));
	      
	   
	        PriceRating = factory.makeLinguisticVariable("PriceRating");
	        PriceRating.add(factory.makeMemberFunction("toolow", -1, -1, 0, 5.0));
	        PriceRating.add(factory.makeMemberFunction("low", 0, 5, 10, 15.0));
	        PriceRating.add(factory.makeMemberFunction("medium", 10, 15, 30, 40.0));
	        PriceRating.add(factory.makeMemberFunction("high", 30, 40 , 60, 80.0));
	        PriceRating.add(factory.makeMemberFunction("toohigh", 60, 80, 100000, 100000));
	       
	        
	        BidValue = factory.makeLinguisticVariable("BidValue");
	        BidValue.add(factory.makeMemberFunction("absolutely_rejected", 0, 50, 50.0, 100.0));
	        BidValue.add(factory.makeMemberFunction("close_to_be_rejected", 50 ,100, 100, 150.0));
	        BidValue.add(factory.makeMemberFunction("to_be_considered", 100, 150, 150, 200.0));
	        BidValue.add(factory.makeMemberFunction("close_to_be_accepted", 150, 200, 200, 250.0));
	        BidValue.add(factory.makeMemberFunction("absolutely_accepted", 200, 250, 250, 300));
	             
	        
	        fuzzyState.register(TaskCount);
	        fuzzyState.register(PriceRating );
	        fuzzyState.register(BidValue);
	     
	}	
	public void LoadValuationRules () 
	
	{
		 try{ 
	         f = new File(".");
	         
	 		try {
			   fstream = new FileInputStream(f.getAbsolutePath() + "\\..\\fuzzy\\Valuation.txt");
			  System.out.println("Bid file opened....");
			  // Get the object of DataInputStream
			  try {
			  
			  in = new DataInputStream(fstream);
			  System.out.println("stream....");
			   br = new BufferedReader(new InputStreamReader(in));
			  System.out.println("reader...");
	        
			  		try {
			  				//fuzzyRules = fuzzyState.createRuleExecutionSet(new java.io.StringReader("if LaneCount is high or LaneCount is medium then Selection is Shortest\nif LaneCount is few then Selection is ClosestTP\n"));
			  			fuzzyValuationRules = fuzzyState.createRuleExecutionSet(br);
			  		} 
			  		catch (Throwable throwable) {
			  			System.err.println("Create Rule Execution Set exception: " + throwable.getMessage());
			  			throwable.printStackTrace(); 
			  		}	   
	        
			  } catch (Exception ex)
			  {
				  System.err.println("DataInputStream: " + ex.getMessage());
				  ex.printStackTrace(); 
			  }
	 		}
			  catch (Exception ex2)
			  {
				  System.err.println("FileInputStream: " + ex2.getMessage());
				  ex2.printStackTrace(); 
			  }
	       
	      } 

          catch (Exception e){//Catch exception if any
        	  System.err.println("File Error: " + e.getMessage());
        	  e.printStackTrace(); 
				  }

	}
	
public void LoadPricingRules () 
	
	{
		 try{ 
	         f = new File(".");
	         
	 		try {
			   fstream = new FileInputStream(f.getAbsolutePath() + "\\..\\fuzzy\\Pricing.txt");
			  System.out.println("Bid file opened....");
			  // Get the object of DataInputStream
			  try {
			  
			  in = new DataInputStream(fstream);
			  System.out.println("stream....");
			   br = new BufferedReader(new InputStreamReader(in));
			  System.out.println("reader...");
	        
			  		try {
			  				//fuzzyRules = fuzzyState.createRuleExecutionSet(new java.io.StringReader("if LaneCount is high or LaneCount is medium then Selection is Shortest\nif LaneCount is few then Selection is ClosestTP\n"));
			  			fuzzyPricingRules = fuzzyState.createRuleExecutionSet(br);
			  		} 
			  		catch (Throwable throwable) {
			  			System.err.println("Create Rule Execution Set exception: " + throwable.getMessage());
			  			throwable.printStackTrace(); 
			  		}	   
	        
			  } catch (Exception ex)
			  {
				  System.err.println("DataInputStream: " + ex.getMessage());
				  ex.printStackTrace(); 
			  }
	 		}
			  catch (Exception ex2)
			  {
				  System.err.println("FileInputStream: " + ex2.getMessage());
				  ex2.printStackTrace(); 
			  }
	       
	      } 

          catch (Exception e){//Catch exception if any
        	  System.err.println("File Error: " + e.getMessage());
        	  e.printStackTrace(); 
				  }

	}

	
	
	public double PricingFuzzy (double taskCount, double current, double inclusion, double price) throws EvaluationException
	{
		    double biddecision = 0; 
	        System.out.println("Bidvaluator inputs : "+taskCount + " > "+ inclusion + " > "+ price);
	        try{ 
		        
	        	if (price > inclusion) 
	        	{
	      		 System.out.println("Price > Inclusion");
		        TaskCount.setInputValue(taskCount);
		        System.out.println("Task Count is set");
		        
		        double pricerating = ((price-inclusion)*100/price )>=0 ? ((price-inclusion)*100/price  ) : 0; 
		        System.out.println("Price Rating : " +  pricerating);
		        PriceRating.setInputValue (pricerating ); 
		        try{ 
		               fuzzyPricingRules.executeRules();
				}   
		        catch (Exception e){//Catch exception if any
					  System.err.println("Execute Rules Exception: " + e.getMessage());
					  }
		        try {
					biddecision = BidValue.defuzzify(); 
		        	System.out.println("Price  : " + biddecision );
					
				} catch (NoRulesFiredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
	        	} 
	        	else 
	        	{
	        		biddecision = 0; 
	        	}
		        
		      } 
	          catch (Exception e){//Catch exception if any
					  System.err.println("BidValuatorFuzzy Exception : " + e.getMessage());
					  }
	     return biddecision; 
      
	}
	public double BidValuatorFuzzy (double taskCount, double current, double inclusion, double price) throws EvaluationException
	{
		    double biddecision = 0; 
	        System.out.println("Bidvaluator inputs : "+taskCount + " > "+ inclusion + " > "+ price);
	        try{ 
		        
	        	if (price > inclusion) 
	        	{
	      		 System.out.println("Price > Inclusion");
		        TaskCount.setInputValue(taskCount);
		        System.out.println("Task Count is set");
		        
		        double pricerating = ((price-inclusion)*100/price )>=0 ? ((price-inclusion)*100/price  ) : 0; 
		        System.out.println("Price Rating : " +  pricerating);
		        PriceRating.setInputValue (pricerating ); 
		        try{ 
		               fuzzyValuationRules.executeRules();
				}   
		        catch (Exception e){//Catch exception if any
					  System.err.println("Execute Rules Exception: " + e.getMessage());
					  }
		        try {
					biddecision = BidValue.defuzzify(); 
		        	System.out.println("BidValue : " + biddecision );
					
				} catch (NoRulesFiredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
	        	} 
	        	else 
	        	{
	        		biddecision = 0; 
	        	}
		        
		      } 
	          catch (Exception e){//Catch exception if any
					  System.err.println("BidValuatorFuzzy Exception : " + e.getMessage());
					  }
	     return biddecision; 
      
	}
}
