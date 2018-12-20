package HurricaneEvacuationGame;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

class AdversarialAgent extends Agent {

    AdversarialAgent(int agentNum) {
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

             System.out.println("target = " + action.getTarget().getId() +
                    ", result =  " + actionResult);

            if (compare.test(actionResult, result)) {
                result = actionResult;
                bestMove = action;
            }

            /* Between moves with equal utility choose the
             * least time consuming, or if agent carries,
             * choose least time consuming shelter */
            else if (actionResult == result) {
                if (getCarrying() > 0) {
                    if (action.getTarget().isShelter())
                        if (bestMove.getTarget().isShelter()) {
                            bestMove = breakTie(bestMove, action);
                        } else {
                            bestMove = action;
                        }
                }
                else {
                    bestMove = breakTie(bestMove, action);
                }
            }
        }

        return bestMove;

    }

    private Move breakTie(Move bestMove, Move action) {

        if (bestMove.getEdge() == null && action.getEdge() == null){
            return bestMove;
        }
        else if (bestMove.getEdge() == null){
            return action;
        }
        else if (action.getEdge() == null){
            return bestMove;
        }
        else {
            double bestMoveTraverseTime =
                    Simulator.computeTraverseTime
                            (getCarrying(), bestMove.getEdge().getWeight());
            double actionTraverseTime =
                    Simulator.computeTraverseTime
                            (getCarrying(), action.getEdge().getWeight());
            if (actionTraverseTime < bestMoveTraverseTime) {
                bestMove = action;
            }
            return bestMove;
        }
    }

    private int minValue(State state, int alpha, int beta) {
        if (state.isTerminal())
            return evalUtility(state.getParent());
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

        if (state.isTerminal())
            return evalUtility(state.getParent());
        else {
            int value = Integer.MIN_VALUE;
            for (Move action : state.getActions()) {
                value = Math.max(value, minValue(state.getResult(action), alpha, beta));
                if (value >= beta)
                    return value;
                alpha = Math.max(alpha, value);
            }
            return value;
        }
    }

    private static int evalUtility(State state) {


        int agentOneSaved, agentTwoSaved, agentOneCarry, agentTwoCarry;
        Vertex agentOneLocation, agentTwoLocation;
        if (state.getAgent().getAgentNum() == 1) {
            agentOneSaved = state.getMySaved();
            agentTwoSaved = state.getOpSaved();
            agentOneCarry = state.getMyCarryCount();
            agentTwoCarry = state.getOpCarryCount();
            agentOneLocation = state.getMyLocation();
            agentTwoLocation = state.getOpLocation();
        } else {
            agentOneSaved = state.getOpSaved();
            agentTwoSaved = state.getMySaved();
            agentOneCarry = state.getOpCarryCount();
            agentTwoCarry = state.getMyCarryCount();
            agentOneLocation = state.getOpLocation();
            agentTwoLocation = state.getMyLocation();
        }

        int result = 1000 * (agentOneSaved - agentTwoSaved) + 50 * (agentOneCarry - agentTwoCarry);


        HashMap<Integer, Double> agentOneLengthsToPeople = agentOneLocation.getLengthsToPeople();
        HashMap<Integer, Double> agentTwoLengthsToPeople = agentTwoLocation.getLengthsToPeople();

        for (Map.Entry<Integer, Integer> entry : state.getPeopleMap().entrySet()) {
            if (entry.getValue() > 0) {

                double agentOneLengthToPeople = agentOneLengthsToPeople.get(entry.getKey());
                double agentTwoLengthToPeople = agentTwoLengthsToPeople.get(entry.getKey());

                if (agentOneLengthToPeople < agentTwoLengthToPeople) {
                    result += entry.getValue();
                } else if (agentOneLengthToPeople > agentTwoLengthToPeople) {
                    result -= entry.getValue();
                }
            }
        }


        return result;
    }
}
