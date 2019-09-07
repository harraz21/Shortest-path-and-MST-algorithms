package view;

import EXTERNAL_LIBRARIES.MazeGenerator;
import controller.Controller;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Pair;
import model.Graph;

import java.io.BufferedWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SceneView implements Initializable {
    private static SceneView instance = null;
    @FXML
    private Button buttonDijkstra;
    @FXML
    private Button buttonGenerateMaze;
    @FXML
    private Button buttonGenerateGraph;
    @FXML
    private Canvas canvas;
    @FXML
    private Label systemMessage;

    private GraphicsContext gphContext;
    private Graph g = null;
    ArrayList<ArrayList<Integer>> Maze = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gphContext = canvas.getGraphicsContext2D();
        canvas.setWidth(800);
        canvas.setHeight(550);
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (Maze == null) return;
                int locX = (int) event.getX();
                int locY = (int) event.getY();
                locX -= locX % (Controller.getInstance().SqrLength + Controller.getInstance().SqrMargin);
                locY -= locY % (Controller.getInstance().SqrLength + Controller.getInstance().SqrMargin);


                // gphContext.fillRect(2+(double)locX,(double)locY,Controller.getInstance().SqrLength,Controller.getInstance().SqrLength);

                /**
                 * j==(locX-18)/(Controller.getInstance().SqrLength+Controller.getInstance().SqrMargin)
                 * (i+1)==locY/(Controller.getInstance().SqrMargin+Controller.getInstance().SqrLength)
                 */
                Pair<Integer, Integer> p = new Pair<>((locX - 18) / (Controller.getInstance().SqrLength + Controller.getInstance().SqrMargin)
                        , locY / (Controller.getInstance().SqrMargin + Controller.getInstance().SqrLength));

                //AVOID CRASHING
                try {
                    if (p.getValue() < 0 || p.getKey() < 0) return;
                    if (p.getValue() > Maze.size() || p.getKey() > Maze.get(0).size() || Maze.get(p.getValue()).get(p.getKey()) == 0)
                        return;
                } catch (IndexOutOfBoundsException e) {
                    return;//just return if out of bounds
                }
                if (!event.isControlDown() && !event.isAltDown()) {
                    if (Controller.getInstance().getStart() != null)
                        Maze.get(Controller.getInstance().getStart().getValue()).set(Controller.getInstance().getStart().getKey(), 1);
                    Controller.getInstance().setStart(p);
                    Maze.get(p.getValue()).set(p.getKey(), 2);
                } else {
                    if (Controller.getInstance().getEnd() != null)
                        Maze.get(Controller.getInstance().getEnd().getValue()).set(Controller.getInstance().getEnd().getKey(), 1);
                    Controller.getInstance().setEnd(p);
                    Maze.get(p.getValue()).set(p.getKey(), 3);
                }
                drawMaze();

            }
        });
        generateMazeOnAction();
    }

    public void drawSqr(double x, double y, double length) {
        gphContext.fillRect(x, y, length, length);
    }

    public void changeFillColor(String colorVal) {
        gphContext.setFill(Paint.valueOf(colorVal));
    }

    @FXML
    private void generateMazeOnAction() {
        systemMessage.setText("Specify start with MouseClick and end with CTRL+MouseClick");
        ArrayList<ArrayList<Integer>> maze = Controller.getInstance().mazeInfo();
        this.Maze = maze;
        Controller.getInstance().setStart(null);
        Controller.getInstance().setEnd(null);
        drawMaze();

    }

    private void drawMaze() {
        int currentY = 0;
        for (int i = 0; i < Maze.size(); i++) {
            for (int j = 0; j < Maze.get(i).size(); j++) {
                chooseColor(Maze.get(i).get(j));
                gphContext.fillRect(20 + j * (Controller.getInstance().SqrLength + Controller.getInstance().SqrMargin)
                        , currentY, Controller.getInstance().SqrLength, Controller.getInstance().SqrLength);

            }
            currentY = (i + 1) * (Controller.getInstance().SqrMargin + Controller.getInstance().SqrLength);
        }
    }

    private void chooseColor(int num) {
        Paint p = Paint.valueOf("000000");
        switch (num) {
            case 0:
                p = Paint.valueOf(Controller.getInstance().blockColor);
                break;
            case 1:
                p = Paint.valueOf(Controller.getInstance().normalColor);
                break;
            case 2:
                p = Paint.valueOf(Controller.getInstance().startColor);
                break;
            case 3:
                p = Paint.valueOf(Controller.getInstance().endColor);
                break;
            case 4:
                p = Paint.valueOf(Controller.getInstance().alongTheWayColor);
                break;
            case 5:
                p = Paint.valueOf(Controller.getInstance().shortestPathColor);
                break;
            case 6:
                p = Paint.valueOf(Controller.getInstance().nodeColor);
                break;
        }
        gphContext.setFill(p);
    }

    @FXML
    private void generateGraphOnAction() {
        systemMessage.setText("");
        g = Controller.getInstance().getGraphFromMaze(Maze);
        drawMaze();
    }

    @FXML
    private void dijkstraOnAction() {
        systemMessage.setText("");
        if (!Controller.getInstance().processDijkstra(Maze, g)) {
            systemMessage.setText("No Path or you didn't generate the Graph!");
        }
        drawMaze();
    }

}
