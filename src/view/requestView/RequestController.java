package view.requestView;

import java.net.URL;
import java.util.ResourceBundle;

import Beans.ActiveContract;
import Control.RequestModel;
import entity.OptionalService;
import entity.TypeOfPayment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class RequestController {

    RequestModel model;

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
    private Button displayBtn;

    @FXML
    private GridPane gp;

    @FXML
    void doSend(ActionEvent event) {

    }

    @FXML
    void doAddService(MouseEvent event) {

    }

    @FXML
    void doChangeDate(ActionEvent event) {

    }

    @FXML
    void doChangePayment(ActionEvent event) {

    }

    @FXML
    void doViewRequests(ActionEvent event) {

    }


    @FXML
    void doDeleteService() {
        messageArea.appendText("ciao!\n");

    }

    /**
     * mostra i campi del contratto inizializzando la pagina
     */
    public void dysplayContractField(){
        int count = 1;
        ActiveContract contract = model.getContract();
        idContractField.setText(String.valueOf(contract.getContractId()));
        tenantField.setText(contract.getTenantNickname());
        renterField.setText(contract.getRenterNickname());
        grossPriceField.setText(String.valueOf(contract.getGrossPrice()));
        netPriceField.setText(String.valueOf(contract.getNetPrice()));
        frequencyField.setText(String.valueOf(contract.getFrequencyOfPayment()));
        paymentComboBox.setValue(contract.getPaymentMethod().getDescription());
        initDateField.setText(contract.getInitDate().toString());
        TerminationDateField.setValue(contract.getTerminationDate());
        for (OptionalService service : contract.getServiceList()){
            gp.addRow(++count);
            gp.add(new Text(service.getServiceName()), 0, count);   //name
            gp.add(new Text(String.valueOf(service.getServicePrice())), 1, count); //price
            gp.add(new Text(service.getDescription()), 2, count); //description
            Button btn = new Button("elimina");
            btn.setOnAction( e -> doDeleteService());
            gp.add(btn, 3, count);
        }


    }

    @FXML
    void initialize() {
        assert frequencyField != null : "fx:id=\"frequencyField\" was not injected: check your FXML file 'Requests.fxml'.";
        assert tenantField != null : "fx:id=\"tenantField\" was not injected: check your FXML file 'Requests.fxml'.";
        assert renterField != null : "fx:id=\"renterField\" was not injected: check your FXML file 'Requests.fxml'.";
        assert grossPriceField != null : "fx:id=\"grossPriceField\" was not injected: check your FXML file 'Requests.fxml'.";
        assert netPriceField != null : "fx:id=\"netPriceField\" was not injected: check your FXML file 'Requests.fxml'.";
        assert initDateField != null : "fx:id=\"initDateField\" was not injected: check your FXML file 'Requests.fxml'.";
        assert TerminationDateField != null : "fx:id=\"TerminationDateField\" was not injected: check your FXML file 'Requests.fxml'.";
        assert changeDateBtn != null : "fx:id=\"changeDateBtn\" was not injected: check your FXML file 'Requests.fxml'.";
        assert paymentComboBox != null : "fx:id=\"paymentComboBox\" was not injected: check your FXML file 'Requests.fxml'.";
        assert changePaymentBtn != null : "fx:id=\"changePaymentBtn\" was not injected: check your FXML file 'Requests.fxml'.";
        assert addServiceBtn != null : "fx:id=\"addServiceBtn\" was not injected: check your FXML file 'Requests.fxml'.";
        assert confirmationBtn != null : "fx:id=\"confirmationBtn\" was not injected: check your FXML file 'Requests.fxml'.";
        assert messageArea != null : "fx:id=\"messageArea\" was not injected: check your FXML file 'Requests.fxml'.";
        assert displayBtn != null : "fx:id=\"dysplayBtn\" was not injected: check your FXML file 'Requests.fxml'.";
        //riempimento della combobox
        for (TypeOfPayment item : TypeOfPayment.values()){
            paymentComboBox.getItems().add(item.getDescription());
        }
        confirmationBtn.setVisible(false);
        messageArea.setVisible(false);

    }
}
