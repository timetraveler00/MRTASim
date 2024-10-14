package MRTAStreamManager;


public class ShortestMapReader extends MRTAFileReader {

	public int shortestPathMatrix [][];
	int tpCount = 0; 
	public int CreateShortestMapArray() 
	{
		shortestPathMatrix = new int [tpCount+1][tpCount+1];
		System.out.println (" tpcount "+tpCount);
		System.out.println (" lines "+lines.size());
		for (int i=0; i<lines.size(); i++) 
		{
		    for (int j=0; j<Get(i).size(); j++)
                 {
                	 shortestPathMatrix [i][j] = Integer.parseInt(Get(i).get(j)) ; 
                	  
                 }
		  }
		  
		return lines.size(); 
		
		
	}
	
	public int [][] GetShortestPathMap () 
	{
		return shortestPathMatrix; 
	}
	public ShortestMapReader(String template, int tpc) {
		super();
		delimiter ="_";
		folderPath = "\\config\\";
		fileName = "ShortestPaths.txt"; 
		templateName = template; 
		tpCount = tpc; 
		 
		FromFileToArray();
		CreateShortestMapArray(); 
		
		
	}

}
