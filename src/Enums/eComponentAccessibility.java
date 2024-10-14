package Enums;


public enum eComponentAccessibility {
	
	    ENABLED(true),DISABLED(false);

	    private final boolean value;
	    private eComponentAccessibility (boolean value) {
	        this.value = value;
	    }

	    public boolean getValue() {
	        return value;
	    }

}
