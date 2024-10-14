package Enums;

public enum eTradingProgress {
    IDLE(0),ON_PROGRESS(1),FAILURE(2),SUCCESS(3);
	private final int value;
    private eTradingProgress (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
}
}

