package HurricaneEvacuationGame;

class Move {

    /* Class implementing move intention.
     * NoOp is represented with the current
      * vertex, and a null edge.*/

    private final Agent agent;
    private final Vertex target;
    private final Edge edge;

    Move(Agent agent, Vertex target, Edge edge) {
        this.agent = agent;
        this.target = target;
        this.edge = edge;
    }

    Agent getAgent() {
        return agent;
    }

    Vertex getTarget() {
        return target;
    }

    Edge getEdge() {
        return edge;
    }
}
