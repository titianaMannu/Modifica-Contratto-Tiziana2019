package view.evaluateSubmitsView;

import Beans.ErrorMsg;
import Beans.RequestBean;
import Control.SubmitModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.List;

/**
 * todo thread che si occupa di aggiornare la vista
 */
public class EvaluateController {
    @FXML
    private GridPane requestGp;

    @FXML
    private TextArea messageArea;

    SubmitModel model;

    public void setModel(SubmitModel model) {
        this.model = model;
    }

    /**
     * mostra tutte le richieste pending fatte all'utente
     */
    @FXML
    public void doViewRequests() {
        int count = 0;
        List<RequestBean> list = model.getSubmits();
        for (RequestBean item : list) {
            ++count;
            Text text0 = new Text(item.getType().getDescription()); //tipo della modifica
            GridPane.setConstraints(text0, 0, count);
            Text text1 = new Text(item.getObjectToChange().toString()); // objectToChange
            GridPane.setConstraints(text1, 1, count);
            Text text2 = new Text(item.getReasonWhy()); //reasonWhy
            GridPane.setConstraints(text2, 2, count);
            Text text3 = new Text(item.getDate().toString()); //dateOfSubmission
            GridPane.setConstraints(text3, 3, count);

            Button acceptBtn = new Button("accetta");
            acceptBtn.setOnAction(e -> accept(item.getIdRequest()));
            GridPane.setConstraints(acceptBtn, 4, count);

            Button declineBtn = new Button("rifiuta");
            declineBtn.setOnAction(e -> decline(item.getIdRequest()));
            GridPane.setConstraints(declineBtn, 5, count);

            requestGp.getChildren().addAll(text0, text1, text2, text3, acceptBtn, declineBtn);
        }
    }

    @FXML
    void decline(int requestId) {
        messageArea.clear();
        ErrorMsg msg = new ErrorMsg();
        RequestBean request = new RequestBean();
        request.setIdRequest(requestId);
        for (RequestBean item : model.getSubmits()){
            if (item.equals(request))
                msg.addAllMsg(model.decline(item));
        }
        if (msg.isErr())
            for (String item : msg.getMsgList()){
                messageArea.appendText(item);
            }
        else
            messageArea.appendText("operazione riuscita\n");
        flushInfo();
    }



    @FXML
    void accept(int requestId) {
        messageArea.clear();
        ErrorMsg msg = new ErrorMsg();
        RequestBean request = new RequestBean();
        request.setIdRequest(requestId);
        for (RequestBean item : model.getSubmits()){
            if (item.equals(request))
                msg.addAllMsg(model.accept(item));
        }
        if (msg.isErr())
            for (String item : msg.getMsgList()){
                messageArea.appendText(item);
            }
        else
            messageArea.appendText("operazione riuscita\n");
        flushInfo();
    }


    private void clearGridPane(GridPane gp){
        ObservableList<Node> childrens = gp.getChildren();
        childrens.clear();
    }

    private void flushInfo(){
        clearGridPane(requestGp);
        doViewRequests();
    }


}
