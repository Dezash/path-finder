package sample;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

public class Node extends Circle implements Comparable<Node>
{
    private String name;

    int id;
    public double x, y;

    private static double radius = 7.0;

    public Label label;

    public Node(int id, double x, double y, String name)
    {
        this(id, x, y);
        this.name = name;
        label.setText(name);
    }

    public Node(int id, double x, double y)
    {
        super(x, y, radius);
        this.name = "";
        this.label = new Label(name);
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public String name() {return name;}
    public int id() {return id;}

    @Override
    public int compareTo(Node o)
    {
        return Integer.compare(this.id, o.id);
    }
}
