package controller;

import EXTERNAL_LIBRARIES.MazeGenerator;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.util.Pair;
import model.Graph;
import model.Utility;

import javax.naming.ldap.Control;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Controller {
    public final int mazeWidth = 10;
    public final int mazeHeight = 14;
    public final int SqrLength = 16;
    public final int SqrMargin = 2;
    public final String shortestPathColor = "00633f";
    public final String alongTheWayColor = "1462e8";
    public final String blockColor = "000000";
    public final String normalColor = "ffffff";
    public final String startColor = "00ff00";
    public final String endColor = "#ff0000";
    public final String nodeColor = "eeff32";
    private static Controller instance = null;
    private Pair<Integer, Integer> start = null;
    private Pair<Integer, Integer> end = null;
    HashMap<Pair<Integer, Integer>, Integer> nodeIDs;


    MazeGenerator maze = new MazeGenerator(mazeWidth, mazeHeight);


    private Controller() {

    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public ArrayList<ArrayList<Integer>> mazeInfo() {
        maze = new MazeGenerator(mazeWidth, mazeHeight);
        String mazeShape = maze.display();
        ArrayList<ArrayList<Integer>> finalMaze = new ArrayList<>();
        int len = mazeShape.length();
        int k = 0;
        finalMaze.add(new ArrayList<>());
        for (int i = 0; i < len; i++) {
            if (mazeShape.charAt(i) != ' ' && mazeShape.charAt(i) != '\n') {
                finalMaze.get(k).add(0);
            } else if (mazeShape.charAt(i) != '\n') {
                finalMaze.get(k).add(1);
            }
            if (mazeShape.charAt(i) == '\n') {
                finalMaze.add(new ArrayList<>());
                k++;
            }
        }
        return finalMaze;
    }

    public Graph getGraphFromMaze(ArrayList<ArrayList<Integer>> maze) {
        Graph g = new Graph();
        if (start == null || end == null) return g;
        ArrayList<ArrayList<Boolean>> Visited = Utility.getVisitedArray(maze.get(0).size(), maze.size());
        makeNodes(Visited, maze, start);
        makeNodes(Visited, maze, end);
        //make corners as nodes
        for (int i = 1; i < maze.size() - 1; i++) {
            for (int j = 1; j < maze.get(0).size() - 1; j++) {
                if (maze.get(i).get(j) == 1 && maze.get(i).get(j - 1) == 0 && maze.get(i - 1).get(j) == 0) {
                    makeNodes(Visited, maze, new Pair<>(j, i));
                } else if (maze.get(i).get(j) == 1 && maze.get(i).get(j + 1) == 0 && maze.get(i + 1).get(j) == 0) {
                    makeNodes(Visited, maze, new Pair<>(j, i));
                } else if (maze.get(i).get(j) == 1 && maze.get(i).get(j + 1) == 0 && maze.get(i - 1).get(j) == 0) {
                    makeNodes(Visited, maze, new Pair<>(j, i));
                } else if (maze.get(i).get(j) == 1 && maze.get(i).get(j - 1) == 0 && maze.get(i + 1).get(j) == 0) {
                    makeNodes(Visited, maze, new Pair<>(j, i));
                }
            }
        }
        makeNodeConnections(maze, g);
        return g;
    }

    private void makeNodes(ArrayList<ArrayList<Boolean>> Visited, ArrayList<ArrayList<Integer>> maze, Pair<Integer, Integer> currentNode) {
        if (Visited.get(currentNode.getValue()).get(currentNode.getKey())) {
            return;
        } else {
            Visited.get(currentNode.getValue()).set(currentNode.getKey(), true);
        }

        Pair<Integer, Integer> right = nextRight(maze, currentNode.getKey(), currentNode.getValue());
        if (!canChangePos(maze, right)) maze.get(right.getValue()).set(right.getKey(), 6);

        Pair<Integer, Integer> left = nextLeft(maze, currentNode.getKey(), currentNode.getValue());
        if (!canChangePos(maze, left)) maze.get(left.getValue()).set(left.getKey(), 6);

        Pair<Integer, Integer> up = nextUp(maze, currentNode.getKey(), currentNode.getValue());
        if (!canChangePos(maze, up)) maze.get(up.getValue()).set(up.getKey(), 6);

        Pair<Integer, Integer> down = nextDown(maze, currentNode.getKey(), currentNode.getValue());
        if (!canChangePos(maze, down)) maze.get(down.getValue()).set(down.getKey(), 6);

        makeNodes(Visited, maze, right);
        makeNodes(Visited, maze, left);
        makeNodes(Visited, maze, up);
        makeNodes(Visited, maze, down);

    }

    private boolean canChangePos(ArrayList<ArrayList<Integer>> maze, Pair<Integer, Integer> pos) {
        return maze.get(pos.getValue()).get(pos.getKey()) != 1;
    }

    private Pair<Integer, Integer> nextRight(ArrayList<ArrayList<Integer>> maze, int x, int y) {
        while (maze.get(y).get(x) != 0) {
            x++;
        }
        return new Pair<>(x - 1, y);
    }

    private Pair<Integer, Integer> nextLeft(ArrayList<ArrayList<Integer>> maze, int x, int y) {
        while (maze.get(y).get(x) != 0) {
            x--;
        }
        return new Pair<>(x + 1, y);
    }

    private Pair<Integer, Integer> nextUp(ArrayList<ArrayList<Integer>> maze, int x, int y) {
        while (maze.get(y).get(x) != 0) {
            y--;
        }
        return new Pair<>(x, y + 1);
    }

    private Pair<Integer, Integer> nextDown(ArrayList<ArrayList<Integer>> maze, int x, int y) {
        while (maze.get(y).get(x) != 0) {
            y++;
        }
        return new Pair<>(x, y - 1);
    }

    private void makeNodeConnections(ArrayList<ArrayList<Integer>> maze, Graph g) {
        nodeIDs = new HashMap<>();
        ArrayList<ArrayList<Boolean>> Visited = Utility.getVisitedArray(maze.get(0).size(), maze.size());
        for (int i = 0; i < maze.size() - 1; i++) {
            for (int j = 0; j < maze.get(0).size() - 1; j++) {
                if (!Visited.get(i).get(j) && (maze.get(i).get(j) == 6 || maze.get(i).get(j) == 2 || maze.get(i).get(j) == 3)) {
                    Visited.get(i).set(j, true);
                    Pair<Integer, Integer> right = connectToNextRight(j, i, maze);
                    Pair<Integer, Integer> down = connectToNextDown(j, i, maze);
                    if (right.getKey() != -1) connectNodes(new Pair(j, i), right, g, nodeIDs);
                    if (down.getKey() != -1) connectNodes(new Pair(j, i), down, g, nodeIDs);
                }
            }
        }
    }

    private Pair<Integer, Integer> connectToNextRight(int x, int y, ArrayList<ArrayList<Integer>> maze) {
        for (int i = x + 1; i < maze.get(0).size(); i++) {
            if (maze.get(y).get(i) == 0) return new Pair<>(-1, -1);
            if (maze.get(y).get(i) == 6 || maze.get(y).get(i) == 3 || maze.get(y).get(i) == 2) {
                for (int j = x + 1; j < i; j++) {
                    if (maze.get(y).get(j) != 6 && maze.get(y).get(j) != 3 && maze.get(y).get(j) != 2)
                        maze.get(y).set(j, 4);

                }
                return new Pair<>(i, y);

            }
        }
        return new Pair<>(-1, -1);
    }

    private Pair<Integer, Integer> connectToNextDown(int x, int y, ArrayList<ArrayList<Integer>> maze) {
        for (int i = y + 1; i < maze.size(); i++) {
            if (maze.get(i).get(x) == 0) return new Pair<>(-1, -1);
            if (maze.get(i).get(x) == 6 || maze.get(i).get(x) == 3 || maze.get(i).get(x) == 2) {
                for (int j = y + 1; j < i; j++) {
                    if (maze.get(j).get(x) != 6 && maze.get(j).get(x) != 3 && maze.get(j).get(x) != 2)
                        maze.get(j).set(x, 4);
                }
                return new Pair<>(x, i);
            }

        }
        return new Pair<>(-1, -1);
    }

    private void connectNodes(Pair<Integer, Integer> node, Pair<Integer, Integer> otherNode, Graph g, HashMap<Pair<Integer, Integer>, Integer> nodeIDs) {
        int nodeID;
        if (nodeIDs.get(node) != null) {
            nodeID = nodeIDs.get(node);
        } else {
            nodeID = g.getCurrentNumberOfNodes();
            g.setCurrentNumberOfNodes(nodeID + 1);
            nodeIDs.put(node, nodeID);
        }
        int otherNodeID;
        if (nodeIDs.get(otherNode) != null) {
            otherNodeID = nodeIDs.get(otherNode);
        } else {
            otherNodeID = g.getCurrentNumberOfNodes();
            g.setCurrentNumberOfNodes(nodeID + 1);
            nodeIDs.put(otherNode, otherNodeID);
        }
        //either on same row or same col
        int distance = node.getValue() - otherNode.getValue() != 0 ?
                Math.abs(node.getValue() - otherNode.getValue()) : Math.abs(node.getKey() - otherNode.getKey());
        g.addNodeConnectionUndirected(nodeID, otherNodeID, distance);

    }

    public boolean processDijkstra(ArrayList<ArrayList<Integer>> maze, Graph g) {
        if (g == null || start == null || end == null || nodeIDs == null) return false;
        String shortestPath = g.processDijkstra(nodeIDs.get(start), nodeIDs.get(end), true);
        if (shortestPath.equals("NO_PATH")) return false;
        clearEdgesVisualization(maze);
        HashMap<Integer, Pair<Integer, Integer>> positions = getPositionsMap(maze);
        putPath(maze, positions, shortestPath, 5);
        return true;

    }

    private void clearEdgesVisualization(ArrayList<ArrayList<Integer>> maze) {
        for (int i = 0; i < maze.size() - 1; i++) {
            for (int j = 0; j < maze.get(0).size() - 1; j++) {
                if (maze.get(i).get(j) == 4) {
                    maze.get(i).set(j, 1);
                }
            }
        }
    }

    private HashMap<Integer, Pair<Integer, Integer>> getPositionsMap(ArrayList<ArrayList<Integer>> maze) {
        HashMap<Integer, Pair<Integer, Integer>> positions = new HashMap();
        for (int i = 0; i < maze.size() - 1; i++) {
            for (int j = 0; j < maze.get(0).size() - 1; j++) {
                if (maze.get(i).get(j) == 6 || maze.get(i).get(j) == 2 || maze.get(i).get(j) == 3) {
                    Pair<Integer, Integer> pos = new Pair<>(j, i);
                    positions.put(nodeIDs.get(pos), pos);
                }
            }
        }

        return positions;
    }

    private void putPath(ArrayList<ArrayList<Integer>> maze, HashMap<Integer, Pair<Integer, Integer>> positons, String path, int val) {
        String[] pathValues = path.split(" ");
        for (int i = 0; i < pathValues.length - 1; i++) {
            writePath(maze, positons.get(Integer.parseInt(pathValues[i])), positons.get(Integer.parseInt(pathValues[i + 1])), val);
        }
    }

    private void writePath(ArrayList<ArrayList<Integer>> maze, Pair<Integer, Integer> pos1, Pair<Integer, Integer> pos2, int val) {
        boolean isSameRow = pos1.getKey() - pos2.getKey() != 0;
        int startX = pos1.getKey(), endX = pos2.getKey(), startY = pos1.getValue(), endY = pos2.getValue();
        if (isSameRow) {
            if (pos1.getKey() > pos2.getKey()) {
                int temp = startX;
                startX = endX;
                endX = temp;
            }
            for (int i = startX + 1; i < endX; i++) {
                //startY and endY are the same
                if (maze.get(startY).get(i) != 2 && maze.get(startY).get(i) != 3 && maze.get(startY).get(i) != 6)
                    maze.get(startY).set(i, val);
            }
        } else {
            if (pos1.getValue() > pos2.getValue()) {
                int temp = startY;
                startY = endY;
                endY = temp;
            }
            for (int i = startY + 1; i < endY; i++) {
                //startX and endX are the same
                if (maze.get(i).get(startX) != 2 && maze.get(i).get(startX) != 3 && maze.get(i).get(startX) != 6)
                    maze.get(i).set(startX, val);
            }
        }

    }

    public Pair<Integer, Integer> getStart() {
        return start;
    }

    public void setStart(Pair start) {
        this.start = start;
    }

    public Pair<Integer, Integer> getEnd() {
        return end;
    }

    public void setEnd(Pair end) {
        this.end = end;
    }
}
