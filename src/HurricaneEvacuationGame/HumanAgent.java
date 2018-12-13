package HurricaneEvacuationGame;

import java.util.InputMismatchException;

class HumanAgent extends Agent {

    HumanAgent(int agentNum) {

        this.agentNum = agentNum;
    }

    @Override
    public Move makeOperation() {

        System.out.println("Choose a neighbour vertex from "
                        + location.getNeighboursToString() + " to move to, or 0 for NoOp: ");

        int targetVertex;
        Edge edge;
        while(true) {
            try {

                 targetVertex = Simulator.sc.nextInt();
                 if (targetVertex == 0){
                     /*NoOp*/
                     targetVertex = this.getLocation().getId();
                     edge = null;
                     break;
                 }

                 edge = location.getNeighbour(targetVertex);
                 break;

            } catch (InputMismatchException e) {
                Simulator.sc.next();
                System.out.println("Invalid option.");
            } catch (Vertex.NotNeighbourException e) {
                System.out.println("Not a neighbour.");
            }
        }

        return new Move(this, Simulator.getGraph().getVertex(targetVertex), edge);
    }

    @Override
    public String toString() {
            return "{Type: Human\n" + super.toString();
    }
}
