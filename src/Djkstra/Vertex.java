package Djkstra;
import IndoorStructure.TransitionPoint;

//package de.vogella.algorithms.dijkstra.model;

public class Vertex {
  final private String id;
  final private String name;
  public boolean MustBe; 
  public TransitionPoint tp; 
  
  
  public Vertex(String id, String name, TransitionPoint tp, boolean MustBe) {
    this.id = id;
    this.name = name;
    this.MustBe = MustBe; 
    this.tp = tp; 
  }
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Vertex other = (Vertex) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return name;
  }
  
} 
