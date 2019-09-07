
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Graph;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/mainScene.fxml"));
        primaryStage.setTitle("Graph Analyzer");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
        Graph g = new Graph();
        //a b c d e f g h i
        //0 1 2 3 4 5 6 7 8
        g.addNodeConnectionUndirected(0,1,4);
        g.addNodeConnectionUndirected(0,7,8);
        g.addNodeConnectionUndirected(1,2,8);
        g.addNodeConnectionUndirected(1,7,11);
        g.addNodeConnectionUndirected(2,3,7);
        g.addNodeConnectionUndirected(2,5,4);
        g.addNodeConnectionUndirected(2,8,2);
        g.addNodeConnectionUndirected(3,4,9);
        g.addNodeConnectionUndirected(3,5,14);
        g.addNodeConnectionUndirected(4,5,10);
        g.addNodeConnectionUndirected(5,6,2);
        g.addNodeConnectionUndirected(6,8,6);
        g.addNodeConnectionUndirected(6,7,1);
        g.addNodeConnectionUndirected(7,8,7);


        //g.addNodeConnection(0,new Pair<>(8,10));
        //g.addNodeConnection(0,new Pair<>(5,5));
        //g.addNodeConnection(8,new Pair<>(9,1));
        //g.addNodeConnection(8,new Pair<>(5,2));
        //g.addNodeConnection(5,new Pair<>(7,2));
        //g.addNodeConnection(5,new Pair<>(8,3));
        //g.addNodeConnection(5,new Pair<>(9,9));
        //g.addNodeConnection(7,new Pair<>(0,7));
        //g.addNodeConnection(7,new Pair<>(9,6));
        //g.addNodeConnection(9,new Pair<>(7,4));
        // g.doPrims(1);
        for (int i = 0; i < 10; i++) {
            System.out.println("Start Node: 0 |End Node: " + i);
            System.out.println("All Path(nodes checked): " + g.processDijkstra(0, i, false));
            String shortPath = g.processDijkstra(0, i, true);
            System.out.println("Shortest Path: " + shortPath);
            System.out.println("Shortest Distance : " + g.getDistance(shortPath.split(" ")));
            System.out.println("-----------------------------------------------");
        }
        g.doPrims(0);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
