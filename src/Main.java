
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import thread.RequestReader;
import thread.RequestParser;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        BorderPane root = loader.load();
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Thread t1 = new Thread( new RequestParser());
        Thread t2 = new Thread(new RequestReader());
        t1.setDaemon(false);
        t2.setDaemon(false);
        t2.start();
        t1.start();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
