package model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Graph {
    private ArrayList<ArrayList<Pair<Integer, Integer>>> graph;//Adjacency list representation
    private int currentNumberOfNodes = 0;
    private Boolean[] MstVisited;
    private PriorityQueue<Vertex> verticesQueue;
    private ArrayList<Vertex> vertices;
    private ArrayList<Pair<Integer, Vertex>> parent;
    private int root;


    public Graph(int nNodes) {
        graph = new ArrayList<>(nNodes);
        currentNumberOfNodes = nNodes;
        generateNewNodes();
    }

    public int getCurrentNumberOfNodes() {
        return currentNumberOfNodes;
    }

    public Graph() {
        graph = new ArrayList<>();
    }

    private void generateNewNodes() {
        int diff = currentNumberOfNodes - graph.size();
        for (int i = 0; i < diff; i++) graph.add(new ArrayList<>());
    }

    public void addNodeConnection(int node, Pair<Integer, Integer> toNodeAndCost) {
        if (node > currentNumberOfNodes - 1) {
            currentNumberOfNodes = node + 1;
            generateNewNodes();
        } else if (toNodeAndCost.getKey() > currentNumberOfNodes - 1) {
            currentNumberOfNodes = toNodeAndCost.getKey() + 1;
            generateNewNodes();
        }
        graph.get(node).add(toNodeAndCost);
    }

    public void addNodeConnectionUndirected(int node, int otherNode, int cost) {
        if (node > currentNumberOfNodes - 1) {
            currentNumberOfNodes = node + 1;
            generateNewNodes();
        } else if (otherNode > currentNumberOfNodes - 1) {
            currentNumberOfNodes = otherNode + 1;
            generateNewNodes();
        }
        this.addNodeConnection(node, new Pair<>(otherNode, cost));
        this.addNodeConnection(otherNode, new Pair<>(node, cost));

    }

    public ArrayList<Pair<Integer, Integer>> getNodeList(int node) {
        return graph.get(node);
    }

    public String processDijkstra(int startNode, int endNode, boolean shortestPathOnly) {
        int size = graph.size();
        ArrayList<Boolean> Visited = new ArrayList<>(size);
        ArrayList<Pair<Integer, Integer>> Distances = new ArrayList<>(size);
        ArrayList<Integer> Parents = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Visited.add(false);
            if (i == startNode) Distances.add(new Pair<>(i, 0));
            else Distances.add(new Pair<>(i, Integer.MAX_VALUE));//setting all nodes except start to inf
            Parents.add(-1);
        }
        if (!shortestPathOnly) return processDijkstraAllPath(startNode, endNode, Distances, Visited, Parents);
        else return processDijkstraShortestPath(startNode, endNode, Distances, Visited, Parents);

    }

    private String processDijkstraAllPath(int startNode, int endNode, ArrayList<Pair<Integer, Integer>> Distances, ArrayList<Boolean> Visited, ArrayList<Integer> Parents) {

        String route = "";
        boolean shortestPathFound = false;
        while (Distances.size() > 0 && !shortestPathFound) {
            Distances.sort((o1, o2) -> o1.getValue() - o2.getValue());
            doDijkstra(Distances.get(0).getKey(), Distances.get(0).getValue(), Distances, Visited, Parents);
            route += Distances.get(0).getKey() + " ";
            if (Distances.get(0).getKey() == endNode && Distances.get(0).getValue() < Integer.MAX_VALUE)
                shortestPathFound = true;
            Distances.remove(0);
        }
        if (!shortestPathFound) return "NO_PATH";
        return route.substring(0, route.length() - 1);
    }

    private String processDijkstraShortestPath(int startNode, int endNode, ArrayList<Pair<Integer, Integer>> Distances, ArrayList<Boolean> Visited, ArrayList<Integer> Parents) {

        boolean shortestPathFound = false;

        while (Distances.size() > 0 && !shortestPathFound) {
            Distances.sort((o1, o2) -> o1.getValue() - o2.getValue());
            doDijkstra(Distances.get(0).getKey(), Distances.get(0).getValue(), Distances, Visited, Parents);

            if (Distances.get(0).getKey() == endNode && Distances.get(0).getValue() < Integer.MAX_VALUE)
                shortestPathFound = true;
            Distances.remove(0);
        }
        if (!shortestPathFound) return "NO_PATH";
        String route = Integer.toString(endNode) + " ";
        int k = endNode;
        while (k != startNode) {
            route += Integer.toString(Parents.get(k)) + " ";
            k = Parents.get(k);
        }
        return Utility.reverseShortestPathString(route);

    }

    private void doDijkstra(int node, int currentCost, ArrayList<Pair<Integer, Integer>> Distances, ArrayList<Boolean> Visited, ArrayList<Integer> Parents) {
        if (Visited.get(node)) return;
        int size = graph.get(node).size();
        for (int i = 0; i < size; i++) {
            if (replaceDistance(graph.get(node).get(i).getKey(), graph.get(node).get(i).getValue() + currentCost, Distances)) {
                Parents.set(graph.get(node).get(i).getKey(), node);
            }

        }

        Visited.set(node, true);
    }

    private boolean replaceDistance(int node, int newDistance, ArrayList<Pair<Integer, Integer>> Distances) {
        int size = Distances.size();
        for (int i = 0; i < size; i++) {
            if (node == Distances.get(i).getKey()) {
                if (newDistance < Distances.get(i).getValue()) {
                    Distances.set(i, new Pair<>(node, newDistance));
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public void doPrims(int Source) {
        MST(this, Source);
        doMst();
        ArrayList<Pair<Integer, Vertex>> edges = getParent();
        Utility.printMinimumSpaningTree(edges);
    }

    public ArrayList<ArrayList<Pair<Integer, Integer>>> getGraph() {
        return graph;
    }

    public void setGraph(ArrayList<ArrayList<Pair<Integer, Integer>>> graph) {
        this.graph = graph;
    }

    public void setCurrentNumberOfNodes(int currentNumberOfNodes) {
        this.currentNumberOfNodes = currentNumberOfNodes;
        generateNewNodes();
    }

    private void MST(Graph graph, int root) {
        this.root = root;
        verticesQueue = new PriorityQueue<>();
        vertices = new ArrayList<>();
        parent = new ArrayList<>();
        int size = graph.getCurrentNumberOfNodes();
        MstVisited = new Boolean[size];
        for (int i = 0; i < size; i++) {
            MstVisited[i] = false;
        }
    }

    private void startMST() {
        int size = this.getCurrentNumberOfNodes();
        for (int i = 0; i < size; i++) {
            if (i == root) {
                vertices.add(new Vertex(i, 0));
                verticesQueue.add(vertices.get(i));
            } else {
                vertices.add(new Vertex(i, Integer.MAX_VALUE));
                verticesQueue.add(vertices.get(i));
            }
        }

        for (int i = 0; i < size; i++) {
            parent.add(null);
        }
    }


    public void doMst() {
        startMST();
        while (!verticesQueue.isEmpty()) {
            Vertex vertexVisited = verticesQueue.poll();
            MstVisited[vertexVisited.getVertex()] = true;
            for (Pair<Integer, Integer> node : this.getNodeList(vertexVisited.getVertex())) {
                /*If weight(u,w) < D(w),D(w) = weight(u,w)*/
                if (!MstVisited[node.getKey()]) {
                    if (node.getValue() < vertices.get(node.getKey()).getWeight())/**&& !MstVisited[node.getKey()]**/ {

                        verticesQueue.remove(vertices.get(node.getKey()));
                        vertices.get(node.getKey()).setWeight(node.getValue());
                        verticesQueue.add(vertices.get(node.getKey()));
                        Integer Parent = node.getKey();
                        Pair<Integer, Vertex> newParent = new Pair(node.getKey(), new Vertex(vertexVisited.getVertex(),node.getValue()));
                        parent.set(node.getKey(), newParent);
                    }
                }
            }
        }
    }

    public int getDistance(String[] path) {
        int cost = 0;
        for (int i = 0; i < path.length - 1; i++) {
            int currentNode = Integer.parseInt(path[i]);
            for (int j = 0; j < graph.get(currentNode).size(); j++) {
                if (graph.get(currentNode).get(j).getKey() == Integer.parseInt(path[i + 1])) {
                    cost += graph.get(currentNode).get(j).getValue();
                    break;
                }
            }
        }
        return cost;

    }

    public ArrayList<Pair<Integer, Vertex>> getParent() {
        return parent;
    }

    public void setParent(ArrayList<Pair<Integer, Vertex>> parent) {
        this.parent = parent;
    }

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }

}
