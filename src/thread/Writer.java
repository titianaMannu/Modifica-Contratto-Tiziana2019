package thread;

import control.ExpireControl;

public class Writer implements Runnable {
    private long timeout;
    private ExpireControl control;

    private Writer() {
        this.control = ExpireControl.getInstance();
        this.timeout = 12000;
    }

    private static class LazyContainer {
        private final static Writer writer = new Writer();
    }

    public static Writer getInstance(){
        return LazyContainer.writer;
    }


    @Override
    public void run() {
        while (true)
            if (control.analyze() ){
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }
}
