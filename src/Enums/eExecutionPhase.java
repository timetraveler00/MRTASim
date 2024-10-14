package Enums;

public enum eExecutionPhase {
	NOT_STARTED(0),CENTRALIZED_ALLOCATIONS(1), AUCTIONS_COMPLETED(2),TRADES(3),TRADES_COMPLETED(4),SCHEDULING_EXECUTING(5),ALL_TASKS_COMPLETED(6);
	private final int value;
    private eExecutionPhase (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
}	

}
