package RLBasics;


import java.util.ArrayList;

/*
 * Sava� �ZT�RK 
 * 200201141
 * 
 * Yaz�l�m Lab. II - Proje 3 
 * 19.05.2021 
 * 
 * 
 * */

public class Episode {

	int stepCount; 
	double cost; 
	int finalAct;
	ArrayList<Move> moves ; 
	double avrg; 
	public Episode() {
		super();
		// TODO Auto-generated constructor stub
		stepCount = 0; 
		cost = 0; 
		
		moves = new ArrayList<Move>();
	} 
	public double getAverage() 
	{
		return cost/stepCount;
	}
	
	
}
