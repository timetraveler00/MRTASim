package Enums;


public enum eScenarioType {
	MANUAL(0),MS(1),ASP1(2),ASP2(3),ASP3(4); 
	
	    private final int value;
	    private eScenarioType (int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

}

