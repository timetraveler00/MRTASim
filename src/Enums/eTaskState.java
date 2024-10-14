package Enums;

public enum eTaskState {

	NOT_STARTED(0), PROCESSING(1), COMPLETED(2), RESERVED1 (3), RESERVED2 (4), RESERVED3 (5), CONFIRMED(6), SOLD(7), RESERVED4 (8), NOT_ASSIGNED(9), ASSIGNED(10);
	private final int value;
    private eTaskState (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
}
    
}