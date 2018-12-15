package HurricaneEvacuationGame;

import java.io.File;
import java.util.*;

public class Simulator {

    private static List<Agent> agents;
    private static Graph graph;
    private static int deadline;
    private static double time = 0;
    private static int safeCount = 0;
    private static int totalPeople = 0;
    private static float kFactor;
    private static int cutoffLimit = 0;
    private static HashMap<Integer, Integer> peopleMap = new HashMap<>();

    static Scanner sc = new Scanner(System.in);


    public static void main(String[] args) {

        graph = new Graph(new File(args[0]));
        graph.constructGraph();

        readInputFromUser();
        for (int i = 1; i <= graph.getNumberOfVertices(); i++) {
            graph.getVertex(i).runLengthsSearch();
        }

        getGraph().displayGraphState();
        System.out.println();

        while (time < getDeadline()) {
            for (int i = 0; i < agents.size() && time < getDeadline(); i++) {
                Agent agent = agents.get(i);
                Move move = agent.makeOperation();
                System.out.print("Agent " + (i + 1) + "'s turn: ");
                makeMove(move);
                displayWorldState();
            }
        }

        System.out.println("Deadline has been reached!");

        System.out.println("Performance measure:");
        for (Agent agent : agents) {
            System.out.println("Agent " + agent.getAgentNum() + ":");
            agent.performanceMeasure();

        }

        sc.close();


    }

    private static void makeMove(Move move) {

        if (move.getEdge() == null) {
            /*NoOp*/
            System.out.println("NoOp");
            time++;
        } else if (move.getEdge().isBlocked()) {
            System.out.println("traverse failed - edge blocked");
            /*Edge is blocked*/
            time++;
        } else {
            double tempTime =
                    time + computeTraverseTime(move.getAgent().getCarrying(), move.getEdge().getWeight());
            if (!(tempTime > getDeadline())) {
                /*If deadline isn't breached*/
                System.out.println("traverse - " + move.getAgent().getLocation().getId()
                        + " to " + move.getTarget().getId());
                move.getAgent().traverse(move.getTarget());
                time = tempTime;
                peopleMap.replace(move.getTarget().getId(), 0);
            } else {
                System.out.println("traverse failed - will breach deadline");
                /*If deadline is breached, traverse fails*/
                time++;
            }
        }

        move.getAgent().increaseMoves();

    }

    private static void readInputFromUser() {

        System.out.println
                ("Please enter the type of agents.\n" +
                        "Type 'h' for human or 'g' for game search:");
        String agentType = sc.next();
        while (!agentType.matches("[h|g]")) {
            System.out.println("Invalid option.");
            agentType = sc.next();
        }

        ArrayList<Agent> agents = new ArrayList<>(2);

        switch (agentType) {

            case "h": {
                agents.add(new HumanAgent(1));
                agents.add(new HumanAgent(2));
                break;
            }
            case "g": {

                System.out.println("Please choose a type of game:");
                System.out.println("Type 'a' for adversarial game," +
                        "'s' for a semi-cooperative game or 'f'" +
                        " for a fully cooperative game:");
                String gameType = sc.next();
                while (!gameType.matches("[a|s|f]")) {
                    System.out.println("Invalid option.");
                    gameType = sc.next();
                }

                System.out.println("Please choose a cutoff limit for the tree:");
                while (true) {
                    try {
                        cutoffLimit = sc.nextInt();
                        if (cutoffLimit <= 0) {
                            System.out.println("cutoff limit must be positive.");
                            continue;
                        }
                        break;
                    } catch (InputMismatchException e) {
                        sc.next();
                        System.out.println("Invalid option.");
                    }
                }

                agents.add(new GameAgent(1));
                agents.add(new GameAgent(2));
                break;
            }
        }

        agents.get(0).setOpponent(agents.get(1));
        agents.get(1).setOpponent(agents.get(0));

        Simulator.agents = Collections.unmodifiableList(agents);

        for (int i = 0; i < 2; i++) {

            System.out.println
                    ("Please enter the number of start" +
                            " vertex for agent " + (i + 1) + ": ");
            int startVertex;

            while (true) {

                try {
                    startVertex = sc.nextInt();
                    if (startVertex > graph.getNumberOfVertices()) {
                        System.out.println("There are only " + graph.getNumberOfVertices()
                                + " vertices.");
                        continue;
                    } else if (startVertex <= 0) {
                        System.out.println("Invalid option.");
                        continue;
                    }
                    agents.get(i).traverse(graph.getVertex(startVertex));
                    break;
                } catch (InputMismatchException e) {
                    sc.next();
                    System.out.println("Invalid option.");
                }
            }
        }

        System.out.println("Please enter the \"slow-down\" constant: ");
        while (true) {
            try {
                kFactor = sc.nextFloat();
                if (kFactor < 0) {
                    System.out.println("K must be non-negative.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                sc.next();
                System.out.println("Invalid option.");
            }
        }
        System.out.println();
    }

    private static void displayWorldState() {

//        System.out.println("----------------");
//        graph.displayGraphState();


        for (Agent agent : agents) {
            System.out.println("Agent " + agent.getAgentNum() + ":");
            System.out.println(agent.toString());
            System.out.println();
        }
        System.out.println("----------------");

        if (safeCount == 1) {
            System.out.println(safeCount + " person is safe");
        } else {
            System.out.println(safeCount + " people are safe");
        }

        System.out.println("Time: " + time);
        System.out.println("\n\n");

    }

    static List<Agent> getAgents() {
        return agents;
    }

    static int getCutoffLimit() {
        return cutoffLimit;
    }

    static int getDeadline() {
        return deadline;
    }

    static void setDeadline(int deadline) {
        Simulator.deadline = deadline;
    }

    private static float getKFactor() {
        return kFactor;
    }

    static int getSafeCount() {
        return safeCount;
    }

    static void setSafeCount(int safeCount) {
        Simulator.safeCount = safeCount;
    }

    static Graph getGraph() {
        return graph;
    }

    static int getTotalPeople() {
        return totalPeople;
    }

    static double getTime() {
        return time;
    }

    static double computeTraverseTime(int carrying, int weight){

        return (carrying * getKFactor() + 1) * weight;
    }

    static void addToPeopleMap(int key, int value) {
        peopleMap.put(key,value);
    }

    static HashMap<Integer, Integer> getPeopleMapCopy() {
        return new HashMap<>(peopleMap);
    }

    static void increaseTotalPeople(int people) {
        Simulator.totalPeople += people;
    }

    static HashMap<Integer, Integer> getInitialPeopleMap() {
        HashMap<Integer, Integer> peopleMap = new HashMap<>();
        for (Map.Entry<Integer, Vertex> v : getGraph().getVertices().entrySet()) {
            if (v.getValue().getPersons() > 0) {
                peopleMap.put(v.getKey(), v.getValue().getPersons());
            }
        }
        return peopleMap;
    }
}
