package Enums;


public enum eServiceArea {
	    ALL_TERRAIN(0), RECTANGULAR(1),EXTENDEDRECTANGULAR(2),CIRCULAR(3); 
	

	    private final int value;
	    private eServiceArea (int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

}
