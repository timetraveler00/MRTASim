package Enums;

public enum eBidValuation {

	EUCLIDIAN(0), FUZZY(1), DSTARLITE(2),DSTARLITEFUZZY(3),RRT(4),TMPTSP(5),TMPRRTTSP(6),DSTARLITETSP(7),HYBRID(8);
	private final int value;
    private eBidValuation (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
}
    
}

