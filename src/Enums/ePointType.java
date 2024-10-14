
package Enums;

public enum ePointType {

	ROBOT(0), TASK(1), TRANSITIONPOINT(2),STATION(3); 
	private final int value;
    private ePointType (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
}
    
}