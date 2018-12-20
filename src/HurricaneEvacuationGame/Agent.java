package HurricaneEvacuationGame;

abstract class Agent {

    private int agentNum;
    private Agent opponent;
    Vertex location;
    private int carrying = 0;
    private int numOfMoves = 0;
    private int saved = 0;

    Agent(int agentNum) {
        this.agentNum = agentNum;
    }

    abstract Move makeOperation();

    void setOpponent(Agent opponent) {
        this.opponent = opponent;
    }

    private void setLocation(Vertex location) {
        this.location = location;
    }

    void traverse(Vertex target) {

        setLocation(target);
        if (target.isShelter()) {
            Simulator.setSafeCount(Simulator.getSafeCount() + getCarrying());
            saved += getCarrying();
            setCarrying(0);
        } else {
            setCarrying(getCarrying() + target.getPersons());
            target.setPersons(0);
            Simulator.getPeopleMap().replace(target.getId(), 0);
        }

    }

    int getAgentNum() {
        return agentNum;
    }

    Agent getOpponent() {
        return opponent;
    }

    Vertex getLocation() {
        return location;
    }

    int getCarrying() {
        return carrying;
    }

    private void setCarrying(int carrying) {

        this.carrying = carrying;
    }

    private int getNumOfMoves() {
        return numOfMoves;
    }

    void increaseMoves() {
        this.numOfMoves++;
    }

    int getSaved() {
        return saved;
    }

    void performanceMeasure(){

        System.out.println(getSaved());

    }

    @Override
    public String toString() {

        return "Location: " + getLocation().getId() + "\n"
                + "Carrying: " + getCarrying() + "\n"
                + "Number of moves: " + getNumOfMoves() + "}";

    }
}
