package HurricaneEvacuationGame;

import java.util.*;

class SemiCooperativeAgent extends Agent {

    SemiCooperativeAgent(int agentNum) {
        super(agentNum);
    }

    @Override
    Move makeOperation() {

        List<Integer> result = Arrays.asList(0, 0);

        State state = new State(this);

        Move bestMove = new Move(this, getLocation(), null);

        for (Move action : state.getActions()) {

            List<Integer> actionValue = search(state.getResult(action));

            System.out.println("target = " + action.getTarget().getId() +
                    ", result =  " + actionValue);

            if (actionValue.get(0).equals(result.get(0))) {
                if (actionValue.get(1) > result.get(1)) {
                    result = actionValue;
                    bestMove = action;
                }
                else if (actionValue.get(1).equals(result.get(1))){
                    if (bestMove.getEdge() == null && action.getEdge() != null){
                        result = actionValue;
                        bestMove = action;
                    }
                }
            } else if (actionValue.get(0) > result.get(0)) {
                result = actionValue;
                bestMove = action;
            }

        }
        return bestMove;
    }

    List<Integer> search(State state) {

        if (state.isTerminal()) {

            int myUtility = evalUtility(state.getParent(), true);
            int otherUtility = evalUtility(state.getParent(), false);

            ArrayList<Integer> result = new ArrayList<>(2);
            result.add(myUtility);
            result.add(otherUtility);
            return result;
        } else {

            return searchActions(state);
        }
    }

    List<Integer> searchActions(State state) {
        List<Integer> result = Arrays.asList(0, 0);

        int myIndex, opIndex;
        if(state.getAgent().getAgentNum() == 1){
            myIndex = 0;
            opIndex = 1;
        }
        else{
            myIndex = 1;
            opIndex = 0;
        }

        for (Move action : state.getActions()) {
            List<Integer> actionValue = search(state.getResult(action));
            if (actionValue.get(myIndex).equals(result.get(myIndex))) {
                if (actionValue.get(opIndex) > result.get(opIndex))
                    result = actionValue;
            } else if (actionValue.get(myIndex) > result.get(myIndex)) {
                result = actionValue;
            }
        }
        return result;
    }

    private int evalUtility(State state, boolean evalForMe) {

        int saved;
        int carry;
        Vertex location;

        if ((state.getAgent() == this) == evalForMe) {

            saved = state.getMySaved();
            carry = state.getMyCarryCount();
            location = state.getMyLocation();

        } else {
            saved = state.getOpSaved();
            carry = state.getOpCarryCount();
            location = state.getOpLocation();
        }

        int result = 1000 * saved;
        int canSave = 0;

        if (carry > 0) {
            double lengthToShelter =
                    Simulator.getGraph().getVertex(location.getId())
                            .getLengthToClosestShelter() * (1 + (carry * Simulator.getKFactor()));
            if (state.getTimeElapsed() + lengthToShelter <= Simulator.getDeadline()) {
                canSave += carry ;
            }
        }

        HashMap<Integer, Double> lengthsToPeopleMap = location.getLengthsToPeople();

        for (Map.Entry<Integer, Integer> entry : state.getPeopleMap().entrySet()) {
            if (entry.getValue() > 0) {
                double lengthToShelter =
                        Simulator.getGraph().getVertex(entry.getKey())
                                .getLengthToClosestShelter();
                double lengthToPeople = (lengthsToPeopleMap.get(entry.getKey()));
                if (state.getTimeElapsed() + lengthToPeople
                        + lengthToShelter <= Simulator.getDeadline()) {
                    canSave += entry.getValue();
                }
            }
        }

        int cantSave = Simulator.getTotalPeople() - state.getMySaved() - state.getOpSaved() - canSave;
        result += 10 * (canSave - cantSave);
        return result;
    }
}
