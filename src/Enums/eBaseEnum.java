package Enums;



public enum eBaseEnum {
	 BASE0(0), BASE1(1); 
	

	    private final int value;
	    private eBaseEnum (int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

}