package HurricaneEvacuationGame;

import java.util.*;

class Vertex {

    private int id;
    private int persons;
    private boolean shelter;
    private HashMap<Integer,Edge> edges;
    private ArrayList<OnPeopleChangeListener> mListeners;
    private HashMap<Integer,Double> lengthsToPeople;
    private double lengthToClosestShelter;

    Vertex(int id) {
        this.id = id;
        this.persons = 0;
        this.shelter = false;
        this.edges = new HashMap<>();
        this.mListeners = new ArrayList<>();
        this.lengthsToPeople = new HashMap<>();
        this.lengthToClosestShelter = Double.POSITIVE_INFINITY;

    }

    void submitEdge(Edge edge){

        this.edges.put(edge.getNeighbour(this).getId(),edge);

    }

    void setPersons(int persons) {
        this.persons = persons;
        if(persons == 0){
            for (OnPeopleChangeListener listener:mListeners) {
                listener.onPeopleChange();
            }
        }
    }

    void setShelter() {
        this.shelter = true;
    }

    int getPersons() {
        return persons;
    }

    boolean isShelter() {
        return shelter;
    }

    HashMap<Integer, Edge> getEdges() {
        return edges;
    }

    int getId() {
        return id;
    }

    Set<Integer> getNeighbours(){

        return edges.keySet();

    }

    /**
     * Returns an edge to the requested neighbour.
     * @param id Requested neighbour's id.
     * @return Edge to the neighbour.
     * @throws NotNeighbourException if a neighbour with the requested id was not found.
     */
    Edge getNeighbour(int id) throws NotNeighbourException{

        if(getNeighbours().contains(id))
            return edges.get(id);
        else {
            throw new NotNeighbourException();
        }
    }

    String getNeighboursToString(){

        Iterator<Integer> iterator = getNeighbours().iterator();

        StringBuilder ans = new StringBuilder("[");

        while (iterator.hasNext()){
            ans.append(iterator.next());
            if (iterator.hasNext()){
                ans.append(", ");
            }
        }

        ans.append("]");
        return ans.toString();
    }

    void runLengthsSearch(){

        HashMap<Integer, Integer> peopleMap = Simulator.getInitialPeopleMap();
        for (Map.Entry<Integer, Integer> next : peopleMap.entrySet()) {
            int targetVertex = next.getKey();
            double length = new UniformSearch(this,
                    node -> node.getState().getLocation().getId() == targetVertex, null)
                    .run().getPathCost();
            lengthsToPeople.put(targetVertex, length);
        }
        this.lengthToClosestShelter = new UniformSearch(
                this, node -> node.getState().getLocation().isShelter(), null)
                .run().getPathCost();

    }

    double getLengthToClosestShelter() {
        return lengthToClosestShelter;
    }

    HashMap<Integer, Double> getLengthsToPeople() {
        return lengthsToPeople;
    }

    void registerListener(OnPeopleChangeListener listener){

        this.mListeners.add(listener);
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }

    class NotNeighbourException extends Throwable {
    }


}

interface OnPeopleChangeListener{

    void onPeopleChange();

}
