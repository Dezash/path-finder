package sample;

import javafx.beans.InvalidationListener;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.time.LocalTime;

public class Connection extends Group
{
    private Node from;
    private Node to;

    private static final double arrowLength = 20;
    private static final double arrowWidth = 7;

    private Line line;
    private Label label;

    public LocalTime arrivalTime;
    public LocalTime departureTime;

    public Connection(Node from, Node to)
    {
        this(from, to, new Line(), new Line(), new Line(), new Label());
    }

    private Connection(Node from, Node to, Line line, Line arrow1, Line arrow2, Label lblCost) {
        super(line, arrow1, arrow2, lblCost);

        this.line = line;
        this.label = lblCost;
        this.from = from;
        this.to = to;

        line.startXProperty().bind(from.centerXProperty());
        line.startYProperty().bind(from.centerYProperty());
        line.endXProperty().bind(to.centerXProperty());
        line.endYProperty().bind(to.centerYProperty());

        InvalidationListener updater = o -> {
            double ex = line.getEndX();
            double ey = line.getEndY();
            double sx = line.getStartX();
            double sy = line.getStartY();

            arrow1.setEndX(ex);
            arrow1.setEndY(ey);
            arrow2.setEndX(ex);
            arrow2.setEndY(ey);

            if (ex == sx && ey == sy) {
                // arrow parts of length 0
                arrow1.setStartX(ex);
                arrow1.setStartY(ey);
                arrow2.setStartX(ex);
                arrow2.setStartY(ey);
            } else {
                double factor = arrowLength / Math.hypot(sx-ex, sy-ey);
                double factorO = arrowWidth / Math.hypot(sx-ex, sy-ey);

                // part in direction of main line
                double dx = (sx - ex) * factor;
                double dy = (sy - ey) * factor;

                // part ortogonal to main line
                double ox = (sx - ex) * factorO;
                double oy = (sy - ey) * factorO;

                arrow1.setStartX(ex + dx - oy);
                arrow1.setStartY(ey + dy + ox);
                arrow2.setStartX(ex + dx + oy);
                arrow2.setStartY(ey + dy - ox);
            }
        };

        // add updater to properties
        line.startXProperty().addListener(updater);
        line.startYProperty().addListener(updater);
        line.endXProperty().addListener(updater);
        line.endYProperty().addListener(updater);
        updater.invalidated(null);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Connection) {
            Connection connection = (Connection) obj;
            return this.from == connection.from && this.to == connection.to;
        }
        return false;
    }

    public Line line()
    {
        return line;
    }

    public Label label()
    {
        return label;
    }

    public Node from()
    {
        return from;
    }

    public Node to()
    {
        return to;
    }

}
