package view;
import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;
import Beans.ErrorMsg;
import Control.RequestModel;
import Control.SubmitModel;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import view.evaluateSubmitsView.EvaluateController;
import view.requestView.RequestController;

import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonListener;

public class InitController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField nickNameField;

    @FXML
    private TextField contractIdField;

    @FXML
    private Button checkForSubmitsBtn;

    @FXML
    private Button doRequestsBtn;

    @FXML
    private TextArea errorMsgArea;

    @FXML
    void checkForSubmits(ActionEvent event) {
        errorMsgArea.clear();
        ErrorMsg msg = new ErrorMsg();
        String nickName = nickNameField.getText();
        int  contractId = Integer.parseInt(contractIdField.getText());
        SubmitModel model = new SubmitModel();
        msg.addAllMsg(model.setUserNickname(nickName));
        msg.addAllMsg(model.setActiveContract(contractId));
        if (msg.isErr()){
            for (String item : msg.getMsgList()){
                errorMsgArea.appendText(item);
            }
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("evaluateSubmitsView/EvaluateSubmits.fxml"));
            BorderPane root1= loader.load();
            EvaluateController controller = loader.getController();
            controller.setModel(model);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Gestione Contratto");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void doRequests(ActionEvent event) {
        errorMsgArea.clear();
        ErrorMsg msg = new ErrorMsg();
        String nickName = nickNameField.getText();
        int  contractId = Integer.parseInt(contractIdField.getText());
        RequestModel model = new RequestModel();
        msg.addAllMsg(model.setUserNickname(nickName));
        msg.addAllMsg(model.setActiveContract(contractId));
        if (msg.isErr()){
            for (String item : msg.getMsgList()){
                errorMsgArea.appendText(item);
            }
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("requestView/Requests.fxml"));
            BorderPane root1= loader.load();
            RequestController controller = loader.getController();
            controller.setModel(model);
            //chiamo metodo di inizializzazione
            controller.dysplayContractField();
            controller.doViewRequests();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Gestione Contratto");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void initialize() {
        assert nickNameField != null : "fx:id=\"nickNameField\" was not injected: check your FXML file 'init.fxml'.";
        assert contractIdField != null : "fx:id=\"contractIdField\" was not injected: check your FXML file 'init.fxml'.";
        assert checkForSubmitsBtn != null : "fx:id=\"checkForSubmitsBtn\" was not injected: check your FXML file 'init.fxml'.";
        assert doRequestsBtn != null : "fx:id=\"doRequestsBtn\" was not injected: check your FXML file 'init.fxml'.";
        assert errorMsgArea != null : "fx:id=\"errorMsgArea\" was not injected: check your FXML file 'init.fxml'.";

    }
}
