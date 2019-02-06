package view.requestView;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import Beans.ActiveContract;
import Beans.ErrorMsg;
import Beans.RequestBean;
import Control.RequestModel;
import Beans.OptionalService;
import entity.TypeOfPayment;
import entity.modification.TypeOfModification;
import entity.request.RequestStatus;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import view.init_page.RefreshRequestThread;


public class RequestController {

    private RequestModel model;
    private RequestBean requestBean;


    public void setModel(RequestModel model) {
        this.model = model;
    }


    @FXML
    private Text idContractField;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    private Button changeDateBtn;

    @FXML
    private ComboBox<String> paymentComboBox;

    @FXML
    private Button changePaymentBtn;

    @FXML
    private Button addServiceBtn;

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
        ErrorMsg msg = model.insertRequest(requestBean);
        messageArea.clear();
        if (msg.isErr())
            for (String item : msg.getMsgList()){
                messageArea.appendText(item);
            }
        else {
            messageArea.appendText("inserimento riuscito!\n");
        }
        flushInfo();
        makeGuiVisible(false);
    }

    @FXML
    void doAddService(ActionEvent event) {
        messageArea.clear();
        int price = 0;
        OptionalService service;
        try {
          price = Integer.parseInt(servicePriceField.getText());
        } catch(NumberFormatException e){
            e.printStackTrace();
        }finally {
           service = new OptionalService(serviceNameField.getText(), price, serviceDescriptionField.getText());
        }
        if (service.isValid()) {
            requestBean = new RequestBean(model.getUserNickname(), TypeOfModification.ADD_SERVICE,
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
        requestBean = new RequestBean(model.getUserNickname(), TypeOfModification.CHANGE_TERMINATIONDATE,
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
        ErrorMsg msg = new ErrorMsg();
        TypeOfPayment type = TypeOfPayment.getType(paymentComboBox.getValue());
        requestBean = new RequestBean(model.getUserNickname(), TypeOfModification.CHANGE_PAYMENTMETHOD,
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
    public void doViewRequests() {
        int count = 0;
        List<RequestBean> list = model.getAllRequests();
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
            if (item.getStatus() != RequestStatus.PENDING && item.getStatus() != RequestStatus.CLOSED) {
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
        for (RequestBean item : model.getAllRequests()){
            if (item.equals(request))
                msg.addAllMsg(model.setAsClosed(item));
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
        ErrorMsg msg = new ErrorMsg();
        OptionalService service = new OptionalService();
        //takes elements from the gridPane by using the input row field
        service.setServiceId(serviceId);
        for (OptionalService item : model.getContract().getServiceList())
            if (item.equals(service)){ //mi basta sapere l'id per ricavare tutti i campi del servizio in questione
                service = item;
                break;
            }

        requestBean = new RequestBean(model.getUserNickname(), TypeOfModification.REMOVE_SERVICE,
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
        ActiveContract contract = model.getContract();
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
        for (OptionalService service : contract.getServiceList()){
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
        Platform.runLater(()-> {
            reasonWhyArea.clear();
            clearGridPane(requestGp);
            clearGridPane(gp);
            displayContractField();
            doViewRequests();
        });
    }



    public void refrshAvailable(boolean b){
        if (b) {
           RefreshRequestThread thread = new RefreshRequestThread(this);
            thread.setDaemon(false);
            thread.start();
        }

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
