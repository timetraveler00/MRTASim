package Enums;

public enum eSimState {
    NOT_STARTED(0),STARTED(1),PAUSED(2),STOPPED(3); 


    private final int value;
    private eSimState (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}