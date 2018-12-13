package HurricaneEvacuationGame;

class GameAgent extends Agent {

    @Override
    Move makeOperation() {


        return maxValue(new State(this));
    }

    /*private Move miniMax(State state){
        
        int result = 0;

        for (Move action: state.getActions()) {
            int actionResult = minValue(state.getResult(action));
            if (actionResult > result)
              result = actionResult;
        }

        return result;

    }*/

    private int minValue(State state){
        if (terminalTest(state))
            return evalUtility(state);
        else{
            int value = Integer.MAX_VALUE;
            for (Move action : state.getActions()) {
                value = Math.min(value, maxValue(state.getResult(action)));
            }
            return value;
        }
    }

    private int maxValue(State state){

        if (terminalTest(state))
            return evalUtility(state);
        else{
            int value = Integer.MIN_VALUE;
            for (Move action : state.getActions()) {
                value = Math.max(value, minValue(state.getResult(action)));
            }
            return value;
        }
    }

}
