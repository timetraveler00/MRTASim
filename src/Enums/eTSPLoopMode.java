package Enums;

public enum eTSPLoopMode  {
	TSP_TOUR(0),TSP_PATH(1),TSP_PATHTOSTATION(2);
	
    private final int value;
    private eTSPLoopMode (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
