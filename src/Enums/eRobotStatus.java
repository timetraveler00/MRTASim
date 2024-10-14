package Enums;

public enum eRobotStatus {
	IDLE(0), AUCTIONING(1), ASSIGNED(2), TRAVERSING(3), COMPLETED(4);
	private final int value;
    private eRobotStatus (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
}
}

