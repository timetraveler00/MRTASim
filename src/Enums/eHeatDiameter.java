package Enums;

public enum eHeatDiameter {

	d300(0),D400(1),D500(2),D600(3),D700(4);
	private final int value;
    private eHeatDiameter (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
}
    
}
