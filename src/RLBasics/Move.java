package RLBasics;
/*
 * Sava� �ZT�RK 
 * 200201141
 * 
 * Yaz�l�m Lab. II - Proje 3 
 * 19.05.2021 
 * 
 * 
 * */
class Move {
    int oldState;
    int newState;
    int action;
    double reward;
    
    public Move(int oldState, int action, int newState, double reward) {
        this.oldState = oldState;
        this.newState = newState;
        this.reward = reward;
        this.action = action;
    }
}