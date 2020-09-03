package screen;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ColorPickerPopUp  extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        Rectangle r = new Rectangle();
        r.setWidth(200);
        r.setHeight(200);

        ColorPicker cp = new ColorPicker(Color.BLUE);
//        cp.setOnAction((EventHandler<ActionEvent>) new ActionEvent());

        GridPane.setConstraints(cp, 0, 0);
        GridPane.setConstraints(r, 0, 1);

        Scene scene = new Scene(grid, 300, 300);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

}

