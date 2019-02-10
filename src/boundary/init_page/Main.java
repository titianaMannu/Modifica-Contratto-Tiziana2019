package boundary.init_page;

import control.InitControl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String userNickName = "pippo"; // i realtà deve essere passato da un altro controller che fa le veci del main

        FXMLLoader loader = new FXMLLoader(getClass().getResource("init_view.fxml"));
        BorderPane root = loader.load();
        InitController controller = loader.getController();
        InitControl model = new InitControl();
        model.setUserNickname(userNickName);
        controller.setInitControl(model);
        controller.refrshAvailable(true);
        primaryStage.setTitle("Gestione Contratti");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}