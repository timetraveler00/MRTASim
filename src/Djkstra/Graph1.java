package Djkstra;
import java.util.List;

public class Graph1 {
  private final List<Vertex> vertexes;
  private final List<Edge> edges;

  public Graph1(List<Vertex> vertexes, List<Edge> edges) {
    this.vertexes = vertexes;
    this.edges = edges;
  }

  public List<Vertex> getVertexes() {
    return vertexes;
  }

  public List<Edge> getEdges() {
    return edges;
  }
  
  
  
} 