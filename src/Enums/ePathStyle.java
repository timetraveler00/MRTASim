package Enums;

public enum ePathStyle {
   
	BRESENHAM(0),DSTARLITE(1),RRT(2),DJKSTRA(3);
	private final int value;
    private ePathStyle (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
