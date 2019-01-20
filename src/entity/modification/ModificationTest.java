package entity.modification;

import Beans.Contract;
import entity.OptionalService;
import entity.TypeOfPayment;

import java.time.LocalDate;
import java.util.ArrayList;

public class ModificationTest {
    public static void main(String args[]){
        Contract c= new Contract(100, false, LocalDate.now(), LocalDate.now(), TypeOfPayment.CREDIT_CARD, "", "",
                "", "", 1, 1, 1, false,  new ArrayList<>());
        OptionalService service = new OptionalService("wifi", "", 30);
        OptionalService service1 = new OptionalService("wifi", "", 30);
        OptionalService service2 = new OptionalService("pulizie", "", 30);
        c= applyChanges("", service, TypeOfModification.ADD_SERVICE, c);
        c= applyChanges("", service2, TypeOfModification.ADD_SERVICE, c);
        System.out.println(c.toString());
        c= applyChanges("", service1, TypeOfModification.REMOVE_SERVICE, c);
        System.out.println(c.toString());
        c= applyChanges("", service1, TypeOfModification.ADD_SERVICE, c);
        System.out.println(c.toString());
        c= applyChanges("", service, TypeOfModification.REMOVE_SERVICE, c);
        System.out.println(c.toString());
        c= applyChanges("", LocalDate.of(2019, 1, 21), TypeOfModification.CHANGE_TERMINATIONDATE, c);
        System.out.println(c.toString());
        c=applyChanges("", LocalDate.of(2019, 1, 18), TypeOfModification.CHANGE_TERMINATIONDATE, c);
        System.out.println(c.toString());
        c= applyChanges("", TypeOfPayment.VISA, TypeOfModification.CHANGE_PAYMENTMETHOD, c);
        System.out.println(c.toString());

    }

    private static Contract applyChanges(String s, Object o, TypeOfModification type, Contract c){
        ModificationFactory factory= ModificationFactory.getInstance();
        Modification m = factory.createProduct(s, o, type);
        return m.update(c);
    }
}
