package view.init_page;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

import Beans.ActiveContract;
import Control.RequestModel;
import Control.SubmitModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
public class InitController  {
    private InitModel initModel;

    public InitController() {

    }

    public void setInitModel(InitModel initModel) {
        this.initModel = initModel;
    }

    @FXML
    private GridPane gp;

    /**
     * riempimento dinamico della GridPane
     * todo da aggiornare periodicamente il # delle richieste arivate per un contratto
     */
    public void viewContracts () {
        if (initModel == null) return;
        List<ActiveContract> list = initModel.getAllContract();
        int count = 0;
        for (ActiveContract contract : list) {
            ++count;
            int inVal = contract.getContractId();
            Label l0 = new Label(String.valueOf(contract.getContractId()));
            GridPane.setConstraints(l0, 0, count);
            Label l1 = new Label(String.valueOf(initModel.getSubmits(contract)));
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
        SubmitModel submitModel = new SubmitModel(initModel.getUserNickname(), contractId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../evaluateSubmitsView/EvaluateSubmits.fxml"));
            BorderPane root1= loader.load();
            EvaluateController controller = loader.getController();
            controller.setModel(submitModel);
            //controller.doViewRequests();
            controller.refrshAvailable(true);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Valutazione Richieste");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        flushInfo();
    }

    @FXML
    void doRequests(int contractId) {
       RequestModel requestModel = new RequestModel(initModel.getUserNickname(), contractId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../requestView/Requests.fxml"));
            BorderPane root1= loader.load();
            RequestController controller = loader.getController();
            controller.setModel(requestModel);

            // thread per aggiornare periodicamente lo stato delle richieste
            controller.refrshAvailable(true);
     //       controller.displayContractField();
          //  controller.doViewRequests();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Gestione Modifiche");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        flushInfo();
    }

    public void refrshAvailable(boolean b){
       if (b) {
           RefreshTread thread = new RefreshTread(this);
           thread.setDaemon(false);
           thread.start();
       }

    }

    private void clearGridPane(GridPane gp){
            ObservableList<Node> childrens = gp.getChildren();
            if(!childrens.isEmpty())
                childrens.clear();
    }

    public void flushInfo (){
        Platform.runLater(() -> {
            clearGridPane(gp);
            viewContracts();
        });
    }

}
