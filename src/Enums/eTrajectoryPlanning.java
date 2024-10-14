package Enums;


public enum eTrajectoryPlanning {

	NODE_TO_NODE(0),DSTAR_PATH(1),RRT_PATH(2),BRESENHAM(3);
	private final int value;
    private eTrajectoryPlanning (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
}
    
}

