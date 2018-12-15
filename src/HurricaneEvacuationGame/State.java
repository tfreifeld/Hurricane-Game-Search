package HurricaneEvacuationGame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class State {

    private int counter;

    private Agent agent;
    private Agent opponent;
    private Vertex myLocation;
    private Vertex opLocation;
    private Set<Move> actions;
    private int myCarryCount;
    private int opponentCarryCount;
    private int mySaved;
    private int opSaved;
    private double timeElapsed;
    private HashMap<Integer, Integer> peopleMap;


    State(Agent agent) {
        this.counter = 0;
        this.agent = agent;
        this.opponent = agent.getOpponent();
        this.myLocation = agent.getLocation();
        this.opLocation = this.opponent.getLocation();
        initActions(this.myLocation);
        this.timeElapsed = Simulator.getTime();
        this.myCarryCount = agent.getCarrying();
        this.opponentCarryCount = this.opponent.getCarrying();
        this.mySaved = agent.getSaved();
        this.opSaved = this.opponent.getSaved();
        this.peopleMap = Simulator.getPeopleMapCopy();
    }

    private State(Agent agent, Edge edgeTraversed, Vertex myLocation,
                  Vertex opLocation, State parent) {

        this.counter = parent.getCounter() + 1;
        this.agent = agent;
        this.opponent = agent.getOpponent();
        this.myLocation = myLocation;
        this.opLocation = opLocation;
        this.mySaved = parent.getOpSaved();
        this.opSaved = parent.getMySaved();
        initActions(this.myLocation);

        this.myCarryCount = parent.getOpponentCarryCount();
        this.opponentCarryCount = parent.getMyCarryCount();

        this.timeElapsed = parent.getTimeElapsed() +
                Simulator.computeTraverseTime(opponentCarryCount,
                        edgeTraversed.getWeight());


        this.peopleMap = parent.getPeopleMap();
        if (this.peopleMap.containsKey(opLocation.getId()) &&
                this.peopleMap.get(opLocation.getId()) != 0) {
            this.opponentCarryCount += opLocation.getPersons();
            this.peopleMap.replace(opLocation.getId(), 0);
        }

        if (opLocation.isShelter()) {
            this.opSaved += this.opponentCarryCount;
            this.opponentCarryCount = 0;
        }

    }



    private void initActions(Vertex myLocation) {
        this.actions = new HashSet<>();

        for (Map.Entry<Integer, Edge> entry : myLocation.getEdges().entrySet()) {
            actions.add(new Move(this.agent, entry.getValue()
                    .getNeighbour(myLocation), entry.getValue()));

        }
    }

    int getCounter() {
        return counter;
    }

    Vertex getMyLocation() {
        return myLocation;
    }

    Vertex getOpLocation() {
        return opLocation;
    }

    private int getMyCarryCount() {
        return myCarryCount;
    }

    private int getOpponentCarryCount() {
        return opponentCarryCount;
    }

    double getTimeElapsed() {
        return timeElapsed;
    }

    Set<Move> getActions() {
        return actions;
    }

    public Agent getAgent() {
        return agent;
    }

    HashMap<Integer, Integer> getPeopleMap() {

        return peopleMap;
    }

    int getMySaved() {
        return mySaved;
    }

    int getOpSaved() {
        return opSaved;
    }

    State getResult(Move action) {

        return new State(this.opponent, action.getEdge(),
                opLocation, action.getTarget(), this);
    }
}
