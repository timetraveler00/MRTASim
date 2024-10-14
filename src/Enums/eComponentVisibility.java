package Enums;


public enum eComponentVisibility {
	
    VISIBLE(true),HIDDEN(false);

    private final boolean value;
    private eComponentVisibility (boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

}

