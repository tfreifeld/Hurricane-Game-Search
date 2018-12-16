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
                    state -> minValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE),
                    (actionResult, result) -> actionResult > result);
        } else {
            return miniMax(new State(this), Integer.MAX_VALUE,
                    state -> maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE),
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
            System.out.println("target = " + action.getTarget().getId()  +
                    ", result =  " + actionResult);
            if (compare.test(actionResult, result)) {
                result = actionResult;
                bestMove = action;
            }
            /* Between moves with equal utility, choose randomly */
            else if (actionResult == result){
                if(Math.random() > 0.5){
                    bestMove = action;
                }
            }
        }

        return bestMove;

    }

    private int minValue(State state, int alpha, int beta) {
        if (isTerminal(state))
            return evalUtility(state);
        else {
            int value = Integer.MAX_VALUE;
            for (Move action : state.getActions()) {
                value = Math.min(value, maxValue(state.getResult(action), alpha, beta));
                if (value <= alpha)
                    return value;
                beta = Math.min(beta, value);
            }
            return value;
        }
    }

    private int maxValue(State state, int alpha, int beta) {

        if (isTerminal(state))
            return evalUtility(state);
        else {
            int value = Integer.MIN_VALUE;
            for (Move action : state.getActions()) {
                value = Math.max(value, minValue(state.getResult(action),alpha , beta));
                if (value >= beta)
                    return value;
                alpha = Math.max(alpha, value);
            }
            return value;
        }
    }

    private boolean isTerminal(State state) {

        return (state.getTimeElapsed() >= Simulator.getDeadline()) ||
                (state.getCounter() == Simulator.getCutoffLimit());
    }

    private int evalUtility(State state) {



        int agentOneSaved, agentTwoSaved, agentOneCarry, agentTwoCarry;
        Vertex agentOneLocation, agentTwoLocation;
        if(state.getAgent().getAgentNum() == 1){
            agentOneSaved = state.getMySaved();
            agentTwoSaved = state.getOpSaved();
            agentOneCarry = state.getMyCarryCount();
            agentTwoCarry = state.getOpCarryCount();
            agentOneLocation = state.getMyLocation();
            agentTwoLocation = state.getOpLocation();
        }
        else{
            agentOneSaved = state.getOpSaved();
            agentTwoSaved = state.getMySaved();
            agentOneCarry = state.getOpCarryCount();
            agentTwoCarry = state.getMyCarryCount();
            agentOneLocation = state.getOpLocation();
            agentTwoLocation = state.getMyLocation();
        }

        int result = 100 * (agentOneSaved - agentTwoSaved) + 50 *(agentOneCarry - agentTwoCarry);


        HashMap<Integer, Double> agentOneLengthsToPeople = agentOneLocation.getLengthsToPeople();
        HashMap<Integer, Double> agentTwoLengthsToPeople = agentTwoLocation.getLengthsToPeople();

        for (Map.Entry<Integer, Integer> entry : state.getPeopleMap().entrySet()) {
            if (entry.getValue() > 0) {

                double agentOneLengthToPeople = agentOneLengthsToPeople.get(entry.getKey());
                double agentTwoLengthToPeople = agentTwoLengthsToPeople.get(entry.getKey());

                if (agentOneLengthToPeople < agentTwoLengthToPeople) {
                    result += entry.getValue();
                }
                else if (agentOneLengthToPeople > agentTwoLengthToPeople){
                    result -= entry.getValue();
                }
            }
        }



        return result;
    }
}
