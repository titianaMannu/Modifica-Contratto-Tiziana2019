package view.init_page;

import view.requestView.RequestController;

public class RefreshRequestThread extends Thread{
    private RequestController requestController;

    public RefreshRequestThread(RequestController requestController) {
        this.requestController = requestController;
    }


    @Override
    public void run() {
        long timeout = 3000;
        while (true){
            try {
                requestController.flushInfo();
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
