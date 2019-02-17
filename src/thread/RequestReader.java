package thread;

import control.ExpireControl;

public class RequestReader implements Runnable {
    private  long timeout;
    private ExpireControl control;

    public RequestReader() {
        this.timeout = 30000;
        this.control = ExpireControl.getInstance();
    }

    @Override
    public void run() {
        while (true)
            if (control.buildListToAnalyze() ){
               try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }

}
