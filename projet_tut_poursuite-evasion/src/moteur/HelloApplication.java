package moteur;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();
        stage.setTitle("Hello!");
        stage.setScene(new Scene(root, 320, 240));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}