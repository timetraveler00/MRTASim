package MRTAmain;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Enums.eTSPLoopMode;
import IndoorStructure.IndoorStructure;

//An modified Traveling Salesperson Problem
// In this example, the salesperson may only visit each city once.
// This is a weighted Hamiltonian Path problem.


//The method findMinSolution finds the shortest TSP solution for the given graph.
//TODO: complete the method minGreedyPath that finds the shortest path using the greedy algorithm
//
//The greedy search algorithm:
//1. Pick a start node as the current node.
//2. Find the next node that is not already in the path 
//   which has the shortest distance from the current node.
//3. Repeat step 2 until all nodes are in the path.
//4. Repeat steps 1-3 for all nodes in the graph, 
//   find the path with the shortest weight.
//
// How do the results of the greedy algorithm compare with the exponential algorithm?


//example graphs here
//
/*
 4
 0 1 1 1
 1 0 1 1
 1 1 0 1
 1 1 1 0
 
 4
 0 2 3 1
 4 0 6 2
 1 2 0 5
 4 2 1 0
 
 10
 0 34 45 37 23 83 12 15 99 11
 11 0 22 33 44 55 66 77 88 99
 33 23 0 12 53 23 87 27 54 91
 21 13 81 0 64 81 81 45 53 51
 64 87 54 16 0 87 42 31 46 51
 91 36 65 52 48 0 15 75 32 30
 78 13 16 54 32 76 0 49 35 15
 49 76 45 82 39 47 18 0 19 53
 75 15 95 45 86 45 48 75 0 14
 34 68 49 78 49 68 84 19 37 0
 */

//an instance of the traveling salesman problem
public class TSPMinGreedy {
	int numCities;
	int[][] distances;
	int maxPath;
	List<Integer> minPathList;
	
	int minLength = -1;
	boolean findingMin = false;
	String minPath;
	eTSPLoopMode lpMode = eTSPLoopMode.TSP_TOUR; 
	
	/*public static TSPMinGreedy readTSPInstance(java.io.InputStream inStream){
		Scanner in = new Scanner(inStream);
		//read number of cities
		int n = in.nextInt();
		TSPMinGreedy result = new TSPMinGreedy(n);
		
		//read the n x n matrix of distances
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				int dist = in.nextInt();
				result.setDistance(i, j, dist);
			}
		}
		
		//read the max path value
		int max = in.nextInt();
		result.setMaxPath(max);
		
		return result;
	}
	
	public static TSPMinGreedy createRandomInstance(int n, int max){
		TSPMinGreedy result = new TSPMinGreedy(n);
		Random rand = new Random();
		//read the n x n matrix of distances
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				if(i != j)
					result.setDistance(i, j, rand.nextInt(100));
				else
					result.setDistance(i,  j, 0);
			}
		}
		result.setMaxPath(max);
		return result;
		
	}*/
	
	public TSPMinGreedy(int cities, eTSPLoopMode loopMode){
		numCities = cities;
		maxPath = numCities; //dummy value
		distances = new int[numCities][numCities];
		lpMode = loopMode; 
	}
	
	public TSPMinGreedy(IndoorStructure ist, boolean loopMode){
		numCities = 0;
		maxPath = numCities; //dummy value
		distances = new int[numCities][numCities];
		minPathList = null; 
		
	}
	
	public int getMaxPath() {
		return maxPath;
	}

	public void setMaxPath(int maxPath) {
		this.maxPath = maxPath;
	}

	//there is no requirement for symmetry
	public void setDistance(int c1, int c2, int dist){
		distances[c1][c2] = dist;
	}
	
	public boolean verifier(List<Integer> certificate){
		//calculate the size of the path
		Iterator<Integer> it = certificate.iterator();
		int prev = it.next();
		int total = 0;
		while(it.hasNext()){
			int curr = it.next();
			
			total += distances[prev][curr];
			
			prev = curr;
		}
		
		
		if(findingMin){
			if( (minLength == -1 || total < minLength) && certificate.get(0) == 0 )
			{		
				if (((lpMode==eTSPLoopMode.TSP_PATH || lpMode==eTSPLoopMode.TSP_TOUR)) || (lpMode == eTSPLoopMode.TSP_PATHTOSTATION && certificate.get(numCities-1) == numCities-1))
				{
				
					minLength = total;
					minPath = pathToString(certificate);
			
					minPathList = new LinkedList<Integer>();
					minPathList.addAll(certificate); 
				}
			}
		}
		

		
		return total <= maxPath;
	}
	
	public boolean hasSolution(){
		findingMin = false;
		List<Integer> cityList = new LinkedList<Integer>();
		for(int i = 0; i < numCities; i++){
			cityList.add(i);
		}
		
		return permutationHelper(new LinkedList<Integer>(), cityList);
		
	}
	
	public int findMinSolution(){
		findingMin = true;
		minLength = -1;
		minPath = null;

		List<Integer> cityList = new LinkedList<Integer>();
		for(int i = 0; i < numCities; i++){
			cityList.add(i);
		}
		
		permutationHelper(new LinkedList<Integer>(), cityList);
	/*	System.out.println("***************************");
		
		System.out.println("Min Length : " + minLength);
		System.out.println("***************************");
		*/
		return minLength;
		
	}
	
	private String pathToString(List<Integer> path){
		StringBuilder result = new StringBuilder();
		
		for(int i: path){
			result.append(i);
			result.append(" ");
		}
		
		return result.toString();
	}
	
	private boolean permutationHelper(List<Integer> used, List<Integer> unused){
		if(unused.isEmpty()){
			if(verifier(used)){
				if(!findingMin){
					printList(used);
					return true;
				}
			}
		}
		else {
			for(int i = 0; i < unused.size(); i++){
				int item = unused.remove(0);
				used.add(item);

				if(permutationHelper(used, unused)){
					if(!findingMin)
						return true;
				}

				used.remove(used.size()-1);
				unused.add(item);
			}
		}
		return false;
	}
	
	//return the length of the shortest path found using the greedy algorithm
	public int minGreedyPath(){
		//add algorithm here (described at the top of the file).
		return 0;
	}
	
	
/*	public static void main(String[] args){
		//read the problem
		/*
		System.out.println("Enter the map: ");
		System.out.println("\tn: number of cities");
		System.out.println("\tn*n distances between cities");
		System.out.println("\tm: max tour length");
		//
		TSPMinGreedy problem = readTSPInstance(System.in);
		
		
		//TSPMinGreedy problem = createRandomInstance(11, 9);

		long t0 = Calendar.getInstance().getTimeInMillis();
		//boolean found = problem.hasSolution();
		int min = problem.findMinSolution();
		long t1 = Calendar.getInstance().getTimeInMillis();
		int minGreedy = problem.minGreedyPath();
		long t2 = Calendar.getInstance().getTimeInMillis();
		//if(!found)
		//	System.out.println("No Solution.");
		System.out.println("Shortest path length: " + min);
		System.out.printf("   Time: %d\n", (t1-t0));
		System.out.println("Greedy path length: " + minGreedy);
		System.out.printf("   Time: %d\n", (t2-t1));
		
	  
	
	}*/
	
	public static void printList(List<Integer> items){
		for(int i: items){
			System.out.printf("%d ", i);
		}
		System.out.println();
	} /*
	public void addPoint(double xLoc,doubleyLoc)
	{
		 
	}*/
}