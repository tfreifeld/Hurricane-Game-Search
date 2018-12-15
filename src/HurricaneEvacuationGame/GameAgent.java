package HurricaneEvacuationGame;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

class GameAgent extends Agent {

    GameAgent(int agentNum) {
        super(agentNum);
    }

    @Override
    Move makeOperation() {

        if (getAgentNum() == 1) {

            return miniMax(new State(this), Integer.MIN_VALUE,
                    this::minValue,
                    (actionResult, result) -> actionResult > result);
        } else {
            return miniMax(new State(this), Integer.MAX_VALUE,
                    this::maxValue,
                    (actionResult, result) -> actionResult < result);
        }
    }

    private Move miniMax(State state, int initialResult,
                         Function<State, Integer> funcValue,
                         BiPredicate<Integer, Integer> compare) {

        int result = initialResult;
        Move bestMove = new Move(this, getLocation(), null);

        for (Move action : state.getActions()) {
            int actionResult = funcValue.apply(state.getResult(action));
            if (compare.test(actionResult, result)) {
                result = actionResult;
                bestMove = action;
            }
        }

        return bestMove;

    }

    private int minValue(State state) {
        if (isTerminal(state))
            return evalUtility(state);
        else {
            int value = Integer.MAX_VALUE;
            for (Move action : state.getActions()) {
                value = Math.min(value, maxValue(state.getResult(action)));
            }
            return value;
        }
    }

    private int maxValue(State state) {

        if (isTerminal(state))
            return evalUtility(state);
        else {
            int value = Integer.MIN_VALUE;
            for (Move action : state.getActions()) {
                value = Math.max(value, minValue(state.getResult(action)));
            }
            return value;
        }
    }

    private boolean isTerminal(State state) {

        return (state.getTimeElapsed() >= Simulator.getDeadline()) ||
                (state.getCounter() == Simulator.getCutoffLimit());
    }

    private int evalUtility(State state) {



        int agentOneSaved, agentTwoSaved;
        Vertex agentOneLocation, agentTwoLocation;
        if(state.getAgent().getAgentNum() == 1){
            agentOneSaved = state.getMySaved();
            agentTwoSaved = state.getOpSaved();
            agentOneLocation = state.getMyLocation();
            agentTwoLocation = state.getOpLocation();
        }
        else{
            agentOneSaved = state.getOpSaved();
            agentTwoSaved = state.getMySaved();
            agentOneLocation = state.getOpLocation();
            agentTwoLocation = state.getMyLocation();
        }

        int result = 100 * (agentOneSaved - agentTwoSaved);

        HashMap<Integer, Double> agentOneLengthsToPeople = agentOneLocation.getLengthsToPeople();
        HashMap<Integer, Double> agentTwoLengthsToPeople = agentTwoLocation.getLengthsToPeople();

        for (Map.Entry<Integer, Integer> entry : state.getPeopleMap().entrySet()) {
            if (entry.getValue() > 0) {

//                double lengthToShelter =
//                        Simulator.getGraph().getVertex(entry.getKey()).getLengthToClosestShelter()
                double agentOneLengthToPeople = agentOneLengthsToPeople.get(entry.getKey());
                double agentTwoLengthToPeople = agentTwoLengthsToPeople.get(entry.getKey());

                if (agentOneLengthToPeople < agentTwoLengthToPeople) {
                    result += entry.getValue();
                }
                else{
                    result -= entry.getValue();
                }
            }
        }

        return result;
    }
}
