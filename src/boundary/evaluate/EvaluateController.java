package boundary.evaluate;

import beans.ErrorMsg;
import beans.RequestBean;
import control.EvaluateControl;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import thread.RefreshEvaluateThread;

import java.util.List;

public class EvaluateController {
    @FXML
    private GridPane requestGp;

    @FXML
    private TextArea messageArea;

    private EvaluateControl control;

    public void setControl(EvaluateControl control) {
        this.control = control;
    }

    /**
     * mostra tutte le richieste pending fatte all'utente
     */
    private void showSubmits() {
        int count = 0;
        List<RequestBean> list = control.getSubmits();
        for (RequestBean item : list) {
            ++count;
            Text sender= new Text(item.getSender()); //mittente della richiesta
            GridPane.setConstraints(sender, 0, count);
            Text text0 = new Text(item.getType().getDescription()); //tipo della modifica
            GridPane.setConstraints(text0, 1, count);
            Text text1 = new Text(item.getObjectToChange().toString()); // objectToChange
            GridPane.setConstraints(text1, 2, count);
            Text text2 = new Text(item.getReasonWhy()); //reasonWhy
            GridPane.setConstraints(text2, 3, count);
            Text text3 = new Text(item.getDate().toString()); //dateOfSubmission
            GridPane.setConstraints(text3, 4, count);

            Button acceptBtn = new Button("accetta");
            acceptBtn.setOnAction(e -> sendApproval(item.getRequestId()));
            GridPane.setConstraints(acceptBtn, 5, count);

            Button declineBtn = new Button("rifiuta");
            declineBtn.setOnAction(e -> sendDeclination(item.getRequestId()));
            GridPane.setConstraints(declineBtn, 6, count);

            requestGp.getChildren().addAll(sender, text0, text1, text2, text3, acceptBtn, declineBtn);
        }
    }

    @FXML
    void sendDeclination(int requestId) {
        messageArea.clear();
        ErrorMsg msg = new ErrorMsg();
        RequestBean request = new RequestBean();
        request.setRequestId(requestId);
        for (RequestBean item : control.getSubmits()){
            if (item.equals(request))
                msg.addAllMsg(control.decline(item));
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
    void sendApproval(int requestId) {
        messageArea.clear();
        ErrorMsg msg = new ErrorMsg();
        RequestBean request = new RequestBean();
        request.setRequestId(requestId);
        for (RequestBean item : control.getSubmits()){
            if (item.equals(request))
                msg.addAllMsg(control.accept(item));
        }
        if (msg.isErr())
            for (String item : msg.getMsgList()){
                messageArea.appendText(item);
            }
        else
            messageArea.appendText("operazione riuscita\n");
        flushInfo();
    }


    public void refrshAvailable(){
        Thread thread = new Thread(new RefreshEvaluateThread(this));
        thread.setDaemon(false);
        thread.start();
    }

    private void clearGridPane(GridPane gp){
        ObservableList<Node> childrens = gp.getChildren();
        childrens.clear();
    }

    public void flushInfo(){
        Platform.runLater(()->{
            clearGridPane(requestGp);
            showSubmits();
        });
    }

}
