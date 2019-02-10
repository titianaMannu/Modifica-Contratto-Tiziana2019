package thread;

import boundary.request.RequestController;

/**
 * soluzione di tipo polling : la gui periodicamente aggiorna i propri dati
 * si è preferito questo tipo di soluzione in quanto il carico di tati da aggiornare non è eccessivo
 */
public class RefreshRequestThread extends Thread{
    private RequestController requestController;

    public RefreshRequestThread(RequestController requestController) {
        this.requestController = requestController;
    }


    @Override
    public void run() {
        long timeout = 6000;
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
