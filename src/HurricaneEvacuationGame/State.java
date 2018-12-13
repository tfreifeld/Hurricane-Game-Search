package HurricaneEvacuationGame;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class State {

    private Agent agent;
    private Vertex location;
    private Set<Move> actions;
    private int myCarryCount;
    private int opponentCarryCount;

    State(Agent agent) {
        this.agent = agent;
        this.location = agent.getLocation();
        this.myCarryCount = 0;
        this.opponentCarryCount = 0;
    }

    private State(Agent agent,Vertex location, State parent) {
        this.agent = agent;
        this.location = location;
        this.actions = new HashSet<>();
        this.myCarryCount = parent.getOpponentCarryCount() + location.getPersons();
        /*TODO: need to incorporate people map and update it here*/
        this.opponentCarryCount = parent.getMyCarryCount();

        for (Map.Entry<Integer,Edge> entry: location.getEdges().entrySet()) {
            actions.add(new Move(agent, entry.getValue()
                            .getNeighbour(location),entry.getValue()));

        }
    }

    public Vertex getLocation() {
        return location;
    }

    private int getMyCarryCount() {
        return myCarryCount;
    }

    private int getOpponentCarryCount() {
        return opponentCarryCount;
    }

    Set<Move> getActions() {
        return actions;
    }

    State getResult(Move action){
        Agent opponent = null;

        for (Agent agent : Simulator.getAgents()) {
            if(agent != this.agent)
                opponent = agent;

        }

        return new State(opponent, this);
    }
}
