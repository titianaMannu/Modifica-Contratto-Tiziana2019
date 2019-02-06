package view.init_page;

import view.evaluateSubmitsView.EvaluateController;

public class RefreshEvaluateThread   extends  Thread{
    private EvaluateController evaluateController;

    public RefreshEvaluateThread(EvaluateController evaluateController) {
        this.evaluateController = evaluateController;
    }

    @Override
    public void run() {
        long timeout = 3000;
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
