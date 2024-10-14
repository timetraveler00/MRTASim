package Enums;

public enum eLocalScheduling {

   
	TSP(0), NEAREST(1), BYNAME(2), RANDOM(3);
	private final int value;
    private eLocalScheduling (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}