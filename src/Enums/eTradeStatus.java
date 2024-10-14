package Enums;

public enum eTradeStatus {

    OWNERLESS(0),OWNED(1),OFFERED(2),SOLD(3),BOUGHT(4);
	private final int value;
    private eTradeStatus (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
}
    
}