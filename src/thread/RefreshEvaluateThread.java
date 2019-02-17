package thread;

import boundary.evaluate.EvaluateController;

/**
 * soluzione di tipo polling : la gui periodicamente aggiorna i propri dati
 * si è preferito questo tipo di soluzione in quanto il carico di tati da aggiornare non è eccessivo
 */
public class RefreshEvaluateThread  implements Runnable{
    private EvaluateController evaluateController;

    public RefreshEvaluateThread(EvaluateController evaluateController) {
        this.evaluateController = evaluateController;
    }

    @Override
    public void run() {
        long timeout = 6000;
        while (true){
            try {
                evaluateController.flushInfo();
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
