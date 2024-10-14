package Enums;

public enum eDistanceCalculationMethod {


	BRESENHAM(0),DSTARLITE(1),RRT(2),DJKSTRA(3); 
	private final int value;
    private eDistanceCalculationMethod (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
}
    
}
