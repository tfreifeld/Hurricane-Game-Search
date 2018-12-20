package HurricaneEvacuationGame;

import java.util.*;
import java.util.function.Predicate;

class UniformSearch {

    private PriorityQueue<Node> fringe;
    private Predicate<Node> goalTest;

    UniformSearch(Vertex location, Predicate<Node> goalTest) {
        this.fringe = new PriorityQueue<>();
        this.goalTest = goalTest;
        this.fringe.add(new UniformSearchNode(location));
    }

    Node run() {

        while (true) {

            if (fringe.isEmpty()) {

                return null;
            }

            Node currentNode = fringe.poll();

            if (checkReturnCondition(currentNode)) return currentNode;

            for (Edge nextEdge : currentNode.getState().getLocation().getEdges().values()) {

                if (nextEdge.isBlocked()) {
                    continue;
                }

                Node child = createChildNode(nextEdge, currentNode);

                boolean inFringe = false;

                for (Node node : fringe) {

                    if (node.getState().equals(child.state)) {

                        inFringe = true;

                        if (node.compareTo(child) > 0) {
                            fringe.remove(node);
                            fringe.add(child);
                            break;
                        }
                    }
                }
                if (!inFringe) {
                        fringe.add(child);
                    }
                }
            }
        }

    private boolean checkReturnCondition(Node currentNode) {
        return goalTest.test(currentNode);
    }

    private Node createChildNode(Edge edge, Node currentNode) {
        return new UniformSearchNode(edge.getNeighbour
                (currentNode.getState().getLocation()), (UniformSearchNode) currentNode, edge);
    }

    protected static class UniformSearchNode extends Node {

        UniformSearchNode(Vertex location) {
            super();
            this.state = new SearchState(location);
        }

        UniformSearchNode(Vertex location, UniformSearchNode parent, Edge edge) {
            super(parent);
            this.pathCost = parent.getPathCost() + edge.getWeight();
            this.state = new SearchState(location);

        }

        @Override
        public int compareTo(Node o) {

            int result = Float.compare(this.getPathCost(), o.getPathCost());
            if (result == 0) {
                /* If nodes have the same path cost, compare according to number of people */
                return Integer.compare(o.getState().getLocation().getPersons(),
                                        this.getState().getLocation().getPersons());
            } else {
                return result;
            }

        }
    }
}

abstract class Node implements Comparable<Node> {

    float pathCost;
    private Node parent;
    private ArrayList<Node> children;
    SearchState state;


    Node() {

        this.pathCost = 0;
        this.children = new ArrayList<>();
        this.parent = null;
    }

    Node(Node parent) {
        this.parent = parent;
        this.parent.getChildren().add(this);
        this.children = new ArrayList<>();

    }

    float getPathCost() {
        return pathCost;
    }

    private ArrayList<Node> getChildren() {
        return children;
    }

    SearchState getState() {
        return state;
    }

    @Override
    abstract public int compareTo(Node o);
}

class SearchState {

    final private Vertex location;

    SearchState(Vertex location) {
        this.location = location;
    }

    Vertex getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SearchState)) {
            return false;
        } else {
            return (this.getLocation().equals(((SearchState) obj).getLocation()));

        }
    }

}

