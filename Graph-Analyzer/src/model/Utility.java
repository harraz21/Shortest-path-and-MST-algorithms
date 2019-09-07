package model;

import javafx.util.Pair;

import java.util.ArrayList;

public class Utility {
    public static String reverseShortestPathString(String input) {
        String[] values = input.split(" ");
        String out = "";
        for (int i = values.length - 1; i >= 0; i--) {
            if (i != 0)
                out += values[i] + " ";
            else
                out += values[i];
        }
        return out;
    }

    public static ArrayList<ArrayList<Boolean>> getVisitedArray(int xSize, int ySize) {
        ArrayList<ArrayList<Boolean>> Visited = new ArrayList<>();
        for (int i = 0; i < ySize; i++) {
            Visited.add(new ArrayList<>());
            for (int j = 0; j < xSize; j++) {
                Visited.get(i).add(false);
            }
        }
        return Visited;
    }

    public static void printMinimumSpaningTree(ArrayList<Pair<Integer,Vertex>> edges)
    {
        int totalWeights=0;
        System.out.println("-----------------------------------------------");
        System.out.println("             Minimum Spanning Tree:");
        for (Pair<Integer,Vertex> edge:edges) {
            if (edge!=null) {
                if (edge.getValue() == null) {
                    System.out.println("root Node : " + edge.getKey());
                } else {
                    System.out.println("edge bet node: " + edge.getKey() + " and ->" + edge.getValue().getVertex() + "  at a cost of : " + edge.getValue().getWeight());
                    totalWeights=totalWeights+edge.getValue().getWeight();
                }
            }
        }
        System.out.println("Total weights in the minimum spanning tree is : "+totalWeights);
        System.out.println("-----------------------------------------------");
    }
}
