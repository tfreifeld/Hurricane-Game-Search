package HurricaneEvacuationGame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class State {

    private int counter;
    private final State parent;
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
        this.parent = null;
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
        this.parent = parent;
        this.agent = agent;
        this.opponent = agent.getOpponent();
        this.myLocation = myLocation;
        this.opLocation = opLocation;
        this.mySaved = parent.getOpSaved();
        this.opSaved = parent.getMySaved();
        initActions(this.myLocation);

        this.myCarryCount = parent.getOpCarryCount();
        this.opponentCarryCount = parent.getMyCarryCount();

        if (edgeTraversed != null) {

            this.timeElapsed = parent.getTimeElapsed() +
                    Simulator.computeTraverseTime(opponentCarryCount,
                            edgeTraversed.getWeight());
        }
        else{
            /*NoOp*/
            this.timeElapsed = parent.getTimeElapsed() + 1;
        }


        this.peopleMap = new HashMap<>(parent.getPeopleMap());
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
        actions.add(new Move(this.agent, this.agent.getLocation(), null));
    }

    private int getCounter() {
        return counter;
    }

    State getParent() {
        return parent;
    }

    Vertex getMyLocation() {
        return myLocation;
    }

    Vertex getOpLocation() {
        return opLocation;
    }

    int getMyCarryCount() {
        return myCarryCount;
    }

    int getOpCarryCount() {
        return opponentCarryCount;
    }

    double getTimeElapsed() {
        return timeElapsed;
    }

    Set<Move> getActions() {
        return actions;
    }

    Agent getAgent() {
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

    boolean isTerminal() {

        return (getTimeElapsed() > Simulator.getDeadline()) ||
                (getCounter() == Simulator.getCutoffLimit());
    }
}
