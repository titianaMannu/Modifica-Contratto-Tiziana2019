package Beans;

import java.util.ArrayList;
import java.util.List;

public class ErrorMsg {
    private List<String> msg;


    public ErrorMsg() {
        this.msg = new ArrayList<String>() ;
    }

    public List<String> getMsgList() {
        return msg ;
    }

    public boolean isErr() {
        return !msg.isEmpty() ;
    }

    public void addAllMsg(ErrorMsg msg){
        this.msg.addAll(msg.getMsgList());

    }

    public void addMsg(String m) {
        msg.add(m) ;
    }

    public void clear() {
        msg.clear() ;
    }
}
