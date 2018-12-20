package HurricaneEvacuationGame;

import java.util.Arrays;
import java.util.List;

class CooperativeAgent extends SemiCooperativeAgent {

    CooperativeAgent(int agentNum) {
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

            int actionSum = actionValue.get(0) + actionValue.get(1);
            int resultSum = result.get(0) + result.get(1);

            if (actionSum > resultSum) {
                result = actionValue;
                bestMove = action;
            }
            else if (actionSum == resultSum){
                if (bestMove.getEdge() == null && action.getEdge() != null){
                    result = actionValue;
                    bestMove = action;
                }
            }

        }
        return bestMove;

    }

    @Override
    List<Integer> searchActions(State state) {
        List<Integer> result = Arrays.asList(0, 0);

        for (Move action : state.getActions()) {
            List<Integer> actionValue = search(state.getResult(action));

            int actionSum = actionValue.get(0) + actionValue.get(1);
            int resultSum = result.get(0) + result.get(1);

            if (actionSum > resultSum) {
                result = actionValue;
            }
        }
        return result;
    }
}
