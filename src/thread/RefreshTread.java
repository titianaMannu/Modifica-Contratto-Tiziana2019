package thread;

import boundary.init_page.InitController;

/**
 * soluzione di tipo polling : la gui periodicamente aggiorna i propri dati
 * si è preferito questo tipo di soluzione in quanto il carico di tati da aggiornare non è eccessivo
 */
public class RefreshTread extends Thread {
    private InitController initController;

    public RefreshTread(InitController initController) {
        this.initController = initController;
    }

    @Override
    public void run() {
        long timeout = 5000;
        while (true){
           try {
               initController.flushInfo();
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                  e.printStackTrace();
            }

        }
    }
}
