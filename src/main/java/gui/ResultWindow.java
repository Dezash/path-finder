package gui;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import sample.Connection;
import sample.Node;
import sample.Path;

/**
 *
 * @author darius
 */
public abstract class ResultWindow extends ScrollPane implements EventHandler<ActionEvent> {

    final GridPane container = new GridPane();
    private final ScrollPane scPane = new ScrollPane();

    public ResultWindow(ArrayList<TravelInfo> travelInfos) {
        container.setHgap(5);
        container.setVgap(5);
        initComponents(travelInfos);
    }

    private void initComponents(ArrayList<TravelInfo> travelInfos) {
        ObservableList children = container.getChildren();
        children.clear();


        int col = 0;
        for(TravelInfo travelInfo : travelInfos)
        {
            int row = 1;

            container.add(
                    new Label("Kelionės kaina: €" + travelInfo.price + "\nKelionės trukmė: " + travelInfo.totalTime),
                    col * 2, 0, 2, 1);

            for(TravelEntry travelEntry : travelInfo.travelEntries)
            {
                int rowCol = col * 2;
                if(travelEntry.hasCircle)
                {
                    Circle bullet = new Circle(10);
                    bullet.setFill(Color.TRANSPARENT);
                    bullet.setStroke(Color.GRAY);
                    container.add(bullet, rowCol, row);
                    rowCol++;
                }

                Label text = new Label(travelEntry.text);
                container.add(text, rowCol, row);

                row++;

                if(travelEntry.travelTime != null)
                {
                    Label travelTimeText = new Label("Kelionės laikas: " + travelEntry.travelTime);
                    travelTimeText.setTextFill(Color.GRAY);

                    container.add(travelTimeText, rowCol, row);
                    row++;
                }

            }
            col++;
        }


        scPane.setContent(container);

        final Stage dialog = new Stage();
        Scene dialogScene = new Scene(scPane, Math.min(1280, (col + 1) * 160), 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @Override
    public abstract void handle(ActionEvent ae);

    public static class TravelInfo
    {
        ArrayList<TravelEntry> travelEntries;
        double price;
        String totalTime;

        public TravelInfo(ArrayList<TravelEntry> travelEntries, double price, String totalTime)
        {
            this.travelEntries = travelEntries;
            this.price = price;
            this.totalTime = totalTime;
        }

    }

    public static class TravelEntry
    {
        public boolean hasCircle;
        String text;
        String travelTime;

        public TravelEntry(boolean hasCircle, String text)
        {
            this.hasCircle = hasCircle;
            this.text = text;
        }

        public TravelEntry(boolean hasCircle, String text, String travelTime)
        {
            this(hasCircle, text);
            this.travelTime = travelTime;
        }
    }
}
