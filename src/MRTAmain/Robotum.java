package MRTAmain;


import java.lang.String; 

import Enums.eRobotStatus;


public class Robotum {

	
	// Robotun anlýk lokasyonu
	public int xLoc ; 
	public int yLoc ;
	
	public double dxLoc ; 
	public double dyLoc ;
	
	
	public String robotName; 
	public double cumulativeWay = 0; 
	public int energyLevel = 100000;
	public eRobotStatus status = eRobotStatus.IDLE; 
	public int completedTasks = 0;
	public int chargeCount = 0 ; 
	public boolean lastTraded = false; 
	int capacityNormal; 
	int capacityExtra; 
	
	
	// Robotun ilk baþlangýç lokasyonu
	public int startX; 
	public int startY;
	
	// Robotun bir hareketinden bir önceki lokasyonu
	public int preX ; 
	public int preY ;
	public int colorIndex = 0; 
	public String boughtItems[][]=new String [100][2]; 

	

	int path[][] = new int [999999][2];
	public int localPathLength [] = new int [100];
	public int priceperTrade [] = new int [100];
	public int taskCountperTrade [] = new int [100]; 
	public String tradedTask[] = new String[100]; 
	int pathCounter = 0; 
	int boughtCounter = 0; 
	boolean scheduleCreated = false; 
	boolean atTheStation = true; 
	public int schTime = 0;
	public int execTime = 0;
	public int pickUpTime = 0; 
	public int waitingTasks = 0; 
	public int speed = 20; 
	
	public double heading = 88; 
	public int numberOfSectors = 24; 
	public double meanProximities[] = new double [numberOfSectors]; 
	
	public HRectangle rect, outerRect; 
    /*
     *   status
     *   0 : idle, available
     *   1 : working for a task
     *   2 : on the way to charge station
     *   3 : charging
     * 
     * */
	

    public  Robotum() 
     { 
	     
	     robotName = "myRobot"; 
	   
	     status = eRobotStatus.IDLE; 
	     completedTasks = 0; 
	     chargeCount =  0; 
	     pathCounter = 0;
	     colorIndex = 0; 
	     boughtCounter = 0; 
	     scheduleCreated = false; 
	     rect = new HRectangle(0,0,0,0);
	     outerRect = new HRectangle(0,0,0,0); 
	     speed = 20; 
	     
	     for (int i=0; i<100; i++) 
	     {
	    	 priceperTrade [i] = -999999999; 
	    	 tradedTask[i] = ""; 
	     }

     }
    
    
    
    public void TradeReset () 
    {
	     for (int i=0; i<100; i++) 
	     {
	    	 priceperTrade [i] = -999999999; 
	     }
    	
    }
    
    
    public Robotum(String rbtName) 
    { 
	     //System.out.println(rbtName);
    	 this(); 
	     this.robotName = rbtName; 
	     completedTasks = 0; 
	   

    }
    
    public void ResetLocation(int xx, int yy)
    {
     	 xLoc = xx; 
     	 yLoc = yy;
     	 dxLoc = xx; 
     	 dyLoc = yy; 
     	 preX = xx; 
     	 preY = yy; 
     	 startX = xx; 
     	 startY = yy; 
    	
    }
    
    public void LocateRobot (int xx, int yy)
    {
    	 xLoc = xx; 
    	 yLoc = yy; 
    	
    }
    public void addToPath (int xx, int yy) 
    {
    	path[pathCounter][0] = xx; 
    	path[pathCounter++][1] = yy;
    }
    
    public void pathLength (int tradeCount, int pathLength) 
    {
        localPathLength[tradeCount] = pathLength;  
    }
    public void setPrice (int tradeCount, int price) 
    {
        priceperTrade[tradeCount] = price;  
    }
    
    public void addToPath () 
    {
    	path[pathCounter][0] = xLoc; 
    	path[pathCounter++][1] = yLoc;
    }
    public int PathCounter()
    {
    	return pathCounter; 
    	
    }
    public int PathX(int index) 
    {
    	return path[index][0]; 
    }
    public int PathY(int index) 
    {
    	return path[index][1]; 
    }
    
    public void AddBoughtTask (String robotName, String taskName)
    {
    	 boughtItems[boughtCounter++][0] = robotName; 
    	 boughtItems[boughtCounter++][1] = taskName;
    	 
    }
    
    

}
