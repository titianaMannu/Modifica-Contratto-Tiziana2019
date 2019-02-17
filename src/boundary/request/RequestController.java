package boundary.request;

import java.time.LocalDate;
import java.util.List;
import beans.ActiveContractBean;
import beans.ErrorMsg;
import beans.RequestBean;
import control.RequestControl;
import beans.OptionalServiceBean;
import enumeration.TypeOfPayment;
import enumeration.TypeOfModification;
import enumeration.RequestStatus;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import thread.RefreshRequestThread;


public class RequestController {

    private RequestControl control;
    private RequestBean requestBean;


    public void setControl(RequestControl control) {
        this.control = control;
    }


    @FXML
    private Text idContractField;

    @FXML
    private Label frequencyField;

    @FXML
    private Text tenantField;

    @FXML
    private Text renterField;

    @FXML
    private Text grossPriceField;

    @FXML
    private Text netPriceField;

    @FXML
    private Text initDateField;

    @FXML
    private DatePicker TerminationDateField;

    @FXML
    private ComboBox<String> paymentComboBox;

    @FXML
    private Button confirmationBtn;

    @FXML
    private TextArea messageArea;

    @FXML
    private GridPane gp;

    @FXML
    private TextField reasonWhyArea;

    @FXML
    private TextField serviceNameField;

    @FXML
    private TextField servicePriceField;

    @FXML
    private TextField serviceDescriptionField;

    @FXML
    private GridPane requestGp;


    @FXML
    void doSend(ActionEvent event) {
        requestBean.setReasonWhy(reasonWhyArea.getText());
        ErrorMsg msg = control.insertRequest(requestBean);
        messageArea.clear();
        if (msg.isErr())
            for (String item : msg.getMsgList()){
                messageArea.appendText(item);
            }
        else {
            messageArea.appendText("inserimento riuscito!\n");
        }
        flushInfo();
        reasonWhyArea.clear();
        makeGuiVisible(false);
    }

    @FXML
    void doAddService(ActionEvent event) {
        messageArea.clear();
        int price = 0;
        OptionalServiceBean service;
        try {
          price = Integer.parseInt(servicePriceField.getText());
        } catch(NumberFormatException e){
            e.printStackTrace();
        }finally {
           service = new OptionalServiceBean(serviceNameField.getText(), price, serviceDescriptionField.getText());
        }
        if (service.isValid()) {
            requestBean = new RequestBean(control.getUserNickname(), TypeOfModification.ADD_SERVICE,
                    service, LocalDate.now());
            if(!requestBean.isValid()){
                for (String item : requestBean.getMsg().getMsgList())
                    messageArea.appendText(item);
                return;
            }
            messageArea.appendText("Sicuro di voler  aggiungere questo servizio?\nUna volta confermato non potrai più cambiare la richesta\n" +
                    "Puoi specificare la ragione della richesta.\n");
            //pulizia dei campi di testo
            serviceNameField.clear();
            serviceDescriptionField.clear();
            servicePriceField.clear();

            makeGuiVisible(true);
        }
        else
            for (String item : service.getMsg().getMsgList())
                messageArea.appendText(item);
    }

    @FXML
    void doChangeDate(ActionEvent event) {
        messageArea.clear();
        LocalDate date = TerminationDateField.getValue();
        requestBean = new RequestBean(control.getUserNickname(), TypeOfModification.CHANGE_TERMINATIONDATE,
                date, LocalDate.now());
        if(!requestBean.isValid()){
            for (String item : requestBean.getMsg().getMsgList())
                messageArea.appendText(item);
            return;
        }
        messageArea.appendText("Sicuro di voler cambiare la data di scadenza?\nUna volta confermato non potrai più cambiarla\n" +
                "Puoi specificare la ragione della richesta\n");
       makeGuiVisible(true);
    }

    @FXML
    void doChangePayment(ActionEvent event) {
        messageArea.clear();
        TypeOfPayment type = TypeOfPayment.getType(paymentComboBox.getValue());
        requestBean = new RequestBean(control.getUserNickname(), TypeOfModification.CHANGE_PAYMENTMETHOD,
                type, LocalDate.now());
        if(!requestBean.isValid()){
            for (String item : requestBean.getMsg().getMsgList())
            messageArea.appendText(item);
            return;
        }
        messageArea.appendText("Sicuro di voler cambiare il metodo di pagamento?\nUna volta confermato non potrai più cambiarla\n" +
                "Puoi specificare la ragione della richesta\n");
        makeGuiVisible(true);
    }

    /**
     * mostra lo stato di tutte le richieste fatte dall'utente
     */
    @FXML
    public void showRequests() {
        int count = 0;
        List<RequestBean> list = control.getAllRequests();
        for (RequestBean item : list){
            ++count;
            Text text0 = new Text(item.getType().getDescription()); //description
            GridPane.setConstraints(text0, 0, count);
            Text text1 = new Text(item.getObjectToChange().toString()); // objectToChange
            GridPane.setConstraints(text1, 1, count);
            Text text2 = new Text(item.getReasonWhy()); //reasonWhy
            GridPane.setConstraints(text2, 2, count);
            Text text3 = new Text(item.getDate().toString()); //dateOfSubmission
            GridPane.setConstraints(text3, 3, count);
            Text text4 =new Text(item.getStatus().getDescription()) ; //status
            GridPane.setConstraints(text4, 4, count);
            if (item.getStatus() != RequestStatus.PENDING && item.getStatus() != RequestStatus.TO_EXPIRE) {
                Button btn = new Button("segna come letto");
                btn.setOnAction(e -> closeRequest(item.getRequestId()));
                GridPane.setConstraints(btn, 5, count);
                requestGp.getChildren().add(btn);
            }
            requestGp.getChildren().addAll(text0, text1, text2, text3, text4);
        }
    }

    @FXML
    void closeRequest(int idRequest){
        messageArea.clear();
        ErrorMsg msg = new ErrorMsg();
        RequestBean request = new RequestBean();
        request.setRequestId(idRequest);
        for (RequestBean item : control.getAllRequests()){
            if (item.equals(request))
                msg.addAllMsg(control.deleteRequest(item));
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
    void doDeleteService( int serviceId) {
        messageArea.clear();
        OptionalServiceBean service = null;
        //takes elements from the gridPane by using the input row field
        for (OptionalServiceBean item : control.getContract().getServiceList())
            if (item.getServiceId() == serviceId){ //mi basta sapere l'id per ricavare tutti i campi del servizio in questione
                service = item;
                break;
            }

        requestBean = new RequestBean(control.getUserNickname(), TypeOfModification.REMOVE_SERVICE,
                service, LocalDate.now());
        if(!requestBean.isValid()){
            for (String item : requestBean.getMsg().getMsgList())
                messageArea.appendText(item);
            return;
        }
        messageArea.appendText("Sicuro di voler  eliminare questo servizio?\nUna volta confermato non potrai più cambiare la richesta\n" +
                "Puoi specificare la ragione della richesta.\n");
        makeGuiVisible(true);
    }

    /**
     * mostra i campi del contratto inizializzando la pagina
     */
    @FXML
    public void displayContractField(){
        int count = 0;
        ActiveContractBean contract = control.getContract();
        idContractField.setText(String.valueOf(contract.getContractId()));
        tenantField.setText(contract.getTenantNickname());
        renterField.setText(contract.getRenterNickname());
        grossPriceField.setText(String.valueOf(contract.getGrossPrice()));
        netPriceField.setText(String.valueOf(contract.getNetPrice()));
        frequencyField.setText(String.valueOf(contract.getFrequencyOfPayment()));
        paymentComboBox.setValue(contract.getPaymentMethod().getDescription());
        initDateField.setText(contract.getStipulationDate().toString());
        TerminationDateField.setValue(contract.getTerminationDate());
        //creation of a grisPane  dynamically
        for (OptionalServiceBean service : contract.getServiceList()){
            ++count;
            Label label0 = new Label(String.valueOf(service.getServiceId()));
            GridPane.setConstraints(label0, 0, count);
            Label label1 = new Label(service.getServiceName());
            GridPane.setConstraints(label1, 1, count);
            Label label2 = new Label(String.valueOf(service.getServicePrice()));
            GridPane.setConstraints(label2, 2, count);
            Label label3 = new Label(service.getDescription());
            GridPane.setConstraints(label3, 3, count);
            Button btn = new Button("elimina");
            btn.setOnAction(e -> doDeleteService(service.getServiceId()));
            GridPane.setConstraints(btn, 4, count);
            gp.getChildren().addAll(label0, label1, label2, label3, btn);
        }


    }

    private void makeGuiVisible(boolean b){
        reasonWhyArea.setVisible(b);
        confirmationBtn.setVisible(b);
    }

    /**
     * aggiorna lo stato dei servizi e dele richieste
     */
    public void flushInfo() {
        control.setActiveContract(control.getContract().getContractId());
        Platform.runLater(()-> {
            clearGridPane(requestGp);
            clearGridPane(gp);
            displayContractField();
            showRequests();
        });
    }



    public void refrshAvailable(){
       Thread thread = new Thread( new RefreshRequestThread(this) );
        thread.setDaemon(false);
        thread.start();
    }


    private void clearGridPane(GridPane gp){
        ObservableList<Node> childrens = gp.getChildren();
        childrens.clear();
    }

    @FXML
    void initialize() {
        //riempimento della combobox preliminare
        for (TypeOfPayment item : TypeOfPayment.values()){
            paymentComboBox.getItems().add( item.getValue(), item.getDescription());
        }
        makeGuiVisible(false);

    }

}
