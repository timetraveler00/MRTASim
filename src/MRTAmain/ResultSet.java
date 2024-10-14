package MRTAmain;


import java.util.ArrayList;
import MRTAStreamManager.MRTAFileWriter;




public class ResultSet {
		
	    public int maxSetCount = 100;
	    public int maxAllCount = 10000;
	    public int allCount = 0;
	    
	    
	  	public ExperimentResult set[] = new ExperimentResult[maxSetCount];
	  	public ExperimentResult all[] = new ExperimentResult[maxAllCount];
		public int setCount =0; 
		
		String creationTime; 
		
		

		
		public ResultSet (int ms) 
		{
			maxSetCount = ms; 
			DateTimeStr dt=new DateTimeStr(); 
			creationTime = dt.DtString();
			 
		}
		
		public  int MaxInTheSet ()
		{
			int maxx = 0; 
			int maxi = -1; 
			for (int i=0; i<setCount; i++) 
			{
				if (set[i].TotalTime() > maxx)
				{
					maxx = set[i].totalTime;
				    maxi = i; 	
				}	
			}
			return maxi; 
		} 
		
		
		public void Sort () 
		{
			for (int i=0; i<setCount-1; i++) 
			{
				for (int j=i+1; j<setCount; j++) 
				{
	
					if (set[i].TotalTime() > set[j].TotalTime()) 
					{
						ExperimentResult temp = set[i]; 
						set[i] = set[j]; 
						set[j] = temp; 
						
					}
					
					
				}
				
			}

		}
		
		public  void AddResults (ExperimentResult er) 
		{
			all[allCount++] = er;
			
			if (setCount < maxSetCount) 
			{
				set[setCount++] = er; 
			}
			else 
			{
				int maxx = MaxInTheSet(); 
				if (set[maxx].TotalTime() > er.TotalTime() )
				{
				    set[maxx] = er;  
					
				}
			}
			
			Sort(); 
		}
		
		
		public void toFile ()
		{ 
			
			
			 WriteResults () ;
			 WriteResultsAll () ;
			 WriteStationSets() ;
			
			 

			
		}
		public void WriteResults () 
		{
		        
		        	ArrayList <String> lines = new ArrayList<String>();
		            String wStr = ""; 
		            for (int i=0; i<setCount; i++)
		            {
		            	ExperimentResult er = set[i]; 
	           	
		            		wStr = er.ExperimentMetrics();
		            		lines.add(wStr);
		            		
		            		
		            }
		            
			       new MRTAFileWriter().WriteToFile("\\Experiments\\Results_SortedSet.txt", lines); 
		}
		public void WriteResultsAll () 
		{
		        
			ExperimentResult ere = set[0]; 
			String fileName = ere.setupStr + "_" +ere.stationSet.setSize+"R_"+ere.taskSet.GetTaskCount()+"T_"+creationTime; 
			
			ArrayList <String> lines = new ArrayList<String>();
		            String wStr = ""; 
		            int cumTime = 0; 
		            int cumWay = 0; 
		            for (int i=0; i<allCount; i++)
		            {
		            	ExperimentResult er = all[i]; 
	           	           cumTime+= er.TotalTime(); 
	           	           cumWay+=er.totalWay; 
		            		wStr = er.ExperimentMetrics();
		            		lines.add(wStr);
		            }
		            lines.add("");
		            lines.add("Record Count : " + Integer.toString(allCount)); 
		            
		            lines.add("Average Time : " + Integer.toString(cumTime/allCount)); 
		            
		            lines.add("Average Travel Distance : " + Integer.toString(cumWay/allCount)); 
		            
		            lines.add("");
		            for (int i=0; i<allCount; i++)
		            {
		            	ExperimentResult er = all[i]; 
		            	String xStr = ""; 
	        	          for (int j=0; j<er.TotalTime()/100;j++) 
	        	        	  xStr = xStr + " "; 
	        	          xStr = xStr + "X"; 
	        	          lines.add(xStr); 
		            		 
		            		
		            }
		            
			       new MRTAFileWriter().WriteToFile("\\Experiments\\Results_All\\"+fileName+".txt", lines); 
		}
		public void WriteStationSets () 
		{
		        
		        	ArrayList <String> lines = new ArrayList<String>();
		            String wStr = ""; 
		            for (int i=0; i<setCount; i++)
		            {
		            	ExperimentResult er =  set[i]; 

		            	for (int j=0; j<er.stationSet.setSize; j++) 
		            	{
		            		StationSet ss = er.stationSet; 
		            		wStr = "S"+Integer.toString(i*10+j)+"_"+ss.stCoor[j][0] + "_"+ss.stCoor[j][1]+"_"+ss.TotalTime();  
		            		lines.add(wStr);
		            	}	
		            }
		            		            
			       new MRTAFileWriter().WriteToFile("\\Experiments\\Results.txt", lines); 
		}
		
		
	}