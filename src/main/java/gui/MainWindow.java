package gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

public class MainWindow extends BorderPane implements EventHandler<ActionEvent> {
    private static ResourceBundle MESSAGES;
    //private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("gui.messages");

    private static final double SPACING = 5.0;
    private static final Insets INSETS = new Insets(SPACING);

    private Graph<Node, Cost> graph;
    private ArrayList<Connection> connections = new ArrayList<>();
    private ArrayList<Cost> costs;
    private Node startNode = null;
    private Node destinationNode = null;

    enum SelectionMode {
        START,
        DESTINATION
    }

    SelectionMode selectionMode = SelectionMode.START;

    private final Pane paneGraph = new Pane();
    private final GridPane paneBottom = new GridPane();
    private final GridPane paneParam1 = new GridPane();
    private final GridPane paneParam2 = new GridPane();
    private final TextField tfTime = new TextField();
    private final ComboBox cmbTimeType = new ComboBox();
    private final TextField tfPrice = new TextField();
    private final Button btnSelectNode = new Button();
    final Label lblStart = new Label();
    final Label lblDestination = new Label();
    final Line hoverLine = new Line();


    final ToggleGroup rbGroup = new ToggleGroup();
    RadioButton rbTime;
    RadioButton rbPrice;

    CheckBox cbToggleEdges;

    private Panels paneButtons;
    private MainWindowMenu mainWindowMenu;
    private ResultWindow resWindow;
    private final Stage stage;


    public MainWindow(Stage stage) {
        this.stage = stage;


        FileInputStream fis;
        try
        {
            fis = new FileInputStream("messages.properties");
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return;
        }
        try
        {
            MESSAGES = new PropertyResourceBundle(fis);
        } catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        initComponents();
    }

    public void initGraph(Graph<Node, Cost> graph, HashMap<Integer, Node> nodes, ArrayList<Integer> setFrom, ArrayList<Integer> setTo, ArrayList<Cost> listCosts, String dir)
    {
        paneGraph.getChildren().clear();
        lblStart.setText(MESSAGES.getString("lblStart") + ":");
        lblDestination.setText(MESSAGES.getString("lblDestination") + ":");
        startNode = null;
        destinationNode = null;
        selectionMode = SelectionMode.START;
        btnSelectNode.setText(MESSAGES.getString("btnDestination"));
        paneButtons.getButtons().forEach(btn -> btn.setDisable(true));

        this.graph = graph;
        this.costs = listCosts;

        paneGraph.getChildren().add(hoverLine);

        double graphWidth = paneGraph.getWidth();
        double graphHeight = paneGraph.getHeight();

        for(int i = 0; i < setFrom.size(); i++)
        {
            Connection connection = new Connection(nodes.get(setFrom.get(i)), nodes.get(setTo.get(i)));
            connections.add(connection);
            Label lineLabel = connection.label();
            Cost cost = listCosts.get(i);
            connection.arrivalTime = cost.getArrivalTime();
            connection.departureTime = cost.getDepartureTime();
            lineLabel.setText(cost.toString());
            lineLabel.setScaleX(0.8);
            lineLabel.setScaleY(0.8);
            paneGraph.getChildren().add(connection);
            connection.setVisible(cbToggleEdges.isSelected());

            // getWidth ir getHeight grazina 0, kol objektas nera surenderintas
            Platform.runLater(() -> {
                Line line = connection.line();
                lineLabel.setTranslateX(line.getStartX() + (line.getEndX() - line.getStartX()) / 2 - lineLabel.getLayoutBounds().getWidth() / 2);
                lineLabel.setTranslateY(line.getStartY() + (line.getEndY() - line.getStartY()) / 2 - lineLabel.getLayoutBounds().getHeight());
            });
        }

        for(Node node : nodes.values())
        {
            double radius = node.getRadius();
            double circleX = graphWidth * node.x;
            if(circleX + radius > graphWidth)
                circleX = graphWidth - radius;
            else if(circleX - radius < 0)
                circleX = radius;

            double circleY = graphHeight * node.y;
            if(circleY + radius > graphHeight)
                circleY = graphHeight - radius;
            else if(circleY - radius < 0)
                circleY = radius;

            node.setCenterX(circleX);
            node.setCenterY(circleY);
            node.label.setLabelFor(node);
            node.label.setTranslateX(circleX);
            node.label.setTranslateY(circleY);
            paneGraph.getChildren().add(node);
            paneGraph.getChildren().add(node.label);

            node.setOnMouseEntered(event -> {
                if(startNode != null && node != startNode && selectionMode == SelectionMode.DESTINATION)
                {
                    hoverLine.setStartX(startNode.getCenterX());
                    hoverLine.setStartY(startNode.getCenterY());
                    hoverLine.setEndX(node.getCenterX());
                    hoverLine.setEndY(node.getCenterY());
                    hoverLine.setVisible(true);
                }
            });

            node.setOnMouseExited(event -> {
                hoverLine.setVisible(false);
            });

            node.setOnMouseClicked((event -> {
                if(selectionMode == SelectionMode.START)
                {
                    if(node != destinationNode)
                    {
                        lblStart.setText(MESSAGES.getString("lblStart") + ": " + node.name());
                        if(startNode != null)
                            startNode.setFill(Color.BLACK);
                        startNode = node;
                        node.setFill(Color.BLUE);
                        selectionMode = SelectionMode.DESTINATION;
                        btnSelectNode.setText(MESSAGES.getString("btnStart"));
                    }
                    else
                        showError(MESSAGES.getString("errSameDes"));

                }
                else
                {
                    if(node != startNode)
                    {
                        lblDestination.setText(MESSAGES.getString("lblDestination") + ": " + node.name());
                        if(destinationNode != null)
                            destinationNode.setFill(Color.BLACK);
                        destinationNode = node;
                        node.setFill(Color.RED);
                        node.setStrokeWidth(2.0);
                    }
                    else
                    {
                        showError(MESSAGES.getString("errSameDes"));
                    }
                }

                if(startNode != null && destinationNode != null && paneButtons.getButtons().get(0).isDisable())
                    paneButtons.getButtons().forEach(btn -> btn.setDisable(false));

            }));
        }

        try{
            BackgroundImage myBI = new BackgroundImage(
                    new Image(new FileInputStream(dir + "/background.png"),
                            graphWidth, graphHeight,false,true),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
            paneGraph.setBackground(new Background(myBI));
        }
        catch(FileNotFoundException e)
        {
            //System.err.println("Background not found: " + e.getMessage());
        }

    }

    private void initComponents() {
        rbTime = new RadioButton(MESSAGES.getString("time"));
        rbPrice = new RadioButton(MESSAGES.getString("price"));

        cbToggleEdges = new CheckBox(MESSAGES.getString("cbToggleEdges"));

        //======================================================================
        // Formuojamas mygtukų tinklelis. Naudojama klasė Panels.
        //======================================================================
        paneButtons = new Panels(
                new String[]{
                        MESSAGES.getString("btnFindRoute"),
                        MESSAGES.getString("btnMinTime"),
                        MESSAGES.getString("btnMinPrice"),
                        MESSAGES.getString("btnFindAll")
                },
                1, 4);
        //======================================================================
        // Formuojama pirmoji parametrų lentelė.
        //======================================================================
        paneParam1.setAlignment(Pos.TOP_LEFT);
        paneParam1.add(new Label(MESSAGES.getString("lblMaxTime")), 3, 0);
        paneParam1.add(tfTime, 3, 1);
        //paneParam1.add(new Label(":"), 1, 1);
        tfTime.setPrefWidth(40);
        tfTime.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) return;
            tfTime.setText(newValue.replaceAll("[^\\d]", ""));
        });
        tfTime.setText("0");

        paneParam1.add(cmbTimeType, 4, 1);
        cmbTimeType.setItems(FXCollections.observableArrayList(
                MESSAGES.getString("cmbMinutes"),
                MESSAGES.getString("cmbHours"),
                MESSAGES.getString("cmbDays")
        ));
        cmbTimeType.getSelectionModel().select(0);

        paneParam1.add(new Label(MESSAGES.getString("lblUnlimited")), 5, 1);


        paneParam1.add(new Label(MESSAGES.getString("lblMaxPrice")), 3, 2);
        paneParam1.add(tfPrice, 3, 3);
        tfPrice.setPrefWidth(40);
        tfPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) return;
            tfPrice.setText(newValue.replaceAll("[^\\d]", ""));
        });
        tfPrice.setText("0");
        paneParam1.add(new Label(MESSAGES.getString("lblUnlimited")), 4, 3);

        lblStart.setText(MESSAGES.getString("lblStart") + ":");
        paneParam1.add(lblStart, 0, 0);
        lblDestination.setText(MESSAGES.getString("lblDestination") + ":");
        paneParam1.add(lblDestination, 0, 1);
        btnSelectNode.setText(MESSAGES.getString("btnDestination"));
        btnSelectNode.setOnAction(this);
        paneParam1.add(btnSelectNode, 0, 2);

        paneParam1.add(new Label(MESSAGES.getString("priority")), 2, 0);
        paneParam1.add(rbTime, 2, 1);
        paneParam1.add(rbPrice, 2, 2);

        rbPrice.setToggleGroup(rbGroup);
        rbPrice.setSelected(true);
        rbTime.setToggleGroup(rbGroup);

        rbGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                if (rbGroup.getSelectedToggle() != null) {
                    Cost.currentCost = rbPrice.isSelected() ? Cost.CurrentCost.PRICE : Cost.CurrentCost.TIME;
                }
            }
        });

        paneParam1.add(cbToggleEdges, 5, 3);
        cbToggleEdges.setOnAction(e -> {
            if(cbToggleEdges.isSelected())
            {
                for(Connection conn : connections)
                    conn.setVisible(true);
            }
            else
            {
                for(Connection conn : connections)
                    conn.setVisible(false);
            }
        });


        hoverLine.setFill(Color.BLUE);
        hoverLine.getStrokeDashArray().add(8.0d);
        hoverLine.setVisible(false);

        paneParam1.setHgap(SPACING);
        //======================================================================
        // Formuojamas bendras parametrų panelis
        //======================================================================
        paneBottom.setPadding(INSETS);
        paneBottom.setHgap(SPACING);
        paneBottom.setVgap(SPACING);
        paneBottom.add(paneButtons, 0, 0);
        paneBottom.add(paneParam1, 1, 0);
        paneBottom.add(paneParam2, 2, 0);
        paneBottom.alignmentProperty().bind(new SimpleObjectProperty<>(Pos.TOP_LEFT));

        mainWindowMenu = new MainWindowMenu() {
            @Override
            public void handle(ActionEvent ae) {
                try {
                    Object source = ae.getSource();
                    if (source.equals(mainWindowMenu.getMenus().get(0).getItems().get(0))) {
                        fileChooseMenu();
                    } else if (source.equals(mainWindowMenu.getMenus().get(0).getItems().get(1))) {
                    } else if (source.equals(mainWindowMenu.getMenus().get(0).getItems().get(3))) {
                        System.exit(0);
                    } else if (source.equals(mainWindowMenu.getMenus().get(1).getItems().get(0))) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.setTitle(MESSAGES.getString("menuItem21"));
                        alert.setHeaderText(MESSAGES.getString("author"));
                        alert.showAndWait();
                    }
                } catch (Exception e) {
                    System.err.println(MESSAGES.getString("systemError"));
                    e.printStackTrace(System.out);
                }
            }
        };


        setTop(mainWindowMenu);
        setCenter(paneGraph);

        VBox vboxPaneBottom = new VBox();
        VBox.setVgrow(paneBottom, Priority.ALWAYS);
        vboxPaneBottom.getChildren().addAll(paneBottom);
        setBottom(vboxPaneBottom);

        paneButtons.getButtons().forEach(btn -> btn.setOnAction(this));
        paneButtons.getButtons().forEach(btn -> btn.setDisable(true));
    }


    @Override
    public void handle(ActionEvent ae) {
        try {
            System.gc();
            System.gc();
            System.gc();

            Object source = ae.getSource();
            if (source instanceof Button) {
                handleButtons(source);
            }
        } catch (UnsupportedOperationException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (Exception e) {
            System.err.println(MESSAGES.getString("systemError"));
            e.printStackTrace(System.out);
        }
    }

    private int findConnection(Node from, Node to)
    {
        for(int i = 0; i < connections.size(); i++)
        {
            if(connections.get(i).from() == from && connections.get(i).to() == to)
                return i;
        }
        return -1;
    }

    public Connection getConnection(Node from, Node to)
    {
        return connections.get(findConnection(from, to));
    }

    private String getTripTime(LocalTime departureTime, LocalTime arrivalTime)
    {
        int tripTime = departureTime.isBefore(arrivalTime) ?
                arrivalTime.getHour() * 60 + arrivalTime.getMinute() - departureTime.getHour() * 60 - departureTime.getMinute()
                :
                24 * 60 - departureTime.getHour() * 60 - departureTime.getMinute() + arrivalTime.getHour() * 60 + arrivalTime.getMinute();

        String strTripTime = "";
        int days = tripTime / 60 / 24;
        if(days > 0)
            strTripTime += days + "d";
        int hours = (tripTime - days * 24 * 60) / 60;
        strTripTime += hours + "h";
        int minutes = tripTime - days * 24 * 60 - hours * 60;
        strTripTime += minutes + "min";

        return strTripTime;
    }

    private void initiateSearch()
    {
        int cmbIndex = cmbTimeType.getSelectionModel().getSelectedIndex();
        Cost.maxTime = Integer.parseInt(tfTime.getText()) * (cmbIndex == 0 ? 1 : cmbIndex == 1 ? 60 : 24 * 60);
        Cost.maxPrice = Integer.parseInt(tfPrice.getText()) * 100;

        // Atstatome marsrutu spalvas
        for(Connection conn : connections)
        {
            conn.line().setStroke(Color.BLACK);
            conn.line().setStrokeWidth(1.0);
            if(!cbToggleEdges.isSelected())
            {
                conn.setVisible(false);
            }
        }
    }

    private ResultWindow.TravelInfo getTravelInfo(Path<Node> path)
    {
        ArrayList<ResultWindow.TravelEntry> travelEntries = new ArrayList<>();

        List<Node> elements = path.elements;
        //LocalTime lastArrival = null;

        Node firstNode = elements.get(0);
        Connection firstConn = connections.get(findConnection(firstNode, elements.get(1)));
        travelEntries.add(new ResultWindow.TravelEntry(true,
                firstConn.departureTime + " " + firstNode.name(),
                getTripTime(firstConn.departureTime, firstConn.arrivalTime)
        ));
        for(int i = 1; i < elements.size(); i++)
        {
            Node from = elements.get(i - 1);
            Node to = elements.get(i);

            Connection conn = connections.get(findConnection(from, to));
            conn.line().setStroke(Color.RED);
            conn.line().setStrokeWidth(2.0);
            conn.setVisible(true);

                /*if(lastArrival != null)
                {
                    if(conn.departureTime.isBefore(lastArrival))
                        sb.append("Nakvojama ").append(from.name()).append("\n");
                }
                lastArrival = conn.arrivalTime;*/


            //System.out.println(conn.departureTime + " - išvykimas iš " + from.name());
            //System.out.println(conn.arrivalTime + " atvykimas į " + to.name());

            String tripTime = null;
            if(i != elements.size() - 1)
            {
                Node nextNode = elements.get(i + 1);
                Connection nextConn = connections.get(findConnection(to, nextNode));
                tripTime = getTripTime(nextConn.departureTime, nextConn.arrivalTime);
            }

            ResultWindow.TravelEntry entry = new ResultWindow.TravelEntry(true,
                    conn.arrivalTime + " " + to.name(), tripTime);
            travelEntries.add(entry);
        }

        String totalTime = "";
        int days = path.distance.minutes / 60 / 24;
        if(days > 0)
            totalTime += days + "d";
        int hours = (path.distance.minutes - days * 24 * 60) / 60;
        totalTime += hours + "h";
        int minutes = path.distance.minutes - days * 24 * 60 - hours * 60;
        totalTime += minutes + "min";


        //System.out.println("Kelionės kaina: €" + (double)path.distance.price / 100);
        //System.out.println("Kelionės trukmė: " + totalTime);

        ResultWindow.TravelInfo travelInfo = new ResultWindow.TravelInfo(travelEntries, (double)path.distance.price / 100, totalTime);
        return travelInfo;
    }

    private void findAllRoutes()
    {
        initiateSearch();

        ArrayList<Path<Node>> allPaths = graph.getAllPaths(startNode, destinationNode);
        //System.out.println("All paths: " + allPaths.size());
        ArrayList<ResultWindow.TravelInfo> travelInfos = new ArrayList<>();
        for(Path<Node> path : allPaths)
        {
            if(path.elements.size() > 1)
            {
                /*System.out.println("Path:");
                System.out.println(path.distance);
                for(Node node : path.elements)
                {
                    System.out.println(node.name());
                }*/

                travelInfos.add(getTravelInfo(path));
            }
        }

        resWindow = new ResultWindow(travelInfos) {
            @Override
            public void handle(ActionEvent ae) {
                try {
                    Object source = ae.getSource();

                } catch (Exception e) {
                    System.err.println(MESSAGES.getString("systemError"));
                    e.printStackTrace(System.out);
                }
            }
        };

    }


    private void findRoute()
    {
        initiateSearch();

        long startTime = System.nanoTime();
        Path<Node> path = graph.getPath(startNode, destinationNode);
        System.out.println("Surasta per: " + (System.nanoTime() - startTime));
        if(path != null)
        {
            ArrayList<ResultWindow.TravelInfo> travelInfos = new ArrayList<>();
            travelInfos.add(getTravelInfo(path));

            resWindow = new ResultWindow(travelInfos) {
                @Override
                public void handle(ActionEvent ae) {
                    try {
                        Object source = ae.getSource();

                    } catch (Exception e) {
                        System.err.println(MESSAGES.getString("systemError"));
                        e.printStackTrace(System.out);
                    }
                }
            };

        }
        else
        {
            //System.out.println("Path not found");
            showError("Maršruto pagal pateiktus parametrus sudaryti nepavyko.");
        }

    }

    private void handleButtons(Object source) {
        if (source.equals(paneButtons.getButtons().get(0)))
        {
            findRoute();
        }
        else if (source.equals(paneButtons.getButtons().get(1)))
        {
            // Maziausio laiko nustatymai
            rbTime.setSelected(true);
            rbPrice.setSelected(false);
            tfPrice.setText("0");
            tfTime.setText("0");
            findRoute();
        }
        else if (source.equals(paneButtons.getButtons().get(2)))
        {
            // Maziausios kainos nustatymai
            rbTime.setSelected(false);
            rbPrice.setSelected(true);
            tfPrice.setText("0");
            tfTime.setText("0");
            findRoute();
        }
        else if (source.equals(paneButtons.getButtons().get(3)))
        {
            findAllRoutes();
        }
        else if(source.equals(btnSelectNode))
        {
            if(selectionMode == SelectionMode.DESTINATION)
            {
                selectionMode = SelectionMode.START;
                btnSelectNode.setText(MESSAGES.getString("btnDestination"));
            }
            else
            {
                selectionMode = SelectionMode.DESTINATION;
                btnSelectNode.setText(MESSAGES.getString("btnStart"));
            }
        }
    }



    /*private void treeEfficiency() {
        KsGui.setFormatStartOfLine(true);
        KsGui.oun(taOutput, "", MESSAGES.getString("benchmark"));
        paneBottom.setDisable(true);
        mainWindowMenu.setDisable(true);

        BlockingQueue<String> resultsLogger = new SynchronousQueue<>();
        Semaphore semaphore = new Semaphore(-1);
        SimpleBenchmark simpleBenchmark = new SimpleBenchmark(resultsLogger, semaphore);

        // Ši gija paima rezultatus iš greitaveikos tyrimo gijos ir išveda
        // juos į taOutput. Gija baigia darbą kai gaunama FINISH_COMMAND
        new Thread(() -> {
            try {
                String result;
                while (!(result = resultsLogger.take())
                        .equals(SimpleBenchmark.FINISH_COMMAND)) {
                    KsGui.ou(taOutput, result);
                    semaphore.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            semaphore.release();
            paneBottom.setDisable(false);
            mainWindowMenu.setDisable(false);
        }, "Greitaveikos_rezultatu_gija").start();

        //Šioje gijoje atliekamas greitaveikos tyrimas
        new Thread(simpleBenchmark::startBenchmark, "Greitaveikos_tyrimo_gija").start();
    }*/


    private void loadGraph(String dir)
    {
        HashMap<Integer, Node> nodes = new HashMap<>();
        ArrayList<Integer> listFrom = new ArrayList<>();
        ArrayList<Integer> listTo = new ArrayList<>();
        ArrayList<Cost> listCosts = new ArrayList<>();
        File nodeFile = new File(dir + "/nodes.txt");
        Scanner scanner = null;
        try
        {
            scanner = new Scanner(nodeFile);
        } catch (FileNotFoundException e)
        {
            showError(MESSAGES.getString("errNodeFile"));
            return;
        }
        while(scanner.hasNext())
        {
            int id = scanner.nextInt();
            double x = scanner.nextDouble(), y = scanner.nextDouble();
            scanner.nextLine();
            String name = scanner.nextLine();
            nodes.put(id, new Node(id, x, y, name));
        }
        scanner.close();

        Graph<Node, Cost> stations = new Graph<>();

        File edgeFile = new File(dir + "/edges.txt");
        Scanner sc = null;
        try
        {
            sc = new Scanner(edgeFile);
        } catch (FileNotFoundException e)
        {
            showError(MESSAGES.getString("errEdgeFile"));
            e.printStackTrace();
        }
        while(sc.hasNext())
        {
            int idFrom = sc.nextInt();
            sc.useDelimiter(" ");
            int idTo = sc.nextInt();
            double price = sc.nextDouble();
            sc.useDelimiter(":|\\s+");
            int dpHour = sc.nextInt();
            int dpMinute = sc.nextInt();
            int arrHour = sc.nextInt();
            int arrMinute = sc.nextInt();
            listFrom.add(idFrom);
            listTo.add(idTo);
            Cost cost = new Cost(price, LocalTime.of(dpHour, dpMinute), LocalTime.of(arrHour, arrMinute));
            listCosts.add(cost);
            stations.add(nodes.get(idFrom), nodes.get(idTo), cost);
        }
        sc.close();
        initGraph(stations, nodes, listFrom, listTo, listCosts, dir);
    }


    private void fileChooseMenu() {
        DirectoryChooser fc = new DirectoryChooser();
        fc.setTitle((MESSAGES.getString("menuOpen")));
        fc.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fc.showDialog(stage);
        if (file != null) {
            loadGraph(file.getAbsolutePath());
        }
    }

    public static MainWindow createAndShowGui(Stage stage) {
        Locale.setDefault(Locale.US); // Suvienodiname skaičių formatus
        MainWindow window = new MainWindow(stage);
        stage.setScene(new Scene(window, 1280, 1024));
        stage.setMaximized(true);
        stage.setTitle("KTU IF Technologinis projektas");
        stage.getIcons().add(new Image("file:ktu.png"));
        stage.show();
        return window;
    }

    void showError(String msg)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(MESSAGES.getString("err"));
        alert.setHeaderText(msg);
        alert.showAndWait();
    }

}
