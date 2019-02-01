package view.init_page;

import java.util.List;
import Beans.ActiveContract;
import Control.RequestModel;
import Control.SubmitModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import view.evaluateSubmitsView.EvaluateController;
import view.requestView.RequestController;

/**
 * todo thread che aggiorna periodicamente la griglia dei contratti e le relative informazioni
 */
public class InitController {
    InitModel model;

    public InitController() {
    }

    public void setModel(InitModel model) {
        this.model = model;
    }

    @FXML
    private GridPane gp;

    /**
     * riempimento dinamico della GridPane
     */
   public void viewContracts(){
       List<ActiveContract> list = model.getAllContract();
       int count = 0;
       for (ActiveContract contract : list){
           ++count;
           int inVal = contract.getContractId();
           Label l0 = new Label(String.valueOf(contract.getContractId()));
           GridPane.setConstraints(l0, 0, count);
           Label l1 = new Label(String.valueOf(model.getSubmits(contract)));
           GridPane.setConstraints(l1, 1, count);
           if (Integer.parseInt(l1.getText()) > 0) { // il bottone di risposta è visibile solo se ci sono richieste pendenti
               Button btn2 = new Button("Rispondi");
               btn2.setOnAction(e -> checkForSubmits(inVal));
               GridPane.setConstraints(btn2, 2, count);
               gp.getChildren().add(btn2);
           }

           Button btn3 = new Button("Gestisci richieste di modifica");
           btn3.setOnAction(e -> doRequests(inVal));
           GridPane.setConstraints(btn3, 3, count);
           Button btn4 = new Button("Rinnova");
           btn4.setOnAction(e -> doRenew(inVal));
           GridPane.setConstraints(btn4, 4, count);

           gp.getChildren().addAll(l0, l1, btn3, btn4);
       }
   }

   @FXML
   void doRenew(int contractId) {
    }

    @FXML
    void checkForSubmits(int  contractId) {
        SubmitModel submitModel = new SubmitModel(model.getUserNickname(), contractId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../evaluateSubmitsView/EvaluateSubmits.fxml"));
            BorderPane root1= loader.load();
            EvaluateController controller = loader.getController();
            controller.setModel(submitModel);
            controller.doViewRequests();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Valutazione Richieste");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void doRequests(int contractId) {
       RequestModel requestModel = new RequestModel(model.getUserNickname(), contractId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../requestView/Requests.fxml"));
            BorderPane root1= loader.load();
            RequestController controller = loader.getController();
            controller.setModel(requestModel);
            //chiamo metodo di inizializzazione
            controller.dysplayContractField();
            controller.doViewRequests();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Gestione Modifiche");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
