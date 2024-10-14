package Djkstra;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// LARS VOGELLA

public class DijkstraAlgorithm {

  
  private final List<Edge> edges;
  private Set<Vertex> settledNodes;
  private Set<Vertex> unSettledNodes;
  private Map<Vertex, Vertex> predecessors;
  private Map<Vertex, Integer> distance;
  public int cumu = 0; 

  public DijkstraAlgorithm(Graph1 Graph1) {
    // Create a copy of the array so that we can operate on this array
  //  this.nodes = new ArrayList<Vertex>(Graph1.getVertexes());
    this.edges = new ArrayList<Edge>(Graph1.getEdges());
  }

  public void execute(Vertex source) {
    settledNodes = new HashSet<Vertex>();
    unSettledNodes = new HashSet<Vertex>();
    distance = new HashMap<Vertex, Integer>();
    predecessors = new HashMap<Vertex, Vertex>();
    distance.put(source, 0);
   
    unSettledNodes.add(source);
    
    
    
    while (unSettledNodes.size() > 0) {
      Vertex node = getMinimum(unSettledNodes);
      settledNodes.add(node);
      unSettledNodes.remove(node);
      findMinimalDistances(node);
    }
  }

  private void findMinimalDistances(Vertex node) {
    List<Vertex> adjacentNodes = getNeighbors(node);
    for (Vertex target : adjacentNodes) {
   	
    	if (node.MustBe && getShortestDistance(target) > getShortestDistance(node)
    	          + getDistance(node, target)) 
    	{
    		// System.out.println("Must Be  :"  + node.getName() + "   " + target.getName() + " shortest(target) : " + Integer.toString(getShortestDistance(target))+ " s hortest : " + Integer.toString(getShortestDistance(node)) + " distance : " +  Integer.toString(getDistance(node, target))) ; 
 	        
    		distance.put(target, getShortestDistance(node)
    	            + getDistance(node, target));
    	        predecessors.put(target, node);
    	        unSettledNodes.add(target);
    	       return; 
       	}
    } 
    for (Vertex target : adjacentNodes) { 
    	 if  	
    	 (getShortestDistance(target) > getShortestDistance(node)
          + getDistance(node, target) ) 
    	 {
    	      //  System.out.println("Minimal  :"  + node.getName() + "   " + target.getName() + " shortest(target) : " + Integer.toString(getShortestDistance(target))+ " shortest : " + Integer.toString(getShortestDistance(node)) + " distance : " +  Integer.toString(getDistance(node, target))) ; 
    		 distance.put(target, getShortestDistance(node)
            + getDistance(node, target));
        predecessors.put(target, node);
        unSettledNodes.add(target);


      }
    }

  }

  public int getDistance(Vertex node, Vertex target) {
    for (Edge edge : edges) {
      if (edge.getSource().equals(node)
          && edge.getDestination().equals(target)) {
        return edge.getWeight();
      }
    }
    throw new RuntimeException("Should not happen");
  }

  private List<Vertex> getNeighbors(Vertex node) {
    List<Vertex> neighbors = new ArrayList<Vertex>();
    for (Edge edge : edges) {
      if ((edge.getSource().equals(node)
          && !isSettled(edge.getDestination())) 
          ) {
        neighbors.add(edge.getDestination());
      }
    }
    return neighbors;
  }

  private Vertex getMinimum(Set<Vertex> vertexes) {
    Vertex minimum = null;
    for (Vertex vertex : vertexes) {
      
      if (vertex.MustBe) 
      {
    	  minimum = vertex;    	  
      }
      else if (minimum == null) {
          minimum = vertex;
        } 
       else {
        if (( getShortestDistance(vertex) < getShortestDistance(minimum)) ) {
          minimum = vertex;
        }
      }
    }
    return minimum;
  }

  private boolean isSettled(Vertex vertex) {
    return settledNodes.contains(vertex);
  }

  private int getShortestDistance(Vertex destination) {
    Integer d = distance.get(destination);
    if (d == null) {
      return Integer.MAX_VALUE;
    } else {
      return d;
    }
  }

  /*
   * This method returns the path from the source to the selected target and
   * NULL if no path exists
   */
  public LinkedList<Vertex> getPath(Vertex target) {
	//  long begin = System.currentTimeMillis();
	  LinkedList<Vertex> path = new LinkedList<Vertex>();
    Vertex step1 = target;
    Vertex step2 = null;
    cumu = -1; 
    // Check if a path exists

    if (predecessors.get(step1) == null) {
      return null;
    }
    path.add(step1);
    while (predecessors.get(step1) != null) {
      step2 = predecessors.get(step1);
      path.add(step2);
      cumu+=getDistance(step1, step2); 
    //  System.out.println("Predecessor  :   step1 "  + step1.getName() + "   step2" + step2.getName() + " cumul : " + Integer.toString(cumu) ) ; 
      step1=step2; 
    }
    // Put it into the correct order
    Collections.reverse(path);
    
	//long end = System.currentTimeMillis();
	// System.out.println("DjkstraAlgorithm>getPath() target : " + target.getName() + " xloc : " +target.tp.xLoc + " yloc : "+target.tp.yLoc + " cumulative : " + cumu + " time : " +(end-begin)); 
	return path;
  }

}

