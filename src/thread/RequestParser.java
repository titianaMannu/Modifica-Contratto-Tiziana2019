package thread;

import control.ExpireControl;

public class RequestParser implements Runnable {
    private long timeout;
    private ExpireControl control;

    public RequestParser() {
        this.control = ExpireControl.getInstance();
        this.timeout = 6000;
    }

    @Override
    public void run() {
        while (true)
            if ( control.analyzeRequestToExpire() ){
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }
}
