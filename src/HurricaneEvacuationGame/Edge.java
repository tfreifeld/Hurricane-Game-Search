package HurricaneEvacuationGame;

class Edge {

    private Vertex in;
    private Vertex out;
    private int weight;
    private boolean blocked;

    Edge(Vertex in, Vertex out, int weight) {
        this.in = in;
        this.out = out;
        this.weight = weight;
        this.blocked = false;

        in.submitEdge(this);
        out.submitEdge(this);
    }

    boolean isBlocked() {
        return blocked;
    }

    Vertex getNeighbour(Vertex vertex) {
        if (vertex.equals(in)) {
            return out;
        } else {
            return in;
        }
    }

    int getWeight() {
        return weight;
    }

    void setBlocked() {
        this.blocked = true;
    }
}
