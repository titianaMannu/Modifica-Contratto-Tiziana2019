package boundary.init_page;

import java.util.List;

import beans.ActiveContractBean;
import control.InitControl;
import control.RequestControl;
import control.EvaluateControl;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import boundary.evaluate.EvaluateController;
import boundary.request.RequestController;
import thread.RefreshTread;

public class InitController  {
    private InitControl initControl;

    public InitController() {

    }

    public void setInitControl(InitControl initControl) {
        this.initControl = initControl;
    }

    @FXML
    private Label UserName;

    @FXML
    private GridPane gp;

    /**
     * riempimento dinamico della GridPane
     */
    private void viewContracts () {
        if (initControl == null) return;
        UserName.setText(initControl.getUserNickname());
        List<ActiveContractBean> list = initControl.getAllContract();
        int count = 0;
        for (ActiveContractBean contract : list) {
            ++count;
            int inVal = contract.getContractId();
            Label l0 = new Label(String.valueOf(contract.getContractId()));
            GridPane.setConstraints(l0, 0, count);
            Label l1 = new Label(String.valueOf(initControl.getSubmits(contract)));
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

            gp.getChildren().addAll(l0, l1, btn3);
        }
    }

    @FXML
    void checkForSubmits(int  contractId) {
        EvaluateControl evaluateControl = new EvaluateControl(initControl.getUserNickname(), contractId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../evaluate/EvaluateSubmits.fxml"));
            BorderPane root1= loader.load();
            EvaluateController controller = loader.getController();
            controller.setControl(evaluateControl);
            controller.refrshAvailable();
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
       RequestControl requestControl = new RequestControl(initControl.getUserNickname(), contractId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../request/Requests.fxml"));
            BorderPane root1= loader.load();
            RequestController controller = loader.getController();
            controller.setControl(requestControl);
            // thread per aggiornare periodicamente lo stato delle richieste
            controller.refrshAvailable();

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

    public void refrshAvailable(){
       Thread thread = new Thread(new RefreshTread(this));
       thread.setDaemon(false);
       thread.start();
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
