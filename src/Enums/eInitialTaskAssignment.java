package Enums;

public enum eInitialTaskAssignment {
   
	RANDOM(0), GREEDY(1), OPTIMAL(2), PRIM(3);
	private final int value;
    private eInitialTaskAssignment (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}