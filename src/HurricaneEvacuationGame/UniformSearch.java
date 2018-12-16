package HurricaneEvacuationGame;

import java.util.function.Predicate;

class UniformSearch extends Search {

    UniformSearch(Vertex location, Predicate<Node> goalTest, Agent agent) {
        super(goalTest, agent);
        this.fringe.add(new UniformSearchNode(location));
    }

    @Override
    Node createChildNode(Edge edge, Node currentNode) {
        return new UniformSearchNode(edge.getNeighbour
                (currentNode.getState().getLocation()), (UniformSearchNode) currentNode, edge);
    }

    static private class UniformSearchNode extends Node {

        UniformSearchNode(Vertex location) {
            super();
            this.state = new SearchState(null, location, -1);
        }

        UniformSearchNode(Vertex location, UniformSearchNode parent, Edge edge) {
            super(parent);
            this.pathCost = parent.getPathCost() + edge.getWeight();
            this.state = new SearchState(null, location, -1);

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
