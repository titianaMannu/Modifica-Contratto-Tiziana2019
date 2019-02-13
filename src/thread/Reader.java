package thread;

import control.ExpireControl;

public class Reader implements Runnable {
    private  long timeout;
    private ExpireControl control;

    private Reader() {
        this.timeout = 12000; //10 sec
        this.control = ExpireControl.getInstance();
    }

    private static class LazyContainer{
        private final static Reader reader = new Reader();
    }

    public static Reader getInstance(){
        return LazyContainer.reader;
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
