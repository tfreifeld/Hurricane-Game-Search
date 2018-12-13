package HurricaneEvacuationGame;

abstract class Agent {

    int agentNum;
    Vertex location;
    private int carrying = 0;
    private int numOfMoves = 0;
    private int saved = 0;
    private int numOfExpansions = -1;


    abstract Move makeOperation();

    void setLocation(Vertex location) {
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
        }

    }

    int getAgentNum() {
        return agentNum;
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

    int getNumOfExpansions() {
        return numOfExpansions;
    }

    void setNumOfExpansions(int numOfExpansions) {
        this.numOfExpansions = numOfExpansions;
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
