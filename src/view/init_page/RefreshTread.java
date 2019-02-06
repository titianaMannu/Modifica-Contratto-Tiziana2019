package view.init_page;



public class RefreshTread extends Thread {
    private InitController initController;

    public RefreshTread(InitController initController) {
        this.initController = initController;
    }

    @Override
    public void run() {
        long timeout = 3000;
        while (true){
           try {
            //   System.out.println("ciao");
               initController.flushInfo();
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                  e.printStackTrace();
            }

        }
    }
}
