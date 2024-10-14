package MRTAmain;
import java.util.TreeSet;
import java.util.Vector;
import java.util.HashSet;

class EdgeMST implements Comparable<EdgeMST>
{
    String vertexA, vertexB;
    int weight;

    public EdgeMST(String vertexA, String vertexB, int weight)
    {
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.weight = weight;
    }
    public String getVertexA()
    {
        return vertexA;
    }
    public String getVertexB()
    {
        return vertexB;
    }
    public int getWeight()
    {
        return weight;
    }
    @Override
    public String toString()
    {
        return "(" + vertexA + ", " + vertexB + ") : Weight = " + weight;
    }
    public int compareTo(EdgeMST edge)
    {
        //== is not compared so that duplicate values are not eliminated.
        return (this.weight < edge.weight) ? -1: 1;
    }
}
class KruskalEdges
{
    Vector<HashSet<String>> vertexGroups = new Vector<HashSet<String>>();
    TreeSet<EdgeMST> kruskalEdges = new TreeSet<EdgeMST>();

    public TreeSet<EdgeMST> getEdges()
    {
        return kruskalEdges;
    }
    HashSet<String> getVertexGroup(String vertex)
    {
        for (HashSet<String> vertexGroup : vertexGroups) {
            if (vertexGroup.contains(vertex)) {
                return vertexGroup;
            }
        }
        return null;
    }
    public void insertEdge(EdgeMST edge)
    {
        String vertexA = edge.getVertexA();
        String vertexB = edge.getVertexB();

        HashSet<String> vertexGroupA = getVertexGroup(vertexA);
        HashSet<String> vertexGroupB = getVertexGroup(vertexB);

        if (vertexGroupA == null) {
            kruskalEdges.add(edge);
            if (vertexGroupB == null) {
                HashSet<String> htNewVertexGroup = new HashSet<String>();
                htNewVertexGroup.add(vertexA);
                htNewVertexGroup.add(vertexB);
                vertexGroups.add(htNewVertexGroup);
            }
            else {
                vertexGroupB.add(vertexA);           
            }
        }
        else {
            if (vertexGroupB == null) {
                vertexGroupA.add(vertexB);
                kruskalEdges.add(edge);
            }
            else if (vertexGroupA != vertexGroupB) {
                vertexGroupA.addAll(vertexGroupB);
                vertexGroups.remove(vertexGroupB);
                kruskalEdges.add(edge);
            }
        }
    }
}


public class Kruskal
{
	TreeSet<EdgeMST> edges; 
	public Kruskal () 
	{
        //TreeSet is used to sort the edges before passing to the algorithm
        edges = new TreeSet<EdgeMST>();

	}
	public void AddEdge (String from, String to, int distance) 
	{
		edges.add(new EdgeMST(from, to, distance));
	}
	public int FastCalc () 
	{
		  System.out.println("Kruskal Graph Fast Calc ");
	        KruskalEdges vv = new KruskalEdges();

	        for (EdgeMST edge : edges) {
	            System.out.println(edge);
	            vv.insertEdge(edge);
	        }

	        System.out.println("Kruskal algorithm");
	        int total = 0;
	        for (EdgeMST edge : vv.getEdges()) {
	            System.out.println(edge);
	            total += edge.getWeight();
	        }
	        System.out.println("Total weight is " + total);
	        return total; 
	}
	public KruskalEdges MSTFinal () 
	{
		  System.out.println("Kruskal Graph Fast Calc ");
	        KruskalEdges vv = new KruskalEdges();

	        for (EdgeMST edge : edges) {
	            System.out.println(edge);
	            vv.insertEdge(edge);
	        }

	        System.out.println("Kruskal algorithm");
	        int total = 0;
	        for (EdgeMST edge : vv.getEdges()) {
	            System.out.println(edge);
	            total += edge.getWeight();
	        }
	        System.out.println("Total weight is " + total);
	        return vv; 
	}
	/* public static void main(String[] args)
    {
        //TreeSet is used to sort the edges before passing to the algorithm
        TreeSet<EdgeMST> edges = new TreeSet<EdgeMST>();

        //Sample problem - replace these values with your problem set
        edges.add(new EdgeMST("0", "1", 2));
        edges.add(new EdgeMST("0", "3", 1));
        edges.add(new EdgeMST("1", "2", 3));
        edges.add(new EdgeMST("2", "3", 5));
        edges.add(new EdgeMST("2", "4", 7));
        edges.add(new EdgeMST("3", "4", 6));
        edges.add(new EdgeMST("4", "5", 4));

        System.out.println("Graph");
        KruskalEdges vv = new KruskalEdges();

        for (EdgeMST edge : edges) {
            System.out.println(edge);
            vv.insertEdge(edge);
        }

        System.out.println("Kruskal algorithm");
        int total = 0;
        for (EdgeMST edge : vv.getEdges()) {
            System.out.println(edge);
            total += edge.getWeight();
        }
        System.out.println("Total weight is " + total);
    } */ 
 }