import boundary.init_page.InitController;
import control.InitControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextArea messageArea;

    @FXML
    private TextField textField;

    @FXML
    void takeUsername(ActionEvent event) {
        messageArea.clear();
        String userNickName = textField.getText();
        if (userNickName.isEmpty()){
            messageArea.setText("Inserire userNickname\n");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("boundary/init_page/init_view.fxml"));
        BorderPane root1 = null;
        try {
            root1 = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InitController controller = loader.getController();
        InitControl control = new InitControl();
        control.setUserNickname(userNickName);
        controller.setInitControl(control);
        controller.refrshAvailable();
        Stage stage = new Stage();
        stage.setTitle("Gestione Contratti");
        stage.setScene(new Scene(root1));
        stage.show();
    }

}

